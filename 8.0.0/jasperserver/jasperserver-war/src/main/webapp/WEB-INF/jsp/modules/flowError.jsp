<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased a commercial license agreement from Jaspersoft,
  ~ the following license terms apply:
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ page isErrorPage="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<html>

<head>
    <js:xssNonce/>

    <title><spring:message code='jsp.flowError.title'/></title>
  <LINK href="/stylesheets/styles.css" type="text/css" rel="stylesheet">
  <LINK href="/stylesheets/base.css" type="text/css" rel="stylesheet">
  <meta name="pageHeading" content='<spring:message code="jsp.flowError.pageHeading"/>'/>
  <meta name="noMenu" content="true">
</head>

<body>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<form name="fmErrorPage">
<table width="100%" cellpadding="0" cellspacing="0" border="0">
<c:choose>
    <c:when test="${exception!=null}">
		<tr><td>${exception}</td></tr>
    </c:when>
	<c:when test="${flowScope.wrapper.errMsg!=null}">
		<tr><td>${flowScope.wrapper.errMsg}</td></tr>
    </c:when>
    <c:otherwise>
		<tr><td align="center" class="ferror"><spring:message code='jsp.flowError.errorMsg1'/></td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td>&nbsp;</td></tr>
		<tr><td width="100%" align="center"><a href="<c:url value="/"/>"/><spring:message code='jsp.flowError.errorMsg2'/></a></td></tr>
    </c:otherwise>
</c:choose>
</table>
</form>

</body>

</html>


