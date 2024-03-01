/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import _ from 'underscore';
import BaseComponentModel from './BaseComponentModel';
import jiveTypes from '../enum/jiveTypes';
export default BaseComponentModel.extend({
    defaults: function () {
        return {
            bookmarks: [],
            id: undefined,
            type: jiveTypes.BOOKMARKS
        };
    },
    constructor: function (attrs, options) {
        options || (options = {});
        options.parse || (options = _.extend({}, options, { parse: true }));
        BaseComponentModel.call(this, attrs, options);
    },
    parse: function (data) {
        data.bookmarks = this._processBookmarks(data.bookmarks);
        return data;
    },
    _processBookmarks: function (bookmarks) {
        if (bookmarks) {
            var self = this;
            return _.map(bookmarks, function (bookmark) {
                return {
                    anchor: bookmark.label,
                    page: bookmark.pageIndex + 1,
                    elementAddress: bookmark.elementAddress,
                    bookmarks: self._processBookmarks(bookmark.bookmarks)
                };
            });
        }
        return null;
    }
});