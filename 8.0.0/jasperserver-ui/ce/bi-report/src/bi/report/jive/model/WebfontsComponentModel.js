/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
import {loadCss} from 'js-sdk/src/common/util/loader/cssLoader'

export default BaseComponentModel.extend({
    defaults: function () {
        return {
            id: null,
            server: '',
            type: jiveTypes.WEBFONTS,
            webfonts: []
        };
    },
    initialize: function (attrs) {
        if (attrs && attrs.webfonts) {
            this._handleWebfonts(attrs.webfonts);
        }
        BaseComponentModel.prototype.initialize.apply(this, arguments);
    },
    _handleWebfonts: function (webfonts) {
        const serverUri = this.get('server');
        webfonts.forEach(function (webfont) {
            loadCss(serverUri + webfont.path);
        });
    }
});