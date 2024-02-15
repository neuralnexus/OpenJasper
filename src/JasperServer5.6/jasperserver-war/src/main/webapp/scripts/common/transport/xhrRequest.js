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
 * @author: Andrew Godovanec
 * @version: $Id: xhrRequest.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {
    "use strict";

    var $ = require("jquery"),
        jrsConfigs = require("jrs.configs"),
        log = require("logger").register("request");

    return function(ajaxParams) {
        return $.ajax(ajaxParams)
            .fail(function(jqXHR, textStatus, errorThrown) {
                if (jqXHR.getResponseHeader("LoginRequested")) {
                    log.warn("Session timed-out. Redirecting to login page.");
                    document.location = jrsConfigs.urlContext;
                } else if (jqXHR.getResponseHeader("adhocException")){
                    log.error(jqXHR.getResponseHeader("adhocException"));
                } else if (jqXHR.status == 500 || (jqXHR.getResponseHeader("JasperServerError") && !jqXHR.getResponseHeader("SuppressError"))) {
                    log.error(jqXHR.responseText);
                }
            });
    };
});