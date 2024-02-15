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

<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@page import="com.jaspersoft.jasperserver.ws.scheduling.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    if (session == null) response.sendRedirect(request.getContextPath() + "/index.jsp");

    ReportScheduler scheduler = (ReportScheduler) session.getAttribute("ReportScheduler");
    if (scheduler == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String[] idParams = request.getParameterValues("jobId");
    if (idParams.length == 1) {
        long id = Long.parseLong(idParams[0]);
        scheduler.deleteJob(id);
    } else {
        long[] ids = new long[idParams.length];
        for (int i = 0; i < idParams.length; ++i) {
            ids[i] = Long.parseLong(idParams[i]);
        }
        scheduler.deleteJobs(ids);
    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JasperServer Web Services Sample</title>
</head>
<body>

<center><h1>JasperServer Web Services Sample</h1></center>
<hr/>
<h3>Job(s)
    <c:forEach items="${paramValues['jobId']}" var="jobId" varStatus="status">
        <c:if test="${status.count > 1}">,</c:if>
        <c:out value="${jobId}"/>
    </c:forEach>
    deleted.</h3>
<hr/>
<a href="<c:url value="reportSchedule.jsp"><c:param name="reportUri" value="${param['reportUri']}"/></c:url>">Back</a>

</body>
</html>
