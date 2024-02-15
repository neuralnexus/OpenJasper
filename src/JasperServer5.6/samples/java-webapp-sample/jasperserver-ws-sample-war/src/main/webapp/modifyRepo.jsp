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
    String modifyAction = request.getParameter("modifyAction");
    if (modifyAction != null && (modifyAction.equalsIgnoreCase("cancel"))) {
        response.sendRedirect(request.getContextPath() + "/listReports.jsp?uri=" + currentUri);
        return;
    }

    String name = request.getParameter("name");
    String label;
    String desc;
    String type = request.getParameter("type");
    
    
    // Strip out the final slash.
   if (currentUri.endsWith("/"))
   {
   	 currentUri = currentUri.substring(0, currentUri.length() -1);
   }
    
    String fullUri = currentUri + "/" + name;
    
    
    if (type == null) {
        //not initialized yet
        ResourceDescriptor rd = null;
        try {
            rd = client.get(fullUri);
            label = rd.getLabel();
            desc = rd.getDescription();
            type = rd.getWsType();
        } catch (Exception ex) {
            out.println("<h1>Unable to get " + fullUri + "</h1>");
            out.println("<pre>");
            out.flush();
            java.io.PrintWriter pw = response.getWriter();
            ex.printStackTrace(pw);
            out.println("</pre>");
            return;
        }
    } else {
        label = request.getParameter("label");
        desc = request.getParameter("description");


        if ((name != null && name.length() > 0) && (label != null && label.length() > 0) &&
                (desc != null && desc.length() > 0)) {
            ResourceDescriptor rd = null;
            try {
                rd = client.update(name, label, desc, currentUri);
            } catch (Exception ex) {
                out.println("<h1>Unable to update " + fullUri + "</h1>");
                out.println("<pre>");
                out.flush();
                java.io.PrintWriter pw = response.getWriter();
                ex.printStackTrace(pw);
                out.println("</pre>");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/listReports.jsp?uri=" + currentUri);
        }
    }

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JSP Page</title>
</head>
<body>

<form action="modifyRepo.jsp?uri=<%= currentUri %>&name=<%= name %>&type=<%= type %>" method=POST>
    <center>
        Please modify the properties:<br><br>

        <table id="mainTable" border="2" cellpadding="2" cellspacing="2" height="100%" valign="top" align="center">

            <tr>
                <td>Type:</td>
                <td><%= type %>
                </td>
            </tr>
            <tr>
                <td>Parent Folder:</td>
                <td><%= (currentUri.length() == 0) ? "/" : currentUri %>
                </td>
            </tr>
            <tr>
                <td>Name:</td>
                <td><%= name %>
                </td>
            </tr>
            <tr>
                <td>Label*:</td>
                <td><input type="text" name="label" <% if (label!= null) { %> value="<%= label %>" <% }%>></td>
            </tr>
            <tr>
                <td>Description*:</td>
                <td><input type="text" name="description" <% if (desc!= null) { %> value="<%= desc %>" <% }%> ></td>
            </tr>
            <tr></tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" name="modifyAction" value="Submit">
                    <input type="submit" name="modifyAction" value="Cancel">
                </td>
            </tr>
        </table>
        * You need to enter these values
        <br>
        <br>
    </center>
</form>

</body>
</html>
