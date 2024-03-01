define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var Backbone = require('backbone');

var ExportModel = require('../model/ExportModel');

var ExportStateModel = require('../model/ExportStateModel');

var AuthorityModel = require('../model/AuthorityPickerModel');

var AuthorityPickerView = require('./AuthorityPickerView');

var LoadingDialog = require('./LoadingDialog');

var ExportDependentResourcesDialogView = require('./ExportDependentResourcesDialogView');

var Notification = require("runtime_dependencies/js-sdk/src/common/component/notification/Notification");

var epoxyViewMixin = require("runtime_dependencies/js-sdk/src/common/view/mixin/epoxyViewMixin");

var exportTemplateFactory = require('../factory/exportTemplateFactory');

var exportTypesEnum = require('../enum/exportTypesEnum');

var i18n = require("bundle!ImportExportBundle");

var i18n2 = require("bundle!CommonBundle");

var awsSettings = require("settings!awsSettings");

/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
// TODO: fix separately
//import '../../../../themes/default/importExport.css';
var ExportView = Backbone.View.extend({
  className: "export-view",
  events: {
    "change .usersWithSelectedRoles": "includeRolesByUsers",
    "change .rolesWithSelectedUsers": "includeUsersWithRoles",
    "change .selectedUsersRoles": "includeSelectedRolesUsersOnly",
    "click .section .checkBox label": "_clickOnCheckbox"
  },
  computeds: {
    includeAccessEventsChecked: function includeAccessEventsChecked() {
      return this.model.get("everything") ? this.model.get("includeAccessEvents") : false;
    },
    enableAttributesValues: {
      deps: ["everything", "includeAttributes"],
      get: function get(everything, includeAttributes) {
        return !everything && includeAttributes;
      }
    },
    enableReportJobs: {
      deps: ["everything", "includeReports"],
      get: function get(everything, includeReports) {
        return !everything && includeReports;
      }
    },
    someResourceIsChecked: {
      deps: ["everything", "includeReports", "includeAdHocViews", "includeDashboards", "includeDomains", "includeDataSources", "includeOtherResourceFiles"],
      get: function get(everything, includeReports, includeAdHocViews, includeDashboards, includeDomains, includeDataSources, includeOtherResourceFiles) {
        return !everything && (includeReports || includeAdHocViews || includeDashboards || includeDomains || includeDataSources || includeOtherResourceFiles);
      }
    }
  },
  initialize: function initialize() {
    _.bindAll(this, "render", "doExport", "handleExportPhase", "filterAuthoritiesBySubOrgs", "bindWithRoles", "bindWithUsers", "changeEnabledState", "includeRolesByUsers", "includeUsersWithRoles", "includeSelectedRolesUsersOnly", "sendParameters", "onRolesToUsersChange", "onUsersToRolesChange", "_isServerLevel", "_includeUsersOrRoles", "_showNotification", "_getBrokenDependencies", "_clickOnCheckbox", "_onModelChange", "_resetModel");

    this.model || (this.model = new ExportModel());
    this.stateModel = new ExportStateModel();
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
    this.listenTo(this.dependentResourcesDialogView, "button:export", _.compose(_.bind(this.dependentResourcesDialogView.close, this.dependentResourcesDialogView), _.bind(function () {
      downloadFile.call(this);

      this._showNotification({
        message: i18n["export.finished"],
        type: "success"
      });
    }, this)));
    this.listenTo(this.dependentResourcesDialogView, "button:cancel", _.bind(cancelExportTask, this, ExportStateModel.STATE.CANCELLED));
  },
  render: function render(options) {
    options = options || {};
    this.type = options.type;

    var isServerType = this._isServerLevel(this.type),
        isServerOrRootType = isServerType || this.type === exportTypesEnum.ROOT_TENANT;

    isServerOrRootType ? this.$el.addClass("with-events") : this.$el.removeClass("with-events");
    this.stopListening(this.model, "change:includeSubOrganizations", this.filterAuthoritiesBySubOrgs);

    this._resetModel(options);

    var encryptionHint = i18n['export.dialog.file.properties.encryption.hint'];

    if (awsSettings.productTypeIsJrsAmi || awsSettings.productTypeIsMpAmi) {
      encryptionHint = i18n['export.dialog.file.properties.encryption.hint.aws'];
    }

    this.template = _.template(exportTemplateFactory(options))({
      everything: false,
      i18n: i18n,
      encryptionHint: encryptionHint,
      isServerOrRootType: isServerOrRootType,
      isServerLevel: isServerType,
      isServerCe: this.type === exportTypesEnum.SERVER_CE,
      isRepository: this.type === exportTypesEnum.REPOSITORY,
      isSubOrgLevel: options.isSubOrgLevel
    });
    this.$el.html(this.template);

    if (this.$(".selectRolesUsers").length) {
      initializeAuthorityPickers.call(this, options);
    }

    this.applyEpoxyBindings();
    return this;
  },
  doExport: function doExport() {
    var self = this;

    if (this.model.isValid(true)) {
      this.loadingDialog.open();
      this.model.save().done(function (status) {
        self.stateModel.unset("warnings", {
          silent: true
        });
        self.stateModel.set(status);
        self.model.set("id", status.id);
      });
    }
  },
  handleExportPhase: function handleExportPhase() {
    var phase = this.stateModel.get("phase"),
        brokenDependencies = this._getBrokenDependencies(),
        notificationOptions;

    if (phase == ExportStateModel.STATE.READY) {
      this.loadingDialog.close();

      if (!_.isEmpty(brokenDependencies)) {
        this.dependentResourcesDialogView.open({
          items: brokenDependencies
        });
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
  filterAuthoritiesBySubOrgs: function filterAuthoritiesBySubOrgs() {
    var contextMixin = {
      excludeSubOrgs: !this.model.get("includeSubOrganizations")
    };
    this.rolesList.model.setContext(contextMixin);
    this.usersList.model.setContext(contextMixin);
  },
  bindWithRoles: function bindWithRoles(selection) {
    this.model.set({
      roles: selection
    });
    this.onUsersToRolesChange();

    if (selection.length) {
      this.rolesToUsers.setContext({
        roles: selection
      });
    } else {
      this.rolesToUsers.set({
        items: []
      });
    }
  },
  bindWithUsers: function bindWithUsers(selection) {
    this.model.set({
      users: selection
    });
    this.onRolesToUsersChange();

    if (selection.length) {
      this.usersToRoles.setContext({
        users: selection
      });
    } else {
      this.usersToRoles.set({
        items: []
      });
    }
  },
  changeEnabledState: function changeEnabledState() {
    var value = this.model.get("everything");
    this.rolesList.setDisabled(value || this.model.get("rolesForUser"));
    this.usersList.setDisabled(value || this.model.get("userForRoles"));

    if (value) {
      this.rolesList.selectNone();
      this.usersList.selectNone();
    }
  },
  includeRolesByUsers: function includeRolesByUsers(evt) {
    var val = $(evt.target).is(":checked");
    this.model.set({
      rolesForUser: !val,
      userForRoles: val
    });
    this.onRolesToUsersChange();

    this._includeUsersOrRoles(!val, val);
  },
  includeUsersWithRoles: function includeUsersWithRoles(evt) {
    var val = $(evt.target).is(":checked");
    this.model.set({
      rolesForUser: val,
      userForRoles: !val
    });
    this.onUsersToRolesChange();

    this._includeUsersOrRoles(val, !val);
  },
  includeSelectedRolesUsersOnly: function includeSelectedRolesUsersOnly(evt) {
    if ($(evt.target).is(":checked")) {
      this.model.set({
        userForRoles: false,
        rolesForUser: false
      });
      this.onRolesToUsersChange();
      this.onUsersToRolesChange();

      this._includeUsersOrRoles(false, false);
    }
  },
  sendParameters: function sendParameters(evt) {
    this.model.isValid(true) && this.model.save();
  },
  onRolesToUsersChange: function onRolesToUsersChange() {
    if (this.model.get("userForRoles")) {
      this.usersList.highlightSet(_.map(this.rolesToUsers.get("items"), function (item) {
        return item.username + (item.tenantId ? "|" + item.tenantId : "");
      }));
      this.rolesList.highlightSet([]);
    } else {
      this.usersList.highlightSet([]);
    }
  },
  onUsersToRolesChange: function onUsersToRolesChange() {
    if (this.model.get("rolesForUser")) {
      this.rolesList.highlightSet(_.map(this.usersToRoles.get("items"), function (item) {
        return item.name + (item.tenantId ? "|" + item.tenantId : "");
      }));
      this.usersList.highlightSet([]);
    } else {
      this.rolesList.highlightSet([]);
    }
  },
  _isServerLevel: function _isServerLevel(type) {
    return type === exportTypesEnum.SERVER_CE || type === exportTypesEnum.SERVER_PRO;
  },
  _includeUsersOrRoles: function _includeUsersOrRoles(rolesForUser, userForRoles) {
    this.rolesList.setDisabled(rolesForUser);
    this.usersList.setDisabled(userForRoles);
    this.rolesList.selectNone();
    this.usersList.selectNone();
  },
  _showNotification: function _showNotification(options) {
    options = _.defaults(options, {
      type: "warning"
    });
    this.loadingDialog.isVisible() && this.loadingDialog.close();
    this.notification.show({
      delay: false,
      message: options.message,
      type: options.type
    });
  },
  _getBrokenDependencies: function _getBrokenDependencies() {
    var warnings = this.stateModel.get("warnings"),
        paths = [];

    _.each(warnings, function (value) {
      if (value.code === "export.broken.dependency") {
        paths.push(value.parameters);
      }
    });

    return _.flatten(_.sortBy(paths, function (path) {
      return path;
    }));
  },
  _clickOnCheckbox: function _clickOnCheckbox(evt) {
    var checkbox = $(evt.target).next();

    if (!checkbox[0].disabled) {
      this.model.set(checkbox[0].name, !checkbox[0].checked);
    }
  },
  _onModelChange: function _onModelChange() {
    this.model.isValid(true);
  },
  _resetModel: function _resetModel(options) {
    var modelOptions = {
      organization: this.type == exportTypesEnum.REPOSITORY ? null : options.tenantId
    };

    if (this.type === exportTypesEnum.ROOT_TENANT || this.type === exportTypesEnum.TENANT) {
      var fileName = this.type === exportTypesEnum.ROOT_TENANT ? "root_export.zip" : options.tenantId + "_export.zip";

      _.extend(modelOptions, {
        fileName: fileName
      });
    }

    this.model.reset(modelOptions, this.type);
  }
});

_.extend(ExportView.prototype, epoxyViewMixin);

function initializeAuthorityPickers(options) {
  this.rolesList = new AuthorityPickerView({
    model: AuthorityModel.instance("rest_v2/{{if (tenantId) { }}organizations/{{-tenantId}}/{{ } }}roles?q={{-searchString}}{{ if (excludeSubOrgs) { }}&includeSubOrgs=false{{ } }}", {
      tenantId: options.tenantId
    }),
    customClass: "selectedRoles",
    title: i18n["export.dialog.roles.users.roles.label"],
    selectLabel: i18n["export.dialog.roles.users.select.all.roles.label"]
  });
  this.rolesList.on("change:selection", this.bindWithRoles);
  this.rolesList.render();
  this.usersList = new AuthorityPickerView({
    model: AuthorityModel.instance("rest_v2/{{if (tenantId) { }}organizations/{{-tenantId}}/{{ } }}users?q={{-searchString}}{{ if (excludeSubOrgs) { }}&includeSubOrgs=false{{ } }}", {
      tenantId: options.tenantId
    }),
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

module.exports = ExportView;

});