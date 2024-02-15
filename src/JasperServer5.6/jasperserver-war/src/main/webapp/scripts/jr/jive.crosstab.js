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
 * @version $Id: jive.crosstab.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery"], function($) {
    var Crosstab = function(o) {
        this.config = {};
        $.extend(this.config, o);

        this.parent = null;
        this.loader = null;
    };

    Crosstab.prototype = {
        getId: function() {
            return this.config.id;
        },
        getFragmentId: function() {
            return this.config.fragmentId;
        },
        sortRowGroup: function(groupIndex, order) {
            var it = this;
            var payload = {
                action: {"actionName":"sortXTabRowGroup",
                    "sortData":{
                        "crosstabId":this.getId(),
                        "order":order,
                        "groupIndex":groupIndex}}};
            return this.loader.runAction(payload).then(function(jsonData) {
                it._notify({
                    name: "action",
                    type: "sortXTabByColumn",
                    data: jsonData});

                return it;
            });
        },
        isDataColumnSortable: function(columnIndex) {
            var dataColumn = this.config.dataColumns[columnIndex - this.config.startColumnIndex];
            return typeof(dataColumn.sortMeasureIndex) == "number";
        },
        getColumnOrder: function(columnIndex) {
            return this.config.dataColumns[columnIndex - this.config.startColumnIndex].order;
        },
        sortByDataColumn: function(columnIndex, order) {
            var it = this;
            var dataColumn = this.config.dataColumns[columnIndex - this.config.startColumnIndex];
            var payload = {
                action: {"actionName":"sortXTabByColumn",
                    "sortData":{
                        "crosstabId":this.getId(),
                        "order":order,
                        "measureIndex": dataColumn.sortMeasureIndex,
                        "columnValues": dataColumn.columnValues}}};
            return this.loader.runAction(payload).then(function(jsonData) {
                it._notify({
                    name: "action",
                    type: "sortXTabByColumn",
                    data: jsonData});

                return it;
            });
        },

        // internal functions
        /**
         * @param evt {object} The event object: {type, name, data}
         */
        _notify: function(evt) {
            // bubble the event
            this.parent._notify(evt);
        }
    };

    return Crosstab;
});