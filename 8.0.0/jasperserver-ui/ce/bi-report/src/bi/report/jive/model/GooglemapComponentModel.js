/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
export default BaseComponentModel.extend({
    defaults: {
        id: null,
        instanceData: null,
        module: 'jasperreports-map',
        type: jiveTypes.GOOGLEMAP
    }
});