define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Menu = require('./Menu');

var ClickComponent = require('../base/ClickComponent');

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

module.exports = Menu.extend(ClickComponent.extend({
  constructor: function constructor(options, attachTo, additionalSettings) {
    additionalSettings || (additionalSettings = {});

    var clickComponentAdditionalSettings = _extends({
      toggleMode: additionalSettings.menuToggleMode
    }, additionalSettings);

    ClickComponent.call(this, attachTo, additionalSettings.padding, clickComponentAdditionalSettings);

    try {
      Menu.call(this, options, additionalSettings);
    } catch (e) {
      ClickComponent.prototype.remove.apply(this, arguments);
      throw e;
    }
  },
  show: function show() {
    ClickComponent.prototype.show.apply(this, arguments);
    return Menu.prototype.show.apply(this, arguments);
  },
  remove: function remove() {
    ClickComponent.prototype.remove.apply(this, arguments);
    Menu.prototype.remove.apply(this, arguments);
  }
}).prototype);

});