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
 * @version: $Id: export.formmodel.js 47331 2014-07-18 09:13:06Z kklein $
 */

JRS.Export.FormModel = (function (Export, jQuery, _, Backbone, exportErrorHandlerTrait, State) {


    return Backbone.Model
        .extend(exportErrorHandlerTrait)
        .extend({

            defaults:{
                "roles":null,
                "users":null,
                "uris":null,
                "fileName":"export.zip",
                "everything" : true,
                "includeSystemProperties" : false,
                "userForRoles": false,
                "rolesForUser": false,
                "includeAccessEvents" : false,
                "includeAuditEvents" : false,
                "includeMonitoringEvents" : false,
                "includeRepositoryPermissions" : true,
                "includeReportJobs" : true,
                "state" :_.extend(State.instance({urlTemplate: "rest_v2/export/{id}/state"}), exportErrorHandlerTrait)
            },

            initialize: function(){
                _.bindAll(this);
            },

            url: function () {
                return "rest_v2/export"
            },

        save:function () {
            var tmpModel = new Backbone.Model();

            tmpModel.url = this.url;

            tmpModel.save(this.prepareServerObject(), {
                success:this.updateState,
                error:this.defaultErrorDelegator
            });
        },

        isAcceptable: function(){
            var state = this.prepareServerObject();
            return state.uris || state.roles || state.users || _.filter(state.parameters, function(param){
                return param !== "role-users" && param !== "users-roles";
            }).length;
        },

        prepareServerObject: function() {
            return {
                roles:(_.isArray(this.get("roles")) && this.get("roles").length) ? this.get("roles") : null,
                users:(_.isArray(this.get("users")) && this.get("users").length) ? this.get("users") : null,
                uris:_.clone(this.get("uris")),
                parameters:this.getConvertedParameters()
            };
        },

        validate:function (attrs) {
            if (!attrs.fileName.length) {
               return Export.i18n["file.name.empty"];
            }

            if (attrs.fileName.length > 256) {
               return Export.i18n["file.name.too.long"];
            }

            if ((/[\/\\?%\*:|"<>]+/).test(attrs.fileName)) {
               return Export.i18n["file.name.not.valid"];
            }
        },

        updateState: function(dto, xhr){
            var state = this.get("state");
            if (state.get("phase") === State.NOT_STARTED){
                state.set({id: dto.get("id")});
                state.name = this.get("fileName");
            }
            state.set({phase: dto.get("phase"), message: dto.get("message")});
        },

        getConvertedParameters : function(){
            var that = this;

            var isUris = this.get("uris");
            isUris = isUris && isUris.length;

            var isEverything = this.get("everything");

            var results = _(this.attributes)
                .chain()
                .keys()
                .filter(function(elemKey){
                    return elemKey !== "roles"
                               && elemKey !== "users"
                               && elemKey !== "roles"
                               && elemKey != "uris"
                               && elemKey != "state"
                               && elemKey != "fileName"
                               && elemKey != "hasReports"
                               && that.get(elemKey)
                })
                .filter(function(elemKey){
                        return isEverything ? isEverything : elemKey != "includeAccessEvents";
                })
                .filter(function(elemKey){
                        return isUris ? isUris :
                                elemKey != "includeRepositoryPermissions" && elemKey != "includeReportJobs"
                })
                .map(function(val){
                    if (val === "userForRoles"){
                        return "role-users"
                    }else if(val === "rolesForUser"){
                        return "users-roles";
                    }else if(val === "includeSystemProperties"){
                        return "include-server-settings";
                    }else if (val === "includeAccessEvents"){
                        return "include-access-events"
                    }else if (val === "includeAuditEvents"){
                        return "include-audit-events";
                    }else if (val === "includeRepositoryPermissions"){
                        return "repository-permissions";
                    }else if (val === "includeReportJobs"){
                        return "report-jobs";
                    }else if (val === "includeMonitoringEvents"){
                        return "include-monitoring-events";
                    }else{
                        return val;
                    }
                })
            .value();
            return results ? results : [];
        }

    });
})(
    __jrsConfigs__.Export,
    jQuery,
    _,
    Backbone,
    JRS.Export.ServerErrorTrait,
    jaspersoft.components.State
);