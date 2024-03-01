define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var $ = require('jquery');

var Backbone = require('backbone');

var TreePlugin = require('./TreePlugin');

var template = require("text!../template/searchPluginTemplate.htm");

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
var SearchForm = Backbone.View.extend({
  template: _.template(template),
  el: function el() {
    return this.template();
  },
  events: {
    'click .button.search': 'clickHandler',
    'click .button.searchClear': 'clear',
    'keydown input[type=text]': 'keyHandler'
  },
  initialize: function initialize(options) {
    this.owner = options.owner;
    this.$searchInput = this.$el.find('input[type=text]');
  },
  search: function search(options) {
    var searchString = this.$searchInput.val();
    this.owner.refresh(_.extend({
      searchString: searchString
    }, options));

    if (searchString) {
      this.$el.find('.button.searchClear').addClass('up');
    } else {
      this.$el.find('.button.searchClear').removeClass('up');
    }
  },
  clearInput: function clearInput() {
    this.$searchInput.val('');
  },
  clear: function clear() {
    this.clearInput();
    this.clickHandler();
    this.trigger('clear', this);
  },
  clearSilently: function clearSilently() {
    this.clearInput();
    delete this.owner.context.searchString;
    this.owner.refresh(_.extend({
      searchString: ''
    }, this.owner.context));
    this.$el.find('.button.searchClear').removeClass('up');
  },
  clickHandler: function clickHandler() {
    delete this.owner.context.searchString;
    this.search(this.owner.context);
    this.trigger('search', this.owner.context);
  },
  keyHandler: function keyHandler(evt) {
    if (evt.which === 13) {
      this.clickHandler();
    }
  },
  setSearchString: function setSearchString(string) {
    this.$searchInput.val(string);
  }
});
module.exports = TreePlugin.extend({
  initialize: function initialize(options) {
    this.searchParameter = options && options.searchParameter || 'q';
    this.additionalParams = options && options.additionalParams;
  },
  dataLayerObtained: function dataLayerObtained(dataLayer) {
    var self = this;

    if (!dataLayer.__searchPluginExtended) {
      var getDataUri = dataLayer.getDataUri,
          searchParameter = this.searchParameter;

      dataLayer.getDataUri = function (options) {
        var uri = getDataUri.apply(this, arguments),
            params = {};

        if (options.searchString) {
          params[searchParameter] = options.searchString;
        }

        if (self.additionalParams) {
          _.extend(params, self.additionalParams, _.pick(options, _.keys(self.additionalParams)));
        }

        uri += (uri.indexOf('?') === -1 ? '?' : '&') + $.param(params, true);
        return uri;
      };

      dataLayer.__searchPluginExtended = true;
    }
  }
}, {
  treeInitialized: function treeInitialized(options) {
    options = options || {};
    var self = this;
    this.searchForm = new SearchForm({
      owner: this
    });

    if (options.dfdRenderTo) {
      options.dfdRenderTo.done(function ($el) {
        $el.prepend(self.searchForm.render().el);
      });
    } else {
      this.$el.prepend(this.searchForm.render().el);
    }
  },
  treeRemoved: function treeRemoved() {
    this.searchForm.remove();
  }
});

});