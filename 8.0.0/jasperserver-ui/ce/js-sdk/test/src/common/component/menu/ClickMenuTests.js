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
import ClickMenu from 'src/common/component/menu/ClickMenu';
import ClickComponent from 'src/common/component/base/ClickComponent';
import Menu from 'src/common/component/menu/Menu';

describe('ClickMenu component', function(){
    var menu, sandbox;

    beforeEach(function() {
        sandbox = sinon.createSandbox();
        menu = new ClickMenu([ { label: "Save Dashboard", action: "save" } ], $("<button></button>"));
    });

    afterEach(function() {
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();
        sandbox.restore();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof ClickMenu).toBe('function');
        expect(ClickMenu.prototype instanceof Backbone.View).toBeTruthy();
    });

    it('should be AttachableMenu instance', function(){
        expect(ClickMenu.prototype instanceof Menu).toBeTruthy();
    });

    it('should throw exception if no menu options set', function(){
        expect(function() { new ClickMenu(); }).toThrow(new Error("Menu should have options"));
        expect(function() { new ClickMenu({}); }).toThrow(new Error("Menu should have options"));
    });

    it("should detach click event from element when remove() is called", function() {
        var $btn = menu.$attachTo;

        menu.remove();

        var events = $._data($btn[0], "events");
        expect(events).toBeUndefined();
    });

    it("should show menu on element click", function() {
        var $btn = menu.$attachTo,
            showSpy = sandbox.spy(menu, "show");

        $btn.trigger("click");

        sandbox.assert.calledWith(showSpy);

        showSpy.restore();
    });

    it("should hide menu on document mousedown outside element or menu", function() {
        var hideSpy = sandbox.spy(menu, "hide");

        $(document.body).trigger("mousedown");

        sandbox.assert.calledWith(hideSpy);

        hideSpy.resetHistory();

        var e = $.Event( "mousedown" );
        $.target = menu.$el.children()[0];

        menu.$el.trigger(e);

        expect(hideSpy).not.toHaveBeenCalled();

        hideSpy.restore();
    });

    it("should throw exception and remove ClickComponent", function(){
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();

        var removeClickComponentSpy = sandbox.spy(ClickComponent.prototype, "remove");

        expect(function(){new ClickMenu(false, $("<button></button>"))}).toThrowError("Menu should have options");
        expect(removeClickComponentSpy).toHaveBeenCalled();

        removeClickComponentSpy.restore();
    });
});