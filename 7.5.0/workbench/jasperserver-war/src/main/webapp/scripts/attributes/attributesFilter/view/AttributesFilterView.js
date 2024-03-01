define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var attributesFilterTemplate = require("text!../../../attributes/attributesFilter/template/attributesFilterViewTemplate.htm");

var Marionette = require('backbone.marionette');

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
var AttributesFilterView = Marionette.ItemView.extend({
  template: function template(rawModel) {
    return _.template(attributesFilterTemplate)({
      model: rawModel
    });
  },
  onRender: function onRender() {
    this.$el = this.$el.children();
    this.$el.unwrap();
    this.setElement(this.$el);
  }
});
module.exports = AttributesFilterView;

});