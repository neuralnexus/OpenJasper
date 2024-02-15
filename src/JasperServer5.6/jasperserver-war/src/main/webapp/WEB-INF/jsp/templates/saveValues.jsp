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

<%--
Overview:
    Usage:permit user to add a system created object to the repository.

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/saveValues.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>

<!--/WEB-INF/jsp/templates/saveValues.jsp revision A-->
<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog saveValues overlay moveable centered_horz centered_vert ${containerClass}</t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>    
    <t:putAttribute name="containerID" value="saveValues" />
    <t:putAttribute name="containerTitle"><spring:message code="dialog.saveValues.title"/></t:putAttribute>
    <t:putAttribute name="bodyContent">

            <label class="control input text" accesskey="o" for="savedValuesName" title="<spring:message code="dialog.file.name.title" javaScriptEscape="true"/>">
                <span class="wrap"><spring:message code='dialog.saveValues.nameForSavedValues' javaScriptEscape="true"/> (<spring:message code='required.field' javaScriptEscape="true"/>):</span>
                <input class="" id="savedValuesName" type="text" value=""/>
                <span class="message warning">error message here</span>
            </label>

    </t:putAttribute>
        <t:putAttribute name="footerContent">
         <button id="saveAsBtnSave" class="button action primary up"><span class="wrap"><spring:message code="button.save"/><span class="icon"></span></button>
         <button id="saveAsBtnCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
