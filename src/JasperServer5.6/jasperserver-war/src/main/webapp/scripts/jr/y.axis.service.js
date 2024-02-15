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
 * @version $Id: y.axis.service.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "defaultSettingService"], function($, DefaultSettingService) {

    var YAxisService = {

        perform: function(highchartsOptions, serviceData) {
            var axis = this.getYAxis(highchartsOptions, serviceData.axisIndex);

            $.each(serviceData.props, function(i, option) {
                if (option) {
                    DefaultSettingService.setProperty(axis, option.prop, option.val, option.isFunction);
                }
            });
        },

        getYAxis: function(highchartsOptions, axisIndex) {
            var existingAxis = null;
            if (Object.prototype.toString.call(highchartsOptions.yAxis) === '[object Array]') {
                existingAxis = highchartsOptions.yAxis[axisIndex];
            } else if (axisIndex == 0) {
                existingAxis = highchartsOptions.yAxis;
            }

            if (existingAxis) {
                return existingAxis;
            }

            var newAxis = {};
            if (Object.prototype.toString.call(highchartsOptions.yAxis) === '[object Array]') {
                highchartsOptions.yAxis[axisIndex] = newAxis;
            } else if (axisIndex == 0) {
                highchartsOptions.yAxis = newAxis;
            } else {
                var axesArray = [];
                axesArray[0] = highchartsOptions.yAxis || {};
                axesArray[axisIndex] = newAxis;
                highchartsOptions.yAxis = axesArray;
            }
            return newAxis;
        }

    };

    return YAxisService;
});