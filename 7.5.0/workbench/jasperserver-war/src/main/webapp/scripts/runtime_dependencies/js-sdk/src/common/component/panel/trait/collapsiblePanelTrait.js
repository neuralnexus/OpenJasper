define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var abstractPanelTrait = require('./abstractPanelTrait');

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
function collapseControlPressed(evt, allowPropagation) {
  !allowPropagation && evt.stopPropagation();
  this.onCollapseControlPressed ? this.onCollapseControlPressed(evt) : this.toggleCollapsedState();
}

function stopListeningToCollapser() {
  this.$collapser && this.$collapser.off('mousedown');
  this.$el && this.$el.find(this.expandOnDblClickSelector).off('dblclick');
}

module.exports = _.extend({}, abstractPanelTrait, {
  onConstructor: function onConstructor(options) {
    this.collapserClass = options.collapserClass || 'buttonIconToggle';
    this.collapserSelector = options.collapserSelector || '.buttonIconToggle';
    this.collapsiblePanelClass = options.collapsiblePanelClass || 'collapsiblePanel';
    this.expandOnDblClickSelector = options.expandOnDblClickSelector || '> p:first';
    this.allowEventPropagation = !!options.allowMouseDownEventPropagation;
    this.onCollapseControlPressed = options.onCollapseControlPressed;
  },
  beforeSetElement: function beforeSetElement() {
    stopListeningToCollapser.call(this);
  },
  afterSetElement: function afterSetElement() {
    this.$el.addClass(this.collapsiblePanelClass);
    this.$collapser = this.$(this.collapserSelector);

    if (!this.$collapser.length) {
      this.$collapser = $('<button></button>').addClass(this.collapserClass);
      this.$('> .header').prepend(this.$collapser);
    }

    this.$collapser.on('mousedown', _.bind(collapseControlPressed, this, this.allowEventPropagation));
    this.$el.find(this.expandOnDblClickSelector).on('dblclick', _.bind(collapseControlPressed, this));
  },
  onRemove: function onRemove() {
    stopListeningToCollapser.call(this);
  }
});

});