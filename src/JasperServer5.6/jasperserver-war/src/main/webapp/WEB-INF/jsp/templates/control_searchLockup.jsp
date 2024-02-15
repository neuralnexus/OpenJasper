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

<%--
Overview:
    Tab control to be used to permit switching
Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
        <t:putAttribute name="containerID" value="[OPTIONAL]"/>
        <t:putAttribute name="containerClass" value="[OPTIONAL]"/>
        <t:putAttribute name="inputID" value="[OPTIONAL]"/>
    </t:insertTemplate>

--%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="containerAttr" name="containerAttr" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="inputID" name="inputID" classname="java.lang.String" ignore="true"/>

<%--/WEB-INF/jsp/templates/control_searchLockup.jsp revision A--%>
<c:choose>
<c:when test="${isIPad}">
<span id="${containerID}" class="control searchLockup ${containerClass}" ${containerAttr}>
	<label for="${inputID}" class="offLeft"><spring:message code="button.search" javaScriptEscape="true"/></label>
    <div class="wrap"><input type="text" class="" id="${inputID}" tabIndex="-1"/></div>
    <b class="right"><a class="button searchClear"></a></b>
    <a class="button search up"></a>
</span>
</c:when>
<c:otherwise>
<span id="${containerID}" class="control searchLockup ${containerClass}" ${containerAttr}>
	<label for="${inputID}" class="offLeft"><spring:message code="button.search" javaScriptEscape="true"/></label>
    <div class="wrap"><input type="text" class="" id="${inputID}" tabIndex="-1"/></div>
    <b class="right"><a class="button searchClear"></a></b>
    <a class="button search up"></a>
</span>
</c:otherwise>
</c:choose>

