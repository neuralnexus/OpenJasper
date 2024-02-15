/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @author Sergey Prilukin
 * @version: $Id: DropDownManager.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * DropDownManager to handle dropdown parts of scalable components
 */

define(function (require) {
    'use strict';

    var _ = require("underscore");

    var RECALC_DROP_DOWN_POS_INTERVAL = 500;

    var DropDownManager = function(options) {
        this.initialize(options);
        return this;
    };

    _.extend(DropDownManager.prototype, {

        initialize: function(options) {
            _.bindAll(this, "checkDimensions", "setNewDimensions");
            this.$dropDownEl = options.dropDownEl;
            this.calcDimensions = options.calcDimensions;
            this.isDimensionsChanged = options.isDimensionsChanged || this._isDimensionsChanged;
            this.recalcInterval = options.recalcInterval || RECALC_DROP_DOWN_POS_INTERVAL;
            this.onDimensionChanged = options.onDimensionChanged;
        },

        /* API */

        startCalc: function() {
            this.checkDimensions(this.setNewDimensions);
            clearInterval(this.recalcTimer);
            this.recalcTimer = setInterval(
                _.bind(this.checkDimensions, this, this.onDimensionChanged),
                this.recalcInterval);
        },

        stopCalc: function() {
            clearInterval(this.recalcTimer);
        },

        /* Internal methods */
        checkDimensions: function(callback) {
            var newDimensions = this.calcDimensions();
            var currentDimensions = this.$dropDownEl.offset();

            if (this.$dropDownEl.width() !== newDimensions.width) {
                this.setNewDimensions(newDimensions);
            }

            if (this.isDimensionsChanged(currentDimensions, newDimensions)) {
                callback && callback(newDimensions);
            }
        },

        setNewDimensions: function(newDimensions) {
            this.$dropDownEl.css("top", newDimensions.top)
                .css("left", newDimensions.left)
                .css("width", newDimensions.width);
        },

        _isDimensionsChanged: function(currentDimensions, newDimensions) {
            //Math.floor is necessary for IE10 which can use float numbers for position
            return (Math.floor(currentDimensions.top) !== Math.floor(newDimensions.top)
                || Math.floor(currentDimensions.left) !== Math.floor(newDimensions.left));
        }
    });

    return DropDownManager;
});
