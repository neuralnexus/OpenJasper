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
 * @version $Id: dual.pie.service.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["highcharts-more"], function(Highcharts) {

    var JRDualPieSettingService = {
        perform: function(highchartsOptions, serviceData) {
            this.setupDualPie(highchartsOptions);
        },

        setupDualPie: function(options) {
            if (options.series.length != 2) {
                return;
            }

            var parentSeries = options.series[0];
            parentSeries.center = ['50%', '50%'];
            parentSeries.size = '60%';
            parentSeries.dataLabels = parentSeries.dataLabels || {};
            parentSeries.dataLabels.color = '#FFFFFF';
            parentSeries.dataLabels.distance = -30;

            var childSeries = options.series[1];
            childSeries.center = ['50%', '50%'];
            childSeries.innerSize = '60%';
            childSeries.size = '90%';

            var defaultColors = Highcharts.getOptions().colors;
            var defaultColorsIdx = 0;

            var childSeriesIdx = 0;
            for (var i = 0; i < parentSeries.data.length; ++i) {
                var parentSeriesItemColor = parentSeries.data[i].color;
                if (!parentSeriesItemColor) {
                    parentSeriesItemColor = defaultColors[defaultColorsIdx];
                    defaultColorsIdx = (defaultColorsIdx + 1) % defaultColors.length;
                    parentSeries.data[i].color = parentSeriesItemColor;
                }

                var childSeriesItemCount = parentSeries.data[i]._jrChildCount;
                if (childSeriesItemCount) {
                    for (var j = 0; j < childSeriesItemCount; ++j, ++childSeriesIdx) {
                        if (childSeriesIdx < childSeries.data.length && !childSeries.data[childSeriesIdx].color) {
                            var brightness = 0.2 - (j / childSeriesItemCount) / 5;
                            childSeries.data[childSeriesIdx].color = Highcharts.Color(parentSeriesItemColor).brighten(brightness).get();
                        }
                    }
                }
            }
        }
    };

    return JRDualPieSettingService;
});