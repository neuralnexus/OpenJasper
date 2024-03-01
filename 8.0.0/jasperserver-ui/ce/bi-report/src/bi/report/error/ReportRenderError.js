/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BiComponentError from 'js-sdk/src/common/bi/error/BiComponentError';
import _ from 'underscore';
import errorCodes from 'js-sdk/src/common/bi/error/enum/biComponentErrorCodes';
import messages from 'js-sdk/src/common/bi/error/enum/biComponentErrorMessages';

export default BiComponentError.extend({

    constructor: function (errorObj) {
        let
            code = errorCodes['REPORT_RENDER_ERROR'],
            msg = messages[code];

        _.extend(this, errorObj);

        if (errorObj.type === 'wrongContainerSize') {
            code = errorCodes['WRONG_CONTAINER_SIZE_ERROR'];
            msg = errorObj.data.error + ' ' + errorObj.data.message;
        }

        if (errorObj.type === 'highchartsInternalError') {
            code = errorCodes['REPORT_RENDER_HIGHCHARTS_ERROR'];
            msg = errorObj.data.error + ' ' + errorObj.data.message;
        }

        BiComponentError.prototype.constructor.call(this, code, msg);
    }
});
