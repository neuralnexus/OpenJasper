define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var importRestErrorCodesEnum = require('../enum/importRestErrorCodesEnum');

var i18n = require("bundle!ImportExportBundle");

var awsSettings = require("settings!awsSettings");

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

var getImportDecodeFailedError = function getImportDecodeFailedError() {
  if (awsSettings.productTypeIsJrsAmi || awsSettings.productTypeIsMpAmi) {
    return i18n['import.decode.failed.aws'];
  }

  return i18n['import.decode.failed'];
};

var restErrorCodeToErrorProviderMap = _defineProperty({}, importRestErrorCodesEnum.IMPORT_DECODE_FAILED, getImportDecodeFailedError);

module.exports = {
  create: function create(errorCode) {
    var errorProvider = restErrorCodeToErrorProviderMap[errorCode];

    if (errorProvider) {
      return errorProvider();
    }

    return i18n[errorCode] || i18n["import.error.unexpected"];
  }
};

});