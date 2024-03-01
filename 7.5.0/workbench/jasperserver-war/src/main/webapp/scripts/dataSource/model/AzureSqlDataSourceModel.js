define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var JdbcDataSourceModel = require('./JdbcDataSourceModel');

var BaseDataSourceModel = require('./BaseDataSourceModel');

var JdbcDriverCollection = require('../collection/JdbcDriverCollection');

var connectionTypes = require('../enum/connectionTypes');

var repositoryResourceTypes = require("runtime_dependencies/bi-repository/src/bi/repository/enum/repositoryResourceTypes");

var i18n = require("bundle!jasperserver_messages");

var awsSettings = require("settings!awsSettings");

var jasperserverConfig = require("bundle!jasperserver_config");

var settingsUtility = require('../util/settingsUtility');

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
var AzureSqlDataSourceModel = JdbcDataSourceModel.extend({
  otherDriverIsPresent: false,
  type: repositoryResourceTypes.AZURE_SQL_DATA_SOURCE,
  defaults: function () {
    var defaults = {};

    _.extend(defaults, JdbcDataSourceModel.prototype.defaults, {
      subscriptionId: '',
      keyStorePassword: '',
      keyStoreUri: '',
      serverName: '',
      dbName: '',
      selectedDriverClass: '',
      useMicrosoftDriver: false,
      microsoftDriverAvailable: false,
      connectionType: connectionTypes.AZURE_SQL
    });

    return defaults;
  }(),
  validation: function () {
    var validation = {};

    _.extend(validation, JdbcDataSourceModel.prototype.validation, {
      subscriptionId: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.azureSqlDataSource.subscriptionId']
      }],
      keyStorePassword: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.azureSqlDataSource.keyStorePassword']
      }],
      keyStoreUri: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.azureSqlDataSource.keyStoreUri']
      }],
      serverName: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.azureSqlDataSource.serverName']
      }],
      dbName: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.not.empty.reportDataSource.dbNameIsEmpty']
      }],
      username: [{
        required: true,
        msg: i18n['ReportDataSourceValidator.error.not.empty.reportDataSource.username']
      }]
    });

    return validation;
  }(),
  initialize: function initialize(attributes, options) {
    BaseDataSourceModel.prototype.initialize.apply(this, arguments);
    var deepDefaults = settingsUtility.deepDefaults(options, {
      awsSettings: awsSettings
    });

    if (!this.isNew()) {
      // use password substitution
      this.set('password', jasperserverConfig['input.password.substitution']);
      this.set('keyStorePassword', jasperserverConfig['input.password.substitution']);
    }

    this.initialization = $.Deferred();
    this.drivers = new JdbcDriverCollection([], this.options);
    var self = this;
    this.drivers.fetch({
      reset: true
    }).done(function () {
      if (self.isNew()) {
        // by default use sqlserver driver (tibco/progress driver)
        self.set('selectedDriverClass', self.drivers.getDriverByName('sqlserver').get('jdbcDriverClass'));
      } else {
        self.set('selectedDriverClass', self.get('driverClass'));
      }

      var sqlServerStandardDriver = self.drivers.getDriverByName('sqlserver_standard');

      if (sqlServerStandardDriver != null) {
        self.set('microsoftDriverAvailable', sqlServerStandardDriver.get('available'));
        self.set('useMicrosoftDriver', self.get('selectedDriverClass') === sqlServerStandardDriver.get('jdbcDriverClass'));
      }

      self.initialization.resolve();
    });
    this.on('change:dbName change:serverName change:connectionUrlTemplate change:useMicrosoftDriver', this.updateConnectionUrl);
    this.on('change:useMicrosoftDriver', this.updateDriverClass);
  },
  updateConnectionUrl: function updateConnectionUrl() {
    if (!this.get('connectionUrlTemplate')) {
      return;
    }

    var valuesMap = this.pick(['dbName', 'serverName', 'dbPort']);
    valuesMap['dbPort'] = 1433;
    var sqlServerStandardDriver = this.drivers.getDriverByName('sqlserver_standard');
    var template = this.get('connectionUrlTemplate');

    if (this.get('useMicrosoftDriver') && sqlServerStandardDriver != null) {
      valuesMap['serverName'] += '.database.windows.net';
      template = sqlServerStandardDriver.get('jdbcUrl');
    } // dbHost is a synonym for serverName
    // dbHost is a synonym for serverName


    valuesMap['dbHost'] = valuesMap['serverName'];
    var connectionUrl = this.replaceConnectionUrlTemplatePlaceholdersWithValues(template, valuesMap);
    this.set('connectionUrl', connectionUrl);
  },
  updateDriverClass: function updateDriverClass() {
    var sqlServerStandardDriver = this.drivers.getDriverByName('sqlserver_standard');
    var driverName = this.get('useMicrosoftDriver') && sqlServerStandardDriver != null ? 'sqlserver_standard' : 'sqlserver';
    var driverClassName = this.drivers.getDriverByName(driverName).get('jdbcDriverClass');
    this.set('selectedDriverClass', driverClassName);
    this.set('driverClass', driverClassName);
  },
  toJSON: function toJSON() {
    var data = JdbcDataSourceModel.prototype.toJSON.apply(this, arguments);

    if (this.options.isEditMode && data.keyStorePassword === jasperserverConfig['input.password.substitution']) {
      data.keyStorePassword = null;
    }

    return data;
  },
  getFullDbTreePath: function getFullDbTreePath() {
    return this.get('serverName') && this.get('dbName') ? '/' + this.get('serverName') + '/' + this.get('dbName') : null;
  }
});
module.exports = AzureSqlDataSourceModel;

});