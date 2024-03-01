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
 * @author: Yaroslav.Kovalchyk
 * @version: $Id$
 */


define(function (require) {
    "use strict";

    var request = require("request"),
        requestSettings = require("requestSettings"),
        _ = require("underscore"),
        configs = require("runtime_dependencies/js-sdk/src/jrs.configs"),
        urlRoot = configs.contextPath + "/rest_v2/settings/";

    var settingsPluginFn = function(settingsGroup, callback) {
        var settings = _.extend({}, requestSettings, {
            type: "GET",
            dataType: "json",
            url: urlRoot + settingsGroup
        });

        // Changing default Cache Control directive to have possibility to choose caching strategy on the server
        // Default value was 'no-cache'
        settings.headers["Cache-Control"] = "private";
        delete settings.headers["Pragma"];

        request(settings).fail(function(){
            // settings group not found. Return null and let module solve the issue ;)
            callback(null);
        }).then(function(resp) {
            callback(resp);
        });
    };

    settingsPluginFn.load = function (name, req, onLoad, config) {

        if (config.isBuild) {
            onLoad();
            return;
        }

        settingsPluginFn(name, onLoad);
    };

    return settingsPluginFn;
});