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

    String parentUri = request.getParameter("parentUri");
    String deleteAction = request.getParameter("deleteAction");
    if (deleteAction != null && (deleteAction.equalsIgnoreCase("no"))) {
        response.sendRedirect(request.getContextPath() + "/listReports.jsp?uri=" + parentUri);
        return;
    }

    String name = request.getParameter("name");
    String fullUri = request.getParameter("fullUri");

    if (fullUri != null && fullUri.length() > 0) {
        if ((deleteAction != null) && (deleteAction.equalsIgnoreCase("yes"))) {
            try {
                client.delete(fullUri);
            } catch (Exception ex) {
                out.println("<h1>Unable to delete " + fullUri + "</h1>");
                out.println("<pre>");
                out.flush();
                java.io.PrintWriter pw = response.getWriter();
                ex.printStackTrace(pw);
                out.println("</pre>");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/listReports.jsp?uri=" + parentUri);
        }
    } else {
        if (parentUri.equals("/")) fullUri = parentUri + name;
        else fullUri = parentUri + "/" + name;
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

<form action="deleteRepo.jsp?fullUri=<%= fullUri%>&parentUri=<%= parentUri %>" method=POST>
    <center>
        Are you sure you want to delete <%= fullUri %> ?<br>

        <table id="mainTable" border="0" cellpadding="2" cellspacing="2" height="100%" valign="top" align="center">

            <tr>
                <td align="center"><input name="deleteAction" type="submit" value="YES"></td>
                <td align="center"><input name="deleteAction" type="submit" value="NO"></td>
            </tr>
        </table>
        <br>
        <br>
    </center>
</form>

</body>
</html>
