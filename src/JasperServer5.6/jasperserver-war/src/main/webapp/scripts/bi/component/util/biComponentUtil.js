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
 * @author: Zakhar.Tomchenko
 * @version: $Id$
 */

define(function(require, exports, module) {
    "use strict";

    require("underscore.string");

    var tv4 = require("tv4"),
        $ = require("jquery"),
        _ = require("underscore"),
        log = require("logger").register(module),
        biComponentErrorFactory = require("../../error/biComponentErrorFactory");

    var biComponentUtil = {
        createField: function(object, propertyName, instanceData, readOnly) {
            object[propertyName] = function(value) {
                if (value === undefined) {
                    // TODO: do deep clone here
                    return biComponentUtil.ES5Object(_.clone(instanceData[propertyName]));
                }

                // TODO: do deep clone here
                var func = _.clone(value);
                if (readOnly && readOnly.length) {
                    for (var i = 0; i < readOnly.length; i++) {
                        delete func[readOnly[i]];
                    }
                }
                instanceData[propertyName] = _.extend({}, instanceData[propertyName], func);
                return object;
            }
        },

        createProperty: function(object, propertyName, instanceData) {
            object[propertyName] = function(value) {
                if (value === undefined) {
                    return instanceData.properties[propertyName];
                }

                instanceData.properties[propertyName] = value;
                return object;
            }
        },

        createReadOnlyProperty: function(object, propertyName, instanceData, throwError) {
            object[propertyName] = function(value) {
                if (value === undefined) {
                    return instanceData.properties[propertyName];
                }

                if (throwError){
                    throw new Error("The property '" + propertyName + "' cannot be set at this moment.");
                }
                return object;
            }
        },

        createValidateAction: function(instanceData, schema) {
            return function() {
                return biComponentUtil.validateObject(schema, instanceData.properties);
            }
        },

        createInstancePropertiesAndFields: function(context, instanceData, propertyNames, fieldNames) {
            function createField(object, propertyName){
                biComponentUtil.createField(object, propertyName, this);
                return object;
            }

            function createProperty(object, propertyName){
                biComponentUtil.createProperty(object, propertyName, this);
                return object;
            }

            _.reduce(propertyNames, _.bind(createProperty, instanceData), context);
            _.reduce(fieldNames, _.bind(createField, instanceData), context);
        },

        createDeferredAction: function() {
            var actionArguments = arguments;

            return function(successCallback, errorCallback, completeCallback){
                var dfd = new $.Deferred();

                successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
                errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
                completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);

                try {
                    var actionFunc = Array.prototype.slice.call(actionArguments, 0, 1)[0],
                        args = Array.prototype.slice.call(actionArguments, 1);

                    args.unshift(dfd);

                    actionFunc.apply(this, args);
                }
                catch (ex) {
                    var e = biComponentErrorFactory.javaScriptException(ex);

                    log.error(e.toString());
                    dfd.reject(e);
                }

                return dfd;
            }
        },

        validateObject: function(schema, obj) {
            var result = tv4.validateResult(obj, schema, false, true);

            if (!result.valid) {
                return convertValidationError(result.error);
            }
        },

        ES5Object: function(object) {
            if (_.isArray(object)) {
                return this.ES5Array(object);
            }

            if (_.isObject(object)) {
                _.each(_.keys(object), function(key) {
                    biComponentUtil.ES5Object(object[key]);
                });
            }

            return object;
        },

        ES5Array: function(array){
            if (!_.isArray(array)){
                throw new Error("Must be array!", array);
            }

            array.indexOf || (array.indexOf = _.bind(_.indexOf, array, array));
            array.lastIndexOf || (array.lastIndexOf = _.bind(_.lastIndexOf, array, array));
            array.forEach || (array.forEach = _.bind(_.each, array, array));
            array.every || (array.every = _.bind(_.every, array, array));
            array.some || (array.some = _.bind(_.some, array, array));
            array.map || (array.map = _.bind(_.map, array, array));
            array.filter || (array.filter = _.bind(_.filter, array, array));
            array.reduce || (array.reduce = _.bind(_.reduce, array, array));
            array.reduceRight || (array.reduceRight = _.bind(_.reduceRight, array, array));

            return array;
        }
    };

    return biComponentUtil;

    function convertValidationError(validationError){

        var result =  validationError;

        if (validationError.subErrors && validationError.subErrors.length){

            var subErrors = _.filter(validationError.subErrors, function(subError){
                return subError.code != tv4.errorCodes.ENUM_MISMATCH;
            });

            var nonEnumMismatchError =  subErrors.length > 0;

            if (nonEnumMismatchError){
                result =  convertValidationError(subErrors[0]);
            }else{
                //enum mismatch
                //workaround for http://jira.jaspersoft.com/browse/JRS-1668
                var noChildSubErrors = _.all(validationError.subErrors, function (err){
                    return !_.isUndefined(err.subErrors)
                });

                if (noChildSubErrors){
                     result = _.max(validationError.subErrors, function(err){
                           var dataPath = err.dataPath;
                           //find most deep data path
                           return _.count(dataPath, "/");
                     });
                }
            }
        }
        return result;
    }
});