define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Marionette = require('backbone.marionette');

var _ = require('underscore');

var attributesDesignerFactory = require('../attributes/factory/attributesDesignerFactory');

var AttributesViewer = require('../attributes/view/AttributesViewer');

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
var AttributesViewFacade = Marionette.Controller.extend({
  initialize: function initialize(options) {
    options = options || {};

    var designerViewOptions = _.extend({}, options, options.designer),
        viewerViewOptions = _.extend({}, options, options.viewer);

    this.designer = attributesDesignerFactory(designerViewOptions.type, designerViewOptions);
    this.viewer = new AttributesViewer(viewerViewOptions);
    this.listenTo(this.designer, 'change', this._triggerChangeEvent);
    this.setCurrentView();
  },
  render: function render(hideFilters) {
    this.getCurrentView().render(hideFilters);
    return this;
  },
  getCurrentView: function getCurrentView() {
    return this.currentView;
  },
  cancel: function cancel() {
    return this.currentView.revertChanges();
  },
  containsUnsavedItems: function containsUnsavedItems() {
    return this.currentView.containsUnsavedItems && this.currentView.containsUnsavedItems();
  },
  setCurrentView: function setCurrentView(currentView) {
    this.currentView = currentView || this.viewer;
  },
  toggleMode: function toggleMode(mode, hideFilters) {
    this.getCurrentView().hide();
    this.setCurrentView(mode ? this.designer : this.viewer);
    this.render(hideFilters).getCurrentView().show();
  },
  _triggerChangeEvent: function _triggerChangeEvent() {
    this.trigger('change', this.containsUnsavedItems());
  }
});
module.exports = AttributesViewFacade;

});