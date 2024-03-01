/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/*global spyOn*/

import orgModule from 'src/org/org.root.role';
import layoutModule from 'src/core/core.layout';
import {rewire$isProVersion, restore} from 'src/namespace/namespace';

if (!orgModule.manager.state) {
    orgModule.manager.state = { text: 'text' };
}

describe('Role Management: Main', function () {
    if (!orgModule.roleManager.tenantsTree) {
        orgModule.roleManager.tenantsTree = {
            getTenant: function () {
                return 'BZC';
            }
        };
    }
    if (!orgModule.roleManager.roleList) {
        orgModule.roleManager.roleList = {
            initialize: function () {
                return false;
            }
        };
    }
    if (!orgModule.roleManager.properties) {
        orgModule.roleManager.properties = {
            initialize: function () {
                return false;
            }
        };
    }
    if (!orgModule.roleManager.addDialog) {
        orgModule.roleManager.addDialog = {
            initialize: function () {
                return false;
            }
        };
    }
    describe(' -- canAddRole method', function () {
        it('should be able to check if role can be added (has organisation not null) (can)', function () {
            spyOn(orgModule.roleManager.tenantsTree, 'getTenant').and.callFake(function () {
                return 'aaa';
            });
            expect(orgModule.canAddRole()).toBeTruthy();
        });
        it('should be able to check if role can be added (has organisation null) (cannot)', function () {
            spyOn(orgModule.roleManager.tenantsTree, 'getTenant').and.callFake(function () {
                return null;
            });
            expect(orgModule.canAddRole()).toBeFalsy();
        });
    });
    describe(' -- canDeleteRole method', function () {
        it('should be able to check if role can be deleted (if isUserSuperuser true) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return true;
            });
            expect(orgModule.canDeleteRole({})).toBeTruthy();
        });
        it('should be able to check if role can be deleted (if role have tenantId) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            expect(orgModule.canDeleteRole({ tenantId: '42' })).toBeTruthy();
        });
        it('should be able to check if role can be deleted (if isUserSuperuser false and role does not have tenantId) (cannot)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            expect(orgModule.canDeleteRole({})).toBeFalsy();
        });
        it('should be able to check if role can be deleted (no role passed, role have tenantId, isUserSuperuser false) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return { tenantId: '42' };
            });
            expect(orgModule.canDeleteRole()).toBeTruthy();
        });
        it('should be able to check if role can be deleted (no role passed, role have tenantId, isUserSuperuser true) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return true;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return { tenantId: '42' };
            });
            expect(orgModule.canDeleteRole()).toBeTruthy();
        });
        it('should be able to check if role can be deleted (no role passed, role does not have tenantId, isUserSuperuser false) (cannot)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return {};
            });
            expect(orgModule.canDeleteRole()).toBeFalsy();
        });
        it('should be able to check if role can be deleted (no role passed, role does not have tenantId, isUserSuperuser true) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return true;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return {};
            });
            expect(orgModule.canDeleteRole()).toBeTruthy();
        });
    });
    describe(' -- canDeleteAll method', function () {
        it('should be able to check if all roles can be deleted (if detected that some role cannot be deleted) (cannot)', function () {
            spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
                return {
                    length: 10,
                    detect: function () {
                        return true;
                    }
                };
            });
            expect(orgModule.canDeleteAllRoles()).toBeFalsy();
        });
        it('should be able to check if all roles can be deleted (if detected that all roles can be deleted) (can)', function () {
            spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
                return {
                    length: 10,
                    detect: function () {
                        return false;
                    }
                };
            });
            expect(orgModule.canDeleteAllRoles()).toBeTruthy();
        });
        it('should be able to check if all roles can be deleted (if no role is selected) (cannot)', function () {
            spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
                return {
                    length: 0,
                    detect: function () {
                        return false;
                    }
                };
            });
            expect(orgModule.canDeleteAllRoles()).toBeFalsy();
        });
    });
    describe(' -- canDeleteAll method', function () {
        it('should be able to check if role can be edited (if isUserSuperuser true) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return true;
            });
            expect(orgModule.canEditRole({})).toBeTruthy();
        });
        it('should be able to check if role can be edited (if role have tenantId) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            expect(orgModule.canEditRole({ tenantId: '42' })).toBeTruthy();
        });
        it('should be able to check if role can be edited (if isUserSuperuser false and role doest not have tenantId) (cannot)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            expect(orgModule.canEditRole({})).toBeFalsy();
        });
        it('should be able to check if role can be edited (no role passed, role have tenantId, isUserSuperuser false) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return { tenantId: '42' };
            });
            expect(orgModule.canEditRole()).toBeTruthy();
        });
        it('should be able to check if role can be edited (no role passed, role have tenantId, isUserSuperuser true) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return true;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return { tenantId: '42' };
            });
            expect(orgModule.canEditRole()).toBeTruthy();
        });
        it('should be able to check if role can be edited (no role passed, role does not have tenantId, isUserSuperuser false) (cannot)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return false;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return {};
            });
            expect(orgModule.canEditRole()).toBeFalsy();
        });
        it('should be able to check if role can be edited (no role passed, role does not have tenantId, isUserSuperuser true) (can)', function () {
            spyOn(orgModule.manager, 'isUserSuperuser').and.callFake(function () {
                return true;
            });
            spyOn(orgModule.properties, 'getValue').and.callFake(function () {
                return {};
            });
            expect(orgModule.canEditRole()).toBeTruthy();
        });
    });
    describe(' -- roleManager component', function () {
        it('should be able to initialize roleManager', function () {
            spyOn(orgModule.roleManager.roleList, 'initialize');
            spyOn(orgModule.roleManager.properties, 'initialize');
            spyOn(orgModule.roleManager.addDialog, 'initialize');
            spyOn(orgModule.manager, 'initialize');
            spyOn(orgModule.manager, 'reloadEntities');
            spyOn(layoutModule, 'resizeOnClient');

            rewire$isProVersion(function () {
                return false;
            });

            orgModule.roleManager.initialize();
            expect(orgModule.roleManager.roleList.initialize).toHaveBeenCalled();
            expect(orgModule.roleManager.properties.initialize).toHaveBeenCalled();
            expect(orgModule.roleManager.addDialog.initialize).toHaveBeenCalled();
            expect(orgModule.manager.initialize).toHaveBeenCalled();
            expect(orgModule.manager.reloadEntities).toHaveBeenCalled();
            expect(layoutModule.resizeOnClient).toHaveBeenCalled();

            restore();
        });
        it('should define orgModule.manager methods after initialization', function () {
            expect(orgModule.manager.entityJsonToObject).toBeDefined();
            expect(orgModule.manager.relatedEntityJsonToObject).toBeDefined();
        });
        it('should define orgModule.addDialog methods after initialization', function () {
            expect(orgModule.addDialog.show).toBeDefined();
            expect(orgModule.addDialog.hide).toBeDefined();
        });
    });
});