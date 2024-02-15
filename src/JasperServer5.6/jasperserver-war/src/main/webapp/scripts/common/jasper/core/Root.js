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
 * @author: Igor Nesterenko
 * @version $Id: Root.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var links = require("common/jasper/core/enum/links").links,
        relations = require("common/jasper/core/enum/relations"),
        _ = require("underscore"),
        $ = require("jquery"),
        request = require("common/jasper/core/transport/request"),
        helper = require("common/jasper/core/util/helper"),
        log = require("logger").register("Root");

    function logResults(message, result){
        log.debug(message, result);
        return result;
    }

    function findLink(req) {
        return _.find(links, function(link) {
            return link.rel === req.rel && link.name === req.name;
        });
    }

    function Root(baseUrl, loggerEnabled, logLevel, scripts){
        this.isLoggerEnabled = loggerEnabled;
        this.logLevel = logLevel;

        this.scripts = scripts;

        this.baseUrl = baseUrl ? baseUrl : "";

        this.xdm()
            .then(function(providerUrl){
                request.rpc(providerUrl);
            });
    }

    _.extend(Root.prototype, {

        xdm : function(){

            var dfd = new $.Deferred(),
                link = findLink( {rel :relations.XDM}),
                url;

            if (link){
                url = this.baseUrl + link.href;
            }

            dfd.resolve(url);

            return dfd.promise();
        },

        settings : function(){

            var dfd = new $.Deferred(),
                link = findLink({ rel: relations.SETTINGS}),
                self = this;

            if (link){
                request({
                   url : this.baseUrl + link.href
                }, function(htmlContent){
                    var serverSettings = helper.serverSettings(htmlContent);
                    //switch context from relative to absolute
                    serverSettings.contextPath =  self.baseUrl;
                    //enable Xdm transfer
                    serverSettings.isXdm =  true;

                    if (typeof __jrsConfigs__ !== "undefined") {
                        //Use instance of __jrsConfigs__ from local lexical scope if possible
                        _.extend(__jrsConfigs__, serverSettings);
                    } else {
                        //TODO: refactor jsConfig to build-in in jsp or provide separate service
                        window.__jrsConfigs__ = serverSettings;
                    }

                    dfd.resolve(serverSettings);
                });
            }else{
                dfd.resolve(new Error("Can't get server settings"));
            }

            return dfd.promise();
        },

        requirejs : function(){

            var dfd = new $.Deferred(),
                jrsLink = findLink({ rel : relations.REQUIREJS, name:  "jrs"}),
                jrLink = findLink({ rel: relations.REQUIREJS, name:  "jr"});

            if (links){

                var scripts = this.scripts ? this.scripts : "scripts",
                    jrsHref = jrsLink.href.replace("{scripts}", this.scripts),
                    jrHref = jrLink.href.replace("{scripts}", this.scripts),
                    baseUrlPrefix = this.baseUrl + "/",
                    logJrs = _.partial(logResults, "Script loader configs for JRS: "),
                    logJr = _.partial(logResults, "Script loader configs for JR: "),
                    jrConfigPromise, jrsConfigPromise,
                    self = this;

                jrsConfigPromise = request({
                    url : this.baseUrl + jrsHref,
                    dataType: "text"
                })
                .then(helper.loaderConfig)
                .then(function(result){
                    result.baseUrl = baseUrlPrefix + result.baseUrl;
                    if(result.config && result.config.logger){
                        result.config.logger.enabled = self.isLoggerEnabled;
                        result.config.logger.level = self.logLevel;
                    }
                    return result;
                })
                .then(logJrs);

                jrConfigPromise = request({
                    url: this.baseUrl + jrHref,
                    dataType: "text"
                }).then(helper.loaderConfig).then(logJr);

                $.when(jrsConfigPromise, jrConfigPromise)
                    .then(function(jrsConfigs, jrConfigs) {
                        _.defaults(jrsConfigs.paths, jrConfigs.paths);
                        return jrsConfigs;
                    }, dfd.reject)
                    .then(dfd.resolve);
            }else{
                dfd.reject(new Error("Can't get RequireJS configs"));
            }
            return dfd.promise();
        }
    });

    return Root;
});