/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import $ from 'jquery';
import BaseJiveComponentView from './BaseJiveComponentView';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import {loadScript} from '../../loader/scriptLoader';

const FUSION_CHARTS_SCRIPT_PATH = 'fusion/fusioncharts.js';

const loadFusionCharts = () => {
    const scriptPath = `${jrsConfigs.contextPath}/${FUSION_CHARTS_SCRIPT_PATH}`;
    return loadScript(scriptPath);
}

var FusionElement = function (config) {
    this.config = config;
    this.parent = null;
    this.loader = null;
    this.fusionInstance = null;
    this._init();
};
FusionElement.prototype = {
    // internal API
    _init: function () {
        const self = this;
        const instData = this.config.instanceData;

        if (!document.getElementById(instData.id)) {
            loadFusionCharts().then(() => {
                if (typeof window.printRequest === 'function') {
                    //FIXME: is this still necessary?
                    window.printRequest();
                }

                const FusionCharts = window.FusionCharts;

                const fcConfig = {
                    id: instData.id,
                    type: instData.type,
                    width: instData.width,
                    height: instData.height,
                    renderAt: instData.renderAt,
                    dataFormat: instData.dataFormat,
                    dataSource: instData.dataSource
                };
                //remove instance if it already exists
                //to avoid memory leaks
                FusionCharts.items[fcConfig.id] && FusionCharts.items[fcConfig.id].dispose();
                this.fusionInstance = new FusionCharts(fcConfig);
                this.fusionInstance.addEventListener('BeforeRender', function (event, eventArgs) {
                    if (eventArgs.renderer === 'javascript') {
                        event.sender.setChartAttribute('exportEnabled', '0');
                    }
                });
                this.fusionInstance.addEventListener('JR_Hyperlink_Interception', function (event, eventArgs) {
                    var handler;
                    self.config.linksOptions.events && (handler = self.config.linksOptions.events.click);
                    handler && handler.call(this, event, eventArgs);
                });
                this.fusionInstance.setTransparent(instData.transparent);
                this.fusionInstance.render();
            });
        }
    },
    remove: function () {
        this.fusionInstance && this.fusionInstance.dispose();
    }
};
export default BaseJiveComponentView.extend({
    render: function ($el) {
        var dfd = new $.Deferred(), linkOptions = this.model.collection ? this.model.collection.linkOptions : null, data = _.extend(this.model.toJSON(), { chart: _.clone(this.stateModel.get('chart')) });
        if (linkOptions) {
            data.linkOptions = linkOptions;
        }
        this.fusionElement = new FusionElement(data);
        dfd.resolve();
        return dfd;
    },
    remove: function () {
        this.fusionElement && this.fusionElement.remove();
        BaseJiveComponentView.prototype.remove.apply(this, arguments);
    }
});