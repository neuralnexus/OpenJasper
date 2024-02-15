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

    <t:insertTemplate template="/WEB-INF/jsp/templates/dependencies.jsp">
    </t:insertTemplate>
    
--%>

<%@ page import="com.jaspersoft.jasperserver.api.JSException" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<t:useAttribute id="containerClass" name="containerClass" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="bodyContent" name="bodyContent" classname="java.lang.String" ignore="true"/>

<!--/WEB-INF/jsp/templates/dependencies.jsp revision A-->
<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass">panel dialog overlay dependencies sizeable moveable ${containerClass}</t:putAttribute>
    <t:putAttribute name="containerID" value="dependencies" />
    <t:putAttribute name="containerTitle"><spring:message code="dialog.dependencies.title"/></t:putAttribute>
    <t:putAttribute name="containerElements"><div class="sizer diagonal"></div></t:putAttribute>
    <t:putAttribute name="headerClass" value="mover"/>
    <t:putAttribute name="bodyContent">
        <p id="topMessage"></p>
        <div class="control groupBox">
            ${bodyContent}
        </div>
        <p id="bottomMessage" class="saveMessage"></p>
    </t:putAttribute>
    <t:putAttribute name="footerContent">
         <button id="dependenciesBtnSave" class="button action primary up"><span class="wrap"><spring:message code="dialog.file.save"/><span class="icon"></span></button>
         <button id="dependenciesBtnSaveAs" class="button action primary up"><span class="wrap"><spring:message code="button.saveAs"/><span class="icon"></span></button>
         <button id="dependenciesBtnOk" class="button action primary up"><span class="wrap"><spring:message code="button.ok"/><span class="icon"></span></button>
         <button id="dependenciesBtnCancel" class="button action up"><span class="wrap"><spring:message code="button.cancel"/><span class="icon"></span></button>
    </t:putAttribute>
</t:insertTemplate>
