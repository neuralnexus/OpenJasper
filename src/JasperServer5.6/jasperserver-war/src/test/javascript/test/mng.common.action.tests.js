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
 * @version: $Id: mng.common.action.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "mng.common.actions", "core.ajax"], function(jQuery, orgModule) {

    describe("Client actions", function(){
          if (!orgModule.entityList){
                orgModule.entityList = {
                    getSelectedEntities: function() { return [{getDisplayName:function() {return 'testName';}}]},
                    deselectAll: function() {return true}
                }
            }

            if (!orgModule.addDialog){
                orgModule.addDialog = {
                    show: function() { return false; }
                }
            }


       it("should return 'true' if not changed (cancelIfEdit action)", function() {
           spyOn(orgModule.properties, 'isChanged').andCallFake(function(){return false});
           var result = invokeClientAction("cancelIfEdit", { entity: {getDisplayName:function(){return 'test';} }});
           expect(result).toBeTruthy();
       });

       it("should return 'true' if user pressed Ok button (cancelIfEdit action)", function() {
           spyOn(orgModule.properties, 'isChanged').andCallFake(function(){return true});
           spyOn(window, 'confirm').andCallFake(function(){return true});
           var result = invokeClientAction("cancelIfEdit", { entity: {getDisplayName:function(){return 'test';} }});
           expect(result).toBeTruthy();
       });

        it("should return 'false' if user pressed Cancel button (cancelIfEdit action)", function() {
           spyOn(orgModule.properties, 'isChanged').andCallFake(function(){return true});
            spyOn(window, 'confirm').andCallFake(function(){return false});
           var result = invokeClientAction("cancelIfEdit", { entity: {getDisplayName:function(){return 'test';} }});
           expect(result).toBeFalsy();
       });

        it("should do something (create action)", function() {
            spyOn(orgModule.properties, 'isChanged').andCallFake(function(){return false});
            spyOn(window, 'confirm').andCallFake(function(){return true});

            spyOn(orgModule.entityList, 'deselectAll');
            spyOn(orgModule.addDialog, 'show');


            invokeClientAction("create");
            expect(orgModule.entityList.deselectAll).toHaveBeenCalled();
            expect(orgModule.addDialog.show).toHaveBeenCalled();

       });

       it("should delete all selected items (deleteAll action)", function() {
            spyOn(window, 'confirm').andCallFake(function(){return true});
            spyOn(orgModule.serverActionFactory, 'deleteAll').andCallFake(function(){ return { invokeAction:function(){}}});

            invokeClientAction("deleteAll");

            expect(orgModule.serverActionFactory.deleteAll).toHaveBeenCalled();

       });

       it("should delete selected item (delete action)", function() {
           spyOn(window, 'confirm').andCallFake(function(){return true});

           spyOn(orgModule.serverActionFactory, 'delete').andCallFake(function(){ return { invokeAction:function(){}}});

            invokeClientAction("delete",{entity:{getDisplayName:function() {return 'test';}}});

            expect(orgModule.serverActionFactory.delete).toHaveBeenCalled();

       });
    });

    describe("Server actions", function(){

        var fakeServer;
        var emptyRequest = {};
        var emptyResponse = { inputData : undefined, unencryptedEntity : undefined, responseData : undefined };

        beforeEach(function(){
            fakeServer = sinon.fakeServer.create();
        });

        afterEach(function(){
            fakeServer.restore();
        });

        it("should fire event (server action browse)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('browse');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('result:changed', emptyResponse);
        });

        it("should fire event (server action search)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('search');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('result:changed', emptyResponse);
        });

         it("should fire event (server action next)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('next');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('result:next', emptyResponse);
         });

         it("should fire event (server action selectAndGetDetails)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('selectAndGetDetails');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('entity:detailsLoaded', emptyResponse);
         });

         it("should fire event (server action searchAvailable)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('searchAvailable');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('searchAvailable:loaded', emptyResponse);
         });

         it("should fire event (server action searchAssigned)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('searchAssigned');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('searchAssigned:loaded', emptyResponse);
         });

         it("should fire event (server action nextAvailable)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('nextAvailable');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('nextAvailable:loaded', emptyResponse);
         });

         it("should fire event (server action nextAssigned)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('nextAssigned');
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('nextAssigned:loaded', emptyResponse);
         });

         it("should fire event (server action create)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('create',{entity:{}});
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('entity:created', { inputData : { entity : '{}' }, unencryptedEntity : undefined, responseData : undefined });
         });

         it("should fire event (server action update)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('update',{entity:{}});
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('entity:updated', { inputData : { entityName : undefined, entity : '{}', assignedEntities : undefined, unassignedEntities : undefined }, unencryptedEntity : undefined, responseData : undefined });
         });

         it("should fire event (server action deleteAll)", function(){
            spyOn(orgModule, 'fire');
            var fakeEntities = {
               collect:function(){return []}
            };
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('deleteAll',{entities:fakeEntities});
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('entities:deleted', { inputData : { entities : '[]' }, unencryptedEntity : undefined, responseData : undefined });
         });

        it("should fire event (server action delete)", function(){
            spyOn(orgModule, 'fire');
            fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify(emptyRequest)]);
            invokeServerAction('delete',{entity : {getNameWithTenant:function(){return "false"}}});
            fakeServer.respond();
            expect(orgModule.fire).toHaveBeenCalledWith('entity:deleted', { inputData : { entity : 'false' }, unencryptedEntity : undefined, responseData : undefined });
         });
    });

});