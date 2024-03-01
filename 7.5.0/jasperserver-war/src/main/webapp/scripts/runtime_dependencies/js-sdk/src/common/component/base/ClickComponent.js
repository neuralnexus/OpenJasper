define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var AttachableComponent = require('./AttachableComponent');

var $ = require('jquery');

var _ = require('underscore');

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
module.exports = AttachableComponent.extend({
  constructor: function constructor(attachTo, padding, additionalSettings) {
    this.additionalSettings = additionalSettings || {};
    AttachableComponent.apply(this, arguments);

    _.bindAll(this, '_onElementClick', '_onDocumentMousedown');

    this.$attachTo.on('click', this._onElementClick);
    $('body').on('mousedown', this._onDocumentMousedown);
  },
  _onElementClick: function _onElementClick() {
    if (this.$attachTo.attr('disabled')) return;
    this.$el.is(":visible") && this.additionalSettings.toggleMode ? this.hide() : this.show();
  },
  _onDocumentMousedown: function _onDocumentMousedown(e) {
    if (!$.contains(this.$el[0], e.target) && !this.$el.is(e.target) && !$.contains(this.$attachTo[0], e.target) && !this.$attachTo.is(e.target)) {
      this.hide();
    }
  },
  remove: function remove() {
    this.$attachTo.off('click', this._onElementClick);
    $('body').off('mousedown', this._onDocumentMousedown);
  }
});

});