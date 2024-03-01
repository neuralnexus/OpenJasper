/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import reportStatuses from '../enum/reportStatuses';
import _ from 'underscore';
export default {
    isCompleted: function () {
        return !_.isUndefined(this.get('status')) && (this.isFailed() || this.isReady() || this.isEmpty() || this.isCancelled());
    },
    isFailed: function () {
        return this.get('status') === reportStatuses.FAILED;
    },
    isCancelled: function () {
        return this.get('status') === reportStatuses.CANCELLED;
    },
    isReady: function () {
        return this.get('status') === reportStatuses.READY;
    },
    isEmpty: function () {
        return this.get('status') === reportStatuses.EMPTY;
    },
    isQueued: function () {
        return this.get('status') === reportStatuses.QUEUED;
    },
    isExecuting: function () {
        return this.get('status') === reportStatuses.EXECUTION;
    }
};