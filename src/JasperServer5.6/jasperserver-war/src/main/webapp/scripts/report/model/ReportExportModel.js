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
 * @version: $Id: ReportExportModel.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BaseModel = require("common/model/BaseModel"),
        Backbone = require("backbone"),
        _ = require("underscore"),
        json3 = require("json3"),
        $ = require("jquery"),
        reportStatusTrait = require("./reportStatusTrait"),
        reportOutputFormats = require("../enum/reportOutputFormats");

    var STATUS_RETRY_TIMEOUT = 1000;

    function parseMarkup(markup) {
        var elements = $.parseHTML(markup),
            rootElem;

        elements = elements.filter(function(node) {
            var $el = $(node),
                tagName = $el.prop("tagName");

            tagName = tagName ? tagName : "";

            return "table" === tagName.toLowerCase();
        });

        if (elements.length == 1) {
            rootElem = elements[0];
        } else {
            throw new Error("Can't find exact root element in markup");
        }

        return rootElem;
    }

    var ReportExportModel = BaseModel.extend({
        defaults: function () {
            return {
                attachments: undefined,
                id: undefined,
                options: undefined,
                outputResource: undefined,
                outputFinal: false,
                status: undefined
            }
        },

        initialize: function(attrs, options) {
            options || (options = {});

            this.report = options.report;

            BaseModel.prototype.initialize.apply(this, arguments);
        },

        url: function() {
            if (_.isUndefined(this.report.get("requestId"))) {
                throw new Error("You must execute report before fetching export.");
            }

            var url = this.report.contextPath;

            if (url[url.length-1] !== "/") {
                url += "/";
            }

            url += "rest_v2/reportExecutions/" + this.report.get("requestId") + "/exports";

            return url;
        },

        urlStatus : function(){
            if (_.isUndefined(this.get("id"))) {
                throw new Error("Export ID is not specified");
            }

            return this.url() + "/" + this.get("id")  + "/status";
        },

        urlOutput : function(){
            if (_.isUndefined(this.get("id"))) {
                throw new Error("Export ID is not specified");
            }

            return this.url() + "/" + this.get("id")  + "/outputResource";
        },

        run: function(settings) {
            settings || (settings = {});

            var data = _.extend(
                this.report.execution.pick("outputFormat", "pages", "attachmentsPrefix", "allowInlineScripts", "baseUrl"),
                this.get("options") ? _.pick(this.get("options"), "outputFormat", "pages", "attachmentsPrefix", "allowInlineScripts", "baseUrl") : {},
                settings);

            return BaseModel.prototype.fetch.call(this, {
                url: this.url(),
                type: "POST",
                contentType: "application/json",
                headers: {
                    Accept: "application/json",
                    "x-jrs-base-url" : this.report.contextPath
                },
                data: json3.stringify(data)
            });
        },

        fetchOutput: function() {
            var self = this;

            return Backbone.ajax({
                url: this.urlOutput(),
                type: "GET",
                headers: {
                    "Accept": "text/html, application/json",
                    "x-jrs-base-url" : this.report.contextPath
                },
                dataType: this.get("options").outputFormat
            }).done(function(response, status, xhr) {
                var output = response;
                if (self.get("options").outputFormat === reportOutputFormats.HTML) {
                    output = parseMarkup(response.markup ? response.markup : response);
                }
                self.set({
                    output: output,
                    outputFinal: xhr.getResponseHeader("output-final") === "true"
                });
            })
        },

        updateStatus: function() {
            var self = this,
                xhr = Backbone.ajax({
                    type: "GET",
                    url: this.urlStatus(),
                    dataType: "json",
                    contentType: "application/json",
                    headers: {
                        "Accept": "application/status+json",
                        "x-jrs-base-url" : this.report.contextPath
                    }
                })
                .done(function(response) {
                    if (!self.set({
                        "status": response.value,
                        "errorDescriptor": response.errorDescriptor
                    })) {
                        return false;
                    }

                    self.trigger('sync', self, response);
                })
                .fail(function(response) {
                    self.trigger('error', self, response);
                });

            this.trigger('request', this, xhr);

            return xhr;
        },

        waitForExport: function() {
            var self = this,
                dfd = new $.Deferred(),
                completeFunc = function() {
                    if (!self.isCompleted()) {
                        setTimeout(function() {
                            self.updateStatus()
                                .done(completeFunc)
                                .fail(dfd.reject);
                        }, STATUS_RETRY_TIMEOUT);
                    } else {
                        dfd.resolve();
                    }
                };

            this.updateStatus()
                .done(completeFunc)
                .fail(dfd.reject);

            return dfd;
        }
    });

    _.extend(ReportExportModel.prototype, reportStatusTrait);

    return ReportExportModel;
});