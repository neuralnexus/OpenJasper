define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var KeyboardManager = require('../manager/KeyboardManager');

var SingleSelectList = require('./SingleSelectList');

var SingleSelectListModel = require('../model/SingleSelectListModel');

var SearcheableDataProvider = require('../dataprovider/SearcheableDataProvider');

var DropDownManager = require('../manager/DropDownManager');

var singleSelectTemplate = require("text!../templates/singleSelectTemplate.htm");

var singleSelectTemplate_IE10_11 = require("text!../templates/singleSelectTemplate_IE10_11.htm");

var singleSelectListTemplate = require("text!../templates/listTemplate.htm");

var singleSelectItemsTemplate = require("text!../templates/itemsTemplate.htm");

var dropDownTemplate = require("text!../templates/dropDownTemplate.htm");

var i18n = require("bundle!ScalableInputControlsBundle");

var browserDetection = require('../../../common/util/browserDetection');

var xssUtil = require('../../../common/util/xssUtil');

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

var $body = $("body");
var SingleSelect = Backbone.View.extend({
  events: function events() {
    return {
      "keydown": this.keyboardManager.onKeydown,
      "input": this.keyboardManager.onKeydown,
      "focus input": "onFocus",
      "blur input": "onBlur",
      "mousedown": "onMousedown",
      "mouseup": "onMouseup",
      "click .jr-mSingleselect-input": "onClickOnInput"
    };
  },
  initialize: function initialize(options) {
    _.bindAll(this, "onGlobalMouseup", "onGlobalMousedown", "onGlobalMousemove", "onMousedown", "calcOffsetForListView", "collapse");

    if (!this.model) {
      this.model = new Backbone.Model();
    }

    this.model.set("expanded", false, {
      silent: true
    });
    this.model.set("criteria", "", {
      silent: true
    });
    this.model.set("value", {}, {
      silent: true
    });
    var template = singleSelectTemplate; // THIS IS A HACK TO COMPENSATE FOR AN IE10 and IE11 BUGS:
    // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/274987/
    // https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/101220/

    if (browserDetection.isIE10() || browserDetection.isIE11()) {
      template = singleSelectTemplate_IE10_11;
    }

    this.dropUpDirection = false;
    this.label = options.label;
    this.template = _.template(options.template || template);
    this.dropDownTemplate = _.template(options.dropDownTemplate || dropDownTemplate);
    this.keyboardManager = new KeyboardManager({
      keydownTimeout: options.keydownTimeout,
      context: this,
      deferredKeydownHandler: this.processKeydown,
      immediateHandleCondition: this.immediateHandleCondition,
      immediateKeydownHandler: this.immediateKeydownHandler,
      stopPropagation: true
    }); //Initialize internal list view model

    this.listViewModel = this._createListViewModel(options); //initialize list view

    this.listView = this._createListView(options);
    this.render();
    this.dropDownManager = new DropDownManager({
      dropDownEl: this.$dropDownElContainer,
      calcOffset: this.calcOffsetForListView,
      isOffsetChanged: this.isDimensionsChangedforListView,
      onOffsetChanged: this.collapse
    });
    this.initListeners();
    this.setValue(options.value, {
      modelOptions: {
        silent: true
      }
    });
  },
  _createListView: function _createListView(options) {
    return new SingleSelectList({
      el: options.listElement || $(singleSelectListTemplate),
      model: this.listViewModel,
      chunksTemplate: options.chunksTemplate,
      itemsTemplate: options.itemsTemplate || singleSelectItemsTemplate,
      scrollTimeout: options.scrollTimeout
    });
  },
  _createListViewModel: function _createListViewModel(options) {
    this.searcheableDataProvider = new SearcheableDataProvider({
      getData: options.getData
    });
    return new SingleSelectListModel({
      getData: this._getGetData(),
      bufferSize: options.bufferSize,
      loadFactor: options.loadFactor
    });
  },
  _getGetData: function _getGetData() {
    return this.searcheableDataProvider.getData;
  },
  _getActiveValueIndex: function _getActiveValueIndex(active) {
    var indexMapping = this.searcheableDataProvider.getIndexMapping();
    return indexMapping ? indexMapping[active.index] : active.index;
  },

  /*
   Bind event listeners
   */
  initListeners: function initListeners() {
    this.listenTo(this.listView, "selection:change", this.selectionChange, this);
    this.listenTo(this.listView, "item:mouseup", this.itemMouseup, this);
    this.listenTo(this.listView, "listRenderError", this.listRenderError, this);
    this.listenTo(this.model, "change:expanded", this.changeExpandedState, this);
    this.listenTo(this.model, "change:value", this.changeValue, this);
    this.listenTo(this.model, "change:disabled", this.changeDisabled, this);
    this.listenTo(this.model, "change:criteria", this.changeFilter, this);
    this.$dropDownEl.on("mousedown", this.onMousedown);
    $body.on("mousedown", this.onGlobalMousedown).on("dataavailable", this.onGlobalMousedown)
    /* hack to handle prototype custom events */
    .on("mouseup", this.onGlobalMouseup).on("mousemove", this.onGlobalMousemove);
  },
  render: function render() {
    var singleSelect = $(this.template({
      label: this.label,
      isIPad: browserDetection.isIPad(),
      value: this.model.get("value").label,
      expanded: this.model.get("expanded"),
      i18n: i18n
    }));
    this.renderListView();
    this.$el.empty();
    this.$el.append(singleSelect);
    this.$singleSelect = singleSelect;
    this.$searchBlock = this.$el.find(".jr-mSingleselect-search");
    this.$inputBlock = this.$el.find(".jr-mSingleselect-input");
    this.$input = this.$searchBlock.find("input");
    return this;
  },
  renderListView: function renderListView() {
    if (!this.listRendered) {
      this.$dropDownElContainer = $(this.dropDownTemplate({
        isIPad: navigator.platform === "iPad"
      }));
      this.$dropDownEl = this.$dropDownElContainer.find(".jr-mSingleselect-dropdown");
      this.listView.undelegateEvents();
      this.$dropDownEl.append(this.listView.$el);
      $body.append(this.$dropDownElContainer);
      this.listView.delegateEvents();
      this.listRendered = true;
    }

    this.$scalablelist = this.$dropDownElContainer.find(".jr-mScalablelist");
    this.model.get("expanded") ? this.$dropDownElContainer.show() : this.$dropDownElContainer.hide();
  },
  renderData: function renderData() {
    this.listView.renderData();
    return this;
  },

  /* Event handlers */
  listRenderError: function listRenderError(responseStatus, error) {
    this.trigger("listRenderError", responseStatus, error);
  },
  itemMouseup: function itemMouseup() {
    if (this.model.get("disabled")) {
      return;
    } //Remove flag to allow blur


    delete this.preventBlur; //Preserve focus

    this.$input.focus();
    var activeValue = this.model.get("activeValue");

    if (activeValue.value !== this.model.get("value").value) {
      this.model.unset("activeValue");
      this.model.set("value", activeValue);
    } else {
      this.collapse();
    }
  },
  selectionChange: function selectionChange() {
    var active = this.listView.getActiveValue();

    if (active) {
      this.listView.scrollTo(active.index);
      this.model.set("activeValue", {
        value: active.value,
        label: active.label,
        index: this._getActiveValueIndex(active)
      });
      this.updateValueFromActive();
    } else {
      this.model.unset("activeValue");
    }
  },
  onClickOnInput: function onClickOnInput() {
    this.model.get("expanded") ? this.collapse() : this.expand();
  },
  onFocus: function onFocus() {
    if (!this.model.get("expanded")) {
      this.$inputBlock.addClass("jr-isFocused");
    }
  },
  onBlur: function onBlur() {
    if (!this.preventBlur) {
      this.collapse();
      this.$inputBlock.removeClass("jr-isFocused");
    }
  },
  onMousedown: function onMousedown() {
    this.preventBlur = true;
  },
  onMouseup: function onMouseup() {
    if (this.preventBlur) {
      delete this.preventBlur;

      if (this.model.get("expanded")) {
        this.$input.focus();
      }
    }
  },
  changeExpandedState: function changeExpandedState(model) {
    if (model.get("expanded")) {
      this.doExpand();
    } else {
      this.doCollapse();
    }
  },
  changeValue: function changeValue() {
    this.collapse();
    this.updateControlLabel();

    if (!this.silent) {
      this.trigger("selection:change", this.model.get("value").value);
    } else {
      delete this.silent;
    }

    this.$input.val("");
    this.model.set("criteria", "");
  },
  changeDisabled: function changeDisabled() {
    var disabled = this.model.get("disabled");

    if (disabled) {
      this.$input.attr("disabled", "disabled");
      this.collapse();
    } else {
      this.$input.removeAttr("disabled");
    }

    this.listView.setDisabled(disabled);
  },
  onGlobalMousedown: function onGlobalMousedown(event) {
    if (this.model.get("expanded")) {
      if (event.target === this.el || this.$el.find(event.target).length > 0 || event.target === this.$dropDownEl[0] || this.$dropDownEl.find(event.target).length > 0) {
        //Do not collapse if mousedown performed on this component
        this.$input.focus();
        return;
      }

      this.preventBlur = false;
    }

    this.onBlur();
  },
  onGlobalMouseup: function onGlobalMouseup() {
    this.onMouseup();
  },
  onGlobalMousemove: function onGlobalMousemove(event) {
    if (this.preventBlur) {
      event.stopPropagation();
    }
  },

  /* Key handlers for KeyboardManager */
  onUpKey: function onUpKey() {
    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      var active = this.listView.getActiveValue();

      if (!active || active.index === 0) {
        this.collapse();
        return;
      } else {
        this.listView.activatePrevious();
      }
    }
  },
  onDownKey: function onDownKey() {
    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      var active = this.listView.getActiveValue();

      if (active) {
        this.listView.activateNext();
      } else {
        this.listView.activateFirst();
      }
    }
  },
  onEnterKey: function onEnterKey(event) {
    event.preventDefault();

    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      if (this.model.get("activeValue")) {
        this.itemMouseup();
      } else {
        this.collapse();
      }
    }
  },
  onEscKey: function onEscKey() {
    if (this.model.get("expanded")) {
      this.collapse();
    }
  },
  onHomeKey: function onHomeKey() {
    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      this.listView.activateFirst();
    }
  },
  onEndKey: function onEndKey() {
    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      this.listView.activateLast();
    }
  },
  onPageUpKey: function onPageUpKey() {
    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      this.listView.pageUp();
    }
  },
  onPageDownKey: function onPageDownKey() {
    if (!this.model.get("expanded")) {
      this.expand();
    } else {
      this.listView.pageDown();
    }
  },
  onTabKey: function onTabKey() {
    /* do nothing */
  },

  /* Internal helper methods */
  updateDropDirection: function updateDropDirection() {
    // calc max height up and down
    var scrollHeight = $("html").scrollTop();
    var offset = this.$el.offset();
    var elHeight = this.$el.height();
    var searchBlockHeight = this.$searchBlock.outerHeight();
    var topHeight = offset.top - scrollHeight;
    var bottomHeight = $(window).height() - topHeight - elHeight;
    this.dropUpDirection = topHeight > bottomHeight; // set list max height

    var maxSize = this.dropUpDirection ? topHeight : bottomHeight;
    maxSize -= searchBlockHeight + elHeight;
    this.$scalablelist.css("max-height", maxSize + "px");
  },
  doExpand: function doExpand() {
    this.updateDropDirection();

    if (this.dropUpDirection) {
      this.$dropDownEl.addClass("jr-mSingleselect-dropdownTop");
      this.$singleSelect.addClass("jr-mSingleselectTop");
    }

    this.$searchBlock.addClass("jr-isOpen");
    this.$inputBlock.addClass("jr-isOpen");
    this.$inputBlock.removeClass("jr-isFocused");
    this.$input.focus();
    this.listView.onScroll();
    this.expandListView();

    if (this.listView.lazy) {
      this.listView.fetch(_.bind(this.listView.resize, this.listView));
    } else {
      this.listView.resize();
    }

    if (typeof this.model.get("value").value !== "undefined") {
      this.setValueToList();
    }

    this.trigger("expand", this);
  },
  expandListView: function expandListView() {
    this.$dropDownElContainer.show();
    this.dropDownManager.startCalc();
  },
  doCollapse: function doCollapse() {
    this.model.unset("activeValue");
    this.$searchBlock.removeClass("jr-isOpen");
    this.$inputBlock.removeClass("jr-isOpen");
    this.$dropDownEl.removeClass("jr-mSingleselect-dropdownTop");
    this.$singleSelect.removeClass("jr-mSingleselectTop");
    this.$inputBlock.addClass("jr-isFocused");
    this.$input.val("");
    this.$dropDownElContainer.hide();
    this.model.set("criteria", "");
    this.dropDownManager.stopCalc();
    this.trigger("collapse", this);
  },
  calcOffsetForListView: function calcOffsetForListView() {
    var top;
    var offset = this.$el.offset();
    var left = $body.offset().left + offset.left;
    var elHeight = this.$el.height();

    if (this.dropUpDirection) {
      top = $body.offset().top + offset.top - this.$searchBlock.outerHeight();
    } else {
      top = $body.offset().top + offset.top + elHeight + this.$searchBlock.height();
    }

    return {
      top: top,
      left: left,
      width: this.el.getBoundingClientRect().width
    };
  },
  isDimensionsChangedforListView: function isDimensionsChangedforListView(currentDimensions, newDimensions) {
    return Math.floor(currentDimensions.top) !== Math.floor(newDimensions.top) || Math.floor(currentDimensions.left) !== Math.floor(newDimensions.left);
  },
  immediateHandleCondition: function immediateHandleCondition(event) {
    return !this.model.get("expanded");
  },
  immediateKeydownHandler: function immediateKeydownHandler(event) {
    this.expand();
    this.keyboardManager.deferredHandleKeyboardEvent(event);
  },
  processKeydown: function processKeydown() {
    this.model.set("criteria", this.$input.val());
  },
  changeFilter: function changeFilter() {
    var that = this;

    this._getGetData()({
      criteria: this.model.get("criteria")
    }).done(function () {
      that.listView.fetch(function () {
        that.setValueToList();
      });
    });
  },
  setValueToList: function setValueToList(options) {
    var value = this.model.get("value");
    var selection = value.value;

    if (typeof value.index !== "undefined") {
      var indexMapping = this.searcheableDataProvider.getReverseIndexMapping();
      var index = indexMapping ? indexMapping[value.index] : value.index;
      selection = {};

      if (typeof index !== "undefined") {
        selection[index] = value.value;
      }
    }

    this.listView.setValue(selection, options);
  },
  //value is external usual value: either string, or array of strings or object
  convertExternalValueToInternalFormat: function convertExternalValueToInternalFormat(value) {
    var internalValue = {};

    if (typeof value !== "undefined") {
      if (typeof value === "string" || value === null) {
        internalValue = {
          value: value
        };
      } else {
        for (var index in value) {
          if (value.hasOwnProperty(index) && value[index] !== undefined) {
            if (_typeof(value[index]) === 'object') {
              internalValue = {
                value: value[index].value,
                index: parseInt(index, 10),
                label: value[index].label
              };
            } else {
              internalValue = {
                value: value[index],
                index: parseInt(index, 10)
              };
            }

            break;
          }
        }
      }
    }

    return internalValue;
  },
  updateValueFromActive: function updateValueFromActive() {
    var value = this.model.get("value");
    var activeValue = this.model.get("activeValue");

    if (value.value === activeValue.value && typeof value.label === "undefined") {
      value.label = activeValue.label;
      this.updateControlLabel();
    }
  },
  updateControlLabel: function updateControlLabel() {
    var value = this.model.get("value");
    var $inputBlockPlaceholder = this.$inputBlock.find(".jr-mSingleselect-input-placeholder");
    var $inputBlockSelection = this.$inputBlock.find(".jr-mSingleselect-input-selection");
    var label;

    if (_.isEmpty(value) || value.value == null) {
      $inputBlockPlaceholder.removeClass("jr-isHidden");
      this.render();
    } else {
      label = this.getControlLabelByValue(value);
      $inputBlockPlaceholder.addClass("jr-isHidden");
      this.$inputBlock.attr("title", label);
      $inputBlockSelection.html(xssUtil.hardEscape(label));
    }
  },
  getControlLabelByValue: function getControlLabelByValue(value) {
    return value.label == null ? value.value : value.label;
  },

  /* API */
  fetch: function fetch(callback, options) {
    this.listView.fetch(callback, options);
    return this;
  },
  reset: function reset(options) {
    this.listView.reset(options);
    return this;
  },
  expand: function expand() {
    if (this.model.get("disabled")) {
      return;
    }

    this.model.set("expanded", true);
    return this;
  },
  collapse: function collapse() {
    this.model.set("expanded", false);
    return this;
  },
  getValue: function getValue() {
    return this.model.get("value").value;
  },
  setValue: function setValue(value, options) {
    var value = this.convertExternalValueToInternalFormat(value);

    if (options && options.silent) {
      this.silent = true;
    }

    this.model.set("value", value);
    this.setValueToList();
  },
  setDisabled: function setDisabled(disabled) {
    this.model.set("disabled", disabled);
    return this;
  },
  getDisabled: function getDisabled() {
    return this.model.get("disabled");
  },
  remove: function remove() {
    this.$dropDownEl.off("mousedown", this.onMousedown).remove();
    this.listView.remove();
    this.$dropDownElContainer.remove();
    Backbone.View.prototype.remove.call(this);
    $body.off("mousedown", this.onGlobalMousedown).off("dataavailable", this.onGlobalMousedown).off("mouseup", this.onGlobalMouseup).off("mousemove", this.onGlobalMousemove);
  }
});
module.exports = SingleSelect;

});