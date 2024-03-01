define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var ScalableListModel = require('../../scalableList/model/ScalableListModel');

var browserDetection = require('../../../common/util/browserDetection');

var doCalcOnVisibleNodeClone = require('../../scalableList/util/domAndCssUtil');

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
var doCalcOnVisibleNodeCloneObject = doCalcOnVisibleNodeClone.doCalcOnVisibleNodeClone;
var scalableListItemHeightCalculationTrait = {
  render: function render() {
    if (!this._itemHeightSet) {
      this._calcItemHeight();

      this._itemHeightSet = true;
    }

    return this;
  },
  _calcItemHeight: function _calcItemHeight() {
    var self = this;
    this._model = this.model;
    this._lazy = this.lazy;
    this._visibility = this.$el.css('visibility');
    this._display = this.$el.css('display');
    this.model = new ScalableListModel({
      getData: function getData() {
        var deferred = new $.Deferred();
        deferred.resolve({
          total: 1,
          data: [{
            value: 'test value',
            label: 'test label'
          }]
        });
        return deferred;
      }
    });
    this.$el.css({
      'visibility': 'hidden',
      'display': 'block'
    });
    this.model.once('change', this.onModelChange, this);
    this.renderData();
    doCalcOnVisibleNodeCloneObject({
      el: this.$el,
      css: {
        'width': '100px'
      },
      classes: ' jr ' + (browserDetection.isIPad() ? 'ipad' : ''),
      callback: function callback($el) {
        self.itemHeight = $el.find('li').outerHeight(true);
        self.itemsPerChunk = Math.floor(self.defaultChunkHeight / self.itemHeight);
        self.chunkHeight = self.itemsPerChunk * self.itemHeight;
      }
    });
    this.$el.empty().css({
      'visibility': this._visibility,
      'display': this._display
    });
    delete this._visibility;
    delete this._display;
    this.totalItems = undefined;
    this.$firstViewChunk = undefined;
    this.model = this._model;
    delete this._model;
    this.lazy = this._lazy;
    this._lazy && delete this._lazy;
  },
  _calcViewPortConstants: function _calcViewPortConstants() {
    if (!this.viewPortConstantsInitialized) {
      if (!this._itemHeightSet) {
        return;
      }

      this._calcViewPortHeight();

      this._renderViewChunks(true);

      this._renderItems();

      this.viewPortConstantsInitialized = true;
    }
  }
};
module.exports = scalableListItemHeightCalculationTrait;

});