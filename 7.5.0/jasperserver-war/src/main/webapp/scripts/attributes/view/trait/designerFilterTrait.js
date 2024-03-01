define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

var AttributesFilterCollectionView = require('../../../attributes/attributesFilter/AttributesFilterCollectionView');

var AttributesFilterView = require('../../../attributes/attributesFilter/view/AttributesFilterView');

var attributesFilterCollectionViewTemplate = require("text!../../../attributes/attributesFilter/template/attributesFilterCollectionViewTemplate.htm");

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
  _initFilters: function _initFilters(options) {
    this.filters = new AttributesFilterCollectionView({
      collection: new Backbone.Collection(options.filters || []),
      childView: AttributesFilterView,
      childViewContainer: 'select',
      template: _.template(attributesFilterCollectionViewTemplate),
      model: new Backbone.Model({
        currentFilter: null
      }),
      targetCollection: this.collection
    });
  },
  _renderFilters: function _renderFilters(hideFilters) {
    var $filterEl = this.filters.render().el;
    return !hideFilters && this.$el.prepend($filterEl);
  },
  _resetFilters: function _resetFilters() {
    this.filters.reset();
  }
};

});