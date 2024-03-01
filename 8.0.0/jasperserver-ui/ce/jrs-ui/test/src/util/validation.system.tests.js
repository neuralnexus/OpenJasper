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

import {ValidationModule} from 'src/util/utils.common';
import ValidationTmpl from './test/templates/validation.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import jQuery from 'jquery';

describe('ValidationModule Component', function () {
    var Validation = ValidationModule;
    var inputToValidate;
    function expectValid(element) {
        if (!element) {
            element = inputToValidate;
        }
        expect(Validation.showError).not.toHaveBeenCalled();
        expect(Validation.hideError).toHaveBeenCalledWith(element);
    }
    function expectInvalid(element, message) {
        if (typeof element == 'string' && !message) {
            message = element;
            element = inputToValidate;
        }
        expect(Validation.showError).toHaveBeenCalledWith(element, message);
        expect(Validation.hideError).not.toHaveBeenCalled();
    }
    beforeEach(function () {
        setTemplates(ValidationTmpl);
        inputToValidate = jQuery('#inputToValidate')[0];
        spyOn(Validation, 'showError');
        spyOn(Validation, 'hideError');
    });
    it('should show error if validation fails', function () {
        Validation.validate({
            element: inputToValidate,
            method: function (element) {
                return 'fail message';
            }
        });
        expectInvalid('fail message');
    });
    it('should hide error if validation succeds', function () {
        Validation.validate({
            element: inputToValidate,
            method: function (element) {
                return null;
            }
        });
        expectValid();
    });
    it('should pass parameters to validation method', function () {
        var methodSpy = jasmine.createSpy().and.returnValue(null);
        Validation.validate({
            element: inputToValidate,
            method: methodSpy,
            messages: { myMsg: 'hello, world!' },
            options: { param1: 123 }
        });
        expect(methodSpy).toHaveBeenCalledWith('', { myMsg: 'hello, world!' }, { param1: 123 });
    });
    it('should pass parameters to multiple validators', function () {
        var methodSpy1 = jasmine.createSpy('validationMethod1').and.returnValue(null);
        var methodSpy2 = jasmine.createSpy('validationMethod2').and.returnValue(null);
        Validation.validate({
            element: inputToValidate,
            validators: [
                {
                    method: methodSpy1,
                    messages: { myMsg: 'hello, world!' },
                    options: { param1: 123 }
                },
                {
                    method: methodSpy2,
                    messages: { myMsg: 'bye, world!' },
                    options: { param1: 'abc' }
                }
            ]
        });
        expect(methodSpy1).toHaveBeenCalledWith('', { myMsg: 'hello, world!' }, { param1: 123 });
        expect(methodSpy2).toHaveBeenCalledWith('', { myMsg: 'bye, world!' }, { param1: 'abc' });
    });
    it('should show error if first validator fails', function () {
        Validation.validate({
            element: inputToValidate,
            validators: [
                {
                    method: function () {
                        return 'a message';
                    }
                },
                {
                    method: function () {
                        return null;
                    }
                }
            ]
        });
        expectInvalid('a message');
    });
    it('should succeed if no validator fails', function () {
        Validation.validate({
            element: inputToValidate,
            validators: [
                {
                    method: function () {
                        return null;
                    }
                },
                {
                    method: function () {
                        return null;
                    }
                }
            ]
        });
        expectValid();
    });
    it('should call validator in delegated mode', function () {
        var methodSpy = jasmine.createSpy('validationMethod').and.returnValue(null);
        var container = jQuery('#dynamicInputContainer')[0];
        jQuery('#dynamicInput1').val('ic1Value');
        jQuery('#dynamicInput2').val('ic2Value');
        Validation.validate({
            element: container,
            selector: '.dynamicInput',
            method: methodSpy
        });
        expect(methodSpy).not.toHaveBeenCalledWith(container, undefined, undefined);
        expect(methodSpy).toHaveBeenCalledWith('ic1Value', undefined, undefined);
        expect(methodSpy).toHaveBeenCalledWith('ic2Value', undefined, undefined);
    });
    it('should call validator by name', function () {
        spyOn(Validation.methods, 'mandatory');
        Validation.validate({
            element: inputToValidate,
            method: 'mandatory',
            messages: { mandatory: 'Field is mandatory' }
        });
        expect(Validation.methods.mandatory).toHaveBeenCalled();
    });
    it('mandatory field empty - should fail with standard message', function () {
        inputToValidate.value = '';
        Validation.validate({
            element: inputToValidate,
            method: 'mandatory'
        });
        expectInvalid(Validation.defaultMessages.mandatory);
    });
    it('mandatory field empty - should fail with custom message', function () {
        inputToValidate.value = '';
        Validation.validate({
            element: inputToValidate,
            method: 'mandatory',
            messages: { mandatory: 'Custom: field is mandatory' }
        });
        expectInvalid('Custom: field is mandatory');
    });
    it('mandatory field ok', function () {
        inputToValidate.value = 'some text';
        Validation.validate({
            element: inputToValidate,
            method: 'mandatory',
            messages: { mandatory: 'Field is mandatory' }
        });
        expectValid();
    });
    it('minMax field ok', function () {
        inputToValidate.value = '100';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.mandatory,
            options: {
                min: 10,
                max: 200
            },
            messages: {
                tooSmall: 'Too small',
                tooBig: 'Too big'
            }
        });
        expectValid();
    });
    it('minMax field too big - should fail', function () {
        inputToValidate.value = '300';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.minMax,
            options: {
                min: 10,
                max: 200
            },
            messages: {
                tooSmall: 'Too small',
                tooBig: 'Too big'
            }
        });
        expectInvalid('Too big');
    });
    it('minMax field too small - should fail', function () {
        inputToValidate.value = '5';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.minMax,
            options: {
                min: 10,
                max: 200
            },
            messages: {
                tooSmall: 'Too small',
                tooBig: 'Too big'
            }
        });
        expectInvalid('Too small');
    });
    it('minMaxLength field ok', function () {
        inputToValidate.value = '1234567890';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.minMaxLength,
            options: {
                minLength: 5,
                maxLength: 15
            },
            messages: {
                tooShort: 'Too short',
                tooLong: 'Too long'
            }
        });
        expectValid();
    });
    it('minMaxLength field too short - should fail', function () {
        inputToValidate.value = '123';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.minMaxLength,
            options: {
                minLength: 5,
                maxLength: 15
            },
            messages: {
                tooShort: 'Too short',
                tooLong: 'Too long'
            }
        });
        expectInvalid('Too short');
    });
    it('minMaxLength field too long - should fail', function () {
        inputToValidate.value = '12345678901234567890';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.minMaxLength,
            options: {
                minLength: 5,
                maxLength: 15
            },
            messages: {
                tooShort: 'Too short',
                tooLong: 'Too long'
            }
        });
        expectInvalid('Too long');
    });
    it('should fill placeholders in tooLong message', function () {
        inputToValidate.value = '12345678901234567890';
        Validation.validate({
            element: inputToValidate,
            method: Validation.methods.minMaxLength,
            options: {
                minLength: 5,
                maxLength: 15
            },
            messages: {
                tooShort: 'Too short',
                tooLong: 'max {0} allowed'
            }
        });
        expectInvalid('max 15 allowed');
    });
    it('startsWithLetter field ok', function () {
        inputToValidate.value = 'some text';
        Validation.validate({
            element: inputToValidate,
            method: 'startsWithLetter',
            messages: { shouldStartWithLetter: 'Should start with letter' }
        });
        expectValid();
    });
    it('startsWithLetter invalid', function () {
        inputToValidate.value = '_some text';
        Validation.validate({
            element: inputToValidate,
            method: 'startsWithLetter',
            messages: { shouldStartWithLetter: 'Should start with letter' }
        });
        expectInvalid('Should start with letter');
    });
});