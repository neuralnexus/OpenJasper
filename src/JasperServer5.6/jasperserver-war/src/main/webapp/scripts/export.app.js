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
 * @author: inesterenko
 * @version: $Id: export.app.js 47331 2014-07-18 09:13:06Z kklein $
 */

JRS.Export.App = (function(Export, $,_, components) {

//module:
//
//
//summary:
//
//
//
    return {

        initialize : function(args){
            _.bindAll(this);
            this.formModel = new Export.FormModel();
            var extendArguments = _.extend({
                model: this.formModel,
                namespace: Export
            }, args);
            var ui = this.ui = new components.Layout(extendArguments);
            var state = this.formModel.get("state");
            this.stateController = new Export.StateController({
                model: state,
                timeout: Export.configs.TIMEOUT,
                delay: Export.configs.DELAY
            });

            if (args && !args["containerID"]) {

                state.on("change:phase", function (model, phase) {
                    if (phase === components.State.READY) {
                        ui.dialog && ui.dialog.hide();
                    }
                });
            }

            $(document).ready(function(){
                ui.render(args);
            });
        },

        showDialogFor: function(data){
            this.formModel.set({uris: this.parseRepoData(data), includeReportJobs: this.hasReports(data)});
            this.ui.showDialog();
        },

        parseRepoData: function(data){
            if (_.isArray(data)){
                return _.pluck(data, "URIString");
            } else{
                return  [data.URIString];
            }
        },

        hasReports: function(data){
            _.isArray(data) || (data = [data]);
            return _.reduce(data, function(memo, item){
                return memo || !item.resourceType
                    || (item.resourceType.indexOf("ReportUnit")+1)
                    || (item.resourceType.indexOf("ReportOptions")+1)
            }, false);
        }

    }

})(
    JRS.Export,
    jQuery,
    _,
    jaspersoft.components
);
