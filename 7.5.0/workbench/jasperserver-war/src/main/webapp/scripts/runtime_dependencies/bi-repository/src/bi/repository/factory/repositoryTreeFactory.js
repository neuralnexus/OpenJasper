define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var Tree = require("runtime_dependencies/js-sdk/src/common/component/tree/Tree");

var $ = require('jquery');

var _ = require('underscore');

var i18n = require("bundle!CommonBundle");

var TreeDataLayer = require("runtime_dependencies/js-sdk/src/common/component/tree/TreeDataLayer");

var processorsFactory = require('../dialog/processor/factory/processorFactory');

var TooltipTreePlugin = require("runtime_dependencies/js-sdk/src/common/component/tree/plugin/TooltipPlugin");

var repositoryResourceTypes = require('../enum/repositoryResourceTypes');

var olapConnectionTypesEnum = require('../enum/olapConnectionTypesEnum');

var resourcesTreeGetDataUriFnUtil = require('../util/resourcesTreeGetDataUriFnUtil');

var extractRootLevelDataFromHtmlResponse = require('../util/extractRootLevelDataFromHtmlResponse');

var tooltipTemplate = require("text!../dialog/resourceChooser/template/repositoryFolderChooserDialogTooltipTemplate.htm");

var repositoryFoldersTreeLevelTemplate = require("text!../dialog/resourceChooser/template/repositoryFoldersTreeLevelTemplate.htm");

var jrsConfigs = require("runtime_dependencies/js-sdk/src/jrs.configs");

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
var LIST_ITEM_HEIGHT = 22;
var TREE_BUFFER_SIZE = 5000;

var repositoryFoldersTreeFactory = function repositoryFoldersTreeFactory(options) {
  options = options || {};

  if (!options.types) {
    throw 'Resources types required';
  }

  if (!options.listView) {
    return getTreeInstance(options);
  } else {
    return getTreeListInstance(options);
  }
};

function getTreeConstructor(options) {
  var tooltipOptions = _.extend({}, {
    i18n: i18n,
    contentTemplate: tooltipTemplate
  }, options.tooltipOptions);

  return options.constr || Tree.use(TooltipTreePlugin, tooltipOptions).create();
}

function getTreeInstance(options) {
  options = options || {};

  var processors = options.processors = _.map(options.processors, function (processorName) {
    return processorsFactory(processorName);
  });

  var TreeConstructor = getTreeConstructor(options);
  return TreeConstructor.instance({
    listItemHeight: LIST_ITEM_HEIGHT,
    bufferSize: options.treeBufferSize || TREE_BUFFER_SIZE,
    additionalCssClasses: 'folders',
    collapserSelector: options.collapserSelector,
    openClass: options.openClass,
    closedClass: options.closedClass,
    levelDataId: 'uri',
    itemsTemplate: options.treeItemsTemplate || repositoryFoldersTreeLevelTemplate,
    collapsed: true,
    lazyLoad: true,
    rootless: true,
    getDataUri: resourcesTreeGetDataUriFnUtil({
      getFolderUri: getFolderUri,
      contextPath: jrsConfigs.contextPath,
      recursive: false,
      type: options.types,
      containerType: options.containerType,
      forceTotalCount: true,
      forceFullPage: true
    }),
    selection: {
      allowed: true,
      multiple: false
    },
    customDataLayers: {
      //workaround for correct viewing of '/public' and '/' folder labels
      '/': getCustomRootDataLayer(options)
    },
    processors: processors,
    getDataArray: function getDataArray(data) {
      if (data && data[repositoryResourceTypes.RESOURCE_LOOKUP]) {
        data = data[repositoryResourceTypes.RESOURCE_LOOKUP];

        var levelDataId = this.levelDataId,
            first = _.first(data);

        if (!first[levelDataId].match(/^\/public/)) {
          data = _.map(data, function (resource) {
            resource.uri = '/root' + resource[levelDataId];
            return resource;
          });
        }

        return data;
      }

      return [];
    },
    getDataSize: function getDataSize(data, status, xhr) {
      return +xhr.getResponseHeader('Total-Count');
    },
    getDataLayer: customizedGetDataLayerFunc
  });
}

function getTreeRootLevelProcessors(processors) {
  var rootLevelProcessors = [];
  var filterPublicFolderProcessor = processorsFactory('filterPublicFolderProcessor');

  _.each(processors, function (processor) {
    if (processor !== filterPublicFolderProcessor) {
      rootLevelProcessors.push(processor);
    }
  });

  return rootLevelProcessors;
}

