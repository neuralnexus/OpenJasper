/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @version: $Id$
 */
import toolbarButtonModule from '../../components/components.toolbarButtons.events';
import layoutModule from '../../core/core.layout';
import {matchAny} from "../../util/utils.common";
import xssUtil from 'js-sdk/src/common/util/xssUtil';
import jQuery from 'jquery';

var messageDetailModule = {
    _flowExecutionKey: null,
    _message: null,
    toolbar: {
        _buttons: null,
        _id: 'toolbar',
        initialize: function () {
            toolbarButtonModule.initialize({});
            this._buttons = jQuery(document.body).find(layoutModule.TOOLBAR_CAPSULE_PATTERN);
            this._initEventHandlers();
        },
        refresh: function () {
            jQuery(this._buttons).each(function (button) {
                toolbarButtonModule.setButtonState(button, true);
            }.bind(this));
        },
        _initEventHandlers: function () {
            jQuery('#' + this._id).on('click', function (e) {
                var button = matchAny(e.target, [layoutModule.BUTTON_PATTERN], true);
                document.location = 'flow.html?_flowExecutionKey=' + messageDetailModule._flowExecutionKey + '&_eventId=' + button.identify();
            }.bind(this));
        }
    },
    initialize: function (options) {
        this._flowExecutionKey = options.flowExecutionKey;
        this._message = options.message;
        this._process();
        this.toolbar.initialize();
    },
    _process: function () {
        jQuery('#subject').html(xssUtil.hardEscape(this._message.subject));
        jQuery('#date').html(xssUtil.hardEscape(this._message.date));
        jQuery('#component').html(xssUtil.hardEscape(this._message.component));
        jQuery('#message').html(xssUtil.hardEscape(this._message.message));
    }
};

export default messageDetailModule;