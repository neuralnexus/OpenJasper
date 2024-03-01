/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Epoxy from 'backbone.epoxy';
import _ from 'underscore';
export default Epoxy.Model.extend({
    defaults: function () {
        return {
            columnLabel: undefined,
            backgroundColor: undefined,
            align: undefined,
            pattern: undefined,
            font: {
                bold: false,
                italic: false,
                underline: false,
                size: 9,
                name: undefined,
                color: '000000'
            }
        };
    },
    computeds: function () {
        var ctds = {};
        // dinamically create computeds for each font property
        _.each(this.defaults().font, function (val, key) {
            ctds['font' + key.charAt(0).toUpperCase() + key.substring(1, key.length)] = {
                deps: ['font'],
                get: function (font) {
                    return font[key];
                },
                set: function(value) {
                    var newFont = _.extend({}, this.get('font')),
                        val = value;

                    if (key === 'name' && val != null && !val.trim().length) {
                        val = null;
                    }

                    newFont[key] = val;

                    return {
                        font: newFont
                    };
                }
            };
        });
        return ctds;
    },
    reset: function () {
        this.clear({ silent: true }).set(this.defaults());
        return this;
    },
    remove: function () {
    }
});