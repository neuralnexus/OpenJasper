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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: ConditionModel.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        jiveDataConverter = require("../util/jiveDataConverter");

    return Backbone.Model.extend({
        defaults: function() {
            return {
                "operator": undefined,
                "value": undefined,
                "backgroundColor": null,
                "font": {
                    "bold": false,
                    "italic": false,
                    "underline": false,
                    "color": "000000"
                }
            }
        },

        parse: function(jiveObj) {
            var valueAndOperator = jiveDataConverter.operatorAndValueToSchemaFormat(
                jiveObj.conditionTypeOperator, this.collection.dataType, jiveObj.conditionStart, jiveObj.conditionEnd);

            return {
                operator: valueAndOperator.operator,
                value: valueAndOperator.value,
                backgroundColor: jiveObj.conditionMode === "Transparent"
                    ? "transparent"
                    : jiveObj.conditionFontBackColor || null,
                font: {
                    "italic": jiveObj.conditionFontItalic || false,
                    "bold": jiveObj.conditionFontBold || false,
                    "underline": jiveObj.conditionFontUnderline || false,
                    "color": jiveObj.conditionFontColor || "000000"
                }
            };
        },

        toJiveFormat: function(genericProperties) {
            return {
                "conditionStart": jiveDataConverter.filterStartValue(this.get("operator"), this.get("value"), this.collection.dataType, genericProperties),
                "conditionEnd": jiveDataConverter.filterEndValue(this.get("operator"), this.get("value"), this.collection.dataType, genericProperties),
                "conditionTypeOperator": jiveDataConverter.schemaFormatOperatorToFilterOperator(this.get("operator"), this.get("value"), this.collection.dataType),
                "conditionFontBold": this.get("font").bold || false,
                "conditionFontItalic": this.get("font").italic || false,
                "conditionFontUnderline": this.get("font").underline || false,
                "conditionFontColor": this.get("font").color || "000000",
                "conditionFontBackColor": this.get("backgroundColor") === "transparent" ? null : this.get("backgroundColor") || null,
                "conditionMode": this.get("backgroundColor") === "transparent" ? "Transparent" : "Opaque"
            }
        }
    });
});
