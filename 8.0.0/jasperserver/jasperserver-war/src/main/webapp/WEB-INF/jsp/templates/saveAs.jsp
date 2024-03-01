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
    Usage:permit user to add a system created object to the repository.

Usage:

    <t:insertTemplate template="/WEB-INF/jsp/templates/saveAs.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="tx" uri="http://tiles.apache.org/tags-tiles-extras"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<tx:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<tx:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>


<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay saveAs sizeable moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="saveAs" />
    <t:putAttribute name="containerTitle"><spring:message code="dialog.saveAs.title"/></t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
            <label class="control input text" accesskey="o" for="saveAsInputName" title="<spring:message code='resource.visiblename.tooltip'/>">
                <span class="wrap"><spring:message code="dialog.file.name"/> (<spring:message code='required.field'/>):</span>
                <input class="" id="saveAsInputName" type="text" value=""/>
                <span class="message warning"></span>
            </label>
            <label class="control textArea" for="saveAsInputDescription">
                <span class="wrap"><spring:message code="dialog.file.description"/>:</span>
                <textarea id="saveAsInputDescription" type="text"> </textarea>
                <span class="message warning"></span>
            </label>

            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="control groupBox"/>
                    <t:putAttribute name="bodyContent">${bodyContent}</t:putAttribute>
            </t:insertTemplate>


    </t:putAttribute>
    <t:putAttribute name="footerContent">
         <button id="saveAsBtnSave" class="button action primary up">
            <span class="wrap">
                <spring:message code="dialog.file.save"/>
                <span class="icon"></span>
            </span>
         </button>
         <button id="saveAsBtnCancel" class="button action up">
            <span class="wrap">
                <spring:message code="dialog.file.cancel"/>
                <span class="icon"></span>
            </span>
         </button>
    </t:putAttribute>
</t:insertTemplate>
