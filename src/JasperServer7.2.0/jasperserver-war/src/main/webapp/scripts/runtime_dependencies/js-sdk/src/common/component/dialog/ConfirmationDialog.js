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


/**
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require('underscore'),
        Dialog = require("./Dialog"),
        confirmDialogTemplate = require("text!./template/confirmDialogTemplate.htm"),
        i18n = require('bundle!js-sdk/CommonBundle');

    return Dialog.extend(/** @lends ConfirmationDialog.prototype */{
        /**
         * @constructor ConfirmationDialog
         * @extends Dialog
         * @classdesc ConfirmationDialog component.
         * @param {object} options
         * @param {string} options.text Message for dialog
         * @param {string} options.title Title for dialog
         * @fires ConfirmationDialog#button:yes
         * @fires ConfirmationDialog#button:no
         */
        constructor: function(options) {
            options || (options = {});

            this.confirmDialogTemplate = _.template(confirmDialogTemplate);

            Dialog.prototype.constructor.call(this, {
                modal: true,
                additionalCssClasses: options.additionalCssClasses || "confirmationDialog",
                title: options.title || i18n["dialog.confirm.title"],
                content: this.confirmDialogTemplate({ text: options.text }),
                buttons: [
                    { label: options.yesLabel || i18n["button.yes"], action: "yes", primary: true },
                    { label: options.noLabel || i18n["button.no"], action: "no", primary: false }
                ]
            });
        },

        initialize: function() {
            Dialog.prototype.initialize.apply(this, arguments);

            /**
             * @event ConfirmationDialog#button:no
             */
            /**
             * @event ConfirmationDialog#button:yes
             */
            this.on("button:yes", this.close);
            this.on("button:no", this.close);
        },

        setContent: function(content) {
            Dialog.prototype.setContent.call(this, this.confirmDialogTemplate({text: content}));
        }
    });
});
