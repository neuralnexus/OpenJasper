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

import _ from 'underscore';
import Dialog from './Dialog';
import confirmDialogTemplate from './template/confirmDialogTemplate.htm';
import i18n from '../../../i18n/CommonBundle.properties';
export default Dialog.extend({
    constructor: function (options) {
        options || (options = {});
        this.confirmDialogTemplate = options.confirmDialogTemplate || _.template(confirmDialogTemplate);
        Dialog.prototype.constructor.call(this, {
            modal: true,
            additionalCssClasses: options.additionalCssClasses || 'confirmationDialog',
            title: options.title || i18n['dialog.confirm.title'],
            content: this.confirmDialogTemplate({ text: options.text }),
            buttons: [
                {
                    label: options.yesLabel || i18n['button.yes'],
                    action: 'yes',
                    primary: true
                },
                {
                    label: options.noLabel || i18n['button.no'],
                    action: 'no',
                    primary: false
                }
            ]
        });
    },
    initialize: function () {
        Dialog.prototype.initialize.apply(this, arguments);
        this.on('button:yes', this.close);
        this.on('button:no', this.close);
    },
    setContent: function (content) {
        Dialog.prototype.setContent.call(this, this.confirmDialogTemplate({ text: content }));
    }
});