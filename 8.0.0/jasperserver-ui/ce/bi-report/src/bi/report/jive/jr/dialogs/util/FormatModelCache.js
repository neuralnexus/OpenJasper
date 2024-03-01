/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
var FormatModelCache = function () {
    this.map = {};
    this.keyInfo = {};
};
FormatModelCache.prototype = {
    get: function (key) {
        if (this.map[key]) {
            return this.map[key].current;
        }
        return null;
    },
    set: function (key, stateJSON) {
        if (!this.map[key]) {
            this.map[key] = {
                original: _.cloneDeep(stateJSON),
                current: _.cloneDeep(stateJSON)
            };
        } else {
            this.map[key].current = _.cloneDeep(stateJSON);
        }
    },
    createKey: function (applyTo, model, isGroup) {
        var key;
        if (!isGroup) {
            key = applyTo + '-column-' + model.get('columnIndex');
        } else {
            key = applyTo + '-column-' + model.get('forColumns').join('_');
        }
        if (!this.keyInfo[key]) {
            this.keyInfo[key] = {
                applyTo: applyTo,
                model: model
            };
        }
        return key;
    },
    clear: function () {
        this.map = {};
        this.keyInfo = {};
    },
    remove: function () {
    }
};
export default FormatModelCache;