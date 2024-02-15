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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: ReportStateStack.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone");

    return Backbone.Model.extend({
        defaults: function() {
            return {
                counter: 0,
                states: [],
                position: -1,
                canUndo: false,
                canRedo: false
            }
        },

        initialize: function() {
            this.on("change:position", function() {
                this.set({
                    "canUndo": this.hasPrevious(),
                    "canRedo": this.hasNext()
                });
            }, this);
        },

        newState: function() {
            if (this.get("position") + 2 < this.get("states").length) {
                this.get("states").splice(this.get("position") + 2, this.get("states").length - this.get("position") - 2);
            }

            this.set("counter", this.get("counter") + 1);
            this.get("states")[this.get("position") + 1] = this.get("counter");
            this.set("position", this.get("position") + 1);
        },

        previousState: function() {
            if (this.get("position") > 0) {
                this.set("position", this.get("position") - 1);
            }
        },

        firstState: function() {
            this.set("position", 0);
        },

        nextState: function() {
            if (this.get("position") + 1 < this.get("states").length) {
                this.set("position", this.get("position") + 1);
            }
        },

        hasPrevious: function() {
            return this.get("position") > 0;
        },

        hasNext: function() {
            return this.get("position") + 1 < this.get("states").length;
        },

        currentState: function() {
            return this.get("states")[this.get("position")];
        }
    });
});