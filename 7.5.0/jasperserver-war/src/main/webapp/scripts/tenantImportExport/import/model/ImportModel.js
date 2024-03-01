define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var importModelAttributesFactory = require('../factory/importModelAttributesFactory');

var BrokenDependencyStrategyEnum = require('../enum/brokenDependencyStrategyEnum');

var AjaxFormSubmitter = require("runtime_dependencies/js-sdk/src/common/transport/AjaxFormSubmitter");

var BackboneValidation = require("runtime_dependencies/js-sdk/src/common/extension/backboneValidationExtension");

var BaseModel = require("runtime_dependencies/js-sdk/src/common/model/BaseModel");

var secureKeyTypeEnum = require('../enum/secureKeyTypeEnum');

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
var ImportModel = BaseModel.extend({
  defaults: {
    'fileName': '',
    'update': true,
    'skipUserUpdate': false,
    'mergeOrganization': false,
    'skipThemes': true,
    'keyType': '',
    'secretKey': '',
    'secretUri': '',
    'invalidKeyError': '',
    'invalidSecureFileContentError': '',
    'brokenDependencies': BrokenDependencyStrategyEnum.FAIL
  },
  url: 'rest_v2/import',
  validation: {
    fileName: [{
      fn: function fn(fileName) {
        return !/\.zip$/.test(fileName);
      }
    }]
  },
  initialize: function initialize(attributes, options) {
    this.form = new AjaxFormSubmitter(options.form, this.url, 'post', 'multipart/form-data');
  },
  parse: function parse() {
    return this.attributes;
  },
  save: function save() {
    var self = this,
        result,
        parameters;

    if (this.isNew()) {
      result = new $.Deferred();
      this.form.submit().done(function (responce) {
        self.set('id', responce.id);

        if (_.isUndefined(responce.errorCode) && _.isUndefined(responce.error)) {
          result.resolve(responce);
        } else {
          result.reject(responce);
        }
      }).fail(function (responce) {
        self.trigger('error', responce);
        result.reject(responce);
      });
    } else {
      parameters = this._convertParameters();
      result = BaseModel.prototype.save.call(this, {
        parameters: parameters
      }, {
        url: this.url + '/' + this.id
      }).fail(_.bind(this.trigger, this, 'error'));
    }

    return result;
  },
  cancel: function cancel() {
    var url = this.url + '/' + this.id;
    this.destroy({
      url: url
    });
  },
  reset: function reset(type, options) {
    var defaults = _.extend({}, this.defaults, importModelAttributesFactory(type), options);

    this.clear().set(_.extend({}, defaults));
    this.id = undefined;
  },
  _convertParameters: function _convertParameters() {
    var parameters = [];
    this.get('skipUserUpdate') && parameters.push('skip-user-update');
    this.get('includeAccessEvents') && parameters.push('include-access-events');
    this.get('includeAuditEvents') && parameters.push('include-audit-events');
    this.get('includeMonitoringEvents') && parameters.push('include-monitoring-events');
    this.get('includeServerSettings') && parameters.push('include-server-setting');
    this.get('mergeOrganization') && parameters.push('merge-organization');
    this.get('skipThemes') && parameters.push('skip-themes');
    this.get('update') && parameters.push('update');
    var keyType = this.get('keyType');

    if (keyType) {
      parameters.push('keyType');

      if (keyType === secureKeyTypeEnum.VALUE) {
        parameters.push('secret-key');
      }

      if (keyType === secureKeyTypeEnum.FILE) {
        parameters.push('secretUri');
      }
    }

    return parameters;
  }
});

_.extend(ImportModel.prototype, BackboneValidation.mixin);

module.exports = ImportModel;

});