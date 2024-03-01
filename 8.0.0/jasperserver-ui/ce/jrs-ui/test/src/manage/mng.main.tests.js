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
import jQuery from 'jquery';
import orgModule from 'src/manage/mng.root';
import treeTemplate from './test/templates/tree.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {rewire$ajaxTargettedUpdate, restore} from 'src/core/core.ajax';

let localContext = window.localContext;

localContext.flowExecutionKey = 'e4s1';
localContext.userMngInitOptions = {
    state: { 'tenantUri': '/' },
    defaultUser: '',
    defaultEntity: 'defaultEntity',
    currentUser: 'jasperadmin',
    currentUserRoles: [
        {
            'external': false,
            'roleName': 'ROLE_USER'
        },
        {
            'external': false,
            'roleName': 'ROLE_ADMINISTRATOR'
        }
    ]
};
describe('Management Main', function () {
    var sandbox;    // variables to temporary save state of some other variables
    // variables to temporary save state of some other variables
    var orgModuleAddDialog, orgModuleManagerTree, orgModuleProperties, orgModuleEntityList;
    var organizationsResponse = '<div id="treeNodeText">' + '{"id":"/","order":1,"children":[{"id":"organizations","order":1,"label":"Organizations","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/organizations"}],"label":"root","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/"}' + '</div>';
    beforeEach(function () {
        sandbox = sinon.createSandbox();
        setTemplates(treeTemplate);    // save state of variables
        // save state of variables
        orgModuleEntityList = orgModule.entityList;
        orgModuleProperties = orgModule.properties;
        orgModuleManagerTree = orgModule.manager.tree;
        orgModuleAddDialog = orgModule.addDialog;    // now, set the new state
        // now, set the new state
        orgModule.entityList = {
            getSelectedEntities: function () {
                return [{
                    getDisplayName: function () {
                        return 'test';
                    }
                }];
            },
            setEntities: function () {
                return true;
            },
            deselectAll: function () {
                return true;
            },
            setSearchText: function (txt) {
            },
            restoreSelectedEntity: function (ent) {
            },
            update: function (a, b) {
            },
            addEntities: function (entity) {
            },
            selectEntity: function (ent) {
            },
            remove: function (ent) {
            },
            toolbar: {
                refresh: function () {
                }
            }
        };
        orgModule.properties = {
            locked: false,
            show: function (entity) {
            },
            setProperties: function (entity) {
            },
            setDetailsLoadedEntity: function () {
            },
            setAvailableEntities: function (a) {
            },
            setAssignedEntities: function (a) {
            },
            addAvailableEntities: function (a) {
            },
            addAssignedEntities: function (a) {
            },
            changeMode: function (y) {
            },
            lock: function () {
                this.locked = true;
            },
            unlock: function () {
                this.locked = false;
            },
            isChanged: function () {
                return false;
            }
        };
        orgModule.manager.tree = {
            selectOrganization: function (val) {
            },
            getOrganization: function () {
                return { id: 'id' };
            }
        };
        orgModule.addDialog = {
            show: function () {
                return false;
            },
            hide: function () {
                return false;
            }
        };
        orgModule.TREE_ID = 'testTree';
        rewire$ajaxTargettedUpdate(function (url, options) {
            jQuery('#' + options.fillLocation).html(organizationsResponse);
            options.callback();
        });
        var TenantsTreeView = function () {
        };
        TenantsTreeView.prototype.on = function () {
        };
        TenantsTreeView.prototype.getTenant = function () {
            return {};
        };
        TenantsTreeView.prototype.selectTenant = function () {
        };
        TenantsTreeView.prototype.render = function () {
        };
        var options = _.extend({}, localContext.userMngInitOptions, { TenantsTreeView: TenantsTreeView });
        orgModule.manager.initialize(options);
    });
    afterEach(function () {
        sandbox.restore();
        orgModule.stopObserving();
        orgModule.entityList = orgModuleEntityList;
        orgModule.properties = orgModuleProperties;
        orgModule.manager.tree = orgModuleManagerTree;
        orgModule.addDialog = orgModuleAddDialog;
        restore();
    });
    it('should respond on org:browse event (edit) ', function () {
        sandbox.stub(orgModule.serverActionFactory, 'browse').callsFake(function () {
            return {
                invokeAction: function () {
                    return false;
                }
            };
        });
        sandbox.stub(orgModule.clientActionFactory, 'cancelIfEdit').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        orgModule.fire('org:browse', {});
        expect(orgModule.serverActionFactory.browse).toHaveBeenCalled();
    });
    it('should respond on org:browse event (lastSelectedOrg) ', function () {
        sandbox.stub(orgModule.clientActionFactory, 'cancelIfEdit').callsFake(function () {
            return {
                invokeAction: function () {
                    return false;
                }
            };
        });
        spyOn(orgModule.manager.tenantsTree, 'selectTenant');
        orgModule.manager.lastSelectedOrg = { id: 'fake' };
        orgModule.fire('org:browse');
        expect(orgModule.manager.tenantsTree.selectTenant).toHaveBeenCalledWith('fake');
    });
    it('should respond on entity:search event (edit) ', function () {
        sandbox.stub(orgModule.serverActionFactory, 'search').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        sandbox.stub(orgModule.clientActionFactory, 'cancelIfEdit').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        orgModule.fire('entity:search');
        expect(orgModule.serverActionFactory.search).toHaveBeenCalled();
    });
    it('should respond on entity:search event (last search text) ', function () {
        sandbox.stub(orgModule.serverActionFactory, 'search').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        sandbox.stub(orgModule.clientActionFactory, 'cancelIfEdit').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        spyOn(orgModule.entityList, 'setSearchText');
        orgModule.fire('entity:search', { text: 'test' });
        expect(orgModule.entityList.setSearchText);
    });
    it('should respond on entity:next event', function () {
        sandbox.stub(orgModule.serverActionFactory, 'next').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        spyOn(orgModule.serverActionFactory, 'next').and.callFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        orgModule.fire('entity:next');
        expect(orgModule.serverActionFactory.next).toHaveBeenCalled();
    });
    it('should respond on entity:selectAndGetDetails event', function () {
        sandbox.stub(orgModule.serverActionFactory, 'selectAndGetDetails').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        orgModule.fire('entity:selectAndGetDetails', {
            entityId: 'entityId',
            entity: {
                getNameWithTenant: function () {
                    return 'fafasf';
                }
            }
        });
        expect(orgModule.serverActionFactory.selectAndGetDetails).toHaveBeenCalled();
    });
    it('should respond on result:changed event', function () {
        spyOn(orgModule.entityList, 'setEntities');
        spyOn(orgModule.entityList, 'restoreSelectedEntity');
        orgModule.fire('result:changed', {
            responseData: {
                entities: {
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.entityList.setEntities).toHaveBeenCalled();
        expect(orgModule.entityList.restoreSelectedEntity).toHaveBeenCalled();
    });
    it('should respond on result:next event', function () {
        spyOn(orgModule.entityList, 'addEntities');
        orgModule.fire('result:next', {
            responseData: {
                entities: {
                    length: 10,
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.entityList.addEntities).toHaveBeenCalled();
    });
    it('should respond on entity:detailsLoaded event', function () {
        spyOn(orgModule.manager, 'entityJsonToObject').and.callFake(function () {
            return {
                getNameWithTenant: function () {
                    return 'false';
                }
            };
        });
        spyOn(orgModule.entityList, 'update');
        spyOn(orgModule.properties, 'show');
        spyOn(orgModule.properties, 'setProperties');
        orgModule.fire('entity:detailsLoaded', {
            inputData: { refreshAttributes: [] },
            responseData: {
                entities: {
                    length: 10,
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.entityList.update).toHaveBeenCalled();
        expect(orgModule.properties.show).toHaveBeenCalled();
        expect(orgModule.properties.setProperties).toHaveBeenCalled();
    });
    it('should respond on searchAvailable:loaded', function () {
        spyOn(orgModule.properties, 'setAvailableEntities');
        orgModule.fire('searchAvailable:loaded', {
            responseData: {
                entities: {
                    length: 10,
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.properties.setAvailableEntities).toHaveBeenCalled();
    });
    it('should respond on searchAssigned:loaded', function () {
        spyOn(orgModule.properties, 'setAssignedEntities');
        orgModule.fire('searchAssigned:loaded', {
            responseData: {
                entities: {
                    length: 10,
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.properties.setAssignedEntities).toHaveBeenCalled();
    });
    it('should respond on nextAvailable:loaded event', function () {
        spyOn(orgModule.properties, 'addAvailableEntities');
        orgModule.fire('nextAvailable:loaded', {
            responseData: {
                entities: {
                    length: 10,
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.properties.addAvailableEntities).toHaveBeenCalled();
    });
    it('should respond on nextAssigned:loaded event', function () {
        spyOn(orgModule.properties, 'addAssignedEntities');
        orgModule.fire('nextAssigned:loaded', {
            responseData: {
                entities: {
                    length: 10,
                    collect: function () {
                        return [];
                    }
                }
            }
        });
        expect(orgModule.properties.addAssignedEntities).toHaveBeenCalled();
    });
    it('should respond on server:error event', function () {
        spyOn(window, 'alert');
        orgModule.fire('server:error', { responseData: { message: 'test' } });
        expect(window.alert).toHaveBeenCalled();
    });
    it('should respond on entity:created event', function () {
        spyOn(orgModule.addDialog, 'hide');
        spyOn(orgModule.manager, 'entityJsonToObject').and.callFake(function () {
            return {
                getNameWithTenant: function () {
                    return '#42';
                }
            };
        });
        spyOn(orgModule.entityList, 'addEntities');
        spyOn(orgModule.entityList, 'selectEntity');
        orgModule.fire('entity:created', {
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                }
            }
        });
        expect(orgModule.addDialog.hide).toHaveBeenCalled();
        expect(orgModule.manager.entityJsonToObject).toHaveBeenCalledWith('#23');
        expect(orgModule.entityList.addEntities).toHaveBeenCalled();
        expect(orgModule.entityList.selectEntity).toHaveBeenCalledWith('#42');
    });
    it('should respond on entity:updated event (Edit mode)', function () {
        spyOn(orgModule.properties, 'changeMode');
        spyOn(orgModule.manager, 'entityJsonToObject').and.callFake(function () {
            return {
                getNameWithTenant: function () {
                    return '#42';
                }
            };
        });
        orgModule.properties.isEditMode = true;
        orgModule.fire('entity:updated', {
            responseData: { roles: true },
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                },
                entityName: 'test'
            }
        });
        expect(orgModule.properties.changeMode).toHaveBeenCalledWith(false);
        orgModule.properties.isEditMode = false;
    });
    it('should respond on entity:updated event (entity)', function () {
        spyOn(orgModule.entityList, 'selectEntity');
        spyOn(orgModule.entityList, 'update');
        spyOn(orgModule.manager, 'entityJsonToObject').and.callFake(function () {
            return {
                getNameWithTenant: function () {
                    return '#42';
                }
            };
        });
        orgModule.fire('entity:updated', {
            responseData: { roles: true },
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                },
                entityName: 'test'
            }
        });
        expect(orgModule.entityList.update).toHaveBeenCalled();
        expect(orgModule.entityList.selectEntity).toHaveBeenCalled();
    });
    it('should respond on entity:updated event (entity)', function () {
        spyOn(orgModule.entityList, 'selectEntity');
        spyOn(orgModule.entityList, 'update');
        spyOn(orgModule.manager, 'entityJsonToObject').and.callFake(function () {
            return {
                getNameWithTenant: function () {
                    return '#42';
                }
            };
        });
        orgModule.fire('entity:updated', {
            responseData: { roles: true },
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                },
                entityName: 'test'
            }
        });
        expect(orgModule.entityList.update).toHaveBeenCalled();
        expect(orgModule.entityList.selectEntity).toHaveBeenCalled();
    });
    it('should respond on entity:deleted event (entity)', function () {
        spyOn(orgModule.entityList, 'selectEntity');
        spyOn(orgModule.entityList, 'update');
        spyOn(orgModule.manager, 'entityJsonToObject').and.callFake(function () {
            return {
                getNameWithTenant: function () {
                    return '#42';
                }
            };
        });
        orgModule.fire('entity:updated', {
            responseData: { roles: true },
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                },
                entityName: 'test'
            }
        });
        expect(orgModule.entityList.update).toHaveBeenCalled();
        expect(orgModule.entityList.selectEntity).toHaveBeenCalled();
    });
    it('should respond on entity:deleted event', function () {
        spyOn(orgModule.entityList, 'remove');
        orgModule.fire('entity:deleted', {
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                },
                entityName: 'test'
            }
        });
        expect(orgModule.entityList.remove).toHaveBeenCalled();
    });
    it('should respond on entities:deleted event', function () {
        spyOn(orgModule.entityList, 'remove');
        orgModule.fire('entity:deleted', {
            inputData: {
                entity: {
                    evalJSON: function () {
                        return '#23';
                    }
                },
                entityName: 'test'
            }
        });
        expect(orgModule.entityList.remove).toHaveBeenCalled();
    });
    it('should fire event org:browse on reloadEntities', function () {
        sandbox.stub(orgModule.serverActionFactory, 'browse').callsFake(function () {
            return {
                invokeAction: function () {
                    return false;
                }
            };
        });
        sandbox.stub(orgModule.clientActionFactory, 'cancelIfEdit').callsFake(function () {
            return {
                invokeAction: function () {
                    return true;
                }
            };
        });
        orgModule.manager.reloadEntities();
        expect(orgModule.serverActionFactory.browse).toHaveBeenCalled();
    });
});