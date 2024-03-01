define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var underscore = require('underscore');

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } }

function _createClass(Constructor, protoProps, staticProps) { if (protoProps) _defineProperties(Constructor.prototype, protoProps); if (staticProps) _defineProperties(Constructor, staticProps); return Constructor; }

var MyClass =
/*#__PURE__*/
function () {
  function MyClass(name) {
    _classCallCheck(this, MyClass);

    this.privateName = name;
    underscore.extend({}, {
      prop: true
    });
  }

  _createClass(MyClass, [{
    key: "name",
    get: function get() {
      return this.privateName;
    },
    set: function set(newName) {
      this.privateName = newName;
    }
  }]);

  return MyClass;
}();

module.exports = MyClass;

});