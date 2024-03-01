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


/**
 * @author: Zakhar Tomchenko
 * @version:
 */

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import ExportModel from '../model/ExportModel';
import ExportStateModel from '../model/ExportStateModel';
import AuthorityModel from '../model/AuthorityPickerModel';
import AuthorityPickerView from './AuthorityPickerView';
import LoadingDialog from './LoadingDialog';
import ExportDependentResourcesDialogView from './ExportDependentResourcesDialogView';
import Notification from 'js-sdk/src/common/component/notification/Notification';
import epoxyViewMixin from 'js-sdk/src/common/view/mixin/epoxyViewMixin';
import exportTemplateFactory from '../factory/exportTemplateFactory';
import exportTypesEnum from '../enum/exportTypesEnum';
import i18n from '../../../i18n/ImportExportBundle.properties';
import i18n2 from 'js-sdk/src/i18n/CommonBundle.properties';

import awsSettings from '../../../settings/awsSettings.settings';
import secureKeyTypeEnum from "../../import/enum/secureKeyTypeEnum";
import CustomKeyModel from '../../model/CustomKeyModel';

var ExportView = Backbone.View.extend({
    className: "export-view",

    events: {
        "change .usersWithSelectedRoles": "includeRolesByUsers",
        "change .rolesWithSelectedUsers": "includeUsersWithRoles",
        "change .selectedUsersRoles": "includeSelectedRolesUsersOnly",
        "change input.jr-jDefaultKey, input.jr-jKeyValue, input.jr-jKeyFile , input.jr-juniversalKeyValue,  input.jr-jCustomKey": "_onKeyTypeChange",
        "click .section .checkBox label": "_clickOnCheckbox",
        "click button.jr-jRepositoryBrowserButton": "_onRepositoryBrowserButtonClick",
        "change .control.select.inline": "_onCustomKeyInput"
    },

    computeds: {
        includeAccessEventsChecked: function() {
            return this.model.get("everything") ? this.model.get("includeAccessEvents") : false;
        },

        enableAttributesValues: {
            deps: ["everything", "includeAttributes"],
            get: function(everything, includeAttributes) {
                return !everything && includeAttributes;
            }
        },

        enableReportJobs: {
            deps: ["everything", "includeReports"],
            get: function(everything, includeReports) {
                return !everything && includeReports;
            }
        },

        someResourceIsChecked: {
            deps: ["everything", "includeReports", "includeAdHocViews", "includeDashboards", "includeDomains", "includeDataSources", "includeOtherResourceFiles"],
            get: function(everything, includeReports, includeAdHocViews, includeDashboards, includeDomains, includeDataSources, includeOtherResourceFiles) {
                return !everything && (includeReports || includeAdHocViews || includeDashboards || includeDomains || includeDataSources || includeOtherResourceFiles);
            }
        },

        isKeyDefaultValue: {
            deps: ['keyType'],
            get: function(keyType) {
                return keyType === secureKeyTypeEnum.DEFAULTKEY;
            }
        },

        isKeyUniversalValue: {
            deps: ['keyType'],
            get: function(keyType) {
                return keyType === secureKeyTypeEnum.UNIVERSALKEY;
            }
        },

        isKeyUseCustom: {
            deps: ['keyType'],
            get: function(keyType) {
                return keyType === secureKeyTypeEnum.CUSTOMKEY;
            }
        }
    },

    initialize: function(options = {}) {
        _.bindAll(this, "render", "doExport", "handleExportPhase", "filterAuthoritiesBySubOrgs",
            "bindWithRoles", "bindWithUsers", "changeEnabledState", "includeRolesByUsers", "includeUsersWithRoles",
            "includeSelectedRolesUsersOnly", "sendParameters", "onRolesToUsersChange", "onUsersToRolesChange",
            "_isServerLevel", "_includeUsersOrRoles", "_showNotification", "_getBrokenDependencies",
            "_clickOnCheckbox", "_onModelChange", "_resetModel");

        this.model || (this.model = new ExportModel());
        this.stateModel = new ExportStateModel();
        this.customKeyStateModel = new CustomKeyModel();

        this.epoxifyView();

        this.loadingDialog = new LoadingDialog({
            content: i18n2["dialog.overlay.loading"]
        });

        this.notification = new Notification();

        this.dependentResourcesDialogView = new ExportDependentResourcesDialogView();

        this.listenTo(this.model, "change", this._onModelChange);
        this.listenTo(this.stateModel, "change:phase", this.handleExportPhase, this);

        this.listenTo(this.model, "error:unauthorized", window.location.reload);
        this.listenTo(this.stateModel, "error:unauthorized", window.location.reload);


        var notFoundNotification = {
                delay: false,
                message: i18n["export.error.cancelled"]
            },
            unexpectedErrorNotification = {
                delay: false,
                message: i18n["export.error.unexpected"]
            };

        this.listenTo(this.model, "error:notFound", _.bind(this.notification.show, this.notification, notFoundNotification));
        this.listenTo(this.stateModel, "error:notFound", _.bind(this.notification.show, this.notification, notFoundNotification));

        this.listenTo(this.model, "error:internalServerError", _.bind(this.notification.show, this.notification, unexpectedErrorNotification));
        this.listenTo(this.stateModel, "error:internalServerError", _.bind(this.notification.show, this.notification, unexpectedErrorNotification));

        this.listenTo(this.model, "error", _.bind(this.loadingDialog.close, this.loadingDialog));
        this.listenTo(this.stateModel, "error", _.bind(this.loadingDialog.close, this.loadingDialog));

        this.listenTo(this.dependentResourcesDialogView, "button:export", _.compose(
            _.bind(this.dependentResourcesDialogView.close, this.dependentResourcesDialogView),
            _.bind(function() {
                downloadFile.call(this);
                this._showNotification({message: i18n["export.finished"], type: "success"});
            }, this)));
        this.listenTo(this.dependentResourcesDialogView, "button:cancel", _.bind(cancelExportTask, this, ExportStateModel.STATE.CANCELLED));
    },
    _onRepositoryBrowserButtonClick: function() {
        this.keyFileDialog.open();
    },
    _clearSecureKeyErrors: function() {
        this.model.set({
            'invalidKeyError': '',
            'invalidSecureFileContentError': ''
        });
    },

    _onKeyTypeChange: function(event) {
        let radioValue = event.target.value;

        this.model.set('keyType', radioValue);

        this._clearSecureKeyErrors();
    },

    _onCustomKeyInput: function(evt){
        const index = evt.target.selectedIndex;
        const element = $(".control.select.inline").find('option')[index];
        this.model.set('keyAlias', element.value, {silent: true});
    },


    render: function(options) {
        options = options || {};

        this.type = options.type;

        var isServerType = this._isServerLevel(this.type),
            isServerOrRootType = isServerType || this.type === exportTypesEnum.ROOT_TENANT;

        isServerOrRootType
            ? this.$el.addClass("with-events")
            : this.$el.removeClass("with-events");

        this.stopListening(this.model, "change:includeSubOrganizations", this.filterAuthoritiesBySubOrgs);

        this._resetModel(options);

        let encryptionHint = i18n['export.dialog.file.properties.encryption.hint'];

        if (awsSettings.productTypeIsJrsAmi || awsSettings.productTypeIsMpAmi) {
            encryptionHint = i18n['export.dialog.file.properties.encryption.hint.aws'];
        }
        var self = this;
        this.customKeyStateModel.getCustomKeys().done(function(response) {
            if(response && response.responseJSON && response.responseJSON.error){
                customKeyFailure(self, options, encryptionHint, isServerOrRootType, isServerType, response);
            } else {
                response.length && self.model.set('keyAlias',response[0].alias);
                renderSecTemplate(self, options, encryptionHint, isServerOrRootType, isServerType, response);
                for (var i = 0; i < response.length; ++i) {
                    var element = $("<option />").attr("value", response[i].alias);
                    element.text(response[i].label ? response[i].label : response[i].alias);
                    self.$el.find('#importCustomKey').append(element);
                }
            }
        }).fail(function (response) {
            customKeyFailure(self, options, encryptionHint, isServerOrRootType, isServerType, response);
        });
        return this;
    },

    doExport: function() {
        var self = this;

        if (this.model.isValid(true)) {
            this.loadingDialog.open();
            this.model.save().done(function(status) {
                self.stateModel.unset("warnings", {silent: true});
                self.stateModel.set(status);
                self.model.set("id", status.id);
            });
        }
    },

    handleExportPhase: function() {
        var phase = this.stateModel.get("phase"),
            brokenDependencies = this._getBrokenDependencies(),
            notificationOptions;

        if (phase == ExportStateModel.STATE.READY) {
            this.loadingDialog.close();
            if (!_.isEmpty(brokenDependencies)) {
                this.dependentResourcesDialogView.open({items: brokenDependencies});
            } else {
                downloadFile.call(this);
                notificationOptions = {
                    message: i18n["export.finished"],
                    type: "success"
                };
            }
        }

        if (phase == ExportStateModel.STATE.FAILED) {
            notificationOptions = {
                message: i18n["export.error.unexpected"]
            };
        }

        if (phase == ExportStateModel.STATE.CANCELLED) {
            notificationOptions = {
                message: i18n["export.error.cancelled"]
            };
        }

        notificationOptions && this._showNotification(notificationOptions);
    },

    filterAuthoritiesBySubOrgs: function(){
        var contextMixin = {excludeSubOrgs: !this.model.get("includeSubOrganizations")};

        this.rolesList.model.setContext(contextMixin);
        this.usersList.model.setContext(contextMixin);
    },

    bindWithRoles: function(selection) {
        this.model.set({roles: selection});
        this.onUsersToRolesChange();
        if (selection.length) {
            this.rolesToUsers.setContext({roles: selection});
        } else {
            this.rolesToUsers.set({items: []});
        }
    },

    bindWithUsers: function(selection) {
        this.model.set({users: selection});
        this.onRolesToUsersChange();
        if (selection.length) {
            this.usersToRoles.setContext({users: selection});
        } else {
            this.usersToRoles.set({items: []});
        }
    },

    changeEnabledState: function() {
        var value = this.model.get("everything");

        this.rolesList.setDisabled(value || this.model.get("rolesForUser"));
        this.usersList.setDisabled(value || this.model.get("userForRoles"));
        if (value) {
            this.rolesList.selectNone();
            this.usersList.selectNone();
        }
    },

    includeRolesByUsers: function(evt) {
        var val = $(evt.target).is(":checked");
        this.model.set({rolesForUser: !val, userForRoles: val});
        this.onRolesToUsersChange();

        this._includeUsersOrRoles(!val, val);
    },

    includeUsersWithRoles: function(evt) {
        var val = $(evt.target).is(":checked");
        this.model.set({rolesForUser: val, userForRoles: !val});
        this.onUsersToRolesChange();

        this._includeUsersOrRoles(val, !val);
    },

    includeSelectedRolesUsersOnly: function(evt) {
        if ($(evt.target).is(":checked")) {
            this.model.set({userForRoles: false, rolesForUser: false});
            this.onRolesToUsersChange();
            this.onUsersToRolesChange();

            this._includeUsersOrRoles(false, false);
        }
    },
    sendParameters: function(evt) {
        this.model.isValid(true) && this.model.save();
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
    },

    _isServerLevel: function(type) {
        return type === exportTypesEnum.SERVER_CE || type === exportTypesEnum.SERVER_PRO;
    },

    _includeUsersOrRoles: function(rolesForUser, userForRoles) {
        this.rolesList.setDisabled(rolesForUser);
        this.usersList.setDisabled(userForRoles);
        this.rolesList.selectNone();
        this.usersList.selectNone();
    },

    _showNotification: function(options) {
        options = _.defaults(options, {type: "warning"});

        this.loadingDialog.isVisible() && this.loadingDialog.close();
        this.notification.show({
            delay: false,
            message: options.message,
            type: options.type
        });
    },

    _getBrokenDependencies: function() {
        var warnings = this.stateModel.get("warnings"),
            paths = [];

        _.each(warnings, function(value) {
            if (value.code === "export.broken.dependency") {
                paths.push(value.parameters);
            }
        });

        return _.flatten(_.sortBy(paths, function (path) { return path; }));
    },

    _clickOnCheckbox: function(evt) {
        var checkbox = $(evt.target).next();
        if (!checkbox[0].disabled) {
            this.model.set(checkbox[0].name, !checkbox[0].checked);
        }
    },

    _onModelChange: function() {
        this.model.isValid(true);
    },

    _resetModel: function(options) {
        var modelOptions = {
            organization: this.type == exportTypesEnum.REPOSITORY ? null : options.tenantId
        };

        if (this.type === exportTypesEnum.ROOT_TENANT || this.type === exportTypesEnum.TENANT) {
            var fileName = this.type === exportTypesEnum.ROOT_TENANT ? "root_export.zip" : options.tenantId + "_export.zip";

            _.extend(modelOptions, {fileName: fileName});
        }

        this.model.reset(modelOptions, this.type);
    }
});

