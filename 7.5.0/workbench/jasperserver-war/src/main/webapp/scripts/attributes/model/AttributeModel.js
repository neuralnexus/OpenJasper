define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var BackboneValidation = require("runtime_dependencies/js-sdk/src/common/extension/backboneValidationExtension");

var i18n = require("bundle!AttributeBundle");

var i18nMessageUtil = require("runtime_dependencies/js-sdk/src/common/util/i18nMessage");

var Epoxy = require('backbone.epoxy');

var permissionMasksEnum = require('../../attributes/enum/permissionMasksEnum');

var validationRulesEnum = require('../../attributes/enum/validationRulesEnum');

var baseProfileAttributeValidation = require('./validation/baseProfileAttributeValidation');

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
var i18nMessage = i18nMessageUtil.extend({
  bundle: i18n
});
var MAX_LENGTH = validationRulesEnum.MAX_ATTRIBUTE_NAME_LENGTH;
var MAX_VALUE_LENGTH = validationRulesEnum.MAX_ATTRIBUTE_VALUE_LENGTH;
var AttributeModel = Epoxy.Model.extend({
  defaults: {
    id: undefined,
    name: undefined,
    value: '',
    description: '',
    inherited: false,
    permissionMask: 1,
    secure: false
  },
  constructor: function constructor() {
    this._initModelWithPermissionDefaults && this._initModelWithPermissionDefaults();
    Epoxy.Model.apply(this, arguments);
  },
  initialize: function initialize() {
    if (!this.get('id')) {
      // By default id is generated from the attribute name.
      this.setId();
    }

    this.validateSameNames = false;
    this.setState('originalState');
    this.setState('confirmedState');
  },

  /**
   * This method returns url, which points in attribute, represented by this Attribute instance
   * @instance
   * @memberof Attribute
   * @return url of representing resource
   */
  url: function url() {
    // duplicated on purpose - overrides some strange behaviour of FF
    var safeId = encodeURIComponent(this.id).replace(/'/g, '%27');
    return this.collection.url(this.isNew() ? '' : safeId);
  },
  validation: {
    name: baseProfileAttributeValidation.concat([{
      fn: function fn() {
        if (this.attr) {
          var msg,
              existOnCurrentLevel,
              attribute,
              attributesLength = this.attr.length;

          for (var i = 0; i < attributesLength; i++) {
            attribute = this.attr[i];

            _.defaults(attribute, this.defaults);

            existOnCurrentLevel = this.holder === attribute.holder && this.get('inherited') === attribute.inherited;

            if (existOnCurrentLevel) {
              // attribute already exist on this level
              msg = 'attributes.error.attribute.name.already.exist';
              break;
            } else if (attribute.inherited) {
              // attribute can not be overridden
              if (attribute.permissionMask === permissionMasksEnum.READ_ONLY) {
                msg = 'attributes.error.attribute.name.already.exist.at.higher.level';
                break;
              }
            }
          }

          this.attr = null;
          this.holder = null;
          return msg && new i18nMessage(msg);
        }
      }
    }, {
      fn: function fn() {
        if (this.validateIfSecure) {
          return new i18nMessage('attributes.error.attribute.secure.renaming.not.allowed');
        }
      }
    }]),
    value: [{
      maxLength: MAX_VALUE_LENGTH,
      msg: new i18nMessage('attributes.error.attribute.value.too.long', MAX_VALUE_LENGTH)
    }, {
      fn: function fn() {
        if (this.validateIfSecure) {
          this.validateIfSecure = false; // return space just to highlight value field without any message
          // return space just to highlight value field without any message

          return ' ';
        }
      }
    }],
    description: [{
      maxLength: MAX_LENGTH,
      msg: new i18nMessage('attributes.error.attribute.description.too.long', MAX_LENGTH)
    }]
  },
  setId: function setId() {
    var name = this.get('name'),
        id = this.get('id');
    name !== id && this.set('id', name);
  },
  toggleSameNamesValidation: function toggleSameNamesValidation() {
    this.validateSameNames = !this.validateSameNames;
  },
  resetField: function resetField(prop) {
    this.set(prop, this.defaults[prop]);
  },
  reset: function reset(property, state) {
    var attribute = {};
    state = this.getState(state);
    attribute[property] = state[property];
    this.set(property ? attribute : state);
    return this;
  },
  isRenamed: function isRenamed() {
    return this.get('name') !== this.get('id');
  },
  isOriginallyInherited: function isOriginallyInherited() {
    return this.originalState.inherited;
  },
  isOverridden: function isOverridden() {
    return !this.compareAttribute('inherited');
  },
  compareAttribute: function compareAttribute(attr) {
    return this.originalState[attr] === this.confirmedState[attr];
  },
  setState: function setState(state, attributes) {
    attributes = attributes || this.attributes;
    this[state || 'originalState'] = _.clone(attributes);
  },
  getState: function getState(state) {
    return this[state || 'originalState'];
  },
  trimAttrs: function trimAttrs(attrs, options) {
    _.each(attrs, function (attr) {
      var attrValue = this.get(attr);
      this.set(attr, $.trim(attrValue), options);
    }, this);
  },
  toJSON: function toJSON(options) {
    options = options || {};
    var attributes = Epoxy.Model.prototype.toJSON.apply(this, arguments);
    return options.omitValue && this.validateSecureValue() && _.omit(attributes, 'value') || attributes;
  },
  confirmState: function confirmState(confirm) {
    this.stateConfirmed = confirm || true;
  },
  isStateConfirmed: function isStateConfirmed() {
    return this.stateConfirmed;
  },
  validateSecureValue: function validateSecureValue() {
    var isEmptyValue = _.isEmpty(this.get('value')),
        isSecure = this.get('secure'),
        isNew = this.isNew() || this.isOverridden();

    return !isNew && isSecure && isEmptyValue;
  }
});

_.extend(AttributeModel.prototype, BackboneValidation.mixin);

module.exports = AttributeModel;

});