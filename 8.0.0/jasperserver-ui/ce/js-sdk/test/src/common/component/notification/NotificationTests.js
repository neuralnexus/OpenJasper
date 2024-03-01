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

import sinon from 'sinon';
import $ from 'jquery';
import Backbone from 'backbone';
import Notification from 'src/common/component/notification/Notification';
describe('Notification component', function () {
    var notification;
    beforeEach(function () {
        notification = new Notification();
        $('body').append(notification.el);
    });
    afterEach(function () {
        notification && notification.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof Notification).toBe('function');
        expect(Notification.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should have public functions', function () {
        expect(notification.render).toBeDefined();
        expect(notification.show).toBeDefined();
        expect(notification.hide).toBeDefined();
        expect(notification.remove).toBeDefined();
    });
    it('should have a defined element', function () {
        expect(notification.$el).toBeDefined();
    });
    it('it should hide its content on render', function () {
        var hideStub = sinon.spy(notification.$el, 'hide');
        notification.render();
        expect(hideStub).toHaveBeenCalled();
        expect($('body').find('.notificationAlert').length).toEqual(1);
        expect(notification.$el.is(':visible')).toEqual(false);
        hideStub.restore();
    });
    it('it should show its content on show', function () {
        notification.show();
        expect(notification.$el.is(':visible')).toEqual(true);
    });
    it('it should hide content on hide', function () {
        notification.hide();
        expect(notification.$el.is(':visible')).toEqual(false);
    });
    it('should show content and hide it after default timeout (2s)', function () {
        var clock = sinon.useFakeTimers();
        var hideStub = sinon.stub(notification, 'hide');
        notification.show();
        clock.tick(2000);
        expect(hideStub).toHaveBeenCalled();
        hideStub.restore();
        clock.restore();
    });
    it('should show content and hide it after custom timeout (1s)', function () {
        var clock = sinon.useFakeTimers();
        var hideStub = sinon.stub(notification, 'hide');
        notification.show({ delay: 1000 });
        clock.tick(1000);
        expect(hideStub).toHaveBeenCalled();
        clock.restore();
        hideStub.restore();
    });
    it('should show message', function () {
        notification.show({ message: 'test' });
        expect(notification.$el.is(':visible')).toEqual(true);
        expect(notification.$messageContainer.text()).toEqual('test');
    });
    it('should be hided on \'close\' click', function () {
        var hideStub = sinon.stub(notification.$el, 'slideUp');
        notification.render();
        notification.show({ message: 'test' });
        notification.$el.find('.close a').trigger('click');
        expect(hideStub).toHaveBeenCalled();
        hideStub.restore();
    });
    it('should be removed', function () {
        var removeSpy = sinon.spy(Backbone.View.prototype, 'remove');
        notification.remove();
        expect(removeSpy).toHaveBeenCalled();
        removeSpy.restore();
    });
});