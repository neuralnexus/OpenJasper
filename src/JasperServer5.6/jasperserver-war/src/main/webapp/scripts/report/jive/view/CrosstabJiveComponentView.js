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
 * @version: $Id: CrosstabJiveComponentView.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BaseJiveComponentView =  require("./BaseJiveComponentView"),
        $ = require("jquery");

    return BaseJiveComponentView.extend({
        _renderComponent: function($el){
            var jiveCrosstab = this.model.get("uiModuleType"),
                dfd = new $.Deferred();

            // override to return correct element
            jiveCrosstab.getReportContainer = function() {
                return $el;
            };

            jiveCrosstab.init(this.report);

            // remove styles that are added manually to head in init method
            $("head #jivext-stylesheet").remove();

            dfd.resolve();

            return dfd;
        },

        _getModulesToLoad: function() {
            var modules = BaseJiveComponentView.prototype._getModulesToLoad.call(this);

            modules.push("csslink!jive.crosstab.templates.styles.css");

            return modules;
        }
    });
});

