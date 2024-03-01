define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var orgRoleMngActions = require('./org.role.mng.actions');

var orgRoleMngComponents = require('./org.role.mng.components');

var orgRoleMngMain = require('./org.role.mng.main');

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

module.exports = _extends(orgRoleMngActions, orgRoleMngComponents, orgRoleMngMain);

});