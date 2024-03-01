/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import reportStatuses from '../enum/reportStatuses';
import request from 'js-sdk/src/common/transport/request';
var ATTACHMENT_PREFIX_PATTERN = '{contextPath}/rest_v2/reportExecutions/{reportExecutionId}/exports/{exportExecutionId}/attachments/';
export default Backbone.Model.extend({
    defaults: function () {
        return {
            'reportUnitUri': undefined,
            'async': true,
            //TODO: remove 'allowInlineScripts' after merging with report executor extention
            'allowInlineScripts': false,
            'markupType': 'embeddable',
            'outputFormat': undefined,
            'interactive': true,
            'freshData': false,
            'saveDataSnapshot': false,
            'transformerKey': null,
            'ignorePagination': false,
            'reportContainerWidth': null,
            'pages': 1,
            'anchor': undefined,
            'attachmentsPrefix': undefined,
            'baseUrl': undefined,
            'parameters': undefined
        };
    },
    urlRun: function () {
        var url = this.get('baseUrl');
        if ((url || url === '') && url[url.length - 1] !== '/') {
            url += '/';
        }
        url += 'rest_v2/reportExecutions';
        return url;
    },
    urlRemove: function () {
        return this.urlRun() + '/' + this.report.get('requestId');
    },
    urlUpdate: function () {
        if (!this.report.has('requestId')) {
            throw new Error('You must execute report before requesting it\'s execution details or status.');
        }
        return this.urlRun() + '/' + this.report.get('requestId');
    },
    urlExisting: function() {
        if (!this.report.has("executionId")) {
            throw new Error("No execution ID to work with!");
        }
        return this.urlRun() + "/" + this.report.get("executionId");
    },
    urlParameters: function (refresh) {
        return this.urlUpdate() + '/parameters?freshData=' + !!refresh;
    },
    urlStatus: function () {
        return this.urlUpdate() + '/status';
    },
    urlPageStatus: function(pageIndex) {
        return this.urlUpdate() + "/pages/" + pageIndex + "/status";
    },
    initialize: function (attrs, options) {
        options || (options = {});
        this.report = options.report;
        this.on('change:baseUrl', function () {
            this.set('attachmentsPrefix', ATTACHMENT_PREFIX_PATTERN.replace('{contextPath}', this.get('baseUrl')));
        }, this);
    },
    run: function () {
        if (!this.report.has("executionId")) {
            return Backbone.ajax({
                url: this.urlRun(),
                type: 'POST',
                processData: false,
                dataType: 'json',
                contentType: 'application/json',
                headers: {
                    'Accept': 'application/json'
                },
                data: JSON.stringify(this.toJSON())
            });
        } else {
            return Backbone.ajax({
                url: this.urlExisting(),
                type: "GET",
                dataType: "json",
                contentType: "application/json",
                headers: {
                    "Accept": "application/json"
                }
            });
        }
    },
    status: function () {
        return Backbone.ajax({
            type: 'GET',
            url: this.urlStatus(),
            dataType: 'json',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/status+json'
            }
        });
    },
    pageStatus: function(pageIndex) {
        return Backbone.ajax({
            type: "GET",
            url: this.urlPageStatus(pageIndex),
            dataType: "json",
            contentType: "application/json",
            headers: {
                "Accept": "application/json"
            }
        });
    },
    update: function () {
        return Backbone.ajax({
            url: this.urlUpdate(),
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/json'
            }
        });
    },
    cancel: function (bAsync) {
        return Backbone.ajax({
            url: this.urlStatus(),
            type: 'PUT',
            dataType: 'json',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/json'
            },
            data: JSON.stringify({
                value: reportStatuses.CANCELLED,
                asyncCancel: !!bAsync
            })
        });
    },
    applyParameters: function (refresh) {
        return Backbone.ajax({
            url: this.urlParameters(refresh),
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            headers: {
                'Accept': 'application/json'
            },
            data: JSON.stringify(this.has('parameters') ? this.get('parameters').reportParameter : [])
        });
    },
    remove: function() {
        return this.removeExecution();
    },

    removeExecution: function() {
        return request({
            url: this.urlRemove(),
            type: 'DELETE'
        });
    }
});