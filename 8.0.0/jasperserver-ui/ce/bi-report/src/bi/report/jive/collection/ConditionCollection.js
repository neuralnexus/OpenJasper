/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
import ConditionModel from '../model/ConditionModel';
export default Backbone.Collection.extend({
    model: ConditionModel,
    initialize: function (models, options) {
        options || (options = {});
        this.dataType = options.dataType;
    }
});