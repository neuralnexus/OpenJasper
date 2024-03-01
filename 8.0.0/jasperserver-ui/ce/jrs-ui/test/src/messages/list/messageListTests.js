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

/*global spyOn*/

import jQuery from 'jquery';
import sinon from 'sinon';
import messageListModule from 'src/messages/list/messageList';
import {dynamicList} from 'src/components/list.base';
import list from '../test/templates/list.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

describe('messageListModule', function () {
    var sandbox;
    var options = {
        flowExecutionKey: "e1s1",
        systemConfirmMessages:
            {
                delete: "Selected messages are deleted",
                markAsRead: "Selected messages are marked as read",
                markAsUnread: "Selected messages are marked as unread"
            }
    };
    beforeEach(function () {
        sandbox = sinon.createSandbox();
        setTemplates(list);
        sandbox.stub(dynamicList, 'List').callsFake(function () {
            this.observe = sinon.stub();
        });
        sandbox.stub(messageListModule, 'doAction').callsFake(function () {
            this.observe = sinon.stub();
        });
        messageListModule.initialize(options);
    });

    afterEach(function () {
        sandbox.restore();
    });

    it('messageList initialize is defined and has been called', function () {
        expect(messageListModule.initialize).toBeDefined();
        spyOn(messageListModule, 'initialize');
        messageListModule.initialize(options);
        expect(messageListModule.initialize).toHaveBeenCalled();
        expect(messageListModule.initialize).toHaveBeenCalledWith(options);
    });

    it('doAction is called with "getMessages" parameter when messageListModule initialized', function () {
        messageListModule.initialize(options);
        expect(messageListModule.doAction).toHaveBeenCalled();
        expect(messageListModule.doAction).toHaveBeenCalledWith('getMessages', '');
    });

    it('toolbar initialize is defined and has been called', function () {
        expect(messageListModule.toolbar.initialize).toBeDefined();
        spyOn(messageListModule.toolbar, 'initialize');
        messageListModule.toolbar.initialize();
        expect(messageListModule.toolbar.initialize).toHaveBeenCalled();
    });

    it('toolbar _initEventHandlers is defined and called when toolbar initialized', function () {
        expect(messageListModule.toolbar._initEventHandlers).toBeDefined();
        spyOn(messageListModule.toolbar, '_initEventHandlers');
        messageListModule.toolbar.initialize();
        expect(messageListModule.toolbar._initEventHandlers).toHaveBeenCalled();
    });

    it('filter initialize is defined and has been called', function () {
        expect(messageListModule.filter.initialize).toBeDefined();
        spyOn(messageListModule.filter, 'initialize');
        messageListModule.filter.initialize();
        expect(messageListModule.filter.initialize).toHaveBeenCalled();
    });
    it('filter _initEventHandlers is defined and called when filter initialize', function () {
        expect(messageListModule.filter._initEventHandlers).toBeDefined();
        spyOn(messageListModule.filter, '_initEventHandlers');
        messageListModule.filter.initialize();
        expect(messageListModule.filter._initEventHandlers).toHaveBeenCalled();
    });

    it('doAction is called with parameter "changeEventsType" & "messageFilter=ALL" when "ALL" filter link is clicked', function () {
        jQuery('#sortMode_item2').trigger('click');
        expect(messageListModule.doAction).toHaveBeenCalled();
        expect(messageListModule.doAction).toHaveBeenCalledWith('changeEventsType', 'messageFilter=ALL');
    });

    it('doAction is called with parameter "changeEventsType" & "messageFilter=UNREAD" when "Unread" filter link is clicked', function () {
        jQuery('#sortMode_item3').trigger('click');
        expect(messageListModule.doAction).toHaveBeenCalled();
        expect(messageListModule.doAction).toHaveBeenCalledWith('changeEventsType', 'messageFilter=UNREAD');
    });
});