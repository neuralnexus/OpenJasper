define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var settings = require("requestSettings");

var configs = require("runtime_dependencies/js-sdk/src/jrs.configs");

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

var locale = configs.userLocale.replace(/_/g, '-');
module.exports = _extends({}, settings, {
  headers: _extends({}, settings.headers, {
    'Accept-Language': locale
  })
});

});