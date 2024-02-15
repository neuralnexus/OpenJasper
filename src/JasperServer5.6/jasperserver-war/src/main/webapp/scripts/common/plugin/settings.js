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
 * @author: Yaroslav.Kovalchyk
 * @version: $Id: settings.js 48524 2014-08-22 10:18:24Z sergey.prilukin $
 */


define(function (require) {
    "use strict";

    var request = require("common/transport/request"),
        requestSettings = require("common/config/requestSettings"),
        _ = require("underscore"),
        configs = require("jrs.configs"),
        urlRoot = configs.contextPath + "/rest_v2/settings/";

    var settingsPluginFn = function(settingsGroup, callback) {
        var settings = _.extend({}, requestSettings, {
            type: "GET",
            dataType: "json",
            url: urlRoot + settingsGroup
        });

        request(settings).done(function(resp) {
            callback(resp);
        }).fail(function() {
            //TODO: need to log exception to a logger
            callback(null);
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