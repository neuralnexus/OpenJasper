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
 * @version: $Id: attributes.view.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "underscore", "attributes.view", "text!templates/attributes.htm", "mng.common"],
    function($, _, attributes, attributesText){

describe("attributes", function(){
    describe("attributes main view", function(){
        var collection, view;

        beforeEach(function(){
            setTemplates(attributesText);

            collection = attributes.Attributes.instance({urlTemplate: "/rest_v2/users/{{userName}}/attributes/{{modelId}}{{#tenantId}}?tenantId={{tenantId}}{{/tenantId}}"});

            view = new attributes.MainView({
                el:jQuery("#attributesTab")[0],
                collection: collection
            });
        });

        it("should initialize", function(){
            expect(view).toBeDefined();
        });

        it("should validate", function(){
            view.render();
            view.renderItems(view.collection);

            collection.add(new attributes.Attribute({name:"a", value:"b"}));
            collection.add(new attributes.Attribute({name:"a", value:"b"}));

            expect(view.isValid()).toBeFalsy();
        });

        it("should add models", function(){
            view.render();
            view.renderItems(view.collection);

            view.$el.find("#newAttribute .attrName textarea").val("a");
            view.$el.find("#newAttribute .attrValue textarea").val("b");

            view.addModel();

            expect(collection.models.length).toEqual(1);
            expect(collection.models.length).toEqual(1);
        });

        it("should add only valid models", function () {
            var callback = jasmine.createSpy("addView");

            view.render();
            view.renderItems(view.collection);

            view.$el.find("#newAttribute .attrName textarea").val("");
            view.$el.find("#newAttribute .attrValue textarea").val("");

            view.collection.on("add", callback);

            view.addModel();

            expect(collection.models.length).toEqual(0);
            expect(callback).not.toHaveBeenCalled();
        });

        it("should clear form after adding of new attribute", function () {
            view.render();
            view.renderItems(view.collection);

            view.$el.find("#newAttribute .attrName textarea").val("a");
            view.$el.find("#newAttribute .attrValue textarea").val("v");

            view.addModel();

            expect(view.$el.find("#newAttribute .attrName textarea").val()).toEqual("");
            expect(view.$el.find("#newAttribute .attrValue textarea").val()).toEqual("");
            expect(view.addAttributeView.name).toEqual("");
            expect(view.addAttributeView.value).toEqual("");
        });

        it("should create views for attribute models", function(){
            var model = attributes.Attribute.instance();
            view.render();
            view.renderItems(view.collection);

            view.addView(model);

            expect(view.subViews.length).toEqual(1);
            expect(view.subViews[0].action).toEqual("save");
            expect(view.$subEl.find("li").length).toEqual(1);
        });
    });

    describe("attributes item view", function(){
        var model ,view;

        beforeEach(function(){
            setTemplates(attributesText);

            model = attributes.Attribute.instance();

            view = new attributes.AttributeView({
                model:model
            });

            jQuery("#attributesTab").append(view.render().el);
        });

        it("should set name to model", function(){
            var name = "asda";
            var input = view.$el.find(".attrName textarea");
            input.val(name);

            view.respondOnInputName({target:input[0]});

            expect(input.parent()).not.toHasClass("error");
            expect(view.model.get("name")).toEqual(name);
            expect(view.action).toEqual("save");

        });

        it("should not set not valid name to model and show error", function(){
            view.name = "as";
            var name = "";
            var input = view.$el.find(".attrName textarea");

            input.val(name);
            view.respondOnInputName({target:input[0]});

            expect(input.parent()).toHasClass("error");
            expect(view.model.get("name")).not.toEqual(name);
            expect(view.action).not.toEqual("save");
        });

        it("should set value to model", function(){
            var value = "asda";
            var input = view.$el.find(".attrValue textarea");
            input.val(value);

            view.respondOnInputValue({target:input[0]});

            expect(input.parent()).not.toHasClass("error");
            expect(view.model.get("value")).toEqual(value);
            expect(view.action).toEqual("save");

        });

        it("should not set not valid value to model and show error", function(){
            view.value = "as";
            var value = "";
            var input = view.$el.find(".attrValue textarea");
            input.val(value);

            view.respondOnInputValue({target:input[0]});

            expect(input.parent()).toHasClass("error");
            expect(view.model.get("value")).not.toEqual(value);
            expect(view.action).not.toEqual("save");
        });

        it("should remove existing models", function(){
            var collection = jasmine.createSpyObj("", ["remove"]);
            view.model.collection = collection;
            view.model.id = "adad";
            view.remove();

            expect(view.$el).not.toBeVisible();
            expect(view.action).toEqual("destroy");
            expect(collection.remove).not.toHaveBeenCalled();
        });

        it("should remove not saved models", function(){
            var collection = jasmine.createSpyObj("", ["remove"]);
            view.model.collection = collection;

            view.remove();

            expect(view.$el).not.toBeVisible();
            expect(view.action).toEqual("nothing");
            expect(collection.remove).toHaveBeenCalled();
        });

    });
});});


