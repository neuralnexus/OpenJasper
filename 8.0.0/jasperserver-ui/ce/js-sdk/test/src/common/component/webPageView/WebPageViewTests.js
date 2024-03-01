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
import WebPageView from 'src/common/component/webPageView/WebPageView';
import Backbone from 'backbone';
import $ from 'jquery';
import _ from 'underscore';
describe('WebPageView embeddable component', function () {
    var view;
    afterEach(function () {
        view && view.remove && view.remove();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof WebPageView).toBe('function');
        expect(WebPageView.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should have static constants and methods', function () {
        expect(WebPageView.TIMEOUT).toBeDefined();
        expect(_.isNumber(WebPageView.TIMEOUT)).toBe(true);
        expect(WebPageView.SCROLLING).toBeDefined();
        expect(_.isBoolean(WebPageView.SCROLLING)).toBe(true);
        expect(WebPageView.open).toBeDefined();
        expect(_.isFunction(WebPageView.open)).toBe(true);
    });
    it('should create a new WebPageView instance with static \'open\' method and URL as an argument', function () {
        view = WebPageView.open('http://test.com');
        expect(view instanceof WebPageView).toBe(true);
        expect(view.url).toBe('http://test.com');
    });
    it('should create a new WebPageView instance with static \'open\' method and settings object as an argument', function () {
        view = WebPageView.open({
            url: 'http://test.com',
            timeout: 100,
            scrolling: false
        });
        expect(view instanceof WebPageView).toBe(true);
        expect(view.url).toBe('http://test.com');
        expect(view.timeout).toBe(100);
        expect(view.scrolling).toBe(false);
    });
    it('should create a new WebPageView instance and call callback if it is passed as second argument when static \'open\' method is called', function () {
        var callbackSpy;
        callbackSpy = jasmine.createSpy('callbackSpy');
        view = WebPageView.open('http://test.com', callbackSpy);
        expect(callbackSpy).toHaveBeenCalledWith(undefined, view);
    });
    it('should throw error if callback is not specified and error happens when calling static \'open\' method', function () {
        expect(function () {
            WebPageView.open({ renderTo: '#sdfsd' });
        }).toThrow(new Error('WebPageView cannot be rendered to specified container'));
    });
    it('should call callback with error if creation a new WebPageView instance failed and callback is passed as second argument when static \'open\' method is called', function () {
        var callbackSpy;
        callbackSpy = jasmine.createSpy('callbackSpy');
        view = WebPageView.open({ renderTo: '#sdfsd' }, callbackSpy);
        expect(callbackSpy.calls.mostRecent().args[0].message).toBe('WebPageView cannot be rendered to specified container');
        expect(callbackSpy.calls.mostRecent().args[1]).toBeUndefined();
    });
    it('should be element with class \'webPageView\'', function () {
        view = new WebPageView();
        expect(view.$el.hasClass('webPageView'));
    });
    it('should throw exception if renderTo option is specified but element does not exist', function () {
        expect(function () {
            new WebPageView({ renderTo: '#someElement' });
        }).toThrow(new Error('WebPageView cannot be rendered to specified container'));
    });
    it('should use default options for \'scrolling\' and \'timeout\' if they are not specified in init options', function () {
        view = new WebPageView();
        expect(view.scrolling).toBe(WebPageView.SCROLLING);
        expect(view.timeout).toBe(WebPageView.TIMEOUT);
    });
    it('should save passed options', function () {
        var container = $('<div></div>');
        view = new WebPageView({
            renderTo: container,
            url: 'http://test.com',
            scrolling: false,
            timeout: 5000
        });
        expect(view.renderTo).toBe(container);
        expect(view.url).toBe('http://test.com');
        expect(view.scrolling).toBe(false);
        expect(view.timeout).toBe(5000);
    });
    it('should append iframe with special class and attributes to inner HTML, make element invisible and append it to DOM', function () {
        view = new WebPageView({ scrolling: false });
        var $iframe = view.$('iframe');
        expect($iframe.length).toBe(1);
        expect($iframe.hasClass('externalUrlIframe')).toBe(true);
        expect($iframe.attr('scrolling')).toBe('no');
        expect(view.$el.hasClass('invisible')).toBe(true);
        expect(view.$el.parent()[0].tagName.toLowerCase()).toBe('body');
    });
    it('should not set iframe \'src\' attribute when \'url\' is not defined on init', function () {
        view = new WebPageView();
        expect(view.$('iframe').attr('src')).toBeFalsy();
        expect(view._iframeSrcSet).toBeFalsy();
    });
    it('should set iframe \'src\' attribute when \'url\' is defined on init', function () {
        view = new WebPageView({ url: 'http://test.com' });
        expect(view.$('iframe').attr('src')).toBe('http://test.com');
        expect(view._iframeSrcSet).toBe(true);
        expect(view._loadingTimeoutId).toBeDefined();
    });
    it('should call \'render\' method automatically when \'renderTo\' option is specified', function () {
        var renderStub = sinon.stub(WebPageView.prototype, 'render');
        var container = $('<div></div>');
        view = new WebPageView({
            renderTo: container,
            url: 'http://test.com'
        });
        expect(renderStub).toHaveBeenCalledWith(container);
        renderStub.restore();
    });
    it('should remove \'loading\' class after timeout', function () {
        var clock = sinon.useFakeTimers();
        view = new WebPageView({
            url: 'http://test.com',
            timeout: 100
        });
        clock.tick(120);
        expect(view.$el.hasClass('loading')).toBeFalsy();
        clock.restore();
    });
    it('should through error when \'render\' is called if \'url\' is not specified', function () {
        view = new WebPageView();
        expect(function () {
            view.render();
        }).toThrow(new Error('WebPageView URL is not specified'));
    });
    it('should through error when \'render\' is called with not existing container', function () {
        view = new WebPageView({ url: 'http://test.com' });
        expect(function () {
            view.render();
        }).toThrow(new Error('WebPageView cannot be rendered to specified container'));
        expect(function () {
            view.render('#someContainer');
        }).toThrow(new Error('WebPageView cannot be rendered to specified container'));
        expect(function () {
            view.render($('#someContainer'));
        }).toThrow(new Error('WebPageView cannot be rendered to specified container'));
    });
    it('should set iframe \'src\' attribute if it was not set before when \'render\' is called', function () {
        view = new WebPageView();
        view.url = 'http://test.com';
        view.render($('<div></div>'));
        expect(view.$('iframe').attr('src')).toBe('http://test.com');
        expect(view._iframeSrcSet).toBe(true);
        expect(view._loadingTimeoutId).toBeDefined();
    });
    it('should attach element to specified container, make it visible and trigger event when \'render\' is called', function () {
        view = new WebPageView({ url: 'http://test.com' });
        var container = $('<div></div>'), triggerSpy = sinon.spy(view, 'trigger');
        view.render(container);
        expect(view.$el.hasClass('invisible')).toBe(false);
        expect(container.find('.webPageView').length).toBe(1);
        expect(view.$el.parent()[0].tagName.toLowerCase()).toBe('div');
        expect(view._rendered).toBe(true);
        expect(triggerSpy).toHaveBeenCalledWith('render', view);
        triggerSpy.restore();
    });
    it('should throw error on refresh if view was not rendered yet', function () {
        view = new WebPageView({ url: 'http://test.com' });
        expect(function () {
            view.refresh();
        }).toThrow(new Error('WebPageView must be rendered to a specific container first'));
    });
    it('should throw error on refresh if \'url\' is not defined', function () {
        view = new WebPageView({ url: 'http://test.com' });
        view.render($('<div></div>'));
        view.url = '';
        expect(function () {
            view.refresh();
        }).toThrow(new Error('WebPageView URL is not specified'));
    });
    it('should update iframe \'src\' attribute, show animation, remove <div> with error on refresh, and trigger event on refresh', function () {
        var clock = sinon.useFakeTimers();
        view = new WebPageView({
            url: 'http://test.com',
            timeout: 100
        });
        view.render($('<div></div>'));
        clock.tick(120);
        view.url = 'http://example.com';
        var triggerSpy = sinon.spy(view, 'trigger');
        view.refresh();
        expect(view.$el.hasClass('loading')).toBeTruthy();
        expect(view.$('iframe').attr('src')).toBe('http://example.com');
        expect(triggerSpy).toHaveBeenCalledWith('refresh', view, view.url);
        triggerSpy.restore();
        clock.restore();
    });
    it('should trigger event, clear timeout and call base \'remove\' method on view remove', function () {
        view = new WebPageView({ url: 'http://test.com' });
        var removeSpy = sinon.spy(Backbone.View.prototype, 'remove'), clearTimeoutSpy = sinon.spy(window, 'clearTimeout'), triggerSpy = sinon.spy(view, 'trigger');
        view.remove();
        expect(triggerSpy).toHaveBeenCalledWith('remove', view);
        expect(clearTimeoutSpy).toHaveBeenCalledWith(view._loadingTimeoutId);
        expect(removeSpy).toHaveBeenCalled();
        removeSpy.restore();
        clearTimeoutSpy.restore();
        triggerSpy.restore();
    });
    it('should set \'url\' property and automatically refresh view with \'setUrl\' method', function () {
        view = new WebPageView({ url: 'http://test.com' });
        view.render($('<div></div>'));
        var refreshSpy = sinon.spy(view, 'refresh'), triggerSpy = sinon.spy(view, 'trigger');
        view.setUrl('http://example.com');
        expect(view.url).toBe('http://example.com');
        expect(refreshSpy).toHaveBeenCalled();
        expect(triggerSpy).toHaveBeenCalledWith('change:url', view, 'http://example.com');
        refreshSpy.restore();
        triggerSpy.restore();
    });
    it('should not automatically refresh view when \'setUrl\' method is called with noRefresh=true', function () {
        view = new WebPageView({ url: 'http://test.com' });
        view.render($('<div></div>'));
        var refreshSpy = sinon.spy(view, 'refresh'), triggerSpy = sinon.spy(view, 'trigger');
        view.setUrl('http://example.com', true);
        expect(view.url).toBe('http://example.com');
        expect(refreshSpy).not.toHaveBeenCalled();
        expect(triggerSpy).toHaveBeenCalledWith('change:url', view, 'http://example.com');
        refreshSpy.restore();
        triggerSpy.restore();
    });
    it('should set \'timeout\' property with \'setTimeout\' method', function () {
        view = new WebPageView({ url: 'http://test.com' });
        view.render($('<div></div>'));
        var triggerSpy = sinon.spy(view, 'trigger');
        view.setTimeout(100);
        expect(view.timeout).toBe(100);
        expect(triggerSpy).toHaveBeenCalledWith('change:timeout', view, 100);
        triggerSpy.restore();
    });
    it('should set \'scrolling\' property and update iframe styles with \'setScrolling\' method', function () {
        view = new WebPageView({ url: 'http://test.com' });
        view.render($('<div></div>'));
        var triggerSpy = sinon.spy(view, 'trigger');
        view.setScrolling(false);
        expect(view.scrolling).toBe(false);
        expect(view.$('iframe').attr('scrolling')).toBe('no');
        expect(triggerSpy).toHaveBeenCalledWith('change:scrolling', view, false);
        triggerSpy.restore();
    });
});