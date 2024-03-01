define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

var primaryNavModule = require('../../actionModel/actionModel.primaryNavigation');

var PermissionModel = require('./PermissionModel');

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
var FLOW_ID = 'roleListFlow';
var USER_NAME_SEPARATOR = '|';
var RoleModel = Backbone.Model.extend({
  initialize: function initialize(options) {
    if (options) {
      this.roleName = options.roleName;
      this.external = options.external;
      this.tenantId = options.tenantId;
    }

    if (options.permissionToDisplay) {
      this.permission = new PermissionModel(options.permissionToDisplay);
    }
  },
  getDisplayName: function getDisplayName() {
    return this.roleName;
  },
  getNameWithTenant: function getNameWithTenant() {
    if (this.tenantId && !this.tenantId.blank()) {
      return this.roleName + USER_NAME_SEPARATOR + this.tenantId;
    } else {
      return this.roleName;
    }
  },
  getManagerURL: function getManagerURL() {
    return 'flow.html?' + Object.toQueryString({
      _flowId: FLOW_ID,
      // Object.toQueryString already does encodeURIComponent() once, so we have double encoding here
      text: typeof this.roleName !== 'undefined' ? encodeURIComponent(this.roleName) : this.roleName,
      tenantId: typeof this.tenantId !== 'undefined' ? encodeURIComponent(this.tenantId) : this.tenantId
    });
  },
  navigateToManager: function navigateToManager() {
    primaryNavModule.navigationPaths.tempNavigateToManager = _.cloneDeep(primaryNavModule.navigationPaths.role);
    primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
      text: this.roleName,
      tenantId: this.tenantId
    });
    primaryNavModule.navigationOption('tempNavigateToManager');
  },
  equals: function equals(role) {
    return role && this.roleName == role.roleName && this.tenantId == role.tenantId;
  },
  toPermissionData: function toPermissionData(user) {
    return {
      roleName: this.roleName,
      tenantId: this.tenantId,
      permissionToDisplay: this.permission.toData()
    };
  }
});
module.exports = RoleModel;

});