define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

var httpStatusCodes = require('../enum/httpStatusCodes');

var errors = require('../enum/errorCodes');

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
var BaseModel = Backbone.Model.extend({
  initialize: function initialize() {
    this.on('error', BaseModel.unifyServerErrors);
  },
  serialize: function serialize() {
    return _.clone(this.attributes);
  }
}, {
  unifyServerErrors: function unifyServerErrors(model, xhr) {
    var errorStatus = httpStatusCodes[xhr.status],
        errorObj = BaseModel.createServerError(xhr);
    model.trigger('error:' + errorStatus, model, errorObj, xhr);
    model.trigger('error:all', model, errorObj, xhr);
  },
  createServerError: function createServerError(xhr) {
    var error;

    try {
      error = JSON.parse(xhr.responseText);
    } catch (e) {
      error = {
        message: 'Can\'t parse server response',
        errorCode: errors.UNEXPECTED_ERROR,
        parameters: []
      };
    }

    return error;
  }
});
module.exports = BaseModel;

});