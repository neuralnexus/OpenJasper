define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Backbone = require('backbone');

var _ = require('underscore');

var $ = require('jquery');

var browserDetection = require('../../util/browserDetection');

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
function setIframeSrc() {
  /*jshint -W107 */
  if ((this.url || '').toLowerCase().indexOf('javascript:') === -1) {
    /*jshint +W107 */
    this.$iframe.attr('src', this.url);
    this._iframeSrcSet = true;
    this.$el.addClass('loading');
    this._loadingTimeoutId && clearTimeout(this._loadingTimeoutId);
    this._loadingTimeoutId = setTimeout(_.bind(this.$el.removeClass, this.$el, 'loading'), this.timeout);
  }
}

var WebPageView = Backbone.View.extend({
  tagName: 'div',
  className: 'webPageView',
  constructor: function constructor(options) {
    options || (options = {});

    if (options.renderTo && (!$(options.renderTo)[0] || !$(options.renderTo)[0].tagName)) {
      throw new Error('WebPageView cannot be rendered to specified container');
    }

    this.renderTo = options.renderTo;
    this.url = options.url;
    this.scrolling = _.isUndefined(options.scrolling) ? WebPageView.SCROLLING : options.scrolling;
    this.timeout = _.isUndefined(options.timeout) ? WebPageView.TIMEOUT : options.timeout;
    Backbone.View.prototype.constructor.apply(this, arguments);
  },
  initialize: function initialize() {
    this.$iframe = $('<iframe></iframe>').addClass('externalUrlIframe');
    this.$el.html(this.$iframe);
    this.$el.addClass('invisible');
    $('body').append(this.$el);
    this.listenToReadyStateCompete();
    this.setScrolling(this.scrolling, {
      silent: true
    });

    if (this.url && _.isString(this.url)) {
      setIframeSrc.call(this);

      if (this.renderTo) {
        this.render(this.renderTo);
      }
    }
  },
  listenToReadyStateCompete: function listenToReadyStateCompete() {
    var doc = this.$iframe[0].contentDocument || this.$iframe[0].contentWindow,
        self = this;
    this.$iframe.on('load', function () {
      clearInterval(self._loadingTimerId);
      $(this).blur();
      $(this).parent().focus();
      self.trigger('load');
    });

    if (doc.document) {
      doc = doc.document;
    }

    this._loadingTimerId = setInterval(function () {
      if (doc.readyState == 'complete') {
        clearInterval(self._loadingTimerId);
        self.$iframe.blur();
        self.$iframe.parent().focus();
        self.$el.removeClass('loading');
        self.trigger('load');
      }
    }, 300);
  },
  render: function render(container) {
    if (!this.url || !_.isString(this.url)) {
      throw new Error('WebPageView URL is not specified');
    }

    if (!container || !$(container)[0] || !$(container)[0].tagName) {
      throw new Error('WebPageView cannot be rendered to specified container');
    }

    if (!this._iframeSrcSet) {
      setIframeSrc.call(this);
    }

    var $el = this.$el.detach();
    $el.removeClass('invisible');
    $(container).html($el);
    this._rendered = true;
    this.trigger('render', this, $(container));
    return this;
  },
  setUrl: function setUrl(url, noRefresh) {
    this.url = url;
    this.trigger('change:url', this, this.url);

    if (!noRefresh) {
      this.refresh();
    }
  },
  setTimeout: function setTimeout(timeout) {
    this.timeout = timeout;
    this.trigger('change:timeout', this, this.timeout);
  },
  setScrolling: function setScrolling(scrolling, options) {
    this.scrolling = scrolling;
    this.$iframe.attr('scrolling', this.scrolling ? 'yes' : 'no');

    if (browserDetection.isIPad()) {
      this.scrolling ? this.$el.addClass('touchScroll') : this.$el.removeClass('touchScroll');
    }

    if (!options || !options.silent) {
      this.trigger('change:scrolling', this, this.scrolling);
    }
  },
  refresh: function refresh() {
    if (!this._rendered) {
      throw new Error('WebPageView must be rendered to a specific container first');
    }

    if (!this.url || !_.isString(this.url)) {
      throw new Error('WebPageView URL is not specified');
    }

    setIframeSrc.call(this);
    this.trigger('refresh', this, this.url);
  },
  remove: function remove() {
    this._loadingTimeoutId && clearTimeout(this._loadingTimeoutId);
    this._loadingTimerId && clearTimeout(this._loadingTimerId);
    this.$iframe.off('load');
    this.trigger('remove', this);
    Backbone.View.prototype.remove.apply(this, arguments);
  }
}, {
  TIMEOUT: 20000,
  SCROLLING: true,
  open: function open(settings, callback) {
    var view, err;

    try {
      view = new WebPageView(_.isObject(settings) ? settings : {
        url: settings
      });
    } catch (ex) {
      err = ex;

      if (!callback || !_.isFunction(callback)) {
        throw ex;
      }
    }

    callback && _.isFunction(callback) && callback(err, view);
    return view;
  }
});
module.exports = WebPageView;

});