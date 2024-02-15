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
 * @version $Id: jive.fusion.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

define(["require", "jquery", "jasperreports-url-manager", "fusioncharts", "jrs.configs", "settings!jasperreports"], function(require, $, UrlManager, fusioncharts, jrsConfigs, jasperreports_properties){
    var FusionElement = function(config) {
        this.config = config;
        this.parent = null;
        this.loader = null;

        this.fusionInstance = null;
        this.events = {
            HYPERLINK_INTERACTION: "hyperlinkInteraction"
        };

        this._init();
    };

    FusionElement.prototype = {

        // internal API
        _init: function() {
            var it = this,
                instData = it.config.instanceData,
                fcConfig;


            FusionCharts.options.scriptBaseUri =
                jrsConfigs.contextPath + "/" +
                jasperreports_properties["com.jaspersoft.jasperreports.fusion.maps.context.swf.url"];

            FusionCharts.options.html5ChartsSrc = jrsConfigs.contextPath + "/" +
                jasperreports_properties["com.jaspersoft.jasperreports.fusion.charts.context.swf.url"] +
                "/FusionCharts.HC.Charts.js";

            FusionCharts.options.html5WidgetsSrc = jrsConfigs.contextPath + "/" +
                jasperreports_properties["com.jaspersoft.jasperreports.fusion.widgets.context.swf.url"] +
                "/FusionCharts.HC.Widgets.js";

            function dbg() {
                var i, args = arguments;
                for(i=0; i<args.length; i++) {
                    console.log(args[i]);
                }
            }

            //			FusionCharts.debugMode.enabled( dbg, 'verbose');

            if(!document.getElementById(instData.id)) {
                if (typeof window.printRequest === 'function') { //FIXME: is this still necessary?
                    window.printRequest();
                }

                fcConfig = {
                    id: instData.id,
                    swfUrl: instData.swfUrl,
                    width: instData.width,
                    height: instData.height,
                    debugMode: instData.debugMode,
                    registerWithJS: instData.registerWithJS,
                    renderAt: instData.renderAt,
                    allowScriptAccess: instData.allowScriptAccess,
                    dataFormat: instData.dataFormat,
                    dataSource: instData.dataSource
                };

                if (instData.rendererType === 'html5') {
                    fcConfig.renderer = 'javascript';
                }

                it.fusionInstance = new FusionCharts(fcConfig);

                it.fusionInstance.addEventListener('BeforeRender', function(event, eventArgs) {
                    if (eventArgs.renderer === 'javascript') {
                        event.sender.setChartAttribute('exportEnabled', '0');
                    }
                });

                it.fusionInstance.addEventListener('JR_Hyperlink_Interception', function(event, eventArgs) {
                    var handler;
                    it.config.linksOptions.events && (handler = it.config.linksOptions.events.click);
                    handler && handler.call(this, event, eventArgs);

                    it._hyperlinkItemClicked(eventArgs);
                });

                it.fusionInstance.setTransparent(instData.transparent);
                it.fusionInstance.render();
            }
        },
        _hyperlinkItemClicked: function(hyperlinkData) {
            this._notify({
                name: this.events.HYPERLINK_INTERACTION,
                type: "hyperlinkClicked",
                data: {hyperlink: hyperlinkData}
            });
        },
        _notify: function(evt) {
            // bubble the event
            this.parent && this.parent._notify(evt);
        }
    };

    return FusionElement;
});
