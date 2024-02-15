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
<form name="updateForm" action="role" method="POST">
    <input type="hidden" name="action" value="put"/>
    <input type="hidden" name="oldRoleName" value="${role.roleName}"/>

    <table cellspacing="2" cellpadding="2" border="0">
        <tbody>
        <tr>
            <td>Role Name:</td>
            <td><input type="text" name="roleName" value="${role.roleName}"/></td>
        </tr>
        <c:if test="${not empty users}">
            <tr>
                <td valign="top">Assigned users:</td>
                <td>
                    <table>
                        <c:forEach var="user" items="${users}">
                            <tr>
                                <td>&nbsp;</td>
                                <td>
                                    <input type="checkbox" name="user_${user.user.username}" <c:if test="${user.assigned}">checked</c:if>/> ${user.user.username}
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
