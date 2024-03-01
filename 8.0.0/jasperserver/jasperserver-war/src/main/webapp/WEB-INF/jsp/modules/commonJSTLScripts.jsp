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

<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<%-- JavaScript which is common to all pages and requires JSTL access --%>

<script type="text/javascript">
    <js:xssNonce type="javascript"/>

    //common URL context
    var urlContext = "${pageContext.request.contextPath}";

    <%--default search text --%>
    var defaultSearchText = "<spring:message code='SEARCH_BOX_DEFAULT_TEXT'/>";
    var serverIsNotResponding = "<spring:message code='confirm.slow.server'/>";

    var commonReportGeneratorsMetadata = ${not empty reportGenerators ? reportGenerators : '[]'};
</script>

<%--section for navigation's json action model data - needs its own script tag with id --%>
<script type="text/json" id="navigationActionModel">
    <%=((NavigationActionModelSupport)application.getAttribute("concreteNavigationActionModelSupport")).getClientActionModelDocument("navigation", request)%>
</script>