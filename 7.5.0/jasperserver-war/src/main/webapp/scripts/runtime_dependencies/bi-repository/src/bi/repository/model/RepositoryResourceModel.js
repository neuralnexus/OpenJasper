define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var $ = require('jquery');

var _ = require('underscore');

var BaseModel = require("runtime_dependencies/js-sdk/src/common/model/BaseModel");

var BackboneValidation = require("runtime_dependencies/js-sdk/src/common/extension/backboneValidationExtension");

var request = require("request");

var i18n = require("bundle!RepositoryResourceBundle");

var i18nMessageUtil = require("runtime_dependencies/js-sdk/src/common/util/i18nMessage");

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
var i18nMessage = i18nMessageUtil.extend({
  bundle: i18n
});
var defaultSettings = {
  LABEL_MAX_LENGTH: 100,
  NAME_MAX_LENGTH: 100,
  DESCRIPTION_MAX_LENGTH: 250,
  NAME_NOT_SUPPORTED_SYMBOLS: '~!#\\$%^|\\s`@&*()\\-+={}\\[\\]:;"\'\\<\\>,?/\\|\\\\'
};
var EXTRACT_RESOURCE_TYPE_EXPRESSION = /application\/repository\.([^\+]+)\+json/;

function doRepoOperation(model, type, destinationUri, overwrite, createFolders) {
  return request({
    type: type,
    dataType: 'json',
    url: model.contextPath + '/rest_v2/resources' + destinationUri + '?overwrite=' + overwrite + '&createFolders=' + createFolders,
    headers: {
      'Accept': 'application/json',
      'Content-Location': model.get('uri')
    }
  }).done(function (data) {
    model.set('uri', data.uri);
  });
}
/*
* Base model for Repository resources.
*
* Usage:
*      var resource = new ResourceModel(
*          { uri: "/public/MyReport" },
*          { contextPath: jrsConfigs.contextPath });
*/


