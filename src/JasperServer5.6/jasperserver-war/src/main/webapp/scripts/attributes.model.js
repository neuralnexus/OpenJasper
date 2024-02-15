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
 * @version: $Id: attributes.model.js 47331 2014-07-18 09:13:06Z kklein $
 */

if (!jaspersoft) {
    var jaspersoft = {};
}
jaspersoft.attributes || (jaspersoft.attributes = {});

;(function(jQuery, _, Backbone, exports, TemplateEngine) {
    /**
     Attributes collection and model
     @module attributes/model
     */

    /**
     * @enum
     * @type number
     */
    exports.settings = {
        MAX_LENGTH_OF_NAME: 255,
        MAX_LENGTH_OF_VALUE: 255
    }


    /**
     * Construtor. Same parameters as Backbone.Model
     * @class  Attribute
     * @classdesc This is a Backbone model, which represents user attribute. Intended to use inside Attributes collection.
     * @extends Backbone.Model
     * @see Attributes
     */
    exports.Attribute = Backbone.Model.extend({

        /**
         * Sets specific params to jQuary.ajax via Backbone
         * @instance
         * @memberof Attribute
         * @return url of representing resource
         */
        sync: function(method, model, options){
            options || (options = {});
            options.async = false;
            return Backbone.sync(method, model, options)
        },

        /**
         * This method returns url, which points in attribute, represented by this Attribute instance
         * @instance
         * @memberof Attribute
         * @return url of representing resource
         */
        url: function() {
            // duplicated on purpose - overrides some strange behaviour of FF
            var safeId = encodeURIComponent(this.id).replace("'","%27");
            safeId = safeId.replace("'","%27");
            return this.collection.url(this.isNew() ? "" : safeId);
        },

        /**
         * This method intended to validate model's parameters. It is called by Backbone.
         * @instance
         * @memberof Attribute
         * @return error code
         */
        validate: function(attrs) {
            if (attrs.name === "" || /^\s*$/.test(attrs.name)) {
                return "attribute.name.empty";
            }

            if (attrs.value === "" || /^\s*$/.test(attrs.value)) {
                return "attribute.value.empty";
            }

            if (attrs.name && attrs.name.length > exports.settings.MAX_LENGTH_OF_NAME) {
                return "attribute.name.too.long";
            }

            if (attrs.value && attrs.value.length > exports.settings.MAX_LENGTH_OF_VALUE) {
                return "attribute.value.too.long";
            }

            if (attrs.name && /[\\\/]/.test(attrs.name)) {
                return "attribute.name.invalid";
            }

            if (this.collection && attrs.name) {
                for (var i = 0, l = this.collection.models.length; i < l; i++) {
                    if (this.cid !== this.collection.models[i].cid && this.collection.models[i].get("name") === attrs.name) {
                        return "attribute.name.already.exist";
                    }
                }
            }

        }
    }, {
        /**
         * Factory method, intended to create new instances of Attribute, allows to configure instance of Attribute in any way.
         * @memberof Attribute
         * @return newly created instance of Attribute
         */
        instance: function(options) {
            var inst = new this();
            return inst;
        }
    });

    /**
     * Construtor. Same parameters as Backbone.Collection. Use of instance() method is preferred
     * @see Attributes.instance
     * @class  Attributes
     * @classdesc This is collection of Attribute
     * @extends Backbone.Collection
     * @see Attribute
     */
    exports.Attributes = Backbone.Collection.extend({
        model: exports.Attribute,



        /**
         * Parses server responce. It is called by Backbone.
         * @instance
         * @memberof Attributes
         * @param data received JavaScript object
         * @return error code
         */
        parse: function(data) {
            return data && data.attribute ? data.attribute : [];
        },

        /**
         * This method returns url of this collection. If modelId param present, this method will return url of model,
         * which belongs to this collection and have given modelId
         * @instance
         * @memberof Attributes
         * @param modelId optional, id of model inside collection
         * @return url of representing collection
         */
        url: function(modelId) {
            return TemplateEngine.renderUrl(this.urlTemplate, _.extend({}, this.context, {modelId: modelId}), true);
        },

        /**
         * Checks, if all model in the collection are valid
         * @instance
         * @memberof Attributes
         * @return boolean value
         */
        isValid: function() {
            for (var i = 0, l = this.models.length; i < l; i++) {
                if (!this.models[i].isValid()) {
                    return false;
                }
            }
            return true;
        }

    }, {

        /**
         * Factory method, intended to create new instances of Attributes, allows to configure instance of Attributes in any way.
         * Valid options - see Parameters section
         * @memberof Attributes
         * @param options.urlTemplate valid Mustache based template of url, which points to used REST-service
         * @return newly created instance of Attributes, configures with provided options
         */
        instance: function(options) {
            var inst = new this();
            inst.urlTemplate = options.urlTemplate;
            return inst;
        }
    });

})(jQuery, _, Backbone, jaspersoft.attributes, JRS.Controls.TemplateEngine);
