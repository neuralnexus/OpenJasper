define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

module.exports = function (html) {
  var match = html.trim().match(/^<div\s?(?:(?:.*)=(?:['"]).*(?:['"]))*>(.*)<\/div>/);
  return JSON.parse(match ? match[1] : '{}');
};

});