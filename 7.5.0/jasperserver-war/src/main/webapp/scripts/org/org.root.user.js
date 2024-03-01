define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var orgUserMngActions = require('./org.user.mng.actions');

var orgUserMngMain = require('./org.user.mng.main');

var orgUserMngComponents = require('./org.user.mng.components');

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

module.exports = _extends(orgUserMngMain, orgUserMngActions, orgUserMngComponents);

});