<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!--
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
-->

<jsp:include page="setScriptOptimizationProps.jsp"/>

<!DOCTYPE html>
<html>
<head>
    <c:if test="${optimizeJavascript == true}">
        <script type="text/javascript" src="${scriptsUri}/xdm.remote.js"></script>
    </c:if>
    <c:if test="${optimizeJavascript == false}">
        <script type="text/javascript" src="${scriptsUri}/lib/require-2.1.10.js"></script>
        <script type="text/javascript" src="${scriptsUri}/require.config.js"></script>
    </c:if>

    <c:if test="${param['logEnabled'] == 'true'}">
        <script type="text/javascript">
            require.config({
                config: {
                    logger: {enabled: true}
                }
            });

            <c:if test="${param['logLevel'] != null}">
                require.config({
                    config: {
                        logger: {level: "${param['logLevel']}"}
                    }
                });
            </c:if>
        </script>
    </c:if>

    <script type="text/javascript">
        require(["xdm.remote"], function(remote) {
            window.remote = remote;
        });
    </script>

</head>
<body></body>
</html>