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

import sinon from 'sinon';
import _ from 'underscore';
import orgModule from 'src/org/org.root.user';
import propertiesTmpl from './test/templates/properties.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import layoutModule from 'src/core/core.layout';

let localContext = window.localContext;

describe('User Management', function () {
    beforeEach(function () {
        setTemplates(propertiesTmpl);
    });

    if (!orgModule.tenantsTree) {
        orgModule.tenantsTree = {};
    }
    if (!orgModule.manager) {
        orgModule.manager = {};
    }
    if (!orgModule.manager.tenantsTree) {
        orgModule.manager.tenantsTree = {
            getTenant: function () {
                return 'BZC';
            }
        };
    }
    if (!orgModule.entityList) {
        orgModule.entityList = {
            getSelectedEntities: function () {
            }
        };
    }
    it('should be able to check if user can be added (has organisation not null)', function () {
        spyOn(orgModule.manager.tenantsTree, 'getTenant').and.callFake(function () {
            return 'aaa';
        });
        expect(orgModule.canAddUser()).toBeTruthy();
    });
    it('should be able to check if user can be added (has organisation null)', function () {
        spyOn(orgModule.manager.tenantsTree, 'getTenant').and.callFake(function () {
            return null;
        });
        expect(orgModule.canAddUser()).toBeFalsy();
    });
    it('should be able to check if user can be added (has`nt tree)', function () {
        var t = orgModule.tenantsTree;
        orgModule.tenantsTree = false;
        expect(orgModule.canAddUser()).toBeTruthy();
        orgModule.tenantsTree = t;
    });
    it('should be able to check if can enable all users (can)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [
                1,
                2,
                3
            ];
        });
        expect(orgModule.canEnableAllUsers()).toBeTruthy();
    });
    it('should be able to check if can enable all users (cannot)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [];
        });
        expect(orgModule.canEnableAllUsers()).toBeFalsy();
    });
    it('should be able to check if can disable all users (can)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [
                1,
                2,
                3
            ];
        });
        spyOn(orgModule, 'isLoggedInUserSelected').and.callFake(function () {
            return true;
        });
        expect(orgModule.canEnableAllUsers()).toBeTruthy();
    });
    it('should be able to check if can disable all users (cannot)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [];
        });
        spyOn(orgModule, 'isLoggedInUserSelected').and.callFake(function () {
            return true;
        });
        expect(orgModule.canEnableAllUsers()).toBeFalsy();
    });
    it('should be able to check if can delete all users (can)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [
                1,
                2,
                3
            ];
        });
        spyOn(orgModule, 'isLoggedInUserSelected').and.callFake(function () {
            return false;
        });
        expect(orgModule.canEnableAllUsers()).toBeTruthy();
    });
    it('should be able to check if can delete all users (cannot)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return [];
        });
        spyOn(orgModule, 'isLoggedInUserSelected').and.callFake(function () {
            return false;
        });
        expect(orgModule.canEnableAllUsers()).toBeFalsy();
    });
    it('should can initialize', function () {
        var fakeServer = sinon.fakeServer.create();
        fakeServer.respondWith([
            200,
            { 'Content-Type': 'application/json' },
            JSON.stringify({ response: { data: {} } })
        ]);
        spyOn(orgModule.manager, 'initialize');
        spyOn(orgModule.userManager.userList, 'initialize');
        spyOn(orgModule.properties, 'initialize');
        spyOn(orgModule.userManager.addDialog, 'initialize');
        orgModule.manager.state = { text: 'test' };
        localContext.userMngInitOptions = { currentUser: {} };
        spyOn(layoutModule, 'resizeOnClient');    // to stub real execution of this function
        // to stub real execution of this function
        orgModule.userManager.initialize({ _: _ });
        spyOn(orgModule, 'fire');    // to stub real execution of the onSuccess handler
        // to stub real execution of the onSuccess handler
        fakeServer.respond();
        expect(orgModule.manager.initialize).toHaveBeenCalled();
        expect(orgModule.userManager.userList.initialize).toHaveBeenCalled();
        expect(orgModule.properties.initialize).toHaveBeenCalled();
        expect(orgModule.userManager.addDialog.initialize).toHaveBeenCalled();
        fakeServer.restore();
    });
    it('should know if logged in user is selected (not selected)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return {
                length: 10,
                detect: function () {
                    return true;
                }
            };
        });
        orgModule.userManager.options = { currentUser: {} };
        expect(orgModule.isLoggedInUserSelected()).toBeTruthy();
    });
    it('should know if logged in user is selected (no selected users at all)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return {
                length: 0,
                detect: function () {
                    return true;
                }
            };
        });
        orgModule.userManager.options = { currentUser: {} };
        expect(orgModule.isLoggedInUserSelected()).toBeFalsy();
    });
    it('should know if logged in user is selected (selected)', function () {
        spyOn(orgModule.entityList, 'getSelectedEntities').and.callFake(function () {
            return {
                length: 10,
                detect: function () {
                    return false;
                }
            };
        });
        orgModule.userManager.options = { currentUser: {} };
        expect(orgModule.isLoggedInUserSelected()).toBeFalsy();
    });
});