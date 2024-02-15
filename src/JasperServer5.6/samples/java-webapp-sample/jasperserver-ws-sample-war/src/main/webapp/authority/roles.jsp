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

<form name="searchForm" action="role" method="GET">
    <input type="text" name="searchName" value="${searchName}" size="30"/>
    <input type="submit" name="search" value="Search" title="Search"/>
</form>
<br>
<br>
<table cellspacing="2" cellpadding="2" border="2">
    <thead>
        <tr>
            <td><b>ROLE NAME</b></td>
            <td><b>EXTERNALLY DEFINED</b></td>
            <td colspan="2"></td>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="role" items="${roles}">
        <tr>
            <td>${role.roleName}</td>
            <td align="center">
                <input type="checkbox" name="enabled" disabled="disabled" ${(role.externallyDefined) ? 'checked="checked"' : ''}/>
            </td>
            <td width="50">
                <center><a href="#" onclick="editRole('${role.roleName}');">Edit</a></center>
            </td>
            <td width="50">
                <center><a href="#" onclick="deleteRole('${role.roleName}');">Delete</a></center>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<a href="#" onclick="editRole('');">[ Add Role ]</a>

<form id="deleteForm" name="deleteForm" action="role" method="POST">
    <input type="hidden" name="action" id="action" value="" />
    <input type="hidden" name="roleName" id="roleName" value="" />
</form>

<script type="text/javascript">
    function deleteRole(roleName) {
        document.getElementById('action').value = 'delete';
        document.getElementById('roleName').value = roleName;
        document.getElementById('deleteForm').submit();
    }
    function editRole(roleName) {
        document.getElementById('action').value = 'edit';
        document.getElementById('roleName').value = roleName;
        document.getElementById('deleteForm').submit();
    }
</script>