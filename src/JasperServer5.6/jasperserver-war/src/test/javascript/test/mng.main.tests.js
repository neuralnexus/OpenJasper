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
 * @version: $Id: mng.main.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "org.role.mng.components", "text!templates/tree.htm"], function(jQuery, orgModule, treeTemplate) {

    var localContext = localContext || {};
    localContext.flowExecutionKey = 'e4s1';
    localContext.userMngInitOptions = {
        state:{"tenantUri":"/"},
        defaultUser:'',
        defaultEntity:'',
        currentUser:'jasperadmin',
        currentUserRoles:[
            {"external":false, "roleName":"ROLE_USER"},
            {"external":false, "roleName":"ROLE_ADMINISTRATOR"}
        ]
    };
    //orgModule.Configuration = {"userNameSeparator":"|", "userNameNotSupportedSymbols":"[\\|\\s`\"'~!#\\u0024%\\^&,*\\+=;:?\\<\\>}{)(\\]\\[/]", "adminRole":"ROLE_ADMINISTRATOR", "userDefaultRole":"ROLE_USER", "passwordMask":"*", "superuserRole":"ROLE_SUPERUSER"};


    describe("Management Main", function(){

        // variables to temporary save state of some other variables
        var orgModuleAddDialog, orgModuleManagerTree, orgModuleProperties, orgModuleEntityList;

		beforeEach(function(){
            // save state of variables
            orgModuleEntityList = orgModule.entityList;
            orgModuleProperties = orgModule.properties;
            orgModuleManagerTree = orgModule.manager.tree;
            orgModuleAddDialog = orgModule.addDialog;
			
            // now, set the new state
            orgModule.entityList = {
                getSelectedEntities: function() { return [{getDisplayName:function() {return 'test';}}]},
                setEntities: function() {return true},
                deselectAll: function() {return true},
                setSearchText: function(txt){} ,
                restoreSelectedEntity: function(ent){},
                update:function(a,b){},
                addEntities:function(entity){},
                selectEntity:function(ent){},
                remove:function(ent){}
            };

            orgModule.properties = {
                show:function(entity){},
                setProperties:function(entity){},
                setAvailableEntities: function(a){},
                setAssignedEntities:function(a){},
                addAvailableEntities:function(a){},
                addAssignedEntities:function(a){},
                changeMode:function(y){},
                isChanged: function(){return false;}
            };

            orgModule.manager.tree = {
                selectOrganization: function(val){},
                getOrganization: function(){return {id:'id'}}
            };

            orgModule.addDialog = {
                show: function() { return false; },
                hide: function() { return false; }
            };
        });

        afterEach(function(){
            orgModule.entityList = orgModuleEntityList;
            orgModule.properties = orgModuleProperties;
            orgModule.manager.tree = orgModuleManagerTree;
            orgModule.addDialog = orgModuleAddDialog;
        });

        if (jasmine.isIE()){
            orgModule._container = $(document.body);
        }

		orgModule.TREE_ID = "testTree";
		
		setTemplates(treeTemplate);
		
		var organizationsResponse = '<div id="treeNodeText">' + '{"id":"/","order":1,"children":[{"id":"organizations","order":1,"label":"Organizations","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/organizations"}],"label":"root","type":"com.jaspersoft.jasperserver.api.metadata.common.domain.Folder","uri":"/"}' + '</div>';
		
		sinon.stub(window, "ajaxTargettedUpdate", function(url, options) {
			jQuery('#' + options.fillLocation).html(organizationsResponse);
			options.callback();
		});
		
        orgModule.manager.initialize(localContext.userMngInitOptions);
		
		window.ajaxTargettedUpdate.restore();

        it("should respond on org:browse event (edit) ", function() {
           spyOn(orgModule.serverActionFactory, 'browse').andCallFake(function(){ return { invokeAction:function(){return false}}});

           orgModule.fire('org:browse',{});

           expect(orgModule.serverActionFactory.browse).toHaveBeenCalled();
       });

        it("should respond on org:browse event (lastSelectedOrg) ", function() {
            spyOn(orgModule.clientActionFactory, 'cancelIfEdit').andCallFake(function(){ return { invokeAction:function(){return false}}});

           spyOn(orgModule.manager.tree,'selectOrganization');
           orgModule.manager.lastSelectedOrg = {id:'fake'};
           orgModule.fire('org:browse');

           expect(orgModule.manager.tree.selectOrganization).toHaveBeenCalledWith({id:'fake'});
       });

        it("should respond on entity:search event (edit) ", function() {
           spyOn(orgModule.clientActionFactory, 'cancelIfEdit').andCallFake(function(){ return { invokeAction:function(){return true}}});
           spyOn(orgModule.serverActionFactory, 'search').andCallFake(function(){ return { invokeAction:function(){return true}}});

           orgModule.fire('entity:search');

           expect(orgModule.serverActionFactory.search).toHaveBeenCalled();
        });

        it("should respond on entity:search event (last search text) ", function() {
            spyOn(orgModule.clientActionFactory, 'cancelIfEdit').andCallFake(function() {
                return { invokeAction:function() {
                    return true;
                }}
            });
            spyOn(orgModule.serverActionFactory, 'search').andCallFake(function(){ return { invokeAction:function(){return true}}});

            spyOn(orgModule.entityList, 'setSearchText');
            orgModule.fire('entity:search', {text:'test'});

            expect(orgModule.entityList.setSearchText);
        });

        it("should respond on entity:next event", function() {
            spyOn(orgModule.serverActionFactory, 'next').andCallFake(function(){ return { invokeAction:function(){return true}}});

            orgModule.fire('entity:next');

            expect(orgModule.serverActionFactory.next).toHaveBeenCalled();
        });

         it("should respond on entity:selectAndGetDetails event", function() {
            spyOn(orgModule.serverActionFactory, 'selectAndGetDetails').andCallFake(function(){ return { invokeAction:function(){return true}}});

            orgModule.fire('entity:selectAndGetDetails', {entity: {getNameWithTenant:function(){return 'fafasf';}}});

            expect(orgModule.serverActionFactory.selectAndGetDetails).toHaveBeenCalled();
        });

        it("should respond on result:changed event", function() {
            spyOn(orgModule.entityList, 'setEntities');
            spyOn(orgModule.entityList, 'restoreSelectedEntity');

            orgModule.fire('result:changed', {responseData:{entities: {collect:function(){return [];}}}});

            expect(orgModule.entityList.setEntities).toHaveBeenCalled();
            expect(orgModule.entityList.restoreSelectedEntity).toHaveBeenCalled();
        });


        it("should respond on result:next event", function() {
            spyOn(orgModule.entityList, 'addEntities');

            orgModule.fire('result:next', {responseData:{entities: {length: 10, collect:function(){return [];}}}});

            expect(orgModule.entityList.addEntities).toHaveBeenCalled();
        });


        it("should respond on entity:detailsLoaded event", function() {
            spyOn(orgModule.manager, 'entityJsonToObject').andCallFake(function(){
                return {getNameWithTenant:function(){return 'false'}}
            });

            spyOn(orgModule.entityList,'update');
            spyOn(orgModule.properties,'show');
            spyOn(orgModule.properties,'setProperties');

            orgModule.fire('entity:detailsLoaded', {responseData:{entities: {length: 10, collect:function(){return [];}}}});

            expect(orgModule.entityList.update).toHaveBeenCalled();
            expect(orgModule.properties.show).toHaveBeenCalled();
            expect(orgModule.properties.setProperties).toHaveBeenCalled();
        });

        it("should respond on searchAvailable:loaded", function() {
            spyOn(orgModule.properties,'setAvailableEntities');

            orgModule.fire('searchAvailable:loaded', {responseData:{entities: {length: 10, collect:function(){return [];}}}});

            expect(orgModule.properties.setAvailableEntities).toHaveBeenCalled();
        });

        it("should respond on searchAssigned:loaded", function() {
            spyOn(orgModule.properties,'setAssignedEntities');

            orgModule.fire('searchAssigned:loaded', {responseData:{entities: {length: 10, collect:function(){return [];}}}});

            expect(orgModule.properties.setAssignedEntities).toHaveBeenCalled();
        });

        it("should respond on nextAvailable:loaded event", function() {
            spyOn(orgModule.properties, 'addAvailableEntities');

            orgModule.fire('nextAvailable:loaded', {responseData:{entities: {length: 10, collect:function(){return [];}}}});

            expect(orgModule.properties.addAvailableEntities).toHaveBeenCalled();
        });

        it("should respond on nextAssigned:loaded event", function() {
            spyOn(orgModule.properties, 'addAssignedEntities');

            orgModule.fire('nextAssigned:loaded', {responseData:{entities: {length: 10, collect:function(){return [];}}}});

            expect(orgModule.properties.addAssignedEntities).toHaveBeenCalled();
        });

        it("should respond on server:error event", function() {
            spyOn(window, 'alert');

            orgModule.fire('server:error', {responseData:{message:'test'}});

            expect(window.alert).toHaveBeenCalled();
        });

        it("should respond on entity:created event", function() {
            spyOn(orgModule.addDialog, 'hide');
            spyOn(orgModule.manager, 'entityJsonToObject').andCallFake(function(){return { getNameWithTenant:function(){return '#42'}}});
            spyOn(orgModule.entityList, 'addEntities');
            spyOn(orgModule.entityList, 'selectEntity');

            orgModule.fire('entity:created', {inputData:{entity:{evalJSON:function(){return '#23'}}}});

            expect(orgModule.addDialog.hide).toHaveBeenCalled();
            expect(orgModule.manager.entityJsonToObject).toHaveBeenCalledWith('#23');
            expect(orgModule.entityList.addEntities).toHaveBeenCalled();
            expect(orgModule.entityList.selectEntity).toHaveBeenCalledWith('#42');
        });

         it("should respond on entity:updated event (Edit mode)", function() {
            spyOn(orgModule.properties, 'changeMode');
            spyOn(orgModule.manager, 'entityJsonToObject').andCallFake(function(){return { getNameWithTenant:function(){return '#42'}}});
            orgModule.properties.isEditMode = true;

            orgModule.fire('entity:updated', {responseData:{roles: true}, inputData:{entity:{evalJSON:function(){return '#23'}}, entityName:'test'}});

            expect(orgModule.properties.changeMode).toHaveBeenCalledWith(false);
            orgModule.properties.isEditMode = false;
        });

        it("should respond on entity:updated event (entity)", function() {
            spyOn(orgModule.entityList, 'selectEntity');
            spyOn(orgModule.entityList, 'update');
            spyOn(orgModule.manager, 'entityJsonToObject').andCallFake(function(){return { getNameWithTenant:function(){return '#42'}}});

            orgModule.fire('entity:updated', {responseData:{roles: true}, inputData:{entity:{evalJSON:function(){return '#23'}}, entityName:'test'}});

            expect(orgModule.entityList.update).toHaveBeenCalled();
            expect(orgModule.entityList.selectEntity).toHaveBeenCalled();
        });

        it("should respond on entity:updated event (entity)", function() {
            spyOn(orgModule.entityList, 'selectEntity');
            spyOn(orgModule.entityList, 'update');
            spyOn(orgModule.manager, 'entityJsonToObject').andCallFake(function(){return { getNameWithTenant:function(){return '#42'}}});

            orgModule.fire('entity:updated', {responseData:{roles: true}, inputData:{entity:{evalJSON:function(){return '#23'}}, entityName:'test'}});

            expect(orgModule.entityList.update).toHaveBeenCalled();
            expect(orgModule.entityList.selectEntity).toHaveBeenCalled();
        });

         it("should respond on entity:deleted event (entity)", function() {
            spyOn(orgModule.entityList, 'selectEntity');
            spyOn(orgModule.entityList, 'update');
            spyOn(orgModule.manager, 'entityJsonToObject').andCallFake(function(){return { getNameWithTenant:function(){return '#42'}}});

            orgModule.fire('entity:updated', {responseData:{roles: true}, inputData:{entity:{evalJSON:function(){return '#23'}}, entityName:'test'}});

            expect(orgModule.entityList.update).toHaveBeenCalled();
            expect(orgModule.entityList.selectEntity).toHaveBeenCalled();
        });

        it("should respond on entity:deleted event", function() {
            spyOn(orgModule.entityList, 'remove');

            orgModule.fire('entity:deleted', {inputData:{entity:{evalJSON:function(){return '#23'}}, entityName:'test'}});

            expect(orgModule.entityList.remove).toHaveBeenCalled();
        });

        it("should respond on entities:deleted event", function() {
            spyOn(orgModule.entityList, 'remove');

            orgModule.fire('entity:deleted', {inputData:{entity:{evalJSON:function(){return '#23'}}, entityName:'test'}});

            expect(orgModule.entityList.remove).toHaveBeenCalled();
        });

        it("should fire event org:browse on reloadEntities", function() {
           spyOn(orgModule.serverActionFactory, 'browse').andCallFake(function(){ return { invokeAction:function(){return false}}});

           orgModule.manager.reloadEntities();

           expect(orgModule.serverActionFactory.browse).toHaveBeenCalled();
        });
    });
});
