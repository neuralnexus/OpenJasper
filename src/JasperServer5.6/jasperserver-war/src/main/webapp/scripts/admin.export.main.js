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
 * @version: $Id: admin.export.main.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require){
    "use strict";

    var domReady = require("!domReady"),
        logging = require("administer.logging"),
        Administer = require("administer.base"),
        jrsConfigs = require("jrs.configs"),
        jrsExport = require("export"),
        _ = require("underscore"),
        jaspersoft = require("namespace");

    require("controls.logging");
    require("json2");
    require("components.stateview");
    require("components.dialog");
    require("export.extendedformview");
    require("export.shortformview");
    require("export.app");

    domReady(function() {
        Administer.urlContext = jrsConfigs.urlContext;

        _.extend(jaspersoft.i18n, jrsConfigs.Export.i18n);
        _.extend(jrsExport.i18n, jrsConfigs.Export.i18n);

        logging.initialize();

        jrsExport.App.initialize(jrsConfigs.Export.initParams);
    });
});