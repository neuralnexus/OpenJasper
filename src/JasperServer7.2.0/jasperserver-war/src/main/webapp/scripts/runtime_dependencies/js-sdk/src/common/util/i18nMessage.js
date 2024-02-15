/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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


/**
 * @author: Sergii Kylypko, Kostiantyn Tsaregradskyi
 * @version: $Id$
 */

define(function(require) {
    "use strict";

    var _ = require("underscore"),
        classUtil = require("common/util/classUtil");

    var ARGS_TYPE = {
        INDEX: "index",
        KEY_VALUE: "keyValue"
    };

    var DEFAILT_KEY_VALUE_SEPARATOR = ", ";

    /*
     * Class to wrap i18n message.
     *
     * @param code Message bundle code
     * @param arguments Additional arguments for message
     */
    var i18nMessage = classUtil.extend({
        constructor: function (code){
            this.code = code;
            this.args = Array.prototype.slice.call(arguments, 1);
        },

        bundle: {}
    });

    /*
     * Override toString method.
     *
     * @return string If code was not found in bundle, just raw code is returned. If code was found in bundle,
     *     bundle from message is returned, optionally parts of message will be replaced with arguments.
     */

    i18nMessage.prototype.toString = function() {
        var text = this.bundle[this.code];

        if (!text) {
            return this.code;
        }

        var argsType = this._getArgsType(this.args);

        if (argsType === ARGS_TYPE.INDEX) {
            text = this._interpolateIndexArgs(this.args, text);
        } else if (argsType === ARGS_TYPE.KEY_VALUE) {
            text = this._interpolateObjectArgs(this.args, text);
        }

        return text;
    };

    i18nMessage.prototype._getArgsType = function(args) {
        if (args && args.length) {
            var firstArg = _.first(args);

            return _.isObject(firstArg) ? ARGS_TYPE.KEY_VALUE : ARGS_TYPE.INDEX
        }
    };

    i18nMessage.prototype._interpolateObjectArgs = function(args, text) {
        var separator = args[0].separator || DEFAILT_KEY_VALUE_SEPARATOR;

        var formattedArgs = args.reduce(function(memo, argument) {
            var key = argument.key,
                value = argument.value;

            //add separator if necessary
            if (memo[key]) {
                memo[key] = memo[key] + separator;
            } else {
                memo[key] = "";
            }

            memo[key] = memo[key] + value;

            return memo;
        }, {});

        text = _.keys(formattedArgs).reduce(function(memo, key) {
            var value = formattedArgs[key],
                regexp = "\\{" + key + "\\}";

            return memo.replace(new RegExp(regexp, "g"), value);
        }, text);

        return text;
    };

    i18nMessage.prototype._interpolateIndexArgs = function(args, text) {

        for (var i = 0, l = args.length; i < l; i++) {
            var parameter = args[i],
                regexp = "\\{" + i + "\\}";

            text = text.replace(new RegExp(regexp, "g"), parameter);
        }

        return text;
    };

    //Static i18n message factory
    i18nMessage.create = function(bundle) {
        return function(code) {
            var message = Object.create(i18nMessage.prototype);

            message = _.extend(message, {
                bundle: bundle,
                code: code,
                args: Array.prototype.slice.call(arguments, 1)
            });

            return message.toString(message);
        }
    };

    return i18nMessage;
});