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
import tooltipPlacements from '../enum/tooltipPlacements';
import tooltipTypesEnum from '../enum/tooltipTypesEnum';
import BackboneValidation from '../../../common/extension/backboneValidationExtension';
import _ from 'underscore';
import log from "../../../common/logging/logger";

var TooltipPopupModel = Backbone.Model.extend({
    defaults: {
        visible: false,
        defaultType: tooltipTypesEnum.INFO,
        type: tooltipTypesEnum.INFO,
        offset: {
            top: 0,
            left: 0
        },
        content: {
            title: undefined,
            text: undefined
        },
        placement: tooltipPlacements.BOTTOM,
        position: {
            top: 0,
            left: 0
        }
    },
    validation: {
        visible: { type: 'boolean' },
        content: { type: 'object' },
        type: { type: 'string' },
        offset: { type: 'object' }
    },
    initialize: function (options) {
        options = options || {};
        this.log = options.log || log;
        this.listenTo(this, 'invalid', function (model, message) {
            this.log.error(message);
        });
    }
});
_.extend(TooltipPopupModel.prototype, BackboneValidation.mixin);
export default TooltipPopupModel;