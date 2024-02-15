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
 * @version: $Id: export.extendedformview.js 47331 2014-07-18 09:13:06Z kklein $
 */

JRS.Export.ExtendedFormView = (function (exportz, jQuery, _, Backbone, templateEngine, AuthorityModel, AuthorityPickerView, State) {
    var isIE = navigator.userAgent.toLowerCase().indexOf("msie") > -1;
    return Backbone.View.extend({

        events:{
            "keyup #exportDataFile #filenameId":"editFileName",
            "change #exportOptions #everything":"exportEverything",
            "change #exportOptions #includeSystemProperties":"includeSystemProperties",
            "change #exportOptions #roleUsers":"includeRolesByUsers",
            "change #exportOptions #usersRoles":"includeUsersWithRoles",
            "change #exportOptions #noUsersRoles":"includeSelectedRolesUsersOnly",
            "change #exportOptions #includeAccessEvents":"includeAccessEvents",
            "change #exportOptions #includeAuditEvents":"includeAuditEvents",
            "change #exportOptions #includeMonitoringEvents ":"includeMonitoringEvents",
            "click #exportOptions .checkBox label":"clickOnCheckbox",
            "click #exportButton":"sendParameters"

        },

        initialize:function () {
            _.bindAll(this);

            this.rolesList = new AuthorityPickerView({
                model:AuthorityModel.instance("rest_v2/roles{{#searchString}}?search={{searchString}}{{/searchString}}"),
                customClass:"selectedRoles",
                title:exportz.i18n["export.select.roles"]
            });

            this.rolesList.on("change:selection", this.bindWithRoles);
            this.rolesList.render();

            this.usersList = new AuthorityPickerView({
                model:AuthorityModel.instance("rest_v2/users{{#searchString}}?search={{searchString}}{{/searchString}}"),
                customClass:"select selectedUsers",
                title:exportz.i18n["export.select.users"]
            });

            this.usersList.on("change:selection", this.bindWithUsers);
            this.usersList.render();

            this.rolesToUsers =  AuthorityModel.instance("rest_v2/users?hasAllRequiredRoles=false{{#roles}}&requiredRole={{.}}{{/roles}}");
            this.rolesToUsers.on("change", this.onRolesToUsersChange);

            this.usersToRoles =  AuthorityModel.instance("rest_v2/roles?hasAllUsers=false{{#users}}&user={{.}}{{/users}}");
            this.usersToRoles.on("change", this.onUsersToRolesChange);

            this.fileTemplate = templateEngine.createTemplate("exportDataFileTemplate");
            this.optionsTemplate = templateEngine.createTemplate("exportOptionsTemplates");
            this.controlsTemplate = templateEngine.createTemplate("controlButtonsTemplate");

            this.model.on("change", this.onModelChange);
        },

        render:function (options) {
            var fileControlHtml = this.fileTemplate({defaultFileName:this.model.get('fileName')});
            var optionsHtml = this.optionsTemplate({
                everything:this.model.get("everything"),
                includeSystemProperties:this.model.get("includeSystemProperties"),
                userForRoles:this.model.get("userForRoles"),
                rolesForUser:this.model.get("rolesForUser"),
                includeAccessEvents:this.model.get("includeAccessEvents"),
                includeAuditEvents:this.model.get("includeAuditEvents")
            });
            var controlsHtml = this.controlsTemplate();

            if (options && options.container) {
                this.undelegateEvents();
                this.$el = jQuery(options.container);
                this.el = this.$el[0];
                this.$el.find(".body").append(fileControlHtml + optionsHtml);
                this.$el.find(".footer").prepend(controlsHtml);
                this.delegateEvents();
            } else {
                this.$el.html(fileControlHtml + optionsHtml + controlsHtml);
            }

            this.$el.find("#selectRolesUsers").append(this.rolesList.el).append(this.usersList.el);
            // IE10 browser has a nice thing - a cross at the end of the input control.
            // also, in cases then user may find the way how to alter input with the mouse, we listen for this type of event
            isIE && (this.$el.find("#exportDataFile #filenameId").on("propertychange input",this.editFileName));

            this.changeEnabledState(this.model.get("everything"));

            return this;
        },

        bindWithRoles : function(selection){
            this.model.set({roles:selection});
            this.onUsersToRolesChange();
            if (selection.length) {
                this.rolesToUsers.setContext({roles: selection});
            } else {
                this.rolesToUsers.set({items:[]});
            }
        },

        bindWithUsers : function(selection){
            this.model.set({users:selection});
            this.onRolesToUsersChange();
            if (selection.length) {
                this.usersToRoles.setContext({users: selection});
            } else {
                this.usersToRoles.set({items:[]});
            }
        },

        changeEnabledState : function(value){
            this.rolesList.setDisabled(value || this.model.get("rolesForUser"));
            this.usersList.setDisabled(value || this.model.get("userForRoles"));

            this.$el.find("#roleUsers").prop("disabled", value);
            this.$el.find("#usersRoles").prop("disabled", value);
            this.$el.find("#noUsersRoles").prop("disabled", value);

            this.$el.find("#includeAccessEvents").prop("disabled", !value).prop("checked", this.model.get("includeAccessEvents"));
        },

        editFileName:function (evt) {
            var edit = jQuery(evt.target);
            var button =  this.$el.find("#exportButton").prop("disabled", false);
            edit.parent().removeClass("error");
            this.model.once("invalid", function (model, error) {
                edit.next().html(error);
                edit.parent().addClass("error");
                button.prop("disabled", true);
            });
            this.model.set("fileName", edit.val(), { validate : true });
        },

        exportEverything:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({everything:val});
            this.changeEnabledState(val);
            if (val){
                this.rolesList.selectNone();
                this.usersList.selectNone();
            }
        },

        includeSystemProperties:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({includeSystemProperties:val});
        },

        includeRolesByUsers:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({rolesForUser:!val, userForRoles: val});
            this.onRolesToUsersChange();
            this.rolesList.setDisabled(!val);
            this.usersList.setDisabled(val);
            this.rolesList.selectNone();
            this.usersList.selectNone();
        },

        includeUsersWithRoles:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({rolesForUser:val, userForRoles: !val});
            this.onUsersToRolesChange();
            this.rolesList.setDisabled(val);
            this.usersList.setDisabled(!val);
            this.rolesList.selectNone();
            this.usersList.selectNone();
        },

        includeSelectedRolesUsersOnly:function (evt) {
            if (jQuery(evt.target).is(":checked")){
                this.model.set({userForRoles:false, rolesForUser: false});
                this.onRolesToUsersChange();
                this.onUsersToRolesChange();
                this.rolesList.setDisabled(false);
                this.usersList.setDisabled(false);
                this.rolesList.selectNone();
                this.usersList.selectNone();
            }
        },

        includeAccessEvents:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({includeAccessEvents:val});
        },

        includeAuditEvents:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({includeAuditEvents:val});
        },

        includeMonitoringEvents:function (evt) {
            var val = jQuery(evt.target).is(":checked");
            this.model.set({includeMonitoringEvents:val});
        },

        sendParameters:function (evt) {
            if (this.model.isValid() && this.model.isAcceptable() && this.isValid()) {
                this.model.save();
            }
        },

        clickOnCheckbox: function(evt){
            var checkbox = jQuery(evt.target).next();
            if (!checkbox[0].disabled) {
                checkbox[0].checked = !checkbox[0].checked;
                checkbox.trigger("change");
            }
        },

        isValid: function(){
            return !this.$el.find("fieldset .error").length;
        },

        onModelChange: function() {
            var button = this.$el.find("#exportButton");
            button.prop("disabled", !(this.model.isAcceptable() && this.isValid()));
        },

        onRolesToUsersChange: function() {
            if (this.model.get("userForRoles")) {
                this.usersList.highlightSet(_.map(this.rolesToUsers.get("items"), function(item) {
                    return item.username + (item.tenantId ? "|" + item.tenantId : "")
                }));
                this.rolesList.highlightSet([]);
            } else {
                this.usersList.highlightSet([]);
            }
        },

        onUsersToRolesChange: function() {
            if (this.model.get("rolesForUser")) {
                this.rolesList.highlightSet(_.map(this.usersToRoles.get("items"), function(item) {
                    return item.name + (item.tenantId ? "|" + item.tenantId : "")
                }));
                this.usersList.highlightSet([]);
            } else {
                this.rolesList.highlightSet([]);
            }
        }
    })

})(
    JRS.Export,
    jQuery,
    _,
    Backbone,
    jaspersoft.components.templateEngine,
    jaspersoft.components.AuthorityModel,
    jaspersoft.components.AuthorityPickerView,
    jaspersoft.components.State
);