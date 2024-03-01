define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var jQuery = require('jquery');

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

var JSEncrypter = require("runtime_dependencies/js-sdk/src/common/util/encrypter");

var webHelpModule = require('../components/components.webHelp');

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
jQuery(function () {
  var usernameElement = jQuery('#j_username'),
      passwordPseudoElement = jQuery('#j_password_pseudo'),
      organisationElement = jQuery('#orgId');
  webHelpModule.setCurrentContext('login');

  var submitLogin = function submitLogin(event) {
    if (jrsConfigs.isEncryptionOn) {
      //global property from jsp page, set up in security-config.properties
      var paramsToEncrypt = {
        j_password: passwordPseudoElement.val()
      };

      if (typeof window.doesAllowUserPasswordChange !== 'undefined' && window.doesAllowUserPasswordChange) {
        var newPass1 = jQuery('#j_newpassword1_pseudo').val();
        var newPass2 = jQuery('#j_newpassword2_pseudo').val();
        if (jQuery.trim(newPass1)) paramsToEncrypt.j_newpassword1 = newPass1;
        if (jQuery.trim(newPass2)) paramsToEncrypt.j_newpassword2 = newPass2;
      }

      JSEncrypter.encryptData(paramsToEncrypt, function (encData) {
        for (var k in encData) {
          //set hidden fields to encrypted values
          jQuery('#' + k).val(encData[k]); // hide pseudo password field contents, so that browser autocomplete
          // is not trigger to remember the encrypted password every time.
          // hide pseudo password field contents, so that browser autocomplete
          // is not trigger to remember the encrypted password every time.

          jQuery('#' + k + '_pseudo').val('');
        }

        jQuery('#loginForm').submit();
      });
    } else {
      jQuery('#j_password').val(passwordPseudoElement.val());
      jQuery('#j_newpassword1').val(jQuery('#j_newpassword1_pseudo').val());
      jQuery('#j_newpassword2').val(jQuery('#j_newpassword2_pseudo').val());
      jQuery('#loginForm').submit();
    }

    event.preventDefault();
  };

  jQuery('#submitButton').click(submitLogin).removeAttr('disabled');
  usernameElement.keypress(function (event) {
    if ((event.keyCode || event.which) != 13) return;
    submitLogin(event);
  });
  passwordPseudoElement.keypress(function (event) {
    if ((event.keyCode || event.which) != 13) return;
    submitLogin(event);
  });
  organisationElement.keypress(function (event) {
    if ((event.keyCode || event.which) != 13) return;
    submitLogin(event);
  });
});

});