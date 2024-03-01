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
/* global alert*/

import jQuery from 'jquery';
import toolbarButtonModule from '../../components/components.toolbarButtons.events';
import layoutModule from '../../core/core.layout';
import {matchAny} from "../../util/utils.common";
import {dynamicList} from '../../components/list.base';
import {ajaxTargettedUpdate, AjaxRequester} from "../../core/core.ajax";
import {baseErrorHandler} from "../../core/core.ajax.utils";

const MESSAGE_FILTER_ID_TO_ACTION_MAP = {
    'sortMode_item2': 'ALL',
    'sortMode_item3': 'UNREAD'
};

var messageListModule = {
    _list: null,
    _messageListId: 'messageList',
    _flowExecutionKey: null,
    _systemConfirmMessages: [],
    toolbar: {
        _buttons: null,
        _id: 'toolbar',
        initialize: function () {
            toolbarButtonModule.initialize({});
            this._buttons = document.body.select(layoutModule.TOOLBAR_CAPSULE_PATTERN);
            this._initEventHandlers();
        },
        refresh: function () {
            this._buttons.each(function (button) {
                toolbarButtonModule.setButtonState(button, messageListModule.hasSelectedMessages());
            }.bind(this));
        },
        _initEventHandlers: function () {
            jQuery('#' + this._id).on('click', function (e) {
                var button = matchAny(e.target, [layoutModule.BUTTON_PATTERN], true);
                var actionName = button.identify();
                messageListModule.doAction(actionName, Object.toQueryString({ 'selectedIds': messageListModule.getSelectedIds() }));
            }.bind(this));
        }
    },
    filter: {
        _id: 'sortMode',
        initialize: function () {
            this._initEventHandlers();
        },
        _initEventHandlers: function () {
            jQuery('#' + this._id).on('click', function (e) {
                let link = matchAny(e.target, [layoutModule.TABSET_TAB_PATTERN], true);
                const linkID = link.identify();
                const action = MESSAGE_FILTER_ID_TO_ACTION_MAP[linkID];
                if (action) {
                    messageListModule.doAction('changeEventsType', Object.toQueryString({ 'messageFilter': action }));
                }
                e.stopPropagation();
            }.bind(this));
        }
    },
    initialize: function (options) {
        this._flowExecutionKey = options.flowExecutionKey;
        this._systemConfirmMessages = options.systemConfirmMessages;
        this._list = new dynamicList.List(this._messageListId, {
            listTemplateDomId: 'list_fourColumn_type_message',
            itemTemplateDomId: 'list_fourColumn_type_message:unread',
            multiSelect: true
        });
        var baseSelectItem = this._list.selectItem;
        this._list.selectItem = function (item) {
            if (!item.getValue().isHeader) {
                baseSelectItem.apply(this, arguments);
            }
        };
        this._initListEvents();
        this.toolbar.initialize();
        this.filter.initialize();
        this.doAction('getMessages', '');
    },
    _getMessageItems: function (messages) {
        var processItemTemplate = function (element) {
            var subject = jQuery(element).find('.subject a')[0];
            jQuery(subject).html(this.getValue().subject);
            jQuery(subject).attr('href', 'flow.html?_flowExecutionKey=' + messageListModule._flowExecutionKey + '&_eventId=viewMessage&id=' + this.getValue().id);
            var date = jQuery(element).find('.date')[0];
            jQuery(date).html(this.getValue().date);
            jQuery(date).attr('title', this.getValue().timestamp);
            var type = jQuery(element).find('.type')[0];
            jQuery(type).html(this.getValue().type);
            var component = jQuery(element).find('.component')[0];
            jQuery(component).html(this.getValue().component);
            return element;
        };    // Adding faked message element to the beginning for header.
        // Adding faked message element to the beginning for header.
        messages.unshift({ isHeader: true });
        return messages.collect(function (message) {
            var templateDomId;
            if (!message.isHeader) {
                if (message.isRead) {
                    templateDomId = 'list_fourColumn_type_message:read';
                } else {
                    templateDomId = 'list_fourColumn_type_message:unread';
                }
                var messageItem = new dynamicList.ListItem({
                    cssClassName: layoutModule.LEAF_CLASS,
                    value: message,
                    templateDomId: templateDomId
                });
                messageItem.processTemplate = processItemTemplate;
                return messageItem;
            }
        }.bind(this));
    },
    _initListEvents: function () {
        this._list.observe('item:selected', function (event) {
            this.refreshToolbar();
        }.bindAsEventListener(this));
        this._list.observe('item:unselected', function (event) {
            this.refreshToolbar();
        }.bindAsEventListener(this));
    },
    refreshToolbar: function () {
        messageListModule.toolbar.refresh();
    },
    hasSelectedMessages: function () {
        return this._list.getSelectedItems().length > 0;
    },
    getSelectedIds: function () {
        return this._list.getSelectedItems().collect(function (item) {
            return item.getValue().id;
        });
    },
    doAction: function (actionName, data) {
        var actionURL = 'flow.html?_flowExecutionKey=' + messageListModule._flowExecutionKey + '&_eventId=' + actionName;
        this._list.setItems([]);
        this.refreshToolbar();
        ajaxTargettedUpdate(actionURL, {
            postData: data,
            callback: function (response) {
                if (response.status == 'OK') {
                    this._list.setItems(this._getMessageItems(response.data));
                    this._list._element.addClassName('collapsible')
                    this._list.show();
                } else {
                    alert(response.data.message);
                }
            }.bind(this),
            errorHandler: this._serverErrorHandler,
            mode: AjaxRequester.prototype.EVAL_JSON
        });
    },
    _serverErrorHandler: function (ajaxAgent) {
        if (ajaxAgent.getResponseHeader('LoginRequested')) {
            document.location = 'flow.html?_flowId=logEventFlow';
        }
        var resolved = baseErrorHandler(ajaxAgent);
        if (!resolved && ajaxAgent.status == 500) {
            document.location = 'flow.html?_flowId=logEventFlow&_eventId=error';
            document.location = 'flow.html?_flowId=logEventFlow&_eventId=error';
        }
    }
};

export default messageListModule;