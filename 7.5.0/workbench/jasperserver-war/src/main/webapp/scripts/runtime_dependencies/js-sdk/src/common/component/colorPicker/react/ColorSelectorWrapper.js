define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var React = require('react');

var ReactDOM = require('react-dom');

var _ColorSelector = require('./ColorSelector');

var ColorSelector = _ColorSelector.ColorSelector;

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(source, true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(source).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var defaultOptions = {
  color: '',
  label: '',
  showTransparentPreset: true,
  onColorChange: function onColorChange() {},
  ColorSelector: ColorSelector
};

var ColorSelectorWrapper =
/*#__PURE__*/
function () {
  function ColorSelectorWrapper(element) {
    var options = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : defaultOptions;

    _classCallCheck(this, ColorSelectorWrapper);

    this.element = element;
    this.onColorChange = options.onColorChange;
    this.renderColorSelector({
      color: options.color,
      label: options.label,
      showTransparentPreset: options.showTransparentPreset,
      onColorChange: this.onColorChange
    });
  }

  _createClass(ColorSelectorWrapper, [{
    key: "renderColorSelector",
    value: function renderColorSelector(state) {
      ReactDOM.render(React.createElement(ColorSelector, state), this.element);
    }
  }, {
    key: "setState",
    value: function setState(state) {
      this.renderColorSelector(_objectSpread({}, state, {
        onColorChange: this.onColorChange
      }));
    }
  }, {
    key: "remove",
    value: function remove() {
      ReactDOM.unmountComponentAtNode(this.element);
    }
  }]);

  return ColorSelectorWrapper;
}();

module.exports = ColorSelectorWrapper;

});