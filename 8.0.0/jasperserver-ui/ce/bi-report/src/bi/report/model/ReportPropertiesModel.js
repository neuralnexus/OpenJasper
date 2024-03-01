/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import _ from 'underscore';
function isFloatingHeaderEnabled(component) {
    var defaultJiveUi = this.get('defaultJiveUi'), property = 'floating' + component + 'HeadersEnabled', enabled = false;
    !_.isUndefined(defaultJiveUi) && !_.isUndefined(defaultJiveUi[property]) && (enabled = defaultJiveUi[property]);
    return enabled;
}
var ReportPropertiesModel = Backbone.Model.extend({
    defaults: {
        pages: 1,
        autoresize: true,
        centerReport: false,
        useReportZoom: false,
        modalDialogs: true,
        chart: {},
        loadingOverlay: true,
        scale: 1
    },
    isDefaultJiveUiEnabled: function () {
        var defaultJiveUi = this.get('defaultJiveUi');
        return _.isUndefined(defaultJiveUi) || _.isUndefined(defaultJiveUi.enabled) || defaultJiveUi.enabled;
    },
    isFloatingTableHeaderEnabled: function () {
        return isFloatingHeaderEnabled.call(this, 'Table');
    },
    isFloatingCrosstabHeaderEnabled: function () {
        return isFloatingHeaderEnabled.call(this, 'Crosstab');
    }
});
export default ReportPropertiesModel;