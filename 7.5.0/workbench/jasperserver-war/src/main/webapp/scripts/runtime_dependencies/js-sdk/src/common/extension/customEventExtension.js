define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

if (typeof window.CustomEvent !== "function") {
  // JRS-20985: For IE11 we have to polyfill CustomEvent
  var CustomEvent = function CustomEvent(event, params) {
    params = params || {
      bubbles: false,
      cancelable: false,
      detail: undefined
    };
    var evt = document.createEvent('CustomEvent');
    evt.initCustomEvent(event, params.bubbles, params.cancelable, params.detail);
    return evt;
  };

  CustomEvent.prototype = window.Event.prototype;
  window.CustomEvent = CustomEvent;
}

});