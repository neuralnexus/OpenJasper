<%--
  ~ Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased  a commercial license agreement from Jaspersoft,
  ~ the following license terms  apply:
  ~
  ~ This program is free software: you can redistribute it and/or  modify
  ~ it under the terms of the GNU Affero General Public License  as
  ~ published by the Free Software Foundation, either version 3 of  the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero  General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public  License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="../jsp/modules/common/jsEdition.jsp" %>

<meta name = "viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, width=device-width">
<meta name="apple-mobile-web-app-capable" content="yes"/>

<%-- JavaScript and CSS resources that are common to all or most pages, doesn't include decorations-related code --%>

<c:choose>
    <c:when test="${param['nui'] == 1}">
        <%@include file="designerMinimalImports.jsp"%>
    </c:when>
    <c:otherwise>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/themes/reset.css" type="text/css" media="screen">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="theme.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="pages.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="containers.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="buttons.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="lists.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="controls.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="dataDisplays.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="pageSpecific.css"/>" type="text/css" media="screen,print"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="dialogSpecific.css"/>" type="text/css" media="screen,print"/>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="forPrint.css"/>" type="text/css" media="print"/>

        <!--[if IE 7.0]>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_ie7.css"/>" type="text/css" media="screen"/>
        <![endif]-->

        <!--[if IE 8.0]>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_ie8.css"/>" type="text/css" media="screen"/>
        <![endif]-->

        <!--[if IE]>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_ie.css"/>" type="text/css" media="screen"/>
        <![endif]-->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_custom.css"/>" type="text/css" media="screen"/>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/scripts/lib/jquery/theme/redmond/jquery-ui-1.10.4-custom.css" type="text/css" media="screen">
    </c:otherwise>
</c:choose>

<%@ include file="../jsp/modules/common/jrsConfigs.jsp" %>
