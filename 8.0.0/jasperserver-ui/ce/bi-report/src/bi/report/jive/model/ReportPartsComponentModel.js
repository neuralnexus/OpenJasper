/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
export default BaseComponentModel.extend({
    defaults: function () {
        return {
            reportParts: [],
            id: undefined,
            type: jiveTypes.REPORTPARTS
        };
    },
    constructor: function (attrs, options) {
        options || (options = {});
        options.parse || (options = _.extend({}, options, { parse: true }));
        BaseComponentModel.call(this, attrs, options);
    },
    parse: function (data) {
        var setterObj = { id: data.id };
        setterObj.reportParts = this._processParts(data.parts);
        return setterObj;
    },
    _processParts: function (parts) {
        if (parts) {
            return _.map(parts, function (part) {
                return {
                    name: part.name,
                    page: part.idx + 1
                };
            });
        }
        return null;
    }
});