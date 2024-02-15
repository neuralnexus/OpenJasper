/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: mng.common.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
    "core.layout",
    "mng.main",
    "ajax.mock",
    "text!templates/list.htm",
    "text!templates/users.htm",
    "text!templates/events.htm",
    "text!templates/properties.htm"
], function(
    jQuery,
    layoutModule,
    orgModule,
    fakeResponce,
    templLists,
    templUsers,
    templEvents,
    templProperties
    ) {

    describe("orgModule", function() {

        beforeEach(function() {
            setTemplates(templLists, templUsers, templEvents, templProperties);
            sinon.stub(window, "ajaxTargettedUpdate", ajaxTargettedUpdateMock);
        });

        afterEach(function(){
            window.ajaxTargettedUpdate.restore();
        });

        var CommonMocks = {
            permissionData : {
                permission: "testPermission",
                inheritedPermission: "testInheritedPermission",
                isInherited: true,
                newPermission: undefined
            }
        };

        it("has document.body as a container", function() {
            expect(orgModule._getContainer()).toEqual(document.body);
        });

        it("should populate templates messages with options", function() {
            this.after(function() {
                orgModule.messages = [];
            });
            orgModule.messages = [ "Test message #{property1} and #{property2}"];
            var resultMessage = orgModule.getMessage(0, {property1:1, property2:2});
            expect(resultMessage).toEqual("Test message 1 and 2")
        });

        describe("Organization", function() {

            var orgNode, orgOptions,orgFromOptions, orgFromNode, orgMockClone;

            beforeEach(function () {
                //prepare org to check creation from json
                orgOptions = {
                    tenantId:  "organizations",
                    tenantName: "testName",
                    tenantAlias: "testTenantAlias",
                    tenantDesc: "testTenantDesc",
                    parentId: 0,
                    tenantUri: "tenant/path/test"
                };
                orgFromOptions = new orgModule.Organization(orgOptions);
                //prepare org to check creation from node
                orgNode = document.createElement('li');
                orgNode.param = {
                    id : "organizations",
                    uri : "tenant/path/test"
                };

                orgNode.treeNode = orgNode;
                orgNode.name = "testName";
                orgFromNode = new orgModule.Organization(orgNode);
                //prepare mock to check equals
                orgMockClone = {
                    id:orgOptions.tenantId
                };
            });

            it("shouldn`t create organization from underfined", function() {
                expect(
                        function() {
                            new orgModule.Organization();
                        }
                ).toThrow("Can't create Organization from undefined json or node");
            });

            it("should return name with tenant", function() {
                expect(orgFromOptions.getNameWithTenant()).toEqual(
                        orgOptions.tenantId
                );
                expect(orgFromNode.getNameWithTenant()).toEqual(
                        orgOptions.tenantId
                );
            });

            it("should return display name", function() {
                expect(orgFromOptions.getDisplayName()).toEqual(
                        orgOptions.tenantName
                );
                expect(orgFromNode.getDisplayName()).toEqual(
                        orgOptions.tenantName
                );
            });

            it("should check is it root", function() {
                expect(orgFromOptions.isRoot()).toBeTruthy();
                expect(orgFromNode.isRoot()).toBeTruthy();
            });

            it("should implement 'equals'", function() {
                expect(orgFromOptions.equals(orgMockClone)).toBeTruthy();
                expect(orgFromNode.equals(orgMockClone)).toBeTruthy();
            });

            it("should serialize to json", function() {
                expect(Object.toJSON(orgFromOptions)).toEqual(
                        Object.toJSON(orgOptions)
                );
                expect(Object.toJSON(orgFromNode)).toEqual(
                        Object.toJSON({
                            tenantId: orgNode.param.id,
                            tenantName: orgNode.name,
                            tenantUri: orgNode.param.uri
                        })
                );
            });

            it("can navigate to organization", function() {
                spyOn(primaryNavModule, 'setNewLocation');
                orgFromOptions.navigateToManager();
                expect(primaryNavModule.navigationPaths.tempNavigateToManager.params).toEqual(
                        primaryNavModule.navigationPaths.organization.params + "&tenantId=organizations"
                );
            });

        });

        describe("Permission", function() {

            var options, permissionJson, permission;

            beforeEach(function() {
                options = {
                    isInherited : true,
                    permission: "testPermission",
                    inheritedPermission : "testInheritedPermission",
                    isDisabled : false
                };
                permissionJson = Object.toJSON({
                            permission: "testPermission",
                            isInherited : true,
                            inheritedPermission : "testInheritedPermission",
                            newPermission: undefined
                });
                permission = new orgModule.Permission(options);
            });

            it("should return resolved permission ", function() {
                expect(permission.getResolvedPermission()).toEqual(
                        options.inheritedPermission
                );
            });

            it("can conver to json", function() {
                expect(Object.toJSON(permission)).toEqual(permissionJson);
            });
        });

        describe("User", function() {
            var usrOptions, usr, usrCloneMock;

            beforeEach(function() {
                usrOptions = {
                    userName: "testName",
                    fullName: "testFullName",
                    password: "mySecretPassword",
                    tenantId: "testOrganization_1",
                    enabled: true,
                    external: true,
                    permissionToDisplay: {
                        permission: "testPermission",
                        inheritedPermission: "testInheritedPermission",
                        isDisabled: false,
                        isInherited: true
                    }
                };
                usr = new orgModule.User(usrOptions);
                usrCloneMock = {
                        userName : usrOptions.userName,
                        tenantId : usrOptions.tenantId
                };

            });

            it("should return display name", function() {
                expect(usr.getDisplayName()).toEqual(
                        usrOptions.userName
                );
            });

            it("should return name with tenant ", function() {
                expect(usr.getNameWithTenant()).toEqual(
                        usrOptions.userName + orgModule.Configuration.userNameSeparator + usrOptions.tenantId
                );
                //check without tenant
                usr.tenantId = "";
                expect(usr.getNameWithTenant()).toEqual(
                        usrOptions.userName
                );
            });

            it("should get manager url", function() {
                expect(usr.getManagerURL()).toEqual(
                        "flow.html?_flowId=" + usr.FLOW_ID +
                                "&" + "text=" + usrOptions.userName +
                                "&" + "tenantId=" + usrOptions.tenantId
                );
            });

            it("can navigate to manager", function() {
                spyOn(primaryNavModule, 'setNewLocation');
                usr.navigateToManager();
                expect(primaryNavModule.navigationPaths.tempNavigateToManager.params).toEqual(
                        primaryNavModule.navigationPaths.user.params +
                                "&text=" + usrOptions.userName +
                                "&tenantId=" + usrOptions.tenantId
                );
            });

            it("should implement 'equals' ", function() {
                expect(usr.equals(usrCloneMock)).toBeTruthy();
            });

            it("can convert to permission data", function() {
                expect(usr.toPermissionData()).toEqual({
                            userName : usrOptions.userName,
                            tenantId : usrOptions.tenantId,
                            permissionToDisplay: CommonMocks.permissionData
                        })
            });
        });


        describe("Role", function() {

            var role, options, roleCloneMock;

            beforeEach(function() {
                options = {
                    roleName: "testRoleName",
                    external : false,
                    tenantId : "testOrganization",
                    permissionToDisplay: {
                        permission: "testPermission",
                        inheritedPermission: "testInheritedPermission",
                        isDisabled: false,
                        isInherited: true
                    }
                };
                roleCloneMock = {
                    roleName:options.roleName,
                    tenantId: options.tenantId
                };
                role = new orgModule.Role(options);
            });

            it("should return display name", function() {
                expect(role.getDisplayName()).toEqual(
                        options.roleName
                );
            });

            it("should return name with tenant ", function() {
                expect(role.getNameWithTenant()).toEqual(
                        options.roleName + orgModule.Configuration.userNameSeparator + options.tenantId
                );
                //check without tenant
                role.tenantId = "";
                expect(role.getNameWithTenant()).toEqual(
                        role.roleName
                );
            });

            it("should get manager url", function() {
                expect(role.getManagerURL()).toEqual(
                        "flow.html?_flowId=" + role.FLOW_ID +
                                "&" + "text=" + role.roleName +
                                "&" + "tenantId=" + role.tenantId
                );
            });

            it("can navigate to manager", function() {
                spyOn(primaryNavModule, 'setNewLocation');
                role.navigateToManager();
                expect(primaryNavModule.navigationPaths.tempNavigateToManager.params).toEqual(
                        primaryNavModule.navigationPaths.role.params +
                                "&text=" + options.roleName +
                                "&tenantId=" + options.tenantId
                );
            });

            it("should implement 'equals' ", function() {
                expect(role.equals(roleCloneMock)).toBeTruthy();
            });

            it("can convert to permission data", function() {
                expect(role.toPermissionData()).toEqual({
                            roleName : options.roleName,
                            tenantId : options.tenantId,
                            permissionToDisplay: CommonMocks.permissionData
                        });
            });
        });

        //TODO: more deep testing
        it("can create organizations tree", function() {
            //code coverage brokes on jquery, use dom
            var treeDiv = jQuery("<div id='" + orgModule.TREE_ID + "'></div>");
            jQuery("body").append(treeDiv);

            var tree = orgModule.createOrganizationsTree();
            expect(tree).toBeDefined();
            expect(dynamicTree.Organization).toBeDefined();
            treeDiv.detach();
        });

        xdescribe("entityList", function() {

            var options = {
                listTemplateId: "tabular_threeColumn:leaf",
                itemTemplateId: "tabular_treeColumn",
                text: "test text",
                toolbarModel: {}
            };

            localContext.flowExecutionKey = 'e2s1';
            localContext.userMngInitOptions = {
                state: {"tenantUri":"/"},
                defaultUser: '',
                defaultEntity: '',
                currentUser: 'jasperadmin',
                currentUserRoles: [
                    {"external":false,"roleName":"ROLE_USER"},
                    {"external":false,"roleName":"ROLE_ADMINISTRATOR"}
                ]
            };

            fakeResponce.reset();

            sinon.stub(layoutModule, "resizeOnClient"); // mock this object to avoid unnecessary code execution
         //   sinon.stub(orgModule.manager, "entityJsonToObject", function(a){return a});

            setTemplates(templLists);

            orgModule.entityList.initialize(options);
            orgModule.userManager.initialize();

            layoutModule.resizeOnClient.restore();
       //     orgModule.manager.entityJsonToObject.restore();

//            beforeEach(function() {
//                var options = {
//                    listTemplateId: "tabular_threeColumn:leaf",
//                    itemTemplateId: "tabular_treeColumn",
//                    text: "test text",
//                    toolbarModel: {}
//                };
//
//                localContext.flowExecutionKey = 'e2s1';
//                localContext.userMngInitOptions = {
//                    state: {"tenantUri":"/"},
//                    defaultUser: '',
//                    defaultEntity: '',
//                    currentUser: 'jasperadmin',
//                    currentUserRoles: [
//                        {"external":false,"roleName":"ROLE_USER"},
//                        {"external":false,"roleName":"ROLE_ADMINISTRATOR"}
//                    ]
//                };
//
//                fakeResponce.reset();
//
//                sinon.stub(layoutModule, "resizeOnClient"); // mock this object to avoid unnecessary code execution
//
//                orgModule.entityList.initialize(options);
//                orgModule.userManager.initialize();
//                layoutModule.resizeOnClient.restore();
//
//            });
//
//            afterEach(function(){
//                layoutModule.resizeOnClient.restore();
//            });

            it("can search text ", function() {
                spyOn(window, "invokeServerAction");

                orgModule.entityList._searchBox._searchHandler();
                expect(window.invokeServerAction).toHaveBeenCalled();
                expect(window.invokeServerAction.argsForCall[0][0]).toEqual("search");

            });

            it("can set search text", function() {
                var searchInput = jQuery('#secondarySearchBox');
                expect(searchInput.find('input')[0]).toBeDefined();
            });

            it("can set entities", function() {
                spyOn(orgModule.entityList.list, 'setItems');
                spyOn(orgModule.entityList._infiniteScroll, 'reset');
                spyOn(orgModule.entityList.list, 'show');
                spyOn(orgModule.entityList.toolbar, 'refresh');

                orgModule.fire('result:changed', { inputData:{tenantId : null}, responseData :{entities: [
                    { enabled:true, userName:"jasperadmin", fullName:"jasperadmin User"}
                ]}});

                expect(orgModule.entityList.list.setItems).toHaveBeenCalled();
                expect(orgModule.entityList._infiniteScroll.reset).toHaveBeenCalled();
                expect(orgModule.entityList.list.show).toHaveBeenCalled();
                expect(orgModule.entityList.toolbar.refresh).toHaveBeenCalled();
            });

            it("can add entities", function() {
                orgModule.userManager.addDialog.userName.value = 'test';
                orgModule.userManager.addDialog.fullName.value = 'test user';
                orgModule.userManager.addDialog.userEmail.value = '';
                orgModule.userManager.addDialog.enableUser.checked = true;
                orgModule.userManager.addDialog.password.value = 'password';

                fakeResponce.addData({exist: false });
                fakeResponce.addData({status: 'success'});

                spyOn(orgModule.userManager.addDialog, '_validate').andCallFake(function(){return true});
                spyOn(orgModule.userManager.addDialog, 'hide');
                spyOn(window, 'invokeServerAction').andCallThrough();

                var fired = false;
                orgModule.observe('entity:created', function() { fired = true;});

                orgModule.userManager.addDialog._doAdd();

                expect(fired).toBeTruthy();
                expect(window.invokeServerAction).toHaveBeenCalled();
                expect(window.invokeServerAction.argsForCall[0][0]).toEqual("exist");
                expect(window.invokeServerAction.argsForCall[1][0]).toEqual("create");

            });

            it("can select entity", function() {
                var fired = false;
                orgModule.observe('entity:selectAndGetDetails', function(){ fired = true;});

                orgModule.fire('result:changed', { inputData:{tenantId : null}, responseData :{entities: [{ enabled:true, userName:"jasperadmin", fullName:"jasperadmin User"}]}});

                orgModule.entityList.list.fire('item:selected',{item:orgModule.entityList.list._items[0]});

                expect(fired).toBeTruthy();
            });

            it("can restore selected entity", function(){
                orgModule.entityList.lastSelectedName = 'jasperadmin';
                spyOn(orgModule.entityList, 'selectEntity');
                spyOn(orgModule.entityList.list, 'show');

                orgModule.fire('result:changed', { inputData:{tenantId : null}, responseData :{entities: [{ enabled:true, userName:"jasperadmin", fullName:"jasperadmin User"}]}});

                expect(orgModule.entityList.selectEntity).toHaveBeenCalledWith('jasperadmin');
            });

            it("can deselect all", function(){
                spyOn(orgModule.entityList.list, 'resetSelected');
                orgModule.entityList.deselectAll();

                expect(orgModule.entityList.list._selectedItems.length).toEqual(0);
            });

            it("can update entities", function(){
                var fired = false;
                orgModule.observe('entity:updated', function() {
                    fired = true;
                });

                fakeResponce.addData({status: 'success'});

                var user = new orgModule.User({
                    fullName: 'a',
                    userName: 'a',
                    email: 'a',
                    enabled: true,
                    external: false,
                    password: 'a'
                });

                invokeServerAction("update", {
                    entityName: 'a',
                    entity: user,
                    assigned: [],
                    unassigned: []
                });

                expect(fired).toBeTruthy();
            });

            it("can remove entities", function(){
                orgModule.entityList.lastSelectedName = 'jasperadmin';
                orgModule.fire('result:changed', { inputData:{tenantId : null}, responseData :{entities: [{ enabled:true, userName:"jasperadmin", fullName:"jasperadmin User"}]}});
                fakeResponce.addData({status: 'success'});

                var fired = false;
                orgModule.observe('entities:deleted', function(){ fired = true;});

                var t = window.confirm;
                window.confirm = function(){return true};

                invokeClientAction('deleteAll',{});

                window.confirm = t;

                expect(fired).toBeTruthy();
            });
        });

        describe("properties", function() {
            it("can initialize", function() {
                var options = {};
                var properties = orgModule.properties;
                spyOn(properties, 'processTemplate');
                spyOn(properties, 'initEvents');
                spyOn(properties, 'initButtonsFunctions');
                properties.initialize(options);
                expect(properties.processTemplate).toHaveBeenCalledWith(options);
                expect(properties.initEvents).toHaveBeenCalled();
                expect(properties.initButtonsFunctions).toHaveBeenCalled();
            });

            it("can show", function() {
                var user = new orgModule.User({attributes:[], email:    "", enabled:true, external:    false, fullName:"jasperadmin User", password:"jasperadmin", roles:    [
                    { external:false, roleName:"ROLE_USER"},
                    { external:false, roleName:"ROLE_ADMINISTRATOR"}
                ],    userName: "jasperadmin"});

                spyOn(orgModule.properties, '_showEntity');

                orgModule.properties.show(user);

                expect(jQuery('#'+orgModule.properties._id).hasClass(orgModule.properties._EDIT_MODE_CLASS)).toBeFalsy();
                expect(orgModule.properties._showEntity).toHaveBeenCalled();
            });

            it("can hide", function() {
                orgModule.properties.hide();

                expect(jQuery('#' + orgModule.properties._NOTHING_TO_DISPLAY_ID).hasClass(layoutModule.HIDDEN_CLASS)).toBeFalsy();
                expect(jQuery(document.body).hasClass(layoutModule.NOTHING_TO_DISPLAY_CLASS)).toBeTruthy();
                orgModule.properties.hide();
            });

            it("can process template", function() {
                orgModule.properties.options.showAssigned = true;
                orgModule.properties.processTemplate(
                    { searchAssigned:false,
                        showAssigned:true,
                        viewAssignedItemTemplateDomId:"list_type_attributes:role",
                        viewAssignedListTemplateDomId :"list_type_attributes"
                    }
                );

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

            it("can initialize drag and drop", function() {
                spyOn(Droppables, 'add');
                orgModule.properties.initDnD();
                expect(Droppables.add).toHaveBeenCalled();
            });

            it("can initialize buttons functions", function() {
                orgModule.properties.initButtonsFunctions();

                expect(typeof (orgModule.properties.buttonsFunctions[orgModule.properties._EDIT_BUTTON_ID])).toEqual('function');
                expect(typeof (orgModule.properties.buttonsFunctions[orgModule.properties._CANCEL_BUTTON_ID])).toEqual('function');
                expect(typeof (orgModule.properties.buttonsFunctions[orgModule.properties._REMOVE_FROM_ASSIGNED_BUTTON_ID])).toEqual('function');
                expect(typeof (orgModule.properties.buttonsFunctions[orgModule.properties._ADD_TO_ASSIGNED_BUTTON_ID])).toEqual('function');
            });

            it("can changeMode", function() {

                var fakeServer = sinon.fakeServer.create();
                fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify({response: {data: {}}})]);

                spyOn(orgModule.properties.assignedList, 'setItems');
                spyOn(orgModule.properties.availableList, 'setItems');
                spyOn(orgModule.properties.assignedList, 'show');
                spyOn(orgModule.properties.availableList, 'show');
                spyOn(orgModule.properties, '_editEntity');
                spyOn(orgModule.properties, '_showEntity');

                orgModule.properties.changeMode(true);

                spyOn(orgModule, "fire"); // to stub real execution of the onSuccess handler
                fakeServer.respond();

                expect(jQuery('#'+orgModule.properties._id).hasClass(orgModule.properties._EDIT_MODE_CLASS)).toBeTruthy();
                expect(orgModule.properties.assignedList.setItems).toHaveBeenCalledWith([]);
                expect(orgModule.properties.availableList.setItems).toHaveBeenCalledWith([]);
                expect(orgModule.properties.assignedList.show).toHaveBeenCalled();
                expect(orgModule.properties.availableList.show).toHaveBeenCalled();
                expect(orgModule.properties._editEntity).toHaveBeenCalled();

                orgModule.properties.changeMode(false);

                expect(jQuery('#'+orgModule.properties._id).hasClass(orgModule.properties._EDIT_MODE_CLASS)).toBeFalsy();
                expect(orgModule.properties._showEntity).toHaveBeenCalled();

                fakeServer.restore();
            });

            it("can edit and delete", function() {
                expect(orgModule.properties.canEdit()).toBeTruthy();
                expect(orgModule.properties.canDelete()).toBeTruthy();
            });

            it("has assigned comparator", function(){
                expect(orgModule.properties.assignedComparator).toBeTruthy();
                expect(typeof(orgModule.properties.assignedComparator) === 'function').toBeTruthy();
            });

            it("can get assigned entities", function(){
               expect(orgModule.properties._assigned).toBeTruthy();
               expect(jQuery.isArray(orgModule.properties._assigned)).toBeTruthy();
              });

            it("can get unassigned entities", function(){
                expect(orgModule.properties._unassigned).toBeTruthy();
                expect(jQuery.isArray(orgModule.properties._unassigned)).toBeTruthy();
            });

            it("can set assigned entities", function(){
                spyOn(orgModule.properties.assignedList, 'setItems');
                spyOn(orgModule.properties.assignedViewList, 'setItems');

                orgModule.properties.isEditMode = true;
                orgModule.properties.setAssignedEntities([]);
                expect(orgModule.properties.assignedList.setItems).toHaveBeenCalled();

                orgModule.properties.isEditMode = false;
                orgModule.properties.setAssignedEntities([]);
                expect(orgModule.properties.assignedViewList.setItems).toHaveBeenCalled();
            });

            it("can add assigned entities", function(){
                spyOn(orgModule.properties.assignedList, 'setItems');
                spyOn(orgModule.properties.assignedViewList, 'setItems');
                spyOn(orgModule.properties, '_filterEntities').andCallThrough();

                orgModule.properties.isEditMode = true;
                orgModule.properties.setAssignedEntities([]);
                expect(orgModule.properties._filterEntities).toHaveBeenCalled();
                expect(orgModule.properties.assignedList.setItems).toHaveBeenCalled();

                orgModule.properties.isEditMode = false;
                orgModule.properties.setAssignedEntities([]);
                expect(orgModule.properties._filterEntities).toHaveBeenCalled();
                expect(orgModule.properties.assignedViewList.setItems).toHaveBeenCalled();
            });

            it("can set available entities", function(){
                spyOn(orgModule.properties.availableList, 'setItems');
                spyOn(orgModule.properties.availableInfiniteScroll, 'reset');
                spyOn(orgModule.properties, '_filterEntities').andCallThrough();

                orgModule.properties.setAvailableEntities([]);

                expect(orgModule.properties._filterEntities).toHaveBeenCalled();
                expect(orgModule.properties.availableList.setItems).toHaveBeenCalled();
                expect(orgModule.properties.availableInfiniteScroll.reset).toHaveBeenCalled();
            });

            it("can add available entities", function(){
                spyOn(orgModule.properties.availableList, 'addItems');
                spyOn(orgModule.properties, '_filterEntities').andCallThrough();

                orgModule.properties.addAvailableEntities([]);

                expect(orgModule.properties._filterEntities).toHaveBeenCalled();
                expect(orgModule.properties.availableList.addItems).toHaveBeenCalled();
            });

            it("can reset validation", function(){
                orgModule.properties.resetValidation(["#userName", "#email", "#confirmPassword"]);
                expect(jQuery('#'+orgModule.properties._id+' .'+layoutModule.ERROR_CLASS).length).toEqual(0);
            });
        });

       describe("Action", function(){
           it("can invoke action ", function(){
             var t = new orgModule.Action();
             expect(t.invokeAction).toBeDefined();
          });

           it("should invoke before action", function(){
             var run = false;
             var t = new orgModule.Action(function(){}, function(){run = true});
             t.invokeAction();
             expect(run).toBeTruthy();
          });
       });

       describe("ServerAction", function(){
           it("can invoke action ", function(){
              var t = new orgModule.ServerAction();
             expect(t.invokeAction).toBeDefined();
          });

           it("should invoke before action", function(){
              var run = false;
             var t = new orgModule.ServerAction(function(){}, function(){run = true});
             t.beforeInvoke =  function(){run = true};
             t.invokeAction();
             expect(run).toBeTruthy();
          });
       });

        describe("validators", function() {
            it("can create input regex validator", function() {
                expect(orgModule.createInputRegExValidator).toBeDefined();
                var validator = orgModule.createInputRegExValidator({val:true});

                expect(validator.validator).toBeDefined();
                expect(validator.element.val).toBeTruthy();
            });

            it("can create max length validator", function() {
                expect(orgModule.createMaxLengthValidator).toBeDefined();
                var validator = orgModule.createMaxLengthValidator({val:true});

                expect(validator.validator).toBeDefined();
                expect(validator.element.val).toBeTruthy();
            });

            it("can create same validator", function() {
                expect(orgModule.createSameValidator).toBeDefined();
                var validator = orgModule.createSameValidator({val:true});

                expect(validator.validator).toBeDefined();
                expect(validator.element.val).toBeTruthy();
            });
        });
    });
});