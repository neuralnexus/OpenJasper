define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var dialogTemplate = require("text!./template/dialogTemplate.htm");

var Event = require('../utils/Event');

var Overlay = require('../overlay/Overlay');

var logger = require("../../common/logging/logger");

require('jquery-ui/ui/widgets/draggable');

require('jquery-ui/ui/widgets/resizable');

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
* @author: Pavel Savushchik
* @version: $Id$
*/
var log = logger.register("Dialog");
var Dialog = Backbone.View.extend({
  defaults: {
    title: "",
    modal: true,
    zIndex: 4000,
    resetSizeOnOpen: true
  },
  template: _.template(dialogTemplate),
  events: {
    'click .jr-jDialogClose': '_onClose',
    'mousedown .jr-jDialogClose': '_onClose'
  },
  el: function el() {
    return this.template({
      options: this.props
    });
  },
  constructor: function constructor(options) {
    this.props = _.defaults(options || {}, this.defaults);
    this.log = this.props.log || log;
    this.$overlay = this.props.$overlay ? this.props.$overlay : new Overlay({
      zIndex: this.props.zIndex
    });
    Backbone.View.apply(this, arguments);
  },
  initialize: function initialize() {
    this.$window = $(window);
    this.$body = $("body");
    this.$body.append(this.$overlay.$el);
    this.$body.append(this.$el);
  },
  _onClose: function _onClose() {
    var event = new Event({
      name: "dialog:close"
    });
    this.trigger(event.name, event);

    if (!event.isDefaultPrevented()) {
      this.close();
    }
  },
  open: function open(coordinates) {
    this.$el.removeClass("jr-isHidden");
    this.$el.css({
      position: "absolute"
    });

    if (this.props.resetSizeOnOpen) {
      this.$el.css({
        height: "",
        width: ""
      });
      this.$el.find("textarea").css({
        height: "",
        width: ""
      });
    }

    this._position(coordinates);

    if (this.props.modal) {
      this.$overlay.show();
      this.$el.addClass('jr-mDialogModal');
    }

    return this;
  },
  setPosition: function setPosition(coordinates) {
    this.$el.css({
      top: parseInt(coordinates.top),
      left: parseInt(coordinates.left),
      zIndex: coordinates.zIndex || this.props.zIndex
    });
  },
  getPosition: function getPosition() {
    var top, left;
    top = parseInt(this.$el.css('top'));
    left = parseInt(this.$el.css('left'));

    if (_.isNaN(top)) {
      top = 0;
    }

    if (_.isNaN(left)) {
      left = 0;
    }

    return {
      top: top,
      left: left
    };
  },
  _position: function _position(coordinates) {
    var position,
        elHeight = this.$el.height(),
        elWidth = this.$el.width();
    var elemRect = {
      height: elHeight,
      width: elWidth
    };

    if (coordinates && typeof coordinates.top !== "undefined" && typeof coordinates.left !== "undefined") {
      position = Dialog.fitInProvidedCoordinates({
        coordinates: coordinates,
        outerRect: {
          height: this.$body.height() || this.$window.height(),
          width: this.$body.width() || this.$window.width()
        },
        elemRect: elemRect
      });
    } else {
      position = Dialog.calculateCenterPosition({
        outerRect: {
          width: this.$window.width(),
          height: this.$window.height()
        },
        innerRect: elemRect,
        scrollCorrection: {
          width: this.$window.scrollLeft(),
          height: this.$window.scrollTop()
        }
      });
    }

    this.setPosition(position);
    return this;
  },
  close: function close() {
    this.$el.addClass("jr-isHidden");

    if (this.props.modal) {
      this.$overlay.hide();
    }

    return this;
  },
  delegateEvents: function delegateEvents() {
    var self = this;
    Backbone.View.prototype.delegateEvents.apply(this, arguments);
    this.$el.draggable({
      handle: ".jr-jDialogDraggable",
      addClasses: false,
      containment: "document"
    });
    var $resizer = this.$(".jr-jDialogResizer");

    if ($resizer.length > 0) {
      this.$el.resizable({
        handles: {
          "se": this.$(".jr-jDialogResizer")
        },
        start: function start(event, ui) {
          self.trigger("dialog:resize:started", ui);
        },
        stop: function stop(event, ui) {
          self.trigger("dialog:resize:stopped", ui);
        },
        resize: function resize(event, ui) {
          self.trigger("dialog:resize", ui);
        }
      });
    }

    return this;
  },
  undelegateEvents: function undelegateEvents() {
    try {
      this.$el.draggable("destroy");
      this.$el.resizable("destroy");
    } catch (err) {// just swallow warning
    }

    return Backbone.View.prototype.undelegateEvents.apply(this, arguments);
  },
  remove: function remove() {
    this.stopListening();
    this.$overlay.remove();
    Backbone.View.prototype.remove.call(this);
    return this;
  }
}, {
  calculateCenterPosition: function calculateCenterPosition(options) {
    options = options || {};
    var outerRect = options.outerRect;
    var innerRect = options.innerRect;
    var scrollCorrection = options.scrollCorrection;

    if (!scrollCorrection) {
      scrollCorrection = {
        width: 0,
        height: 0
      };
    }

    if (outerRect && innerRect) {
      var left = Math.max(0, scrollCorrection.width + outerRect.width / 2 - innerRect.width / 2);
      var top = Math.max(0, scrollCorrection.height + outerRect.height / 2 - innerRect.height / 2);

      if (_.isNaN(left) || _.isNaN(top)) {
        throw new TypeError("Can't calculate position. Make sure that you pass dimension as integer values");
      }

      return {
        left: left,
        top: top
      };
    } else {
      throw new Error("Illegal arguments");
    }
  },
  fitInProvidedCoordinates: function fitInProvidedCoordinates(options) {
    options = options || {};
    var coordinates = options.coordinates;
    var outerRect = options.outerRect;
    var elemRect = options.elemRect;
    var topPoint = coordinates.topPoint || 0;
    var leftPoint = coordinates.leftPoint || 0;
    var top = coordinates.top - topPoint * elemRect.height;
    var left = coordinates.left - leftPoint * elemRect.width;
    var fitByHeight = outerRect.height - coordinates.top;
    var fitByWidth = outerRect.width - coordinates.left;

    if (fitByHeight < elemRect.height) {
      top = coordinates.top - elemRect.height;

      if (top < 0) {
        top = outerRect.height / 2 - elemRect.height / 2;
      }
    }

    if (fitByWidth < elemRect.width) {
      left = coordinates.left - elemRect.width;

      if (left < 0) {
        left = outerRect.width / 2 - elemRect.width / 2;
      }
    }

    return {
      top: top,
      left: left
    };
  }
});
Dialog.prototype = _.extend({
  get title() {
    return this.props.title;
  },

  set title(value) {
    if (!_.isString(value)) {
      throw new TypeError("'Title' should be string");
    }

    this.props.title = value;
    this.$el.find(".jr-jDialogTitle").text(this.props.title);
  },

  get modal() {
    return this.props.modal;
  },

  set modal(value) {
    if (!_.isBoolean(value)) {
      throw new TypeError("'Modal' should be boolean");
    }

    this.props.modal = value;
  }

}, Dialog.prototype);
module.exports = Dialog;

});