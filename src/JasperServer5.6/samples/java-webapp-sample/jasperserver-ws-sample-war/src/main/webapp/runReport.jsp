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
<%@page import="com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.*;" %>
<%
    if (session == null) response.sendRedirect(request.getContextPath() + "/index.jsp");
    com.jaspersoft.jasperserver.sample.WSClient client = (com.jaspersoft.jasperserver.sample.WSClient) session.getAttribute("client");

    if (client == null) response.sendRedirect(request.getContextPath() + "/index.jsp");

    String currentUri = request.getParameter("uri");
    if (currentUri == null || currentUri.length() == 0) currentUri = "/";
    if (currentUri.length() > 1 && currentUri.endsWith("/"))
        currentUri = currentUri.substring(0, currentUri.length() - 1);

    ResourceDescriptor reportUnit = null;
    try {
        reportUnit = client.get(currentUri);
    } catch (Exception ex) {
        out.println("<h1>Unable to get " + currentUri + "</h1>");
        out.println("<pre>");
        out.flush();
        java.io.PrintWriter pw = response.getWriter();
        ex.printStackTrace(pw);
        out.println("</pre>");
        //response.sendRedirect(request.getContextPath()+"/index.jsp");
        return;
    }

    request.setAttribute("reportUnit", reportUnit);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JasperServer Web Services Sample</title>
</head>
<body>

<h2><%=reportUnit.getLabel()%>
</h2>

<br>
<br>
</a><br>

<form name="executeForm" id="executeForm" action="executeReport.jsp">
    <input type="hidden" name="uri" value="<%=currentUri%>">

    <jsp:include page="reportParameters.jsp"/>

    <br>
    Export format: <select name="format">
    <option value="html">HTML</option>
    <option value="pdf" selected>PDF</option>
    <option value="xls">XLS</option>
</select>


    <input type="submit" value="Refresh" onclick="changeAction('runReport.jsp');">
    <input type="submit" value="Run the report" onclick="changeAction('executeReport.jsp');">
</form>
<br>
<%
    if (request.getAttribute("hasParameters") != null) {
%>
Attention: some input controls may require a number. The date/time format used with the webservices is a Long (current
time: <%=(new java.util.Date()).getTime()%>).
<%
    }
%>
<script type="text/javascript">
    function changeAction(action) {
        document.getElementById("executeForm").action = action;
    }
</script>
</body>
</html>
