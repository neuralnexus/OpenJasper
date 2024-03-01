/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import _ from 'underscore';
import BaseComponentMetaModel from '../model/BaseComponentMetaModel';
import reportOutputFormats from '../../enum/reportOutputFormats';

export default Backbone.Collection.extend({
    initialize: function (models, options) {
        this.report = options.report;
    },
    model: function (attrs, options) {
        return new BaseComponentMetaModel(attrs, options);
    },
    url: function () {
        return this.report.getExport(reportOutputFormats.HTML).urlAttachments()
            + "reportComponents.json";
    },
    fetch: function() {
        if (!this.report.has("requestId")) {
            throw new Error("You must run report first before fetching components.");
        }

        return Backbone.Collection.prototype.fetch.call(this, {
            type: "GET",
            reset: true,
            headers: {
                "Accept": "application/json",
                "x-jrs-base-url" : this.report.contextPath
            }
        });
    },

    parse: function(response) {
        // each component meta is bound to a property in the response object
        return _.values(response);
    }
});