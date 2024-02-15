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
	This template used to enforce that each page must have a title, a body ID and a body class. 

Usage:
	<tiles:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
	    <tiles:putAttribute name="pageTitle" value="[REQUIRED]"/>
	    <tiles:putAttribute name="moduleName" value="[OPTIONAL]"/>
	    <tiles:putAttribute name="bodyID" value="[REQUIRED]"/>
	    <tiles:putAttribute name="pageClass" value="[OPTIONAL]"/>
	    <tiles:putAttribute name="bodyClass" value="[REQUIRED]"/>
	    <tiles:putAttribute name="headerContent" >
			[OPTIONAL]
	    </tiles:putAttribute>
	    <tiles:putAttribute name="bodyContent" >
			[REQUIRED]
	    </tiles:putAttribute>
	</tiles:insertTemplate>
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<!--/WEB-INF/jsp/templates/page.jsp revision A-->
<tiles:useAttribute id="pageTitle" name="pageTitle" classname="java.lang.String" ignore="false"/>
<tiles:useAttribute id="bodyID" name="bodyID" classname="java.lang.String" ignore="false"/><%-- This should be renamed to 'pageID' but would require name change on all calling pages --%>
<tiles:useAttribute id="pageClass" name="pageClass" classname="java.lang.String" ignore="true"/>
<tiles:useAttribute id="bodyClass" name="bodyClass" classname="java.lang.String" ignore="false"/>
<tiles:useAttribute id="moduleName" name="moduleName" classname="java.lang.String" ignore="true"/>

<%
    if (pageTitle.length() == 0) { throw new JSException("Attribute \"pageTitle\" can't be empty."); }
    if (bodyID.length() == 0) { throw new JSException("Attribute \"bodyID\" can't be empty."); }
    if (bodyClass.length() == 0) { throw new JSException("Attribute \"bodyClass\" can't be empty."); }
%>

<html>
<head>
    <title><tiles:getAsString name="pageTitle"/></title>
    <tiles:insertAttribute name="headerContent" ignore="true"/>

    <c:if test="${not empty moduleName}">

        <jsp:include page="../modules/includeRequirejsScripts.jsp"/>
        <%--
            Performance optimization:
            we do not wait until requirejs will load this module,
            instead we load it by ourself (but only if optimization is turned on)
            thus when requirejs will request it it already will be loaded.
        --%>
        <c:if test="${optimizeJavascript == true}">
            <script type="text/javascript" src="${scriptsUri}/${moduleName}.js"></script>
        </c:if>

        <script type="text/javascript">
            require(["<tiles:insertAttribute name="moduleName"/>"]);
        </script>

    </c:if>


</head>
<body id="<tiles:getAsString name="bodyID"/>" class="<tiles:getAsString name="pageClass" ignore="true"/>">

<div id="display" class="body <tiles:getAsString name="bodyClass"/>">
    <a name="maincontent" id="maincontent"></a>
    <tiles:insertAttribute name="bodyContent"/>
</div><!-- /#display -->
</body>
</html>