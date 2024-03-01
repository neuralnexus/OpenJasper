define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var jQuery = require('jquery');

var Backbone = require('backbone');

var mainTemplate = require("text!../template/authorityPickerTemplate.htm");

var optionTemplate = require("text!../template/authorityPickerOptionsTemplate.htm");

var _ = require('underscore');

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
function isBeetween(a, b, c) {
  return c <= Math.max(a, b) && c >= Math.min(a, b);
}

module.exports = Backbone.View.extend({
  events: {
    'click .button.search': 'search',
    'click .button.searchClear': 'clear',
    'click .button-select-all': '_selectAllHandler',
    'keydown input[type=text]': 'keyHandler',
    'mousedown li': 'selectionStarted',
    'mouseenter li': 'mouseEntered',
    'mouseleave li': 'mouseLeave',
    'mouseup ul': 'selectionFinished',
    'mouseenter ul': 'stopScroll',
    'mouseleave ul': 'startScroll'
  },
  cursor: -1,
  baseCursor: -1,
  selecting: false,
  scrollSpeed: 30,
  initialize: function initialize(options) {
    this.model.on('change', _.bind(this.renderList, this));
    this.model.on('error:server', _.bind(function () {
      this.trigger('error:server', arguments);
    }, this));
    this.upHandler = _.bind(function (evt) {
      this.stopScroll(evt);
      this.selecting = false;
    }, this);
    this.el = false;
    this.initOptions = options;
  },
  render: function render() {
    if (!this.mainTemplate) this.mainTemplate = _.template(mainTemplate);
    if (!this.optionsTemplate) this.optionsTemplate = _.template(optionTemplate);

    if (!this.el) {
      this.$el = jQuery('<div/>').html(this.mainTemplate(this.initOptions)).children();
      this.el = this.$el[0];
      this.subEl = this.$el.find('.authorityPicker ul');
      this.delegateEvents();
    }

    this.model.context || this.model.setContext();
    return this;
  },
  renderList: function renderList() {
    this.subEl.html(this.optionsTemplate(this.model.attributes)).find('li').each(function (index, element) {
      jQuery(element).attr('index', index);
    });
    this.cursor = -1;
    this.baseCursor = -1;
    this.scrollSpeed = this.subEl.find('li:eq(0)').height();
    this.subEl.scrollTop(0);
    this.trigger('change:selection', []);
    return this;
  },
  getSelected: function getSelected() {
    return _.reduce(this.subEl.find('.selected'), function (memo, el) {
      var selected = _.map(jQuery(el).find('span'), function (span) {
        return jQuery(span).html();
      });

      memo.push(selected.join('|'));
      return memo;
    }, []);
  },
  search: function search() {
    var searchString = this.$el.find('input[type=text]').val();
    this.model.setContext({
      searchString: searchString
    });

    if (searchString) {
      this.$el.find('.button.searchClear').addClass('up');
    } else {
      this.$el.find('.button.searchClear').removeClass('up');
    }
  },
  _selectAllHandler: function _selectAllHandler() {
    this.selectAll();
  },
  clear: function clear() {
    this.$el.find('input[type=text]').val('');
    this.search();
  },
  keyHandler: function keyHandler(evt) {
    if (evt.which === 13) {
      this.search();
    }
  },
  setDisabled: function setDisabled(value) {
    var $selectAll = this.$el.find('.button-select-all').children();

    if (value) {
      this.subEl.addClass('disabled');
      $selectAll.addClass('disabled');
      this.undelegateEvents();
    } else {
      this.subEl.removeClass('disabled');
      $selectAll.removeClass('disabled');
      this.delegateEvents();
    }

    this.$el.find('input[type=text]').attr('disabled', value);
  },
  highlightSet: function highlightSet(what) {
    this.subEl.find('li').each(function (index, element) {
      element = jQuery(element);

      var selected = _.map(element.find('span'), function (span) {
        return jQuery(span).html();
      });

      element.toggleClass('highlighted', _.contains(what, selected.join('|')));
    });
  },
  selectAll: function selectAll(silent) {
    this.subEl.find('li').each(function (index, element) {
      jQuery(element).addClass('selected');
    });
    silent || this.trigger('change:selection', this.getSelected());
  },
  selectNone: function selectNone(silent) {
    this.subEl.find('li').each(function (index, element) {
      jQuery(element).removeClass('selected');
    });
    silent || this.trigger('change:selection', this.getSelected());
  },
  selectInverse: function selectInverse(silent) {
    this.subEl.find('li').each(function (index, element) {
      jQuery(element).toggleClass('selected');
    });
    silent || this.trigger('change:selection', this.getSelected());
  },
  selectItem: function selectItem(n, silent) {
    this.subEl.find('li[index=' + n + ']').addClass('selected');
    silent || this.trigger('change:selection', this.getSelected());
  },
  unSelectItem: function unSelectItem(n, silent) {
    this.subEl.find('li[index=' + n + ']').removeClass('selected');
    silent || this.trigger('change:selection', this.getSelected());
  },
  selectRange: function selectRange(a, b, silent) {
    var begin = Math.min(a, b);
    var end = Math.max(a, b);
    this.subEl.find('li:lt(' + (end + 1) + ')').each(function (index, element) {
      if (index >= begin) {
        jQuery(element).addClass('selected');
      }
    });
    silent || this.trigger('change:selection', this.getSelected());
  },
  selectionStarted: function selectionStarted(evt) {
    this.selecting = true;

    if (!(evt.ctrlKey || evt.metaKey)) {
      this.selectNone(true);
    }

    var index = +jQuery(evt.target).parents('li').toggleClass('selected').attr('index');

    if (evt.shiftKey) {
      if (this.cursor != -1) {
        this.selectRange(this.cursor, index, true);
      }
    } else {
      this.cursor = index;
      this.baseCursor = this.cursor;
    }
  },
  mouseEntered: function mouseEntered(evt) {
    if (this.selecting) {
      this.cursor = +jQuery(evt.target).parents('li').addClass('selected').attr('index');
    }
  },
  mouseLeave: function mouseLeave(evt) {
    if (this.selecting) {
      var previous = jQuery(evt.target).parents('li');
      var next = jQuery(evt.relatedTarget).parents('li');

      if (isBeetween(this.cursor, this.baseCursor, +next.attr('index'))) {
        previous.removeClass('selected');
      }
    }
  },
  selectionFinished: function selectionFinished(evt) {
    this.selecting = false;
    this.trigger('change:selection', this.getSelected());
  },
  startScroll: function startScroll(evt) {
    if (this.selecting) {
      if (jQuery(evt.relatedTarget).hasClass('upper')) {
        this.direction = -1;
      } else if (jQuery(evt.relatedTarget).hasClass('lower')) {
        this.direction = 1;
      } else {
        this.selecting = false;
        return;
      }

      jQuery(document.body).on('mouseup', this.upHandler);
      this.scrollTimer = setInterval(_.bind(this.scrollList, this), 200);
    }
  },
  stopScroll: function stopScroll(evt) {
    if (this.selecting) {
      clearInterval(this.scrollTimer);
      this.direction = 0;
      jQuery(document.body).off('mouseup', this.upHandler);
    }
  },
  scrollList: function scrollList() {
    this.subEl.scrollTop(+this.subEl.scrollTop() + this.scrollSpeed * this.direction);

    if (isBeetween(this.cursor, this.baseCursor, this.cursor + this.direction)) {
      this.unSelectItem(this.cursor, true);
    } else {
      this.selectItem(this.cursor + this.direction, true);
    }

    this.cursor = this.cursor + this.direction;
  }
});

});