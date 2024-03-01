define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

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
 * @version: $Id$
 */
module.exports = {
  isMozilla: function isMozilla() {
    return navigator.appName === "Netscape";
  },
  isFirefox: function isFirefox() {
    return navigator.userAgent.toLowerCase().indexOf("firefox") !== -1;
  },
  isWebKitEngine: function isWebKitEngine() {
    return $.browser && $.browser.webkit;
  },
  isChrome: function isChrome() {
    return navigator.userAgent.toLowerCase().indexOf('chrome');
  },
  isIPad: function isIPad() {
    return navigator.platform == "iPad";
  },
  isIOS: function isIOS() {
    return navigator.userAgent.match(/(iPad|iPhone|iPod)/g);
  },
  isAndroid: function isAndroid() {
    return navigator.userAgent.toLowerCase().indexOf('android');
  },
  isIE: function isIE() {
    return navigator.appName === "Microsoft Internet Explorer" || navigator.userAgent.indexOf("Trident/") >= 0 || this.isEdge();
  },
  isIE6: function isIE6() {
    return this.isIEVersion(6);
  },
  isIE7: function isIE7() {
    return this.isIEVersion(7);
  },
  isIE8: function isIE8() {
    return this.isIEVersion(8);
  },
  isIE9: function isIE9() {
    return this.isIEVersion(9);
  },
  isIE10: function isIE10() {
    return this.isIEVersion(10);
  },
  isIE11: function isIE11() {
    return this.isIEVersion(11);
  },
  isEdge: function isEdge() {
    if (navigator.appName !== "Netscape") {
      return false;
    }

    return navigator.userAgent.indexOf("Edge/") >= 0;
  },
  isIEVersion7Upwards: function isIEVersion7Upwards() {
    return this.getIEVersion() >= 7;
  },
  isIEVersion: function isIEVersion(testVersion) {
    return this.getIEVersion() === testVersion;
  },
  getIEVersion: function getIEVersion() {
    var version = 0; // not IE

    if (this.getIEVersion.version >= 0) {
      return this.getIEVersion.version;
    }

    if (this.isIE()) {
      if (navigator.appName === "Netscape") {
        var ua = navigator.userAgent;
        var re = new RegExp("Trident/.*rv:([0-9]{1,}[\\.0-9]{0,})");

        if (re.exec(ua) != null) {
          version = parseFloat(RegExp.$1);
        }
      } else {
        var msVersion = navigator.appVersion.split("MSIE")[1];
        version = parseFloat(msVersion);
      }
    }

    this.getIEVersion.version = version;
    return version;
  }
};

});