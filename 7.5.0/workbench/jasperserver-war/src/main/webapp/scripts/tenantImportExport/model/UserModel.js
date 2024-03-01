define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

var RoleModel = require('./RoleModel');

var PermissionModel = require('./PermissionModel');

var primaryNavModule = require('../../actionModel/actionModel.primaryNavigation');

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
var FLOW_ID = 'userListFlow';
var USER_NAME_SEPARATOR = '|';
var UserModel = Backbone.Model.extend({
  initialize: function initialize(options) {
    if (options) {
      this.userName = options.userName;
      this.fullName = options.fullName;
      this.password = options.password || '';
      this.confirmPassword = options.confirmPassword || '';
      this.tenantId = options.tenantId;
      this.email = options.email;
      this.enabled = options.enabled;
      this.external = options.external;
      this.roles = [];

      if (options.roles) {
        options.roles.each(function (role) {
          this.roles.push(new RoleModel(role));
        }.bind(this));
      }

      if (options.permissionToDisplay) {
        this.permission = new PermissionModel(options.permissionToDisplay);
      }
    }
  },
  getNameWithTenant: function getNameWithTenant() {
    if (this.tenantId && !_.isEmpty(this.tenantId)) {
      return this.userName + USER_NAME_SEPARATOR + this.tenantId;
    } else {
      return this.userName;
    }
  },
  getDisplayName: function getDisplayName() {
    return this.userName;
  },
  getManagerURL: function getManagerURL() {
    return 'flow.html?' + Object.toQueryString({
      _flowId: FLOW_ID,
      text: !_.isUndefined(this.userName) ? encodeURIComponent(this.userName) : this.userName,
      tenantId: !_.isUndefined(this.tenantId) ? encodeURIComponent(this.tenantId) : this.tenantId
    });
  },
  equals: function equals(user) {
    return user && this.userName == user.userName && this.tenantId == user.tenantId;
  },
  toPermissionData: function toPermissionData() {
    return {
      userName: this.userName,
      tenantId: this.tenantId,
      permissionToDisplay: this.permission.toData()
    };
  },
  navigateToManager: function navigateToManager() {
    primaryNavModule.navigationPaths.tempNavigateToManager = _.cloneDeep(primaryNavModule.navigationPaths.user);
    primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
      text: this.userName,
      tenantId: this.tenantId
    });
    primaryNavModule.navigationOption('tempNavigateToManager');
  }
});
module.exports = UserModel;

});