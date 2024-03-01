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

import Backbone from 'backbone';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import ImportView from './ImportView';
import importExportTypesEnum from '../../export/enum/exportTypesEnum';

export default Backbone.View.extend({
    events: { 'click #importButton': 'doImport' },
    initialize: function () {
        this.importView = new ImportView();
        this.importView.render({
            tenantId: jrsConfigs.isProVersion ? 'organizations' : null,
            type: jrsConfigs.isProVersion ? importExportTypesEnum.SERVER_PRO : importExportTypesEnum.SERVER_CE
        });
        this.$el.find('.body').append(this.importView.el);
        this.listenTo(this.importView.model, 'validated', function (isValid) {
            var $importButton = this.$('#importButton'), value = isValid ? null : 'disabled';
            $importButton.attr('disabled', value);
        }, this);
    },
    doImport: function () {
        this.importView.doImport();
    }
});