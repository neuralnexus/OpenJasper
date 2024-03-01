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


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: RepositoryFileModel.js 1979 2016-04-22 20:41:56Z inestere $
 */
import ResourceModel from './RepositoryResourceModel';
import Backbone from 'backbone';
import repositoryResourceTypes from '../enum/repositoryResourceTypes';
import repositoryFileTypes from '../enum/repositoryFileTypes';
import base64 from 'js-sdk/src/common/util/base64';
import _ from 'underscore';

export default ResourceModel.extend({
    type: repositoryResourceTypes.FILE,
    stringifyContent: true,

    validation: (function() {
        var validation =  _.extend({}, ResourceModel.prototype.validation);

        delete validation.parentFolderUri;

        return validation;
    })(),

    defaults: _.extend({
        type: repositoryFileTypes.UNSPECIFIED,
        content: undefined
    }, ResourceModel.prototype.defaults),

    initialize: function(attrs) {
        ResourceModel.prototype.initialize.apply(this, arguments);

        this.content = this._decodeContent(this.get("content"));

        this.on("change:content", function() {
            this.content = this._decodeContent(this.get("content"));
        }, this);
    },

    setContent: function(content) {
        this.content = content;
        this.set("content", this._encodeContent(content), { silent: true });
    },

    fetchContent: function(options) {
        options || (options = {});

        var self = this;

        return Backbone.ajax(_.defaults(options, {
            type: "GET",
            url: this.url() + "?expanded=false",
            success: function(response) {
                self.setContent(response);
            }
        }));
    },

    _encodeContent: function (content) {
        if (!_.isUndefined(content)) {
            if (this.stringifyContent) {
                content = JSON.stringify(content);
            }

            content = content && base64.encode(content);
        }

        return content;
    },

    _decodeContent: function (content) {
        try {
            if (/[A-Za-z0-9+/=]/.test(content)) {
                content = base64.decode(content);

                if (this.stringifyContent) {
                    content = JSON.parse(content);
                }
            }
        } catch (ex) {
        }

        return content;
    }
});