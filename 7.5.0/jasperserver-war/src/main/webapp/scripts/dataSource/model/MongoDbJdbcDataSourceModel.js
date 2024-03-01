define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var CustomDataSourceModel = require('./CustomDataSourceModel');

var mongoJdbcFileSourceTypes = require('../enum/mongoJdbcFileSourceTypes');

var adminWorkflows = require("restResource!hypermedia/workflows?parentName=admin");

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
var MongoDbJdbcDataSourceModel = CustomDataSourceModel.extend({
  fileTypes: ['pdf', 'config'],
  defaults: function () {
    var defaults = {};

    _.extend(defaults, CustomDataSourceModel.prototype.defaults, {
      dataSourceName: 'mongoDbJDBCDataSource',
      fileSourceType: 'repository',
      timeZone: ''
    });

    return defaults;
  }(),
  validation: function () {
    var validation = {};

    _.extend(validation, CustomDataSourceModel.prototype.validation, {
      serverAddress: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.not.empty.host']
      }],
      portNumber: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.not.empty.server.port']
      }],
      repositoryFileName: [{
        fn: function fn(value, attr, computedState) {
          if (!computedState.autoSchemaDefinition && computedState.fileSourceType === 'repository' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }, {
        fn: function fn(value, attr, computedState) {
          if (!computedState.autoSchemaDefinition && computedState.fileSourceType === 'repository' && !(_.isString(value) && value !== '' && value.indexOf('/') === 0)) {
            return i18n['resource.file.invalid.path'];
          }

          return null;
        }
      }],
      serverFileName: [{
        fn: function fn(value, attr, computedState) {
          if (!computedState.autoSchemaDefinition && computedState.fileSourceType === 'serverFileSystem' && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n['fillParameters.error.mandatoryField'];
          }

          return null;
        }
      }]
    });

    return validation;
  }(),
  isLocalFileSystemAccessAllowed: function isLocalFileSystemAccessAllowed() {
    return adminWorkflows && adminWorkflows._embedded && adminWorkflows._embedded.workflow && _.find(adminWorkflows._embedded.workflow, function (item) {
      return item.name === 'serverSettings';
    });
  },
  parse: function parse() {
    var model = CustomDataSourceModel.prototype.parse.apply(this, arguments);

    if (_.isString(model.fileName)) {
      if (model.fileName.indexOf('repo:') !== -1) {
        var path = model.fileName.split(':');
        model.fileSourceType = mongoJdbcFileSourceTypes.REPOSITORY.name;
        model.repositoryFileName = path[1];
      } else {
        model.fileSourceType = mongoJdbcFileSourceTypes.SERVER_FILE_SYSTEM.name;
        model.serverFileName = model.fileName;
      }

      delete model.fileName;
      model.autoSchemaDefinition = false;
    } else {
      model.autoSchemaDefinition = true;
    } // fix for timeZone: server side expects timezone to be passed in custom fields by name "timeZone"
    // while the section on UI renders it with name='timezone' which creates variable 'timeZone'
    // What we need is to rename field from 'timeZone' to 'timezone'
    // fix for timeZone: server side expects timezone to be passed in custom fields by name "timeZone"
    // while the section on UI renders it with name='timezone' which creates variable 'timeZone'
    // What we need is to rename field from 'timeZone' to 'timezone'


    model.timezone = model.timeZone;
    delete model.timeZone;
    return model;
  },
  toJSON: function toJSON() {
    var data = CustomDataSourceModel.prototype.toJSON.apply(this, arguments);
    data.fileName = data.serverFileName;
    return data;
  },
  customFieldsToJSON: function customFieldsToJSON(data, customFields) {
    // converting file location section
    if (data.fileSourceType === mongoJdbcFileSourceTypes.REPOSITORY.name) {
      data.fileName = 'repo:' + data.repositoryFileName;
    } else if (data.fileSourceType === mongoJdbcFileSourceTypes.SERVER_FILE_SYSTEM.name) {
      data.fileName = data.serverFileName;
    }

    delete data.repositoryFileName;
    delete data.serverFileName;

    if (data.autoSchemaDefinition) {
      // null or empty string in "fileName" is indication what schema must be auto-generated
      delete data.fileName;
      delete data.fileSourceType;
    }

    delete data.autoSchemaDefinition; // fix for timeZone: server side expects timezone to be passed in custom fields by name "timeZone"
    // while the section on UI renders it with name='timezone' which creates variable 'timeZone'
    // What we need is to rename field from 'timezone' to 'timeZone'
    // fix for timeZone: server side expects timezone to be passed in custom fields by name "timeZone"
    // while the section on UI renders it with name='timezone' which creates variable 'timeZone'
    // What we need is to rename field from 'timezone' to 'timeZone'

    data.timeZone = data.timezone;
    delete data.timezone;
    data = CustomDataSourceModel.prototype.customFieldsToJSON.call(this, data, customFields);
    return data;
  },
  resetValidation: function resetValidation() {
    this.validation = _.clone(MongoDbJdbcDataSourceModel.prototype.validation);
  }
});
module.exports = MongoDbJdbcDataSourceModel;

});