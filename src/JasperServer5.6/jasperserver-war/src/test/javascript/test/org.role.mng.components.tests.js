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
 * @version: $Id: org.role.mng.components.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "org.role.mng.components", "text!templates/roleManagementComponents.htm"], function(jQuery, orgModule, roleManagementComponents) {

    describe('Role Management components', function() {

        beforeEach(function(){
            setTemplates(roleManagementComponents);
        });

        describe(' -- roleList', function(){

            it('should be able to initialize roleList and define orgModule.entityList methods', function() {
                spyOn(orgModule.entityList, 'initialize');

                orgModule.roleManager.roleList.initialize({});

                expect(orgModule.entityList.initialize).toHaveBeenCalled();
                expect(orgModule.entityList._createEntityItem).toBeDefined();
            });
        });

        describe(' -- properties', function(){

            it('should be able to initialize properties and define orgModule.entityList methods', function() {
                spyOn(orgModule.properties, 'initialize');
                spyOn(orgModule.roleManager.properties, '_initCustomEvents');

                orgModule.roleManager.properties.initialize();

                expect(orgModule.properties.setProperties).toBeDefined();
                expect(orgModule.properties._deleteEntity).toBeDefined();
                expect(orgModule.properties._editEntity).toBeDefined();
                expect(orgModule.properties._showEntity).toBeDefined();
                expect(orgModule.properties.validate).toBeDefined();
                expect(orgModule.properties.isChanged).toBeDefined();
                expect(orgModule.properties.save).toBeDefined();
                expect(orgModule.properties.cancel).toBeDefined();
                expect(orgModule.properties.canEdit).toBeDefined();
                expect(orgModule.properties.canDelete).toBeDefined();

                expect(orgModule.roleManager.properties._initCustomEvents).toHaveBeenCalled();
                expect(orgModule.properties.initialize).toHaveBeenCalled();
            });
        });

        describe(' -- addDialog', function() {
            var addRole, addRoleBtn,
                addRoleBtnWrap, addRoleName,
                rmAddDialog, organization, savedDialogs;

            beforeEach(function(){
                savedDialogs = dialogs;
                dialogs = {
                    popup:{
                        hide:function() {
                            return true;
                        },
                        show:function() {
                            return false;
                        }
                    }
                };

                addRole = jQuery('#addRole');
                addRoleBtn = jQuery('#addRoleBtn');
                addRoleBtnWrap = jQuery('#addRoleBtn>.wrap');
                addRoleName = jQuery('#addRoleName');
                rmAddDialog = orgModule.roleManager.addDialog;
                organization = {
                    isRoot:function() {
                        return true;
                    }
                };
                rmAddDialog.initialize();
            });

            afterEach(function(){
                dialogs = savedDialogs;
            });

            it('should have public methods', function() {
                expect(rmAddDialog.initialize).toBeDefined();
                expect(rmAddDialog.show).toBeDefined();
                expect(rmAddDialog.hide).toBeDefined();
            });

            it('should be able to initialize addDialog', function() {

                expect(rmAddDialog.addRole).toBeDefined();
                expect(rmAddDialog.addBtn).toBeDefined();
                expect(rmAddDialog.cancelBtn).toBeDefined();
                expect(rmAddDialog.roleName).toBeDefined();
                expect(rmAddDialog.roleName.regExp).toBeDefined();
                expect(rmAddDialog.roleName.unsupportedSymbols).toBeDefined();
                expect(rmAddDialog.roleName.inputValidator).toBeDefined();
                expect(rmAddDialog._validators).toBeDefined();
            });

            it('should be able to hide dialog and clear input', function() {
                spyOn(dialogs.popup, 'hide');

                rmAddDialog.hide();

                expect(dialogs.popup.hide).toHaveBeenCalled();
                expect(addRoleName).toHaveValue('');
            });

            it('should be able to show popup dialog, change inner text', function() {
                expect(addRoleBtnWrap).toHaveText('Add Role to [Organization Name]');
                expect(addRoleBtn).not.toHaveAttr('title');
                expect(addRole).toHaveClass('error');

                spyOn(orgModule, 'getMessage').andCallFake(function () {return '42'});
                spyOn(dialogs.popup, 'show');

                rmAddDialog.show(organization);

                expect(addRole).not.toHaveClass('error');
                expect(addRoleBtnWrap).toHaveText('42');
                expect(addRoleBtn).toHaveAttr('title', '42');
                expect(dialogs.popup.show).toHaveBeenCalled();
            });

            it('should be able to add new role', function() {
                spyOn(rmAddDialog, '_validate').andCallFake(function() {return true;});
                spyOn(orgModule, 'Role').andCallThrough();
                spyOn(window, 'invokeServerAction');

                rmAddDialog._doAdd();

                expect(window.invokeServerAction).toHaveBeenCalled();
                expect(orgModule.Role).toHaveBeenCalled();
                expect(rmAddDialog._validate).toHaveBeenCalled();
            });
        });
    });
});
