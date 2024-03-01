/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import ReportView from './view/ReportView';
import ReportStateStack from './model/ReportStateStack';
import PageComponentMetaCollection from './jive/collection/PageComponentMetaCollection';
import ReportComponentMetaCollection from './jive/collection/ReportComponentMetaCollection';
import ReportComponentCollection from './jive/collection/ReportComponentCollection';
import ReportModel from './model/ReportModel';
import ExportModel from './model/ReportExportModel';
import biComponentErrorFactoryReportProxy from './error/biComponentErrorFactoryReportProxy';
import reportEvents from './enum/reportEvents';
import reportStatuses from './enum/reportStatuses';
import reportOutputFormats from './enum/reportOutputFormats';
import request from 'js-sdk/src/common/transport/request';

import logger from "js-sdk/src/common/logging/logger";

let log = logger.register("ReportController");

function runNonFillingAction(action, options) {
    var dfd = new $.Deferred(),
        self = this,
        opts = _.extend({
            hideOverlay: false,
            noStateStack: false
        }, options),
        actionNames = extractActionNames(action);

    if (!opts.hideOverlay) {
        this.view.showOverlay();
    }
    self.trigger(reportEvents.BEFORE_ACTION, actionNames);
    this.model
        .runAction(action, true)
        .then(function(result) {
            if(!opts.hideOverlay) {
                self.view.hideOverlay();
            }
            if(!opts.noStateStack) {
                self.stateStack.newState();
            }
            dfd.resolve(result);
        }, function(response) {
            if(!opts.hideOverlay) {
                self.view.hideOverlay();
            }
            dfd.reject(response);
        });

    dfd.always(function() {
        self.trigger(reportEvents.AFTER_ACTION, actionNames);
    });

    return dfd;
}

function extractActionNames(action) {
    var result;
    if (_.isArray(action)) {
        result = _.pluck(action, "actionName");
    } else {
        result= [action.actionName];
    }
    return result;
}

function runAction(action, options) {
    var dfd = new $.Deferred(),
        self = this,
        actionResponse,
        shouldCancel = { cancel: false },
        actionNames= extractActionNames(action);

    this.view.showOverlay();
    self.trigger(reportEvents.BEFORE_ACTION, extractActionNames(action), shouldCancel);
    if (!shouldCancel.cancel) {
        this.model
            .runAction(action)
            .then(function(response) {
                actionResponse = response;
                return self.model.updateStatus();
            }, function(xhr) {
                return dfd.reject(xhr);
            })
            .then(function() {
                if (self.model.isFailed() || self.model.isCancelled()) {
                    dfd.reject({
                        source: "execution",
                        status: self.model.get("status"),
                        errorDescriptor: self.model.get("errorDescriptor")
                    });
                } else {
                    action.silent || self.trigger(reportEvents.AFTER_REPORT_EXECUTION);

                    self.fetchPageHtmlExportAndJiveComponents({
                        silent: action.silent,
                        needsRerender: true
                    }).done(function() {
                        dfd.resolve(actionResponse);
                    }).fail(dfd.reject);
                }
            }, function(xhr) {
                dfd.reject(xhr);
            });

        dfd.fail(function(response) {
            action = _.isArray(action) ? _.reduce(action) : action;
            var handlerError;
            var actionName = action.actionName + "Data";
            var actionObject = action[actionName];
            var showErrorDialog = options && options.showErrorDialog;

            if (response.readyState === 4 &&
                response.status === 500) {

                if(showErrorDialog) {
                    if(actionObject.chartComponentUuid) {
                        handlerError = self.components.find(function (model) {
                            return model.get("chartUuid") == actionObject.chartComponentUuid;
                        })
                    } else {
                        handlerError = self.components.get(actionObject[actionObject.chartComponentUuid ? "chartComponentUuid" : "tableUuid"]);
                    }
                }

                if (handlerError && handlerError.handleServerError) {
                    handlerError.handleServerError(_.extend({}, action, response.responseJSON.result));
                }
            }

            if (response.type === "highchartsInternalError") {
                if(showErrorDialog) {
                    if(actionObject.chartComponentUuid) {
                        handlerError = self.components.find(function (model) {
                            return model.get("chartUuid") == actionObject.chartComponentUuid;
                        })
                    } else {
                        handlerError = self.components.get(actionObject[actionObject.chartComponentUuid ? "chartComponentUuid" : "tableUuid"]);
                    }
                }
                if (handlerError && handlerError.handleClientError) {
                    handlerError.handleClientError(response);
                }
            }

            self.view.hideOverlay();
        }).always(function() {
            self.trigger(reportEvents.AFTER_ACTION, actionNames);
        });
    } else {
        dfd.reject({ canceled: true });
    }

    return dfd;
}