function getCustomRootDataLayer(options) {
  var processors = options.processors;
  var rootLevelProcessors = getTreeRootLevelProcessors(processors);
  var uri = '/flow.html?_flowId=searchFlow&method=getNode&provider=repositoryExplorerTreeFoldersProvider&uri=/&depth=1';
  return _.extend(new TreeDataLayer({
    dataUriTemplate: options.contextPath + uri,
    processors: rootLevelProcessors,
    getDataArray: function getDataArray(data) {
      data = extractRootLevelDataFromHtmlResponse(data);

      var publicFolder = _.find(data.children, function (item) {
        return item.uri === '/public';
      }),
          fakeRoot = [{
        id: '/root',
        label: data.label,
        uri: '/',
        resourceType: 'folder',
        permissionMask: computePermissionMask(data.extra),
        _links: {
          content: '@fakeContentLink'
        }
      }];

      if (publicFolder) {
        fakeRoot.push({
          id: '/public',
          label: publicFolder.label,
          uri: '/public',
          resourceType: 'folder',
          permissionMask: computePermissionMask(publicFolder.extra),
          _links: {
            content: '@fakeContentLink'
          }
        });
      }

      return fakeRoot;
    }
  }), {
    accept: 'text/html',
    dataType: 'text'
  });
}

function getTreeListInstance(options) {
  var olapDataLayer = getOlapDataLayer(),
      processors = options.processors;
  var TreeConstructor = getTreeConstructor(options);
  return TreeConstructor.instance({
    itemsTemplate: options.treeItemsTemplate || repositoryFoldersTreeLevelTemplate,
    listItemHeight: LIST_ITEM_HEIGHT,
    selection: {
      allowed: true,
      multiple: false
    },
    rootless: true,
    collapsed: true,
    lazyLoad: true,
    getDataUri: resourcesTreeGetDataUriFnUtil({
      getFolderUri: getFolderUri,
      contextPath: jrsConfigs.contextPath,
      recursive: true,
      type: options.types,
      containerType: options.containerType,
      forceTotalCount: true,
      forceFullPage: true
    }),
    levelDataId: 'uri',
    cache: {
      searchKey: 'searchString',
      pageSize: 100
    },
    processors: processors,
    customDataLayers: {
      'olapDataLayer': olapDataLayer
    },
    getDataLayer: customizedGetDataLayerFunc,
    getDataArray: function getDataArray(data) {
      return data && data[repositoryResourceTypes.RESOURCE_LOOKUP] ? data[repositoryResourceTypes.RESOURCE_LOOKUP] : [];
    },
    getDataSize: function getDataSize(data, status, xhr) {
      return +xhr.getResponseHeader('Total-Count');
    }
  });
}

function getOlapDataLayer() {
  return new TreeDataLayer({
    requestType: 'POST',
    dataUriTemplate: jrsConfigs.contextPath + '/rest_v2/connections',
    processors: [processorsFactory('i18nItemProcessor'), processorsFactory('cssClassItemProcessor')],
    levelDataId: 'uri',
    getDataArray: function getDataArray(data) {
      return data && data[repositoryResourceTypes.RESOURCE_LOOKUP] ? data[repositoryResourceTypes.RESOURCE_LOOKUP] : [];
    }
  });
}

function customizedGetDataLayerFunc(level) {
  var resourceType, isOlapResource;
  level = level.item ? level : this.getLevel(level.id);

  if (level) {
    resourceType = level.item.value.resourceType;
    isOlapResource = _.contains(olapConnectionTypesEnum, resourceType);

    if (!isOlapResource) {
      return this.customDataLayers && this.customDataLayers[level.id] ? this.customDataLayers[level.id] : this.defaultDataLayer;
    } else {
      var olapDataLayer = _.clone(this.customDataLayers['olapDataLayer']);

      _.extend(olapDataLayer, {
        accept: 'application/repository.' + resourceType + '.metadata+json',
        contentType: 'application/repository.' + resourceType + '+json',
        data: JSON.stringify(resourceType)
      });

      return olapDataLayer;
    }
  }
}

function getFolderUri(id) {
  if (id.match(/^\/root/)) {
    return id.replace("/root", "");
  } else {
    return id;
  }
}

function computePermissionMask(extra) {
  var mask = 2;
  extra.isWritable && (mask = mask | 4);
  extra.isRemovable && (mask = mask | 16);
  extra.isAdministrable && (mask = 1);
  return mask;
}

repositoryFoldersTreeFactory.settings = jrsConfigs;
module.exports = repositoryFoldersTreeFactory;

});