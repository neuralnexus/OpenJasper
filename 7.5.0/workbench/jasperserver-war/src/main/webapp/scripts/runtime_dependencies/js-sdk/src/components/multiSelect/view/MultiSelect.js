define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var SearcheableDataProvider = require('../../singleSelect/dataprovider/SearcheableDataProvider');

var NumberUtils = require('../../../common/util/parse/NumberUtils');

var doCalcOnVisibleNodeClone = require('../../scalableList/util/domAndCssUtil');

var browserDetection = require('../../../common/util/browserDetection');

var i18n = require("bundle!ScalableInputControlsBundle");

var xssUtil = require('../../../common/util/xssUtil');

var AvailableItemsList = require('../view/AvailableItemsList');

var SelectedItemsList = require('../view/SelectedItemsList');

var multiSelectTemplate = require("text!../templates/multiSelectTemplate.htm");

var SelectedItemsDataProvider = require('../dataprovider/SelectedItemsDataProvider');

var Sizer = require('../../sizer/Sizer');

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
var doCalcOnVisibleNodeCloneObj = doCalcOnVisibleNodeClone.doCalcOnVisibleNodeClone;
var numberUtils = new NumberUtils();
var SELECTION_CHANGE_TIMEOUT = 100;
var DEFAULT_VISIBLE_ITEMS_COUNT = 10;
var DEFAULT_MIN_ITEMS_COUNT = 3;
module.exports = Backbone.View.extend({
  className: 'jr-mMultiselect jr',
  events: function events() {
    return {
      'click  .jr-mMultiselect-toggle': 'toggleLists'
    };
  },
  initialize: function initialize(options) {
    this.template = _.template(multiSelectTemplate);
    this.i18n = {
      selected: i18n['sic.multiselect.toggle.selected'],
      available: i18n['sic.multiselect.toggle.available']
    };
    this.heightAutoAdjustment = _.isUndefined(options.heightAutoAdjustment) ? true : options.heightAutoAdjustment;
    this.availableItemsListModel = this._createAvailableItemsListModel(options);
    this.availableItemsList = this._createAvailableItemsList(options);
    this.selectedItemsDataProvider = this._createSelectedItemsListDataProvider(options);
    this.selectedItemsList = this._createSelectedItemsList(options);
    this.height = options.height;
    this.resizable = options && options.resizable;
    this.initListeners();

    if (typeof options.value !== 'undefined') {
      this.silent = true;
      this.availableItemsList.setValue(options.value);
    }

    if (options && options.resizable) {
      this.sizer = new Sizer({
        container: this.$el,
        stop: _.bind(function () {
          this.resize();
        }, this)
      });
    }

    this.render();
  },
  _createAvailableItemsListModel: function _createAvailableItemsListModel(options) {
    return new Backbone.Model();
  },
  _createAvailableItemsList: function _createAvailableItemsList(options) {
    return options.availableItemsList || new AvailableItemsList({
      model: this.availableItemsListModel,
      getData: new SearcheableDataProvider({
        getData: options.getData
      }).getData,
      bufferSize: options.bufferSize,
      loadFactor: options.loadFactor,
      chunksTemplate: options.chunksTemplate,
      scrollTimeout: options.scrollTimeout
    });
  },
  _createSelectedItemsListDataProvider: function _createSelectedItemsListDataProvider(options) {
    return options.selectedItemsDataProvider || new SelectedItemsDataProvider(options.selectedListOptions);
  },
  _createSelectedItemsList: function _createSelectedItemsList(options) {
    this.formatValue = options.formatValue;
    return new SelectedItemsList({
      getData: this.selectedItemsDataProvider.getData,
      bufferSize: options.bufferSize,
      loadFactor: options.loadFactor,
      chunksTemplate: options.chunksTemplate,
      scrollTimeout: options.scrollTimeout
    });
  },
  initListeners: function initListeners() {
    this.listenTo(this.availableItemsList, 'selection:change', this.selectionChange, this);
    this.listenTo(this.availableItemsList, 'listRenderError', this.listRenderError, this);
    this.listenTo(this.availableItemsListModel, 'change:totalValues', this._availableItemsTotalCountChange, this);
    this.listenTo(this.selectedItemsList, 'selection:remove', this.selectionRemoved, this);
  },
  render: function render() {
    this.sizer && this.sizer.$el.detach();
    this.$el.html($(this.template({
      i18n: i18n
    })));
    this.$toggleContainer = this.$el.find('.jr-mMultiselect-toggleContainer');
    this.availableItemsList.undelegateEvents();
    this.selectedItemsList.undelegateEvents();
    this.selectedItemsList.$el.insertAfter(this.$toggleContainer);
    this.availableItemsList.$el.insertAfter(this.$toggleContainer);
    this.availableItemsList.render();
    this.selectedItemsList.render();

    this._updateAvailableItemsCountLabel();

    this._updateSelectedItemsCountLabel();

    this.availableItemsList.delegateEvents();
    this.selectedItemsList.delegateEvents();

    this._tuneCSS();

    this._renderSizer();

    return this;
  },
  _renderSizer: function _renderSizer() {
    if (this.sizer) {
      this.heightAutoAdjustment && this._handleHeight();
      this.$el.append(this.sizer.$el);
    }
  },
  _tuneCSS: function _tuneCSS() {
    var self = this;

    if (!this._cssDepententSizesSet) {
      doCalcOnVisibleNodeCloneObj({
        el: this.$el,
        css: {
          'width': '500px'
        },
        classes: 'jr',
        alwaysClone: true,
        callback: function callback($el) {
          self.toggleContainerHeight = $el.find('.jr-mMultiselect-toggleContainer').outerHeight();

          self._tuneCSSInternal($el);

          $el.find('.jr-mScalablelist').css({
            height: '0'
          });
          self._emptyContainerHeight = $el.outerHeight();
          var height;

          if (self.height) {
            height = self.height;
          } else {
            height = self._calcHeightByItemsCount(DEFAULT_VISIBLE_ITEMS_COUNT);
          }

          self.$el.css('height', height);
        }
      });
      this._cssDepententSizesSet = true;
    }

    this._tuneCSSInternal(this.$el);
  },
  _tuneCSSInternal: function _tuneCSSInternal($el) {
    var toggleContainerHeight = this.toggleContainerHeight;
    browserDetection.isIPad() && this.$el.addClass('ipad');
    $el.find('.jr-mMultiselect-listContainer').css('padding-top', toggleContainerHeight);
  },
  listRenderError: function listRenderError(responseStatus, error) {
    this.trigger('listRenderError', responseStatus, error);
  },
  toggleLists: function toggleLists(evt) {
    evt.stopPropagation();

    if ($(evt.currentTarget).hasClass('jr-isActive')) {
      return;
    }

    this.$el.find('.jr-mMultiselect-toggle').toggleClass('jr-isActive jr-isInactive');
    this.$el.find('.jr-mMultiselect-listContainer').toggleClass('jr-isActive jr-isInactive');

    if (!browserDetection.isIPad()) {
      this.$el.find('.jr-mMultiselect-listContainer.jr-isActive input').focus();
    }
  },
  selectionChange: function selectionChange(selection) {
    clearTimeout(this.selectionChangeTimeout);
    this.selectionChangeTimeout = setTimeout(_.bind(this.selectionChangeInternal, this, selection), SELECTION_CHANGE_TIMEOUT);
  },
  selectionRemoved: function selectionRemoved(selection) {
    var currentRawSelection = this.availableItemsList.model.get('value'),
        seletedIndex,
        selectedLength = selection.length;

    for (seletedIndex = 0; seletedIndex < selectedLength; seletedIndex += 1) {
      delete currentRawSelection[selection[seletedIndex]];
    }

    this.availableItemsList.setValue(_.keys(currentRawSelection));
  },
  selectionChangeInternal: function selectionChangeInternal(selection) {
    var self = this,
        activeValue = this.selectedItemsList.listView.getActiveValue(),
        scrollTop = this.selectedItemsList.listView.$el.scrollTop();
    this.selectedItemsDataProvider.setData(selection);
    this.selectedItemsList.fetch(function () {
      self._updateSelectedItemsCountLabel();

      self.selectedItemsList.resize();
      self.selectedItemsList.listView.$el.scrollTop(scrollTop);

      if (activeValue && self.selectedItemsList.$el.hasClass('j-active')) {
        var total = self.selectedItemsList.listView.model.get('total');

        if (total && total > activeValue.index) {
          self.selectedItemsList.listView.activate(activeValue.index);
        } else if (total) {
          self.selectedItemsList.listView.activate(activeValue.index - 1);
        }
      }
    });

    if (!this.silent) {
      this.triggerSelectionChange();
    } else {
      delete this.silent;
    }
  },
  _setToggleLabel: function _setToggleLabel(target, count, text) {
    var labelCount = numberUtils.formatNumber(count),
        $labelEl = this.$el.find(target + ' .jr-mMultiselect-toggle-label'),
        labelText = text + ': ' + labelCount;
    $labelEl.text(labelText).attr('title', xssUtil.hardEscape(labelText));
  },
  _handleHeight: function _handleHeight() {
    var totalItems = this.availableItemsList.model.get('totalValues') || 0;

    if (!this._cssDepententSizesSet || !totalItems) {
      return;
    }

    var currentHeight = this.$el.height(),
        height = this.$el.height();

    var minHeight = this._calcHeightByItemsCount(DEFAULT_MIN_ITEMS_COUNT);

    var maxHeight = this._calcHeightByItemsCount(totalItems);

    if (totalItems <= DEFAULT_VISIBLE_ITEMS_COUNT) {
      minHeight = maxHeight = height = this._calcHeightByItemsCount(Math.max(DEFAULT_MIN_ITEMS_COUNT, totalItems));
    } else if (currentHeight > maxHeight) {
      height = this._calcHeightByItemsCount(totalItems);
    }

    this.$el.css('height', height + 'px');

    this._updateSizerVisibility({
      minHeight: minHeight,
      maxHeight: maxHeight
    });
  },
  _updateSizerVisibility: function _updateSizerVisibility(boundaries) {
    if (!this.sizer) {
      return;
    }

    this.sizer.updateMinMax(boundaries);

    if (boundaries.minHeight === boundaries.maxHeight) {
      this.sizer.hide();
    } else {
      this.sizer.show();
    }
  },
  _calcHeightByItemsCount: function _calcHeightByItemsCount(items) {
    var itemHeight = this.availableItemsList.listView.itemHeight;
    return items * itemHeight + this._emptyContainerHeight;
  },
  _availableItemsTotalCountChange: function _availableItemsTotalCountChange() {
    this._updateAvailableItemsCountLabel();

    this.heightAutoAdjustment && this._handleHeight();
  },
  _updateAvailableItemsCountLabel: function _updateAvailableItemsCountLabel() {
    var total = this.availableItemsList.model.get('totalValues') || 0;

    this._setToggleLabel('.jr-mMultiselect-toggleAvailable', total || 0, this.i18n.available);
  },
  _updateSelectedItemsCountLabel: function _updateSelectedItemsCountLabel() {
    var $noSelection = this.$el.find('.jr-mMultiselect-list-message'),
        count = this.selectedItemsList.listView.model.get('total') || 0;

    this._setToggleLabel('.jr-mMultiselect-toggleSelected', count, this.i18n.selected);

    if (count === 0) {
      $noSelection.removeClass('jr-isHidden');
    } else {
      $noSelection.addClass('jr-isHidden');
    }
  },
  triggerSelectionChange: function triggerSelectionChange() {
    this.trigger('selection:change', this.getValue());
  },
  renderData: function renderData() {
    this.availableItemsList.renderData();
    this.selectedItemsList.renderData();
    return this;
  },
  fetch: function fetch(callback, options) {
    this.availableItemsList.fetch(callback, options);
  },
  reset: function reset(options) {
    this.availableItemsList.reset(options);
  },
  resize: function resize() {
    _.debounce(_.bind(function () {
      this.availableItemsList.resize();
      this.selectedItemsList.resize();
    }, this), 500)();
  },
  setValue: function setValue(value, options) {
    if (options && options.silent) {
      this.silent = true;
    }

    delete options.silent;
    this.availableItemsList.setValue(value, options);
  },
  getValue: function getValue() {
    var value = this.availableItemsList.getValue();
    var result = [];
    var i = 0;

    for (var index in value) {
      if (value.hasOwnProperty(index) && value[index] !== undefined) {
        result[i++] = value[index];
      }
    }

    return result;
  },
  setDisabled: function setDisabled(disabled) {
    this.availableItemsList.setDisabled(disabled);
    this.selectedItemsList.setDisabled(disabled);
    return this;
  },
  getDisabled: function getDisabled() {
    return this.availableItemsList.getDisabled();
  },
  remove: function remove() {
    this.availableItemsList.remove();
    this.selectedItemsList.remove();
    this.sizer && this.sizer.remove();
    Backbone.View.prototype.remove.call(this);
  }
});

});