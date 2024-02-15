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

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<form name="updateForm" action="user" method="POST">
    <input type="hidden" name="action" value="put"/>

    <table cellspacing="2" cellpadding="2" border="0">
        <tbody>
        <tr>
            <td>User Name:</td>
            <td><input type="text" name="username" value="${user.username}" <c:if test="${not empty user.username}">readonly</c:if>/></td>
        </tr>
        <tr>
            <td>Password:</td>
            <td><input type="password" name="password" value="${user.password}"/></td>
        </tr>
        <tr>
            <td>Full Name:</td>
            <td><input type="text" name="fullName" value="${user.fullName}"/></td>
        </tr>
        <tr>
            <td>Email Address:</td>
            <td><input type="text" name="emailAddress" value="${user.emailAddress}"/></td>
        </tr>
        <tr>
            <td>Enabled:</td>
            <td><input type="checkbox" name="enabled" ${(user.enabled) ? 'checked="checked"' : ''}/></td>
        </tr>
        <c:if test="${not empty roles}">
            <tr>
                <td valign="top">Assigned roles:</td>
                <td>
                    <table>
                        <c:forEach var="role" items="${roles}">
                            <tr>
                                <td>&nbsp;</td>
                                <td>
                                    <input type="checkbox" name="role_${role.role.roleName}" <c:if test="${role.assigned}">checked</c:if>/> ${role.role.roleName}
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </td>
            </tr>
        </c:if>
        <tr>
            <td colspan="2">
                <center><input type="submit" name="save" value="Save" title="save"/></center>
            </td>
        </tr>
        </tbody>
    </table>
</form>
