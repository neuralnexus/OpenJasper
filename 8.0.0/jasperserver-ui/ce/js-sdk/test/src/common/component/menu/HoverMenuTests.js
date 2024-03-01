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
import Backbone from 'backbone';
import $ from 'jquery';
import HoverMenu from 'src/common/component/menu/HoverMenu';
import AttachableMenu from 'src/common/component/menu/AttachableMenu';
describe('HoverMenu component', function () {
    var menu;
    let sandbox;
    beforeEach(function () {
        sandbox = sinon.createSandbox({
            useFakeTimers: true
        });
        menu = new HoverMenu([{
            label: 'Save Dashboard',
            action: 'save'
        }], $('<button></button>'));
    });
    afterEach(function () {
        menu && menu.remove();
        $('.menu.vertical.dropDown.fitable').remove();
        sandbox.restore();
    });
    it('should be Backbone.View instance', function () {
        expect(typeof HoverMenu).toBe('function');
        expect(HoverMenu.prototype instanceof Backbone.View).toBeTruthy();
    });
    it('should be AttachableMenu instance', function () {
        expect(HoverMenu.prototype instanceof AttachableMenu).toBeTruthy();
    });
    it('should attach mouseover/mouseout event handlers to element', function () {
        var $btn = menu.$attachTo;
        var events = $._data($btn[0], 'events');
        expect(events).toBeDefined();
        expect(events.mouseover).toBeDefined();
        expect(events.mouseout).toBeDefined();
    });
    it('should detach mouseover/mouseout events from element when remove() is called', function () {
        var $btn = menu.$attachTo;
        menu.remove();
        var events = $._data($btn[0], 'events');
        expect(events).toBeUndefined();
    });
    it('should show menu on element mouseover', function () {
        var $btn = menu.$attachTo, showSpy = sinon.spy(menu, 'show');
        $btn.trigger('mouseover');
        sinon.assert.calledWith(showSpy);
        showSpy.restore();
    });
    it('should hide menu on element mouseout', function () {
        var $btn = menu.$attachTo, hideSpy = sinon.spy(AttachableMenu.prototype, 'hide');
        $btn.trigger('mouseout');
        sandbox.clock.tick(300);
        sinon.assert.calledWith(hideSpy);
        hideSpy.restore();
    });
    it('should hide menu on mouseout', function () {
        var hideSpy = sinon.spy(AttachableMenu.prototype, 'hide');
        menu.trigger('mouseover');
        menu.trigger('mouseout');
        sandbox.clock.tick(300);
        sinon.assert.calledWith(hideSpy);
        hideSpy.restore();
    });
    it('should hide menu on container:mouseout', function () {
        var hideSpy = sinon.spy(AttachableMenu.prototype, 'hide');
        menu.trigger('container:mouseover');
        menu.trigger('container:mouseout');
        sandbox.clock.tick(300);
        sinon.assert.calledWith(hideSpy);
        hideSpy.restore();
    });
    it('should be possible to change element to which menu is attached', function () {
        var $el = $('<div></div>'), showSpy = sinon.spy(menu, 'show');
        menu.setAttachTo($el);
        $el.trigger('mouseover');
        sinon.assert.calledWith(showSpy);
        showSpy.restore();
    });
});