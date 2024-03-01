define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var Backbone = require('backbone');

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
var TenantModel = Backbone.Model.extend({
  defaults: {
    id: undefined,
    name: undefined,
    alias: undefined,
    desc: '',
    uri: undefined,
    parentId: undefined
  },
  initialize: function initialize(json) {
    if (!json) {
      throw new Error('Can\'t create Tenant from undefined json');
    } //TODO: Move this to model.attributes.
    //TODO: Move this to model.attributes.


    this.name = json.label || json.tenantName;
    this.label = this.name;
    this.alias = json.alias || json.tenantAlias;
    this.desc = json.tenantDesc;
    this.uri = json.tenantUri || json.uri;
    this.parentId = json.parentId;
    this.subTenantCount = json.subTenantCount;
    this.set({
      id: json.id || json.tenantId,
      name: this.name,
      uri: json.tenantUri || json.uri,
      alias: json.alias || json.tenantAlias
    });
  },
  isRoot: function isRoot() {
    // TODO: load name of root organization from configuration.
    return this.get('id') == 'organizations';
  },
  //TODO: Move to parent class.
  getId: function getId() {
    return this.get('id');
  },
  getNameWithTenant: function getNameWithTenant() {
    return this.get('id');
  },
  getDisplayName: function getDisplayName() {
    return this.get('name');
  },
  equals: function equals(org) {
    return org && this.get('id') == org.get('id');
  },
  navigateToManager: function navigateToManager() {
    primaryNavModule.navigationPaths.tempNavigateToManager = _.cloneDeep(primaryNavModule.navigationPaths.organization);
    primaryNavModule.navigationPaths.tempNavigateToManager.params += '&' + Object.toQueryString({
      tenantId: this.get('id')
    });
    primaryNavModule.navigationOption('tempNavigateToManager');
  }
});
module.exports = TenantModel;

});