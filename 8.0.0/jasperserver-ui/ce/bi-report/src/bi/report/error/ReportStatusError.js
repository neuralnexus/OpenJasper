/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import BiComponentError from 'js-sdk/src/common/bi/error/BiComponentError';
import _ from 'underscore';
import reportStatuses from '../enum/reportStatuses';
import errorCodes from 'js-sdk/src/common/bi/error/enum/biComponentErrorCodes';
import messages from 'js-sdk/src/common/bi/error/enum/biComponentErrorMessages';

export default BiComponentError.extend({
    constructor: function (errorObj) {
        var code, msg, parameters;
        _.extend(this, errorObj);
        if (errorObj.source === 'execution') {
            code = errorCodes[errorObj.status === reportStatuses.CANCELLED ? 'REPORT_EXECUTION_CANCELLED' : 'REPORT_EXECUTION_FAILED'];
        } else {
            code = errorCodes[errorObj.status === reportStatuses.CANCELLED ? 'REPORT_EXPORT_CANCELLED' : 'REPORT_EXPORT_FAILED'];
        }
        msg = errorObj.errorDescriptor && errorObj.errorDescriptor.message || messages[code];
        if (errorObj.errorDescriptor && errorObj.errorDescriptor.parameters) {
            parameters = errorObj.errorDescriptor.parameters;
        }
        BiComponentError.prototype.constructor.call(this, code, msg, parameters);
    }
});