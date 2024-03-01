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
import Dialog from 'src/components/dialog/Dialog';
import _ from 'underscore';
import $ from 'jquery';
import Backbone from 'backbone';

describe("Dialog.", function() {
    var dialog,
        sandbox,
        scrollCorrection,
        innerRect,
        outerRect;

    beforeEach(function() {

        sandbox = sinon.createSandbox();

        var elem = $(
            "<div>" +
            "<div class='jr-jDialogTitle'></div>" +
            "<div class='jr-jDialogDraggable'></div>" +
            "<textarea></textarea>" +
            "<div class='jr-jDialogClose'></div>" +
            "<div class='jr-jDialogResizer'></div>" +
            "</div>"
        )[0];

        innerRect = {
            height: 100,
            width: 100
        };
        outerRect = {
            height: 400,
            width: 400
        };

        scrollCorrection = {
            height: 50,
            width: 50
        };

        dialog = new Dialog({
            el: elem
        });
    });

    afterEach(function() {
        sandbox.restore();
        dialog.remove();
    });

    it("should reset size on open", function () {
        var $textarea = dialog.$el.find("textarea");

        dialog.$el.css({
            width: "100px",
            height: "100px"
        });

        $textarea.css({
            width: "50px",
            height: "50px"
        });

        dialog.open();

        expect(dialog.$el[0].style.width).toEqual("");
        expect(dialog.$el[0].style.height).toEqual("");
        expect($textarea[0].style.width).toEqual("");
        expect($textarea[0].style.height).toEqual("");
    });

    describe("Static Helpers", function() {

        describe("Calculate Position", function() {
            var options;

            beforeEach(function() {

                options = {
                    innerRect: innerRect,
                    outerRect: outerRect,
                    scrollCorrection: scrollCorrection
                };
            });

            it("should return correct position", function() {
                var result = Dialog.calculateCenterPosition(options);

                expect(result).toEqual({
                    left: 200,
                    top: 200
                });
            });

            it("should work without scroll correction", function() {
                options.scrollCorrection = null;

                var result = Dialog.calculateCenterPosition(options);

                expect(result).toEqual({
                    left: 150,
                    top: 150
                });
            });

            it("should show error if set not an object", function() {
                innerRect.height = 100;
                innerRect.width = 100;
                options.outerRect = "test";

                expect(function() {
                    Dialog.calculateCenterPosition(options)
                }).toThrow(new Error("Can't calculate position. Make sure that you pass dimension as integer values"));
            });

            it("should show error if outerRect or innerRect doesn't set", function() {
                outerRect.height = 400;
                outerRect.width = 400;
                options.innerRect = null;

                expect(function() {
                    Dialog.calculateCenterPosition(options)
                }).toThrow(new Error("Illegal arguments"));
            });

            it()
        });

        describe("fit in provided coordinates", function() {
            var coordinates,
                outerRect,
                elemRect,
                options;

            beforeEach(function() {
                coordinates = {
                    top: 100,
                    left: 100
                };

                outerRect = {
                    height: 1000,
                    width: 600
                };

                elemRect = {
                    height: 300,
                    width: 150
                };

                options = {
                    coordinates: coordinates,
                    outerRect: outerRect,
                    elemRect: elemRect
                }

            });

            it("should keep provided position", function() {
                var position = Dialog.fitInProvidedCoordinates(options);

                expect(position).toEqual({
                    top: 100,
                    left: 100
                });
            });

            it("should fit in height", function() {
                coordinates.top = 900;

                var position = Dialog.fitInProvidedCoordinates(options);

                expect(position).toEqual({
                    top: 600,
                    left: 100
                });

            });

            it("should fit in width", function() {
                coordinates.left = 500;

                var position = Dialog.fitInProvidedCoordinates(options);

                expect(position).toEqual({
                    top: 100,
                    left: 350
                });

            });

            it("should fit both in width and height", function() {
                coordinates.top = 900;
                coordinates.left = 500;

                var position = Dialog.fitInProvidedCoordinates(options);

                expect(position).toEqual({
                    top: 600,
                    left: 350
                });
            });

            it("should position by center of element rect", function() {
                coordinates.top = 300;
                coordinates.left = 300;
                coordinates.topPoint = 0.5;
                coordinates.leftPoint = 0.5;

                var position = Dialog.fitInProvidedCoordinates(options);

                expect(position).toEqual({
                    top: 150,
                    left: 225
                });
            });
        });
    });

    describe("Events", function() {

        beforeEach(function() {
            this.closeStub = sandbox.stub(Dialog.prototype, "close");
        });

        it("should call 'close' when user click on closable element", function() {

            dialog.$(".jr-jDialogClose").trigger("click");

            expect(this.closeStub).toHaveBeenCalledOnce();
        });

        it("should trigger event 'dialog:close' when user click on element", function() {

            var handlerSpy = sandbox.spy();
            dialog.on("dialog:close", handlerSpy);

            dialog.$(".jr-jDialogClose").trigger("click");

            expect(handlerSpy).toHaveBeenCalled();
        });

        it("can prevent dialog from closing", function() {

            dialog.on("dialog:close", function(evt) {
                evt.preventDefault();
            });

            dialog.$(".jr-jDialogClose").trigger("click");

            expect(this.closeStub).not.toHaveBeenCalled();

        });

    });

    describe("Delegation and undelegation of draggable functionality", function() {

        beforeEach(function() {
            sandbox.stub(dialog.$el, "draggable");
        });

        it("should initialize draggable plugin with default options during event delegation", function() {
            dialog.delegateEvents();

            expect(dialog.$el.draggable).toHaveBeenCalledWith({
                handle: ".jr-jDialogDraggable",
                addClasses: false,
                containment: "document"
            });
        });

        it("should destroy draggable plugin  during event undelegation", function() {
            dialog.undelegateEvents();

            expect(dialog.$el.draggable).toHaveBeenCalledWith("destroy");
        });

    });

    describe("Delegation and undelegation of resizer functionality", function() {

        var $, resizable;

        beforeEach(function() {
            resizable = sandbox.stub(dialog.$el, "resizable");

            $ = sandbox.stub().returns(["elem"]);

            dialog.$ = $;
        });

        it("should initialize draggable plugin with default options during event delegation", function() {
            dialog.delegateEvents();

            expect(dialog.$el.resizable.args[1][0].handles.se).toEqual([
                "elem"
            ]);
        });

        it("should't apply resizer if no such elem in markup", function() {
            dialog.$.withArgs(".jr-jDialogResizer").returns([]);
            sandbox.stub(Backbone.View.prototype, "delegateEvents");

            dialog.delegateEvents();

            expect(resizable).not.toHaveBeenCalled();
        });

        it("should destroy draggable plugin  during event undelegation", function() {
            dialog.undelegateEvents();

            expect(dialog.$el.resizable).toHaveBeenCalledWith("destroy");
        });

    });

    describe("Setter and Getter", function() {

        it("should throw an exception if 'title' isn't a string", function() {
            expect(_.bind(function() {
                dialog.title = 3;
            }, this)).toThrow(new Error("'Title' should be string"));
        });

        it("title should be set in DOM", function() {
            dialog.title = "Title";
            expect(dialog.$(".jr-jDialogTitle").html()).toEqual("Title");
        });

        describe("Overlay", function() {
            it("should throw an exception if 'modal' isn't a boolean", function() {
                expect(_.bind(function() {
                    dialog.modal = 3;
                }, this)).toThrow(new Error("'Modal' should be boolean"));
            });

            it("should has class 'jr-isHidden' if modal is false", function() {
                dialog.modal = false;
                dialog.open();
                expect($("body").find(".jr-mOverlay").hasClass("jr-isHidden")).toBeTruthy();
            });

            it("should hasn't class 'jr-isHidden' if modal is true", function() {
                dialog.modal = true;
                dialog.open();
                expect($("body").find(".jr-mOverlay").hasClass("jr-isHidden")).toBeFalsy();
            });
        });


    });

    describe("Methods", function() {

        it("'close' should hide dialog", function() {
            dialog.close();
            expect(dialog.$el.hasClass("jr-isHidden")).toBeTruthy();
        });

        describe("Open", function() {

            var calculateCenterPosition,
                fitInProvidedCoordinates,
                expectedPosition,
                $body,
                $window,
                $overlay,
                $el,
                original = {};

            beforeEach(function(){
                expectedPosition = {
                    top: 10,
                    left: 10
                };

                $body = {
                    width: sandbox.stub().returns(outerRect.width),
                    height: sandbox.stub().returns(outerRect.height)
                };

                $window = {
                    width: sandbox.stub().returns(outerRect.width),
                    height: sandbox.stub().returns(outerRect.height),
                    scrollLeft: sandbox.stub().returns(scrollCorrection.width),
                    scrollTop: sandbox.stub().returns(scrollCorrection.height)
                };

                $overlay = {
                    show: sandbox.stub(),
                    remove: sandbox.stub()
                };

                $el = {
                    width: sandbox.stub().returns(innerRect.width),
                    height: sandbox.stub().returns(innerRect.height),
                    removeClass: sandbox.stub(),
                    css: sandbox.stub(),
                    addClass: sandbox.stub(),
                    remove: sandbox.stub(),
                    find: sandbox.stub().returns({
                        css: sandbox.stub()
                    })
                };

                original = {
                    $body: dialog.$body,
                    $window: dialog.$window,
                    $overlay: dialog.$overlay,
                    $el: dialog.$el
                };
                dialog.$body = $body;
                dialog.$window = $window;
                dialog.$overlay = $overlay;
                dialog.$el = $el;

                calculateCenterPosition = sandbox.stub(Dialog, "calculateCenterPosition");
                fitInProvidedCoordinates = sandbox.stub(Dialog, "fitInProvidedCoordinates");

                calculateCenterPosition.returns(expectedPosition);
                fitInProvidedCoordinates.returns(expectedPosition);
            });

            afterEach(function() {
                dialog.$body = original.$body;
                dialog.$window = original.$window;
                dialog.$overlay = original.$overlay;
                dialog.$el = original.$el;
            });


            it("should make dialog visible", function() {
                dialog.open();

                expect($el.removeClass).toHaveBeenCalledWith("jr-isHidden");
            });

            it("should calculate position, but not fit in position", function() {
                dialog.open();

                expect(calculateCenterPosition).toHaveBeenCalled();
                expect(fitInProvidedCoordinates).not.toHaveBeenCalled();
            });

            it("should fit in position if coordinates were passed", function() {
                dialog.open({ top: 10, left: 10});

                expect(calculateCenterPosition).not.toHaveBeenCalled();
                expect(fitInProvidedCoordinates).toHaveBeenCalled();
            });

            it("should calculate fit in position", function() {
                var coordinates = { top: 10, left: 10};

                dialog.open({ top: 10, left: 10});

                expect(fitInProvidedCoordinates).toHaveBeenCalledWith({
                    elemRect: innerRect,
                    outerRect: outerRect,
                    coordinates: coordinates
                });
            });

            it("should calculate center position", function() {
                dialog.open();

                expect(calculateCenterPosition).toHaveBeenCalledWith({
                    innerRect: innerRect,
                    outerRect: outerRect,
                    scrollCorrection: scrollCorrection
                });
            });

            it("should set calculated position to element 1", function() {
                dialog.open();

                expect($el.css).toHaveBeenCalledWith({
                    position: "absolute"
                });

                expect($el.css).toHaveBeenCalledWith({
                    top: expectedPosition.top,
                    left: expectedPosition.left,
                    zIndex: 4000
                });
            });

            it("should set calculated position to element 2", function() {
                dialog.open({ top: 10, left: 10});

                expect($el.css).toHaveBeenCalledWith({
                    position: "absolute"
                });

                expect($el.css).toHaveBeenCalledWith({
                    top: expectedPosition.top,
                    left: expectedPosition.left,
                    zIndex: 4000
                });
            });

            it("should show overlay if modal is true", function() {
                dialog.modal = true;
                dialog.open();

                expect($overlay.show).toHaveBeenCalled();
            });

        });
    });
});