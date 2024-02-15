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
 * @version: $Id: org.user.mng.main.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "org.user.mng.components"], function(jQuery, orgModule) {

     describe("User Management", function(){
         if (!orgModule.tree) {
             orgModule.tree = {};
         }

         if (!orgModule.manager) {
             orgModule.manager = {};
         }

         if (!orgModule.manager.tree) {
             orgModule.manager.tree = {
                 getOrganization:function() {
                     return 'BZC';
                 }
             }
         }

         if (!orgModule.entityList){
             orgModule.entityList = {
                 getSelectedEntities:function(){}
             }
         }

          it('should be able to check if user can be added (has organisation not null)', function(){
              spyOn(orgModule.manager.tree, 'getOrganization').andCallFake(function(){return 'aaa'});
              expect(canAddUser()).toBeTruthy();
          });

          it('should be able to check if user can be added (has organisation null)', function(){
              spyOn(orgModule.manager.tree, 'getOrganization').andCallFake(function(){return null});
              expect(canAddUser()).toBeFalsy();
          });

         it('should be able to check if user can be added (has`nt tree)', function(){
              var t = orgModule.tree;
              orgModule.tree = false;
              expect(canAddUser()).toBeTruthy();
              orgModule.tree = t;
         });

         it('should be able to check if can enable all users (can)', function(){
              spyOn(orgModule.entityList,'getSelectedEntities').andCallFake(function(){return [1,2,3]});
              expect(canEnableAll()).toBeTruthy();
         });

         it('should be able to check if can enable all users (cannot)', function(){
              spyOn(orgModule.entityList,'getSelectedEntities').andCallFake(function(){return []});
              expect(canEnableAll()).toBeFalsy()
         });

         it('should be able to check if can disable all users (can)', function(){
             spyOn(orgModule.entityList,'getSelectedEntities').andCallFake(function(){return [1,2,3]});
             spyOn(window,'isLoggedInUserSelected').andCallFake(function(){return true});
             expect(canEnableAll()).toBeTruthy();
         });

         it('should be able to check if can disable all users (cannot)', function(){
              spyOn(orgModule.entityList,'getSelectedEntities').andCallFake(function(){return []});
              spyOn(window,'isLoggedInUserSelected').andCallFake(function(){return true});
              expect(canEnableAll()).toBeFalsy();
         });

         it('should be able to check if can delete all users (can)', function(){
             spyOn(orgModule.entityList,'getSelectedEntities').andCallFake(function(){return [1,2,3]});
             spyOn(window,'isLoggedInUserSelected').andCallFake(function(){return false});
             expect(canEnableAll()).toBeTruthy();
         });

         it('should be able to check if can delete all users (cannot)', function(){
              spyOn(orgModule.entityList,'getSelectedEntities').andCallFake(function(){return []});
              spyOn(window,'isLoggedInUserSelected').andCallFake(function(){return false});
              expect(canEnableAll()).toBeFalsy();
         });

         it ('should can initialize', function(){

             var fakeServer = sinon.fakeServer.create();
             fakeServer.respondWith([200, { "Content-Type": "application/json" }, JSON.stringify({response: {data: {}}})]);

             spyOn(orgModule.manager, 'initialize');
             spyOn(orgModule.userManager.userList, 'initialize');
             spyOn(orgModule.properties, 'initialize');
             spyOn(orgModule.userManager.addDialog, 'initialize');
             orgModule.manager.state = {text: 'test'};
             localContext.userMngInitOptions = {currentUser:{}};

             spyOn(layoutModule, "resizeOnClient"); // to stub real execution of this function

             orgModule.userManager.initialize();
             spyOn(orgModule, "fire"); // to stub real execution of the onSuccess handler
             fakeServer.respond();

             expect(orgModule.manager.initialize).toHaveBeenCalled();
             expect(orgModule.userManager.userList.initialize).toHaveBeenCalled();
             expect(orgModule.properties.initialize).toHaveBeenCalled();
             expect(orgModule.userManager.addDialog.initialize).toHaveBeenCalled();

             fakeServer.restore();
         });

            it ('should know if logged in user is selected (not selected)', function(){
                spyOn(orgModule.entityList, 'getSelectedEntities').andCallFake(function(){return {length:10,detect:function(){return true}}});
                orgModule.userManager.options = {currentUser:{}};

                expect(isLoggedInUserSelected()).toBeTruthy();
            });

            it ('should know if logged in user is selected (no selected users at all)', function(){
                spyOn(orgModule.entityList, 'getSelectedEntities').andCallFake(function(){return {length:0,detect:function(){return true}}});
                orgModule.userManager.options = {currentUser:{}};

                expect(isLoggedInUserSelected()).toBeFalsy();
            });

         it ('should know if logged in user is selected (selected)', function(){
              spyOn(orgModule.entityList, 'getSelectedEntities').andCallFake(function(){return {length:10,detect:function(){return false}}});
              orgModule.userManager.options = {currentUser:{}};

              expect(isLoggedInUserSelected()).toBeFalsy();
          });
     });
 });