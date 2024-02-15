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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head><title>Permissions</title></head>
<body>
<div>
    <c:choose>
        <c:when test="${show eq 'byRole'}">
            <div><b>Assign Permissions by Role</b></div>
            <br/>
            <center>
                by Role | <a href="permissions?show=byUser&resourceUri=${resourceUri}">by User</a>
            </center>
        </c:when>
        <c:when test="${show eq 'byUser'}">
            <div><b>Assign Permissions by User</b></div>
            <br/>
            <center>
                <a href="permissions?show=byRole&resourceUri=${resourceUri}">by Role</a> | by User
            </center>
        </c:when>
    </c:choose>
</div>
<br/>
<div>Permissions for: <b>${resourceUri}</b></div>
<br/>
<form action="permissions?show=${show}&resourceUri=${resourceUri}" method="POST">
<c:choose>
    <c:when test="${show eq 'byUser'}">
        <c:choose>
            <c:when test="${not empty users}">
                <table border="1">
                    <thead>
                    <tr>
                        <td><b>User Name</b></td>
                        <td><b>Permission Level</b></td>
                    </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="userVO" items="${users}">
                            <tr>
                                <td><a href="user?searchName=${userVO.user.username}">${userVO.user.username}</a></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty userVO.permission}">No permission</c:when>
                                        <c:otherwise>
                                            <jsp:include page="permissionOptions.jsp">
                                                <jsp:param name="permission" value="${userVO.permission.permissionMask}"/>
                                                <jsp:param name="inheritedPermission" value="${userVO.permission.inheritedPermissionMask}"/>
                                                <jsp:param name="removeInheritedPermission" value="${userVO.permission.removeInheritedPermissionMask}"/>
                                                <jsp:param name="name" value="${userVO.user.username}"/>
                                            </jsp:include>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                No permissions
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:when test="${show eq 'byRole'}">
        <c:choose>
            <c:when test="${not empty roles}">
                <table border="1">
                    <thead>
                    <tr>
                        <td><b>Role Name</b></td>
                        <td><b>Permission Level</b></td>
                    </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="roleVO" items="${roles}">
                            <tr>
                                <td><a href="role?searchName=${roleVO.role.roleName}">${roleVO.role.roleName}</a></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${empty roleVO.permission}">No permission</c:when>
                                        <c:otherwise>

                                            <jsp:include page="permissionOptions.jsp">
                                                <jsp:param name="permission" value="${roleVO.permission.permissionMask}"/>
                                                <jsp:param name="inheritedPermission" value="${roleVO.permission.inheritedPermissionMask}"/>
                                                <jsp:param name="removeInheritedPermission" value="${roleVO.permission.removeInheritedPermissionMask}"/>
                                                <jsp:param name="name" value="${roleVO.role.roleName}"/>
                                            </jsp:include>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                No permissions
            </c:otherwise>
        </c:choose>
    </c:when>
</c:choose>
    <br/>
    <div>
        <input type="submit" name="ok" value='OK'/>
        <input type="submit" name="cancel" value='Cancel'/>
    </div>
</form>
</body>
</html>