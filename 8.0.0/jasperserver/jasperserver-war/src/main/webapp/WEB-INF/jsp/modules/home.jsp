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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<html>
<head>
  <js:xssNonce/>

  <title><spring:message code='jsp.home.title'/></title>
  <meta name="pageHeading" content="HOME PAGE">
</head>

<body>

<form name="fmHome" action="../" method="GET">
<table cellpadding="20" cellspacing="0" width="100%" border="0">
  <tr>
    <td>
      <br/>
      <br/>
      <b><spring:message code='jsp.home.content_title'/></b><spring:message code='jsp.home.summary'/>
      <br>
      <br>
      <b><spring:message code='jsp.home.detail1'/></b> <br>
      <spring:message code='jsp.home.detail2'/><br>
      <spring:message code='jsp.home.detail3'/><br>
      <spring:message code='jsp.home.detail4'/><br>
      <spring:message code='jsp.home.detail5'/><br>
      <spring:message code='jsp.home.detail6'/>
      <input type="hidden" name="topage" value="">
    </td>
  </tr>
</table>
</form>

</body>
</html>
