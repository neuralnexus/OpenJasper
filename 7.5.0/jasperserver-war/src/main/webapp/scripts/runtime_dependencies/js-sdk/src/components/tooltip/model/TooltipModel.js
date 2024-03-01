define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var tooltipPlacements = require('../enum/tooltipPlacements');

var tooltipTypesEnum = require('../enum/tooltipTypesEnum');

var BackboneValidation = require('../../../common/extension/backboneValidationExtension');

var _ = require('underscore');

var log = require("../../../common/logging/logger");

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
var TooltipPopupModel = Backbone.Model.extend({
  defaults: {
    visible: false,
    defaultType: tooltipTypesEnum.INFO,
    type: tooltipTypesEnum.INFO,
    offset: {
      top: 0,
      left: 0
    },
    content: {
      title: undefined,
      text: undefined
    },
    placement: tooltipPlacements.BOTTOM,
    position: {
      top: 0,
      left: 0
    }
  },
  validation: {
    visible: {
      type: 'boolean'
    },
    content: {
      type: 'object'
    },
    type: {
      type: 'string'
    },
    offset: {
      type: 'object'
    }
  },
  initialize: function initialize(options) {
    options = options || {};
    this.log = options.log || log;
    this.listenTo(this, 'invalid', function (model, message) {
      this.log.error(message);
    });
  }
});

_.extend(TooltipPopupModel.prototype, BackboneValidation.mixin);

module.exports = TooltipPopupModel;

});