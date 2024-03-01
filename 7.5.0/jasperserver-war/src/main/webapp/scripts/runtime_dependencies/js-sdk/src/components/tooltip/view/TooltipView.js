define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

var tooltipTemplate = require("text!../template/tooltipTemplate.htm");

var tooltipTypesEnum = require('../enum/tooltipTypesEnum');

var placements = require('../enum/tooltipPlacements');

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
var capitalize = _.str.capitalize;
var TooltipPopupView = Backbone.View.extend({
  template: _.template(tooltipTemplate),
  el: function el() {
    return this.template(this.model.toJSON());
  },
  initialize: function initialize(options) {
    options = options || {};
    this.listenTo(this.model, 'change', this.render);
  },
  position: function position() {
    var position = this.model.get('position');
    this.$el.offset(position);
  },
  render: function render() {
    var $mTooltip = this.$el.find('.jr-mTooltip'),
        $jTooltipText = this.$el.find('.jr-jTooltipText'),
        $jTooltipLabel = this.$el.find('.jr-jTooltipLabel');

    if (this.model.hasChanged('content')) {
      var content = this.model.get('content');
      $jTooltipText.text(content.text);
      $jTooltipLabel.text(content.label);
    }

    if (this.model.hasChanged('visible')) {
      if (this.model.get('visible')) {
        this.$el.removeClass('jr-isInvisible');
      } else {
        this.$el.addClass('jr-isInvisible');
      }
    }

    if (this.model.hasChanged('type')) {
      var type = this.model.get('type');

      if (type !== TooltipPopupView.TYPES.INFO) {
        $mTooltip.addClass('jr-mTooltip' + capitalize(type));
      }
    }

    if (this.model.hasChanged('position')) {
      var position = this.model.get('position');
      this.$el.offset(position);
    }

    if (this.model.hasChanged('placement')) {
      var prevPlacement = capitalize(this.model.previousAttributes().placement),
          newPlacement = capitalize(this.model.get('placement'));
      this.$el.removeClass('jr-mTooltip' + prevPlacement);
      this.$el.addClass('jr-mTooltip' + newPlacement);
    }

    return this;
  }
}, {
  PLACEMENTS: placements,
  TYPES: tooltipTypesEnum
});
module.exports = TooltipPopupView;

});