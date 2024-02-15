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
 * @author yaroslav.kovalchyk
 * @version $Id: Authentication.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";
    var $ = require("jquery"),
        ComponentEngine = require("./ComponentEngine"),
        BiComponent = require("./BiComponent"),
        request = require("common/transport/request"),
        biComponentErrorFactory = require("../error/biComponentErrorFactory"),
        schemaString = require("text!./schema/Authentication.json");

    function getParametersString(properties) {
        if (properties.token) {
            return "ticket=" + properties.token;
        } else {
            return "j_username=" + properties.name + "&j_password=" + properties.password + "&orgId=" + (properties.organization ? properties.organization : "null") + (properties.locale ? "&userLocale=" + properties.locale : "") + (properties.timezone ? "&userTimezone=" + properties.timezone : "");
        }
    }

    var JrsAuthenticationExecutor = {
        login: function(properties, request){
            var dfd = $.Deferred();
            request({
                url: properties.url + "/j_spring_security_check?forceDefaultRedirect=true&" + getParametersString(properties)
            }).done(function (result, a, b) {
                if (b.getResponseHeader("LoginRequested")) {
                    dfd.reject(b);
                } else {
                    dfd.resolve(result);
                }
            }).fail(function (xhr) {
                dfd.reject(xhr);
            });
            return dfd;
        },
        logout: function(properties, request){
            return request({
                url: properties.url + "/logout.html"
            });
        }
    };

    var Authentication = function (properties) {
        var engine = ComponentEngine.newInstance(schemaString, properties), self = this;
        engine.decorateComponent(this, function (instanceData, dfd) {
            var loginFunction;
            if (instanceData.properties && instanceData.properties.loginFn && _.isFunction(instanceData.properties.loginFn)) {
                // Authentication login hook is defined. Let's use it.
                loginFunction = instanceData.properties.loginFn;
            } else {
                loginFunction = JrsAuthenticationExecutor.login;
            }
            loginFunction(instanceData.properties, request).done(function(result){
                instanceData.data = true;
                dfd.resolve(result);
            }).fail(function (xhr) {
                dfd.reject(biComponentErrorFactory.requestError(xhr));
            });
        });
        this.logout = function (callback, errorback, always) {
            var instanceData = engine.instanceData;
            var logoutFunction;
            if (instanceData.properties && instanceData.properties.logoutFn && _.isFunction(instanceData.properties.logoutFn)) {
                // Authentication logout hook is defined. Let's use it.
                logoutFunction = instanceData.properties.logoutFn;
            } else {
                logoutFunction = JrsAuthenticationExecutor.logout;
            }

            return logoutFunction(instanceData.properties, request)
                .done(function (result, a, b) {
                instanceData.data = false;
                if (callback && _.isFunction(callback)) {
                    callback(result);
                }
            }).fail(function (xhr) {
                if (errorback && _.isFunction(errorback)) {
                    errorback(xhr);
                }
            }).always(function () {
                if (always && _.isFunction(always)) {
                    always();
                }
            });
        };
        this.login = function (loginData, callback, errorback, always) {
            self.properties(loginData);
            return self.run(callback, errorback, always);
        };
    };
    Authentication.prototype = new BiComponent();
    return Authentication;
});
