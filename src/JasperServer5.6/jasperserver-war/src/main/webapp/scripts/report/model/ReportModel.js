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
 * @author: Igor Nesterenko, Kostiantyn Tsaregradskyi
 * @version: $Id: ReportModel.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BaseModel = require("common/model/BaseModel"),
        ReportExecutionModel = require("./ReportExecutionModel"),
        ReportExportCollection = require("../collection/ReportExportCollection"),
        _ = require("underscore"),
        Backbone = require("backbone"),
        $ = require("jquery"),
        json3 = require("json3"),
        reportStatuses = require("../enum/reportStatuses"),
        reportEvents = require("../enum/reportEvents"),
        jiveTypes = require("../jive/enum/jiveTypes"),
        reportStatusTrait = require("./reportStatusTrait"),
        log =  require("logger").register("Report");

    var STATUS_RETRY_TIMEOUT = 1000;

    var ReportModel = BaseModel.extend({
        idAttribute: "requestId",

        defaults: function() {
            return {
                exports: undefined,
                reportURI: undefined,
                requestId: undefined,
                status: undefined,
                totalPages: undefined
            };
        },

        initialize: function(attrs, options) {
            var self = this;

            BaseModel.prototype.initialize.apply(this, arguments);

            options || (options = {});

            this.contextPath = options.contextPath;

            this.execution = new ReportExecutionModel({}, { report: this });
            this.exports = new ReportExportCollection(this.get("exports") || [], { report: this });
        },

        urlAction: function(){
            var url = this.contextPath;

            if (url[url.length-1] !== "/") {
                url += "/";
            }

            url += "runReportAction.html";

            return url;
        },

        execute: function(options) {
            var self = this;

            this.unset("status", { silent: true });

            options || (options = {});

            this.execution.set(_.defaults(_.extend({}, options), {
                reportUnitUri: this.get("reportURI"),
                baseUrl: this.contextPath
            }));

            var xhr = this.execution.run()
                .done(function(response) {
                    if (!self.set(self.parse(response))) {
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

        updateStatus: function() {
            var self = this,
                xhr = this.execution.status()
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

        update: function() {
            var self = this,
                xhr = this.execution.update()
                    .done(function(response) {
                        if (!self.set(self.parse(response))) {
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

        waitForExecution: function() {
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
        },

        runAction: function(action) {
            if (!this.has("requestId")) {
                throw new Error("You must execute report first before running any action.");
            }

            this.unset("status", { silent: true });

            var self = this,
                xhr = Backbone.ajax({
                    url: this.urlAction(),
                    type: "POST",
                    dataType: "json",
                    headers: {
                        "Accept": "application/json",
                        "x-jrs-base-url" : this.contextPath
                    },
                    data: {
                        jr_ctxid: this.get("requestId"),
                        jr_action: json3.stringify(action)
                    }
                })
                .done(function(response) {
                    if (!self.set("requestId", response.result.contextid)) {
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

        cancel: function() {
            var self = this,
                xhr = this.execution.cancel()
                    .done(function(response) {
                        if (!response) {
                            return;
                        }

                        if (!self.set("status", response.value)) {
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

        applyParameters: function(refresh) {
            this.unset("status", { silent: true });

            return this.execution.applyParameters(refresh);
        },

        getExport: function(format) {
            return this.exports.find(function(model) {
                return model.get("options").outputFormat === format;
            });
        },

        addExport: function(attrs) {
            return this.exports.add(attrs);
        },

        // mock _notify function from jasperreports-report
        _notify: function() {},

        // fake event manager for jive.interactive.column
        eventManager: {
            registerEvent: function() {
                return {
                    trigger: function() {}
                }
            }
        }
    });

    _.extend(ReportModel.prototype, reportStatusTrait);

    return ReportModel;
});