/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * @version: $Id: components.tooltip.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery",
        "core.events.bis",
        "components.tooltip",
        "text!templates/jsTooltip.htm"],
        function(jQuery, events, JSTooltip, jsTooltipText) {

        describe("components.tooltip", function() {
            var tooltip, element, clock;

            beforeEach(function() {
                setTemplates(jsTooltipText);
                element = jQuery("#sampleElement");
                tooltip = new JSTooltip($(element.get(0)), {text: "Hi all!"});
                clock = sinon.useFakeTimers();
            });

            afterEach(function() {
                tooltip.hide();
                clock.restore()
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

                expect(jQuery("#" + tooltipElementId).size()).toBe(1);
                expect(jQuery(".tooltip")).toBeVisible();

                tooltip.hide();

                expect(jQuery("#" + tooltipElementId).size()).toBe(1);
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

                expect(jQuery("#" + tooltipElementId).size()).toBe(1);
                expect(jQuery(".tooltip")).toBeVisible();

                jQuery("#sampleElement").remove();
                tooltipModule.cleanUp();

                expect(jQuery("#" + tooltipElementId).size()).toBe(1);
                expect(jQuery(".tooltip")).not.toBeVisible();
            });
        });
    });