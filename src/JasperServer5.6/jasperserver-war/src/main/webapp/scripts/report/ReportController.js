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
 * @version: $Id: ReportController.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        _ = require("underscore"),
        $ = require("jquery"),
        ReportView = require("./view/ReportView"),
        ReportStateStack = require("./model/ReportStateStack"),
        ReportComponentCollection = require("./jive/collection/ReportComponentCollection"),
        ReportModel = require("./model/ReportModel"),
        ExportModel = require("./model/ReportExportModel"),
        log =  require("logger").register("Report"),
        reportEvents = require("./enum/reportEvents"),
        reportStatuses = require("./enum/reportStatuses"),
        reportOutputFormats = require("./enum/reportOutputFormats");

    function runAction(action) {
        var dfd = new $.Deferred(),
            self = this;

        this.view.showOverlay();
        this.model
            .runAction(action)
            .then(_.bind(self.model.updateStatus, self.model), dfd.reject)
            .then(function() {
                if (self.model.isFailed() || self.model.isCancelled()) {
                    dfd.reject({
                        source: "execution",
                        status: self.model.get("status"),
                        errorDescriptor: self.model.get("errorDescriptor")
                    });
                } else {
                    self.trigger(reportEvents.AFTER_REPORT_EXECUTION);

                    self.fetchReportHtmlExportAndJiveComponents()
                        .done(dfd.resolve)
                        .fail(dfd.reject);
                }
            }, dfd.reject)
            .fail(function(response) {
                if (
                        action.actionName === "changeChartType" &&
                        response.readyState === 4 &&
                        response.status === 500
                    ) {
                    self.components.get(action.changeChartTypeData.chartComponentUuid).showTypeError();
                }
                self.view.hideOverlay();
            });

        return dfd;
    }

    function ReportController(properties) {
        var self = this,
            isDefaultJiveUiEnabled = _.isUndefined(properties.defaultJiveUi) ||
                                    _.isUndefined(properties.defaultJiveUi.enabled) ||
                                    properties.defaultJiveUi.enabled;

        this.model = new ReportModel();
        this.components = new ReportComponentCollection([], {
            report: this.model ,
            linkOptions: properties.linkOptions,
            isolateDom: properties.isolateDom
        });
        this.view = new ReportView({
            model: this.model,
            collection: this.components,
            isDefaultJiveUiEnabled: isDefaultJiveUiEnabled,
            isolateDom: properties.isolateDom
        });
        this.stateStack = new ReportStateStack();

        // dirty hack to make JIVE work now
        this.model.components = this.components;
        this.view.linkOptions = properties.linkOptions;
        this.model.config = {
            container: this.view.$el
        };

        if (!this.model.getExport(reportOutputFormats.HTML)) {
            this.model.addExport({ options: { outputFormat: reportOutputFormats.HTML } });
        }

        this.listenTo(this.model.getExport(reportOutputFormats.HTML), "change:outputFinal", function(){
            self.trigger(reportEvents.PAGE_FINAL, this.model.getExport(reportOutputFormats.HTML).get("output"));
        });

        this.listenTo(this.components, reportEvents.ACTION, this.runReportAction);

        this.listenTo(this.model, "change:status", function() {
            log.info("Report status changed to '" + self.model.get("status") + "'");

            if (self.model.isReady()) {
                self.model.update()
                    .done(function() {
                        log.info("Report total pages number is " + self.model.get("totalPages"));
                        if(self.model.getExport(reportOutputFormats.HTML).get("outputFinal")){
                            // HTML is final, don't need to reload.
                            self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"));
                        } else {
                            self.fetchReportHtmlExportAndJiveComponents().fail(function () {
                                    var args = Array.prototype.slice.call(arguments);
                                    args.unshift(reportStatuses.FAILED);
                                    args.unshift(reportEvents.REPORT_COMPLETED);

                                    self.trigger.apply(self, args);
                                });
                        }
                    });
            } else if (self.model.isCancelled() || self.model.isFailed()) {
                self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"), {
                    source: "execution",
                    status: self.model.get("status"),
                    errorDescriptor: self.model.get("errorDescriptor")
                });
            }
        });

        this.once(reportEvents.REQUESTED_PAGES_READY, function() {
            if (!self.model.isCompleted()) {
                self.model.waitForExecution();
            }

            // first REQUESTED_PAGES_READY event differs a bit from consequent events
            self.on(reportEvents.REQUESTED_PAGES_READY, function() {
                if (!self.model.isCompleted()) {
                    self.model.waitForExecution();
                }

                if (self._reportRendered) {
                    self.view.renderMarkup();
                    self.view.renderJive()
                        .done(function() {
                            if (self.model.isReady()) {
                                self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"));
                            }
                        });
                }
            });
        });

        this.on(reportEvents.AFTER_REPORT_EXECUTION, function() {
            self.model.execution.set("pages", 1);
            _.extend(self.model.getExport(reportOutputFormats.HTML).get("options"), { "pages": 1 });
        });
    }

    _.extend(ReportController.prototype, Backbone.Events, {
        undoReportAction: function() {
            var self = this;

            return runAction.call(this, { actionName: "undo" })
                .done(function() {
                    self.stateStack.previousState();
                });
        },

        undoAllReportAction: function() {
            var self = this;

            return runAction.call(this, { actionName: "undoAll" })
                .done(function() {
                    self.stateStack.firstState();
                });
        },

        redoReportAction: function() {
            var self = this;

            return runAction.call(this, { actionName: "redo" })
                .done(function() {
                    self.stateStack.nextState();
                });
        },

        runReportAction: function(action) {
            var self = this;

            return runAction.call(this, action)
                .done(function() {
                    self.stateStack.newState();
                });
        },

        executeReport: function(refresh) {
            var dfd = new $.Deferred(),
                self = this;

            this.model
                .execute({freshData: !!refresh})
                .then(_.bind(self.fetchReportHtmlExportAndJiveComponents, self), dfd.reject)
                .then(function() {
                    self.stateStack.newState();
                    dfd.resolve.apply(dfd, arguments);
                }, dfd.reject);

            return dfd;
        },

        cancelReportExecution: function() {
            return this.model.cancel();
        },

        applyReportParameters: function(refresh) {
            var dfd = new $.Deferred(),
                self = this;

            this.model
                .applyParameters(refresh)
                .then(_.bind(self.model.updateStatus, self.model), dfd.reject)
                .then(function() {
                    self.trigger(reportEvents.AFTER_REPORT_EXECUTION);

                    return self.fetchReportHtmlExportAndJiveComponents();
                }, dfd.reject)
                .then(dfd.resolve, dfd.reject);

            return dfd;
        },

        fetchReportHtmlExportAndJiveComponents: function() {
            var dfd = new $.Deferred();

            if (this.model.isFailed() || this.model.isCancelled()) {
                dfd.reject({
                    source: "execution",
                    status: this.model.get("status"),
                    errorDescriptor: this.model.get("errorDescriptor")
                });
            } else {
                var self = this,
                    htmlExport = this.model.getExport(reportOutputFormats.HTML);

                htmlExport
                    .run()
                    .then(function () {
                        if (htmlExport.isFailed() || htmlExport.isCancelled()) {
                            dfd.reject({
                                source: "export",
                                format: reportOutputFormats.HTML,
                                status: htmlExport.get("status"),
                                errorDescriptor: htmlExport.get("errorDescriptor")
                            });
                        } else {
                            htmlExport
                                .fetchOutput()
                                .then(_.bind(self.components.fetch, self.components), dfd.reject)
                                .then(function () {
                                    dfd.resolve();
                                    self.trigger(reportEvents.REQUESTED_PAGES_READY, self);
                                }, dfd.reject);
                        }
                    }, dfd.reject);
            }

            return dfd;
        },

        renderReport: function(container) {
            return this.view.render(container)
                .done(_.bind(function() { this._reportRendered = true; }, this));
        },

        exportReport: function(options) {
            var dfd = new $.Deferred();
            if (this.model.isFailed() || this.model.isCancelled()) {
                dfd.reject({
                    source: "execution",
                    status: this.model.get("status"),
                    errorDescriptor: this.model.get("errorDescriptor")
                });
            } else {
                // 'pages' must present in any case to override report properties
                options.pages = options.pages;
                var exportModel = new ExportModel({options: options}, {report: this.model}), wait = _.bind(exportModel.waitForExport, exportModel);

                exportModel.run().then(wait, dfd.reject).then(function() {
                    if (exportModel.isFailed() || exportModel.isCancelled()) {
                        dfd.reject({
                            source: "export",
                            format: options.outputFormat,
                            status: exportModel.get("status"),
                            errorDescriptor: exportModel.get("errorDescriptor")
                        });
                    } else {
                        //TODO: extend link with export mime-type info, resource name
                        dfd.resolve({href: exportModel.urlOutput()});
                    }
                });
            }
            return dfd;
        }
    });

    return ReportController;
});