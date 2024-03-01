<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
	<t:putAttribute name="pageTitle"><spring:message code="report.scheduling.list.title"/></t:putAttribute>
	<t:putAttribute name="bodyID" value="scheduler_jobSummary"/>
	<t:putAttribute name="bodyClass" value="oneColumn scheduler_jobSummary"/>

	<t:putAttribute name="moduleName" value="scheduler/schedulerMain"/>

	<t:putAttribute name="headerContent">

		<jsp:include page="../inputControls/commonInputControlsImports.jsp" />

		<js:out javaScriptEscape="true">
		<script type="text/javascript">
			__jrsConfigs__.usersTimeZone = "${timezone}";
			__jrsConfigs__.enableSaveToHostFS = "${enableSaveToHostFS}";
			__jrsConfigs__.reportJobEditorDefaults = JSON.parse('${reportJobDefaults}');
			__jrsConfigs__.availableReportJobOutputFormats = JSON.parse('${availableReportJobOutputFormats}');
			__jrsConfigs__.availableDashboardJobOutputFormats = JSON.parse('${availableDashboardJobOutputFormats}');
			__jrsConfigs__.VALUE_SUBSTITUTION = "<spring:message code="input.password.substitution"/>";

			__jrsConfigs__.timeZones = [];
            <c:forEach items="${userTimezones}" var="timezone">
                __jrsConfigs__.timeZones.push({value: "${timezone.code}", title: "<spring:message code="timezone.option" arguments='${timezone.code},${timezone.description}'/>"});
            </c:forEach>
		</script>
		</js:out>
	</t:putAttribute>

	<t:putAttribute name="bodyContent" >

		<div id="saveValues" class="panel dialog saveValues overlay moveable centered_horz centered_vert hidden" >
			<div  class="content hasFooter " >
				<div  class="header mover" >
					<div class="title">
						Save Values
					</div>
				</div>
				<div  class="body  " >
					<label class="control input text" accesskey="o" for="savedValuesName" title="This is the displayed name of the resource. It can be changed at any time.">
						<span class="wrap">The Name for the Saved Values (required):</span>
						<input class="" id="savedValuesName" type="text" value=""/>
						<span class="message warning">error message here</span>
					</label>
				</div>
				<div  class="footer " >
					<button id="saveAsBtnSave" class="button action primary up"><span class="wrap">Save<span class="icon"></span></button>
					<button id="saveAsBtnCancel" class="button action up"><span class="wrap">Cancel<span class="icon"></span></button>
				</div>
			</div>
		</div>

		<jsp:include page="../inputControls/InputControlTemplates.jsp" />

	</t:putAttribute>

</t:insertTemplate>
