define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var React = require('react');

var colorConvertUtil = require('../util/colorConvertUtil');

var Colors = require('./enum/colors');

var SWATCH_LIGHT_CLASS = 'jr-mControl-launcher-swatchLight';
var TRANSPARENT_CLASS = "".concat(SWATCH_LIGHT_CLASS, " jr-mControl-launcher-swatchTransparent");

var getSwatchLightClass = function getSwatchLightClass(color) {
  if (color === Colors.TRANSPARENT) {
    return TRANSPARENT_CLASS;
  }

  if (!colorConvertUtil.isColorDark(color)) {
    return SWATCH_LIGHT_CLASS;
  }

  return '';
};

var ColorSample = function ColorSample(props) {
  var style = {
    backgroundColor: props.color
  };
  var className = "jr-mControl-launcher-swatch ".concat(getSwatchLightClass(props.color), " jr");
  return (// eslint-disable-next-line jsx-a11y/no-static-element-interactions
    React.createElement("div", {
      className: "jr-mControl-launcher jr",
      onClick: props.onClick
    }, React.createElement("div", {
      className: className,
      style: style
    }), React.createElement("div", {
      className: "jr-mControl-launcher-hex jr"
    }, props.label))
  );
};

exports.ColorSample = ColorSample;

});