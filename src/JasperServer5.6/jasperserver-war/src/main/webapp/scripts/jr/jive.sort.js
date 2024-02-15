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
 * @version $Id: jive.sort.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery"], function($){
    var Sort = function(config) {
        this.config = config;
        this.parent = null;
        this.loader = null;

        this.events = {
            ACTION_PERFORMED: "action",
            BEFORE_ACTION_PERFORMED: "beforeAction"
        };
    };

    Sort.prototype = {
        sort: function(parms) {
            var it = this,
                payload = {
                    action: this.config.sortData
                };
            payload.action.sortData.tableUuid = it.config.datasetUuid;
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            return this.loader.runAction(payload).then(function(jsonData) {
                it._notify({
                    name: it.events.ACTION_PERFORMED,
                    type: "sortica",
                    data: jsonData
                });
                return it;
            });
        },
        filter: function(parms) {
            var it = this,
                filterParms = $.extend({}, it.config.filterData, parms),
                payload = {
                    action: {
                        actionName: 'filterica',
                        filterData: filterParms
                    }
                };
            it._notify({name: it.events.BEFORE_ACTION_PERFORMED});
            return this.loader.runAction(payload).then(function(jsonData) {
                it._notify({
                    name: it.events.ACTION_PERFORMED,
                    type: "filterica",
                    data: jsonData
                });

                return it;
            });
        },

        // internal functions
        _notify: function(evt) {
            // bubble the event
            this.parent._notify(evt);
        }
    }

    return Sort;
});