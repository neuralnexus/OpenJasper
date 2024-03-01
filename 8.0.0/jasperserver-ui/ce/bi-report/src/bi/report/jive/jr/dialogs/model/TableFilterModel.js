/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Epoxy from 'backbone.epoxy';
import _ from 'underscore';
export default Epoxy.Model.extend({
    defaults: {
        operator: null,
        value: ""
    },
    computeds: {
        isMultiValueOperator: function () {
            var op = this.get('operator');
            return op && op.toLowerCase().indexOf('between') !== -1 ? true : false;
        },
        isNotNullCheckOperator: function() {
            var op = this.get('operator'),
                isNullCheck = op && op.toLowerCase().indexOf('null') !== -1 ? true : false;
            return !isNullCheck;
        }
    },
    initialize: function () {
        this.on('change:operator', this._onOperatorChange);
        Epoxy.Model.prototype.initialize.apply(this, arguments);
    },
    reset: function () {
        this.clear({ silent: false }).set(this.defaults);
        return this;
    },
    _onOperatorChange: function () {
        var currentValue = this.get('value'), operator = this.get('operator'), isMultiValue, isNullCheck;
        isMultiValue = operator && operator.toLowerCase().indexOf('between') !== -1;
        isNullCheck = operator && operator.toLowerCase().indexOf('null') !== -1;

        if (isMultiValue === true) {
            if (!_.isArray(currentValue)) {
                this.set({ value: [currentValue] });
            }
        } else if (isMultiValue === false) {
            if (_.isArray(currentValue)) {
                this.set({ value: currentValue[0] });
            }
        }

        if (isNullCheck) {
            this.set({ value: '' });
        }
    },
    remove: function () {
    }
});