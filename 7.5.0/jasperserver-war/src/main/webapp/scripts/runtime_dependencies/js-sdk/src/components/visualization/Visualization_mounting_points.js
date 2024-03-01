define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var template = require("text!./template/visualizationTemplate.htm");

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
module.exports = Backbone.View.extend({
  template: _.template(template),
  initialize: function initialize(options) {
    if (options && options.props) {
      this.props = options.props;
    } else {
      this.props = {
        title: false
      };
    }

    watch(this.props, 'title', this._onTitleChange.bind(this));
    this.render();
  },
  render: function render() {
    this.$el = $(this.template({
      props: this.props
    }));
    this.$canvas = this.$('.jr-mVisualization-canvas');
    this.$switcher = this.$('.jr-mVisualization-switcher');
    this.$title = this.$('.jr-mVisualization-title');
    return this;
  },
  switchCanvasTo: function switchCanvasTo($view) {
    this.$canvas.children().detach();
    $view && this.$canvas.append($view);
  },
  _onTitleChange: function _onTitleChange(newTitle) {
    if (newTitle) {
      this.$title.find('.jr-mVisualization-title-text').text(newTitle);
      this.$title.removeClass('jr-isHidden');
      this.$switcher.removeClass('jr-isMinimized');
    } else {
      this.$title.addClass('jr-isHidden');
      this.$switcher.addClass('jr-isMinimized');
    }
  },
  remove: function remove() {
    this.switchCanvasTo(null);
    Backbone.View.prototype.remove.call(this);
  }
});

function watch(obj, prop, handler) {
  var oldval = obj[prop],
      newval = oldval,
      getter = function getter() {
    return newval;
  },
      setter = function setter(val) {
    oldval = newval;
    newval = handler.call(this, val, oldval, prop);
    return newval;
  };

  if (delete obj[prop]) {
    if (Object.defineProperty) Object.defineProperty(obj, prop, {
      get: getter,
      set: setter
    });else if (Object.prototype.__defineGetter__ && Object.prototype.__defineSetter__) {
      Object.prototype.__defineGetter__.call(obj, prop, getter);

      Object.prototype.__defineSetter__.call(obj, prop, setter);
    }
  }
}

function unwatch(obj, prop) {
  var val = obj[prop];
  delete obj[prop];
  obj[prop] = val;
}

});