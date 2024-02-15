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
 * @version: $Id: attributes.view.js 47331 2014-07-18 09:13:06Z kklein $
 */

if (!jaspersoft) {
    var jaspersoft = {};
}
jaspersoft.attributes || (jaspersoft.attributes = {});

;(function(jQuery, _, Backbone, attributes, TemplateEngine) {

    /**
     Attributes view
     @module attributes/view
     */

    /**
     * Construtor. Same parameters as Backbone.View and parameter 'quiet'.
     * If this parameter set to 'true', view will not show nothing to display message if collection is empty
     * @class  MainView
     * @classdesc This is a Backbone view, which is a main container for attributes on page
     * @extends Backbone.View
     * @see Attributes
     */
    attributes.MainView = Backbone.View.extend({

        events: {
            "click textarea": "enable",
            "click #newAttribute a": "addModel"
        },

        subViews: [],
        editMode: false,
        validationModel: attributes.Attribute.instance(),
        addAttributeView: null,

        /**
         * Initialization of the view, called by Backbone.
         * @instance
         * @memberof MainView
         */
        initialize: function(options) {
            this.mainTemplateId = "attributesMain";
            this.validationModel.collection = this.collection;

            this.collection.on("add", _.bind(this.addView, this));
			this.initOptions = options;
        },

        /**
         * Rendering of main view, can trigger rendering of items if context is set.
         * @instance
         * @memberof MainView
         * @see MainView#renderItems
         * @see MainView#setContext
         * @return chainable instance
         */
        render: function() {
            this.mainTemplate || (this.mainTemplate = TemplateEngine.createTemplate(this.mainTemplateId));

            this.collection.context && this.collection.fetch({success: _.bind(this.renderItems, this), reset: true});

            this.$el.html(this.mainTemplate());

            this.$subEl = this.$el.find(".items");
            this.$nameInput = this.$el.find("#newAttribute .attrName textarea");
            this.$valueInput = this.$el.find("#newAttribute .attrValue textarea");

            this.addAttributeView = new attributes.AttributeView({el:this.$el.find("#newAttribute"), model:this.validationModel});
            this.addAttributeView.remove = function(){};
            this.addAttributeView.undelegateEvents();
            this.addAttributeView.delegateEvents();
            this.addAttributeView.registerEvents();

            this.toggleList(this.initOptions.quiet);

            return this;
        },

        /**
         * Renders attributes, stored in collection.
         * @instance
         * @memberof MainView
         * @return chainable instance
         */
        renderItems: function(collection) {
            this.toggleList(collection.length);

            for (var i = 0, l = this.subViews.length; i < l; i++) {
                this.subViews[i].undelegateEvents();
            }
            this.subViews.clear();

            this.$subEl.hide().html("");

            var item;
            for (var i = 0, l = collection.length; i < l; i++) {
                // Workaround for 'id' (it is required, but our services does not provide it)
                collection.models[i].id = collection.models[i].get("name");
                item = new attributes.AttributeView({model: collection.models[i]});
                this.$subEl.append(item.render().el);
                this.subViews.push(item);
            }

            this.$subEl.show().find("textarea").each(function(index, element){
                adjustSizeOfTextarea(jQuery(element));
            });

            return this;
        },

        /**
         * Sets context, hides list, which belongs to old context. Context - object, used to build urls.
         * @instance
         * @memberof MainView
         * @return chainable instance
         */
        setContext: function(context) {
            var sameContext = true;
            if (this.collection.context) {
                for (var key in context) {
                    if (Object.hasOwnProperty.call(context, key) && !_.isArray(context[key])) {
                        sameContext = this.collection.context[key] === context[key] && sameContext;
                    }
                }
            } else {
                sameContext = false;
            }
            if (!sameContext) {
                this.collection.context = context;
                this.collection.fetch({success: _.bind(this.renderItems, this), reset: true});
                this.$subEl.hide();
            }

            return this;
        },

        /**
         * Sets edit mode
         * @instance
         * @param edit if true, view will be switched to edit mode, if false - to view mode
         * @memberof MainView
         * @return chainable instance
         */
        setEditMode: function(edit) {
            this.editMode = edit;
            this.toggleList(edit || this.collection.length);

            if (edit) {
                this.$el.addClass("editMode");
            } else {
                this.$el.removeClass("editMode");
                this.disableAll();
            }

            return this;
        },

        /**
         * Toggles list of elements
         * @instance
         * @param show if true, list will be shown, if false - nothing to display element
         * @memberof MainView
         * @return chainable instance
         */
        toggleList: function(show) {
            if (show) {
                this.initOptions.quiet || this.$el.find("#noAttributes").addClass("hidden");
                this.$el.find(".attributesTable").removeClass("hidden");
            } else {
                this.$el.find(".attributesTable").addClass("hidden");
                this.initOptions.quiet || this.$el.find("#noAttributes").removeClass("hidden");
            }

            this.clearAddForm();

            return this;
        },

        /**
         * Determines, if all data is valid and form hasn't validation errors
         * @instance
         * @memberof MainView
         * @return boolean
         */
        isValid: function() {
            return this.collection.isValid() && !this.$subEl.find(".error").length;
        },

        /**
         * Tries to save models one by one, returns true if all models was successfully saved, false otherwise.
         * @instance
         * @memberof MainView
         * @return boolean
         */
        save: function() {
            var res = true;

            for (var i = 0, l = this.subViews.length; i < l; i++) {
                res = this.subViews[i].save() && res;
            }

            return res;
        },

        /**
         * Reloads models from server and redraws items in view.
         * @instance
         * @memberof MainView
         */
        cancel: function() {
            this.collection.fetch({success: _.bind(this.renderItems, this), reset: true});
        },

        /**
         * Resets astate of add attribute form
         * @instance
         * @memberof MainView
         */
        clearAddForm: function(){
            this.$nameInput.val("");
            this.$nameInput.attr("style", null).parent().removeClass("error");
            this.$valueInput.val("");
            this.$valueInput.attr("style", null).parent().removeClass("error");
            this.addAttributeView.name = "";
            this.addAttributeView.value = "";
        },

        /**
         * Event handler, enables input for editing.
         * @instance
         * @memberof MainView
         */
        enable: function(evt) {
            if (this.editMode) {
                jQuery(evt.target).attr("readonly", false);
            }
        },

        /**
         * Disables all previously enabled for editing fields inputs.
         * @instance
         * @memberof MainView
         */
        disableAll: function(){
            this.$subEl.find("textarea").attr("readonly", true);
        },

        /**
         * Event handler, adds model, if provided values are correct
         * @instance
         * @memberof MainView
         */
        addModel: function() {
            var name = this.$nameInput.val();
            for (var i = 0, l = this.subViews.length; i < l; i++) {
                if (this.subViews[i].action === "destroy" && this.subViews[i].model.get("name") === name){
                    this.subViews[i].action = "nothing";
                    this.collection.remove(this.subViews[i].model, {silent:true});
                    break;
                }
            }

            if (setValue(this.$nameInput, this.validationModel, "name") &&
                setValue(this.$valueInput, this.validationModel, "value")) {

                this.collection.add(this.validationModel.attributes);
                this.validationModel.attributes = {};

                this.clearAddForm();
            }
        },

        /**
         * Event handler, if model was added to collection, creates view for it.
         * @instance
         * @param model model for new item view
         * @memberof MainView
         */
        addView: function(model) {
            var view = new attributes.AttributeView({model: model});
            view.action = "save";
            this.subViews.push(view);
            this.$subEl.append(view.render().el);
            view.$el.find("textarea").each(function(index, element){
                adjustSizeOfTextarea(jQuery(element));
            });
        }
    });

    /**
     * Construtor. Same parameters as Backbone.View
     * @class  AttributeView
     * @classdesc This is a Backbone view, intended to render one attribute and handles user interactions with it.
     * @extends Backbone.View
     * @see Attribute
     */
    attributes.AttributeView = Backbone.View.extend({

        tagName: "li",
        className: "leaf",

        action: "nothing",

        events: {
            "click  a": "remove",
            "focus textarea": "adjustSizeOnEnable"
        },

        /**
         * Initialization of the view, called by Backbone.
         * @instance
         * @memberof AttributeView
         */
        initialize: function() {
            this.mainTemplateId = "attributesItem";
            this.value = this.model.get("value");
            this.name = this.model.get("name");
        },

        registerEvents: function(){
            this.$el.find(".attrName textarea").on("input propertychange", _.bind(this.respondOnInputName, this));
            this.$el.find(".attrValue textarea").on("input propertychange", _.bind(this.respondOnInputValue, this));
        },

        /**
         * Renders data, provided in model
         * @instance
         * @memberof AttributeView
         */
        render: function() {
            if (!this.mainTemplate) {
                this.mainTemplate = TemplateEngine.createTemplate(this.mainTemplateId);
            }

            this.$el.html(this.mainTemplate(this.model.attributes));

            this.registerEvents();

            return this;
        },

        /**
         * Performs action, scheduled for it's model.
         * action - name of method, which must be called during collections save.
         * @instance
         * @memberof AttributeView
         */
        save: function() {
            var res = true;

            //forces to use PUT instead of POST
            if (this.model.isNew()){
                this.model.id = this.model.get("name");
            }

            if (this.model[this.action] && this.model[this.action]()) {
                this.action = "nothing";
                //if oparation was renaming, we should set new id
                this.model.id = this.model.get("name");
            } else {
                res = false;
            }

            return res;
        },

        /**
         * Determines, if given input, mapped to given name changed
         * (used for handling IE's onpropertychange event)
         * @param input input for analysis
         * @param param param name for analysis
         * @instance
         * @memberof AttributeView
         */
        hasChanged: function(input, param){
            return this[param] !== input.val();
        },

        /**
         * Sets valuew from input to model's attribute with name of param
         * @param input input for value
         * @param param param name to set
         * @instance
         * @memberof AttributeView
         */
        setParam: function(input, param) {
            this[param] = input.val();
            if (setValue(input, this.model, param)) {
                this.action = "save";
            }
        },

        /**
         * Sets size of texttarea depending on it's content size
         * @param input rextarea to set size
         * @instance
         * @memberof AttributeView
         */
        adjustSizeOfTextarea: function($input){
            $input.css({height:"auto"});
            var lineHeight = +$input.css("lineHeight").replace("px","");
            var height = Math.floor(Math.min($input[0].scrollHeight, $input[0].clientHeight) / lineHeight)*lineHeight;
            $input.css({height:(height)? height : lineHeight +"px"});
            // workaround for IE 7
            if ($input.height() < $input[0].scrollHeight){
                $input.height($input[0].scrollHeight);
            }
        },


        /**
         * Event handler, wrapper of adjustSizeOfTextarea
         * @see AttributeView#adjustSizeOfTextarea
         * @instance
         * @memberof AttributeView
         */
        adjustSizeOnEnable: function(evt){
            adjustSizeOfTextarea(jQuery(evt.target));
        },

        /**
         * Event handler, validates users input (name) and sets it to model if it is valid
         * @instance
         * @memberof MainView
         */
        respondOnInputName: function(evt){
            var input = jQuery(evt.target);
            if (this.hasChanged(input, "name")) {
                this.setParam(input, "name");
                adjustSizeOfTextarea(input);
            }
        },

        /**
         * Event handler, validates users input (value) and sets it to model if it is valid
         * @instance
         * @memberof MainView
         */
        respondOnInputValue: function(evt){
            var input = jQuery(evt.target);
            if (this.hasChanged(input, "value")) {
                this.setParam(input, "value");
                adjustSizeOfTextarea(input);
            }
        },



        /**
         * Schedules it's model to destroy during next save operation, hides view.
         * @instance
         * @memberof AttributeView
         */
        remove: function() {
            if (this.model.id) {
                this.action = "destroy";
            } else {
                this.action = "nothing";
                this.model.collection.remove(this.model);
            }
            this.$el.hide().find(".error").each(function(index, element) {
                jQuery(element).removeClass("error");
            });
        }
    });

    function setValue(inp, model, prop) {
        var input = jQuery(inp);
        input.parent().removeClass("error");
        var val = (prop === "name") ? input.val().strip() : input.val();
        model.once("invalid", function(model, error) {
            input.parent().addClass("error").find("span").html(orgModule.messages[error]);
        });
        return model.set(prop, val, {validate : true});
    }

    function adjustSizeOfTextarea($input){
        $input.css({height:"auto"});
        var lineHeight = +$input.css("lineHeight").replace("px","");
        var height = Math.floor(Math.min($input[0].scrollHeight, $input[0].clientHeight) / lineHeight)*lineHeight;
        $input.css({height:(height)? height : lineHeight +"px"});
        // workaround for IE 7
        if ($input.height() < $input[0].scrollHeight){
            $input.height($input[0].scrollHeight);
        }
    }

})(jQuery, _, Backbone, jaspersoft.attributes, JRS.Controls.TemplateEngine );