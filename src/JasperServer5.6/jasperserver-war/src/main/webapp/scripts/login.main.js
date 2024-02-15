/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: login.main.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {

    "use strict";
    var domReady = require("!domReady");
    require("login");

    var jQuery = require("jquery");
    var loginBox = require("components.loginBox");
    var jrsConfigs = require("jrs.configs");

    domReady(function(){
        isIPad() && jQuery('#frame').hide();

        if (jrsConfigs.isProVersion) {

            loginBox._initVars = function(options) {
                this._baseInitVars(options);

                this._organizationId = options.organizationId;
                this._singleOrganization = options.singleOrganization;
            };

            loginBox._processTemplate = function() {
                this._baseProcessTemplate();

                this._organizationIdLabel = this._dom.select('label[for="orgId"]')[0];
                this._organizationIdInput = jQuery('#orgId');
            };

            loginBox.initialize = function(options) {
                var usernameInput = jQuery('#j_username');
                this._baseInitialize(options);

                if (!this._singleOrganization && !this._organizationId){
                    this._organizationIdLabel.removeClassName("hidden");
                } else {
                    this._organizationIdInput.val(this._organizationId);
                }

                if (usernameInput.val() === "" && jQuery('#j_password_pseudo').val() === "") {
                    if (this._singleOrganization) {
                        usernameInput.focus();
                    } else if (this._organizationIdInput.val() === "") {
                        this._organizationIdInput.focus();
                    }
                }
            };
        }

        loginBox.initialize(jrsConfigs.loginState);

        if (isIPad()) {
            var orientation = window.orientation;
            switch (orientation) {
                case 0:
                    jQuery('#welcome').get(0).style.webkitTransform = 'scale(0.8) translate3d(-60px,0,0)';
                    jQuery('h2.textAccent').css('font-size', '14px').parent().css('width', '39%');
                    jQuery('#copy').css('width', '600px');
                    jQuery('#loginForm').css({
                        left: '524px',
                        right: ''
                    });
                    break;
                case 90:
                    jQuery('#welcome').get(0).style.webkitTransform = 'scale(1.0) translate3d(0,0,0)';
                    jQuery('h2.textAccent').css('font-size', '16px').parent().css('width', '46%');
                    jQuery('#copy').css('width', '766px');
                    break;
                case -90:
                    jQuery('#welcome').get(0).style.webkitTransform = 'scale(1.0) translate3d(0,0,0)';
                    jQuery('h2.textAccent').css('font-size', '16px').parent().css('width', '46%');
                    jQuery('#copy').css('width', '766px');
                    break;
            }
            jQuery('#frame').show();
            window.addEventListener('orientationchange', function(e) {
                var orientation = window.orientation;
                switch (orientation) {
                    case 0:
                        jQuery('#welcome').get(0).style.webkitTransform = 'scale(0.75) translate3d(-60px,0,0)';
                        jQuery('h2.textAccent').css('font-size', '14px').parent().css('width', '39%');
                        jQuery('#copy').css('width', '600px');
                        jQuery('#loginForm').css({
                            left: '524px',
                            right: ''
                        });
                        break;
                    case 90:
                        jQuery('#welcome').get(0).style.webkitTransform = 'scale(1.0) translate3d(0,0,0)';
                        jQuery('h2.textAccent').css('font-size', '16px').parent().css('width', '46%');
                        jQuery('#copy').css('width', '766px');
                        jQuery('#loginForm').css({
                            left: '',
                            right: '-10px'
                        });
                        break;
                    case -90:
                        jQuery('#welcome').get(0).style.webkitTransform = 'scale(1.0) translate3d(0,0,0)';
                        jQuery('h2.textAccent').css('font-size', '16px').parent().css('width', '46%');
                        jQuery('#copy').css('width', '766px');
                        jQuery('#loginForm').css({
                            left: '',
                            right: '-10px'
                        });
                        break;
                }
            });
        }
    });
});
