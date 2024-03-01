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

import AttributeModel from 'src/attributes/model/AttributeModel';
import _ from 'underscore';
import i18n from 'src/i18n/AttributeBundle.properties';
import i18nMessageUtil from 'js-sdk/src/common/util/i18nMessage';

var i18nMessage = i18nMessageUtil.extend({bundle: i18n});
var MAX_LENGTH = 255;
var MAX_VALUE_LENGTH = 2000;

describe('AttributeModel Tests', function () {
    var attributeModel, options = {
        id: undefined,
        name: 'name',
        value: 'value',
        description: 'description',
        inherited: false,
        permissionMask: 1,
        secure: true
    };
    beforeEach(function () {
        attributeModel = new AttributeModel(options);
    });
    it('should be properly initialized', function () {
        expect(attributeModel.defaults).toEqual({
            id: undefined,
            name: undefined,
            value: '',
            description: '',
            inherited: false,
            permissionMask: 1,
            secure: false
        });
        expect(attributeModel.validateSameNames).toBeFalsy();
        expect(attributeModel.get('id')).toEqual('name');
        expect(attributeModel.getState('originalState')).toEqual(_.extend({}, options, {id: 'name'}));
        expect(attributeModel.getState('confirmedState')).toEqual(_.extend({}, options, {id: 'name'}));
    });
    it('should have validation object with correct rules', function () {
        expect(attributeModel.validation).toBeDefined();    // name
        // name
        expect(attributeModel.validation.name[0].required).toBeTruthy();
        expect(attributeModel.validation.name[0].msg.code).toEqual(new i18nMessage('attributes.error.attribute.name.empty').code);
        expect(attributeModel.validation.name[1].maxLength).toEqual(MAX_LENGTH);
        expect(attributeModel.validation.name[1].msg.code).toEqual(new i18nMessage('attributes.error.attribute.name.too.long', MAX_LENGTH).code);
        expect(attributeModel.validation.name[2].doesNotContainSymbols).toEqual('\\\\/');
        expect(attributeModel.validation.name[2].msg.code).toEqual(new i18nMessage('attributes.error.attribute.name.invalid').code);
        expect(typeof attributeModel.validation.name[3].fn).toEqual("function");    // value
        // value
        expect(attributeModel.validation.value[0].maxLength).toEqual(MAX_VALUE_LENGTH);
        expect(attributeModel.validation.value[0].msg.code).toEqual(new i18nMessage('attributes.error.attribute.value.too.long', MAX_VALUE_LENGTH).code);    // description
        // description
        expect(attributeModel.validation.description[0].maxLength).toEqual(MAX_LENGTH);
        expect(attributeModel.validation.description[0].msg.code).toEqual(new i18nMessage('attributes.error.attribute.description.too.long', MAX_LENGTH).code);
    });
    it('should validate name', function () {
        var tooLongName = new Array(257).join('n'), attr = [{
            name: 'name1',
            holder: 'tenant',
            inherited: true,
            permissionMask: 2
        }];
        attributeModel.set('name', '');
        expect(attributeModel.validate().name.code).toEqual('attributes.error.attribute.name.empty');
        expect(attributeModel.isValid()).toBeFalsy();
        attributeModel.set('name', tooLongName);
        expect(attributeModel.validate().name.code).toEqual('attributes.error.attribute.name.too.long');
        expect(attributeModel.isValid()).toBeFalsy();
        attributeModel.set('name', '\\\\/');
        expect(attributeModel.validate().name.code).toEqual('attributes.error.attribute.name.invalid');
        expect(attributeModel.isValid()).toBeFalsy();
        attributeModel.holder = 'tenant';
        attributeModel.attr = attr;
        attr[0].inherited = false;
        attributeModel.set('name', 'name1');
        expect(attributeModel.validate().name.code).toEqual('attributes.error.attribute.name.already.exist');
        expect(attributeModel.isValid()).toBeFalsy();
        attr[0].holder = 'server';
        attr[0].inherited = true;
        attributeModel.holder = 'tenant';
        attributeModel.attr = attr;
        attributeModel.set('name', 'name1');
        expect(attributeModel.validate().name.code).toEqual('attributes.error.attribute.name.already.exist.at.higher.level');
        expect(attributeModel.isValid()).toBeFalsy();
    });
    it('should validate value', function () {
        var tooLongValue = new Array(2002).join('v');
        attributeModel.set('value', tooLongValue);
        expect(attributeModel.validate().value.code).toEqual('attributes.error.attribute.value.too.long');
        expect(attributeModel.isValid()).toBeFalsy();
    });
    it('should validate description', function () {
        var tooLongDescription = new Array(257).join('v');
        attributeModel.set('description', tooLongDescription);
        expect(attributeModel.validate().description.code).toEqual('attributes.error.attribute.description.too.long');
        expect(attributeModel.isValid()).toBeFalsy();
    });
    it('should set id using setId function', function () {
        expect(attributeModel.get('id')).toEqual(attributeModel.get('name'));
        attributeModel.set('name', 'anotherName');
        attributeModel.setId();
        expect(attributeModel.get('id')).toEqual('anotherName');
        expect(attributeModel.get('id')).toEqual(attributeModel.get('name'));
    });
    it('should toggle same name validation', function () {
        expect(attributeModel.validateSameNames).toBeFalsy();
        attributeModel.toggleSameNamesValidation();
        expect(attributeModel.validateSameNames).toBeTruthy();
    });
    it('should reset field to default', function () {
        attributeModel.set('name', 'anotherName');
        expect(attributeModel.get('name')).toEqual('anotherName');
        attributeModel.resetField('name');
        expect(attributeModel.get('name')).toEqual(undefined);
    });
    it('should reset to state', function () {
        attributeModel.set({
            name: 'anotherName',
            value: 'anotherValue',
            description: 'anotherDescription'
        });
        attributeModel.setState('confirmedState');
        expect(attributeModel.getState('confirmedState')).toEqual(attributeModel.attributes);
        attributeModel.set('name', 'name1');
        expect(attributeModel.getState('confirmedState')).not.toEqual(attributeModel.attributes);
        attributeModel.reset('name', 'confirmedState');
        expect(attributeModel.getState('confirmedState')).toEqual(attributeModel.attributes);
    });
    it('should check if model is renamed', function () {
        expect(attributeModel.isRenamed()).toBeFalsy();
        attributeModel.set('name', 'anotherName');
        expect(attributeModel.isRenamed()).toBeTruthy();
    });
    it('should check if originally inherited', function () {
        attributeModel.set('inherited', true);
        expect(attributeModel.isOriginallyInherited()).toBeFalsy();
    });
    it('should set state', function () {
        expect(attributeModel.getState()).toEqual(attributeModel.attributes);
        attributeModel.set({
            name: 'anotherName',
            value: 'anotherValue',
            description: 'anotherDescription'
        });
        expect(attributeModel.getState()).not.toEqual(attributeModel.attributes);
        attributeModel.setState('confirmedState');
        expect(attributeModel.getState()).not.toEqual(attributeModel.attributes);
        expect(attributeModel.getState('confirmedState')).toEqual(attributeModel.attributes);
        attributeModel.setState();
        expect(attributeModel.getState()).toEqual(attributeModel.attributes);
        attributeModel.setState('confirmedState', _.extend({}, attributeModel.attributes, {name: 'oneAnotherName'}));
        expect(attributeModel.getState('confirmedState')).not.toEqual(attributeModel.attributes);
        expect(attributeModel.getState('confirmedState').name).toEqual('oneAnotherName');
    });
    it('should trim values', function () {
        var attrs = {
            name: '   anotherName   ',
            value: '  anotherValue  ',
            description: '  anotherDescription   '
        };
        attributeModel.set(attrs);
        expect(attributeModel.get('name')).toEqual(attrs.name);
        expect(attributeModel.get('value')).toEqual(attrs.value);
        expect(attributeModel.get('description')).toEqual(attrs.description);
        attributeModel.trimAttrs([
            'name',
            'value',
            'description'
        ]);
        expect(attributeModel.get('name')).toEqual('anotherName');
        expect(attributeModel.get('value')).toEqual('anotherValue');
        expect(attributeModel.get('description')).toEqual('anotherDescription');
    });
});