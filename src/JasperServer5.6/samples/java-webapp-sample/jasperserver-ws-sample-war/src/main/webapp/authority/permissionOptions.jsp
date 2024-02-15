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

<c:set var="permission" value="<%= request.getParameter(\"permission\") %>"/>
<c:set var="inheritedPermission" value="<%= request.getParameter(\"inheritedPermission\") %>"/>
<c:set var="removeInheritedPermission" value="<%= request.getParameter(\"removeInheritedPermission\") %>"/>
<c:set var="name" value="<%= request.getParameter(\"name\") %>"/>

<input type="hidden"
       name="prev_permission_${name}"
       <c:choose>
             <c:when test="${inheritedPermission == 1}">
               value="${removeInheritedPermission == 0 ? 256 : removeInheritedPermission * 512}"
             </c:when>
             <c:otherwise>
               value="${permission}"
             </c:otherwise>
       </c:choose>/>

<select name="permission_${name}">
    <option <c:choose>
               <c:when test="${inheritedPermission == 1}">
                 value="256"
               </c:when>
               <c:otherwise>
                 value="0"
               </c:otherwise>
            </c:choose>
            <c:if test="${(permission == 0) || ((removeInheritedPermission == 0) && (inheritedPermission == 1))}">selected</c:if>>
            No Access
            <c:if test="${(((inheritedPermission == 1) && (removeInheritedPermission == 0)) || ((inheritedPermission == 0) && (removeInheritedPermission == 0)))}">*</c:if>
    </option>

    <option <c:choose>
               <c:when test="${inheritedPermission == 1}">
                 value="512"
               </c:when>
               <c:otherwise>
                 value="1"
               </c:otherwise>
            </c:choose>
            <c:if test="${(permission == 1) || ((removeInheritedPermission == 1) && (inheritedPermission == 1))}">selected</c:if>>
            Administer
            <c:if test="${(removeInheritedPermission == 1) && (permission ne 1)}">*</c:if>
    </option>

    <option <c:choose>
               <c:when test="${inheritedPermission == 1}">
                 value="1024"
               </c:when>
               <c:otherwise>
                 value="2"
               </c:otherwise>
            </c:choose>
            <c:if test="${(permission == 2) || ((removeInheritedPermission == 2) && (inheritedPermission == 1))}">selected</c:if>>
            Read Only
            <c:if test="${(removeInheritedPermission == 2) && (permission ne 2)}">*</c:if>
    </option>

    <option <c:choose>
               <c:when test="${inheritedPermission == 1}">
                 value="9216"
               </c:when>
               <c:otherwise>
                 value="18"
               </c:otherwise>
            </c:choose>
            <c:if test="${(permission == 18) || ((removeInheritedPermission == 18) && (inheritedPermission == 1))}">selected</c:if>>
            Read + Delete
            <c:if test="${(removeInheritedPermission == 18) && (permission ne 18)}">*</c:if>
    </option>

    <option <c:choose>
               <c:when test="${inheritedPermission == 1}">
                 value="15360"
               </c:when>
               <c:otherwise>
                 value="30"
               </c:otherwise>
            </c:choose>
            <c:if test="${(permission == 30) || ((removeInheritedPermission == 30) && (inheritedPermission == 1))}">selected</c:if>>
            Read + Write + Delete
            <c:if test="${(removeInheritedPermission == 30) && (permission ne 30)}">*</c:if>
    </option>
</select>
