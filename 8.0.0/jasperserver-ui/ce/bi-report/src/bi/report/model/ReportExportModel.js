/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseModel from 'js-sdk/src/common/model/BaseModel';
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import reportStatusTrait from './reportStatusTrait';
import reportOutputFormats from '../enum/reportOutputFormats';
import reportStatuses from "../enum/reportStatuses";
var STATUS_RETRY_TIMEOUT = 1000;
function parseMarkup(markup) {
    var elements = $.parseHTML(markup, document);
    elements = _.filter(elements, function (node) {
        var $el = $(node), tagName = $el.prop('tagName');
        tagName = tagName ? tagName : '';
        return 'table' === tagName.toLowerCase();
    });
    if (elements.length === 0) {
        return false;
    }
    return elements;
}
var ReportExportModel = BaseModel.extend({
    defaults: function () {
        return {
            attachments: undefined,
            id: undefined,
            options: undefined,
            outputResource: undefined,
            outputFinal: false,
            outputEmpty: false,
            ignorePagination: undefined,
            pageMeta: {
                dataTimestampMessage: null
            },
            status: undefined
        };
    },
    initialize: function (attrs, options) {
        options || (options = {});
        this.report = options.report;
        this.isFirstRun = true;
        BaseModel.prototype.initialize.apply(this, arguments);
    },
    resetFirstRun: function () {
        this.isFirstRun = true;
    },
    url: function () {
        if (_.isUndefined(this.report.get('requestId'))) {
            throw new Error('You must execute report before fetching export.');
        }
        var url = this.report.contextPath;
        if (url[url.length - 1] !== '/') {
            url += '/';
        }
        url += 'rest_v2/reportExecutions/' + this.report.get('requestId') + '/exports';
        return url;
    },
    urlStatus: function () {
        if (_.isUndefined(this.get('id'))) {
            throw new Error('Export ID is not specified');
        }
        return this.url() + '/' + this.get('id') + '/status';
    },
    urlOutput: function () {
        if (_.isUndefined(this.get('id'))) {
            throw new Error('Export ID is not specified');
        }
        return this.url() + '/' + this.get('id') + '/outputResource';
    },
    urlAttachments: function() {
        if (_.isUndefined(this.get("id"))) {
            throw new Error("Export ID is not specified");
        }

        return this.url() + "/" + this.get("id")  + "/attachments/";
    },
    run: function (settings) {
        settings || (settings = {});
        var data = _.extend(//TODO: remove 'allowInlineScripts' after merging report executor extention
            this.report.execution.pick('outputFormat', 'pages', 'anchor',
                'attachmentsPrefix', 'allowInlineScripts', 'markupType', 'baseUrl', 'ignorePagination'),
            this.get('options') ? _.pick(this.get('options'), 'outputFormat', 'pages', 'anchor',
                'attachmentsPrefix', 'allowInlineScripts', 'markupType', 'baseUrl', 'ignorePagination') : {},
            settings);

        if (this.isFirstRun) {
            data.clearContextCache = true;
            this.isFirstRun = false;
        }

        return BaseModel.prototype.fetch.call(this, {
            url: this.url(),
            type: 'POST',
            contentType: 'application/json',
            headers: {
                Accept: 'application/json'
            },
            data: JSON.stringify(data)
        });
    },
    fetchOutput: function () {
        var self = this;
        return Backbone.ajax({
            url: this.urlOutput(),
            type: 'GET',
            headers: {
                'Accept': 'text/html, application/json'
            },
            dataType: this.get('options').outputFormat
        }).done(function (response, status, xhr) {
            var output = response;
            if (self.get('options').outputFormat === reportOutputFormats.HTML) {
                self.set({
                    outputEmpty: !response
                });
                output = response && response.markup ? response.markup : _.isUndefined(response) ? '' : response;
            }
            if (response) {
                self.set({
                    output: output,
                    outputFinal: xhr.getResponseHeader('output-final') === 'true',
                    outputTimestamp: parseInt(xhr.getResponseHeader("output-timestamp"))//TODO handle NaN?
                });

                var outputZoom = xhr.getResponseHeader('output-zoom');
                if (outputZoom) {
                    self.set({
                        outputZoom: outputZoom
                    });
                }
            } else {
                self.set({
                    output: output
                });
            }
        });
    },
    getHTMLOutput: function () {
        return parseMarkup(this.get('output'));
    },
    updateStatus: function () {
        var self = this, xhr = Backbone.ajax({
            type: 'GET',
            url: this.urlStatus(),
            dataType: 'json',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/status+json'
            }
        }).done(function (response) {
            if (!self.set({
                'status': response.value,
                'errorDescriptor': response.errorDescriptor,
                'pageMeta': {
                    dataTimestampMessage: response.dataTimestampMessage
                }
            })) {
                return false;
            }
            self.trigger('sync', self, response);
        }).fail(function (response) {
            self.trigger('error', self, response);
        });
        this.trigger('request', this, xhr);
        return xhr;
    },
    waitForExport: function () {
        var self = this,
            dfd = new $.Deferred(),
            completeFunc = function (response) {
                if (self.isCancelled() || self.isFailed()) {
                    dfd.reject(response);
                } else if (self.isQueued() || self.isExecuting()) {
                    self.timeout = setTimeout(function () {
                        self.updateStatus().done(completeFunc).fail(dfd.reject);
                    }, STATUS_RETRY_TIMEOUT);
                } else {
                    dfd.resolve();
                }
            };
        this.waitForExportDfd = dfd;
        this.updateStatus().done(completeFunc).fail(dfd.reject);
        return dfd;
    },

    cancel: function () {
        this.timeout && clearTimeout(this.timeout);
        if (this.waitForExportDfd && this.waitForExportDfd.state() === "pending") {
            this.waitForExportDfd.reject({
                source: "export",
                status: "cancelled"
            });
        }
        this.set({status: reportStatuses.CANCELLED});
    }
});
_.extend(ReportExportModel.prototype, reportStatusTrait);
export default ReportExportModel;