define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('../extension/jQueryjCryptionExtensions');

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
var Base64Coder = {};
Base64Coder.code = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';

Base64Coder.encode16BitString = function (str) {
  var hd1,
      hd2,
      bits,
      h1,
      h2,
      h3,
      h4,
      h5,
      d6,
      e = [],
      pad = '',
      c,
      plain,
      coded;
  var b64 = Base64Coder.code;
  plain = str;
  c = plain.length % 2;

  if (c > 0) {
    while (c++ < 2) {
      pad += '===';
      plain += '\0';
    }
  }

  for (c = 0; c < plain.length; c += 2) {
    hd1 = plain.charCodeAt(c);
    hd2 = plain.charCodeAt(c + 1);
    bits = hd1 << 16 | hd2;
    h1 = bits >> 26 & 63;
    h2 = bits >> 20 & 63;
    h3 = bits >> 14 & 63;
    h4 = bits >> 8 & 63;
    h5 = bits >> 2 & 63;
    d6 = bits & 3;
    e[c / 2] = b64.charAt(h1) + b64.charAt(h2) + b64.charAt(h3) + b64.charAt(h4) + b64.charAt(h5) + b64.charAt(d6);
  }

  coded = e.join('');
  coded = coded.slice(0, coded.length - pad.length) + pad;
  return coded;
};

Base64Coder.decode16BitString = function (str) {
  var o1,
      o2,
      o3,
      o4,
      h1,
      h2,
      h3,
      h4,
      h5,
      d6,
      bits,
      d = [],
      plain,
      coded;
  var b64 = Base64Coder.code;
  coded = str;

  for (var c = 0; c < coded.length; c += 6) {
    h1 = b64.indexOf(coded.charAt(c));
    h2 = b64.indexOf(coded.charAt(c + 1));
    h3 = b64.indexOf(coded.charAt(c + 2));
    h4 = b64.indexOf(coded.charAt(c + 3));
    h5 = b64.indexOf(coded.charAt(c + 4));
    d6 = b64.indexOf(coded.charAt(c + 5));
    bits = h1 << 26 | h2 << 20 | h3 << 14 | h4 << 8 | h5 << 2 | d6 & 3;
    o1 = bits >>> 24 & 255;
    o2 = bits >>> 16 & 255;
    o3 = bits >>> 8 & 255;
    o4 = bits & 255;
    d[c / 6] = String.fromCharCode(o1 << 8 | o2, o3 << 8 | o4);
    if (h4 == 64) d[c / 6] = d[c / 6] = String.fromCharCode(o1 << 8 | o2);
  }

  plain = d.join('');
  return plain;
};

var JSEncrypter = {
  encryptData: function encryptData(dataObj, callbackFunc) {
    if (!dataObj) {
      callbackFunc();
      return;
    }

    $.jCryption.getKeys('GetEncryptionKey', function (receivedKey) {
      var pubKey = receivedKey;
      var encDataObj = {};
      var keyArr = [];

      for (var i in dataObj) {
        keyArr.push(i);
      }

      var encData = {};

      JSEncrypter._encryptDataRecursive(dataObj, keyArr, 0, pubKey, encData, callbackFunc);
    });
  },
  _encryptDataRecursive: function _encryptDataRecursive(dataObj, keyArr, index, pubKey, encDataObj, callbackFunc) {
    if (!keyArr || keyArr.length == index) return;
    var dataToEncrypt = encodeURIComponent(dataObj[keyArr[index]]);
    var reversedDataToEncrypt = dataToEncrypt.split('').reverse().join('');
    $.jCryption.encryptKeyWithoutRedundancy(reversedDataToEncrypt, pubKey, function (receivedEncrypted) {
      encDataObj[keyArr[index]] = receivedEncrypted;
      if (keyArr.length == index + 1) callbackFunc(encDataObj);else JSEncrypter._encryptDataRecursive(dataObj, keyArr, index + 1, pubKey, encDataObj, callbackFunc);
    });
  }
};
module.exports = JSEncrypter;

});