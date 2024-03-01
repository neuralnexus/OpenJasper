<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased a commercial license agreement from Jaspersoft,
  ~ the following license terms apply:
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%--
Overview:
    Usage:

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/addRole.jsp">
    </t:insertTemplate>

--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="tx" uri="http://tiles.apache.org/tags-tiles-extras"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<tx:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<tx:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay addRole moveable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="addRole" />
    <t:putAttribute name="containerTitle">
        <spring:message code="jsp.roleManager.roleCreator.title" javaScriptEscape="true"/>
    </t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
            <fieldset class="group first">
                <legend class="offLeft"><span>Name</span></legend>
                <label class="control input text" for="addRoleName" title="<spring:message code="jsp.roleManager.roleCreator.roleName.title" javaScriptEscape="true"/>">
                    <span class="wrap"><spring:message code="jsp.roleManager.roleCreator.roleName" javaScriptEscape="true"/> (<spring:message code='required.field'/>):</span>
                    <input class="" id="addRoleName" type="text" maxlength="100" value=""/>
                    <span class="hint"><spring:message code="jsp.roleManager.roleCreator.roleName.hint" javaScriptEscape="true"/></span>
                    <span class="message warning">error message here</span>
                </label>
			</fieldset>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
             <!--NOTE: label of #addRoleBtn is dynamic with [Organization Name] token replaced with name of org to which role will be added -->
            <button id="addRoleBtn" class="button action primary up"><span class="wrap">Add Role to [Organization Name]</span><span class="icon"></span></button>
            <button id="cancelAddRoleBtn" class="button action up"><span class="wrap"><spring:message code="dialog.file.cancel" javaScriptEscape="true"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
