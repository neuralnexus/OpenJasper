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
import BackboneValidation from '../../../extension/backboneValidationExtension';
import i18n from '../../../../i18n/CommonBundle.properties';
import i18nMessageUtil from "../../../util/i18nMessage";
import _ from 'underscore';

let i18nMessage = i18nMessageUtil.extend({ bundle: i18n });

var PaginationModel = Backbone.Model.extend({
    validation: {
        step: [{
            min: 1,
            msg: new i18nMessage('error.pagination.property.min.value', 'step', 1)
        }],
        current: [
            {
                integerNumber: true,
                msg: new i18nMessage('error.pagination.property.integer.value', 'current', 1)
            },
            {
                min: 1,
                msg: new i18nMessage('error.pagination.property.min.value', 'current', 1)
            },
            {
                fn: function (value) {
                    if (value > this.get('total')) {
                        return new i18nMessage('error.pagination.property.max.value', 'current', value);
                    }
                }
            }
        ],
        total: [{
            min: 1,
            msg: new i18nMessage('error.pagination.property.min.value', 'total', 1)
        }]
    },
    defaults: {
        step: 1,
        current: 1,
        total: 1
    }
});
_.extend(PaginationModel.prototype, BackboneValidation.mixin);
export default PaginationModel;