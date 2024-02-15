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

<form name="searchForm" action="user" method="GET">
    <input type="text" name="searchName" value="${searchName}" size="30"/>
    <input type="submit" name="search" value="Search" title="Search"/>
</form>
<br>
<br>
<table cellspacing="2" cellpadding="2" border="2">
    <thead>
        <tr>
            <td><b>USERNAME</b></td>
            <td><b>FULL NAME</b></td>
            <td><b>EMAIL</b></td>
            <td><b>ENABLED</b></td>
            <td><b>ROLES</b></td>
            <td colspan="2"></td>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="user" items="${users}">
        <tr>
            <td>${user.username}</td>
            <td>${user.fullName}</td>
            <td><i>${user.emailAddress}</i></td>
            <td>
                <input type="checkbox" name="enabled" disabled="disabled" ${(user.enabled) ? 'checked="checked"' : ''}/>
            </td>
            <td>
                <c:set var="first" value="true"/>
                <c:forEach var="role" items="${user.roles}"><c:if test="${first != 'true'}">,&nbsp;</c:if><a href="role?searchName=${role.roleName}">${role.roleName}</a><c:set var="first" value="false"/></c:forEach>
                &nbsp;
            </td>
            <td width="50">
                <center><a href="#" onclick="editUser('${user.username}');">Edit</a></center>
            </td>
            <td width="50">
                <center><a href="#" onclick="deleteUser('${user.username}');">Delete</a></center>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<a href="#" onclick="editUser('');">[ Add User ]</a>

<form id="deleteForm" name="deleteForm" action="user" method="POST">
    <input type="hidden" name="action" id="action" value="" />
    <input type="hidden" name="username" id="username" value="" />
</form>

<script type="text/javascript">
    function deleteUser(username) {
        document.getElementById('action').value = 'delete';
        document.getElementById('username').value = username;
        document.getElementById('deleteForm').submit();
    }
    function editUser(username) {
        document.getElementById('action').value = 'edit';
        document.getElementById('username').value = username;
        document.getElementById('deleteForm').submit();
    }
</script>