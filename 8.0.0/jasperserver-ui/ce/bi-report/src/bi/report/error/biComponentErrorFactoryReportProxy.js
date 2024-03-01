/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import biComponentErrorFactory from 'js-sdk/src/common/bi/error/biComponentErrorFactory';
import ReportStatusError from './ReportStatusError';
import ReportRenderError from './ReportRenderError';

biComponentErrorFactory.reportStatus = function (errorObj) {
    return new ReportStatusError(errorObj);
};
biComponentErrorFactory.reportRender = function (errorObj) {
    return new ReportRenderError(errorObj);
};
export default biComponentErrorFactory;