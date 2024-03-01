define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var logger = require("runtime_dependencies/js-sdk/src/common/logging/logger");

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
var log = logger.register('request');
var result;

function triggerEvent() {
  $document.trigger.apply($document, arguments);
}

function checkSessionExpiration(xhr) {
  //TODO: custom error handlers should be used instead
  if (xhr.status == 401 || xhr.getResponseHeader('LoginRequested')) {
    //trigger custom event to allow do something before page reloading
    $(window).trigger('sessionExpired');
    log.warn('Session timed-out. Redirecting to login page.'); //Force page reload
    //Force page reload

    window.location.reload();
  }
} // workaround for optimizer which usually runs in Node env without document defined


if (typeof document === 'undefined') {
  result = {};
} else {
  var $document = $(document);

  result = function result(ajaxParams) {
    _.partial(triggerEvent, 'request:before').apply(null, arguments);

    return $.ajax(ajaxParams).fail(checkSessionExpiration).fail(function (jqXHR, textStatus, errorThrown) {
      if (jqXHR.getResponseHeader('adhocException')) {
        log.error(jqXHR.getResponseHeader('adhocException'));
      } else if (jqXHR.status == 500 || jqXHR.getResponseHeader('JasperServerError') && !jqXHR.getResponseHeader('SuppressError')) {
        log.error(jqXHR.responseText);
      }
    }).fail(_.partial(triggerEvent, 'request:failure')).done(_.partial(triggerEvent, 'request:success'));
  };
} // TODO: observableRequest functionality is temporary here. This should be revised after Amber release.


module.exports = result;

});