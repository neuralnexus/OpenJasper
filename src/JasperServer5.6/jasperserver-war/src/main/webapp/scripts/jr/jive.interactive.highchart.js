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
 * @version $Id: jive.interactive.highchart.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery.ui", "text!jive.chartSelector.tmpl", "csslink!jive.highcharts.vm.css"], function($, chartSelectorTemplates, css){
    var chartSelector = function() {
        var it = this;
        var initialized = false;

        it.showTypeError = function() {
            if (!it.errorDialog) {
                it.errorDialog = $(chartSelectorTemplates).find('.jive_chartTypeSelector');

                it.errorDialog.find(".body").html("Selected chart type not supported.");
                it.errorDialog.find(".title").html("Error");
                it.errorDialog
                    .appendTo("body")
                    .draggable()
                    .on('click touchend', '.closeIcon', function() {
                        it.errorDialog.hide();
                    });
            }

            var chartEl = $("#" + it.currentChart.get("hcinstancedata").renderto)[0];
            it.errorDialog.show().position({
                of: chartEl || 'body',
                at: 'center center',
                my: 'center center'
            });

            it.chartTypeSelector.find('div.cell').removeClass('selected');
            it.chartTypeSelector.find('div.cell[data-hcname="' + it.currentChart.config.charttype + '"]').addClass('selected');
        };

        it.init = function(report) {
        	var currentChartId = it.currentChart && it.currentChart.config.id;
        	it.currentChart = null;
        	
            it.chartIcon = $(chartSelectorTemplates).find('.show_chartTypeSelector_wrapper');
            
        	$.each(report.components.chart, function() {
        		var chart = this;
        		if (!chart.config.interactive) {
        			return;
        		}
        		
        		if (currentChartId == chart.config.id) {
        			it.currentChart = chart;
        		}
        		
	            var chartIcon = it.chartIcon.clone().insertBefore('#' + chart.config.hcinstancedata.renderto);
	            chartIcon.find('.jive_chartMenu').on('click touchend','li.jive_chartTypeMenuEntry', function(){
                    it.currentChart = chart;

                    var chartEl = $("#" + it.currentChart.get("hcinstancedata").renderto)[0];
                    it.chartTypeSelector.show().position({
           	            of: chartEl || 'body',
               	        at: 'center top',
                   	    my: 'center top',
                        collision: "none fit"
                   	});

                   	it.renderChartTypeSelector(chart.config.charttype, chart);
                	return false; //cancel event bubbling
            	});
        	});

            $('.jive_chartSettingsIcon').on('mouseenter', function() {
                var jo = $(this);
                jo.addClass('over');
                jo.next('.jive_chartMenu').show().position({
                    of: jo,
                    at: 'left bottom',
                    my: 'left top-1'
                })
            });

            $('.jive_chartMenu').on('mouseleave touchend', function() {
                var jo = $(this);
                jo.prev('.jive_chartSettingsIcon').removeClass('over');
                jo.hide();
            });

            $('.jive_chartMenu').on('mouseenter touchstart','p.wrap', function(){
                $(this).addClass('over');
            });

            $('.jive_chartMenu').on('mouseleave touchend','p.wrap', function(){
                $(this).removeClass('over');
            });

            if(!initialized) {
                it.chartTypeSelector = $(chartSelectorTemplates).find('.jive_chartTypeSelector');
                it.chartTypeSelector.appendTo('body').hide();
                it.chartTypeSelector.draggable();

                it.chartTypeSelector.on('click touchend', '.closeIcon', function() {
                    it.chartTypeSelector.hide();
                });

                it.chartTypeSelector.on('click touchstart', 'div.cell', function() {
                    var chartType;

                    if ($(this).hasClass('disabled')) {
                        return;
                    }

                    chartType = this.getAttribute('data-hcname');

                    if (chartType !== it.currentChart.config.charttype) {
                        it.renderChartTypeSelector(chartType, it.currentChart);
                        it.currentChart.changeType({type: chartType});
                    }
                });

                initialized = true;
            }
        }

        it.renderChartTypeSelector = function(chartType, chart) {
            it.chartTypeSelector.find('div.cell').removeClass('selected');
            it.chartTypeSelector.find('div.cell[data-hcname="' + chartType + '"]').addClass('selected');
            chart.config.datetimeSupported 
            	? it.chartTypeSelector.find('div.cell[data-hcname^="TimeSeries"]').removeClass('disabled')
            	: it.chartTypeSelector.find('div.cell[data-hcname^="TimeSeries"]').addClass('disabled');
        }
    }

    return new chartSelector();
});