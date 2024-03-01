define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

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
function doPanelAction(action, panel) {
  var self = this;

  if (_.indexOf(this.panels, panel) < 0) {
    return;
  }

  if (action === 'toggle' || action === 'open') {
    if (!self.allowMultiplePanelsOpen) {
      _.each(this.panels, function (panelView) {
        panelView !== panel && panelView.close();
      });
    }
  }

  if (action === 'toggle') {
    panel.collapsed ? panel.open() : panel.close();
  } else {
    panel[action]();
  }

  this.fit();
}

function Accordion(options) {
  options || (options = {});

  if (!options.container || !$(options.container).length) {
    throw new Error('Accordion should have specified container');
  }

  this.container = options.container;
  this.panels = options.panels || [];
  this.allowMultiplePanelsOpen = options.allowMultiplePanelsOpen || false;
  $(this.container).css('overflow', 'hidden');

  _.each(this.panels, function (panel) {
    panel.$resizableEl && this.listenTo(panel, 'resizeStart', _.bind(this.calcMaxHeight, this, panel));
  }, this);
}

Accordion.prototype.toggle = function (panel) {
  doPanelAction.call(this, 'toggle', panel);
};

Accordion.prototype.expand = function (panel) {
  doPanelAction.call(this, 'open', panel);
};

Accordion.prototype.collapse = function (panel) {
  doPanelAction.call(this, 'close', panel);
};

Accordion.prototype.fit = function () {
  var collapsedPanels = [],
      nonCollapsedPanels = [];

  _.each(this.panels, function (panel) {
    panel.collapsed ? collapsedPanels.push(panel) : nonCollapsedPanels.push(panel);
  });

  var collapsedPanelsTotalHeight = 0,
      containerHeight = $(this.container).height(),
      freeHeightToShare = 0,
      fixedHeightPanelsNumber = 0;

  _.each(collapsedPanels, function (panel) {
    collapsedPanelsTotalHeight += panel.$el.outerHeight(true);
  });

  freeHeightToShare = containerHeight - collapsedPanelsTotalHeight;

  _.each(nonCollapsedPanels, function (panel) {
    if (panel.fixedHeight) {
      freeHeightToShare -= panel.$el.outerHeight(true);
      fixedHeightPanelsNumber++;
    }
  });

  _.each(nonCollapsedPanels, function (panel) {
    if (panel.fixedHeight) {
      panel.setHeight(panel.$el.outerHeight(true));
    } else {
      panel.setHeight(Math.floor(freeHeightToShare / (nonCollapsedPanels.length - fixedHeightPanelsNumber)));
    }
  });

  this.trigger('fit', this);
};

Accordion.prototype.calcMaxHeight = function (resizablePanel) {
  var containerHeight = $(this.container).height(),
      activePanel = this.panels[this.panels.indexOf(resizablePanel) + 1],
      outerHeight = 0;

  _.each(this.panels, function (panel) {
    if (!_.isEqual(resizablePanel, panel)) {
      outerHeight = panel.$el.find('>.header').outerHeight(true) || outerHeight;
      containerHeight -= outerHeight;

      if (_.isEqual(activePanel, panel)) {
        containerHeight -= panel.collapsed ? 0 : parseInt(panel.$el.find('>.subcontainer').css('minHeight'), 10);
      } else {
        containerHeight -= panel.$el.outerHeight(true) || 0;
      }
    }
  }, this);

  resizablePanel.$resizableEl.resizable('option', 'maxHeight', containerHeight);
};

_.extend(Accordion.prototype, Backbone.Events);

module.exports = Accordion;

});