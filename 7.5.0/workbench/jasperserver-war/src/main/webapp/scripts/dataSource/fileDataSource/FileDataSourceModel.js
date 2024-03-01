define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var CustomDataSourceModel = require('../model/CustomDataSourceModel');

var fileSourceTypes = require('./enum/fileSourceTypes');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var i18n = require("bundle!jasperserver_messages");

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
var FileDataSourceModel = CustomDataSourceModel.extend({
  fileTypes: [],
  validation: function () {
    var validation = {};

    _.extend(validation, CustomDataSourceModel.prototype.validation, {
      repositoryFileName: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.fileSourceType === 'repository' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }, {
        fn: function fn(value, attr, computedState) {
          if (computedState.fileSourceType === 'repository' && !(_.isString(value) && value !== '' && value.lastIndexOf('.') !== -1 && _.indexOf(this.fileTypes, value.substr(value.lastIndexOf('.') + 1)) !== -1)) {
            return i18n['resource.file.extension'];
          }

          return null;
        }
      }],
      serverFileName: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.fileSourceType === 'serverFileSystem' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }],
      serverAddress: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.fileSourceType === 'ftpServer' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }],
      serverPath: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.fileSourceType === 'ftpServer' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }],
      ftpsPort: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.fileSourceType === 'ftpServer' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }]
    });

    return validation;
  }(),
  parse: function parse() {
    var model = CustomDataSourceModel.prototype.parse.apply(this, arguments);

    if (_.isString(model.fileName)) {
      if (model.fileName.indexOf('repo:') !== -1) {
        var path = model.fileName.split(':');
        model.fileSourceType = fileSourceTypes.REPOSITORY.name;
        model.repositoryFileName = path[1];
      } else {
        model.fileSourceType = fileSourceTypes.SERVER_FILE_SYSTEM.name;
        model.serverFileName = model.fileName;
      }

      delete model.fileName;
    }

    model.useFirstRowAsHeader = model.useFirstRowAsHeader === 'true';
    return model;
  },
  customFieldsToJSON: function customFieldsToJSON(data, customFields) {
    // converting file location section
    if (data.fileSourceType === fileSourceTypes.REPOSITORY.name) {
      data.fileName = 'repo:' + data.repositoryFileName + (jrsConfigs.organizationId ? '|' + jrsConfigs.organizationId : '');
    } else if (data.fileSourceType === fileSourceTypes.SERVER_FILE_SYSTEM.name) {
      data.fileName = data.serverFileName;
    }

    delete data.repositoryFileName;
    delete data.serverFileName;
    return CustomDataSourceModel.prototype.customFieldsToJSON.call(this, data, customFields);
  },
  resetValidation: function resetValidation() {
    this.validation = _.clone(FileDataSourceModel.prototype.validation);
  }
});
module.exports = FileDataSourceModel;

});