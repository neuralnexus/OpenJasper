define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var JavaScriptExceptionBiComponentError = require('../error/JavaScriptExceptionBiComponentError');

var BiComponentError = require('../error/BiComponentError');

var SchemaValidationBiComponentError = require('../error/SchemaValidationBiComponentError');

var ContainerNotFoundBiComponentError = require('../error/ContainerNotFoundBiComponentError');

var AlreadyDestroyedBiComponentError = require('../error/AlreadyDestroyedBiComponentError');

var RequestBiComponentError = require('../error/RequestBiComponentError');

var NotYetRenderedBiComponentError = require('../error/NotYetRenderedBiComponentError');

var InputControlParameterNotFoundBiComponentError = require('../error/InputControlParameterNotFoundBiComponentError');

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
module.exports = {
  genericError: function genericError(errorCode, message, parameters) {
    return new BiComponentError(errorCode, message, parameters);
  },
  validationError: function validationError(_validationError) {
    return new SchemaValidationBiComponentError(_validationError);
  },
  javaScriptException: function javaScriptException(ex) {
    return new JavaScriptExceptionBiComponentError(ex);
  },
  requestError: function requestError(xhr, code) {
    return new RequestBiComponentError(xhr, code);
  },
  containerNotFoundError: function containerNotFoundError(container) {
    return new ContainerNotFoundBiComponentError(container);
  },
  alreadyDestroyedError: function alreadyDestroyedError() {
    return new AlreadyDestroyedBiComponentError();
  },
  notYetRenderedError: function notYetRenderedError() {
    return new NotYetRenderedBiComponentError();
  },
  inputControlParameterNotFound: function inputControlParameterNotFound(message) {
    return new InputControlParameterNotFoundBiComponentError(message);
  }
};

});