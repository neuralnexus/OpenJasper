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
import {Droppables} from 'dragdropextra';
import jQuery from 'jquery';
import layoutModule from 'src/core/core.layout';
import orgModule from 'src/manage/mng.root';
import {
    fakeResponse,
    AjaxRequester as AjaxRequesterMock,
    ajaxTargettedUpdateMock
} from "../../tools/ajax.mock.tool";

import primaryNavModule from 'src/actionModel/actionModel.primaryNavigation';

import templLists from './test/templates/list.htm';
import templUsers from './test/templates/users.htm';
import templEvents from './test/templates/events.htm';
import templProperties from './test/templates/properties.htm';

import {rewire$ajaxTargettedUpdate, rewire$AjaxRequester, restore} from 'src/core/core.ajax';

import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('orgModule', function () {
    var sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();

        setTemplates(templLists, templUsers, templEvents, templProperties);

        sandbox.stub(orgModule.serverActionFactory, 'searchAvailable').callsFake(function () {
            return { invokeAction: sinon.stub() };
        });

        sandbox.stub(orgModule.serverActionFactory, 'searchAssigned').callsFake(function () {
            return { invokeAction: sinon.stub() };
        });

        rewire$ajaxTargettedUpdate(ajaxTargettedUpdateMock);
        rewire$AjaxRequester(AjaxRequesterMock);
    });

    afterEach(function () {
        sandbox.restore();
        restore();
    });

    var CommonMocks = {
        permissionData: {
            permission: 'testPermission',
            inheritedPermission: 'testInheritedPermission',
            isInherited: true,
            newPermission: undefined
        }
    };
    it('has document.body as a container', function () {
        expect(orgModule._getContainer()).toEqual(document.body);
    });
    it('should populate templates messages with options', function () {
        orgModule.messages = ['Test message #{property1} and #{property2}'];
        var resultMessage = orgModule.getMessage(0, {
            property1: 1,
            property2: 2
        });
        expect(resultMessage).toEqual('Test message 1 and 2');
        orgModule.messages = [];
    });
    describe('Organization', function () {
        var orgNode, orgOptions, orgFromOptions, orgFromNode, orgMockClone;
        beforeEach(function () {
            //prepare org to check creation from json
            orgOptions = {
                tenantId: 'organizations',
                tenantName: 'testName',
                tenantAlias: 'testTenantAlias',
                tenantDesc: 'testTenantDesc',
                parentId: 0,
                tenantUri: 'tenant/path/test'
            };
            orgFromOptions = new orgModule.Organization(orgOptions);    //prepare org to check creation from node
            //prepare org to check creation from node
            orgNode = document.createElement('li');
            orgNode.param = {
                id: 'organizations',
                uri: 'tenant/path/test'
            };
            orgNode.treeNode = orgNode;
            orgNode.name = 'testName';
            orgFromNode = new orgModule.Organization(orgNode);    //prepare mock to check equals
            //prepare mock to check equals
            orgMockClone = { id: orgOptions.tenantId };
        });
        it('shouldn`t create organization from underfined', function () {
            expect(function () {
                new orgModule.Organization();
            }).toThrowError('Can\'t create Organization from undefined json or node');
        });
        it('should set alias to empty string if it is not available', function() {
            const org = new orgModule.Organization(Object.assign({}, orgOptions, {
                tenantAlias: ''
            }));

            expect(org.alias).toEqual('');
        });
        it('should return name with tenant', function () {
            expect(orgFromOptions.getNameWithTenant()).toEqual(orgOptions.tenantId);
            expect(orgFromNode.getNameWithTenant()).toEqual(orgOptions.tenantId);
        });
        it('should return display name', function () {
            expect(orgFromOptions.getDisplayName()).toEqual(orgOptions.tenantName);
            expect(orgFromNode.getDisplayName()).toEqual(orgOptions.tenantName);
        });
        it('should check is it root', function () {
            expect(orgFromOptions.isRoot()).toBeTruthy();
            expect(orgFromNode.isRoot()).toBeTruthy();
        });
        it('should implement \'equals\'', function () {
            expect(orgFromOptions.equals(orgMockClone)).toBeTruthy();
            expect(orgFromNode.equals(orgMockClone)).toBeTruthy();
        });
        it('should serialize to json', function () {
            expect(Object.toJSON(orgFromOptions)).toEqual(Object.toJSON(orgOptions));
            expect(Object.toJSON(orgFromNode)).toEqual(Object.toJSON({
                tenantId: orgNode.param.id,
                tenantName: orgNode.name,
                tenantUri: orgNode.param.uri
            }));
        });
        it('can navigate to organization', function () {
            spyOn(primaryNavModule, 'setNewLocation');
            orgFromOptions.navigateToManager();
            expect(primaryNavModule.navigationPaths.tempNavigateToManager.params).toEqual(primaryNavModule.navigationPaths.organization.params + '&tenantId=organizations');
        });
    });
    describe('Permission', function () {
        var options, permissionJson, permission;
        beforeEach(function () {
            options = {
                isInherited: true,
                permission: 'testPermission',
                inheritedPermission: 'testInheritedPermission',
                isDisabled: false
            };
            permissionJson = Object.toJSON({
                permission: 'testPermission',
                isInherited: true,
                inheritedPermission: 'testInheritedPermission',
                newPermission: undefined
            });
            permission = new orgModule.Permission(options);
        });
        it('should return resolved permission ', function () {
            expect(permission.getResolvedPermission()).toEqual(options.inheritedPermission);
        });
        it('can conver to json', function () {
            expect(Object.toJSON(permission)).toEqual(permissionJson);
        });
    });
    describe('User', function () {
        var usrOptions, usr, usrCloneMock;
        beforeEach(function () {
            usrOptions = {
                userName: 'testName',
                fullName: 'testFullName',
                password: 'mySecretPassword',
                tenantId: 'testOrganization_1',
                enabled: true,
                external: true,
                permissionToDisplay: {
                    permission: 'testPermission',
                    inheritedPermission: 'testInheritedPermission',
                    isDisabled: false,
                    isInherited: true
                }
            };
            usr = new orgModule.User(usrOptions);
            usrCloneMock = {
                userName: usrOptions.userName,
                tenantId: usrOptions.tenantId
            };
        });
        it('should return display name', function () {
            expect(usr.getDisplayName()).toEqual(usrOptions.userName);
        });
        it('should return name with tenant ', function () {
            expect(usr.getNameWithTenant()).toEqual(usrOptions.userName + orgModule.Configuration.userNameSeparator + usrOptions.tenantId);    //check without tenant
            //check without tenant
            usr.tenantId = '';
            expect(usr.getNameWithTenant()).toEqual(usrOptions.userName);
        });
        it('should get manager url', function () {
            expect(usr.getManagerURL()).toEqual('flow.html?_flowId=' + usr.FLOW_ID + '&' + 'text=' + usrOptions.userName + '&' + 'tenantId=' + usrOptions.tenantId);
        });
        it('can navigate to manager', function () {
            spyOn(primaryNavModule, 'setNewLocation');
            usr.navigateToManager();
            expect(primaryNavModule.navigationPaths.tempNavigateToManager.params).toEqual(primaryNavModule.navigationPaths.user.params + '&text=' + usrOptions.userName + '&tenantId=' + usrOptions.tenantId);
        });
        it('should implement \'equals\' ', function () {
            expect(usr.equals(usrCloneMock)).toBeTruthy();
        });
        it('can convert to permission data', function () {
            expect(usr.toPermissionData()).toEqual({
                userName: usrOptions.userName,
                tenantId: usrOptions.tenantId,
                permissionToDisplay: CommonMocks.permissionData
            });
        });
    });
    describe('Role', function () {
        var role, options, roleCloneMock;
        beforeEach(function () {
            options = {
                roleName: 'testRoleName',
                external: false,
                tenantId: 'testOrganization',
                permissionToDisplay: {
                    permission: 'testPermission',
                    inheritedPermission: 'testInheritedPermission',
                    isDisabled: false,
                    isInherited: true
                }
            };
            roleCloneMock = {
                roleName: options.roleName,
                tenantId: options.tenantId
            };
            role = new orgModule.Role(options);
        });
        it('should return display name', function () {
            expect(role.getDisplayName()).toEqual(options.roleName);
        });
        it('should return name with tenant ', function () {
            expect(role.getNameWithTenant()).toEqual(options.roleName + orgModule.Configuration.userNameSeparator + options.tenantId);    //check without tenant
            //check without tenant
            role.tenantId = '';
            expect(role.getNameWithTenant()).toEqual(role.roleName);
        });
        it('should get manager url', function () {
            expect(role.getManagerURL()).toEqual('flow.html?_flowId=' + role.FLOW_ID + '&' + 'text=' + role.roleName + '&' + 'tenantId=' + role.tenantId);
        });
        it('can navigate to manager', function () {
            spyOn(primaryNavModule, 'setNewLocation');
            role.navigateToManager();
            expect(primaryNavModule.navigationPaths.tempNavigateToManager.params).toEqual(primaryNavModule.navigationPaths.role.params + '&text=' + options.roleName + '&tenantId=' + options.tenantId);
        });
        it('should implement \'equals\' ', function () {
            expect(role.equals(roleCloneMock)).toBeTruthy();
        });
        it('can convert to permission data', function () {
            expect(role.toPermissionData()).toEqual({
                roleName: options.roleName,
                tenantId: options.tenantId,
                permissionToDisplay: CommonMocks.permissionData
            });
        });
    });
    describe('entityList', function () {

        var options = {
            listTemplateId: 'tabular_threeColumn:leaf',
            itemTemplateId: 'tabular_treeColumn',
            text: 'test text',
            toolbarModel: {}
        };

        window.localContext.flowExecutionKey = 'e2s1';
        window.localContext.userMngInitOptions = {
            state: { 'tenantUri': '/' },
            defaultUser: '',
            defaultEntity: '',
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

        fakeResponse.reset();
        setTemplates(templLists);

        orgModule.manager.initialize({
            state: {},
            defaultEntity: 'defaultEntity',
            TenantsTreeView: function () {
                this.on = function () {
                };
                this.render = function () {
                };
                this.getTenant = function () {
                    return { id: 'tenantId' };
                };
            }
        });

        orgModule.entityList.initialize(options);

        beforeEach(function () {
            sandbox.stub(orgModule.manager, 'entityJsonToObject').callsFake(function () {
                return {
                    getNameWithTenant: function () {
                        return 'jasperadmin';
                    },
                    getDisplayName: function () {
                        return 'jasperadmin';
                    }
                };
            });
            sandbox.spy(orgModule.entityList.list, 'setItems');
            sandbox.stub(orgModule.entityList._infiniteScroll, 'reset');
            sandbox.stub(orgModule.entityList.list, 'show');
            sandbox.stub(orgModule.entityList.toolbar, 'refresh');
        });
        it('can search text ', function () {
            sinon.stub(orgModule, 'invokeServerAction');
            orgModule.entityList._searchBox._searchHandler();
            expect(orgModule.invokeServerAction).toHaveBeenCalled();
            expect(orgModule.invokeServerAction.args[0][0]).toEqual('search');
            orgModule.invokeServerAction.restore();
        });
        it('can set search text', function () {
            var searchInput = jQuery('#secondarySearchBox');
            expect(searchInput.find('input')[0]).toBeDefined();
        });
        it('can set entities', function () {
            orgModule.fire('result:changed', {
                inputData: { tenantId: null },
                responseData: {
                    entities: [{
                        enabled: true,
                        userName: 'jasperadmin',
                        fullName: 'jasperadmin User'
                    }]
                }
            });
            expect(orgModule.entityList.list.setItems).toHaveBeenCalled();
            expect(orgModule.entityList._infiniteScroll.reset).toHaveBeenCalled();
            expect(orgModule.entityList.list.show).toHaveBeenCalled();
            expect(orgModule.entityList.toolbar.refresh).toHaveBeenCalled();
        });
        // eslint-disable-next-line no-undef
        xit('can add entities', function () {
            orgModule.userManager.addDialog.userName.value = 'test';
            orgModule.userManager.addDialog.fullName.value = 'test user';
            orgModule.userManager.addDialog.userEmail.value = '';
            orgModule.userManager.addDialog.enableUser.checked = true;
            orgModule.userManager.addDialog.password.value = 'password';
            fakeResponse.addData({ exist: false });
            fakeResponse.addData({ status: 'success' });
            spyOn(orgModule.userManager.addDialog, '_validate').and.callFake(function () {
                return true;
            });
            spyOn(orgModule.userManager.addDialog, 'hide');
            spyOn(orgModule, 'invokeServerAction').and.callThrough();
            var fired = false;
            orgModule.observe('entity:created', function () {
                fired = true;
            });
            orgModule.userManager.addDialog._doAdd();
            expect(fired).toBeTruthy();
            expect(orgModule.invokeServerAction).toHaveBeenCalled();
            expect(orgModule.invokeServerAction.calls.argsFor(0)[0]).toEqual('exist');
            expect(orgModule.invokeServerAction.calls.argsFor(1)[0]).toEqual('create');
        });
        it('can select entity', function () {
            var fired = false;
            orgModule.observe('entity:selectAndGetDetails', function () {
                fired = true;
            });
            orgModule.fire('result:changed', {
                inputData: { tenantId: null },
                responseData: {
                    entities: [{
                        enabled: true,
                        userName: 'jasperadmin',
                        fullName: 'jasperadmin User'
                    }]
                }
            });
            orgModule.entityList.list.fire('item:selected', { item: orgModule.entityList.list._items[0] });
            expect(fired).toBeTruthy();
        });
        it('can restore selected entity', function () {
            orgModule.entityList.lastSelectedName = 'jasperadmin';
            spyOn(orgModule.entityList, 'selectEntity');
            orgModule.fire('result:changed', {
                inputData: { tenantId: null },
                responseData: {
                    entities: [{
                        enabled: true,
                        userName: 'jasperadmin',
                        fullName: 'jasperadmin User'
                    }]
                }
            });
            expect(orgModule.entityList.selectEntity).toHaveBeenCalledWith('jasperadmin');
        });
        it('can deselect all', function () {
            spyOn(orgModule.entityList.list, 'resetSelected');
            orgModule.entityList.deselectAll();
            expect(orgModule.entityList.list._selectedItems.length).toEqual(0);
        });
        it('can update entities', function () {
            orgModule.properties.initialize(options);
            var fired = false;
            orgModule.observe('entity:updated', function () {
                fired = true;
            });
            fakeResponse.addData({ status: 'success' });
            var user = new orgModule.User({
                fullName: 'a',
                userName: 'a',
                email: 'a',
                enabled: true,
                external: false,
                password: 'a'
            });
            orgModule.invokeServerAction('update', {
                entityName: 'a',
                entity: user,
                assigned: [],
                unassigned: []
            });
            expect(fired).toBeTruthy();
        });
        // eslint-disable-next-line no-undef
        xit('can remove entities', function () {
            orgModule.entityList.lastSelectedName = 'jasperadmin';
            orgModule.fire('result:changed', {
                inputData: { tenantId: null },
                responseData: {
                    entities: [{
                        enabled: true,
                        userName: 'jasperadmin',
                        fullName: 'jasperadmin User'
                    }]
                }
            });
            fakeResponse.addData({ status: 'success' });
            var fired = false;
            orgModule.observe('entities:deleted', function () {
                fired = true;
            });
            var t = window.confirm;
            window.confirm = function () {
                return true;
            };
            orgModule.invokeClientAction('deleteAll', {});
            window.confirm = t;
            expect(fired).toBeTruthy();
        });
    });
    describe('properties', function () {
        it('can initialize', function () {
            var options = {};
            var properties = orgModule.properties;
            spyOn(properties, 'processTemplate');
            spyOn(properties, 'initEvents');
            spyOn(properties, 'initButtonsFunctions');
            spyOn(properties, '_toggleButton');
            properties.initialize(options);
            expect(properties.processTemplate).toHaveBeenCalledWith(options);
            expect(properties.initEvents).toHaveBeenCalled();
            expect(properties.initButtonsFunctions).toHaveBeenCalled();
        });
        it('can show', function () {
            var user = new orgModule.User({
                attributes: [],
                email: '',
                enabled: true,
                external: false,
                fullName: 'jasperadmin User',
                password: 'jasperadmin',
                roles: [
                    {
                        external: false,
                        roleName: 'ROLE_USER'
                    },
                    {
                        external: false,
                        roleName: 'ROLE_ADMINISTRATOR'
                    }
                ],
                userName: 'jasperadmin'
            });
            spyOn(orgModule.properties, '_showEntity');
            orgModule.properties.show(user);
            expect(jQuery('#' + orgModule.properties._id).hasClass(orgModule.properties._EDIT_MODE_CLASS)).toBeFalsy();
            expect(orgModule.properties._showEntity).toHaveBeenCalled();
        });
        it('can hide', function () {
            orgModule.properties.hide();
            expect(jQuery('#' + orgModule.properties._NOTHING_TO_DISPLAY_ID).hasClass(layoutModule.HIDDEN_CLASS)).toBeFalsy();
            expect(jQuery(document.body).hasClass(layoutModule.NOTHING_TO_DISPLAY_CLASS)).toBeTruthy();
            orgModule.properties.hide();
        });
        it('can process template', function () {
            orgModule.properties.options.showAssigned = true;
            orgModule.properties.processTemplate({
                searchAssigned: false,
                showAssigned: true,
                viewAssignedItemTemplateDomId: 'list_type_attributes:role',
                viewAssignedListTemplateDomId: 'list_type_attributes'
            });
            expect(orgModule.properties.editButton).toBeDefined();
            expect(orgModule.properties.saveButton).toBeDefined();
            expect(orgModule.properties.cancelButton).toBeDefined();
            expect(orgModule.properties.editButton).toBeDefined();
            expect(orgModule.properties.deleteButton).toBeDefined();
            expect(orgModule.properties.removeFromAssigned).toBeDefined();
            expect(orgModule.properties.addToAssigned).toBeDefined();
            expect(orgModule.properties.assignedViewList).toBeDefined();
            expect(orgModule.properties.assignedList).toBeDefined();
            expect(orgModule.properties.availableList).toBeDefined();
            expect(orgModule.properties.availableSearchBox).toBeDefined();
            expect(orgModule.properties.assignedSearchBox).toBeDefined();
            expect(orgModule.properties.availableInfiniteScroll).toBeDefined();
            expect(orgModule.properties.assignedInfiniteScroll).toBeDefined();
        });
        it('can initialize drag and drop', function () {
            spyOn(Droppables, 'add');
            orgModule.properties.initDnD();
            expect(Droppables.add).toHaveBeenCalled();
        });
        it('can initialize buttons functions', function () {
            orgModule.properties.initButtonsFunctions();
            expect(typeof orgModule.properties.buttonsFunctions[orgModule.properties._EDIT_BUTTON_ID]).toEqual('function');
            expect(typeof orgModule.properties.buttonsFunctions[orgModule.properties._CANCEL_BUTTON_ID]).toEqual('function');
            expect(typeof orgModule.properties.buttonsFunctions[orgModule.properties._REMOVE_FROM_ASSIGNED_BUTTON_ID]).toEqual('function');
            expect(typeof orgModule.properties.buttonsFunctions[orgModule.properties._ADD_TO_ASSIGNED_BUTTON_ID]).toEqual('function');
        });
        it('can changeMode', function () {
            var fakeServer = sinon.fakeServer.create();
            fakeServer.respondWith([
                200,
                { 'Content-Type': 'application/json' },
                JSON.stringify({ response: { data: {} } })
            ]);
            spyOn(orgModule.properties.assignedList, 'setItems');
            spyOn(orgModule.properties.availableList, 'setItems');
            spyOn(orgModule.properties.assignedList, 'show');
            spyOn(orgModule.properties.availableList, 'show');
            spyOn(orgModule.properties, '_editEntity');
            spyOn(orgModule.properties, '_showEntity');
            orgModule.properties.changeMode(true);
            spyOn(orgModule, 'fire');    // to stub real execution of the onSuccess handler
            // to stub real execution of the onSuccess handler
            fakeServer.respond();
            expect(jQuery('#' + orgModule.properties._id).hasClass(orgModule.properties._EDIT_MODE_CLASS)).toBeTruthy();
            expect(orgModule.properties.assignedList.setItems).toHaveBeenCalledWith([]);
            expect(orgModule.properties.availableList.setItems).toHaveBeenCalledWith([]);
            expect(orgModule.properties.assignedList.show).toHaveBeenCalled();
            expect(orgModule.properties.availableList.show).toHaveBeenCalled();
            expect(orgModule.properties._editEntity).toHaveBeenCalled();
            orgModule.properties.changeMode(false);
            expect(jQuery('#' + orgModule.properties._id).hasClass(orgModule.properties._EDIT_MODE_CLASS)).toBeFalsy();
            expect(orgModule.properties._showEntity).toHaveBeenCalled();
            fakeServer.restore();
        });
        it('can edit', function () {
            expect(orgModule.properties.canEdit()).toBeTruthy();
        });
        it('can not delete if uri is "/"', function () {
            let orgOptions = {
                alias: "organizations",
                desc: "organizations",
                id: "organizations",
                name: "root",
                parentId: undefined,
                subTenantCount: 0,
                uri: "/"
            };
            sinon.stub(orgModule.properties, 'getValue').callsFake(function(){
                return orgOptions;
            });
            expect(orgModule.properties.canDelete()).toBeFalsy();
            orgModule.properties.getValue.restore();
        });
        it('can delete if uri is not "/"', function () {
            let orgOptions = {
                alias: "organizations",
                desc: "organizations",
                id: "organizations",
                name: "org",
                parentId: undefined,
                subTenantCount: 0,
                uri: "/organization_1"
            };
            sinon.stub(orgModule.properties, 'getValue').callsFake(function(){
                return orgOptions;
            });
            expect(orgModule.properties.canDelete()).toBeTruthy();
            orgModule.properties.getValue.restore();
        });
        it('has assigned comparator', function () {
            expect(orgModule.properties.assignedComparator).toBeTruthy();
            expect(typeof orgModule.properties.assignedComparator === 'function').toBeTruthy();
        });
        it('can get assigned entities', function () {
            expect(orgModule.properties._assigned).toBeTruthy();
            expect(jQuery.isArray(orgModule.properties._assigned)).toBeTruthy();
        });
        it('can get unassigned entities', function () {
            expect(orgModule.properties._unassigned).toBeTruthy();
            expect(jQuery.isArray(orgModule.properties._unassigned)).toBeTruthy();
        });
        it('can set assigned entities', function () {
            spyOn(orgModule.properties.assignedList, 'setItems');
            spyOn(orgModule.properties.assignedViewList, 'setItems');
            orgModule.properties.isEditMode = true;
            orgModule.properties.setAssignedEntities([]);
            expect(orgModule.properties.assignedList.setItems).toHaveBeenCalled();
            orgModule.properties.isEditMode = false;
            orgModule.properties.setAssignedEntities([]);
            expect(orgModule.properties.assignedViewList.setItems).toHaveBeenCalled();
        });
        it('can add assigned entities', function () {
            spyOn(orgModule.properties.assignedList, 'setItems');
            spyOn(orgModule.properties.assignedViewList, 'setItems');
            spyOn(orgModule.properties, '_filterEntities').and.callThrough();
            orgModule.properties.isEditMode = true;
            orgModule.properties.setAssignedEntities([]);
            expect(orgModule.properties._filterEntities).toHaveBeenCalled();
            expect(orgModule.properties.assignedList.setItems).toHaveBeenCalled();
            orgModule.properties.isEditMode = false;
            orgModule.properties.setAssignedEntities([]);
            expect(orgModule.properties._filterEntities).toHaveBeenCalled();
            expect(orgModule.properties.assignedViewList.setItems).toHaveBeenCalled();
        });
        it('can set available entities', function () {
            spyOn(orgModule.properties.availableList, 'setItems');
            spyOn(orgModule.properties.availableInfiniteScroll, 'reset');
            spyOn(orgModule.properties, '_filterEntities').and.callThrough();
            orgModule.properties.setAvailableEntities([]);
            expect(orgModule.properties._filterEntities).toHaveBeenCalled();
            expect(orgModule.properties.availableList.setItems).toHaveBeenCalled();
            expect(orgModule.properties.availableInfiniteScroll.reset).toHaveBeenCalled();
        });
        it('can add available entities', function () {
            spyOn(orgModule.properties.availableList, 'addItems');
            spyOn(orgModule.properties, '_filterEntities').and.callThrough();
            orgModule.properties.addAvailableEntities([]);
            expect(orgModule.properties._filterEntities).toHaveBeenCalled();
            expect(orgModule.properties.availableList.addItems).toHaveBeenCalled();
        });
        it('can reset validation', function () {
            orgModule.properties.resetValidation([
                '#userName',
                '#email',
                '#confirmPassword'
            ]);
            expect(jQuery('#' + orgModule.properties._id + ' .' + layoutModule.ERROR_CLASS).length).toEqual(0);
        });
    });
    describe('Action', function () {
        it('can invoke action ', function () {
            var t = new orgModule.Action();
            expect(t.invokeAction).toBeDefined();
        });
        it('should invoke before action', function () {
            var run = false;
            var t = new orgModule.Action(function () {
            }, function () {
                run = true;
            });
            t.invokeAction();
            expect(run).toBeTruthy();
        });
    });
    describe('ServerAction', function () {
        it('can invoke action ', function () {
            var t = new orgModule.ServerAction();
            expect(t.invokeAction).toBeDefined();
        });
        it('should invoke before action', function () {
            var run = false;
            var t = new orgModule.ServerAction(function () {
            }, function () {
                run = true;
            });
            t.beforeInvoke = function () {
                run = true;
            };
            t.invokeAction();
            expect(run).toBeTruthy();
        });
    });
    describe('validators', function () {
        it('can create input regex validator', function () {
            expect(orgModule.createInputRegExValidator).toBeDefined();
            var validator = orgModule.createInputRegExValidator({ val: true });
            expect(validator.validator).toBeDefined();
            expect(validator.element.val).toBeTruthy();
        });
        it('can create max length validator', function () {
            expect(orgModule.createMaxLengthValidator).toBeDefined();
            var validator = orgModule.createMaxLengthValidator({ val: true });
            expect(validator.validator).toBeDefined();
            expect(validator.element.val).toBeTruthy();
        });
        it('can create same validator', function () {
            expect(orgModule.createSameValidator).toBeDefined();
            var validator = orgModule.createSameValidator({ val: true });
            expect(validator.validator).toBeDefined();
            expect(validator.element.val).toBeTruthy();
        });
    });
});