/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var _ = require("underscore"),
        json3 = require("json3"),
        Backbone = require("backbone"),
        InputControlModel = require("../model/InputControlModel");

    return Backbone.Collection.extend({
        model: InputControlModel,

        url: function() {
            if (!this.resourceUri) {
                throw new Error("Resource URI is not specified.");
            }

            var url = this.contextPath
                ? (this.contextPath[this.contextPath.length - 1] === "/"
                    ? this.contextPath
                    : (this.contextPath + "/"))
                : "";

            url += "rest_v2/reports";

            url += this.resourceUri[0] === "/"
                ? this.resourceUri
                : ("/" + this.resourceUri);

            url += this.resourceUri[this.resourceUri.length - 1] === "/"
                ? "inputControls"
                : "/inputControls";

            return url;
        },

        initialize: function(models, options) {
            options || (options = {});

            this.contextPath = options.contextPath;
            this.resourceUri = options.resourceUri;
        },

        parse: function(response) {

            if (response) {
                if (response.inputControl && _.isArray(response.inputControl)) {
                    return response.inputControl;
                }
            } else {
                return [];
            }

            throw new Error("Unable to parse response from server.");
        },

        fetch: function(options) {
            options || (options = {});

            _.extend(options, {
                url: this.url(),
                reset: true
            });

            if (options.excludeState) {
                options.url += "?exclude=state";
            }

            return Backbone.Collection.prototype.fetch.call(this, options);
        },

        update: function(options) {
            options || (options = {});

            if (!options.params) {
                throw new Error("Cannot update input controls without passed params");
            }

            _.extend(options, {
                type: "POST",
                contentType: "application/json",
                data: json3.stringify(options.params),
                reset: true
            });

            return Backbone.Collection.prototype.fetch.call(this, options);
        }
    });
});