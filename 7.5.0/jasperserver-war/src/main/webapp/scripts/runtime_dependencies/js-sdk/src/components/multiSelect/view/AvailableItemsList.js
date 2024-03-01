define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var browserDetection = require('../../../common/util/browserDetection');

var i18n = require("bundle!ScalableInputControlsBundle");

var KeyboardManager = require('../../singleSelect/manager/KeyboardManager');

var ListWithNavigation = require('../../scalableList/view/ListWithNavigation');

var doCalcOnVisibleNodeClone = require('../../scalableList/util/domAndCssUtil');

var listWithNavigationModelTrait = require('../../scalableList/model/listWithNavigationModelTrait');

var ListWithSelectionAsObjectHashModel = require('../../scalableList/model/ListWithSelectionAsObjectHashModel');

var scalableListItemHeightCalculationTrait = require('../mixin/scalableListItemHeightCalculationTrait');

var availableItemsTabTemplate = require("text!../templates/availableItemsTemplate.htm");

var availableItemsTabTemplate_IE10_11 = require("text!../templates/availableItemsTemplate_IE10_11.htm");

var listTemplate = require("text!../templates/listTemplate.htm");

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
 * @author Sergey Prilukin; modified by Ken Penn
 * @version: $Id: AvailableItemsList.js 812 2015-01-27 11:01:30Z psavushchik $
 */

/**
 * AvailableItems list -  Part of Tabbed MultiSelect
 */
