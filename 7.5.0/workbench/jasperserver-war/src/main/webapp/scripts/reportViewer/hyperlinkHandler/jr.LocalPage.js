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
var LocalPage = function LocalPage(arrHyperlinks) {
  this.hyperlinks = arrHyperlinks;
  this.reportInstance = null;
  this.reportContainer = null;
};

LocalPage.prototype = {
  register: function register() {
    var it = this;
    $(it.hyperlinks[0].selector).on('click', function (evt) {
      var hlData = it._getHyperlinkData($(this).attr('data-id'));

      if (hlData) {
        it._handleHyperlinkClick(hlData);
      }
    }).css('cursor', 'pointer');
  },
  handleInteraction: function handleInteraction(evt) {
    if ('hyperlinkClicked' == evt.type) {
      var hlData = this._getHyperlinkData(evt.data.hyperlink.id);

      if (hlData) {
        this._handleHyperlinkClick(hlData);
      }
    }
  },
  // internal functions
  _getHyperlinkData: function _getHyperlinkData(id) {
    var hlData = null;
    $.each(this.hyperlinks, function (i, hl) {
      if (id === hl.id) {
        hlData = hl;
        return false; //break each
      }
    });
    return hlData;
  },
  _handleHyperlinkClick: function _handleHyperlinkClick(hyperlink) {
    var it = this,
        href = hyperlink.href,
        pageIndex = href && href.indexOf('pageIndex') != -1 ? /pageIndex=(\d*)/.exec(href)[1] : '',
        anchor;

    if (pageIndex.length > 0) {
      it.reportInstance.gotoPage(parseInt(pageIndex)).then(function () {
        anchor = href.indexOf('#') != -1 ? /\#(.*)/.exec(href)[0] : '';

        if (anchor.length > 0) {
          window.location.hash = anchor;
        }
      });
    }
  }
};
module.exports = LocalPage;

});