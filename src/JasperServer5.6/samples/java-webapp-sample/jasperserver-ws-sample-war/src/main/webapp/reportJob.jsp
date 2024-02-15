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
<%@page import="com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor" %>
<%@page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    if (session == null) response.sendRedirect(request.getContextPath() + "/index.jsp");
    com.jaspersoft.jasperserver.sample.WSClient client = (com.jaspersoft.jasperserver.sample.WSClient) session.getAttribute("client");
    if (client == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }

    String reportUri = request.getParameter("reportUri");
    ResourceDescriptor reportUnit = client.get(reportUri);
    request.setAttribute("reportUnit", reportUnit);

    String[] outputs = request.getParameterValues("output");
    Map outputsMap = new HashMap();
    if (outputs != null) {
        for (int i = 0; i < outputs.length; ++i) {
            outputsMap.put(outputs[i], Boolean.TRUE);
        }
    }
    pageContext.setAttribute("outputsMap", outputsMap);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>JasperServer Web Services Sample</title>
    <script type="text/javascript">
        function triggerTypeChanged()
        {
            document.jobForm.action = "reportJob.jsp";
            document.jobForm.reload.click();
        }
    </script>
</head>
<body>

<center><h1>JasperServer Web Services Sample</h1></center>
<hr/>
<center>
    <h3>New job</h3>

    <form name="jobForm" method="post" action="reportJobSave.jsp">
        <input type="submit" name="reload" style="visibility:hidden;"/>
        <table border="2" cellpadding="2" cellspacing="2">
            <tr>
                <td align="right">Report *</td>
                <td align="left"><input name="reportUri" value="${param['reportUri']}" readonly="readonly" size="50"/>
                </td>
            </tr>
            <tr>
                <td align="right">Label *</td>
                <td align="left"><input name="label" value="${param['label']}" size="50"/></td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="radio" name="triggerType" value="simple"
                           onclick="triggerTypeChanged()"
                           <c:if test="${empty param['triggerType'] or param['triggerType'] == 'simple'}">checked="checked"</c:if>
                            />
                    Simple recurrence <sup>1</sup>
                    &nbsp;
                    <input type="radio" name="triggerType" value="weekly"
                           onclick="triggerTypeChanged()"
                           <c:if test="${param['triggerType'] == 'weekly'}">checked="checked"</c:if>
                            />
                    Weekly recurrence
                </td>
            </tr>
            <c:if test="${empty param['triggerType'] or param['triggerType'] == 'simple'}">
                <tr>
                    <td align="right">Every *</td>
                    <td align="left">
                        <input name="interval" size="5"/>
                        <select name="intervalUnit">
                            <option value="MINUTE">mins</option>
                            <option value="HOUR">hrs</option>
                            <option value="DAY">days</option>
                            <option value="WEEK">weeks</option>
                        </select>
                    </td>
                </tr>
            </c:if>
            <c:if test="${param['triggerType'] == 'weekly'}">
                <tr>
                    <td align="right">Day</td>
                    <td align="left">
                        <select name="weekDay">
                            <option value="2">Monday</option>
                            <option value="3">Tuesday</option>
                            <option value="4">Wednesday</option>
                            <option value="5">Thursday</option>
                            <option value="6">Friday</option>
                            <option value="7">Saturday</option>
                            <option value="1">Sunday</option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td align="right">Time *</td>
                    <td align="left">
                        <input name="weekDayHour" size="2"/>
                        :
                        <input name="weekDayMinute" size="2"/>
                    </td>
                </tr>
            </c:if>
            <tr>
                <td align="right">Output *</td>
                <td align="left">
                    <input type="checkbox" name="output" value="PDF"
                           <c:if test="${outputsMap['PDF']}">checked="checked"</c:if>
                            />
                    PDF &nbsp;
                    <input type="checkbox" name="output" value="HTML"
                           <c:if test="${outputsMap['HTML']}">checked="checked"</c:if>
                            />
                    HTML &nbsp;
                    <input type="checkbox" name="output" value="XLS"
                           <c:if test="${outputsMap['XLS']}">checked="checked"</c:if>
                            />
                    XLS &nbsp;
                    <input type="checkbox" name="output" value="RTF"
                           <c:if test="${outputsMap['RTF']}">checked="checked"</c:if>
                            />
                    RTF &nbsp;
                    <input type="checkbox" name="output" value="CSV"
                           <c:if test="${outputsMap['CSV']}">checked="checked"</c:if>
                            />
                    CSV &nbsp;
                </td>
            </tr>
            <tr>
                <td align="right">Output filename * <sup>2</sup></td>
                <td align="left"><input name="outputName" value="${param['outputName']}" size="50"/></td>
            </tr>
            <tr>
                <td align="right">Sequential filenames</td>
                <td align="left">
                    <input type="checkbox" name="sequential" value="true"
                           <c:if test="${not empty param['sequential']}">checked="checked"</c:if>
                            />
                </td>
            </tr>
            <tr>
                <td align="right">Mail to</td>
                <td align="left">
                    <input name="mailTo" value="${param['mailTo']}" size="50"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <jsp:include page="reportParameters.jsp"/>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <input type="submit" value="Save"/>
                </td>
            </tr>
            <tr>
                <td align="right">*</td>
                <td align="left"><em>Mandatory</em></td>
            </tr>
            <tr>
                <td align="right"><sup>1</sup></td>
                <td align="left"><em>The job will be scheduled to start immediately</em></td>
            </tr>
            <tr>
                <td align="right"><sup>2</sup></td>
                <td align="left"><em>Saved under /ContentFiles</em></td>
            </tr>
        </table>
    </form>
</center>
<hr/>
<a href="<c:url value="reportSchedule.jsp"><c:param name="reportUri" value="${param['reportUri']}"/></c:url>">Back</a>
</body>
</html>
