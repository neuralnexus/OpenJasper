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
import _ from 'underscore';
import AttachableMenu from 'src/common/component/menu/AttachableMenu';
import Menu from 'src/common/component/menu/Menu';

var showStub = function(){
    var attachOffset = this.$attachTo.offset(),
        attachHeight = this.$attachTo.height(),
        attachWidth  = this.$attachTo.width();

    var bodyHeight = 500,
        bodyWidth = 500,
        colorPickerWidth = this.$el.innerWidth(),
        colorPickerHeight = this.$el.innerHeight(),
        fitByHeight = attachOffset.top + attachHeight + this.padding.top,
        fitByWidth =  attachOffset.left;

    var top = attachOffset.top + attachHeight + this.padding.top;
    var left = attachOffset.left;

    if(bodyHeight < colorPickerHeight+fitByHeight){
        top = attachOffset.top - colorPickerHeight - this.padding.top;
    }
    if(bodyWidth < colorPickerWidth+fitByWidth){
        left = attachOffset.left - colorPickerWidth + attachWidth;
    }

    _.extend(this, {top: top, left: left});

    this.$el.css({ top: this.top, left: this.left });

    this.$el.show();
};

describe('AttachableMenu component', function(){
    var menu;

    beforeEach(function() {
        menu = new AttachableMenu([ { label: "Save Dashboard", action: "save" } ], $("<button></button>"));
    });

    afterEach(function() {
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof AttachableMenu).toBe('function');
        expect(AttachableMenu.prototype instanceof Backbone.View).toBeTruthy();
    });

    it('should be Menu instance', function(){
        expect(AttachableMenu.prototype instanceof Menu).toBeTruthy();
    });

    it('should be able to instantiate AttachableMenu without attachTo setting', function(){
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();

        expect(new AttachableMenu([ { label: "Save Dashboard", action: "save" } ])).toBeDefined();
    });

    it("should show menu near element", function() {
        menu && menu.remove();
        $(".menu.vertical.dropDown.fitable").remove();

        var showStubFunc = sinon.stub(AttachableMenu.prototype, "show").callsFake(showStub);

        menu = new AttachableMenu([ { label: "Save Dashboard", action: "save" } ], $("<button></button>"));

        var offsetStub = sinon.stub(menu.$attachTo, "offset").returns({ top: 30, left: 30 }),
            heightStub = sinon.stub(menu.$attachTo, "height").returns(50),
            widthStub = sinon.stub(menu.$attachTo, "width").returns(50),
            cssSpy = sinon.spy(menu.$el, "css");

        menu.show();

        expect(offsetStub).toHaveBeenCalled();
        expect(heightStub).toHaveBeenCalled();
        expect(cssSpy).toHaveBeenCalledWith({
            top: 80,
            left: 30
        });
        expect(showStubFunc).toHaveBeenCalled();

        offsetStub.restore();
        heightStub.restore();
        cssSpy.restore();
        showStubFunc.restore();
    });
});