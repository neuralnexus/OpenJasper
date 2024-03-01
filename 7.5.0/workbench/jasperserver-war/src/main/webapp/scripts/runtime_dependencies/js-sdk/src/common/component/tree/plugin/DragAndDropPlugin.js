define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var TreePlugin = require('./TreePlugin');

require('jquery-ui/ui/widgets/draggable');

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
* @author: Zakhar Tomchenko
* @version: $Id$
*/
module.exports = TreePlugin.extend({
  initialize: function initialize(options) {
    this.onDragStart = options.onDragStart;
    this.onDrag = options.onDrag;
    this.onDragStop = options.onDragStop;
    TreePlugin.prototype.initialize.apply(this, arguments);
  },
  itemsRendered: function itemsRendered(model) {
    var self = this;
    this.$draggableElements = this.$("> .subcontainer > .j-view-port-chunk > ul > li > .draggable");
    this.$draggableElements.draggable({
      cursor: "move",
      appendTo: $("#display"),
      cursorAt: {
        top: 0,
        left: 0
      },
      helper: function helper(event) {
        return $('<div class="wrap button draggable dragging dragHelper"></div>');
      },
      start: function start(e, ui) {
        var $item = $(this),
            index = $item.attr("data-index");

        while (index === undefined) {
          $item = $item.parent();
          index = $item.attr("data-index");
        }

        index = parseInt(index, 10);

        var data = _.reject(model.get("items"), function (item) {
          return item.index !== index;
        })[0].value;

        ui.helper.text(data.label).data("data", data);
        self.onDragStart && self.onDragStart(data, e);
      },
      drag: function drag(e, ui) {
        self.onDrag && self.onDrag(ui.helper.data("data"), e);
      },
      stop: function stop(e, ui) {
        self.onDragStop && self.onDragStop(ui.helper.data("data"), e);
      }
    });
  },
  remove: function remove() {
    try {
      this.$draggableElements.draggable("destroy");
    } catch (e) {// destroyed already, skip
    }

    TreePlugin.prototype.remove.apply(this, arguments);
  }
});

});