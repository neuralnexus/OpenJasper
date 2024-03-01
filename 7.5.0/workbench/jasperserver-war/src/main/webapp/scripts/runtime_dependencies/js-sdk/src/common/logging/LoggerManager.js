define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require("underscore");

var Log = require("./Log");

var Level = require("./Level");

var ConsoleAppender = require("./appender/ConsoleAppender");

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
 * @author: Pavel Savushchyk
 * @version: $Id$
 */
var appenderConstructors = {
  console: ConsoleAppender
};

var LoggerManager = function LoggerManager(options) {
  this.initialize(options || {});
};

_.extend(LoggerManager.prototype, {
  defaults: function defaults() {
    return {
      enabled: false,
      level: "error",
      appenders: {},
      appenderInstances: {},
      loggers: {}
    };
  },
  initialize: function initialize(options) {
    this.attributes = _.defaults(options, this.defaults()); // initialize appenders

    var appenders = {};

    _.each(appenderConstructors, function (appender, name) {
      appenders[name] = new appender();
    });

    this.set("appenderInstances", appenders);
  },
  get: function get(attr) {
    return this.attributes[attr];
  },
  set: function set(attr, value) {
    this.attributes[attr] = value;
  },
  register: function register(options) {
    var settings = {
      id: "root"
    };

    if (typeof options === "string" && options !== "") {
      settings.id = options;
    } else if (options && options.hasOwnProperty("id")) {
      settings.id = options.id;
    }

    if (!this.get("loggers").hasOwnProperty(settings.id)) {
      var loggers = this.get("loggers");
      loggers[settings.id] = new Log(settings, _.bind(this._processLogItem, this));
      this.set("loggers", loggers);
    }

    return this.get("loggers")[settings.id];
  },
  disable: function disable() {
    this.set("enabled", false);
  },
  enable: function enable(level) {
    if (level) {
      this.set("level", Level.getLevel(level));
    }

    this.set("enabled", true);
  },
  setLevel: function setLevel(level) {
    this.set("level", level);
  },
  _processLogItem: function _processLogItem(logItem) {
    if (this.get("enabled") && logItem.level.isGreaterOrEqual(this.get("level"))) {
      this._appendLogItem(logItem);
    }
  },
  _appendLogItem: function _appendLogItem(logItem) {
    var appenders = this.get("appenders"),
        appenderInstances = this.get("appenderInstances");

    for (var i in appenders) {
      if (appenderInstances.hasOwnProperty(appenders[i])) {
        appenderInstances[appenders[i]].write(logItem);
      }
    }
  }
});

module.exports = LoggerManager;

});