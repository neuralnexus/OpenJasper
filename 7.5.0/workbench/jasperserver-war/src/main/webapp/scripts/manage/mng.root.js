define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var mngCommonActions = require('./mng.common.actions');

var mngCommon = require('./mng.common');

var mngMain = require('./mng.main');

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

var orgModule = _extends(mngCommonActions, mngCommon, mngMain); // expose to global scope


window.orgModule = orgModule;
module.exports = orgModule;

});