<%@ page language="java" contentType="application/javascript" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
* @author: Igor Nesterenko, Sergey Prilukin
* @version $Id: visualize.jsp 47331 2014-07-18 09:13:06Z kklein $
*/

<jsp:include page="setScriptOptimizationProps.jsp"/>

<c:if test="${optimizeJavascript == false}">
    <%-- Workaround to fix jquery.ui.datepicker. Set global __jrsConfigs__ property --%>
    var __jrsConfigs__ = {
        userLocale: "${userLocale}",
        avaliableLocales: ["de", "en", "es", "fr", "it", "ja", "ro", "zh_TW", "zh_CN"]
    };

    <jsp:include page="${scriptsFolderInternal}/client/jasper.js" />
</c:if>

<jsp:include page="${scriptsFolderInternal}/client/visualize.js" />

<c:if test="${optimizeJavascript == true}">
    <%-- Workaround to fix jquery.ui.datepicker in case if optimization is enabled. In this case __jrsConfigs__ will not be global --%>
    visualize.__jrsConfigs__["userLocale"] = "${userLocale}";
    visualize.__jrsConfigs__["avaliableLocales"] = ["de", "en", "es", "fr", "it", "ja", "ro", "zh_TW", "zh_CN"];

    if (typeof define === "function" && define.amd) {
        define([], function () {
            return visualize;
        });
    }
</c:if>

visualize.config({
    server : "${baseUrl}",
    scripts : "${scriptsFolder}",
    logEnabled: ${logEnabled},
    logLevel: "${logLevel}"
});
