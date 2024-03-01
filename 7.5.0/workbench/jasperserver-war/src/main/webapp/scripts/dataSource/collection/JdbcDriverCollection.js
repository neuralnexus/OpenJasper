define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

var JdbcDriverModel = require('../model/JdbcDriverModel');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

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
module.exports = Backbone.Collection.extend({
  model: JdbcDriverModel,
  url: jrsConfigs.contextPath + '/rest_v2/jdbcDrivers',
  initialize: function initialize(models, options) {
    this.options = options;
    this.driverUploadEnabled = false;
  },
  getDefaultDriver: function getDefaultDriver() {
    var defaultDriver = this.find(function (driver) {
      return !_.isUndefined(driver.get('isDefault')) && driver.get('isDefault') !== false;
    });
    return defaultDriver ? defaultDriver : this.first();
  },
  set: function set(models, options) {
    if (typeof options === 'undefined') {
      options = {};
    }

    _.extend(options, this.options);

    return Backbone.Collection.prototype.set.call(this, models, options);
  },
  getDriverByClass: function getDriverByClass(className) {
    var driver = this.findWhere({
      jdbcDriverClass: className
    });
    return driver ? driver : this.findWhere({
      jdbcDriverClass: JdbcDriverModel.OTHER_DRIVER
    });
  },
  getDriverByName: function getDriverByName(name) {
    return this.findWhere({
      name: name
    });
  },
  getAllPossibleCustomAttributes: function getAllPossibleCustomAttributes() {
    return _.keys(JdbcDriverModel.VALIDATION_PATTERNS);
  },
  fetch: function fetch(options) {
    _.defaults(options || (options = {}), {
      headers: {
        Accept: 'application/hal+json'
      }
    });

    return Backbone.Collection.prototype.fetch.call(this, options);
  },
  parse: function parse(response) {
    var result = Backbone.Collection.prototype.parse.apply(this, arguments);

    if (result._links && result._links.create && result._links.edit) {
      this.driverUploadEnabled = true;
    }

    return result.jdbcDrivers ? result.jdbcDrivers : result;
  },
  markDriverAsAvailable: function markDriverAsAvailable(driverId) {
    var model = this.models.find(function (model) {
      return model.id === driverId;
    });
    model.set('available', true);
  }
});

});