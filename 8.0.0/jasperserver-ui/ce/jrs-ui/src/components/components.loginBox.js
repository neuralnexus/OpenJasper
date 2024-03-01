/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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


/**
 * @version: $Id$
 */
import webHelpModule from './components.webHelp';
import dialogs from './components.dialogs';
import layoutModule from '../core/core.layout';
import {ValidationModule} from "../util/utils.common";
import jQuery from 'jquery';

var loginBox = {
    LOGIN_BOX_TEMPLATE_DOM_ID: "login",

    DOCUMENTATION_BUTTON_ID: "documentationButton",
    GOTO_JASPERFORGE_BUTTON_ID: "gotoJasperForge",
    CONTACT_SALES_BUTTON_ID: "contactSalesButton",
    NEED_HELP_LINK_ID: "needHelp",

    NEED_HELP_DIALOG_ID: "helpLoggingIn",

    CONTACT_SALES_URL: "http://www.jaspersoft.com/contact-us",

    _dom: null,

    _baseInitialize: function(options) {
        this._initVars(options);

        this._processTemplate();
        this._initHandlers();

        if (this._warningMessage) {
            jQuery(this._customError).html(this._warningMessage)[0];
            jQuery(this._customError).removeClass("hidden");
        }

        if (this._passwordExpiredDays) {
            jQuery(this._passwordExpiredDays).val(this._passwordExpirationInDays);
        }
    },

    initialize: function(options) {
        this._baseInitialize(options);
    },

    _baseInitVars: function(options) {
        this._showLocaleMessage = options.showLocaleMessage;
        this._hideLocaleMessage = options.hideLocaleMessage;
        this._changePasswordMessage = options.changePasswordMessage;
        this._cancelPasswordMessage = options.cancelPasswordMessage;

        this._allowUserPasswordChange = options.allowUserPasswordChange;
        this._showPasswordChange = options.showPasswordChange;
        this._allowedPasswordPattern = new RegExp(options.allowedPasswordPattern);

        this._passwordExpirationInDays = options.passwordExpirationInDays;

        this._nonEmptyPasswordMessage = options.nonEmptyPasswordMessage;
        this._passwordNotMatchMessage = options.passwordNotMatchMessage;
        this._passwordNotMatchMessage = options.passwordNotMatchMessage;
        this._passwordTooWeakMessage = options.passwordTooWeakMessage;

        this._warningMessage = options.warningMessage;
    },

    _initVars: function(options) {
        this._baseInitVars(options);
    },

    _baseProcessTemplate: function() {
        this._dom = jQuery('#' + this.LOGIN_BOX_TEMPLATE_DOM_ID)[0];

        this._usernameInput = jQuery('#j_username')[0];
        this._passwordInput = jQuery('#j_password')[0];
        this._showHideLocaleAndTimezone = jQuery('#showHideLocaleAndTimezone')[0];
        this._localeAndTimeZone = jQuery('#localeAndTimeZone')[0];
        this._userLocale = jQuery('#userLocale')[0];

        this._changePassword = jQuery('#changePassword')[0];
        this._j_newpassword1 = jQuery('#j_newpassword1')[0];
        this._j_newpassword2 = jQuery('#j_newpassword2')[0];
        this._showHideChangePassword = jQuery('#showHideChangePassword')[0];
        this._passwordExpiredDays = jQuery(this._dom).find('input[name="passwordExpiredDays"]')[0];

        this._customError = jQuery("#customError")[0];

        this._loginForm = jQuery( this._dom).parent('form');;

        this.documentationButton = jQuery('#' + this.DOCUMENTATION_BUTTON_ID)[0];
        this.gotoJasperForge = jQuery('#' + this.GOTO_JASPERFORGE_BUTTON_ID)[0];
        this.needHelpLink = jQuery('#' + this.NEED_HELP_LINK_ID)[0];

        this.needHelpDialog = jQuery('#' + this.NEED_HELP_DIALOG_ID)[0];
    },

    _processTemplate: function() {
        this._baseProcessTemplate();
    },

    _initHandlers: function() {
        this._showHideLocaleAndTimezone.on('click', this._localeAndTimezoneShowHideHandler.bindAsEventListener(this));

        if(this._allowUserPasswordChange) {
            this._showHideChangePassword.on('click', this._changePasswordShowHideHandler.bindAsEventListener(this));
            // use jQuery to work with login.js
            jQuery(this._loginForm).on('submit', this._submitValidateHandler.bind(this));
        }

        if (this._showPasswordChange) {
            this._changePasswordShowHideHandler();
        }

        //web help
        if (window.webHelpModule) {
            this.documentationButton && this.documentationButton.on("click", function(e) {
                webHelpModule.displayWebHelp();
            }.bindAsEventListener(this));
        }

        this.gotoJasperForge && this.gotoJasperForge.on("click", function(e) {
            var url = "http://jasperforge.org";
            window.name = "";
            var runPopup=window.open(url, "jasperforge.org");
            runPopup.focus();
        }.bindAsEventListener(this));

        this.needHelpLink.on("click", function(e) {
            dialogs.popup.show(this.needHelpDialog);
        }.bindAsEventListener(this));

        var loginDialogs = [this.needHelpDialog];

        loginDialogs.each(function(dialog) {
            jQuery(dialog).find(layoutModule.BUTTON_PATTERN).on("click", function(e) {
                dialogs.popup.hide(dialog);
            });
        });
    },

    _submitValidateHandler: function(event) {
        jQuery(this._customError).addClass("hidden");

        if (!jQuery(this._changePassword).hasClass("hidden")) {
            var isValid = ValidationModule.validate([
                {
                    validator: this._emptyPasswordValidator.bind(this),
                    element: this._j_newpassword1
                },
                {
                    validator: this._emptyPasswordValidator.bind(this),
                    element: this._j_newpassword2
                },
                {
                    validator: this._confirmationPasswordNotMatchValidator.bind(this),
                    element: this._j_newpassword2
                },
                {
                    validator: this._confirmationPasswordTooWeakValidator.bind(this),
                    element: this._j_newpassword1
                }
            ]);

            if (!isValid) {
                event.preventDefault();
            }
        }
    },

    _emptyPasswordValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value.blank()) {
            isValid = false;
            errorMessage = this._nonEmptyPasswordMessage;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _confirmationPasswordNotMatchValidator: function(value) {
        var isValid = true;
        var errorMessage = "";

        if (value != this._j_newpassword1.getValue()) {
            isValid = false;
            errorMessage = this._passwordNotMatchMessage;
        }

        return {
            isValid: isValid,
            errorMessage: errorMessage
        };
    },

    _confirmationPasswordTooWeakValidator: function(value) {
        return {
            isValid: this._allowedPasswordPattern.test(value),
            errorMessage: this._passwordTooWeakMessage
        }
    },

    _changePasswordShowHideHandler: function() {
        jQuery(this._changePassword).toggleClass("hidden");

        if (jQuery(this._changePassword).hasClass("hidden")) {
            jQuery(this._showHideChangePassword).html(this._changePasswordMessage)[0];
            this._j_newpassword1.setValue("");
            this._j_newpassword2.setValue("");
        } else {
            jQuery(this._showHideChangePassword).html(this._cancelPasswordMessage)[0];
            this._j_newpassword1.focus();
        }
    },

    _localeAndTimezoneShowHideHandler: function() {
        jQuery(this._localeAndTimeZone).toggleClass("hidden");

        if (jQuery(this._localeAndTimeZone).hasClass("hidden")) {
            jQuery(this._showHideLocaleAndTimezone).html(this._showLocaleMessage)[0];
        } else {
            jQuery(this._showHideLocaleAndTimezone).html(this._hideLocaleMessage)[0];
            this._userLocale.focus();
        }
    }
};

export default loginBox;
