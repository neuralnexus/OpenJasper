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
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

/**
 * Common configuration of HTTP request,
 * currently use jQuery.ajax settings api for settings object
 */

define(function (require) {

    var configs = require("jrs.configs"),
        locale = configs.userLocale.replace(/_/g, "-");

    return {

        headers: {

            /**
             *  Use custom HTTP header to prevent 401 response from server.
             *  401 response makes browsers to show native login dialog.
             *  We don't want that.
             */
            "X-Suppress-Basic" : "true",

            /**
             * Overwrite locale of browser, use locale of current user
             */

            "Accept-Language": locale

        }

    };
});