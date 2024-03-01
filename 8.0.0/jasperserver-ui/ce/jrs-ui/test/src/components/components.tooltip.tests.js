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
import jQuery from 'jquery';
import {JSTooltip, tooltipModule} from 'src/components/components.tooltip';
import jsTooltipText from './test/templates/jsTooltip.htm';
import setTemplates from 'js-sdk/test/tools/setTemplates';

import 'src/core/core.events.bis';

describe("components.tooltip", function() {
    var tooltip, element, clock, sandbox;

    document.fire("dom:loaded");

    beforeEach(function() {
        sandbox = sinon.createSandbox();

        setTemplates(jsTooltipText);
        element = jQuery("#sampleElement");
        tooltip = new JSTooltip((element.get(0)), {text: "Hi all!"});
        clock = sinon.useFakeTimers();
    });

    afterEach(function() {
        tooltip.hide();
        clock.restore();
        sandbox.restore();
    });

    it("should create tooltip", function() {
        expect(tooltip).toBeDefined();
    });

    it("show should display tooltip", function() {
        expect(tooltip._element).toBeFalsy();

        tooltip.show();

        expect(tooltip._element).toBeDefined();
        expect(jQuery(tooltip._element)).toBeVisible();
    });

    it("hide should remove tooltip element", function() {
        tooltip.show();
        var tooltipElementId = tooltip._element.getAttribute("id");

        expect(jQuery("#" + tooltipElementId).length).toBe(1);
        expect(jQuery(".tooltip")).toBeVisible();

        tooltip.hide();

        expect(jQuery("#" + tooltipElementId).length).toBe(1);
        expect(jQuery(".tooltip")).not.toBeVisible();
    });

    it("mouseover should display tooltip", function() {
        expect(jQuery(".tooltip")).not.toBeVisible();

        jQuery("#sampleElement").simulate("mouseover");

        clock.tick(1000);

        expect(jQuery(".tooltip")).toBeVisible();
    });

    it("mouseout should hide tooltip", function() {
        tooltip.show();
        expect(jQuery(".tooltip")).toBeVisible();

        jQuery("#sampleElement").simulate("mouseout");

        expect(jQuery(".tooltip")).not.toBeVisible();
    });

    it("click on element should hide its tooltip", function() {
        expect(jQuery(".tooltip")).not.toBeVisible();

        tooltip.show();
        expect(jQuery(".tooltip")).toBeVisible();

        jQuery("#sampleElement").trigger("click");
        expect(jQuery(".tooltip")).not.toBeVisible();
    });

    it("cleanUp hide remove tooltip on element removal", function() {
        tooltip.show();
        var tooltipElementId = tooltip._element.getAttribute("id");

        expect(jQuery("#" + tooltipElementId).length).toBe(1);
        expect(jQuery(".tooltip")).toBeVisible();

        jQuery("#sampleElement").remove();
        tooltipModule.cleanUp();

        expect(jQuery("#" + tooltipElementId).length).toBe(1);
        expect(jQuery(".tooltip")).not.toBeVisible();
    });

    it("should update tooltip text", function() {
        tooltip.show();

        var tooltipElementId = tooltip._element.getAttribute("id");
        var tooltipEl = jQuery("#" + tooltipElementId);
        var tooltipMessageEl = tooltipEl.find("p.message");

        expect(jQuery.trim(tooltipMessageEl.text())).toEqual("Hi all!");

        tooltip.updateText(["testText"]);

        expect(jQuery.trim(tooltipMessageEl.text())).toEqual("testText");
    });

    it("should update tooltip text as empty string", function() {
        tooltip.show();

        var tooltipElementId = tooltip._element.getAttribute("id");
        var tooltipEl = jQuery("#" + tooltipElementId);
        var tooltipMessageEl = tooltipEl.find("p.message");

        expect(jQuery.trim(tooltipMessageEl.text())).toEqual("Hi all!");

        tooltip.updateText([""]);

        expect(jQuery.trim(tooltipMessageEl.text())).toEqual("");
    });

    it("should disable tooltips", function() {
        var stubs = [];

        tooltipModule.tooltips.forEach(function(tooltip) {
            sandbox.stub(tooltip, "disable");

            stubs.push(tooltip.disable);
        });

        tooltipModule.disableTooltips();

        stubs.forEach(function(stub) {
            expect(stub).toHaveBeenCalled();
        });
    });

    it("should enable tooltips", function() {
        var stubs = [];

        tooltipModule.tooltips.forEach(function(tooltip) {
            sandbox.stub(tooltip, "enable");

            stubs.push(tooltip.enable);
        });

        tooltipModule.enableTooltips();

        stubs.forEach(function(stub) {
            expect(stub).toHaveBeenCalled();
        });
    });

    it("should not enable tooltip without offsets set (never showed before tooltips)", function() {
        var firstTooltip = tooltipModule.tooltips[0];

        var firstTooltipOffsets = firstTooltip.offsets;

        firstTooltip.offsets = undefined;

        sandbox.spy(firstTooltip, "enable");
        sandbox.spy(tooltipModule, "showJSTooltip");
        sandbox.spy(tooltipModule, "cleanUp");

        firstTooltip.enable();

        expect(tooltipModule.showJSTooltip).toHaveBeenCalled();
        expect(tooltipModule.cleanUp).not.toHaveBeenCalled();

        firstTooltip.offsets = firstTooltipOffsets;
    });
});