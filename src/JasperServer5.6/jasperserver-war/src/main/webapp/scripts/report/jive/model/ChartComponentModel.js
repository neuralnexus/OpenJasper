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
 * @author: nesterone
 * @version: $Id: ChartComponentModel.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BaseComponentModel = require("./BaseComponentModel"),
        jiveTypes = require("../enum/jiveTypes"),
        interactiveComponentTypes = require("../enum/interactiveComponentTypes"),
        _ = require("underscore"),
        reportEvents = require("../../enum/reportEvents"),
        reportCreators = require("../../enum/reportCreators");

    return BaseComponentModel.extend({
        defaults: function() {
            return {
                charttype: undefined,
                datetimeSupported: false,
                hcinstancedata: undefined,
                id: undefined,
                interactive: true,
                module: "jive.highcharts",
                type: jiveTypes.CHART,
                uimodule: "jive.interactive.highchart"
            };
        },

        api: {
            changeType: {}
        },

        actions: {
            "change:charttype": function() {
                return {
                    actionName: 'changeChartType',
                    changeChartTypeData: {
                        chartComponentUuid: this.get("id"),
                        chartType: this.get("charttype")
                    }
                }
            }
        },

        initialize: function(){
            if (this.has("hcinstancedata")){
                var hcinstancedata = this.get("hcinstancedata"),
                    creator = this._detectCreator(hcinstancedata);
                if (reportCreators.AD_HOC_DESIGNER === creator){
                    //workaround to stretch adhoc's
                    delete hcinstancedata.width;
                    delete hcinstancedata.height;
                }
            }
            // JSON.parse(JSON.stringify(.... deep clone.
            // JR services are changing model by initialization, therefore deep clone is done to avoid data corruption
            // should be replaced with true deep clone if JRS-1450 implemented
            this.config = JSON.parse(JSON.stringify(this.toJSON()));
        },

        showTypeError: function() {
            this.get("uiModuleType").showTypeError();
        },

        changeType: function(parms) {
            this.trigger(reportEvents.ACTION, {
                actionName: 'changeChartType',
                changeChartTypeData: {
                    chartComponentUuid: this.config.id,
                    chartType: parms.type
                }
            });
        },

        _detectCreator: function(hcInstance){
            var services = hcInstance.services,
                isCreatedFromAdhoc = _.some(services, function (info) {
                    return info.service.indexOf("adhoc") != -1;
                }),
                creator;

            if (isCreatedFromAdhoc) {
                creator = reportCreators.AD_HOC_DESIGNER;
            }

            if (creator){
                this.set("creator", creator);
            }

            return creator;
        },


        toReportComponentObject: function() {
            if (!this.get("interactive")) {
                return undefined;
            }

            return {
                id: this.get("id"),
                componentType: interactiveComponentTypes.CHART,
                chartType: this.get("charttype"),
                name: this.get("name")
            };
        },

        updateFromReportComponentObject: function(obj) {
            this.set({ charttype: obj.chartType });
        }
    });
});

