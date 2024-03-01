define(function(require, exports, module) {
var __disableStrictMode__ = "use strict";

var tv4 = require('tv4');

var $ = require('jquery');

var Backbone = require('backbone');

var _ = require('underscore');

var logger = require("../../../logging/logger");

var biComponentErrorFactory = require('../../error/biComponentErrorFactory');

require('underscore.string');

function _typeof(obj) { if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") { _typeof = function _typeof(obj) { return typeof obj; }; } else { _typeof = function _typeof(obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; }; } return _typeof(obj); }

var log = logger.register("biComponentUtil");

function checkAlreadyDestroyed(stateModel) {
  if (stateModel && stateModel instanceof Backbone.Model && stateModel.get('_destroyed')) {
    throw biComponentErrorFactory.alreadyDestroyedError();
  }
}

var biComponentUtil = {
  cloneDeep: function cloneDeep(obj) {
    return _.cloneDeep(obj, function (o) {
      if (o instanceof $) {
        return o;
      }

      if (_.isArray(o) && o.parameters) {
        var array = _.cloneDeep(o);

        array.parameters = _.cloneDeep(o.parameters);
        return array;
      }
    });
  },
  createField: function createField(object, propertyName, instanceData, readOnly, stateModel) {
    object[propertyName] = function (value) {
      checkAlreadyDestroyed(stateModel);

      if (!arguments.length) {
        return biComponentUtil.ES5Object(biComponentUtil.cloneDeep(instanceData[propertyName]));
      }

      var func = biComponentUtil.cloneDeep(value);

      if (readOnly && readOnly.length) {
        for (var i = 0; i < readOnly.length; i++) {
          delete func[readOnly[i]];
        }
      }

      instanceData[propertyName] = _.extend({}, instanceData[propertyName], func);

      if (stateModel !== undefined && propertyName == 'properties') {
        stateModel.set(biComponentUtil.cloneDeep(instanceData[propertyName]));
      }

      return object;
    };
  },
  createReadOnlyField: function createReadOnlyField(object, fieldName, instanceData, throwError, stateModel) {
    object[fieldName] = function (value) {
      checkAlreadyDestroyed(stateModel);

      if (!arguments.length) {
        return biComponentUtil.ES5Object(biComponentUtil.cloneDeep(instanceData[fieldName]));
      }

      if (throwError) {
        throw new Error('The field \'' + fieldName + '\' cannot be set at this moment.');
      }

      return object;
    };
  },
  createProperty: function createProperty(object, propertyName, instanceData, stateModel) {
    object[propertyName] = function (value) {
      checkAlreadyDestroyed(stateModel);

      if (!arguments.length) {
        return instanceData.properties[propertyName];
      }

      instanceData.properties[propertyName] = value;

      if (stateModel && stateModel instanceof Backbone.Model) {
        stateModel.set(propertyName, biComponentUtil.cloneDeep(instanceData.properties[propertyName]));
      }

      return object;
    };
  },
  createReadOnlyProperty: function createReadOnlyProperty(object, propertyName, instanceData, throwError, stateModel) {
    object[propertyName] = function (value) {
      checkAlreadyDestroyed(stateModel);

      if (!arguments.length) {
        return instanceData.properties[propertyName];
      }

      if (throwError) {
        throw new Error('The property \'' + propertyName + '\' cannot be set at this moment.');
      }

      return object;
    };
  },
  createValidateAction: function createValidateAction(instanceData, schema, stateModel) {
    return function () {
      checkAlreadyDestroyed(stateModel);
      return biComponentUtil.validateObject(schema, instanceData.properties);
    };
  },
  createInstancePropertiesAndFields: function createInstancePropertiesAndFields(context, instanceData, propertyNames, fieldNames, readOnlyFieldNames, stateModel) {
    function createField(object, propertyName) {
      biComponentUtil.createField(object, propertyName, this, false, stateModel);
      return object;
    }

    function createReadOnlyField(object, propertyName) {
      biComponentUtil.createReadOnlyField(object, propertyName, this, true, stateModel);
      return object;
    }

    function createProperty(object, propertyName) {
      biComponentUtil.createProperty(object, propertyName, this, stateModel);
      return object;
    }

    _.reduce(propertyNames, _.bind(createProperty, instanceData), context);

    _.reduce(fieldNames, _.bind(createField, instanceData), context);

    _.reduce(readOnlyFieldNames, _.bind(createReadOnlyField, instanceData), context);
  },
  createDeferredAction: function createDeferredAction() {
    var actionArguments = arguments;
    return function (successCallback, errorCallback, completeCallback) {
      var dfd = new $.Deferred();
      successCallback && _.isFunction(successCallback) && dfd.done(successCallback);
      errorCallback && _.isFunction(errorCallback) && dfd.fail(errorCallback);
      completeCallback && _.isFunction(completeCallback) && dfd.always(completeCallback);

      try {
        var actionFunc = actionArguments[0],
            stateModel = actionArguments[1],
            args = Array.prototype.slice.call(actionArguments, 2);

        if (stateModel.get('_destroyed')) {
          var err = biComponentErrorFactory.alreadyDestroyedError();
          log.error(err.toString());
          dfd.reject(err);
        } else {
          args.unshift(dfd);
          actionFunc.apply(this, args);
        }
      } catch (ex) {
        var e = biComponentErrorFactory.javaScriptException(ex);
        log.error(e.toString());
        dfd.reject(e);
      }

      return dfd;
    };
  },
  validateObject: function validateObject(schema, obj) {
    var result = tv4.validateResult(obj, schema, false, true);

    if (!result.valid) {
      return convertValidationError(result.error);
    }
  },
  ES5Object: function ES5Object(object) {
    if (_.isArray(object)) {
      return this.ES5Array(object);
    }

    if (_.isObject(object)) {
      _.each(_.keys(object), function (key) {
        biComponentUtil.ES5Object(object[key]);
      });
    }

    return object;
  },
  ES5Array: function ES5Array(array) {
    if (!_.isArray(array)) {
      throw new Error('Must be array!', array);
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
  },
  bindContextToArgument: function bindContextToArgument(context, obj) {
    if (!obj) {
      return obj;
    }

    if (_.isFunction(obj.done) && _.isFunction(obj.fail) && _.isFunction(obj.progress)) {
      var dfd = $.Deferred();
      obj.progress(function () {
        dfd.notifyWith(context, arguments);
      });
      obj.fail(function () {
        dfd.rejectWith(context, arguments);
      });
      obj.done(function () {
        dfd.resolveWith(context, arguments);
      });
      return dfd;
    } else if (_.isString(obj.jquery)) {
      return obj;
    } else if (isElement(obj)) {
      return obj;
    } else if (_.isFunction(obj)) {
      return _.bind(obj, context);
    } else if (_.isArray(obj)) {
      var mapped = _.map(obj, function (element) {
        return biComponentUtil.bindContextToArgument(context, element);
      });

      obj.parameters && (mapped.parameters = _.cloneDeep(obj.parameters));
      return mapped;
    } else if (_.isObject(obj)) {
      return _.reduce(obj, function (memo, element, key) {
        memo[key] = biComponentUtil.bindContextToArgument(context, element);
        return memo;
      }, {});
    }

    return obj;
  }
};
module.exports = biComponentUtil;

function convertValidationError(validationError) {
  var result = validationError;

  if (validationError.subErrors && validationError.subErrors.length) {
    var subErrors = _.filter(validationError.subErrors, function (subError) {
      return subError.code != tv4.errorCodes.ENUM_MISMATCH;
    });

    var nonEnumMismatchError = subErrors.length > 0;

    if (nonEnumMismatchError) {
      result = convertValidationError(subErrors[0]);
    } else {
      var noChildSubErrors = _.all(validationError.subErrors, function (err) {
        return !_.isUndefined(err.subErrors);
      });

      if (noChildSubErrors) {
        result = _.max(validationError.subErrors, function (err) {
          var dataPath = err.dataPath;
          return _.count(dataPath, '/');
        });
      }
    }
  }

  return result;
}

function isElement(o) {
  return (typeof HTMLElement === "undefined" ? "undefined" : _typeof(HTMLElement)) === 'object' ? o instanceof HTMLElement : o && _typeof(o) === 'object' && o !== null && o.nodeType === 1 && typeof o.nodeName === 'string';
}

});