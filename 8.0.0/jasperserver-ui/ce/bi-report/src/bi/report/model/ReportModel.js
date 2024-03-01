/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseModel from 'js-sdk/src/common/model/BaseModel';
import ReportExecutionModel from './ReportExecutionModel';
import ReportExportCollection from '../collection/ReportExportCollection';
import _ from 'underscore';
import Backbone from 'backbone';
import $ from 'jquery';
import reportStatusTrait from './reportStatusTrait';
import reportStatuses from '../enum/reportStatuses';
import reportOutputFormats from '../enum/reportOutputFormats';

var STATUS_RETRY_TIMEOUT = 1000;
var ReportModel = BaseModel.extend({
    idAttribute: 'requestId',
    defaults: function () {
        return {
            exports: undefined,
            reportURI: undefined,
            requestId: undefined,
            executionId: undefined,
            status: undefined,
            totalPages: undefined,
            lastPartialPage: undefined,
            snapshotSaveStatus: undefined,
            canSave: true
        };
    },
    initialize: function (attrs, options) {
        var self = this;
        BaseModel.prototype.initialize.apply(this, arguments);
        options || (options = {});
        this.contextPath = options.contextPath;
        this.execution = new ReportExecutionModel({}, { report: this });
        this.exports = new ReportExportCollection(this.get('exports') || [], { report: this });
    },
    urlAction: function () {
        var url = this.contextPath;
        if (url[url.length - 1] !== '/') {
            url += '/';
        }
        url += "rest_v2/reportExecutions/" + this.get("requestId") + "/runAction";
        return url;
    },
    execute: function (options) {
        var self = this;
        this.unset('status', { silent: true });
        options || (options = {});
        this.execution.set(_.defaults(_.extend({}, options), {
            reportUnitUri: this.get('reportURI'),
            baseUrl: this.contextPath
        }));
        var xhr = this.execution.run().then(function(response, status, jqXHR) {
            if (self.has("executionId")) {
                self.set("requestId", self.get("executionId"));
            }
            if (!self.set(self.parse(response))) {
                return false;
            }
            if (jqXHR.getResponseHeader("jrio-execution-id") !== null) {
                self.set("canSave", false);
            }
            self.trigger('sync', self, response);
            return $.Deferred().resolve(response);
        }, function(response) {
            self.trigger('error', self, response);
            return $.Deferred().reject(response);
        });
        this.trigger('request', this, xhr);
        return xhr;
    },
    updateStatus: function () {
        var self = this,
            htmlExport = this.getExport(reportOutputFormats.HTML),
            xhr;
        if (!htmlExport || htmlExport.get("outputFinal") || htmlExport.get("outputEmpty")) {
            xhr = this.execution.status()
                .done(function (response) {
                    if (!self.set({
                        'status': htmlExport.get("outputEmpty") ? reportStatuses.EMPTY : response.value,
                        'errorDescriptor': response.errorDescriptor
                    })) {
                        return false;
                    }
                    self.trigger('sync', self, response);
                })
                .fail(function (response) {
                    self.trigger('error', self, response);
                });
        } else {
            xhr = this.execution.pageStatus(this.execution.get("pages"))
                .done(function(response) {
                    if (!self.set({
                        "status": response.reportStatus,
                        "pageStatus": {
                            "pageFinal": response.pageFinal,
                            "timestamp": response.pageTimestamp
                        },
                        "errorDescriptor": response.errorDescriptor
                    })) {
                        return false;
                    }
                    self.trigger('sync', self, response);
                })
                .fail(function(response) {
                    self.trigger('error', self, response);
                });
        }
        this.trigger('request', this, xhr);
        return xhr;
    },
    update: function () {
        var self = this,
            htmlExport = this.getExport(reportOutputFormats.HTML),
            xhr;
        xhr = this.execution.update().done(function (response) {
            var parsedResponse = self.parse(response);

            // maintain the EMPTY status if the HTML export was empty
            if (parsedResponse.status && htmlExport.get("outputEmpty") === true) {
                parsedResponse.status = reportStatuses.EMPTY;
            }

            if (!self.set(self.parse(parsedResponse))) {
                return false;
            }
            self.trigger('sync', self, response);
        }).fail(function (response) {
            self.trigger('error', self, response);
        });
        this.trigger('request', this, xhr);
        return xhr;
    },
    waitForExecution: function () {
        if (this.updateStatusTimer != null) {
            clearTimeout(this.updateStatusTimer);
            this.updateStatusTimer = null;
        }
        var self = this,
            dfd = new $.Deferred(),
            completeFunc = function () {
                if (self.isCancelled() || self.isFailed()) {
                    dfd.reject();
                } else if (self.isQueued() || self.isExecuting()) {
                    self.updateStatusTimer = setTimeout(function () {
                        self.updateStatus().done(completeFunc).fail(dfd.reject);
                    }, STATUS_RETRY_TIMEOUT);
                } else {
                    dfd.resolve();
                }
            };
        this.waitForExecutionDfd = dfd;
        this.updateStatus().done(completeFunc).fail(dfd.reject);
        return dfd;
    },
    runAction: function (action, isNonFillingAction = false) {
        if (!this.has('requestId')) {
            throw new Error('You must execute report first before running any action.');
        }
        if (!isNonFillingAction) {
            this.unset('status', { silent: true });
        }
        var self = this,
            jqXHR = Backbone.ajax({
                url: this.urlAction(),
                type: 'POST',
                dataType: 'json',
                headers: {
                    'Accept': 'application/json'
                },
                data: {
                    action: JSON.stringify(action)
                }
            }).then(function (response) { // success handler
                if (!self.set('requestId', response.result.contextid)) {
                    return false;
                }
                self.trigger('sync', self, response);

                if (response && response.result) {
                    return _.omit(response.result, "contextid");
                }
                return {};
            }, function (response) { // fail handler
                self.trigger('error', self, response);

                if (!isNonFillingAction) {
                    self.set({ status: reportStatuses.FAILED }, { silent: true });
                }

                return response;
            });

        this.trigger('request', this, jqXHR);
        return jqXHR;
    },
    cancel: function (bAsync) {
        this.exports.each(function(exp){
            exp.cancel();
        });

        this.updateStatusTimer && clearTimeout(this.updateStatusTimer);
        if (this.waitForExecutionDfd && this.waitForExecutionDfd.state() === "pending") {
            this.waitForExecutionDfd.reject({
                source: "execution",
                status: "cancelled"
            });
        }

        if (this.isCompleted()) {
            return new $.Deferred().resolve();
        }
        var self = this, xhr = this.execution.cancel(bAsync).done(function (response) {
            if (!response) {
                return;
            }

            var status = response.value || response.reportStatus;

            if (!self.set('status', status)) {
                return false;
            }
            if (response.lastPartialPage) {
                self.set('lastPartialPage', response.lastPartialPage);
            }
            if (response.totalPages) {
                self.set('totalPages', response.totalPages);
            }
            if (response.snapshotSaveStatus) {
                self.set('snapshotSaveStatus', response.snapshotSaveStatus);
            }

            self.trigger('sync', self, response);
        }).fail(function (response) {
            self.trigger('error', self, response);
        });
        this.trigger('request', this, xhr);
        return xhr;
    },
    removeExecution: function(){
        this.exports.each(function(exp){
            exp.cancel();
        });

        this.exports.reset([]);

        return this.execution.removeExecution();
    },
    applyParameters: function (refresh) {
        this.unset('status', { silent: true });
        return this.execution.applyParameters(refresh);
    },
    getExport: function (format) {
        return this.exports.find(function (model) {
            return model.get('options').outputFormat === format;
        });
    },
    addExport: function (attrs) {
        return this.exports.add(attrs);
    },
    // mock _notify function from jasperreports-report
    _notify: function () {
    },
    // fake event manager for jive.interactive.column
    eventManager: {
        registerEvent: function () {
            return {
                trigger: function () {
                }
            };
        }
    }
});
_.extend(ReportModel.prototype, reportStatusTrait);
export default ReportModel;
