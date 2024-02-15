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
  --%><%--

    IMPORTANT: Do not remove the comments between the tags, they need to prevent unnecessary spaces in the output.

--%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><%--
--%><%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %><%--

--%><c:choose><%--
	--%><c:when test="${control.inputControl.mandatory
			and (control.inputControl.type == 2
				or control.inputControl.type == 3
				or control.inputControl.type == 4
				or control.inputControl.type == 6
				or control.inputControl.type == 7
				or control.inputControl.type == 8
				or control.inputControl.type == 9
				or control.inputControl.type == 10
				or control.inputControl.type == 11
				)}"><%--
        --%><spring:message code="jsp.defaultParametersForm.star.input.label" argumentSeparator="\\\\" arguments="${controlLabel}"/><%--
    --%></c:when><%--
	--%><c:otherwise><%--
		--%><c:out value="${controlLabel}"/><%--
	--%></c:otherwise><%--
--%></c:choose>