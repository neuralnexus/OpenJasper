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
 * @version: $Id: repository.main.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require){
    "use strict";

    var domReady = require("!domReady"),
        jaspersoft = require("namespace"),
        jrsExport = require("export"),
        repositorySearch = require("component.repository.search"),
        jrsConfigs = require("jrs.configs"),
        dynamicList = require("components.list"),
        _ = require("underscore"),
        localContext = require("localContext");

    require("json2");
    require("backbone");
    require("csrf.guard");
    require("components.dependent.dialog");
    require("components.toolbar");
    require("components.tooltip");
    require("tools.infiniteScroll");
    require("mng.common");
    require("export.app");
    require("import.extendedformview");
    require("export.extendedformview");
    require("export.shortformview");

    domReady(function(){
        _.extend(repositorySearch.messages, jrsConfigs.repositorySearch.i18n);
        _.extend(dynamicList.messages, jrsConfigs.dynamicList.i18n);
        _.extend(localContext, jrsConfigs.repositorySearch.localContext);
        _.extend(jaspersoft.i18n, jrsConfigs.Export.i18n);

        jrsExport.App.initialize(jrsConfigs.Export.initParams);

        repositorySearch.initialize(localContext);
    });

});