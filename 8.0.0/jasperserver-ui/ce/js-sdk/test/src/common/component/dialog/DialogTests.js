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
import Dialog from 'src/common/component/dialog/Dialog';
import OptionContainer from 'src/common/component/base/OptionContainer';

var positionStub = function(coordinates){
    var top, left;

    if(coordinates){
        top = coordinates.top;
        left = coordinates.left;

        var elHeight = this.$el.height();
        var elWidth = this.$el.width();

        var bodyHeight = coordinates.bodyHeight || 40;
        var bodyWidth = coordinates.bodyWidth || 40;

        var fitByHeight = bodyHeight - coordinates.top;
        var fitByWidth =  bodyWidth - coordinates.left;

        if(fitByHeight < elHeight){
            top = coordinates.top - elHeight;
            top = (top < 0) ? (bodyHeight/2 - elHeight/2) : top
        }
        if(fitByWidth < elWidth){
            left = coordinates.left - elWidth;
            left = (left < 0) ? (bodyWidth/2 - elWidth/2) : left
        }

    }else{
        top = $(window).height() / 2 - this.$el.height() / 2;
        left = $(window).width() / 2 - this.$el.width() / 2;
    }

    this.$el.css({
        top: top,
        left: left,
        position: "absolute"
    });

    return this;
};

