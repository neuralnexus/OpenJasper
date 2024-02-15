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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    if (session == null) response.sendRedirect(request.getContextPath() + "/index.jsp");

    ReportScheduler scheduler = (ReportScheduler) session.getAttribute("ReportScheduler");
    if (scheduler == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String reportUri = request.getParameter("reportUri");
    pageContext.setAttribute("reportUri", reportUri);

    int lastSep = reportUri.lastIndexOf('/');
    String parentUri = lastSep == 0 ? "/" : reportUri.substring(0, lastSep);
    pageContext.setAttribute("parentUri", parentUri);

    JobSummary[] jobs = scheduler.getReportJobs(reportUri);
    pageContext.setAttribute("jobs", jobs);
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
<h3>List report jobs</h3>
Report: <c:out value="${reportUri}"/><br>
<br/>
<table id="mainTable" border="2" cellpadding="2" cellspacing="2" height="100%" valign="top">
    <tr>
        <td align="center">Id</td>
        <td align="center">Label</td>
        <td align="center">State</td>
        <td align="center">Last ran at</td>
        <td align="center">Next run time</td>
        <td align="center">Operations</td>
    <tr>
        <c:forEach items="${jobs}" var="job">
    <tr>
        <td><c:out value="${job.id}"/></td>
        <td><c:out value="${job.label}"/></td>
        <td><c:out value="${job.state}"/></td>
        <td><fmt:formatDate type="both" value="${job.previousFireTime.time}"/></td>
        <td><fmt:formatDate type="both" value="${job.nextFireTime.time}"/></td>
        <td>
            <a href="<c:url value="reportJobDelete.jsp"><c:param name="reportUri" value="${reportUri}"/><c:param name="jobId" value="${job.id}"/></c:url>">Delete</a>
        </td>
    </tr>
    </c:forEach>
</table>
<br/>
<a href="<c:url value="reportJob.jsp"><c:param name="reportUri" value="${reportUri}"/></c:url>">Schedule new job</a>
<c:if test="${not empty jobs}">
    <br/>

    <form name="deleteJobsForm" method="post" action="reportJobDelete.jsp">
        <input type="hidden" name="reportUri" value="${reportUri}"/>
        <c:forEach items="${jobs}" var="job">
            <input type="hidden" name="jobId" value="${job.id}"/>
        </c:forEach>
        <input type="submit" name="deleteAll" style="visibility:hidden;"/>
    </form>
    <a href="javascript:document.deleteJobsForm.deleteAll.click();">Delete all jobs</a>
</c:if>
<hr/>
<a href="<c:url value="listReports.jsp"><c:param name="uri" value="${parentUri}"/></c:url>">Back to repository</a>
</body>
</html>
