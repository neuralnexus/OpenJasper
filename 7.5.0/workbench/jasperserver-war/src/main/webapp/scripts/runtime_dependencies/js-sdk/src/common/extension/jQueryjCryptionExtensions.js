define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _jCryption = require('jCryption');

var BigInt = _jCryption.BigInt;
var biToHex = _jCryption.biToHex;
var biToString = _jCryption.biToString;

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

/*global BigInt, biToHex, biToString */
$.jCryption.encryptKeyWithoutRedundancy = function (string, keyPair, callback) {
  if (string === '') {
    if ($.isFunction(callback)) {
      callback(string);
      return;
    } else {
      return string;
    }
  }

  var charSum = 0;

  for (var i = 0; i < string.length; i++) {
    charSum += string.charCodeAt(i);
  }

  var encrypt = [];
  var j = 0;

  while (j < string.length) {
    encrypt[j] = string.charCodeAt(j);
    j++;
  }

  while (encrypt.length % keyPair.chunkSize !== 0) {
    encrypt[j++] = 0;
  }

  function encryption(encryptObject) {
    var charCounter = 0;
    var j, block;
    var encrypted = '';

    function encryptChar() {
      block = new BigInt();
      j = 0;

      for (var k = charCounter; k < charCounter + keyPair.chunkSize; ++j) {
        block.digits[j] = encryptObject[k++];
        block.digits[j] += encryptObject[k++] << 8;
      }

      var crypt = keyPair.barrett.powMod(block, keyPair.e);
      var text = keyPair.radix == 16 ? biToHex(crypt) : biToString(crypt, keyPair.radix);
      encrypted += text + ' ';
      charCounter += keyPair.chunkSize;

      if (charCounter < encryptObject.length) {
        setTimeout(encryptChar, 1);
      } else {
        var encryptedString = encrypted.substring(0, encrypted.length - 1);

        if ($.isFunction(callback)) {
          callback(encryptedString);
        } else {
          return encryptedString;
        }
      }
    }

    setTimeout(encryptChar, 1);
  }

  encryption(encrypt);
};

module.exports = $;

});