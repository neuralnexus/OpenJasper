define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var ResourceModel = require('./RepositoryResourceModel');

var Backbone = require('backbone');

var repositoryResourceTypes = require('../enum/repositoryResourceTypes');

var repositoryFileTypes = require('../enum/repositoryFileTypes');

var base64 = require("runtime_dependencies/js-sdk/src/common/util/base64");

var _ = require('underscore');

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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: RepositoryFileModel.js 1979 2016-04-22 20:41:56Z inestere $
 */
module.exports = ResourceModel.extend({
  type: repositoryResourceTypes.FILE,
  stringifyContent: true,
  validation: function () {
    var validation = _.extend({}, ResourceModel.prototype.validation);

    delete validation.parentFolderUri;
    return validation;
  }(),
  defaults: _.extend({
    type: repositoryFileTypes.UNSPECIFIED,
    content: undefined
  }, ResourceModel.prototype.defaults),
  initialize: function initialize(attrs) {
    ResourceModel.prototype.initialize.apply(this, arguments);
    this.content = this._decodeContent(this.get("content"));
    this.on("change:content", function () {
      this.content = this._decodeContent(this.get("content"));
    }, this);
  },
  setContent: function setContent(content) {
    this.content = content;
    this.set("content", this._encodeContent(content), {
      silent: true
    });
  },
  fetchContent: function fetchContent(options) {
    options || (options = {});
    var self = this;
    return Backbone.ajax(_.defaults(options, {
      type: "GET",
      url: this.url() + "?expanded=false",
      success: function success(response) {
        self.setContent(response);
      }
    }));
  },
  _encodeContent: function _encodeContent(content) {
    if (!_.isUndefined(content)) {
      if (this.stringifyContent) {
        content = JSON.stringify(content);
      }

      content = content && base64.encode(content);
    }

    return content;
  },
  _decodeContent: function _decodeContent(content) {
    try {
      if (/[A-Za-z0-9+/=]/.test(content)) {
        content = base64.decode(content);

        if (this.stringifyContent) {
          content = JSON.parse(content);
        }
      }
    } catch (ex) {}

    return content;
  }
});

});