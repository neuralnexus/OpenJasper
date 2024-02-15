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

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<c:set var="optimizeJavascript" value="${jsOptimizationProperties.useOptimizedJavascript}" scope="session"/>
<c:if test="${optimizeJavascript == null}">
    <c:set var="optimizeJavascript" value="${false}" scope="session"/>
</c:if>

<%-- Process HTTP parameters: check for '_opt' parameter --%>
<c:choose>
    <c:when test="${param['_opt'] == 'true'}">
        <c:set var="optimizeJavascript" value="${true}" scope="session"/>
    </c:when>
    <c:when test="${param['_opt'] == 'false'}">
        <c:set var="optimizeJavascript" value="${false}" scope="session"/>
    </c:when>
</c:choose>

<c:set var="optimizedScriptsFolder" value="${jsOptimizationProperties.optimizedJavascriptPath}" scope="request"/>
<c:set var="notOptimizedScriptsFolder" value="scripts" scope="request"/>
<c:set var="optimizedScriptsUri" value="${pageContext.request.contextPath}/${optimizedScriptsFolder}" scope="request"/>
<c:set var="notOptimizedScriptsUri" value="${pageContext.request.contextPath}/${notOptimizedScriptsFolder}" scope="request"/>

<%-- set scriptsUri parameters --%>
<c:if test="${optimizeJavascript == true}">
    <c:set var="scriptsUri" value="${optimizedScriptsUri}" scope="request"/>
    <c:set var="scriptsFolder" value="${optimizedScriptsFolder}" scope="request"/>
</c:if>
<c:if test="${optimizeJavascript == false}">
    <c:set var="scriptsUri" value="${notOptimizedScriptsUri}" scope="request"/>
    <c:set var="scriptsFolder" value="${notOptimizedScriptsFolder}" scope="request"/>
</c:if>

<c:set var="scriptsFolderInternal" value="../../../${scriptsFolder}" scope="request"/>
