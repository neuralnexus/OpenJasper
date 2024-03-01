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
import abstractPanelTrait from 'src/common/component/panel/trait/abstractPanelTrait';
import Panel from 'src/common/component/panel/Panel';

describe('Panel component', function(){
    var panel;

    beforeEach(function() {
        panel = new Panel({
            content: "some content here"
        });
        $("body").append(panel.el);
    });

    afterEach(function() {
        panel && panel.remove();
    });

    it('should be Backbone.View instance', function(){
        expect(typeof Panel).toBe('function');
        expect(Panel.prototype instanceof Backbone.View).toBeTruthy();
    });

    it('should have public functions', function() {
        expect(panel.render).toBeDefined();
        expect(panel.open).toBeDefined();
        expect(panel.close).toBeDefined();
        expect(panel.remove).toBeDefined();
        expect(panel.toggleCollapsedState).toBeDefined();
    });

    it('should have no traits by default', function() {
        expect(_.isArray(panel.traits)).toBe(true);
        expect(panel.traits.length).toBe(0);
    });

    it('should have traits passed through options', function() {
        panel && panel.remove();

        panel = new Panel({
            traits: [ abstractPanelTrait ],
            content: "some content here"
        });

        expect(panel.traits.length).toBe(1);
        expect(panel.traits[0]).toBe(abstractPanelTrait);
    });

    it("should invoke traits for most of methods", function() {
        panel && panel.remove();

        var invokeTraitsSpy = sinon.spy(Panel.prototype, "invokeTraits"),
            options = { content: "some content here" };

        panel = new Panel(options);

        panel.close();

        panel.remove();

        expect(invokeTraitsSpy).toHaveBeenCalled();
        expect(invokeTraitsSpy.getCall(0).args[0]).toBe("onConstructor");
        expect(invokeTraitsSpy.getCall(1).args[0]).toBe("beforeSetElement");
        expect(invokeTraitsSpy.getCall(2).args[0]).toBe("afterSetElement");
        expect(invokeTraitsSpy.getCall(3).args[0]).toBe("beforeInitialize");
        expect(invokeTraitsSpy.getCall(4).args[0]).toBe("beforeOpen");
        expect(invokeTraitsSpy.getCall(5).args[0]).toBe("afterOpen");
        expect(invokeTraitsSpy.getCall(6).args[0]).toBe("afterInitialize");
        expect(invokeTraitsSpy.getCall(7).args[0]).toBe("beforeClose");
        expect(invokeTraitsSpy.getCall(8).args[0]).toBe("afterClose");
        expect(invokeTraitsSpy.getCall(9).args[0]).toBe("onRemove");

        invokeTraitsSpy.restore();
    });

    it("should extend instance with extensions from trait", function() {
        panel && panel.remove();

        var trait = _.extend({}, abstractPanelTrait, {
            extension: {
                myNewMethod: function() {}
            }
        });

        panel = new Panel({
            content: "some content here",
            traits: [ trait ]
        });

        expect(panel.myNewMethod).toBeDefined();
    });

    it("invokeTraits should call appropriate method of trait", function() {
        panel && panel.remove();

        panel = new Panel({
            content: "some content here",
            traits: [ abstractPanelTrait ]
        });

        var onConstructorSpy = sinon.spy(abstractPanelTrait, "onConstructor");

        panel.invokeTraits("onConstructor");

        expect(onConstructorSpy).toHaveBeenCalled();

        onConstructorSpy.restore();
    });

    it('should have a content container', function() {
        expect(panel.$contentContainer).toBeDefined();
    });

    it('should be expanded by default', function() {
        expect(panel.$contentContainer.is(":visible")).toEqual(true);
    });

    it('should be collapsed if param passed to constructor', function() {
        panel && panel.remove();

        panel = new Panel({
            collapsed:true,
            title: "somePanel",
            content: "some content here"
        });

        expect(panel.$contentContainer.is(":visible")).toEqual(false);
    });

    it('should append content to panel in case of string', function() {
        expect(panel.$(".subcontainer").text()).toEqual("some content here");
    });

    it('should render content view to panel in case of Backbone view', function() {
        panel && panel.remove();

        var contentView = new Backbone.View({ className: "someTestClass" }),
            contentViewRenderSpy = sinon.spy(contentView, "render");

        panel = new Panel({
            buttons: [ { label: "Save", action: "save", primary: true } ],
            content: contentView
        });

        sinon.assert.calledWith(contentViewRenderSpy);

        expect(panel.$(".subcontainer").find(".someTestClass").length).toEqual(1);

        contentViewRenderSpy.restore();
    });

    it('it should show its content on open', function() {
        panel.open();

        expect(panel.$contentContainer.is(":visible")).toEqual(true);
    });

    it('it should trigger "open" event on open', function() {
        var triggerSpy = sinon.spy(panel, "trigger");

        panel.open();

        sinon.assert.calledWith(triggerSpy, "open", panel);

        triggerSpy.restore();
    });

    it('it should hide content on close', function() {
        panel.close();

        expect(panel.$contentContainer.is(":visible")).toEqual(false);
    });

    it('it should hide content on close and show it again', function() {
        panel.close();
        panel.open();

        expect(panel.$contentContainer.is(":visible")).toEqual(true);
    });

    it('it should trigger "close" event on close', function() {
        var triggerSpy = sinon.spy(panel, "trigger");

        panel.open();

        panel.close();

        sinon.assert.calledWith(triggerSpy, "close", panel);

        triggerSpy.restore();
    });

    it("should be expanded by default", function() {
        expect(panel.collapsed).toBe(false);
    });

    it("should be collapsed if parameter is set to true", function() {
        var pane = new Panel({
            collapsed: true,
            content: "some content here"
        });

        expect(pane.collapsed).toBe(true);
        pane.remove();
    });

    it("should be expanded if parameter is set to false", function() {
        var pane = new Panel({
            collapsed: false,
            content: "some content here"
        });

        expect(pane.collapsed).toBe(false);
        pane.remove();
    });

    it("should be expanded no parameter set", function() {
        var pane = new Panel({
            collapsed: false,
            content: "some content here"
        });

        expect(pane.collapsed).toBe(false);
        pane.remove();
    });


});