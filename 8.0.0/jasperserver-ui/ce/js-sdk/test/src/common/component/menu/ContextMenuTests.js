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
import ContextMenu from 'src/common/component/menu/ContextMenu';
import Menu from 'src/common/component/menu/Menu';

describe("Context Menu component", function(){
    var contextMenu;

    beforeEach(function(){
        contextMenu = new ContextMenu([{ label: "My Issues", action: "show" }]);
        contextMenu.$el.width(200);
    });

    afterEach(function() {
        contextMenu && contextMenu.remove();
        $(".menu.vertical.dropDown.fitable").remove();
    });

    it('should be Menu instance', function(){
        expect(ContextMenu.prototype instanceof Menu).toBeTruthy();
    });

    it("should be initialized with proper API and defaults", function(){
        expect(contextMenu).toBeDefined();
        expect(contextMenu.show).toBeDefined();
        expect(contextMenu.remove).toBeDefined();
        expect(contextMenu.topPadding).toEqual(5);
        expect(contextMenu.leftPadding).toEqual(5);
    });

    it("should throw exception when not enough params" , function(){
        expect(function(){contextMenu.show({left: 100})}).toThrow(new Error("Required params (top, left) missing: " +
            JSON.stringify({left: 100})));
    });

    it("should set paddings", function(){
        contextMenu && contextMenu.remove();
        $(".menu.vertical.dropDown.fitable").remove();

        contextMenu = new ContextMenu([{ label: "My Issues", action: "show" }], {topPadding: 10, leftPadding: 10});

        expect(contextMenu.topPadding).toEqual(10);
        expect(contextMenu.leftPadding).toEqual(10);
    });

    it("should set context menu position within container", function(){
        var container = $("<div></div>");
        container.css("width", 500);
        container.css("height", 500);
        container.css("top", 100);
        container.css("left", 100);

        contextMenu.show({top: 150, left: 150}, container);

        expect(contextMenu.top).toEqual(150);
        expect(contextMenu.left).toEqual(150);

        container.remove();
    });

    it("should set context menu position within container in case of context menu sizes do not fully intersect with container by width (right)", function(){
        var container = $("<div></div>");
        container.css("width", 500);
        container.css("height", 500);
        container.css("top", 100);
        container.css("left", 100);

        contextMenu.show({top: 150, left: 500}, container);

        var menuWidth = contextMenu.$el.width();

        expect(contextMenu.top).toEqual(150);
        expect(contextMenu.left).toEqual(500-menuWidth-contextMenu.leftPadding);

        container.remove();
    });

    it("should set context menu position within container in case of context menu sizes do not fully intersect with container by height(bottom)", function(){
        var container = $("<div></div>");
        container.css("width", 500);
        container.css("height", 500);
        container.css("top", 100);
        container.css("left", 100);

        contextMenu.show({top: 500, left: 150}, container);

        var menuHeight = contextMenu.$el.height();

        expect(contextMenu.top).toEqual(500-menuHeight-contextMenu.topPadding);
        expect(contextMenu.left).toEqual(150);

        container.remove();
    });

    it("should set context menu position within container in case of context menu sizes do not fully intersect with container by width (left)", function(){
        var container = $("<div></div>");
        container.css("width", 500);
        container.css("height", 500);
        sinon.stub(container, "offset").returns({top: 100, left: 500});

        contextMenu.show({top: 150, left: 502}, container);

        expect(contextMenu.top).toEqual(150);
        expect(contextMenu.left).toEqual(500+contextMenu.leftPadding);

        container.remove();
    });

    it("should set context menu position within container in case of context menu sizes do not fully intersect with container by height (top)", function(){
        var container = $("<div></div>");
        container.css("width", 500);
        container.css("height", 500);
        sinon.stub(container, "offset").returns({top: 500, left: 100});

        contextMenu.show({top: 502, left: 150}, container);

        expect(contextMenu.top).toEqual(500+contextMenu.topPadding);
        expect(contextMenu.left).toEqual(150);

        container.remove();
    });

});