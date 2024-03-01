define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var JdbcDataSourceModel = require('./JdbcDataSourceModel');

var BaseDataSourceModel = require('./BaseDataSourceModel');

var JdbcDriverCollection = require('../collection/JdbcDriverCollection');

var dataSourcePatterns = require("settings!dataSourcePatterns");

var connectionTypes = require('../enum/connectionTypes');

var repositoryResourceTypes = require("runtime_dependencies/bi-repository/src/bi/repository/enum/repositoryResourceTypes");

var i18n = require("bundle!jasperserver_messages");

var awsSettings = require("settings!awsSettings");

var jasperserverConfig = require("bundle!jasperserver_config");

var settingsUtility = require('../util/settingsUtility');

var XRegExp = require('xregexp');

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

/**
 * @author: Dima Gorbenko
 * @version: $Id$
 */
var AwsDataSourceModel = JdbcDataSourceModel.extend({
  otherDriverIsPresent: false,
  type: repositoryResourceTypes.AWS_DATA_SOURCE,
  defaults: function () {
    var defaults = {};

    _.extend(defaults, JdbcDataSourceModel.prototype.defaults, {
      dbName: "",
      dbInstanceIdentifier: "",
      dbService: "",
      accessKey: "",
      secretKey: "",
      roleArn: "",
      region: "",
      credentialsType: "",
      connectionType: connectionTypes.AWS
    });

    return defaults;
  }(),
  validation: function () {
    var validation = {};

    _.extend(validation, {
      connectionUrl: [{
        required: true,
        msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.connectionUrl"]
      }, {
        xRegExpPattern: XRegExp(dataSourcePatterns.forbidWhitespacesPattern),
        msg: i18n["ReportDataSourceValidator.error.invalid.chars.reportDataSource.connectionUrl"]
      }],
      username: [{
        required: true,
        msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.username"]
      }],
      driverClass: [{
        required: true,
        msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.driverClass"]
      }, {
        xRegExpPattern: XRegExp(dataSourcePatterns.forbidWhitespacesPattern),
        msg: i18n["ReportDataSourceValidator.error.invalid.chars.reportDataSource.driverClass"]
      }, {
        fn: function fn(value, attr, computedState) {
          var contains = new RegExp(dataSourcePatterns.attributePlaceholderPattern).test(value);

          if (!contains) {
            var driver = this.drivers.getDriverByClass(value);

            if (!driver || !driver.get("available")) {
              return i18n['ReportDataSourceValidator.error.not.empty.reportDataSource.driverNotInstalled'];
            }
          }
        }
      }],
      dbName: [{
        required: true,
        msg: i18n["ReportDataSourceValidator.error.not.empty.reportDataSource.dbNameIsEmpty"]
      }],
      region: [{
        required: true
      }],
      accessKey: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.credentialsType === AwsDataSourceModel.credentialsType.AWS && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n["fillParameters.error.mandatoryField"];
          }
        }
      }],
      secretKey: [{
        fn: function fn(value, attr, computedState) {
          if (computedState.credentialsType === AwsDataSourceModel.credentialsType.AWS && (_.isNull(value) || _.isUndefined(value) || _.isString(value) && value === '')) {
            return i18n["fillParameters.error.mandatoryField"];
          }
        }
      }]
    });

    return validation;
  }(),
  initialize: function initialize(attributes, options) {
    BaseDataSourceModel.prototype.initialize.apply(this, arguments);
    var deepDefaults = settingsUtility.deepDefaults(options, {
      awsSettings: awsSettings
    });

    if (this.isNew()) {
      this.set("region", _.first(deepDefaults.awsSettings.awsRegions));
    } else {
      // use password substitution
      this.set("password", jasperserverConfig["input.password.substitution"]);
      this.set("secretKey", jasperserverConfig["input.password.substitution"]);
    }

    this.set("credentialsType", !deepDefaults.awsSettings.isEc2Instance || deepDefaults.awsSettings.suppressEc2CredentialsWarnings || this.get("accessKey") !== "" ? AwsDataSourceModel.credentialsType.AWS : AwsDataSourceModel.credentialsType.EC2);
    this.initialization = $.Deferred();
    this.drivers = new JdbcDriverCollection([], this.options);
    var self = this;
    this.drivers.fetch({
      reset: true
    }).done(function () {
      self.initialization.resolve();
    });
    this.on("change:dbName change:dbPort change:dbHost change:sName change:connectionUrlTemplate", this.updateConnectionUrl);
    this.on("change:credentialsType", this.changeCredentialsType);
  },
  updateConnectionUrl: function updateConnectionUrl() {
    if (!this.get("connectionUrlTemplate")) {
      return;
    }

    var valuesMap = this.pick(["dbName", "dbPort", "dbHost", "sName"]);

    if (!valuesMap.sName) {
      // some JDBC URL templates use sName instead of dbName.
      valuesMap.sName = valuesMap.dbName;
    }

    var connectionUrl = this.replaceConnectionUrlTemplatePlaceholdersWithValues(this.get("connectionUrlTemplate"), valuesMap);
    this.set("connectionUrl", connectionUrl);
  },
  changeCredentialsType: function changeCredentialsType() {
    var ec2CredentialsSelected = this.get("credentialsType") === AwsDataSourceModel.credentialsType.EC2;

    if (ec2CredentialsSelected) {
      this.set({
        accessKey: "",
        secretKey: "",
        roleArn: ""
      });
    }
  },
  toJSON: function toJSON() {
    var data = JdbcDataSourceModel.prototype.toJSON.apply(this, arguments);

    if (this.options.isEditMode && data.secretKey === jasperserverConfig["input.password.substitution"]) {
      data.secretKey = null;
    }

    return data;
  },
  getFullDbTreePath: function getFullDbTreePath() {
    return this.get("dbInstanceIdentifier") && this.get("dbService") ? "/" + this.get("dbService") + "/" + this.get("dbInstanceIdentifier") : null;
  }
}, {
  credentialsType: {
    EC2: "ec2",
    AWS: "aws"
  }
});
module.exports = AwsDataSourceModel;

});