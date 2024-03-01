define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var BaseDataSourceModel = require('./BaseDataSourceModel');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var requestSettings = require("requestSettings");

var connectionTypes = require('../enum/connectionTypes');

var repositoryResourceTypes = require("runtime_dependencies/bi-repository/src/bi/repository/enum/repositoryResourceTypes");

var dialogs = require('../../components/components.dialogs');

var i18n = require("bundle!all");

var jasperserverConfig = require("bundle!jasperserver_config");

var dialogErrorMessageTemplate = require("text!runtime_dependencies/js-sdk/src/common/templates/dialogErrorPopupTemplate.htm");

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
var CustomDataSourceModel = BaseDataSourceModel.extend({
  type: repositoryResourceTypes.CUSTOM_DATA_SOURCE,
  constructor: function constructor(attributes, options) {
    this.defaults = _.extend({}, this.defaults, {
      dataSourceName: options.dataSourceType,
      connectionType: connectionTypes.CUSTOM
    });
    BaseDataSourceModel.prototype.constructor.apply(this, arguments);
  },
  initialize: function initialize(attributes, options) {
    var result = BaseDataSourceModel.prototype.initialize.apply(this, arguments); // define attributes which specific for custom model (we will fetch their values from the server)
    // define attributes which specific for custom model (we will fetch their values from the server)

    this.customFields = [];
    this.testable = false;
    this.queryTypes = null;
    this.initialization = $.Deferred();
    this.getCustomFieldsDefinition();
    return result;
  },
  getCustomFieldsDefinition: function getCustomFieldsDefinition() {
    var headers = {},
        dfr;

    _.extend(headers, requestSettings, {
      'Accept': 'application/json'
    });

    dfr = $.ajax({
      type: 'GET',
      headers: headers,
      url: jrsConfigs.contextPath + '/rest_v2/customDataSources/' + this.get('dataSourceName')
    }).done(_.bind(this.getCustomFieldsDefinitionDone, this)).fail(_.bind(this.getCustomFieldsDefinitionFail, this));
    return dfr;
  },
  getCustomFieldsDefinitionDone: function getCustomFieldsDefinitionDone(response) {
    var self = this;

    if (response && response.propertyDefinitions && _.isArray(response.propertyDefinitions)) {
      this.resetValidation(); // reset validation to initial state
      // reset validation to initial state

      this.testable = !!response.testable;
      this.queryTypes = response.queryTypes ? response.queryTypes : null;

      _.each(response.propertyDefinitions, function (definition) {
        var validationRule = {};

        if (definition.properties) {
          definition.properties = _(definition.properties).reduce(function (memo, property) {
            memo[property.key] = property.value;
            return memo;
          }, {});
        }

        self.customFields.push(definition);
        self.defaults[definition.name] = definition.defaultValue;

        if (!self.options.isEditMode) {
          self.set(definition.name, definition.defaultValue);
        }

        if (definition.name === 'password' && self.options.isEditMode && !self.isNew()) {
          self.set('password', jasperserverConfig['input.password.substitution']);
        } // now, extend the validation object with required fields
        // now, extend the validation object with required fields


        if (definition.properties && definition.properties.mandatory) {
          validationRule[definition.name] = {
            required: true,
            msg: i18n[self.get('dataSourceName') + '.' + definition.name + '.required'] || i18n['required.field.specify.value']
          };

          _.extend(self.validation, validationRule);
        }
      });
    }

    if (!this.options.isEditMode) {
      this.set(this.parse(this.attributes), {
        silent: true
      });
    }

    this.initialization.resolve();
  },
  getCustomFieldsDefinitionFail: function getCustomFieldsDefinitionFail(xhr) {
    var response = false;

    try {
      response = JSON.parse(xhr.responseText);
    } catch (e) {}

    var errTempl = _.template(dialogErrorMessageTemplate, {
      message: 'Failed to load custom data source definition. ',
      errorCode: response && response[0] ? response[0].errorCode : null,
      errorMsg: response && response.message,
      respText: xhr.responseText
    });

    dialogs.errorPopup.show(errTempl);
  },
  parse: function parse(response) {
    var result = BaseDataSourceModel.prototype.parse.apply(this, arguments);
    result = _.extend(result, this.parseProperties(response.properties));
    delete response.properties;
    return result;
  },
  parseProperties: function parseProperties(properties) {
    var result = {};

    if (!_.isEmpty(properties)) {
      _.each(properties, function (element) {
        result[element.key] = 'password' === element.key ? jasperserverConfig['input.password.substitution'] : element.value;
      });
    }

    return result;
  },
  toJSON: function toJSON() {
    var result = BaseDataSourceModel.prototype.toJSON.apply(this, arguments);
    return this.customFieldsToJSON(result, this.customFields);
  },
  customFieldsToJSON: function customFieldsToJSON(data, customFields) {
    if (!_.isEmpty(customFields)) {
      data.properties = [];

      _.each(customFields, function (field) {
        var value = data[field.name],
            isPassword = 'password' === field.name;

        if (!_.isUndefined(value)) {
          if (!isPassword || isPassword && value !== jasperserverConfig['input.password.substitution']) {
            data.properties.push({
              key: field.name,
              value: value
            });
            delete data[field.name];
          }
        }
      });
    }

    return data;
  },
  resetValidation: function resetValidation() {
    this.validation = _.clone(CustomDataSourceModel.prototype.validation);
  }
});
module.exports = CustomDataSourceModel;

});