function getResponsiveInterval(width, breakpoints) {
    let interval = null;
    for (let i = 0; i < breakpoints.length; i = i + 1) {
        const breakpoint = breakpoints[i];
        if (width < breakpoint) {
            interval = i === 0 ? `(min, ${breakpoint})` : `[${breakpoints[i - 1]}, ${breakpoint})`;
            break;
        } else if (i === breakpoints.length - 1) {
            interval = `[${breakpoint}, max)`;
        }
    }
    return interval;
}

function ReportController(stateModel) {
    var self = this;

    this.model = new ReportModel();
    this.stateModel = stateModel;

    this.pageComponentsMeta = new PageComponentMetaCollection([], {
        report: this.model
    });

    this.reportComponentsMeta = new ReportComponentMetaCollection([], {
        report: this.model
    });

    this.components = new ReportComponentCollection([], {
        report: this.model ,
        pageComponentsMeta: this.pageComponentsMeta,
        reportComponentsMeta: this.reportComponentsMeta,
        stateModel: stateModel
    });

    this.view = new ReportView({
        model: this.model,
        collection: this.components,
        stateModel: stateModel
    });
    this.stateStack = new ReportStateStack();

    // dirty hack to make JIVE work now
    this.model.components = this.components;
    this.model.config = {
        container: this.view.$el
    };

    if (!this.model.getExport(reportOutputFormats.HTML)) {
        this.model.addExport({ options: { outputFormat: reportOutputFormats.HTML } });
    }

    this.listenTo(this.model.getExport(reportOutputFormats.HTML), "change:outputFinal", function(){
        self.trigger(reportEvents.PAGE_FINAL, this.model.getExport(reportOutputFormats.HTML).getHTMLOutput());
    });

    this.listenTo(this.model.getExport(reportOutputFormats.HTML), "change:pageMeta", function() {
        self.trigger(reportEvents.PAGE_META_CHANGED, this.model.getExport(reportOutputFormats.HTML).get("pageMeta"));
    });

    this.listenTo(this.components, reportEvents.ACTION, this.runReportAction);

    this.listenTo(this.model, "change:status", function() {
        log.info("Report status changed to '" + self.model.get("status") + "'");

        if (self.model.isReady() || self.model.isEmpty()) {
            self.model.update()
                .done(function() {
                    if (self.fetchExportDfd) {
                        self.fetchExportDfd.done(function() {
                            log.info("Report total pages number is " + self.model.get("totalPages"));
                            var exp = self.model.getExport(reportOutputFormats.HTML);
                            if(exp.get("outputFinal")){
                                self.fetchReportJiveComponents()
                                    .then(function() {
                                        // HTML is final, don't need to reload.
                                        self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"));
                                    }, function () {
                                        var args = Array.prototype.slice.call(arguments);
                                        args.unshift(reportStatuses.FAILED);
                                        args.unshift(reportEvents.REPORT_COMPLETED);

                                        self.trigger.apply(self, args);
                                    });
                            } else if (!exp.get("outputEmpty")) {
                                self.fetchPageHtmlExportAndJiveComponents({ needsRerender: true }).fail(function () {
                                    var args = Array.prototype.slice.call(arguments);
                                    args.unshift(reportStatuses.FAILED);
                                    args.unshift(reportEvents.REPORT_COMPLETED);

                                    self.trigger.apply(self, args);
                                });
                            } else {
                                self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"));
                            }
                        });
                    }
                });
        } else if (self.model.isCancelled() || self.model.isFailed()) {
            var errorDescriptor = self.model.get("errorDescriptor");
            if (self.model.isFailed() && errorDescriptor != null && errorDescriptor.message) {
                log.error(errorDescriptor.message);
            }
            self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"), {
                source: "execution",
                status: self.model.get("status"),
                errorDescriptor: errorDescriptor
            });
        }
    });

    this.listenTo(this.model, "change:pageStatus", function() {
        var htmlExport = self.model.getExport(reportOutputFormats.HTML);
        if (!self.model.isCompleted() && !htmlExport.get("outputFinal")
                && (self.model.get("pageStatus").pageFinal
                    || self.model.get("pageStatus").timestamp > htmlExport.get("outputTimestamp"))) {
            log.info("page updated from " + htmlExport.get("outputTimestamp") + " to " + self.model.get("pageStatus").timestamp);
            self.fetchPageHtmlExportAndJiveComponents({ needsRerender: true });
        }
    });

    this.on(reportEvents.REQUESTED_PAGES_READY, function(needsRerender) {
        if (!self.model.isCompleted()) {
            self.model.waitForExecution();
        }

        if (needsRerender) {
            // the view already rendered; triggering new render
            self.viewRenderDfd && self.viewRenderDfd.done(function() {
                self.view.renderReport();
                var renderJiveDfd = self.view.renderJive();
                if (self.model.isReady()) {
                    renderJiveDfd.done(function() {
                        self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"));
                    });
                }
            });
        } else {
            if (self.model.isReady()) {
                // the view may have not rendered already
                self.afterViewRenderDfd = new $.Deferred();
                self.afterViewRenderDfd.done(function() {
                    self.trigger(reportEvents.REPORT_COMPLETED, self.model.get("status"));
                });
            }
        }
    });

    this.on(reportEvents.AFTER_REPORT_EXECUTION, function() {
        // unset totalPages to allow listener to be retriggered
        self.model.unset("totalPages", { silent: true });

        self.model.execution.set({"pages": 1, "anchor": undefined });
        _.extend(self.model.getExport(reportOutputFormats.HTML).get("options"), { "pages": 1, "anchor": undefined });
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

    runReportAction: function(action, options) {
        var self = this;

        return runAction.call(this, action, options)
            .done(function() {
                self.stateStack.newState();
            });
    },

    searchReportAction: function(options) {
        var action = {
            actionName: "search",
            searchData: {}
        };

        if (typeof options === "string") {
            action.searchData.searchString = options;
        } else {
            action.searchData = {
                searchString: options.text,
                caseSensitive: options.caseSensitive,
                wholeWordsOnly: options.wholeWordsOnly
            }
        }

        return runNonFillingAction.call(this, action, { noStateStack: true });
    },

    save: function(options) {
        return runNonFillingAction.call(this,
            _.extend(options || {}, { actionName: "saveReport" }), { noStateStack: true });
    },

    runZoomAction: function(options) {
        return runNonFillingAction.call(this,
            _.extend(options || {}, { actionName: "saveZoom" }),
            { hideOverlay: true, });
    },

    executeReport: function(refresh) {
        var dfd = new $.Deferred(),
            self = this;

        this.model
            .execute({freshData: !!refresh})
            .then(function() {
                self.fetchExportDfd = self.fetchPageHtmlExportAndJiveComponents();
                return self.fetchExportDfd;
            }, dfd.reject)
            .then(function() {
                self.stateStack.newState();
                dfd.resolve.apply(dfd, arguments);
            }, dfd.reject);

        return dfd;
    },

    cancelReportExecution: function(bAsync) {
        if (this.fetchExportDfd && this.fetchExportDfd.state() === "pending") {
            this.fetchExportDfd.reject({
                source: "execution",
                status: "cancelled"
            });
        }

        return this.model.cancel(bAsync);
    },

    removeReportExecution: function() {
        var dfd = new $.Deferred(),
            self = this;
        this.cancelReportExecution().done(function(){
            self.model.removeExecution().done(dfd.resolve.bind(dfd)).fail(dfd.reject.bind(dfd))
        }).fail(dfd.reject.bind(dfd));

        return dfd;
    },

    applyReportParameters: function(refresh) {
        var dfd = new $.Deferred(),
            self = this;

        this.fetchExportDfd &&
            this.fetchExportDfd.state() === "pending" &&
            this.fetchExportDfd.reject({
                source: "execution",
                status: "cancelled"
            });

        this.model
            .applyParameters(refresh)
            .then(function() {
                self.trigger(reportEvents.AFTER_REPORT_EXECUTION);
                self.fetchExportDfd = self.fetchPageHtmlExportAndJiveComponents();
                return self.fetchExportDfd;
            }, dfd.reject)
            .then(dfd.resolve, dfd.reject);

        return dfd;
    },

    fetchPageHtmlExportAndJiveComponents: function(options) {
        var dfd = new $.Deferred(),
            exportDfd,
            outputDfd;

        log.debug("Start fetching of html and JIVE");

        dfd.fail(function(error){
            exportDfd && exportDfd.state() === "pending" && (exportDfd.reject ? exportDfd.reject(error) : exportDfd.abort(error));
            outputDfd && outputDfd.state() === "pending" && (outputDfd.reject ? outputDfd.reject(error) : outputDfd.abort(error));
        });

        var ignoreCancelledReportExecution = (options && options.ignoreCancelledReportExecution) || false;
        if (!ignoreCancelledReportExecution && (this.model.isFailed() || this.model.isCancelled())) {
            dfd.reject({
                source: "execution",
                status: this.model.get("status"),
                errorDescriptor: this.model.get("errorDescriptor")
            });
        } else {
            var self = this,
                htmlExport = this.model.getExport(reportOutputFormats.HTML),
                exportSettings = {};

            if (ignoreCancelledReportExecution) {
                htmlExport.set({
                    status: "__ignore_cancelled__"
                }, { silent: true });

                exportSettings.ignoreCancelledReportExecution = true
            }

            exportDfd = htmlExport.run(exportSettings);
            exportDfd.then(function () {
                if (htmlExport.isFailed() || htmlExport.isCancelled()) {
                    dfd.reject({
                        source: "export",
                        format: reportOutputFormats.HTML,
                        status: htmlExport.get("status"),
                        errorDescriptor: htmlExport.get("errorDescriptor")
                    });
                } else {
                    outputDfd = htmlExport.waitForExport();
                    outputDfd
                        .then(_.bind(htmlExport.fetchOutput, htmlExport))
                        .then(function(response, status, jqXhr) {
                            self.trigger(reportEvents.REPORT_HTML_READY);
                            if (response) {
                                if (self.stateModel.get("useReportZoom")) {
                                    if (htmlExport.get("outputZoom")) {
                                        self.stateModel.set({
                                            scale: htmlExport.get("outputZoom")
                                        });
                                    }
                                }

                                var lastPartialPage = parseInt(jqXhr.getResponseHeader("output-lastPartialPage"), 10);
                                if (!isNaN(lastPartialPage)) {
                                    self.model.set("lastPartialPage", lastPartialPage);
                                }

                                var snapshotSaveStatus = jqXhr.getResponseHeader("report-snapshotSaveStatus");
                                if (snapshotSaveStatus) {
                                    self.model.set("snapshotSaveStatus", snapshotSaveStatus);
                                }

                                try {
                                    var currentPage = parseInt(jqXhr.getResponseHeader("report-pages"), 10),
                                        previousPage = self.stateModel.previous("pages");

                                    if (!isNaN(currentPage)) {
                                        self.model.execution.set("pages", currentPage, { silent: true });

                                        previousPage = parseInt(_.isObject(previousPage) ? previousPage.pages : previousPage, 10);

                                        if (currentPage !== previousPage) {
                                            log.debug("Fetching of html and JIVE: fires CURRENT_PAGE_CHANGED");
                                            self.trigger(reportEvents.CURRENT_PAGE_CHANGED, currentPage);
                                        }
                                    }
                                } catch(e) {
                                    log.error("Failed to parse 'report-pages' response header from server", e);
                                }

                                self.pageComponentsMeta.fetch()
                                    .then(_.bind(self.components.fetch, self.components), dfd.reject)
                                    .then(function () {
                                        if (options && options.silent === true) {
                                            log.debug("Finish fetching of html and JIVE: silent");
                                            dfd.resolve();
                                        } else {
                                            log.debug("Finish fetching of html and JIVE: fires REQUESTED_PAGES_READY");
                                            self.trigger(reportEvents.REQUESTED_PAGES_READY,
                                                options && options.needsRerender === true ? true : false);
                                            dfd.resolve();
                                        }
                                    }, function(error){
                                        dfd.state() === "pending" && dfd.reject(error);
                                    });
                            } else {
                                log.debug("Report is empty! Nothing to do!");
                                if (!(options && options.silent === true)) {
                                    self.trigger(reportEvents.REQUESTED_PAGES_READY, self, dfd);
                                }
                                self.model.set({ status: reportStatuses.EMPTY });
                                dfd.resolve();
                            }
                        },
                        function() {
                            if (htmlExport.isFailed() || htmlExport.isCancelled()) {
                                self.model.set({
                                    status: htmlExport.get("status"),
                                    errorDescriptor: htmlExport.get("errorDescriptor")
                                }, { silent: true });
                                dfd.reject({
                                    source: "export",
                                    format: reportOutputFormats.HTML,
                                    status: htmlExport.get("status"),
                                    errorDescriptor: htmlExport.get("errorDescriptor")
                                });
                            } else {
                                dfd.reject();
                            }
                        });
                }
            }, function(error){
                dfd.state() === "pending" && dfd.reject(error);
            });
        }

        return dfd;
    },

    renderReport: function() {
        var self = this;
        this.viewRenderDfd = this.view.render();
        this.viewRenderDfd.done(function() {
            if (self.afterViewRenderDfd) {
                self.afterViewRenderDfd.resolve();
            }
        });
        return this.viewRenderDfd;
    },

    fetchReportJiveComponents: function() {
        var dfd = new $.Deferred();

        log.debug("Start fetching of report-level JIVE components");

        if (this.model.isFailed() || this.model.isCancelled()) {
            dfd.reject({
                source: "execution",
                status: this.model.get("status"),
                errorDescriptor: this.model.get("errorDescriptor")
            });
        } else {
            this.reportComponentsMeta.fetch()
                .then(_.bind(this.components.fetchReportComponents, this.components), dfd.reject)
                .then(function () {
                    log.debug("Finish fetching of report-level JIVE components");
                    dfd.resolve();
                }, function(error){
                    dfd.state() === "pending" && dfd.reject(error);
                });
        }

        return dfd;
    },

    exportReport: function(options) {
        var dfd = new $.Deferred();
        if (this.model.isFailed() || this.model.isCancelled()) {
            var err = biComponentErrorFactoryReportProxy.reportStatus({
                source: "execution",
                status: this.model.get("status"),
                errorDescriptor: this.model.get("errorDescriptor")
            });

            dfd.reject(err);
        } else {
            options || (options = {});

            var exportOptions = _.pick(options, "outputFormat", "ignorePagination");

            if (_.isObject(options.pages)) {
                exportOptions.pages = options.pages.pages;
                exportOptions.anchor = options.pages.anchor;
            } else {
                exportOptions.pages = options.pages;
                exportOptions.anchor = undefined;
            }

            var exportModel = new ExportModel({options: exportOptions}, {report: this.model}),
                wait = _.bind(exportModel.waitForExport, exportModel);

            exportModel.run().then(wait).then(function() {
                //TODO: extend link with export mime-type info, resource name
                dfd.resolve({href: exportModel.urlOutput()}, function(options){
                    options = _.defaults(options || {}, {
                        url: exportModel.urlOutput(),
                        type: "GET",
                        headers: {
                            "Accept": "text/plain, application/json"
                        },
                        dataType: "text",
                        data: {
                            suppressContentDisposition: true
                        }
                    });
                    return request(options);
                });
            }, function() {
                if (exportModel.isFailed() || exportModel.isCancelled()) {
                    var err = biComponentErrorFactoryReportProxy.reportStatus({
                        source: "export",
                        format: options.outputFormat,
                        status: exportModel.get("status"),
                        errorDescriptor: exportModel.get("errorDescriptor")
                    });

                    dfd.reject(err);
                }
            });
        }
        return dfd;
    },

    responsiveBreakpointChanged: function({ width, previousWidth }) {
        const reportConfig = this.components.getReportConfig();
        if (reportConfig) {
            const breakpointsProp = reportConfig["responsive.breakpoints"];

            if (breakpointsProp) {
                const breakpoints = breakpointsProp
                    .split(",")
                    .map(element => {
                        const parsed = parseInt(element, 10);
                        if (isNaN(parsed)) {
                            throw new Error(`Unable to parse number '${element}' in 'responsive.breakpoints' report configuration!`);
                        }
                        return parsed;
                    });

                const currentInterval = getResponsiveInterval(width, breakpoints);
                const previousInterval = getResponsiveInterval(previousWidth, breakpoints);

                if (previousInterval != currentInterval) {
                    return true;
                }
            }
        }
        return false;
    },

    destroy: function() {
        return this.removeReportExecution().done(_.bind(this.view.remove, this.view));
    }
});

export default ReportController;
