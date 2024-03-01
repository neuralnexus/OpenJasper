define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var repositorySearchActions = require('./repository.search.actions');

var repositorySearchComponents = require('./repository.search.components');

var _repositorySearchMain = require('./repository.search.main');

var repositorySearch = _repositorySearchMain.repositorySearch;

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

var repositorySearchRoot = _extends(repositorySearch, repositorySearchActions, repositorySearchComponents); // expose to global scope


window.repositorySearch = repositorySearchRoot;
module.exports = repositorySearchRoot;

});