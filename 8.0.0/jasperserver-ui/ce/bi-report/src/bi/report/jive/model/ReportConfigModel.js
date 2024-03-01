/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import jiveTypes from '../enum/jiveTypes';


export default Backbone.Model.extend({
    defaults: function () {
        return {
            id: undefined,
            reportConfig: undefined,
            type: jiveTypes.REPORT_CONFIG
        };
    },
    parse: function({ reportConfig, ...rest }) {
        let processedConfig = new Map();

        if (reportConfig.length > 0) {
            reportConfig.forEach(element => {
                processedConfig[element.suffix] = element.value;
            });
        }

        return { reportConfig: processedConfig, ...rest };
    }
});