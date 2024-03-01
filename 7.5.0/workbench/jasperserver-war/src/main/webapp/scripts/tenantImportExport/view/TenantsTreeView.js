define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var Backbone = require('backbone');

var i18n = require("bundle!ImportExportBundle");

var ExportDialogView = require('../export/view/ExportDialogView');

var ImportDialogView = require('../import/view/ImportDialogView');

var tenantTree = require('./tenantTree');

var ContextMenu = require("runtime_dependencies/js-sdk/src/common/component/menu/ContextMenu");

var tooltipTemplate = require("text!../templates/tooltipTemplate.htm");

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
var ROOT_TENANT_ID = 'organizations';
var TenantsTreeView = Backbone.View.extend({
  initialize: function initialize(options) {
    this.$container = $(options.container);
    var tenant = window.localStorage ? JSON.parse(localStorage.getItem('selectedTenant')) : null,
        selectedTenant = options.selectedTenant || tenant;
    var root = options.currentUser ? options.currentUser.split('|')[1] || ROOT_TENANT_ID : selectedTenant.tenantUri === '/' ? selectedTenant.tenantId || ROOT_TENANT_ID : ROOT_TENANT_ID;
    this.splittedUri = _.compact(selectedTenant.tenantUri.split('/'));
    this.splittedUri.splice(this.splittedUri.length - 1, 1);
    this.splittedUri.unshift(root);
    var treeInitOptions = {
      tooltipContentTemplate: tooltipTemplate,
      tenantId: selectedTenant.id || root,
      comparator: options.comparator
    };

    if (!options.removeContextMenuTreePlugin) {
      this.contextMenu = new ContextMenu([{
        label: i18n['context.menu.option.export'],
        action: 'export'
      }, {
        label: i18n['context.menu.option.import'],
        action: 'import'
      }], {
        hideOnMouseLeave: true
      });

      _.extend(treeInitOptions, {
        contextMenu: this.contextMenu
      });

      this.listenTo(this.contextMenu, 'option:export', this._onExport);
      this.listenTo(this.contextMenu, 'option:import', this._onImport);
    }

    this.exportDialog = new ExportDialogView();
    this.importDialog = new ImportDialogView();
    this.tree = new tenantTree(treeInitOptions);

    this._initEvents();
  },
  render: function render() {
    this.$container.append(this.tree.render().$el);
  },
  refreshTenant: function refreshTenant(tenantId) {
    var treeLevel = this.tree.getLevel(tenantId); //TODO: Fix this temporary hack with dublicating items when they're in predefinedData.
    //TODO: Fix this temporary hack with dublicating items when they're in predefinedData.

    this.tree.getDataLayer(treeLevel).predefinedData = [];
    tenantId ? treeLevel.refresh() : this.tree.refresh();
  },
  getTenant: function getTenant() {
    return this.selectedTenant;
  },
  setTenant: function setTenant(tenant, name, value) {
    !name ? this.selectedTenant = tenant : this.selectedTenant[name] = value;
  },
  selectTenant: function selectTenant(tenantId) {
    this.tree.select(tenantId);
  },
  addTenant: function addTenant(parentTenantId, tenant) {
    var parentLevel = this.tree.getLevel(parentTenantId);
    this.listenToOnce(parentLevel, 'ready', function () {
      this.tree.addItem(parentTenantId, this._processItem(tenant, true));
    });
    parentLevel.open();
  },
  updateTenant: function updateTenant(parentTenantId, updatedTenant, selectTenant) {
    this.tree.updateItem(this._processItem(updatedTenant, false, selectTenant), parentTenantId);
  },
  removeTenant: function removeTenant(tenantIds, parentTenantId) {
    var level,
        parentLevel = this.tree.getLevel(parentTenantId);
    tenantIds = !_.isArray(tenantIds) ? [tenantIds] : tenantIds;

    _.each(tenantIds, function (tenantId) {
      level = this.tree.getLevel(tenantId);
      level && level.remove();
    }, this);

    parentLevel.refresh();
    parentLevel.open();
  },
  remove: function remove() {
    this.tree.remove();
    this.contextMenu && this.contextMenu.remove();
  },
  _processItem: function _processItem(item, isNew, select) {
    var id = item.tenantId || item.id,
        uri = item.tenantFolderUri || item.tenantUri,
        label = item.tenantName,
        value = {
      id: id,
      label: label,
      uri: uri,
      tenantUri: uri,
      parentId: item.parentId
    };
    return isNew ? {
      _node: true,
      id: id,
      label: label,
      value: value
    } : {
      id: item.tenantId,
      addToSelection: select,
      label: label,
      value: value
    };
  },
  _initEvents: function _initEvents() {
    this.expandedLevels = [];
    this.listenTo(this.tree, 'selection:change', function (selection) {
      this.trigger('selection:change', _.compact(selection)[0]);
    });
    this.listenTo(this.importDialog, 'import:finished', function (tenantId) {
      this.refreshTenant(tenantId);
      this.trigger('import:finished', tenantId);
    });

    this._recursivelyOpenLevels(this.tree.rootLevel);
  },
  _recursivelyOpenLevels: function _recursivelyOpenLevels(level) {
    this.listenTo(level, 'ready', function () {
      var level = this.tree.getLevel(this.splittedUri[0]);
      this.splittedUri.splice(0, 1);

      if (level) {
        this.expandedLevels.push(level);
        this.tree.expand(level.id);

        this._recursivelyOpenLevels(level);
      } else {
        _.each(this.expandedLevels, function (level) {
          this.stopListening(level, 'ready');
        }, this);
      }
    });
  },
  _onExport: function _onExport() {
    this.exportDialog.openTenantDialog(this.selectedTenant);
  },
  _onImport: function _onImport() {
    this.importDialog.openDialog(this.selectedTenant);
  }
});
module.exports = TenantsTreeView;

});