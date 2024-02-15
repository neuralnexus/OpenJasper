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
 * @version: $Id: org.user.mng.actions.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "org.user.mng.actions", "text!templates/users.htm"], function(jQuery, orgModule, templUsers) {

    describe("Additional actions", function() {

        beforeEach(function(){
            setTemplates(templUsers);
        });

        it('should provide enable all action', function() {
            expect(orgModule.userActionFactory.enableAll).toBeDefined();

            spyOn(orgModule, 'ServerAction').andCallThrough();

            orgModule.userActionFactory.enableAll({users:{collect:function() {
                return ['cat','dog']
            }}});

            expect(orgModule.ServerAction).toHaveBeenCalledWith('enableAll', { userNames : '["cat","dog"]' });
        });

        it('should provide disable all action', function() {
            expect(orgModule.userActionFactory.disableAll).toBeDefined();

            spyOn(orgModule, 'ServerAction').andCallThrough();

            orgModule.userActionFactory.disableAll({users:{collect:function() {
                return ['cat','dog']
            }}});

            expect(orgModule.ServerAction).toHaveBeenCalledWith('disableAll', { userNames : '["cat","dog"]' });
        });

        it('should provide disable all action', function() {
            expect(orgModule.userActionFactory.disableAll).toBeDefined();

            spyOn(orgModule, 'ServerAction').andCallThrough();

            orgModule.userActionFactory.disableAll({users:{collect:function() {
                return ['cat','dog']
            }}});

            expect(orgModule.ServerAction).toHaveBeenCalledWith('disableAll', { userNames : '["cat","dog"]' });
        });

        it('should provide disable all action', function() {
            expect(orgModule.userActionFactory.disableAll).toBeDefined();

            spyOn(orgModule, 'ServerAction').andCallThrough();

            orgModule.userActionFactory.disableAll({users:{collect:function() {
                return ['cat','dog']
            }}});

            expect(orgModule.ServerAction).toHaveBeenCalledWith('disableAll', { userNames : '["cat","dog"]' });
        });

        it('should provide enableAllUsers factory method', function() {
            expect(orgModule.userManager.actionFactory.enableAllUsers).toBeDefined();

            spyOn(window, 'invokeUserAction');
            spyOn(orgModule.entityList, 'getSelectedEntities').andCallFake(function() {
                return []
            });

            orgModule.userManager.actionFactory.enableAllUsers().invokeAction();

            expect(window.invokeUserAction).toHaveBeenCalledWith('enableAll', {users:[]});

        });

        it('should provide enableAllUsers factory method', function() {
            expect(orgModule.userManager.actionFactory.enableAllUsers).toBeDefined();

            spyOn(window, 'invokeUserAction');
            spyOn(orgModule.entityList, 'getSelectedEntities').andCallFake(function() {
                return []
            });

            orgModule.userManager.actionFactory.enableAllUsers().invokeAction();

            expect(window.invokeUserAction).toHaveBeenCalledWith('enableAll', {users:[]});

        });

        it('should provide disableAllUsers factory method', function() {
            expect(orgModule.userManager.actionFactory.disableAllUsers).toBeDefined();

            spyOn(window, 'invokeUserAction');
            spyOn(orgModule.entityList, 'getSelectedEntities').andCallFake(function() {
                return []
            });

            orgModule.userManager.actionFactory.disableAllUsers().invokeAction();

            expect(window.invokeUserAction).toHaveBeenCalledWith('disableAll', {users:[]});

        });

         it('should login work', function() {
            expect(orgModule.userManager.actionFactory.login).toBeDefined();

            spyOn(jQuery('#loginAsForm')[0], 'submit');

            orgModule.userManager.actionFactory.login({user:{getNameWithTenant:function(){return 'me'}}}).invokeAction();

            expect(jQuery('#loginAsForm')[0].submit).toHaveBeenCalled();
            expect(jQuery('#j_username').val()).toEqual('me');
        });

    });

});