var doCalcOnVisibleNodeCloneObject = doCalcOnVisibleNodeClone.doCalcOnVisibleNodeClone;
var BUTTON_TESTS = [{
  selector: ".jr-jSelectAll",
  strings: [i18n["sic.multiselect.selectAll"], i18n["sic.multiselect.all"], ""]
}, {
  selector: ".jr-jSelectNone",
  strings: [i18n["sic.multiselect.deselectAll"], i18n["sic.multiselect.none"], ""]
}, {
  selector: ".jr-jInvert",
  strings: [i18n["sic.multiselect.inverse"], ""]
}];
var AvailableItemsList = Backbone.View.extend({
  className: "jr-mMultiselect-listContainer jr-isActive jr",
  events: function events() {
    return {
      "keydown input.jr-mInput-search": this.keyboardManager.onKeydown,
      "focus input.jr-mInput-search": "onFocus",
      "blur input.jr-mInput-search": "onBlur",
      "click .jr-jSelectAll": "onSelectAll",
      "click .jr-jSelectNone": "onSelectNone",
      "click .jr-jInvert": "onInvertSelection",
      "click input.jr-mInput-search": "onClickOnInput",
      "touchend input.jr-mInput-search": "onClickOnInput",
      "mousedown": "onMousedown",
      "mouseup": "onMouseup"
    };
  },
  keydownHandlers: _.extend({
    "65": "onAKey"
  }, KeyboardManager.prototype.keydownHandlers),
  initialize: function initialize(options) {
    _.bindAll(this, "onGlobalMouseup", "onGlobalMousedown", "onResize", "onGlobalMousemove", "onMousedown", "onMouseup", "onEscKey", "onSelectAll", "onSelectNone", "onInvertSelection");

    this._debouncedOnResize = _.debounce(this.onResize, 500);

    if (!this.model) {
      this.model = new Backbone.Model();
    }

    this.model.set("criteria", "", {
      silent: true
    }); // THIS IS A HACK TO COMPENSATE FOR AN IE10 and IE11 BUGS:
    // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/274987/
    // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/101220/

    var template;

    if (browserDetection.isIE10() || browserDetection.isIE11()) {
      template = availableItemsTabTemplate_IE10_11;
    } else {
      template = availableItemsTabTemplate;
    }

    this.label = options.label;
    this.template = _.template(options.availableItemsTemplate || template);
    this.keyboardManager = new KeyboardManager({
      keydownHandlers: this.keydownHandlers,
      keydownTimeout: options.keydownTimeout,
      context: this,
      deferredKeydownHandler: this.processKeydown,
      immediateHandleCondition: this.immediateHandleCondition,
      immediateKeydownHandler: this.immediateKeydownHandler,
      stopPropagation: true
    });
    this.listViewModel = this._createListViewModel(options);
    this.setValue(options.value, {
      silent: true
    });
    this.listView = this._createListView(options);
    this.render();
    this.initListeners();
  },
  _createListViewModel: function _createListViewModel(options) {
    this.getData = options.getData;
    var Model = ListWithSelectionAsObjectHashModel.extend(listWithNavigationModelTrait);
    return options.listViewModel || new Model({
      getData: options.getData,
      bufferSize: options.bufferSize,
      loadFactor: options.loadFactor
    });
  },
  _createListView: function _createListView(options) {
    var list = options.listView || new ListWithNavigation({
      el: options.listElement || $(listTemplate),
      model: this.listViewModel,
      chunksTemplate: options.chunksTemplate,
      itemsTemplate: options.itemsTemplate,
      scrollTimeout: options.scrollTimeout,
      lazy: true,
      selectedClass: "jr-isSelected",
      selection: {
        allowed: true,
        multiple: true
      }
    });

    _.extend(list, scalableListItemHeightCalculationTrait);

    return list;
  },
  initListeners: function initListeners() {
    this.listenTo(this.listView, "active:changed", this.activeChange, this);
    this.listenTo(this.listView, "mousedown", this.onMousedown, this);
    this.listenTo(this.listView, "render:data", this.onRenderData, this);
    this.listenTo(this.listView, "listRenderError", this.listRenderError, this);
    this.listenTo(this.listViewModel, "change", this.onListViewModelChanged, this);
    this.listenTo(this.listViewModel, "selection:clear", this.selectionClear, this);
    this.listenTo(this.listViewModel, "selection:add", this.selectionAdd, this);
    this.listenTo(this.listViewModel, "selection:addRange", this.selectionAddRange, this);
    this.listenTo(this.listViewModel, "selection:remove", this.selectionRemove, this);
    this.listenTo(this.model, "change:totalValue", this.onChangeTotalValue, this);
    this.listenTo(this.model, "change:value", this.changeValue, this);
    this.listenTo(this.model, "change:disabled", this.changeDisabled, this);
    this.listenTo(this.model, "change:criteria", this.changeFilter, this);
    $("body").on("mousedown", this.onGlobalMousedown).on("mouseup", this.onGlobalMouseup).on("mousemove", this.onGlobalMousemove);
    $(window).on("resize", this._debouncedOnResize);
  },
  render: function render() {
    this.$el.empty();
    this.$el.append($(this.template(this.getModelForRendering())));
    this.listView.undelegateEvents();
    this.$el.find('.jr-mMultiselect-list').append(this.listView.el);
    this.listView.render();
    this.listView.fetch(_.bind(this.listView.resize, this.listView));
    this.listView.delegateEvents();

    this._tuneCSS();

    this.setBulkSelectionText();
    return this;
  },
  _tuneCSS: function _tuneCSS() {
    var self = this; // only need to do this once

    if (!this._paddingHeightsSet) {
      doCalcOnVisibleNodeCloneObject({
        el: this.$el,
        css: {
          "width": "500px"
        },
        classes: "jr " + (browserDetection.isIPad() ? "ipad" : ""),
        callback: function callback($el) {
          self.searchBarHeight = Math.round($el.find(".jr-mMultiselect-search").outerHeight());
          self.buttonsContainerHeight = Math.round($el.find(".jr-mMultiselect-buttonContainer").outerHeight());
        }
      });
      this._paddingHeightsSet = true;
    }

    this.$el.find(".jr-mMultiselect-list").css({
      "height": "100%",
      "padding-top": this.searchBarHeight,
      "padding-bottom": this.buttonsContainerHeight
    });
  },
  renderData: function renderData() {
    this.listView.renderData();
    return this;
  },

  /* Event Handlers */
  activeChange: function activeChange(active) {
    if (active) {
      if (this.activeChangedWithShift) {
        delete this.activeChangedWithShift;
        this.listViewModel.addRangeToSelection(active.value, active.index);
      }

      this.listView.scrollTo(active.index);
    }
  },
  selectionAdd: function selectionAdd(selection) {
    this.model.get("value")[selection.value] = true;
    this.model.trigger("change:value");
  },
  selectionAddRange: function selectionAddRange(range) {
    var selection = range.selection,
        value = this.model.get("value"),
        sx = 0,
        sLen = selection.length;

    for (sx; sx < sLen; sx += 1) {
      value[selection[sx]] = true;
    }

    this.model.trigger("change:value");
  },
  selectionRemove: function selectionRemove(selection) {
    delete this.model.get("value")[selection.value];
    this.model.trigger("change:value");
  },
  selectionClear: function selectionClear() {
    this.model.attributes.value = {};
  },
  onSelectAll: function onSelectAll() {
    if (!this.model.get("disabled")) {
      this.listView.once("selection:change", this.processSelectionThroughApi, this);
      this.clearFilter(_.bind(this.listView.selectAll, this.listView));
    }
  },
  onSelectNone: function onSelectNone() {
    if (!this.model.get("disabled")) {
      this.listView.once("selection:change", this.processSelectionThroughApi, this);
      this.clearFilter(_.bind(this.listView.selectNone, this.listView));
    }
  },
  onInvertSelection: function onInvertSelection() {
    if (!this.model.get("disabled")) {
      this.listView.once("selection:change", this.processSelectionThroughApi, this);
      this.clearFilter(_.bind(this.listView.invertSelection, this.listView));
    }
  },
  onRenderData: function onRenderData() {
    this.trigger("render:data");
  },
  listRenderError: function listRenderError(responseStatus, error) {
    this.trigger("listRenderError", responseStatus, error);
  },
  onListViewModelChanged: function onListViewModelChanged() {
    if (typeof this.model.get("totalValues") == "undefined") {
      var total = this.listViewModel.get("total");

      if (_.isNumber(total) && !_.isNaN(total)) {
        this.model.set("totalValues", total);
      }
    }
  },
  onChangeTotalValue: function onChangeTotalValue() {
    this.listView.resize();
  },
  setBulkSelectionText: function setBulkSelectionText() {
    if (!this.$el.is(":visible")) {
      //We can not measure bulk buttons width
      //so no text will be changed
      return;
    }

    var componentWidth = this.$el.outerWidth();

    if (componentWidth === this._componentWidth) {
      //no need to check text since size was not changed since last check
      return;
    } else {
      this._componentWidth = componentWidth;
    }

    var $bulkButtonsBar = this.$el.find(".jr-mMultiselect-buttonContainer"),
        bulkButtonsBarWidth = $bulkButtonsBar.outerWidth();
    doCalcOnVisibleNodeCloneObject({
      el: $bulkButtonsBar,
      css: {
        "left": 0 - bulkButtonsBarWidth * 2 + "px",
        "width": bulkButtonsBarWidth + "px"
      },
      classes: "jr " + (browserDetection.isIPad() ? "ipad" : ""),
      //add additional classes to parent container of cloned node
      alwaysClone: true,
      callback: function callback($clone) {
        _.each(BUTTON_TESTS, function (buttonTest) {
          var widthOk = false;

          _.each(buttonTest.strings, function (buttonString) {
            if (widthOk) {
              return;
            }

            var $button = $clone.find(buttonTest.selector),
                $buttonText = $button.find(".jr-mItemselector-button-label span").text(buttonString),
                btnRight = $button[0].getBoundingClientRect().right,
                txtRight = $buttonText[0].getBoundingClientRect().right;

            if (btnRight - txtRight >= 3 || buttonString === "") {
              widthOk = true;
              $bulkButtonsBar.find(buttonTest.selector + " .jr-mItemselector-button-label span").text(buttonString);
            }
          });
        });
      }
    });
  },
  changeValue: function changeValue() {
    this.trigger("selection:change", this.getValue());
  },
  changeDisabled: function changeDisabled() {
    var disabled = this.model.get("disabled");

    if (disabled) {
      this.$el.find("input[type='text']").attr("disabled", "disabled");
    } else {
      this.$el.find("input[type='text']").removeAttr("disabled");
    }

    this.listView.setDisabled(disabled);
  },
  onClickOnInput: function onClickOnInput() {
    this.onFocus();
  },
  onFocus: function onFocus() {
    //Workaround for input's HTML5 placeholder attribute
    var input = this.$el.find("input");

    if (input.val() == input.attr('placeholder')) {
      input.val('');
      input.removeClass('jr-mMultiselect-input-placeholder');
    }
  },
  onBlur: function onBlur() {
    //Workaround for input's HTML5 placeholder attribute
    var input = this.$el.find("input");

    if (input.val() === '' || input.val() === input.attr('placeholder')) {
      input.addClass('jr-mMultiselect-input-placeholder');
      input.val(input.attr('placeholder'));
    } // End of workaround


    if (this.preventBlur) {
      return false;
    }
  },
  onMousedown: function onMousedown() {
    if (!browserDetection.isIPad()) {
      this.preventBlur = true;
    }
  },
  onMouseup: function onMouseup() {
    if (this.preventBlur) {
      delete this.preventBlur;
      this.$el.find("input.jr-mInput-search").focus();
    }
  },
  onGlobalMousedown: function onGlobalMousedown(event) {
    if (this.preventBlur) {
      if (event.target !== this.el && this.$el.find(event.target).length === 0) {
        delete this.preventBlur;
        this.onBlur();
      }
    }
  },
  onGlobalMouseup: function onGlobalMouseup() {
    this.onMouseup();
  },
  onGlobalMousemove: function onGlobalMousemove(event) {
    if (this.preventBlur) {
      event.stopPropagation();
    }
  },
  onResize: function onResize() {
    this.resize();
  },

  /* Key handlers for KeyboardManager */
  onUpKey: function onUpKey(event) {
    var self = this;

    this._onUpDownNavigation(event, function () {
      self.listView.activatePrevious();
    }, function () {
      self.listView.activateLast();
    });
  },
  onDownKey: function onDownKey(event) {
    var self = this;

    this._onUpDownNavigation(event, function () {
      self.listView.activateNext();
    }, function () {
      self.listView.activateFirst();
    });
  },
  _onUpDownNavigation: function _onUpDownNavigation(event, activateItem, resetToInitialActiveItem) {
    var nextActive,
        prevActive = this.listView.getActiveValue();

    if (prevActive) {
      if (event.shiftKey) {
        this.activeChangedWithShift = true;
      }

      activateItem();
      nextActive = this.listView.getActiveValue();

      if (nextActive.index === prevActive.index) {
        resetToInitialActiveItem();
      }
    } else {
      resetToInitialActiveItem();
    }
  },
  onEnterKey: function onEnterKey(event) {
    event.preventDefault();
    var active = this.listView.getActiveValue();

    if (!active) {
      return;
    }

    if (event.shiftKey) {
      this.listViewModel.addRangeToSelection(active.value, active.index);
    } else if (event.ctrlKey || event.metaKey) {
      this.listViewModel.clearSelection();
      this.listViewModel.addValueToSelection(active.value, active.index);
    } else {
      this.listViewModel.toggleSelection(active.value, active.index);
    }
  },
  onEscKey: function onEscKey() {
    /* do nothing */
  },
  onHomeKey: function onHomeKey(event) {
    if (event.shiftKey) {
      this.activeChangedWithShift = true;
    }

    this.listView.activateFirst();
  },
  onEndKey: function onEndKey(event) {
    if (event.shiftKey) {
      this.activeChangedWithShift = true;
    }

    this.listView.activateLast();
  },
  onPageUpKey: function onPageUpKey(event) {
    if (event.shiftKey) {
      this.activeChangedWithShift = true;
    }

    this.listView.pageUp();
  },
  onPageDownKey: function onPageDownKey(event) {
    if (event.shiftKey) {
      this.activeChangedWithShift = true;
    }

    this.listView.pageDown();
  },
  onTabKey: function onTabKey() {
    /* do nothing */
  },
  onAKey: function onAKey(event) {
    if (!event.ctrlKey && !event.metaKey) {
      this.keyboardManager.deferredHandleKeyboardEvent(event);
    } else {
      event.stopPropagation();
      this.onSelectAll();
    }
  },

  /* Internal helper methods */
  processKeydown: function processKeydown() {
    this.model.set("criteria", this.$el.find("input.jr-mInput-search").val());
  },
  changeFilter: function changeFilter(callback) {
    var self = this,
        criteria = this.model.get("criteria");

    if (typeof criteria == "undefined" || criteria === "") {
      this.model.unset("totalValues", {
        silent: true
      });
    }

    this.listView.scrollTo(0);
    self.listView.activate(null, {
      silent: true
    }); //we have to drop active element before filter change

    this.getData({
      criteria: criteria,
      offset: 0,
      limit: 1
    }).done(function () {
      self.listViewModel.once("change", callback && typeof callback === "function" ? callback : function () {
        //need to set selected values again after change search criteria
        self.listView.setValue(_.keys(self.model.get("value")), {
          silent: true
        });
        self.listView.activate(0);
      });
      self.listView.fetch();
    });
  },
  clearFilter: function clearFilter(callback) {
    if (this.model.get("criteria")) {
      this.$el.find("input").val("");
      this.model.set("criteria", "", {
        silent: true
      });
      this.changeFilter(callback);
    } else {
      this.model.unset("totalValues", {
        silent: true
      });
      callback && callback();
    }
  },
  processSelectionThroughApi: function processSelectionThroughApi(selection, options) {
    var value = {},
        sx = 0,
        sLen = selection.length;
    this.selectionClear();

    for (sx; sx < sLen; sx += 1) {
      value[selection[sx]] = true;
    }

    this.model.attributes.value = value;

    if (!options || !options.silent) {
      this.model.trigger("change:value");
    }
  },
  convertSelectionForListViewModel: function convertSelectionForListViewModel(selection) {
    return selection;
  },

  /* Internal methods */
  getModelForRendering: function getModelForRendering() {
    return {
      label: this.label,
      isIPad: browserDetection.isIPad(),
      disabled: this.model.get("disabled"),
      i18n: i18n
    };
  },

  /* API */
  fetch: function fetch(callback, options) {
    var criteria = this.model.get("criteria");

    if (typeof criteria === "undefined" || criteria === "") {
      this.model.unset("totalValues", {
        silent: true
      });
    }

    this.listView.fetch(callback, options);
  },
  resize: function resize() {
    this.listView.resize();
    this.setBulkSelectionText();
  },
  reset: function reset(options) {
    var self = this;
    this.clearFilter(function () {
      self.listView.reset(options);
    });
  },
  getValue: function getValue() {
    return _.keys(this.model.get("value"));
  },
  setValue: function setValue(value, options) {
    options = options || {};
    this.listViewModel.once("selection:change", function () {
      this.selectionClear();
      var selection = this.listViewModel.getSelection(),
          i = 0,
          length = selection.length;

      for (i; i < length; i++) {
        this.model.attributes.value[selection[i]] = true;
      }

      if (!options || !options.silent) {
        this.changeValue();
      }
    }, this);

    if (!_.isArray(value) && typeof value !== "string") {
      value = this.convertSelectionForListViewModel(value);
    }

    this.listViewModel.select(value, options.modelOptions);
  },
  setDisabled: function setDisabled(disabled) {
    this.model.set("disabled", disabled);
  },
  getDisabled: function getDisabled() {
    return this.model.get("disabled");
  },
  remove: function remove() {
    this.listView.remove();
    Backbone.View.prototype.remove.call(this);
    $("body").off("mousedown", this.onGlobalMousedown).off("mouseup", this.onGlobalMouseup).off("mousemove", this.onGlobalMousemove);
    $(window).off("resize", this._debouncedOnResize);
  }
});
module.exports = AvailableItemsList;

});