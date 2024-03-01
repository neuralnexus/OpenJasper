define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Menu = require('./Menu');

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
var OPACITY_VALUE_FOR_HEIGHT_CALCULATION = 0;
var EXTRA_OFFSET = 10;
module.exports = Menu.extend({
  constructor: function constructor(options, additionalSettings) {
    Menu.call(this, options, additionalSettings);

    _.bindAll(this, '_tryHide');

    this.topPadding = additionalSettings && additionalSettings.topPadding || 5;
    this.leftPadding = additionalSettings && additionalSettings.leftPadding || 5;
    this.extraOffset = options.extraOffset || EXTRA_OFFSET;
    this.hideOnMouseLeave = additionalSettings && additionalSettings.hideOnMouseLeave || false;

    _.bindAll(this, '_tryHide', '_checkBounds');

    this.hideOnMouseLeave && this.$el.on('mouseleave', this._tryHide);
  },
  _tryHide: function _tryHide() {
    this.hide();

    this._signOffTryHide();
  },
  _signOffTryHide: function _signOffTryHide() {
    $(document.body).off('click.contextMenu', this._tryHide);
    $(document.body).off('mousemove', this._checkBounds);
  },
  show: function show(position, container) {
    if (!position || !_.isNumber(position.top) || !_.isNumber(position.left)) {
      throw new Error('Required params (top, left) missing: ' + JSON.stringify(position));
    }

    $(document.body).on('click.contextMenu', this._tryHide);
    this.hideOnMouseLeave && $(document.body).on('mousemove', this._checkBounds);

    var top = position.top,
        left = position.left,
        topPadding = this.topPadding,
        leftPadding = this.leftPadding,
        body = $('body'),
        menuElHeight = this._calculateMenuHeightOnShow(),
        menuElWidth = this.$el.width(),
        containerHeight = container ? container.height() : body.height(),
        containerWidth = container ? container.width() : body.width(),
        containerOffset = container ? container.offset() : body.offset(),
        fitByHeight = containerHeight - position.top,
        fitByWidth = containerWidth - position.left;

    if (fitByHeight < menuElHeight) {
      top = position.top - menuElHeight - topPadding;
      top < containerOffset.top && (top += containerOffset.top - top + topPadding);
      top = top < 0 ? containerHeight / 2 - menuElHeight / 2 : top;
    }

    if (fitByWidth < menuElWidth) {
      left = position.left - menuElWidth - leftPadding;
      left < containerOffset.left && (left += containerOffset.left - left + leftPadding);
      left = left < 0 ? containerWidth / 2 - menuElWidth / 2 : left;
    }

    _.extend(this, {
      top: top,
      left: left
    });

    this.$el.css({
      top: this.top,
      left: this.left
    });
    return Menu.prototype.show.apply(this, arguments);
  },
  _checkBounds: function _checkBounds(event) {
    var elementRect = {
      top: this.$el[0].offsetTop - this.extraOffset,
      bottom: this.$el[0].offsetTop + this.$el[0].offsetHeight + this.extraOffset,
      left: this.$el[0].offsetLeft - this.extraOffset,
      right: this.$el[0].offsetLeft + this.$el[0].offsetWidth + this.extraOffset
    };

    if (this._isPointerOutOfBounds(elementRect, event)) {
      this._tryHide();
    }
  },
  _isPointerOutOfBounds: function _isPointerOutOfBounds(elementRect, event) {
    return event.clientX < elementRect.left || event.clientX > elementRect.right || event.clientY < elementRect.top || event.clientY > elementRect.bottom;
  },
  _calculateMenuHeightOnShow: function _calculateMenuHeightOnShow() {
    this.$el.css({
      opacity: OPACITY_VALUE_FOR_HEIGHT_CALCULATION
    });
    Menu.prototype.show.apply(this, arguments);
    var height = this.$el.height();
    this.hide();
    this.$el.css({
      opacity: ''
    });
    return height;
  },
  remove: function remove() {
    this._signOffTryHide();

    this.hideOnMouseLeave && this.$el.off('mouseleave', this._tryHide);
    Menu.prototype.remove.apply(this, arguments);
  }
});

});