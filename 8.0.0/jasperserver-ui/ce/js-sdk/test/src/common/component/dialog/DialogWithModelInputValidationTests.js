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
import DialogWithModelInputValidation from 'src/common/component/dialog/DialogWithModelInputValidation';
import Dialog from 'src/common/component/dialog/Dialog';
import Validation from 'backbone-validation';
import Backbone from 'backbone';
import $ from 'jquery';
describe('DialogWithModelInputValidation', function () {
    it('should be Backbone.View instance', function () {
        expect(typeof DialogWithModelInputValidation).toBe('function');
        expect(DialogWithModelInputValidation.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should be Dialog instance', function () {
        expect(DialogWithModelInputValidation.prototype instanceof Dialog).toBeTruthy();
    });
    it('should be able to bindValidation', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), validationBindSpy = sinon.spy(Validation, 'bind');
        dialog.bindValidation();
        expect(validationBindSpy).toHaveBeenCalledWith(dialog, {
            valid: dialog.fieldIsValid,
            invalid: dialog.fieldIsInvalid,
            forceUpdate: true,
            selector: 'name'
        });
        validationBindSpy.restore();
        dialog.remove();
    });
    it('should be able to unbindValidation', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), validationUnbindSpy = sinon.spy(Validation, 'unbind');
        dialog.unbindValidation();
        expect(validationUnbindSpy).toHaveBeenCalledWith(dialog);
        validationUnbindSpy.restore();
        dialog.remove();
    });
    it('should be able to clear validation errors', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
            model: model,
            buttons: [{
                label: 'Save',
                action: 'save',
                primary: true
            }]
        });
        dialog.open();
        dialog.$('label').addClass('error');
        dialog.$('.message.warning').text('error');
        dialog.clearValidationErrors();
        expect(dialog.$('label').hasClass('error')).toBe(false);
        expect(dialog.$('.message.warning').text()).toBe('');
        dialog.remove();
    });
    it('should have keyup/change events in "events" object', function () {
        expect(DialogWithModelInputValidation.prototype.events['keyup input[type=text], textarea, select']).toBeDefined();
        expect(DialogWithModelInputValidation.prototype.events['change input[type=text], input:checkbox, textarea, select']).toBeDefined();
    });
    it('should update model attribute from DOM event and trigger validation in case of input="text"', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), $target = $('<input type=\'text\'></div>');
        model.validate = function () {
        };
        var setSpy = sinon.spy(model, 'set'), validateSpy = sinon.spy(model, 'validate');
        $target.attr('name', 'label');
        $target.val('  test  ');
        dialog.updateModelProperty({ target: $target[0] });
        expect(setSpy).toHaveBeenCalledWith({ label: 'test' });
        expect(validateSpy).toHaveBeenCalledWith({ label: 'test' });
        setSpy.restore();
        validateSpy.restore();
        dialog.remove();
    });
    it('should update model attribute from DOM event and trigger validation in case of textarea', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), $target = $('<textarea></textarea>');
        model.validate = function () {
        };
        var setSpy = sinon.spy(model, 'set'), validateSpy = sinon.spy(model, 'validate');
        $target.attr('name', 'label');
        $target.val('  test  ');
        dialog.updateModelProperty({ target: $target[0] });
        expect(setSpy).toHaveBeenCalledWith({ label: 'test' });
        expect(validateSpy).toHaveBeenCalledWith({ label: 'test' });
        setSpy.restore();
        validateSpy.restore();
        dialog.remove();
    });
    it('should update model attribute from DOM event and trigger validation in case of non-multiple select', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), $target = $('<select><option selected=\'selected\' val=\'test\'>test</option></select>');
        model.validate = function () {
        };
        var setSpy = sinon.spy(model, 'set'), validateSpy = sinon.spy(model, 'validate');
        $target.attr('name', 'label');
        $target.val('test');
        dialog.updateModelProperty({ target: $target[0] });
        expect(setSpy).toHaveBeenCalledWith({ label: 'test' });
        expect(validateSpy).toHaveBeenCalledWith({ label: 'test' });
        setSpy.restore();
        validateSpy.restore();
        dialog.remove();
    });
    it('should update model attribute from DOM event and trigger validation in case of input:checkbox', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), $target = $('<input type=\'checkbox\'>Test</select>');
        model.validate = function () {
        };
        var setSpy = sinon.spy(model, 'set'), validateSpy = sinon.spy(model, 'validate');
        $target.attr('name', 'label');
        $target.prop('checked', true);
        dialog.updateModelProperty({ target: $target[0] });
        expect(setSpy).toHaveBeenCalledWith({ label: true });
        expect(validateSpy).toHaveBeenCalledWith({ label: true });
        setSpy.restore();
        validateSpy.restore();
        dialog.remove();
    });
    it('should call \'beforeModelPropertySet\' hook if available when updating model attrs', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), $target = $('<input type=\'text\'></div>');
        dialog.beforeModelPropertySet = sinon.stub();
        model.validate = function () {
        };
        var setSpy = sinon.spy(model, 'set'), validateSpy = sinon.spy(model, 'validate');
        $target.attr('name', 'label');
        $target.val('  test  ');
        dialog.updateModelProperty({ target: $target[0] });
        expect(dialog.beforeModelPropertySet).toHaveBeenCalledWith({ 'label': 'test' });
        dialog.remove();
    });
    it('should be able to mark field as invalid', function () {
        var model = new Backbone.Model({ label: 'a' }), dialog = new DialogWithModelInputValidation({
            model: model,
            content: '<div><input name=\'label\'/><span class=\'message warning\'></span></div>',
            buttons: [{
                label: 'Save',
                action: 'save',
                primary: true
            }]
        });
        dialog.open();
        dialog.fieldIsInvalid(dialog, 'label', 'test error', 'name');
        var $parentEl = dialog.$('[name="label"]').parent();
        expect($parentEl.hasClass('error')).toBe(true);
        expect($parentEl.find('.message.warning').text()).toBe('test error');
        dialog.remove();
    });
    it('should be able to mark field as valid', function () {
        var model = new Backbone.Model({ label: 'a' }), dialog = new DialogWithModelInputValidation({
            model: model,
            content: '<div><input name=\'label\'/><span class=\'message warning\'></span></div>',
            buttons: [{
                label: 'Save',
                action: 'save',
                primary: true
            }]
        });
        dialog.open();
        dialog.fieldIsInvalid(dialog, 'label', 'test error', 'name');
        dialog.fieldIsValid(dialog, 'label', 'name');
        var $parentEl = dialog.$('[name="label"]').parent();
        expect($parentEl.hasClass('error')).toBe(false);
        expect($parentEl.find('.message.warning').text()).toBe('');
        dialog.remove();
    });
    it('should unbind validation on remove and then call base Dialog remove method', function () {
        var model = new Backbone.Model(), dialog = new DialogWithModelInputValidation({
                model: model,
                buttons: [{
                    label: 'Save',
                    action: 'save',
                    primary: true
                }]
            }), unbindValidationSpy = sinon.spy(dialog, 'unbindValidation'), dialogRemoveSpy = sinon.spy(Dialog.prototype, 'remove');
        dialog.remove();
        expect(unbindValidationSpy).toHaveBeenCalled();
        expect(dialogRemoveSpy).toHaveBeenCalled();
        unbindValidationSpy.restore();
        dialogRemoveSpy.restore();
    });
});