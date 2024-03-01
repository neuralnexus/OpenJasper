define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var Backbone = require('backbone');

var Marionette = require('backbone.marionette');

var i18n = require("bundle!AttributesBundle");

var epoxyViewMixin = require("runtime_dependencies/js-sdk/src/common/view/mixin/epoxyViewMixin");

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
 * @author: Taras Bidyuk
 * @version: $Id$
 */
var DEFAULT_FILTER = "defaultFilter",
    BOOLEAN_VALUES = ["true", "false"];
var AttributesFilterCollectionView = Marionette.CompositeView.extend({
  className: "filtersContainer",
  events: {
    "change select": "_onCurrentFilterChange"
  },
  templateHelpers: function templateHelpers() {
    return {
      i18n: i18n
    };
  },
  constructor: function constructor(options) {
    this._initCurrentCriteria();

    Marionette.CompositeView.apply(this, arguments);
  },
  initialize: function initialize(options) {
    this.targetCollection = options && options.targetCollection || [];
    this.epoxifyView();
    this.initEvents();
  },
  initEvents: function initEvents() {
    this.listenTo(this.targetCollection, "add", this.addItemToFilterCollection);
    this.listenTo(this.targetCollection, "remove", this.removeItemFromFilterCollection);
    this.listenTo(this.targetCollection, "sync", this._initFilterCollection);
  },
  filter: function filter(criteria) {
    var currentCriteria = criteria || this.currentCriteria,
        filtered = currentCriteria.defaultFilter ? this.filterCollection.models : this.filterCollection.where(currentCriteria);
    this.targetCollection.reset(filtered);
  },
  reset: function reset() {
    if (!this.currentCriteria[DEFAULT_FILTER]) {
      this.$el.find("option[value='" + DEFAULT_FILTER + "::true']").prop("selected", true);

      this._initCurrentCriteria();

      this.filter();
    }
  },
  addItemToFilterCollection: function addItemToFilterCollection(model) {
    this.filterCollection.add(model);
  },
  removeItemFromFilterCollection: function removeItemFromFilterCollection(model) {
    this.filterCollection.remove(model);
  },
  render: function render() {
    Marionette.CompositeView.prototype.render.apply(this, arguments);
    this.applyEpoxyBindings();
    this.delegateEvents(this.events);
    return this;
  },
  remove: function remove() {
    this.removeEpoxyBindings();
    Marionette.CompositeView.prototype.remove.apply(this, arguments);
  },
  _initCurrentCriteria: function _initCurrentCriteria(prop, val) {
    prop = prop || DEFAULT_FILTER;
    val = !_.isUndefined(val) ? val : true;
    this.currentCriteria = {};
    this.currentCriteria[prop] = val;
  },
  _initFilterCollection: function _initFilterCollection() {
    this.filterCollection = new Backbone.Collection(this.targetCollection.models);
  },
  _onCurrentFilterChange: function _onCurrentFilterChange(e) {
    var splittedValue = $(e.target).val().split("::"),
        prop = splittedValue[0],
        val = splittedValue[1];

    if (_.indexOf(BOOLEAN_VALUES, val) !== -1) {
      this._initCurrentCriteria(prop, JSON.parse(val));

      this.filter();
    }
  }
});

_.extend(AttributesFilterCollectionView.prototype, epoxyViewMixin);

module.exports = AttributesFilterCollectionView;

});