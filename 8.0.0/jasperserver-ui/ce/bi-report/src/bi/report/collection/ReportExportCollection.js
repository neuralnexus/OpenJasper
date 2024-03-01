/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import ReportExportModel from '../model/ReportExportModel';
export default Backbone.Collection.extend({
    model: function (attrs, options) {
        return new ReportExportModel(attrs, { report: options.collection.report });
    },
    initialize: function (models, options) {
        this.report = options.report;
    }
});