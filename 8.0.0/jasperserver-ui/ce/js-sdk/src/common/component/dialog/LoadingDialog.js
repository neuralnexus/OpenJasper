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
import confirmDialogTemplate from './template/loadingDialogTemplate.htm';
import i18n from '../../../i18n/CommonBundle.properties';
export default Dialog.extend({
    events: _.extend({}, Dialog.prototype.events, { 'click button': 'cancel' }),
    el: function () {
        return this.template({
            title: this.title,
            additionalCssClasses: this.additionalCssClasses,
            i18n: i18n
        });
    },
    constructor: function (options) {
        this.options = options || {};
        Dialog.prototype.constructor.call(this, {
            modal: true,
            additionalCssClasses: this.options.additionalCssClasses,
            template: confirmDialogTemplate,
            title: this.options.title || i18n['dialog.overlay.title']
        });
        if (this.options.showProgress) {
            this.progress = _.bind(function (progress) {
                if (arguments.length === 0) {
                    return +this.$('.percents').text();
                } else {
                    return this.$('.percents').text(progress + '%');
                }
            }, this);
            this.on('open', _.bind(this.progress, this, 0));
        }
    },
    initialize: function () {
        Dialog.prototype.initialize.apply(this, arguments);
        if (this.options.cancellable) {
            this.on('button:cancel', this.close);
        } else {
            this.$('button').hide();
        }
    },
    cancel: function () {
        this.trigger('button:cancel', this);
    }
});