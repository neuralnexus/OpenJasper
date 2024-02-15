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
 * @version: $Id: InputControls.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var  _ = require("underscore"),
        json3 = require("json3"),
        biComponentUtil = require("./util/biComponentUtil"),
        BiComponent = require("./BiComponent"),
        InputControlCollection = require("inputControl/collection/InputControlCollection"),
        biComponentErrorFactory = require("../error/biComponentErrorFactory"),
        schema = json3.parse(require("text!./schema/InputControls.json"));

    var propertyNames = _.keys(schema.properties),
        fieldNames = ['properties', 'data'];

    function run(dfd, instanceData, inputControlCollection) {
        var validationResult = this.validate(),
            self = this;

        if (validationResult) {
            dfd.reject(biComponentErrorFactory.validationError(validationResult));
        } else {
            extendCollectionWithOptions(instanceData, inputControlCollection);

            var method = instanceData.properties.params && _.keys(instanceData.properties.params).length ? "update" : "fetch",
                options = method === "update" ? { params: instanceData.properties.params } : undefined;

            inputControlCollection[method](options)
                .done(function (response) {
                    instanceData.data = inputControlCollection.toJSON();
                    dfd.resolve(self.data());
                })
                .fail(function(xhr) {
                    dfd.reject(biComponentErrorFactory.requestError(xhr));
                });
        }
    }

    function extendCollectionWithOptions(instanceData, inputControlCollection) {
        _.extend(inputControlCollection, {
            resourceUri: instanceData.properties.resource,
            contextPath: instanceData.properties.server
        });
    }

    var InputControls = function(properties) {
        var instanceData = {
            properties: _.extend({}, properties),
            data: []
        };

        biComponentUtil.createInstancePropertiesAndFields(this, instanceData, propertyNames, fieldNames);

        var inputControlCollection = new InputControlCollection();
        extendCollectionWithOptions(instanceData, inputControlCollection);

        _.extend(this, {
            validate: biComponentUtil.createValidateAction(instanceData, schema),
            run: biComponentUtil.createDeferredAction(run, instanceData, inputControlCollection)
        });
    };

    InputControls.prototype = new BiComponent();

    return InputControls;
});