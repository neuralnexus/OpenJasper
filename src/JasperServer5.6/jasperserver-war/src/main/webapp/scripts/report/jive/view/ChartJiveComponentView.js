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
 * @version: $Id: ChartJiveComponentView.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BaseJiveComponentView =  require("./BaseJiveComponentView"),
        _ = require("underscore"),
        $ = require("jquery");

    var opacityCssTransparent = {
            "opacity": "0.3",
            "filter": "progid:DXImageTransform.Microsoft.Alpha(opacity=30)"
        },
        opacityCssVisible = {
            "opacity": "1",
            "filter": "progid:DXImageTransform.Microsoft.Alpha(opacity=100)"
        };

    return BaseJiveComponentView.extend({
        _renderComponent: function($el) {
            var dfd = new $.Deferred();
            BaseJiveComponentView.prototype._renderComponent.call(this, $el);

            var jiveChart = this.model.get("uiModuleType");

            if (jiveChart) {
                jiveChart.init(this.report);

                // fix top position of Chart Type button
                $el.find(".show_chartTypeSelector_wrapper").css(_.extend({ "top": "0" }, opacityCssTransparent));

                // fix top position of Chart Type dialog // - position calculating when opening dialog
                //$(".jive_chartTypeSelector").css("top", "5px");

                // make Chart Type selection button visible when active
                $el.find('.jive_chartSettingsIcon').on('mouseenter', function() {
                    $(this).parent().css(opacityCssVisible);
                });

                // make Chart Type selection button transparent when not active
                $('.jive_chartMenu').on('mouseleave touchend', function() {
                    $(this).parent().css(opacityCssTransparent);
                });

                // remove styles that are added manually to head in init method
                $("head #jive-chart-selector-stylesheet").remove();
            }

            dfd.resolve();

            return dfd;
        }
    });
});

