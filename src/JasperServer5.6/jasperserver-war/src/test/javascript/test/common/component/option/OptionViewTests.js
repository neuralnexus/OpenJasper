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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: OptionViewTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        $ = require("jquery"),
        OptionView = require("common/component/option/OptionView");

    describe('OptionView', function(){
        var optionView;

        beforeEach(function() {
            optionView = new OptionView({
                model: new Backbone.Model({ label: "test" }),
                template: "<div>{{- label }}</div>"
            });
        });

        afterEach(function() {
            optionView && optionView.remove();
        });

        it('should be Backbone.View instance', function(){
            expect(typeof OptionView).toBe('function');
            expect(OptionView.prototype instanceof Backbone.View).toBeTruthy();
        });

        it('should throw exception if "template" or "model" options are not defined', function(){
            expect(function() { new OptionView(); }).toThrow("Option should have defined template");
            expect(function() { new OptionView({}); }).toThrow("Option should have defined template");
            expect(function() { new OptionView({ template: "<div></div>" }); }).toThrow("Option should have associated Backbone.Model");
            expect(function() { new OptionView({ template: "<div></div>", model: {} }); }).toThrow("Option should have associated Backbone.Model");
        });

        it('should have default values for "toggle" and "toggleClass" params', function(){
            expect(optionView.toggle).toBeFalsy();
            expect(optionView.toggleClass).toBe("active");
        });

        it('should have passed values for "toggle" and "toggleClass" params', function(){
            optionView && optionView.remove();

            optionView = new OptionView({
                model: new Backbone.Model({ label: "test" }),
                template: "<div>{{- label }}</div>",
                toggle: true,
                toggleClass: "someClass"
            });

            expect(optionView.toggle).toBeTruthy();
            expect(optionView.toggleClass).toBe("someClass");
        });

        it('should be rendered from passed template', function(){
            expect(optionView.$el.text()).toEqual("test");
        });

        it('should trigger "click" event on model when is clicked', function(){
            var modelTriggerSpy = sinon.spy(optionView.model, "trigger");

            optionView.$el.trigger("click");

            sinon.assert.calledWith(modelTriggerSpy, "click", optionView.model);

            modelTriggerSpy.restore();
        });

        it('should toggle CSS class on element when is clicked if toggle=true', function(){
            optionView && optionView.remove();

            optionView = new OptionView({
                model: new Backbone.Model({ label: "test" }),
                template: "<div>{{- label }}</div>",
                toggle: true,
                toggleClass: "someClass"
            });

            optionView.$el.trigger("click");

            expect(optionView.$el.hasClass("someClass")).toBeTruthy();
        });
    });
});