_.extend(ExportView.prototype, epoxyViewMixin);

function customKeyFailure(self, options, encryptionHint, isServerOrRootType, isServerType, response) {
    renderSecTemplate(self, options, encryptionHint, isServerOrRootType, isServerType, []);
    self.notification.show({
        delay: false,
        message: response && response.responseJSON && response.responseJSON.message
    });
    self.trigger('error', self, response);
}

function initializeAuthorityPickers(options){
    this.rolesList = new AuthorityPickerView({
        model: AuthorityModel.instance("rest_v2/{{if (tenantId) { }}organizations/{{-tenantId}}/{{ } }}roles?q={{-searchString}}{{ if (excludeSubOrgs) { }}&includeSubOrgs=false{{ } }}", {tenantId: options.tenantId}),
        customClass: "selectedRoles",
        title: i18n["export.dialog.roles.users.roles.label"],
        selectLabel: i18n["export.dialog.roles.users.select.all.roles.label"]
    });

    this.rolesList.on("change:selection", this.bindWithRoles);
    this.rolesList.render();

    this.usersList = new AuthorityPickerView({
        model: AuthorityModel.instance("rest_v2/{{if (tenantId) { }}organizations/{{-tenantId}}/{{ } }}users?q={{-searchString}}{{ if (excludeSubOrgs) { }}&includeSubOrgs=false{{ } }}", {tenantId: options.tenantId}),
        customClass: "select selectedUsers",
        title: i18n["export.dialog.roles.users.users.label"],
        selectLabel: i18n["export.dialog.roles.users.select.all.users.label"]
    });

    this.usersList.on("change:selection", this.bindWithUsers);
    this.usersList.render();

    this.rolesToUsers = AuthorityModel.instance("rest_v2/users?hasAllRequiredRoles=false{{ if (roles) for (var i = 0; i < roles.length; i++) { }}&requiredRole={{-roles[i]}}{{ } }}");
    this.rolesToUsers.on("change", this.onRolesToUsersChange);

    this.usersToRoles = AuthorityModel.instance("rest_v2/roles?hasAllUsers=false{{ if (users) for (var i = 0; i < users.length; i++) { }}&user={{-users[i]}}{{ } }}");
    this.usersToRoles.on("change", this.onUsersToRolesChange);

    this.changeEnabledState();
    this.listenTo(this.model, "change:everything", this.changeEnabledState);

    this.$el.find(".selectRolesUsers").append(this.rolesList.el).append(this.usersList.el);

    this.listenTo(this.model, "change:includeSubOrganizations", this.filterAuthoritiesBySubOrgs);
}
function renderSecTemplate(self, options, encryptionHint, isServerOrRootType, isServerType, response) {
    self.template = _.template(exportTemplateFactory(options))({
        everything: false,
        i18n: i18n,
        i18n2: i18n2,
        encryptionHint: encryptionHint,
        isServerOrRootType: isServerOrRootType,
        isServerLevel: isServerType,
        isServerCe: self.type === exportTypesEnum.SERVER_CE,
        isRepository: self.type === exportTypesEnum.REPOSITORY,
        isSubOrgLevel: options.isSubOrgLevel,
        model: _.extend(self.model.toJSON(), {
            secureKeyTypes: secureKeyTypeEnum
        }),
        showCustomKey: response.length === 0 ? false : true,
        showKeyValue: false,
        exportMode: true
    });
    self.$el.html(self.template);
    if (self.$(".selectRolesUsers").length) {
        initializeAuthorityPickers.call(self, options);
    }
    self.applyEpoxyBindings();
}

function downloadFile() {
    var iframe = document.createElement("iframe");
    iframe.src = this.model.url() + "/" + this.stateModel.id + "/" + this.model.get("fileName");
    iframe.style.display = "none";
    $("body").append(iframe);
}

function cancelExportTask(phase) {
    this.model.cancel();
    this.stateModel.set("phase", phase);
}

export default ExportView;