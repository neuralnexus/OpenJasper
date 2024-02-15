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
 * @author: Igor Nesterenko
 * @version: $Id: TableComponentModel.js 47331 2014-07-18 09:13:06Z kklein $
 */
define(function (require) {

    var BaseComponentModel = require("./BaseComponentModel"),
        jiveTypes = require("../enum/jiveTypes"),
        $ = require("jquery"),
        _ = require("underscore");

    var genericProperties = null;

    return BaseComponentModel.extend({

        defaults: function() {
            return {
                calendarPatterns: {},
                filterPatterns: {},
                fontSizes: [],
                fonts: {},
                operators: {},
                patterns: {},
                id: null,
                genericProperties : {},
                module: "jive.table",
                type: jiveTypes.TABLE,
                uimodule: "jive.interactive.column"
            };
        },

        initialize: function(o){
            this.config = {
                id: null,

                /**
                 * {"1":{"index":"1","label":"Name","uuid":"ace5fd47-03c8-4d26-b2c0-354ca60560e0","visible":false,"interactive":true},..}
                 */
                allColumnsData: null
            };
            $.extend(this.config, o);

            if(o.genericProperties) {
                genericProperties = o.genericProperties;
            } else {
                this.config.genericProperties = genericProperties;
            }

            this.columns = [];
            this.columnMap = {};
        },

        registerPart: function(column) {

//            var column = new Column(partConfig);
            column.parent = this;
            this.columns[column.get("columnIndex")] = column;
            this.columnMap[column.get("id")] = column;
        },

        getId: function() {
            return this.config.id;
        }
    });
});



