/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import $ from 'jquery';
import Dialog from 'js-sdk/src/common/component/dialog/Dialog';

export default {
    extension: {
        _adjustPositionWithinViewport: function(position) {
            return {
                top: position.top + this._getWindowScrollTop(),
                left: position.left
            };
        },

        _position: function () {
            var position = Dialog.prototype._position.apply(this, arguments);

            return this._adjustPositionWithinViewport(position);
        },

        _getWindowScrollTop: function(){
            return $(window).scrollTop();
        }
    }
}