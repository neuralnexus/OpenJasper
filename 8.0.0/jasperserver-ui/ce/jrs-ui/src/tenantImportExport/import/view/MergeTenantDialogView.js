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
import i18n from 'js-sdk/src/i18n/CommonBundle.properties';
import i18n2 from '../../../i18n/ImportExportBundle.properties';
import BaseWarningDialogView from '../../view/BaseWarningDialogView';
import template from '../template/mergeTenantDialogTemplate.htm';

export default BaseWarningDialogView.extend({
    constructor: function (options) {
        options || (options = {});
        _.extend(options, {
            resizable: true,
            additionalCssClasses: 'merge-tenant-dialog',
            title: i18n2['import.dialog.merge.tenant.title'],
            buttons: [
                {
                    label: i18n2['import.button.continue'],
                    action: 'import',
                    primary: true
                },
                {
                    label: i18n['button.cancel'],
                    action: 'cancel',
                    primary: false
                }
            ],
            template: template
        });
        BaseWarningDialogView.prototype.constructor.call(this, options);
        this.on('button:import', _.bind(this.close, this));
        this.on('button:cancel', _.bind(this.close, this));
    },
    open: function (options) {
        _.extend(options, { i18n: i18n2 });
        BaseWarningDialogView.prototype.open.call(this, options);
    }
});