/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import _ from 'underscore';
import BaseComponentMetaModel from '../model/BaseComponentMetaModel';

export default Backbone.Collection.extend({
    initialize: function (models, options) {
        this.report = options.report;
    },
    model: function (attrs, options) {
        return new BaseComponentMetaModel(attrs, options);
    },
    url: function () {
        var url = this.report.contextPath;
        if (url[url.length-1] !== "/") {
            url += "/";
        }
        url += "rest_v2/reportExecutions/" + this.report.get("requestId") + "/info";
        return url;
    },
    fetch: function () {
        if (!this.report.has('requestId')) {
            throw new Error('You must run report first before fetching components.');
        }
        return Backbone.Collection.prototype.fetch.call(this, {
            type: 'GET',
            reset: true,
            headers: {
                'Accept': 'application/json'
            }
        });
    },
    parse: function (response) {
        var res;
        if (response.errorCode){
            // the report is either cancelled or failed and this is handled already
            // some valid value is required to no cause errors
            res = this.toJSON()
        } else {
            // each component meta is bound to a property in the response object
            res = _.values(response);
        }
        return res;
    }
});