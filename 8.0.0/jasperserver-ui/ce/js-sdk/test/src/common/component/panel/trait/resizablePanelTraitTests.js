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
import resizablePanelTrait from 'src/common/component/panel/trait/resizablePanelTrait';
import $ from 'jquery';

describe("resizablePanelTrait", function() {
    let sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();
    });

    afterEach(function () {
        sandbox.restore();
    });

    it("should assign default handles, minWidth and maxWidth in 'onConstructor' method", function() {
        var obj = {};

        resizablePanelTrait.onConstructor.call(obj, {});

        expect(obj.handles).toBeDefined();
        expect(obj.minWidth).toBeDefined();
        expect(obj.maxWidth).toBeDefined();
    });

    it("should assign passed through options handles, minWidth and maxWidth in 'onConstructor' method", function() {
        var obj = {};

        resizablePanelTrait.onConstructor.call(obj, {
            handles: "e",
            minWidth: 100,
            maxWidth: 500
        });

        expect(obj.handles).toBe("e");
        expect(obj.minWidth).toBe(100);
        expect(obj.maxWidth).toBe(500);
    });

    it("should call 'resizable' method on $el in 'afterSetElement' method with string handles", function() {
        var obj = {
            $el: $("<div></div>"),
            handles: "s",
            minHeight: 10,
            minWidth: 20,
            maxWidth: 40,
            maxHeight: 20,
            alsoResize: true
        };

        var resizableStub = sinon.stub(obj.$el, "resizable");

        resizablePanelTrait.afterSetElement.call(obj, {});

        var args = resizableStub.args[0][0];

        expect(resizableStub).toHaveBeenCalled();

        expect(args.handles).toEqual("s");
        expect(args.minHeight).toEqual(10);
        expect(args.minWidth).toEqual(20);
        expect(args.maxWidth).toEqual(40);
        expect(args.maxHeight).toEqual(20);
        expect(args.alsoResize).toBeTruthy();

        resizableStub.restore();
    });

    it("should invoke handles if its a function in 'afterSetElement' method", function() {
        var handlesStub = sandbox.stub().returns("s");

        var $div = $("<div></div>");

        var obj = {
            $el: $div,
            handles: handlesStub
        };

        var resizableStub = sandbox.stub(obj.$el, "resizable");

        resizablePanelTrait.afterSetElement.call(obj, {});

        var args = resizableStub.args[0][0];

        expect(resizableStub).toHaveBeenCalled();
        expect(args.handles).toEqual("s");
        expect(handlesStub).toHaveBeenCalledWith($div);

        resizableStub.restore();
    });

    it("should call 'resizable' method on $el in 'afterSetElement' method with object handles", function() {
        var obj = {
            $el: $("<div><div class='resize-handle'></div></div>"),
            handles: {
                "s": ".resize-handle"
            }
        };

        var resizableStub = sinon.stub(obj.$el, "resizable");

        resizablePanelTrait.afterSetElement.call(obj, {});

        var args = resizableStub.args[0][0];

        expect(resizableStub).toHaveBeenCalled();

        expect(args.handles).toEqual({
            "s": ".resize-handle"
        });

        resizableStub.restore();
    });

    it("should destroy 'resizable' on $el in 'onRemove' method", function() {
        var obj = {
            $el: $("<div></div>")
        };

        var resizableStub = sinon.stub(obj.$el, "resizable");

        resizablePanelTrait.onRemove.call(obj, {});

        expect(resizableStub).toHaveBeenCalledWith("destroy");

        resizableStub.restore();
    });
});