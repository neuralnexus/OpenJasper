define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var Backbone = require('backbone');

var biComponentErrorFactory = require('../error/biComponentErrorFactory');

var biComponentUtil = require('./util/biComponentUtil');

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
var ComponentEngine = function ComponentEngine(schema, properties) {
  this.instanceData = {
    properties: _.extend({}, properties),
    data: null
  };
  this.schema = _.isString(schema) ? JSON.parse(schema) : schema;
};

function createRunAction(decorable, instanceData, runCallable) {
  return function (successCallback, errorCallback, completeCallback) {
    var dfd = new $.Deferred();
    successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
    errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
    completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);

    try {
      var validationResult = decorable.validate();

      if (validationResult) {
        dfd.reject(biComponentErrorFactory.validationError(validationResult));
      } else {
        runCallable(instanceData, dfd);
      }
    } catch (ex) {
      dfd.reject(biComponentErrorFactory.javaScriptException(ex));
    }

    return dfd;
  };
}

ComponentEngine.prototype.decorateComponent = function (decorable, runCallable) {
  biComponentUtil.createInstancePropertiesAndFields(decorable, this.instanceData, _.keys(this.schema.properties), ['properties'], ['data'], new Backbone.Model());

  _.extend(decorable, {
    validate: biComponentUtil.createValidateAction(this.instanceData, this.schema),
    run: createRunAction(decorable, this.instanceData, runCallable)
  });
};

module.exports = {
  newInstance: function newInstance(schemaString, properties) {
    return new ComponentEngine(schemaString, properties);
  }
};

});