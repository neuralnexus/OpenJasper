define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var _ = require('underscore');

var BaseDataSourceView = require('../view/BaseDataSourceView');

var VirtualDataSourceModel = require('../model/VirtualDataSourceModel');

var SubDataSourceModel = require('../model/SubDataSourceModel');

var dynamicTree = require('../../dynamicTree/dynamicTree.utils');

var dependentResourcesDialog = require('../../components/components.dependent.dialog');

var i18n = require("bundle!jasperserver_messages");

var virtualSpecificTemplate = require("text!../template/virtualSpecificTemplate.htm");

var dependenciesTemplate = require("text!../template/dialog/dependenciesTemplate.htm");

var SubDataSourcesListView = require('../view/SubDataSourcesListView');

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
module.exports = BaseDataSourceView.extend({
  PAGE_TITLE_NEW_MESSAGE_CODE: 'resource.datasource.virtual.page.title.new',
  PAGE_TITLE_EDIT_MESSAGE_CODE: 'resource.datasource.virtual.page.title.edit',
  hasDependedResources: false,
  // does this data source has depended resources, or not ?
  modelConstructor: VirtualDataSourceModel,
  events: {
    'keyup input[type=\'text\'][class!=\'dataSourceID\'], textarea': 'updateModelProperty',
    'change input[type=\'text\'][class!=\'dataSourceID\'], textarea, select': 'updateModelProperty',
    'click [name=toRight]': 'chooseTreeNodes',
    'click [name=toLeft]': 'removeSelectedSubDataSources',
    'click [name=allToLeft]': 'removeAllSubDataSources'
  },
  initialize: function initialize(options) {
    this.dependentResources = options.dependentResources;
    this._subDataSourcesHiddenNodes = {};

    if (this.dependentResources && this.dependentResources.length > 0) {
      this.hasDependedResources = true;
    }

    BaseDataSourceView.prototype.initialize.apply(this, arguments);
    this.showDependentResources();
    this.listenTo(this.model.subDataSources, 'reset', this.updateAllToLeftButtonState);
  },
  chooseTreeNodes: function chooseTreeNodes(e) {
    e.preventDefault();
    this.$('[name=toRight]').attr('disabled', 'disabled').removeClass('over');

    var self = this,
        nodes = this.subDataSourcesTree.selectedNodes,
        models = _.compact(_.map(nodes, function (node) {
      return !node ? null : new SubDataSourceModel({
        name: node.name,
        id: node.param.id,
        uri: node.param.uri,
        readOnly: false
      });
    })); // resetting the whole collection there instead of add, because we need a single "reset" event, signalizing
    // that all models were added to collection instead of several "add" events for each new model
    // resetting the whole collection there instead of add, because we need a single "reset" event, signalizing
    // that all models were added to collection instead of several "add" events for each new model


    this.model.subDataSources.reset(this.model.subDataSources.models.concat(models));

    var nodeUris = _.map(nodes, function (node) {
      return node.param.uri;
    });

    this._hideAvailableSubDataSources(nodeUris);
  },
  removeAllSubDataSources: function removeAllSubDataSources(e) {
    e.preventDefault();

    if (this.hasDependedResources) {
      return;
    }

    this.subDataSourcesTree._deselectAllNodes();

    var self = this;
    this.model.subDataSources.forEach(function (model) {
      self._unhideAvailableSubDataSources(model.get('uri'));
    });
    this.model.subDataSources.reset([]);
    this.updateRightButtonState();
  },
  removeSelectedSubDataSources: function removeSelectedSubDataSources(e) {
    e.preventDefault();

    this.subDataSourcesTree._deselectAllNodes();

    var selectedModels = this.selectedSubDataSourcesList.getSelectedModels(),
        self = this,
        newCollection = [];

    _.each(selectedModels, function (model) {
      self._unhideAvailableSubDataSources(model.get('uri'));
    });

    this.model.subDataSources.forEach(function (model) {
      if (!_.include(selectedModels, model)) {
        newCollection.push(model);
      }
    });
    this.model.subDataSources.reset(newCollection);
    this.updateRightButtonState();
  },
  updateAllToLeftButtonState: function updateAllToLeftButtonState() {
    if (this.hasDependedResources || this.selectedSubDataSourcesList.getListLength() === 0) {
      this.$('[name=allToLeft]').attr('disabled', 'disabled').removeClass('over');
    } else {
      this.$('[name=allToLeft]').removeAttr('disabled');
    }
  },
  updateRightButtonState: function updateRightButtonState() {
    var $btn = this.$('[name=toRight]'),
        nodes = this.subDataSourcesTree.selectedNodes,
        hasMovables = false,
        hasUnmovables = false;

    for (var i = 0; i < nodes.length; i++) {
      if (nodes[i].isParent() || this.model.subDataSources.where({
        'uri': nodes[i].param.uri
      }).length) {
        hasUnmovables = true;
        break;
      } else {
        hasMovables = true;
      }
    }

    if (hasMovables && !hasUnmovables) {
      $btn.removeAttr('disabled');
    } else {
      $btn.attr('disabled', 'disabled').removeClass('over');
    }
  },
  updateLeftButtonState: function updateLeftButtonState() {
    var models = this.selectedSubDataSourcesList.getSelectedModels(),
        $btn = this.$('[name=toLeft]');

    if (models.length > 0) {
      $btn.removeAttr('disabled');
    } else {
      $btn.attr('disabled', 'disabled').removeClass('over');
    }
  },
  render: function render() {
    this.$el.empty();
    this.renderVirtualSpecificSection();
    this.updateAllToLeftButtonState();
    return this;
  },
  showDependentResources: function showDependentResources() {
    if (!this.hasDependedResources) {
      return;
    }

    dependentResourcesDialog.show(this.dependentResources, {}, //no actions, just inform user
    {
      canSave: false,
      okOnly: true,
      topMessage: i18n['resource.dataSource.virtual.dependencies.top.message'],
      bottomMessage: i18n['resource.dataSource.virtual.dependencies.bottom.message']
    });
  },
  renderVirtualSpecificSection: function renderVirtualSpecificSection() {
    var self = this,
        hideNodesFn = function hideNodesFn() {
      self._hideAvailableSubDataSources(self.model.subDataSources.map(function (subDataSource) {
        return subDataSource.get('uri');
      }));
    };

    this.$el.append(_.template(virtualSpecificTemplate, this.templateData()));
    this.$el.append(_.template(dependenciesTemplate, this.templateData()));
    this.selectedSubDataSourcesList = new SubDataSourcesListView({
      collection: this.model.subDataSources
    });
    this.listenTo(this.selectedSubDataSourcesList, 'item:unselected item:selected', _.bind(this.updateLeftButtonState, this));
    this.selectedSubDataSourcesList.render();
    this.subDataSourcesTree = dynamicTree.createRepositoryTree('subDataSourcesTree', {
      treeId: 'subDataSourcesTree',
      providerId: 'joinableDsTreeDataProvider',
      selectLeavesOnly: true,
      multiSelectEnabled: true
    });
    this.subDataSourcesTree.observe('leaf:dblclick', _.bind(this.chooseTreeNodes, this));
    this.subDataSourcesTree.observe('node:selected', _.bind(this.updateRightButtonState, this));
    this.subDataSourcesTree.observe('node:unselected', _.bind(this.updateRightButtonState, this));
    this.subDataSourcesTree.observe('leaf:selected', _.bind(this.updateRightButtonState, this));
    this.subDataSourcesTree.observe('leaf:unselected', _.bind(this.updateRightButtonState, this));
    this.subDataSourcesTree.observe('children:loaded', hideNodesFn);
    this.subDataSourcesTree.showTreePrefetchNodes(this.model.subDataSources.map(function (subDataSource) {
      return subDataSource.get('uri');
    }).join(','), hideNodesFn);
  },
  _hideAvailableSubDataSources: function _hideAvailableSubDataSources(uri) {
    var self = this;

    if (_.isArray(uri)) {
      _.each(uri, function (uriItem) {
        self._hideAvailableSubDataSources(uriItem);
      });
    } else {
      var node = this.subDataSourcesTree.findLastLoadedNode(uri);

      if (node && node.param.uri === uri) {
        // keep removed node for the case if user unselects it
        this._subDataSourcesHiddenNodes[uri] = {
          parent: node.parent,
          child: node
        };
        var parent = node.parent;
        parent.removeChild(node);
        parent.resortChilds();
      }
    }
  },
  _unhideAvailableSubDataSources: function _unhideAvailableSubDataSources(uri) {
    function expandTreePath(tree, uri) {
      tree.processNodePath(uri, function (node) {
        if (node.parent) {
          if (tree.rootNode != node.parent && tree.getState(node.parent.id) == dynamicTree.TreeNode.State.CLOSED) {
            node.parent.handleNode();
          }

          if (node.param.uri === uri) {
            node.select();
          }
        }
      });
    }

    var self = this,
        notLoadedHiddenNodes = [];

    _.each(_.isArray(uri) ? uri : [uri], function (uriItem) {
      var hiddenNode = self._subDataSourcesHiddenNodes[uriItem];

      if (hiddenNode) {
        hiddenNode.parent.addChild(hiddenNode.child);
        hiddenNode.parent.resortChilds();
        hiddenNode.parent.refreshNode();
        expandTreePath(self.subDataSourcesTree, uriItem);
      } else {
        notLoadedHiddenNodes.push(uriItem);
      }
    });

    if (notLoadedHiddenNodes.length) {
      this.subDataSourcesTree.showTreePrefetchNodes(notLoadedHiddenNodes.join(','), function () {
        _.each(notLoadedHiddenNodes, _.bind(expandTreePath, this, self.subDataSourcesTree));
      });
    }
  },
  remove: function remove() {
    this.selectedSubDataSourcesList && this.selectedSubDataSourcesList.remove();
    BaseDataSourceView.prototype.remove.apply(this, arguments);
  }
});

});