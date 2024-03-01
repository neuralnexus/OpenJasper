define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var React = require('react');

var _reactColor = require('react-color');

var SketchPicker = _reactColor.SketchPicker;

var positionUtil = require('../../base/util/attachableComponentPositionUtil');

var Colors = require('./enum/colors');

function ownKeys(object, enumerableOnly) { var keys = Object.keys(object); if (Object.getOwnPropertySymbols) { var symbols = Object.getOwnPropertySymbols(object); if (enumerableOnly) symbols = symbols.filter(function (sym) { return Object.getOwnPropertyDescriptor(object, sym).enumerable; }); keys.push.apply(keys, symbols); } return keys; }

function _objectSpread(target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i] != null ? arguments[i] : {}; if (i % 2) { ownKeys(source, true).forEach(function (key) { _defineProperty(target, key, source[key]); }); } else if (Object.getOwnPropertyDescriptors) { Object.defineProperties(target, Object.getOwnPropertyDescriptors(source)); } else { ownKeys(source).forEach(function (key) { Object.defineProperty(target, key, Object.getOwnPropertyDescriptor(source, key)); }); } } return target; }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

function _possibleConstructorReturn(self, call) { if (call && (_typeof(call) === "object" || typeof call === "function")) { return call; } return _assertThisInitialized(self); }

function _getPrototypeOf(o) { _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) { return o.__proto__ || Object.getPrototypeOf(o); }; return _getPrototypeOf(o); }

function _assertThisInitialized(self) { if (self === void 0) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function"); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, writable: true, configurable: true } }); if (superClass) _setPrototypeOf(subClass, superClass); }

function _setPrototypeOf(o, p) { _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) { o.__proto__ = p; return o; }; return _setPrototypeOf(o, p); }

var getPresetColors = function getPresetColors(showTransparentPreset) {
  var presetColors = ['#D0021B', '#F5A623', '#F8E71C', '#8B572A', '#7ED321', '#417505', '#BD10E0', '#9013FE', '#4A90E2', '#50E3C2', '#B8E986', '#000000', '#4A4A4A', '#9B9B9B', '#FFFFFF'];

  if (showTransparentPreset) {
    presetColors = presetColors.concat([Colors.TRANSPARENT]);
  }

  return presetColors;
};

var ColorPickerWithAbilityToAttach =
/*#__PURE__*/
function (_React$Component) {
  _inherits(ColorPickerWithAbilityToAttach, _React$Component);

  function ColorPickerWithAbilityToAttach(props) {
    var _this;

    _classCallCheck(this, ColorPickerWithAbilityToAttach);

    _this = _possibleConstructorReturn(this, _getPrototypeOf(ColorPickerWithAbilityToAttach).call(this, props));
    var disableAlpha = props.disableAlpha,
        showTransparentPreset = props.showTransparentPreset;
    _this.state = {
      disableAlpha: typeof disableAlpha === 'undefined' ? true : disableAlpha,
      showTransparentPreset: typeof showTransparentPreset === 'undefined' ? true : showTransparentPreset
    };
    _this.divRef = React.createRef();
    _this.boundOnDocumentMousedown = _this.onDocumentMousedown.bind(_assertThisInitialized(_this));
    return _this;
  }

  _createClass(ColorPickerWithAbilityToAttach, [{
    key: "componentDidMount",
    value: function componentDidMount() {
      this.forceUpdate();
    }
  }, {
    key: "componentDidUpdate",
    value: function componentDidUpdate() {
      this.props.doc.removeEventListener('mousedown', this.boundOnDocumentMousedown);

      if (this.props.show && this.divRef.current) {
        this.props.doc.addEventListener('mousedown', this.boundOnDocumentMousedown);
      }
    }
  }, {
    key: "componentWillUnmount",
    value: function componentWillUnmount() {
      this.props.doc.removeEventListener('mousedown', this.boundOnDocumentMousedown);
    }
  }, {
    key: "onDocumentMousedown",
    value: function onDocumentMousedown(e) {
      var colorPickerEl = this.divRef.current;
      var mouseDownTarget = e.target;
      var attachTo = this.props.attachTo;

      if (colorPickerEl) {
        if (!colorPickerEl.contains(mouseDownTarget) && !colorPickerEl.isEqualNode(mouseDownTarget) && !attachTo.contains(mouseDownTarget) && !attachTo.isEqualNode(mouseDownTarget)) {
          this.props.onHide();
        }
      }
    }
  }, {
    key: "render",
    value: function render() {
      var _this$props = this.props,
          show = _this$props.show,
          color = _this$props.color,
          attachTo = _this$props.attachTo,
          padding = _this$props.padding,
          onChangeComplete = _this$props.onChangeComplete;
      var _this$state = this.state,
          disableAlpha = _this$state.disableAlpha,
          showTransparentPreset = _this$state.showTransparentPreset;
      var position = {
        top: 0,
        left: 0
      };

      if (this.divRef.current) {
        position = positionUtil.getPosition(attachTo, padding, this.divRef.current);
      }

      var style = {
        position: 'absolute',
        zIndex: 9000,
        top: "".concat(position.top, "px"),
        left: "".concat(position.left, "px"),
        visibility: show && this.divRef.current ? 'visible' : 'hidden'
      };
      return React.createElement("div", {
        style: style,
        ref: this.divRef
      }, React.createElement(this.props.ColorPicker, {
        color: color,
        disableAlpha: disableAlpha,
        onChangeComplete: onChangeComplete,
        presetColors: getPresetColors(showTransparentPreset)
      }));
    }
  }]);

  return ColorPickerWithAbilityToAttach;
}(React.Component);

var withAbilityToAttach = function withAbilityToAttach(ColorPicker, doc) {
  return function (props) {
    var extendedProps = _objectSpread({}, props, {
      ColorPicker: ColorPicker,
      doc: doc
    });

    return React.createElement(ColorPickerWithAbilityToAttach, extendedProps);
  };
};

var AttachableColorPicker = withAbilityToAttach(SketchPicker, document);
exports.withAbilityToAttach = withAbilityToAttach;
exports.AttachableColorPicker = AttachableColorPicker;

});