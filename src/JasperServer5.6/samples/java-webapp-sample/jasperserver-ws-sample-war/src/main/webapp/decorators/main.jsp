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

<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title><decorator:title/></title>
    <decorator:head/>
</head>
<body>
    <h1>
        <center>JasperServer Web Services Sample</center>
    </h1>
    <hr>
    <div>
        <center>
            <a href="${pageContext.request.contextPath}/listReports.jsp">Home</a> |
            <a href="${pageContext.request.contextPath}/authority/user">Users</a> |
            <a href="${pageContext.request.contextPath}/authority/role">Roles</a> |
            <a href="${pageContext.request.contextPath}/index.jsp">Log out</a>
        </center>
    </div>
    <br>
    <div>
        <br>
        <center>
            <decorator:body/>
        </center>
    </div>
    <br>
    <%--<hr>--%>
    <%--<a href="index.jsp">Exit</a>--%>
</body>
</html>
