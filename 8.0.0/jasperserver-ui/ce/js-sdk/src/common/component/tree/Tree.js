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

import $ from 'jquery';
import _ from 'underscore';
import Backbone from 'backbone';
import TreeLevel from './TreeLevel';
import TreeDataLayer from './TreeDataLayer';
import listTemplate from './template/treeLevelListTemplate.htm';
import levelTemplate from './template/treeLevelTemplate.htm';

var ITEM_ACTIONS = {
    ADD: 'add',
    UPDATE: 'update'
};
var Tree = Backbone.View.extend({
    template: _.template(levelTemplate),
    _plugins: [],
    el: function () {
        return this.template();
    },
    constructor: function (options) {
        options || (options = {});
        this.defaultDataLayer = new TreeDataLayer(options);
        this.customDataLayers = options.customDataLayers;
        if (options.type) {
            this._type = options.type;
            options.type = undefined;
        } else {
            this._type = 'tree';
        }
        options.collapserSelector || (options.collapserSelector = 'b.icon:eq(0)');
        if (options.predefinedData) {
            this.defaultDataLayer.predefinedData = options.predefinedData;
            options.predefinedData = undefined;
        } else {
            this.defaultDataLayer.predefinedData = {};
        }
        if (options.rootless) {
            this.rootless = options.rootless;
            options.rootless = undefined;
        }
        if (options.additionalCssClasses) {
            this.additionalCssClasses = options.additionalCssClasses;
            options.additionalCssClasses = undefined;
        }
        if (options.getDataLayer) {
            this.getDataLayer = options.getDataLayer;
            options.getDataLayer = undefined;
        }
        this._options = _.extend({
            itemsTemplate: listTemplate,
            plugins: this._plugins,
            owner: this
        }, options);
        this.context = options.context || {};
        for (var i = 0, l = this._plugins.length; i < l; i++) {
            this._plugins[i].processors && (this.processors = this.processors.concat(this._plugins[i].processors));
        }
        Backbone.View.apply(this, arguments);
    },
    initialize: function (options) {
        this.rootLevel = new TreeLevel(_.extend({}, this._options, { levelHeight: options.rootLevelHeight }, { el: this.$('li')[0] }, {
            item: {
                id: '/',
                value: {
                    id: '/',
                    resourceType: 'folder'
                }
            }
        }));
        this.rootless && this.$el.find('ul:first').addClass('hideRoot');
        this.additionalCssClasses && this.$el.find('ul:first').addClass(this.additionalCssClasses);
        this.listenTo(this.rootLevel, 'ready', _.bind(addLevels, this));
        this.listenTo(this.rootLevel, 'selection:change', _.bind(function (selection, level, visitedLevels) {
            if (level && visitedLevels && _.isArray(visitedLevels)) {
                this.rootLevel.selection.multiple || this.rootLevel.resetSelection({
                    silent: true,
                    exclude: [visitedLevels[0]]
                });
            }
            this.trigger('selection:change', selection);
        }, this));
        this.listenTo(this.rootLevel, 'item:dblclick', _.bind(function (selection) {
            this.trigger('item:dblclick', selection);
        }, this));
        for (var i = 0, l = this._plugins.length; i < l; i++) {
            this._plugins[i].constr.treeInitialized.call(this, this._plugins[i].options);
        }
        this.rootLevel.render();
        if (options.collapsed && options.lazyLoad && this.rootless) {
            this.expand('/');
        }
    },
    remove: function () {
        for (var i = 0, l = this._plugins.length; i < l; i++) {
            this._plugins[i].constr.treeRemoved.call(this);
        }
        this.rootLevel.remove();
        Backbone.View.prototype.remove.apply(this, arguments);
    },
    expand: function (nodeId, options) {
        var level = this.getLevel(nodeId);
        level && level.open(options);
        return this;
    },
    collapse: function (nodeId, options) {
        var level = this.getLevel(nodeId);
        level && level.close(options);
        return this;
    },
    _getNodeByLevelId: function (levelId) {
        var listContainingLevel, itemOfLevel;
        var level = this.getLevel(levelId);
        if (level) {
            if (!level.parent) {
                return null;
            }
            listContainingLevel = level.parent.list;
            return {
                node: level.item,
                nodeElement: level.$el,
                itsList: listContainingLevel
            };
        }
        var parentLevel = false;
        if (this._type === 'list') {
            parentLevel = this.rootLevel;
        } else if (this._type === 'tree') {
            var parentPath = levelId.split('/');
            parentPath = parentPath.slice(0, parentPath.length - 1).join('/');
            if (parentPath === '') {
                parentPath = '/';
            }
            parentLevel = this.getLevel(parentPath);
            if (!parentLevel) {
                return null;
            }
        }
        if (!parentLevel) {
            return null;
        }
        listContainingLevel = parentLevel.list;
        itemOfLevel = _.find(listContainingLevel.model.get('items'), function (item) {
            return item.id === levelId;
        });
        if (!itemOfLevel) {
            return null;
        }
        return {
            node: itemOfLevel,
            nodeElement: listContainingLevel.$el.find('li.leaf.selected'),
            itsList: listContainingLevel
        };
    },
    select: function (levelId) {
        var nodeInfo = this._getNodeByLevelId(levelId);
        if (!nodeInfo) {
            return;
        }
        nodeInfo.itsList.model.clearSelection().addValueToSelection(nodeInfo.node.value, nodeInfo.node.index);
        this.trigger('selection:change', nodeInfo.itsList.getValue());
    },
    deselect: function (nodeId) {
    },
    addItem: function (parentId, itemData) {
        var level = this.getLevel(parentId), action = ITEM_ACTIONS.ADD;
        level && this._getNodeItems(parentId, level, itemData, action);
        return this;
    },
    updateItem: function (itemData, parentId) {
        var level = parentId ? this.getLevel(parentId) : this.rootLevel;
        level && this._getNodeItems(level.id, level, itemData);
        return this;
    },
    resetSelection: function (options) {
        this.rootLevel.list.clearSelection();
        this.rootLevel.list.model.selection = [];
        this.rootLevel.resetSelection(options);
        return this;
    },
    refresh: function (context) {
        context && (this.context = context);
        this.rootLevel.refresh();
    },
    recalcConstraints: function () {
        this.rootLevel.recalcConstraints();
    },
    fetchVisibleData: function () {
        this.rootLevel.fetchVisibleData();
    },
    clearCache: function () {
        this.rootLevel.clearCache();
    },
    getLevel: function (levelId) {
        return this.rootLevel.id === levelId ? this.rootLevel : this.rootLevel.getLevel(levelId);
    },
    getDataLayer: function (level) {
        return this.customDataLayers && this.customDataLayers[level.id] ? this.customDataLayers[level.id] : this.defaultDataLayer;
    },
    _addItem: function (childItems, parentId, itemData) {
        !_.findWhere(childItems, { id: itemData.id }) && childItems.push(itemData);
        return childItems;
    },
    _updateItem: function (childItems, itemData) {
        _.each(childItems, function (item) {
            item.id === itemData.id && _.extend(item, itemData);
        }, this);
        return childItems;
    },
    _getNodeItems: function (parentId, level, itemData, action) {
        var dataLayer = this.getDataLayer(level), childItems = level.list.model.get('items') ? level.list.model.get('items') : [], updatedChildItems;
        dataLayer.predefinedData = dataLayer.predefinedData || {};
        updatedChildItems = action === ITEM_ACTIONS.ADD ? this._addItem(childItems, parentId, itemData) : this._updateItem(childItems, itemData);
        dataLayer.predefinedData[parentId] = _.sortBy(updatedChildItems, 'label');
        level.fetch({
            force: true,
            keepPosition: true
        });
    },
    _selectTreeNode: function (pathToSelect, $scrollContainer) {
        if (!_.isString(pathToSelect) || pathToSelect === '') {
            return;
        }
        var onceRootNodeIsReady = _.bind(function () {
            var selectItemAfterFolderTreeIsOpened = _.bind(function () {
                var folderToSelect = pathToSelect, levelIdToSelect = this.getLevelId(folderToSelect);
                this.expand(levelIdToSelect);
                this.select(levelIdToSelect);
                if (this._type === 'list') {
                    this.rootLevel.on('ready', onceRootNodeIsReady);
                } else if (this._type === 'tree') {
                    if ($scrollContainer && $scrollContainer.length) {
                        var $tree = this.$el, nodeInfo = this._getNodeByLevelId(levelIdToSelect);
                        if (!nodeInfo) {
                            return;
                        }
                        var $selectedItem = nodeInfo.nodeElement;
                        if (!$selectedItem) {
                            return;
                        }
                        var scrollTo = $selectedItem.offset().top - $tree.offset().top - $scrollContainer.height() / 2 + $selectedItem.height() / 2;
                        $scrollContainer.scrollTop(scrollTo);
                    }
                }
            }, this);
            if (pathToSelect === '/' || pathToSelect === '/public' || this._type === 'list') {
                selectItemAfterFolderTreeIsOpened();
            } else {
                var tmp = pathToSelect.replace(/\/$/, '');
                var pathToOpen = tmp.substr(0, tmp.lastIndexOf('/'));
                pathToOpen = pathToOpen || '/';
                var dfd = new $.Deferred();
                this._openPath(pathToOpen, dfd, 0);
                dfd.done(selectItemAfterFolderTreeIsOpened);
            }
        }, this);
        if (this.rootLevel.isReady()) {
            onceRootNodeIsReady();
        } else {
            this.rootLevel.on('ready', onceRootNodeIsReady);
        }
    },
    _openPath: function (path, dfd, index) {
        if (!path) {
            return dfd.resolve();
        }
        var self = this, pathFragmentToOpen, splitPath, level, levelIdToOpen;
        if (path === '/') {
            splitPath = ['/'];
        } else {
            splitPath = path.split('/');
            splitPath[0] = '/';
        }
        if (splitPath[0] === '/' && splitPath[1] === 'public') {
            splitPath = _.union(['/public'], _.rest(splitPath, 2));
        }
        index = index || 0;
        if (index === splitPath.length) {
            return dfd.resolve();
        }
        pathFragmentToOpen = _.first(splitPath, index + 1).join('/');
        pathFragmentToOpen = pathFragmentToOpen.replace(/\/\//g, '/');
        levelIdToOpen = this.getLevelId(pathFragmentToOpen);
        level = this.getLevel(levelIdToOpen);
        if (level) {
            if (level.collapsed) {
                level.once('ready', function () {
                    self._openPath(path, dfd, index + 1);
                });
                level.open();
            } else {
                this._openPath(path, dfd, index + 1);
            }
        }
    },
    getLevelId: function (repositoryPath) {
        var levelId = '';
        var nodesOnTop = ['/public'];
        if (repositoryPath === '/' || repositoryPath === '/root') {
            levelId = '/root';
        } else {
            var topLevelCase = false;
            for (var i = 0; i < nodesOnTop.length; i++) {
                if (repositoryPath.indexOf(nodesOnTop[i]) === 0) {
                    topLevelCase = true;
                    break;
                }
            }
            if (topLevelCase) {
                levelId = repositoryPath;
            } else {
                levelId = '/root' + '/' + repositoryPath.substr(1);
            }
        }
        return levelId;
    }
}, {
    instance: function (options) {
        return new this(options);
    }
});
export default {
    use: function (plugin, options) {
        return function (constructor) {
            return {
                use: function (plugin, options) {
                    constructor.prototype._plugins.push({
                        constr: plugin,
                        options: options
                    });
                    return this;
                },
                create: function () {
                    return constructor;
                }
            };
        }(Tree.extend({
            _plugins: [{
                constr: plugin,
                options: options
            }]
        }));
    },
    create: function () {
        return Tree;
    }
};
function addLevels(parentLevel) {
    var items = parentLevel.list.model.get('items'), self = this;
    items = _.where(items, { _node: true });
    parentLevel.$('.node').each(function (index, element) {
        var levelName = items[index].id;
        if (parentLevel.items[levelName]) {
            parentLevel.items[levelName].setElement(element);
        } else {
            parentLevel.items[levelName] = new TreeLevel(_.extend({}, self._options, {
                el: element,
                item: items[index],
                parent: parentLevel
            }));
            self.listenTo(parentLevel.items[levelName], 'listRenderError', _.bind(function (responseStatus, error, level) {
                this.trigger('levelRenderError', responseStatus, error, level);
            }, self));
            parentLevel.listenTo(parentLevel.items[levelName], 'ready', _.bind(addLevels, self));
            parentLevel.listenTo(parentLevel.items[levelName], 'selection:change', function (selection, level, visitedLevels) {
                level && visitedLevels.push(level);
                parentLevel.list.model.clearSelection();
                parentLevel.trigger('selection:change', selection, parentLevel, visitedLevels);
            });
            parentLevel.listenTo(parentLevel.items[levelName], 'item:dblclick', function (selection) {
                parentLevel.list.model.clearSelection();
                parentLevel.trigger('item:dblclick', selection);
            });
            parentLevel.items[levelName].render();
        }
    }).length && parentLevel.$('.j-view-port-chunk').css({ height: 'auto' });
}