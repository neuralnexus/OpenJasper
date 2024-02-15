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
 * @author: Zakhar Tomchenko
 * @version: $Id$
 */

define(function (require) {
    "use strict";

    var  _ = require("underscore"),
         $ = require("jquery"),

        BiComponent = require("./BiComponent"),
        biComponentUtil = require("./util/biComponentUtil"),
        biComponentErrorFactory = require("../error/biComponentErrorFactory"),

        request = require("common/transport/request"),
        json3 = require("json3"),

        schema = json3.parse(require("text!./schema/ResourcesSearch.json")),

        propertyNames = _.keys(schema.properties),
        fieldNames = ['properties', 'data'],

        propertyOverride = (function(){
            var override = _.reduce(propertyNames, function(memo, name){
                memo[name] = name;
                return memo;
            }, {});

            override.server = false;
            override.types = "type";

            return override;
        })(),

    ResourcesSearch = function(properties) {
        var instanceData = {
            properties: _.extend({}, properties),
            data: []
        };

        biComponentUtil.createInstancePropertiesAndFields(this, instanceData, propertyNames, fieldNames);

        _.extend(this, {
            validate: biComponentUtil.createValidateAction(instanceData, schema),
            run: biComponentUtil.createDeferredAction(run, instanceData)
        });
    };

    ResourcesSearch.prototype = new BiComponent();

    _.extend(ResourcesSearch, {
        types: [
            "folder", "dataType", "jdbcDataSource", "awsDataSource", "jndiJdbcDataSource", "virtualDataSource",
            "customDataSource", "beanDataSource", "xmlaConnection", "listOfValues", "file", "reportOptions",
            "dashboard", "adhocDataView", "query", "olapUnit", "reportUnit", "domainTopic", "semanticLayerDataSource",
            "secureMondrianConnection", "mondrianXmlaDefinition", "mondrianConnection", "inputControl"
        ],
        sortBy: ["uri", "label", "description", "type", "creationDate", "updateDate", "accessTime", "popularity"],
        accessTypes: ["viewed", "modified"]
    });

    return ResourcesSearch;

    function run(dfd, instanceData){
        var validationResult = this.validate(),
            self = this;

        if (validationResult){
            dfd.reject(biComponentErrorFactory.validationError(validationResult));
        } else {
            request({
                dataType: "json",
                url: constructUrl(instanceData)
            }).done(function (result, a, b) {
                instanceData.data = result ? result.resourceLookup : [];
                dfd.resolve(self.data());
            }).fail(function (xhr) {
                dfd.reject(biComponentErrorFactory.requestError(xhr));
            });
        }
    }

    function constructUrl(instanceData){
         var server = instanceData.properties.server,
             serverUrl = server + (server.charAt(server.length - 1) === '/' ? "rest_v2/resources?" : "/rest_v2/resources?");

        return serverUrl + _(instanceData.properties).keys().reduce(function(memo, property) {
            var overridenProperty = propertyOverride[property];
            if (overridenProperty) {
                if (_.isArray(instanceData.properties[property])) {
                    memo = memo.concat(_.reduce(instanceData.properties[property], function(m2, pValue) {
                        m2.push(overridenProperty + '=' + pValue);
                        return m2;
                    }, []));
                } else {
                    memo.push(overridenProperty + '=' + instanceData.properties[property]);
                }
            }

            return memo;
        }, []).join('&');
    }
});