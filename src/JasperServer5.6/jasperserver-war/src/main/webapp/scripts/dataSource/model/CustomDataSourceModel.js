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

define(function(require) {
    "use strict";

    var BaseDataSourceModel = require("dataSource/model/BaseDataSourceModel"),
        _ = require("underscore"),
        $ = require("jquery"),
        jrsConfigs = require("jrs.configs"),
        requestSettings = require("common/config/requestSettings"),
		connectionTypes = require("dataSource/enum/connectionTypes"),
        repositoryResourceTypes = require("common/enum/repositoryResourceTypes"),
        dialogs = require("components.dialogs"),
		i18n = require("bundle!all"),
        jasperserverConfig = require("bundle!jasperserver_config");

    return BaseDataSourceModel.extend({
        type: repositoryResourceTypes.CUSTOM_DATA_SOURCE,

		constructor: function(attributes, options) {

			this.defaults = _.extend({}, this.defaults, {
				dataSourceName: options.dataSourceType,
				connectionType: connectionTypes.CUSTOM
			});

			BaseDataSourceModel.prototype.constructor.apply(this, arguments);
		},

		initialize: function(attributes, options) {
			var result = BaseDataSourceModel.prototype.initialize.apply(this, arguments);

			// define attributes which specific for custom model (we will fetch their values from the server)
			this.customFields = [];
			this.testable = false;
			this.queryTypes = null;
            this.initialization = $.Deferred();
            this.getCustomFieldsDefinition();
            if(!this.isNew()){
                // editing mode
                this.set(this.parseProperties(this.get("properties")), options);
                this.set("password", jasperserverConfig["input.password.substitution"]);
            }
			return result;
		},

        getCustomFieldsDefinition: function() {
            var headers = {},
                self = this;

            _.extend(headers, requestSettings, { "Accept": "application/json" });

            return $.ajax({
                type: "GET",
                headers: headers,
                url: jrsConfigs.contextPath + "/rest_v2/customDataSources/" + this.get("dataSourceName")
            }).done(function(response) {
                if (response && response.propertyDefinitions && _.isArray(response.propertyDefinitions)) {

                    self.testable = !!response.testable;
                    self.queryTypes = response.queryTypes ? response.queryTypes : null;

                    _.each(response.propertyDefinitions, function(definition) {
                        var validationRule = {};

                        if (definition.properties) {
                            definition.properties = _(definition.properties).reduce(function(memo, property) {
                                memo[property.key] = property.value;
                                return memo;
                            }, {});
                        }

                        self.customFields.push(definition);
                        self.defaults[definition.name] = definition.defaultValue;
                        if (!self.options.isEditMode) {
                            self.set(definition.name, definition.defaultValue);
                        }

                        // now, extend the validation object with required fields
                        if (definition.properties && definition.properties.mandatory) {
                            validationRule[definition.name] = {
                                required: true,
                                msg: i18n[self.get("dataSourceName") + "." + definition.name + ".required"] || i18n["required.field.specify.value"]
                            };
                            _.extend(self.validation, validationRule);
                        }
                    });
                }
                self.initialization.resolve();
            }).fail(function(xhr) {
                    var response = false, msg = "Failed to load custom data source definition. ";
                    try {
                        response = JSON.parse(xhr.responseText);
                    } catch (e) {
                    }
                    if (response) {
                        if (response[0] && response[0].errorCode) {
                            msg += "<br/>The reason is: " + response[0].errorCode;
                        } else if (response.message) {
                            msg += "<br/>The reason is: " + response.message;
                        }
                        msg += "<br/><br/>The full response from the server is: " + xhr.responseText;
                    }
                    dialogs.errorPopup.show(msg);
                });
        },

        parse: function(response) {
            var result = BaseDataSourceModel.prototype.parse.apply(this, arguments);

			result = _.extend(result, this.parseProperties(response.properties));

            return result;
        },

        parseProperties: function(properties) {
            var result = {};

            if (!_.isEmpty(properties)) {
                _.each(properties, function(element) {
                    result[element.key] = "password" === element.key ? jasperserverConfig["input.password.substitution"] : element.value;
                })
            }

            return result;
        },

        toJSON: function() {
            var result = BaseDataSourceModel.prototype.toJSON.apply(this, arguments);

            if (!_.isEmpty(this.customFields)) {
                result.properties = [];

                _.each(this.customFields, function(field){
                    var value = result[field.name];
                    var isPassword = "password" === field.name;
                    if(!isPassword || (isPassword && value !== jasperserverConfig["input.password.substitution"])){
                        result.properties.push({key: field.name, value: value});
                    }
                });
            }

            return result;
        }
    });
});