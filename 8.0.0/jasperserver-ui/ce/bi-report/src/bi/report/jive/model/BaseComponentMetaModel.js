/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
export default Backbone.Model.extend({
    defaults: function () {
        return {
            id: null,
            type: null,
            module: null
        };
    }
});