var RepositoryResourceModel = BaseModel.extend({
  /*
       *  ID of Repository Resource is "uri" attribute.
       */
  idAttribute: 'uri',

  /*
       * Type of the Repository Resource as required by server. Should be overridden in inherited classes.
       */
  type: undefined,

  /*
       * "contextPath" should be set from options when initializing new RepositoryResourceModel.
       */
  urlRoot: function urlRoot() {
    return (this.contextPath || '') + '/rest_v2/resources';
  },

  /*
       * All fields that can be present in model.
       */
  defaults: {
    name: undefined,
    // helper field to track changing of resource name alone
    parentFolderUri: undefined,
    // helper field to track changing of resource parentFolderUri alone
    uri: undefined,
    label: undefined,
    description: undefined,
    permissionMask: undefined,
    // permissionMask is read only field
    creationDate: undefined,
    // creationDate is read only field
    updateDate: undefined,
    // updateDate is read only field
    version: undefined // version is read only field

  },

  /*
       * backbone-validation plugin validation mappings for attributes.
       */
  validation: {
    name: function name(value, attr, computedState) {
      if (value && value.length > defaultSettings.NAME_MAX_LENGTH) {
        return new i18nMessage('error.field.max.length', 'name', defaultSettings.NAME_MAX_LENGTH);
      }

      if (value && new RegExp('[' + defaultSettings.NAME_NOT_SUPPORTED_SYMBOLS + ']', 'g').test(value)) {
        return new i18nMessage('error.field.bad.symbols', 'name', defaultSettings.NAME_NOT_SUPPORTED_SYMBOLS);
      }
    },
    label: function label(value, attr, computedState) {
      if (_.isEmpty(value)) {
        return new i18nMessage('error.field.required', 'label');
      }

      if (value.length > defaultSettings.LABEL_MAX_LENGTH) {
        return new i18nMessage('error.field.max.length', 'label', defaultSettings.LABEL_MAX_LENGTH);
      }
    },
    description: function description(value, attr, computedState) {
      if (value && value.length > defaultSettings.DESCRIPTION_MAX_LENGTH) {
        return new i18nMessage('error.field.max.length', 'description', defaultSettings.DESCRIPTION_MAX_LENGTH);
      }
    },
    parentFolderUri: [{
      required: true,
      msg: new i18nMessage('error.field.required', 'parentFolderUri')
    }]
  },

  /*
       * Override Backbone.Model.prototype.url method to work in two modes.
       * If model is new (no "uri" attribute is set), then it returns full path to parent folder,
       * e.g. http://localhost:8080/jasperserver-pro/rest_v2/resources/public.
       * If model is not new ("uri" attribute is set), then function returns full path to resource,
       * e.g. http://localhost:8080/jasperserver-pro/rest_v2/resources/public/MyResourceModel.
       */
  url: function url() {
    if (this.isNew()) {
      return this.urlRoot() + encodeURI(this.get('parentFolderUri'));
    }

    return this.urlRoot() + encodeURI(this.id);
  },

  /*
       * Override constructor to set options.parse to true by default
       */
  constructor: function constructor(attributes, options) {
    options || (options = {});

    _.defaults(options, {
      parse: true
    });

    BaseModel.call(this, attributes, options);
  },
  initialize: function initialize(attributes, options) {
    this.contextPath = options.contextPath;
    options.type && (this.type = options.type); // update "uri" attribute when "name" or "parentFolderUri" changes
    // update "uri" attribute when "name" or "parentFolderUri" changes

    this.on('change:parentFolderUri change:name', this._updateUri); // update "name" and "parentFolderUri" attributes when "uri" changes
    // update "name" and "parentFolderUri" attributes when "uri" changes

    this.on('change:uri', this._updateNameAndParentFolderUri);
    BaseModel.prototype.initialize.apply(this, arguments);
  },

  /*
       * Override clone method to path additional options when cloning model.
       */
  clone: function clone() {
    return new this.constructor(this.attributes, {
      contextPath: this.contextPath
    });
  },

  /*
       * Override Backbone.Model.prototype.parse to get name and parentFolderUri from name.
       */
  parse: function parse(response) {
    if (typeof response.uri !== 'undefined') {
      response.name = RepositoryResourceModel.getNameFromUri(response.uri);
      response.parentFolderUri = RepositoryResourceModel.getParentFolderFromUri(response.uri);
    } else {
      if (response.parentFolderUri && response.name) {
        response.uri = RepositoryResourceModel.constructUri(response.parentFolderUri, response.name);
      }
    }

    return response;
  },

  /*
       * Override Backbone.Model.prototype.toJSON method to clean up fields that are not used by server.
       */
  toJSON: function toJSON() {
    var data = this.serialize();
    delete data.name;
    delete data.parentFolderUri;
    return data;
  },

  /*
       * Set "uri" attribute from "parentFolderUri" and "name" attributes.
       */
  _updateUri: function _updateUri() {
    var name = this.get('name'),
        folder = this.get('parentFolderUri'),
        uri = RepositoryResourceModel.constructUri(folder, name);

    if (uri) {
      this.set('uri', uri);
    }
  },

  /*
       * Set "parentFolderUri" and "name" attributes from "uri".
       */
  _updateNameAndParentFolderUri: function _updateNameAndParentFolderUri() {
    var uri = this.get('uri'),
        name = RepositoryResourceModel.getNameFromUri(uri),
        parentFolderUri = RepositoryResourceModel.getParentFolderFromUri(uri);
    this.set({
      name: name,
      parentFolderUri: parentFolderUri
    });
  },

  /*
       * Override Backbone.Model.prototype.fetch method to set correct HTTP headers and options.
       *
       * @param options Options object that is passed to Backbone.sync method. For default options see http://backbonejs.org/#Sync.
       *      if options.expanded - is true, all subresources will be retrieved as full descriptors,
       *          if false or undefined - all subresources will be retrieved as references.
       *
       * @return $.Deferred instance
       */
  fetch: function fetch(options) {
    _.defaults(options || (options = {}), {
      headers: {
        Accept: 'application/json'
      }
    });

    options.url = this.url() + '?expanded=' + (options.expanded === true);
    delete options.expanded;
    return BaseModel.prototype.fetch.call(this, options);
  },

  /*
       * Override Backbone.Model.prototype.sync method to set correct 'type' property from headers.
       */
  sync: function sync(method, model, options) {
    if ('read' === method) {
      var success = options.success,
          self = this;

      options.success = function (resource, status, xhr) {
        var contentType = xhr.getResponseHeader('Content-Type'),
            result = EXTRACT_RESOURCE_TYPE_EXPRESSION.exec(contentType);

        if (!result || !result[1]) {
          throw new Error('Unsupported response content type: ' + contentType);
        }

        self.type = result[1];

        if (success) {
          success(resource, status, xhr);
        }
      };
    }

    return BaseModel.prototype.sync.call(this, method, model, options);
  },

  /*
       * Override BaseModel.prototype.fetch method to set correct HTTP headers and options.
       *
       * @param options Options object that is passed to Backbone.sync method. For default options see http://backbonejs.org/#Sync.
       *      If options.createFolders is true or undefined, service will create all absent parent folders,
       *          if false - service will return error if parent folder not exists.
       *      If options.overwrite is true, service will overwrite resource if any,
       *          if false or undefined - service will return error if destination uri already in use.
       *
       * @return $.Deferred instance
       */
  save: function save(key, val, options) {
    var attrs; // Handle both `"key", value` and `{key: value}` -style arguments.
    // Handle both `"key", value` and `{key: value}` -style arguments.

    if (_.isUndefined(key) || _.isNull(key) || _.isObject(key)) {
      attrs = key || {};
      options = val;
    } else {
      (attrs = {})[key] = val;
    }

    if (!this.type) {
      throw new Error('Resource type is unspecified. It\'s not possible to save ' + 'a resource without it\'s type specified');
    }

    _.defaults(options || (options = {}), {
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/repository.' + this.type + '+json; charset=UTF-8'
      }
    });

    options = this._getSaveUrlOptions(options);
    return BaseModel.prototype.save.call(this, attrs, options);
  },
  _getSaveUrlOptions: function _getSaveUrlOptions(options) {
    var url = this.url() + '?createFolders=' + (options.createFolders === true);
    url += '&overwrite=' + (options.overwrite === true);
    url += '&expanded=' + (options.expanded === true);
    url += '&dry-run=' + (options.dryRun === true);
    options = _.omit(options, ['createFolders', 'overwrite', 'expanded', 'dryRun']);
    options = _.extend({}, options, {
      url: url
    });
    return options;
  },

  /*
       * Check if model supports "write" operation from current user.
       */
  isWritable: function isWritable() {
    var permission = this.get('permissionMask'),
        result = false;

    if (!_.isUndefined(permission)) {
      result = permission === 1 || permission & 4;
    }

    return result;
  },
  checkLabelExistenceOnServer: function checkLabelExistenceOnServer() {
    var label = this.get('label'),
        folderToSearchIn = this.get('parentFolderUri');
    this.operationInProgress = $.Deferred();
    request({
      type: "GET",
      dataType: "json",
      url: this.contextPath + "/rest_v2/resources" + "?folderUri=" + encodeURIComponent(folderToSearchIn) + "&q=" + encodeURIComponent(label) + "&recursive=false",
      headers: {
        "Accept": "application/json"
      }
    }).done(this._checkLabelExistenceOnServerDone.bind(this)).fail(this._checkLabelExistenceOnServerFail.bind(this));
    return this.operationInProgress;
  },
  _checkLabelExistenceOnServerDone: function _checkLabelExistenceOnServerDone(response, status, xhr) {
    var foundItems = [];

    if (response && response.resourceLookup && response.resourceLookup.length > 0) {
      foundItems = response.resourceLookup;
    }

    this.operationInProgress.resolve({
      foundResources: foundItems
    });
  },
  _checkLabelExistenceOnServerFail: function _checkLabelExistenceOnServerFail(xhr) {
    if (xhr.status === 404) {
      // this means there is no such label on server
      this.operationInProgress.resolve({
        foundResources: []
      });
    } else {
      // on other HTTP error code we suppose we got server error (like 503).
      this.operationInProgress.reject(xhr);
    }
  },
  copyTo: function copyTo(destinationUri, overwrite, createFolders) {
    return doRepoOperation(this, 'POST', destinationUri, !!overwrite, arguments.length < 3 || createFolders);
  },
  moveTo: function moveTo(destinationUri, overwrite, createFolders) {
    return doRepoOperation(this, 'PUT', destinationUri, !!overwrite, arguments.length < 3 || createFolders);
  }
}, {
  settings: defaultSettings,

  /*
       * Parse resource 'name' from 'uri'
       *
       * @param uri string
       *
       * @return resource name, string
       */
  getNameFromUri: function getNameFromUri(uri) {
    if (!uri) {
      return undefined;
    }

    var uriParts = uri.split('/');
    return uriParts[uriParts.length - 1];
  },

  /*
       * Parse resource 'parentFolderUri' from 'uri'
       *
       * @param uri string
       *
       * @return resource parentFolderUri, string
       */
  getParentFolderFromUri: function getParentFolderFromUri(uri) {
    if (!uri) {
      return undefined;
    }

    var uriParts = uri.split('/');

    if (uriParts.length === 2 && uriParts[1] !== '') {
      // resource is in the root folder
      return '/';
    }

    return uriParts.slice(0, uriParts.length - 1).join('/');
  },

  /*
       * Construct resource 'uri' from 'parentFolderUri' and 'name'
       *
       * @param parentFolderUri string
       * @param name string
       *
       * @return string or undefined
       */
  constructUri: function constructUri(parentFolderUri, name) {
    if (name && parentFolderUri) {
      return parentFolderUri.indexOf('/', parentFolderUri.length - 1) !== -1 ? parentFolderUri + name : parentFolderUri + '/' + name;
    }
  },

  /*
       * Generate 'name' for resource from string.
       *
       * @param label string
       *
       * @return name for resource, string
       */
  generateResourceName: function generateResourceName(label) {
    var name = '';

    if (label) {
      name = label.replace(new RegExp('[' + RepositoryResourceModel.settings.NAME_NOT_SUPPORTED_SYMBOLS + ']', 'g'), '_');
    }

    return name;
  }
}); // Add validation to model (https://github.com/thedersen/backbone.validation#validation-mix-in)

_.extend(RepositoryResourceModel.prototype, BackboneValidation.mixin);

module.exports = RepositoryResourceModel;

});