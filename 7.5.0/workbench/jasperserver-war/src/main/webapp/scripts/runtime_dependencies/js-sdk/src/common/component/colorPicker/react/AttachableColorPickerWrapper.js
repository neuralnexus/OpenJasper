define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var React = require('react');

var ReactDOM = require('react-dom');

var _AttachableColorPicker = require('./AttachableColorPicker');

var AttachableColorPicker = _AttachableColorPicker.AttachableColorPicker;

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var defaultOptions = {
  padding: {
    top: 0,
    left: 0
  },
  disableAlpha: true,
  showTransparentPreset: true,
  color: '',
  onChangeComplete: function onChangeComplete() {},
  onHide: function onHide() {},
  ColorPicker: AttachableColorPicker
};

var AttachableColorPickerWrapper =
/*#__PURE__*/
function () {
  function AttachableColorPickerWrapper(attachTo) {
    var _this = this;

    var options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : defaultOptions;

    _classCallCheck(this, AttachableColorPickerWrapper);

    this.options = options;
    this.attachTo = attachTo;
    this.color = this.options.color;
    this.ColorPicker = this.options.ColorPicker || AttachableColorPicker;
    this.colorPickerContainerWrapper = document.createElement('div');
    this.colorPickerContainerWrapper.className = 'jr-jColorPickerWrapper';
    this.boundOnAttachElementClick = this.onAttachElementClick.bind(this);
    var _this$options = this.options,
        onChangeComplete = _this$options.onChangeComplete,
        onHide = _this$options.onHide;

    this.onChangeCompleteWrapper = function (color) {
      _this.color = color.hex;
      onChangeComplete(color);
    };

    this.onHideWrapper = function () {
      var state = _this.getColorPickerState(false);

      _this.renderColorPicker(state);

      if (onHide) {
        onHide();
      }
    };

    var state = this.getColorPickerState(false);
    this.renderColorPicker(state);
  }

  _createClass(AttachableColorPickerWrapper, [{
    key: "renderColorPicker",
    value: function renderColorPicker(state) {
      this.remove();
      this.attachTo.addEventListener('click', this.boundOnAttachElementClick);
      document.body.appendChild(this.colorPickerContainerWrapper);
      ReactDOM.render(React.createElement(this.ColorPicker, state), this.colorPickerContainerWrapper);
    }
  }, {
    key: "onAttachElementClick",
    value: function onAttachElementClick() {
      var state = this.getColorPickerState(true);
      this.renderColorPicker(state);
    }
  }, {
    key: "getColorPickerState",
    value: function getColorPickerState(show) {
      return {
        padding: this.options.padding,
        show: show,
        color: this.color,
        disableAlpha: this.options.disableAlpha,
        showTransparentPreset: this.options.showTransparentPreset,
        onChangeComplete: this.onChangeCompleteWrapper,
        onHide: this.onHideWrapper,
        attachTo: this.attachTo
      };
    }
  }, {
    key: "setColor",
    value: function setColor(color) {
      this.color = color;
    }
  }, {
    key: "remove",
    value: function remove() {
      this.attachTo.removeEventListener('click', this.boundOnAttachElementClick);
      ReactDOM.unmountComponentAtNode(this.colorPickerContainerWrapper);
      $(this.colorPickerContainerWrapper).remove();
    }
  }]);

  return AttachableColorPickerWrapper;
}();

module.exports = AttachableColorPickerWrapper;

});