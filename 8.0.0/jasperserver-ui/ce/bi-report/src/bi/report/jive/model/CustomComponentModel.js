/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */


/**
 * @author: Igor Nesterenko
 * @version: $Id$
 */

import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
import _ from 'underscore';

var localResourceTemplate = _.template("{{=uri}}?noext");

export default BaseComponentModel.extend({
    defaults: function () {
        return {
            id: null,
            type: jiveTypes.CUSTOM_VISUALIZATION_COMPONENT,
            script: null,
            css: null
        };
    },
    parse: function (attributes, options) {
        attributes = _.defaults(attributes, {
            server: options.parent.contextPath,
            report: options.parent.get('reportURI')
        });
        attributes.script = {
            name: attributes.renderer,
            href : localResourceTemplate({ uri: attributes.instanceData.script_uri })
        };

        if(attributes.instanceData.css_uri){
            attributes.css = {
                name : attributes.id + "_css",
                href : localResourceTemplate({ uri: attributes.instanceData.css_uri })
            };
        }
        return attributes;
    },
    constructor: function (attributes, options) {
        attributes || (attributes = {});
        if (!attributes.instanceData.script_uri) {
            throw new Error('Can\'t initialize without script name');
        }
        BaseComponentModel.prototype.constructor.apply(this, arguments);
    }
});