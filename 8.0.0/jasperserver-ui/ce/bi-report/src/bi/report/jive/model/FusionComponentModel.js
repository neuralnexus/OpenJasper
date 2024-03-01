/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import $ from 'jquery';
import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';

import logger from "js-sdk/src/common/logging/logger";

let localLogger = logger.register("FusionComponentModel");

export default BaseComponentModel.extend({
    defaults: {
        id: null,
        instanceData: null,
        module: 'jive.fusion',
        type: jiveTypes.FUSION_WIDGET,
        linksOptions: {}
    },
    initialize: function (attr, options) {
        this.on('change:linksOptions', processLinkOptions);
        options.linkOptions && this.set('linksOptions', options.linkOptions);
    }
});
function processLinkOptions(model, linkOptions) {
    if (linkOptions.events && !model.collection.fusionChartsLinkOptionsProcessed) {
        model.collection.fusionChartsLinkOptionsProcessed = true;
        linkOptions.events.mouseout && localLogger.info('Fusion charts does not support mouseout events for hyperlinks');
        linkOptions.events.mouseover && localLogger.info('Fusion charts does not support mouseover events for hyperlinks');
        linkOptions.events.click && (linkOptions.events.click = _.wrap(linkOptions.events.click, function (func, ev, link) {
            // handle case when we receive event from fusion charts - it's not a jQuery.Event instance
            if (link instanceof $.Event) {
                func.call(this, ev, link);
            } else {
                func.call(this, link.id, ev);
            }
        }));
    }
}