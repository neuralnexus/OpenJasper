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
import Validation from 'src/common/extension/backboneValidationExtension';
import _ from 'underscore';
import Backbone from 'backbone';
describe('Backbone Validation Extension', function () {
    it('should have custom validators', function () {
        expect(typeof Validation.validators.doesNotContainSymbols).toBe('function');
    });
    it('should trigger additional events if no additional options were passed to \'validate\' method', function () {
        var ModelWithValidation = Backbone.Model.extend(_.extend({ validation: { attr: [{ required: true }] } }, Validation.mixin));
        var model = new ModelWithValidation(), triggerSpy = sinon.spy(model, 'trigger');
        model.validate();
        expect(triggerSpy).toHaveBeenCalledWith('validate:attr', model, 'attr', 'Attr is required');
        triggerSpy.restore();
        triggerSpy = sinon.spy(model, 'trigger');
        model.set('attr', 'value');
        model.validate();
        expect(triggerSpy).toHaveBeenCalledWith('validate:attr', model, 'attr');
        triggerSpy.restore();
    });
    describe('doesNotContainSymbols validator', function () {
        it('should return undefined if value does not contain forbidden symbols', function () {
            expect(Validation.validators.doesNotContainSymbols('test', 'label', '~!#\\$%^|\\s`@&*()\\-+={}\\[\\]:;""\\<\\>,?/\\|\\\\')).toBeUndefined();
        });
        it('should return error message if value contains forbidden symbols', function () {
            expect(Validation.validators.doesNotContainSymbols('test+', 'label', '~!#\\$%^|\\s`@&*()\\-+={}\\[\\]:;""\\<\\>,?/\\|\\\\')).toBe('Attribute \'label\' contains forbidden symbols');
        });
    });
    describe('integerNumber validator', function () {
        it('should return undefined if value is an integer number', function () {
            expect(Validation.validators.integerNumber(11)).toBeUndefined();
            expect(Validation.validators.integerNumber(-1)).toBeUndefined();
        });
        it('should return error message if value is not an integer number', function () {
            expect(Validation.validators.integerNumber('11')).toEqual('Value is not a valid integer number');
            expect(Validation.validators.integerNumber(11.5)).toEqual('Value is not a valid integer number');
            expect(Validation.validators.integerNumber(Number.NaN)).toEqual('Value is not a valid integer number');
            expect(Validation.validators.integerNumber(Number.POSITIVE_INFINITY)).toEqual('Value is not a valid integer number');
            expect(Validation.validators.integerNumber(Number.NEGATIVE_INFINITY)).toEqual('Value is not a valid integer number');
        });
    });
});