describe('Dialog component', function(){
    var dialog,
        sandbox;

    beforeEach(function() {
        dialog = new Dialog({
            buttons: [
                { label: "Save", action: "save", primary: true },
                { label: "Cancel", action: "cancel", primary: false }
            ],
            title: "Save Dialog",
            additionalCssClasses: "saveAs",
            modal: true,
            resizable: true,
            content: "some content here"
        });

        sandbox = sinon.createSandbox();
    });

    afterEach(function() {
        sandbox.restore();
        dialog && dialog.remove();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof Dialog).toBe('function');
        expect(Dialog.prototype instanceof Backbone.View).toBeTruthy();
    });


    it('should have public functions', function() {
        expect(dialog.render).toBeDefined();
        expect(dialog.open).toBeDefined();
        expect(dialog.close).toBeDefined();
        expect(dialog.remove).toBeDefined();
    });

    it('should have element with complex structure', function(){
        expect(dialog.$el.hasClass("jr-mDialog")).toBe(true);
        expect(dialog.$(".jr-mDialog-footer").length).toBe(1);
        expect(dialog.$(".jr-mDialog-body").length).toBe(1);
        expect(dialog.$(".jr-mDialog-header").length).toBe(1);
    });

    it('should have additionalCssClasses passed through options', function(){
        expect(dialog.$el.hasClass("saveAs")).toBe(true);
    });

    it('should render title passed through options', function(){
        expect(dialog.$(".jr-mDialog-header-title").text()).toBe("Save Dialog");
    });

    it('should transform buttons into OptionContainer', function() {
        expect(dialog.buttons instanceof OptionContainer).toBe(true);
    });

    it('should be appended to body', function() {
        expect(dialog.$el.parent()[0]).toEqual($("body")[0]);
    });

    it('should be hidden by default', function() {
        expect(dialog.$el.is(":visible")).toEqual(false);
    });

    it('should add dimmer to page if is modal and there is no dimmer already added', function() {
        expect($(".dimmer").length).toEqual(1);
        expect($(".dimmer").parent()[0]).toEqual($("body")[0]);
    });

    it('should be draggable', function() {
        expect(dialog.$el.draggable("option", "disabled")).toEqual(false);
    });

    it('should be resizable if corresponding option was passed to constructor', function() {
        expect(dialog.$el.resizable("option", "disabled")).toEqual(false);
    });

    it('should not be resizable by default', function() {
        dialog.remove();

        dialog = new Dialog({
            buttons: [ { label: "Save", action: "save", primary: true } ]
        });

        expect(dialog.resizable).toEqual(false);
    });

    it('should append content to dialog in case of string', function() {
        expect(dialog.$(".jr-mDialog-body-wrapper").text()).toEqual("some content here");
    });

    it('should render content view to dialog in case of Backbone view', function() {
        dialog.remove();

        var contentView = new Backbone.View({ className: "someTestClass" }),
            contentViewRenderSpy = sinon.spy(contentView, "render");

        dialog = new Dialog({
            buttons: [ { label: "Save", action: "save", primary: true } ],
            content: contentView
        });

        sinon.assert.calledWith(contentViewRenderSpy);

        expect(dialog.$(".jr-mDialog-body").find(".someTestClass").length).toEqual(1);

        contentViewRenderSpy.restore();
    });

    it('should render buttons', function() {
        expect(dialog.buttons.collection.length).toEqual(2);
        expect(dialog.$(".jr-mDialog-footer").find("button").length).toEqual(2);
    });

    it('should show dimmer on open if is modal and trigger "dialog:visible" event', function() {
        var dialogTriggerSpy = sinon.spy(dialog, "trigger");
        dialog.open();

        expect(dialogTriggerSpy).toHaveBeenCalledWith("dialog:visible");
        expect($(".dimmer").is(":visible")).toEqual(true);

        dialogTriggerSpy.restore();
    });

    it('should not show dimmer by default', function() {
        dialog.remove();

        var $dimmer = $("<div class='dimmer hidden' style='z-index: 900; display: none;'></div>");
        $("body").append($dimmer);

        dialog = new Dialog({
            buttons: [ { label: "Save", action: "save", primary: true } ]
        });

        expect($(".dimmer").is(":visible")).toEqual(false);

        $dimmer.remove();
    });

    it("should reset size on open", function () {
        dialog.$el.css({
            width: "100px",
            height: "100px"
        });

        dialog.open();

        expect(dialog.$el[0].style.width).toEqual("");
        expect(dialog.$el[0].style.height).toEqual("");
    });

    it('it should center dialog and increase z-index on open', function() {
        var index = Dialog.highestIndex,
            centerSpy = sinon.spy(dialog, "_position"),
            increaseZIndexSpy = sinon.spy(dialog, "_increaseZIndex");

        dialog.open();

        sinon.assert.calledWith(centerSpy);
        sinon.assert.calledWith(increaseZIndexSpy);

        expect(Dialog.highestIndex).toEqual(index + 2);
        expect(dialog.$el.css("position")).toEqual("absolute");
        expect(dialog.$el.css("zIndex")*1).toEqual(Dialog.highestIndex);

        centerSpy.restore();
        increaseZIndexSpy.restore();
    });

    it('it should show dialog window on open', function() {
        dialog.open();

        expect(dialog.$el.is(":visible")).toEqual(true);
    });

    it('it should trigger "open" event on open', function() {
        var triggerSpy = sinon.spy(dialog, "trigger");

        dialog.open();

        sinon.assert.calledWith(triggerSpy, "open", dialog);

        triggerSpy.restore();
    });

    it('should set min size on dialog open', function() {
        dialog.remove();

        dialog = new Dialog({
            buttons: [
                {
                    label: "Save",
                    action: "save", primary: true
                }
            ],
            minWidth: 200,
            minHeight: 200
        });

        sandbox.spy(dialog.$el, "css");

        dialog.open();

        expect(dialog.$el.css).toHaveBeenCalledWith({
            minHeight: 200
        });

        expect(dialog.$el.css).toHaveBeenCalledWith({
            minWidth: 200
        });
    });

    it('should set min size to size on dialog open', function() {
        dialog.remove();

        dialog = new Dialog({
            buttons: [
                {
                    label: "Save",
                    action: "save", primary: true
                }
            ],
            minWidth: 200,
            minHeight: 200,
            setMinSizeAsSize: true
        });

        sandbox.spy(dialog.$el, "css");

        dialog.open();

        expect(dialog.$el.css).toHaveBeenCalledWith({
            minHeight: 200
        });

        expect(dialog.$el.css).toHaveBeenCalledWith({
            minWidth: 200
        });

        expect(dialog.$el.css).toHaveBeenCalledWith({
            height: 200,
            width: 200
        });
    });

    it('it should hide dimmer on close', function() {
        dialog.open();

        dialog.close();

        expect($(".dimmer").is(":visible")).toEqual(false);
    });

    it('it should hide dialog on close', function() {
        var isVisibleSpy = sinon.spy(dialog, "isVisible");

        dialog.open();

        dialog.close();

        expect(isVisibleSpy).toHaveBeenCalled();
        expect(dialog.$el.is(":visible")).toEqual(false);

        isVisibleSpy.restore();
    });

    it('it should trigger "close" event on close', function() {
        var triggerSpy = sinon.spy(dialog, "trigger");

        dialog.open();

        dialog.close();

        sinon.assert.calledWith(triggerSpy, "close", dialog);

        triggerSpy.restore();
    });

    it("should trigger 'button' event when dialog button is clicked", function() {
        var triggerSpy = sinon.spy(dialog, "trigger");

        dialog.open();

        dialog.$(".jr-mDialog-footer button:eq(0)").trigger("click");

        sinon.assert.calledWith(triggerSpy, "button:save");

        triggerSpy.restore();
    });

    it("should remove buttons", function() {
        var removeSpy = sinon.spy(dialog.buttons, "remove");

        dialog.remove();

        sinon.assert.calledWith(removeSpy);

        removeSpy.restore();
    });

    it("should remove dimmer if it was added by dialog", function() {
        dialog.remove();

        expect($(".dimmer").length).toEqual(0);
    });

    it("should call 'remove' method on content view", function() {
        dialog.remove();

        var contentView = new Backbone.View({ className: "someTestClass" }),
            contentViewRemoveSpy = sinon.spy(contentView, "remove");

        dialog = new Dialog({
            buttons: [ { label: "Save", action: "save", primary: true } ],
            content: contentView
        });

        dialog.remove();
        sinon.assert.calledWith(contentViewRemoveSpy);
    });

    it("should call base Backbone.View 'remove' method", function() {
        var removeSpy = sinon.spy(Backbone.View.prototype, "remove");

        dialog.remove();

        sinon.assert.calledWith(removeSpy);

        removeSpy.restore();
    });

    it("shouldn't increase z-index when clicked/touched dialog is modal", function() {
        dialog.open();

        var index = Dialog.highestIndex;

        dialog.$el.trigger("mousedown");

        expect(Dialog.highestIndex).toEqual(index);
    });

    it("should increase z-index when clicked/touched", function() {
        dialog && dialog.remove();

        dialog = new Dialog({
            buttons: [
                { label: "Save", action: "save", primary: true },
                { label: "Cancel", action: "cancel", primary: false }
            ],
            title: "Save Dialog",
            additionalCssClasses: "saveAs",
            modal: false,
            resizable: true,
            content: "some content here"
        });

        dialog.open();

        var index = Dialog.highestIndex;

        dialog.$el.trigger("mousedown");

        expect(Dialog.highestIndex).toEqual(index + 1);
    });

    it("should set position of dialog due to given coordinates", function(){
        var coordinates = {top: 20, left: 20, bodyWidth: 500, bodyHeight: 500};

        var positionStubFunc = sinon.stub(dialog, "_position").callsFake(positionStub);

        dialog.open(coordinates);

        var dialogOffset = dialog.$el.offset();

        expect(dialogOffset.top).toEqual(coordinates.top);
        expect(dialogOffset.left).toEqual(coordinates.left);

        positionStubFunc.restore();
    });

    it("should center position of dialog due to given coordinates in case of small body", function(){
        var coordinates = {top: 20, left: 20};

        dialog.$el.css("height", 60);
        dialog.$el.css("width", 60);

        var originalPosition = dialog._position;

        dialog._position = positionStub;

        dialog.open(coordinates);

        var dialogOffset = dialog.$el.offset();
        var dialogWidth = dialog.$el.width();
        var dialogHeight = dialog.$el.height();

        var top = parseInt(40/2 - dialogHeight / 2);
        expect(parseInt(dialogOffset.top)).toEqual(top);

        var left = parseInt(40/2 - dialogWidth / 2);
        expect(parseInt(dialogOffset.left)).toEqual(left);

        dialog._position = originalPosition;
    });

    it("should reset Zindex", function(){
        var index = Dialog.highestIndex,
            centerSpy = sinon.spy(dialog, "_position"),
            increaseZIndexSpy = sinon.spy(dialog, "_increaseZIndex");

        dialog.open();

        sinon.assert.calledWith(centerSpy);
        sinon.assert.calledWith(increaseZIndexSpy);

        expect(Dialog.highestIndex).toEqual(index + 2);
        expect(dialog.$el.css("position")).toEqual("absolute");
        expect(dialog.$el.css("zIndex")*1).toEqual(Dialog.highestIndex);

        Dialog.resetHighestIndex();

        expect(Dialog.highestIndex).toEqual(5000);

        Dialog.resetHighestIndex(10000);

        expect(Dialog.highestIndex).toEqual(10000);

        centerSpy.restore();
        increaseZIndexSpy.restore();
    });

    it("should call '_onKeyDown' on keydown event", function(){
        dialog && dialog.remove();
        var onKeyDownSpy = sinon.spy(Dialog.prototype, "_onKeyDown");

        dialog = new Dialog({
            buttons: [
                { label: "Save", action: "save", primary: true },
                { label: "Cancel", action: "cancel", primary: false }
            ],
            title: "Save Dialog",
            additionalCssClasses: "saveAs",
            modal: true,
            resizable: true,
            content: "some content here"
        });


        dialog.$el.trigger("keydown", {keyCode: 13});

        expect(onKeyDownSpy).toHaveBeenCalled();

        onKeyDownSpy.restore();
    });
});