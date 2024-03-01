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

import _ from 'underscore';
import sinon from 'sinon';
import jQuery from 'jquery';
import orgModule from 'src/org/org.root.user';
import dialogs from 'src/components/components.dialogs';
import propertiesText from './test/templates/properties.htm';
import addUserTmpl from './test/templates/addUserDialog.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import { ValidationModule } from 'src/util/utils.common';

describe('Org User Manager', function () {
    var sandbox;
    beforeEach(function () {
        sandbox = sinon.createSandbox();
        setTemplates(propertiesText, addUserTmpl);
    });
    afterEach(function () {
        sandbox.restore();
    });
    describe('User Management component List', function () {
        it('should initialize', function () {
            spyOn(orgModule.entityList, 'initialize');
            expect(orgModule.entityList.initialize).toBeTruthy();
            expect(orgModule.entityList._createEntityItem).toBeDefined();
        });
    });
    describe('User Management properties ', function () {
        it('should initialize', function () {
            spyOn(orgModule.properties, 'initialize');
            spyOn(orgModule.userManager.properties, '_initCustomEvents');
            orgModule.userManager.properties.initialize({
                _: _,
                currentUser: {}
            });
            expect(orgModule.properties.initialize).toHaveBeenCalled();
            expect(orgModule.userManager.properties._initCustomEvents).toHaveBeenCalled();    // variable profileAttributesList is absent in source code
            //expect(orgModule.userManager.properties.profileAttributesList).toBeDefined();
            // variable profileAttributesList is absent in source code
            //expect(orgModule.userManager.properties.profileAttributesList).toBeDefined();
            expect(orgModule.userManager.properties.name).toBeDefined();
            expect(orgModule.userManager.properties.id).toBeDefined();
            expect(orgModule.userManager.properties.email).toBeDefined();
            expect(orgModule.userManager.properties.enabled).toBeDefined();
            expect(orgModule.userManager.properties.external).toBeDefined();
            expect(orgModule.userManager.properties.pass).toBeDefined();
            expect(orgModule.userManager.properties.confirmPass).toBeDefined();
            expect(orgModule.userManager.properties.email.blurValidator).toBeDefined();
            expect(orgModule.userManager.properties._validators).toBeDefined();
            expect(orgModule.properties.setProperties).toBeDefined();
            expect(orgModule.properties._deleteEntity).toBeDefined();
            expect(orgModule.properties._loginAsUser).toBeDefined();
            expect(orgModule.properties._editEntity).toBeDefined();
            expect(orgModule.properties._showEntity).toBeDefined();
            expect(orgModule.properties.validate).toBeDefined();
            expect(orgModule.properties.isChanged).toBeDefined();
            expect(orgModule.properties.save).toBeDefined();
            expect(orgModule.properties.cancel).toBeDefined();
        });
    });
    describe('User Management component Dialog', function () {
        const errorSelector = 'span.message.warning';

        it('should initialize', function () {
            orgModule.userManager.addDialog.initialize();
            expect(orgModule.userManager.addDialog.addUser).toBeDefined();
            expect(orgModule.userManager.addDialog.addBtn).toBeDefined();
            expect(orgModule.userManager.addDialog.cancelBtn).toBeDefined();
            expect(orgModule.userManager.addDialog.fullName).toBeDefined();
            expect(orgModule.userManager.addDialog.userName).toBeDefined();
            expect(orgModule.userManager.addDialog.userEmail).toBeDefined();
            expect(orgModule.userManager.addDialog.enableUser).toBeDefined();
            expect(orgModule.userManager.addDialog.password).toBeDefined();
            expect(orgModule.userManager.addDialog.confirmPassword).toBeDefined();
            expect(orgModule.userManager.addDialog.userName.regExp).toBeDefined();
            expect(orgModule.userManager.addDialog.userName.regExpForReplacement).toBeDefined();
            expect(orgModule.userManager.addDialog.userName.unsupportedSymbols).toBeDefined();
            expect(orgModule.userManager.addDialog.userName.inputValidator).toBeDefined();
            expect(orgModule.userManager.addDialog._validators).toBeDefined();
        });
        it('should show', function () {
            spyOn(dialogs.popup, 'show');
            orgModule.userManager.addDialog.show();
            expect(jQuery(orgModule.userManager.addDialog.userName).hasClass('error')).toBeFalsy();
            expect(jQuery(orgModule.userManager.addDialog.password).hasClass('error')).toBeFalsy();
            expect(jQuery(orgModule.userManager.addDialog.confirmPassword).hasClass('error')).toBeFalsy();
            expect(jQuery(orgModule.userManager.addDialog.userEmail).hasClass('error')).toBeFalsy();
            expect(orgModule.userManager.addDialog.userName.changedByUser).toBeFalsy();
            expect(orgModule.userManager.addDialog.password.changedByUser).toBeFalsy();
            expect(orgModule.userManager.addDialog.confirmPassword.changedByUser).toBeFalsy();
            expect(orgModule.userManager.addDialog.userEmail.changedByUser).toBeFalsy();
            expect(dialogs.popup.show).toHaveBeenCalled();
        });
        it('should clean errors on show', function() {
            const userNameError = jQuery(orgModule.userManager.addDialog.userName).siblings(errorSelector);
            const passwordError = jQuery(orgModule.userManager.addDialog.password).siblings(errorSelector);
            const confirmPasswordError = jQuery(orgModule.userManager.addDialog.confirmPassword).siblings(errorSelector);
            const userEmailError = jQuery(orgModule.userManager.addDialog.userEmail).siblings(errorSelector);

            userNameError.text('error');
            passwordError.text('error');
            confirmPasswordError.text('error');
            userEmailError.text('error');

            orgModule.userManager.addDialog.show();

            expect(userNameError.text()).toEqual('');
            expect(passwordError.text()).toEqual('');
            expect(confirmPasswordError.text()).toEqual('');
            expect(userEmailError.text()).toEqual('');
        });
        it('should hide and reset', function () {
            spyOn(dialogs.popup, 'hide').and.callThrough();
            orgModule.userManager.addDialog.hide();
            expect(dialogs.popup.hide).toHaveBeenCalled();
            expect(orgModule.userManager.addDialog.userName.value).toEqual('');
            expect(orgModule.userManager.addDialog.fullName.value).toEqual('');
            expect(orgModule.userManager.addDialog.userEmail.value).toEqual('');
            expect(orgModule.userManager.addDialog.enableUser.checked).toEqual(true);
            expect(orgModule.userManager.addDialog.password.value).toEqual('');
            expect(orgModule.userManager.addDialog.confirmPassword.value).toEqual('');
        });
        it('should check if user exists before add', function () {
            spyOn(orgModule.userManager.addDialog, '_validate').and.callFake(function () {
                return true;
            });
            sandbox.stub(orgModule.serverActionFactory, 'exist').callsFake(function () {
                return {
                    invokeAction: function () {
                    }
                };
            });
            orgModule.userManager.addDialog._doAdd();
            expect(orgModule.userManager.addDialog._validate).toHaveBeenCalled();
            expect(orgModule.serverActionFactory.exist).toHaveBeenCalled();
        });
        it('should validate input fields before add', function () {
            sandbox.stub(ValidationModule, 'validateLegacy');

            const userNameError = jQuery(orgModule.userManager.addDialog.userName).siblings(errorSelector);

            userNameError.text('error');

            sandbox.stub(orgModule.serverActionFactory, 'exist');

            orgModule.userManager.addDialog._doAdd();

            expect(orgModule.serverActionFactory.exist).not.toHaveBeenCalled();
        });
    });
});