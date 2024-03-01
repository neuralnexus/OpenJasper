define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var AlertDialog = require("runtime_dependencies/js-sdk/src/common/component/dialog/AlertDialog");

var JdbcDataSourceView = require('../view/JdbcDataSourceView');

var BaseDataSourceView = require('../view/BaseDataSourceView');

var AzureSqlDataSourceModel = require('../model/AzureSqlDataSourceModel');

var dynamicTree = require('../../dynamicTree/dynamicTree.utils');

var i18n = require("bundle!jasperserver_messages");

var jsExceptionsMessages = require("bundle!jsexceptions_messages");

var azureSqlSpecificTemplate = require("text!../template/azureSqlSpecificTemplate.htm");

var selectDialogTemplate = require("text!runtime_dependencies/js-sdk/src/common/templates/components.pickers.htm");

var resourceLocator = require('../../resource/resource.locate');

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
module.exports = JdbcDataSourceView.extend({
  PAGE_TITLE_NEW_MESSAGE_CODE: 'resource.datasource.aws.page.title.new',
  PAGE_TITLE_EDIT_MESSAGE_CODE: 'resource.datasource.aws.page.title.edit',
  modelConstructor: AzureSqlDataSourceModel,
  events: function events() {
    var events = {};

    _.extend(events, JdbcDataSourceView.prototype.events);

    events['click #updateDatabaseTree'] = 'updateDatabaseTree';
    return events;
  },
  initialize: function initialize(options) {
    BaseDataSourceView.prototype.initialize.apply(this, arguments);
    this.listenTo(this.model, 'change', this.onModelChange);
  },
  onModelChange: function onModelChange() {
    var changedAttributes = _.keys(this.model.changedAttributes()),
        inputsToUpdate = ['serverName', 'dbName', 'connectionUrl'],
        self = this;

    _.each(_.intersection(changedAttributes, inputsToUpdate), function (attr) {
      self.$('[name=\'' + attr + '\']').val(self.model.get(attr));
    });
  },
  updateDatabaseTree: function updateDatabaseTree(e) {
    e.preventDefault();
    this.showAzureSqlDsTree('/');
  },
  render: function render() {
    this.$el.empty();
    this.renderTimezoneSection();
    this.renderAzureSqlSpecificSection();
    this.renderTestConnectionSection();
    this.initDataSourceTree(); // Do not try to load Azure SQL DB Tree if we are creating a new DS
    // Do not try to load Azure SQL DB Tree if we are creating a new DS

    if (this.options.isEditMode) {
      this.showAzureSqlDsTree(this.model.getFullDbTreePath());
    }

    return this;
  },
  showAzureSqlDsTree: function showAzureSqlDsTree(path) {
    this.model.validate({
      subscriptionId: this.model.get('subscriptionId'),
      keyStorePassword: this.model.get('keyStorePassword'),
      keyStoreUri: this.model.get('keyStoreUri')
    });

    if (this.model.isValid('keyStorePassword') && this.model.isValid('subscriptionId')) {
      this.azureSqlDataSourceTree.showTreePrefetchNodes(path || '/');
    }
  },
  renderAzureSqlSpecificSection: function renderAzureSqlSpecificSection() {
    this.$el.append(_.template(azureSqlSpecificTemplate, this.templateData()));
    this.browseButton = resourceLocator.initialize({
      i18n: i18n,
      template: selectDialogTemplate,
      resourceInput: this.$el.find('[name=keyStoreUri]')[0],
      browseButton: this.$el.find('[name=repositoryBrowserButton]')[0],
      providerId: 'fileResourceBaseTreeDataProvider',
      dialogTitle: i18n['resource.Add.Files.Title'],
      selectLeavesOnly: true,
      onChange: _.bind(function (value) {
        this.model.set('keyStoreUri', value);
        this.model.validate({
          'keyStoreUri': value
        });
      }, this)
    });
  },
  initDataSourceTree: function initDataSourceTree() {
    var isEdit = this.options.isEditMode,
        self = this,
        treeOptions = {
      hideLoader: true,
      bShowRoot: false,
      treeId: 'azureSqlDataSourceTree',
      providerId: 'azureSqlDataSourceTreeDataProvider',
      selectLeavesOnly: true,
      additionalParams: function additionalParams() {
        return {
          subscriptionId: self.model.get('subscriptionId'),
          keyStorePassword: self.model.get('keyStorePassword'),
          keyStoreUri: self.model.get('keyStoreUri'),
          datasourceUri: self.model.get('uri')
        };
      }
    };
    this.azureSqlDataSourceTree = dynamicTree.createRepositoryTree('azureSqlDataSourceTree', treeOptions);

    this.azureSqlDataSourceTree.httpErrorHandler = function (ajaxAgent) {
      // some requests came with 500 error code, some with 200, so let's just check error test
      var alertDialog,
          error = false;
      var messageCodes = ['azure.exception.datasource.recovery.public.ip.not.provided', 'azure.exception.datasource.recovery.firewall.rule.name.not.provided', 'azure.exception.datasource.recovery.server.name.not.provided', 'azure.exception.datasource.recovery.subscription.id.not.provided', 'azure.exception.datasource.recovery.key.store.file.not.provided', 'azure.exception.datasource.recovery.key.store.type.not.provided', 'azure.exception.datasource.key.error', 'azure.exception.datasource.auth.error', 'azure.exception.datasource.cannot.retrieve.database.list', 'azure.exception.datasource.cannot.ensure.firewall.rule', 'azure.exception.datasource.cannot.recover.datasource'];
      var i;

      for (i = 0; i < messageCodes.length; i++) {
        var messageCode = messageCodes[i];

        if (ajaxAgent.responseText.indexOf(messageCode) !== -1) {
          error = jsExceptionsMessages[messageCode];
          break;
        }
      }

      if (error) {
        alertDialog = new AlertDialog({
          modal: true
        });
        alertDialog.setMessage(error);
        alertDialog.open(); // return 'true' means don't let the parent's handler to handle this error
        // return 'true' means don't let the parent's handler to handle this error

        return true;
      }

      return false;
    }; // Initialize tree events: auto-fill connection setting from tree leaf
    // Initialize tree events: auto-fill connection setting from tree leaf


    this.azureSqlDataSourceTree.observe('leaf:selected', function (ev) {
      var node = ev.memo.node.param;

      if (node.type === 'leaf' && !isEdit) {
        var extra = node.extra;
        self.model.set({
          serverName: extra.serverName,
          dbName: extra.dBName,
          connectionUrlTemplate: extra.jdbcTemplate,
          driverClass: extra.jdbcDriver
        });
      }
    });
    this.azureSqlDataSourceTree.observe('tree:loaded', function () {
      if (self.model.getFullDbTreePath()) {
        self.azureSqlDataSourceTree.openAndSelectNode(self.model.getFullDbTreePath(), function () {
          isEdit = false;
          var selectedNode = self.azureSqlDataSourceTree.getSelectedNode();

          if (selectedNode && selectedNode.param && selectedNode.param.extra) {
            // add required fields to a model from selected node.
            // it's required for JDBC URL auto update
            self.model.set({
              connectionUrlTemplate: selectedNode.param.extra.jdbcTemplate,
              dbHost: selectedNode.param.extra.dnsAddress,
              dbPort: selectedNode.param.extra.dbPort
            });
          }
        });
      }
    });
  },
  remove: function remove() {
    this.azureSqlDataSourceTree && this.azureSqlDataSourceTree.stopObserving();
    JdbcDataSourceView.prototype.remove.apply(this, arguments);
  }
});

});