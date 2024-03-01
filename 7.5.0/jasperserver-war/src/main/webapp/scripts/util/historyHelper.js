define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var redirectToUrl = require('../util/redirectToUrlUtil');

var _utilsCommon = require("./utils.common");

var JSCookie = _utilsCommon.JSCookie;

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
  saveCurrent: function saveCurrent(token) {
    new JSCookie(token, encodeURIComponent(document.location.href));
  },
  saveReferrer: function saveReferrer(token) {
    if (document.referrer.indexOf('login.html') === -1) {
      new JSCookie(token, encodeURIComponent(document.referrer));
    }
  },
  restore: function restore(token, defaultUrl) {
    var currentUrl = encodeURIComponent(document.location.href);
    var tokenUrl = new JSCookie(token).value;

    if (tokenUrl && tokenUrl !== currentUrl) {
      var url = decodeURIComponent(tokenUrl);

      if (url) {
        redirectToUrl.redirect(url);
        return;
      }
    } // in worse scenario, we need to get to the starting point
    // in worse scenario, we need to get to the starting point


    defaultUrl = defaultUrl || '/';
    redirectToUrl.redirect(jrsConfigs.contextPath + defaultUrl);
  }
};

});