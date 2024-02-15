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

define(function (require) {
    "use strict";

    var jrsConfigs = require("jrs.configs"),
        request = require("request"),
        momentLocale = jrsConfigs.userLocale.toLowerCase().replace("_", "-");

    return {
        create: function(localesLocation) {
            return {
                load: function (name, req, onLoad, config) {

                    if (config.isBuild) {
                        onLoad();
                        return;
                    }

                    if (momentLocale === "en" || momentLocale === "en-us") {
                        // such locale already loaded by momentJS by default
                        onLoad();
                        return;
                    }

                    var pathToLocale = localesLocation + momentLocale,
                        absolutePathToLocale = req.toUrl(pathToLocale);

                    // check if file with locale is present,
                    // use of request here because require has long 60 seconds timeout
                    request({
                        url: absolutePathToLocale + ".js",
                        type: "HEAD"
                    }).done(function() {
                        req([pathToLocale], function (value) {
                            onLoad(value);
                        });
                    }).fail(function() {
                        onLoad();
                    });
                }
            };
        }
    }
});
