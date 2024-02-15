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

Usage:

	<t:insertTemplate template="/WEB-INF/jsp/templates/exportControls.jsp">
	     //some selectore to controls container
	    <t:putAttribute name="containerID" value=""/>
	     //detalization of displayed functionality, could be 'short' or 'extended'
	     // 'short' provide functions for resource export only
	     // 'extended'  adds user/roles and events export function
	    <t:putAttribute name="detalization" value=""/>
	</t:insertTemplate>
	
--%>

<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:useAttribute id="containerID" name="containerID" classname="java.lang.String" ignore="true"/>
<t:useAttribute id="typeID" name="typeID" classname="java.lang.String" ignore="true"/>

<%--Templates--%>
<jsp:include page="importTemplates.jsp" />
<jsp:include page="componentsTemplates.jsp" />

<%--i18n--%>
<script type="text/javascript">
    if (typeof __jrsConfigs__.Import === "undefined") {
        __jrsConfigs__.Import = {};
    }

    __jrsConfigs__.Import.i18n = {
        "unexpected.error": '<spring:message code="jsexception.unable.to.complete.request" javaScriptEscape="true"/>',
        "error.timeout" : '<spring:message code="error.timeout" javaScriptEscape="true"/>',
        "error.invalid.response" : '<spring:message code="error.invalid.response" javaScriptEscape="true"/>'
    };

    __jrsConfigs__.Import.initParams = {
        container: "${containerID}",
        type: "${typeID}"
    };
</script>