/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
export default Backbone.Model.extend({
    constructor: function (attributes, options) {
        options || (options = {});
        this.parent = options.parent;
        Backbone.Model.prototype.constructor.call(this, attributes, options);
    },
    // internal functions
    _notify: function (evt) {
        // bubble the event
        this.parent._notify(evt);
    },
    handleServerError: function (result) {
        var uiModuleType = this.get('uiModuleType');
        uiModuleType && uiModuleType.handleServerError(result);
    },
    handleClientError: function (result) {
        var uiModuleType = this.get('uiModuleType');
        uiModuleType && uiModuleType.handleClientError(result);
    }
});