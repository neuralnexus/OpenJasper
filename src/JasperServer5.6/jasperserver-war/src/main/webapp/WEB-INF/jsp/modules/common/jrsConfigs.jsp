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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="authz"%>
<%@ page import="com.jaspersoft.jasperserver.war.webHelp.WebHelpLookup" %>

<%--Global JRS State/Config object --%>

<script type="text/javascript">
    var JRS = {};

    var __jrsConfigs__ = {

        i18n: {},
        localContext: {},
        isIPad: "<c:out value="${isIPad}"/>",
        contextPath: "<c:out value="${pageContext.request.contextPath}"/>",
        publicFolderUri: "<c:out value="${not empty publicFolderUri ? publicFolderUri : commonProperties.publicFolderUri}"/>",
        organizationId: "<c:out value="${not empty organizationId ? organizationId : commonProperties.organizationId}"/>",
        commonReportGeneratorsMetadata: ${not empty reportGenerators ? reportGenerators : '[]'},
        templatesFolderUri: '<c:out value="${not empty templatesFolderUri ? templatesFolderUri : templateProperties.templatesFolderUri}"/>',
        defaultTemplateUri: '<c:out value="${not empty defaultTemplateUri ? defaultTemplateUri : templateProperties.defaultTemplateUri}"/>',
        advNotSelected: "<spring:message code="ADH_162_NULL_SAVE_REPORT_SOURCE" javaScriptEscape="true"/>",
        templateNotSelected: "<spring:message code="ADH_162_NULL_SELECT_TEMPLATE_SOURCE" javaScriptEscape="true"/>",
        calendar: {

            userLocale: "${userLocale}",

            timepicker: {
                timeText: '<spring:message code="CAL_time" javaScriptEscape="true"/>',
                hourText: '<spring:message code="CAL_hour" javaScriptEscape="true"/>',
                minuteText: '<spring:message code="CAL_min" javaScriptEscape="true"/>',
                secondText: '<spring:message code="CAL_second" javaScriptEscape="true"/>',
                currentText: '<spring:message code="CAL_now" javaScriptEscape="true"/>',
                closeText: '<spring:message code="CAL_close" javaScriptEscape="true"/>',
                timeFormat: '<spring:message code="calendar.time.format" javaScriptEscape="true"/>',
                dateFormat: '<spring:message code="calendar.date.format" javaScriptEscape="true"/>',
                separator:'<spring:message code="calendar.datetime.separator" javaScriptEscape="true"/>'
            },

            i18n: {
                bundledCalendarTimeFormat: '<spring:message code="calendar.time.format" javaScriptEscape="true"/>',
                bundledCalendarFormat: '<spring:message code="calendar.date.format" javaScriptEscape="true"/>'
            }
        },

        webHelpModuleState: {
            contextMap: <%= WebHelpLookup.getInstance().getHelpContextMapAsJSON() %>,
            hostURL: '<%= WebHelpLookup.getInstance().getHostURL() %>',
            pagePrefix: '<%= WebHelpLookup.getInstance().getPagePrefix() %>'
        },

        urlContext: "${pageContext.request.contextPath}",
        defaultSearchText: "<spring:message code='SEARCH_BOX_DEFAULT_TEXT'/>",
        serverIsNotResponding: "<spring:message code='confirm.slow.server'/>"
    };

    __jrsConfigs__.isProVersion = "${isProVersion}" === "true" ? true : false;

    __jrsConfigs__.userLocale = "${userLocale}";

    __jrsConfigs__.isFreeOrLimitedEdition = false;

    //TODO: get it from the server
    __jrsConfigs__.avaliableLocales = ["de", "en", "es", "fr", "it", "ja", "ro", "zh_TW", "zh_CN"];


    __jrsConfigs__.localeSettings = {
        locale: "${userLocale}",
        decimalSeparator: "${requestScope.decimalSeparatorForUserLocale}" || ".",
        groupingSeparator: "${requestScope.groupingSeparatorForUserLocale}" || ",",
        timeFormat: "<spring:message code="calendar.time.format" javaScriptEscape="true"/>",
        dateFormat: "<spring:message code="calendar.date.format" javaScriptEscape="true"/>",
        timestampSeparator: "<spring:message code="calendar.datetime.separator" javaScriptEscape="true"/>"
    };
    //Heartbeat

    __jrsConfigs__.heartbeatInitOptions = {
        baseUrl: "<c:out value="${pageContext.request.contextPath}"/>",
        showDialog: false,
        sendClientInfo: false
    };

    <c:choose>
        <c:when test="${param['decorate'] == 'no' || param['sessionDecorator'] == 'no' || sessionScope['sessionDecorator'] == 'no'}">
            __jrsConfigs__.initAdditionalUIComponents = false;
        </c:when>
        <c:otherwise>
            __jrsConfigs__.initAdditionalUIComponents = true;
        </c:otherwise>
    </c:choose>

    <%
     com.jaspersoft.jasperserver.war.common.HeartbeatBean heartbeat =
           (com.jaspersoft.jasperserver.war.common.HeartbeatBean) application.getAttribute("concreteHeartbeatBean");
    %>
    <authz:authorize ifAnyGranted="ROLE_ADMINISTRATOR">
    <%
        if (heartbeat != null && heartbeat.haveToAskForPermissionNow()) {
    %>
    __jrsConfigs__.heartbeatInitOptions.showDialog = true;
    <%
    }
    %>
    </authz:authorize>
    <authz:authorize ifAnyGranted="ROLE_USER,ROLE_ADMINISTRATOR">
    <%
        if (heartbeat != null && heartbeat.isMakingCalls()
                && session != null && session.getAttribute("jsHeartbeatSentClientInfo") == null) {
            session.setAttribute("jsHeartbeatSentClientInfo", Boolean.TRUE);
    %>
    __jrsConfigs__.heartbeatInitOptions.sendClientInfo = true;
    <%
        }
    %>
    </authz:authorize>

    __jrsConfigs__.flowExecutionKey = "${flowExecutionKey}";

</script>
