<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

<%@ page import="
		com.jaspersoft.jasperserver.war.dto.StringOption,
		com.jaspersoft.jasperserver.war.common.UserLocale
		" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<html>
<head>
  <js:xssNonce/>

  <title><spring:message code='jsp.exportAsFlash.pageTitle'/></title>
  <meta name="noMenu" content="true">
  <meta name="pageHeading" content="<spring:message code='jsp.exportAsFlash.pageTitle'/>"/>

<%
  response.setHeader("LoginRequested","true");
  session.removeAttribute("js_uname");
  session.removeAttribute("js_upassword");
%>

</head>

<body id="swfExport">
	<div class="swfWrapper">
		<object class="swfExport" >
			<param name="wmode" value="transparent">
		  	<param name="movie" value="jasperreports-flash-5.0.1.swf"/>
            <param name="FlashVars" value="jrpxml=jrpxml?jrprint=${jasperPrintName}">
		  	<embed src="jasperreports-flash-5.0.1.swf" FlashVars="jrpxml=jrpxml?jrprint=${jasperPrintName}" width="100%" height="100%" wmode="transparent"></embed>
		</object>
	</div>
</body>

</html>
