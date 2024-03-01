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


/**
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

import sinon from 'sinon';
import Accordion from 'src/common/component/accordion/Accordion';
import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import 'jquery-ui/ui/widgets/resizable';

describe('Accordion', function(){
    it("should accept various settings in constructor", function() {
        var initOptions = {
                container: $("<div></div>"),
                panels: [ {}, {} ],
                allowMultiplePanelsOpen: true
            },
            acc = new Accordion(initOptions);

        expect(acc.container).toBe(initOptions.container);
        expect(acc.panels).toBe(initOptions.panels);
        expect(acc.allowMultiplePanelsOpen).toBe(initOptions.allowMultiplePanelsOpen);
    });

    it("should throw exception if container is not specified", function() {
        expect(function() { new Accordion()}).toThrow(new Error("Accordion should have specified container"));
        expect(function() { new Accordion({ container: "#someNotExisting" }); }).toThrow(new Error("Accordion should have specified container"));
        expect(function() { new Accordion({ container: $("#someNotExisting") }); }).toThrow(new Error("Accordion should have specified container"));
    });

    it("should set default settings in constructor if not provided in init options", function() {
        var initOptions = {
                container: $("<div></div>")
            },
            acc = new Accordion(initOptions);

        expect(acc.container).toBe(initOptions.container);
        expect(acc.panels).toEqual([]);
        expect(acc.allowMultiplePanelsOpen).toBe(false);
    });

    it("should set 'overflow: hidden' to container in constructor", function() {
        var initOptions = {
                container: $("<div></div>")
            },
            acc = new Accordion(initOptions);

        expect($(acc.container).css("overflow")).toBe("hidden");
    });

    it("should be able to toggle panel and close all other panels if allowMultiplePanelsOpen = false", function() {
        var panel1 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            panel2 = {
                collapsed: true,
                close: function() {},
                open: function() {}
            },
            initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: false
            },
            acc = new Accordion(initOptions),
            panel1CloseSpy = sinon.spy(panel1, "close"),
            panel2OpenSpy = sinon.spy(panel2, "open"),
            fitStub = sinon.stub(acc, "fit");

        acc.toggle(panel2);

        expect(panel1CloseSpy).toHaveBeenCalled();
        expect(panel2OpenSpy).toHaveBeenCalled();
        expect(fitStub).toHaveBeenCalled();

        fitStub.restore();
        panel1CloseSpy.restore();
        panel2OpenSpy.restore();
    });

    it("should be able to expand panel and close all other panels if allowMultiplePanelsOpen = false", function() {
        var panel1 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            panel2 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: false
            },
            acc = new Accordion(initOptions),
            panel1CloseSpy = sinon.spy(panel1, "close"),
            panel2OpenSpy = sinon.spy(panel2, "open"),
            fitStub = sinon.stub(acc, "fit");

        acc.expand(panel2);

        expect(panel1CloseSpy).toHaveBeenCalled();
        expect(panel2OpenSpy).toHaveBeenCalled();
        expect(fitStub).toHaveBeenCalled();

        fitStub.restore();
        panel1CloseSpy.restore();
        panel2OpenSpy.restore();
    });

    it("should be able to collapse panel", function() {
        var panel1 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            panel2 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: false
            },
            acc = new Accordion(initOptions),
            panel1CloseSpy = sinon.spy(panel1, "close"),
            panel2CloseSpy = sinon.spy(panel2, "close"),
            fitStub = sinon.stub(acc, "fit");

        acc.collapse(panel2);

        expect(panel1CloseSpy).not.toHaveBeenCalled();
        expect(panel2CloseSpy).toHaveBeenCalled();
        expect(fitStub).toHaveBeenCalled();

        fitStub.restore();
        panel1CloseSpy.restore();
        panel2CloseSpy.restore();
    });

    it("should be able to toggle panel and not close all other panels if allowMultiplePanelsOpen = true", function() {
        var panel1 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            panel2 = {
                collapsed: true,
                close: function() {},
                open: function() {}
            },
            initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: true
            },
            acc = new Accordion(initOptions),
            panel1CloseSpy = sinon.spy(panel1, "close"),
            panel2OpenSpy = sinon.spy(panel2, "open"),
            fitStub = sinon.stub(acc, "fit");

        acc.toggle(panel2);

        expect(panel1CloseSpy).not.toHaveBeenCalled();
        expect(panel2OpenSpy).toHaveBeenCalled();
        expect(fitStub).toHaveBeenCalled();

        fitStub.restore();
        panel1CloseSpy.restore();
        panel2OpenSpy.restore();
    });

    it("should be able to expand panel and not close all other panels if allowMultiplePanelsOpen = true", function() {
        var panel1 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            panel2 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: true
            },
            acc = new Accordion(initOptions),
            panel1CloseSpy = sinon.spy(panel1, "close"),
            panel2OpenSpy = sinon.spy(panel2, "open"),
            fitStub = sinon.stub(acc, "fit");

        acc.expand(panel2);

        expect(panel1CloseSpy).not.toHaveBeenCalled();
        expect(panel2OpenSpy).toHaveBeenCalled();
        expect(fitStub).toHaveBeenCalled();

        fitStub.restore();
        panel1CloseSpy.restore();
        panel2OpenSpy.restore();
    });

    it("should do nothing if panel cannot be found in collection", function() {
        var panel1 = {
                collapsed: false,
                close: function() {},
                open: function() {}
            },
            panel2 = {
                collapsed: true,
                close: function() {},
                open: function() {}
            },
            initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: true
            },
            acc = new Accordion(initOptions),
            fitStub = sinon.stub(acc, "fit");

        acc.toggle({});
        acc.expand({});
        acc.collapse({});

        expect(fitStub).not.toHaveBeenCalled();

        fitStub.restore();
    });

    it("should 'fit' all items inside container", function() {
        var panel1 = new Backbone.View(),
            panel2 = new Backbone.View(),
            panel3 = new Backbone.View();

        _.extend(panel1, {
            collapsed: false,
            close: function() {},
            open: function() {},
            setHeight: function() {}
        });

        _.extend(panel2, {
            collapsed: true,
            close: function() {},
            open: function() {},
            setHeight: function() {}
        });

        _.extend(panel3, {
            collapsed: false,
            fixedHeight: true,
            close: function() {},
            open: function() {},
            setHeight: function() {}
        });

        var initOptions = {
                container: $("<div></div>"),
                panels: [ panel1, panel2, panel3 ],
                allowMultiplePanelsOpen: true
            },
            acc = new Accordion(initOptions);

        $("body").append(initOptions.container);
        initOptions.container.height(400);

        panel1.$el.height(200);
        panel2.$el.height(30);
        panel3.$el.height(300);

        var panel1SetHeightStub = sinon.stub(panel1, "setHeight"),
            panel2SetHeightStub = sinon.stub(panel2, "setHeight"),
            panel3SetHeightStub = sinon.stub(panel3, "setHeight"),
            triggerSpy = sinon.spy(acc, "trigger");

        acc.fit();

        expect(panel3SetHeightStub).toHaveBeenCalledWith(300);
        expect(panel1SetHeightStub).toHaveBeenCalledWith(70);
        expect(panel2SetHeightStub).not.toHaveBeenCalled();
        expect(triggerSpy).toHaveBeenCalledWith("fit", acc);

        initOptions.container.remove();
        panel1SetHeightStub.restore();
        panel2SetHeightStub.restore();
        panel3SetHeightStub.restore();
        triggerSpy.restore();
    });

    it("should be able to calculate max size for resizable panel", function() {
        var $el = $("<div><div class='header'></div><div class='subcontainer'></div></div>"),
            panel1 = new Backbone.View(),
            panel2 = new Backbone.View(),
            initOptions = {
                container: $el,
                panels: [ panel1, panel2 ],
                allowMultiplePanelsOpen: false
            },
            acc = new Accordion(initOptions);

        _.extend(panel1, {
            collapsed: false,
            $resizableEl: $el.find(".subcontainer"),
            close: function() {},
            open: function() {},
            setHeight: function() {}
        });

        _.extend(panel2, {
            collapsed: true,
            close: function() {},
            open: function() {},
            setHeight: function() {}
        });
        var resizableStub = sinon.stub(acc.panels[0].$resizableEl, "resizable");

        $("body").append(initOptions.container);

        acc.calcMaxHeight(panel1);

        expect(resizableStub).toHaveBeenCalledWith("option", "maxHeight", 0);

        resizableStub.restore();
        initOptions.container.remove();
    });
});
