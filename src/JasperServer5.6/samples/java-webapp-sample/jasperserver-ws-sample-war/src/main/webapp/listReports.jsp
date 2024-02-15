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

    String parentUri = "/";
    if (currentUri.length() > 1 && currentUri.lastIndexOf("/") >= 0)
        parentUri = currentUri.substring(0, currentUri.lastIndexOf("/"));

    java.util.List list = null;
    try {
        list = client.list(currentUri);
    } catch (Exception ex) {

        out.println("<h1>Unable to list " + currentUri + "</h1>");
        out.println("<pre>");
        out.flush();
        java.io.PrintWriter pw = response.getWriter();
        ex.printStackTrace(pw);
        out.println("</pre>");
        //response.sendRedirect(request.getContextPath()+"/index.jsp");
        return;
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
<h3>List report</h3>
Current Directory: <%=currentUri%><br>
<br>
<a href="?uri=<%=parentUri%>">[..]</a><br>
<table id="mainTable" border="2" cellpadding="2" cellspacing="2" valign="top">
    <tr>
        <td align="center">NAME</td>
        <td align="center">LABEL</td>
        <td colspan="3" align="center">OPERATIONS</td>
    <tr>
            <%
       for (int i=0; i<list.size(); ++i)
       {
            ResourceDescriptor rd =
                   (ResourceDescriptor) list.get(i);
            String type = rd.getWsType();
            if ( !((type.equals( ResourceDescriptor.TYPE_FOLDER)) ||
                   (type.equals( ResourceDescriptor.TYPE_REPORTUNIT))
                   || type.equals(ResourceDescriptor.TYPE_CONTENT_RESOURCE))) {
                continue;
            }
    %>
    <tr>
        <td>
            <%
                if (type.equals(ResourceDescriptor.TYPE_FOLDER)) {
            %>
            <a href="?uri=<%=rd.getUriString()%>">[<%=rd.getName()%>]</a><br>
            <%
            } else if (type.equals(ResourceDescriptor.TYPE_REPORTUNIT)) {
            %>
            <a href="runReport.jsp?uri=<%=rd.getUriString()%>"><b><%=rd.getName()%>
            </b></a><br>
            <%
            } else if (type.equals(ResourceDescriptor.TYPE_CONTENT_RESOURCE)) {
            %>
            <a href="content<%=rd.getUriString()%>"><b><%=rd.getName()%>
            </b></a><br>
            <%
                }
            %>
        </td>
        <td><%=rd.getLabel()%>
        </td>
        <td><a href="modifyRepo.jsp?uri=<%=currentUri%>&name=<%=rd.getName()%>">Edit</a></td>
        <td><a href="deleteRepo.jsp?parentUri=<%=currentUri%>&name=<%=rd.getName()%>">Delete</a></td>
        <td align="center">
            <%
                if (type.equals(ResourceDescriptor.TYPE_REPORTUNIT)) {
            %>
            <a href="reportSchedule.jsp?reportUri=<%= rd.getUriString() %>">Schedule</a>
            <%
            } else {
            %>
            -
            <%
                }
            %>
        </td>
        <td>
            <a href="authority/permissions?show=byRole&resourceUri=<%= rd.getUriString()%>">Permissions</a>
        </td>
    </tr>

    <%
        }
    %>
</table>
<br>
<a href="addToRepo.jsp?uri=<%=currentUri%>">[Add New Resource ...]</a>
<br>
</body>
</html>
