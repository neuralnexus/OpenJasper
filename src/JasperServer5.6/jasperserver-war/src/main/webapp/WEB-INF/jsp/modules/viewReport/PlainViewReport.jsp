<%--
  ~ Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~ Licensed under commercial Jaspersoft Subscription License Agreement
  --%>

<%@ taglib prefix="spring" uri="/spring" %>
<%@ taglib uri="/WEB-INF/jasperserver.tld" prefix="js" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page errorPage="/WEB-INF/jsp/modules/system/errorPage.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title>${reportUnitObject.label}</title>
    <%--styles for report--%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/themes/reset.css" type="text/css" media="screen">
    <!-- Theme -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="theme.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="pages.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="containers.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="buttons.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="lists.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="controls.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="dataDisplays.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="pageSpecific.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="dialogSpecific.css"/>" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="forPrint.css"/>" type="text/css" media="print"/>

    <!--[if IE 7.0]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_ie7.css"/>" type="text/css" media="screen"/>
    <![endif]-->

    <!--[if IE 8.0]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_ie8.css"/>" type="text/css" media="screen"/>
    <![endif]-->

    <!--[if IE]>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_ie.css"/>" type="text/css" media="screen"/>
    <![endif]-->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code="overrides_custom.css"/>" type="text/css" media="screen"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/scripts/lib/jquery/theme/jquery.ui.theme.css" type="text/css" media="screen,print"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/scripts/lib/jquery/theme/redmond/jquery-ui-1.10.4-custom.css" type="text/css" media="screen">

    <style type="text/css">
        /*
            Override body background color set in theme.css
        */
        body {background:none;}
        .novis {visibility:hidden;}
    </style>

    <%-- requirejs module name --%>
    <c:set var="moduleName" value="plain.report.viewer.page"/>

    <jsp:include page="../includeRequirejsScripts.jsp"/>
    <jsp:include page="../common/jrsConfigs.jsp"/>
    <%@ include file="ViewReportState.jsp" %>

    <%--
        Performance optimization:
        we do not wait until requirejs will load this module,
        instead we load it by ourself (but only if optimization is turned on)
        thus when requirejs will request it it already will be loaded.
    --%>
    <c:if test="${optimizeJavascript == true}">
        <script type="text/javascript" src="${scriptsUri}/${moduleName}.js"></script>
    </c:if>

    <script type="text/javascript">
        <c:if test="${!pageContext.request.requestedSessionIdValid and pageContext.request.method == 'GET'}">
            <%--[18280] HTTP redirect can't be applied here because it adds jsessionid parameter --%>
            window.location.reload();
        </c:if>

        require.onError = function (err) {
            //block errors because of Bug 34818 with frequent refresh of iFrames,
            //throw err;
        };

        require(["${moduleName}"]);
    </script>
</head>

<body id="reportViewer" class="oneColumn">
<%--required by JIVE--%>
<div class="body">

<%--note: not using class hidden because it creates a padding--%>
<form style="display:none"  name="viewReportForm" action="<c:url value="flow.html"/>" method="post">
    <input class="hidden" type="hidden" name="pageIndex" value="${pageIndex}"/>
    <input class="hidden" type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
</form>

<t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
    <t:putAttribute name="containerID" value="emptyReportID" />
    <t:putAttribute name="bodyContent">
        <p class="message emphasis">${reportUnitObject.label}</p>
        <p class="message">
            <%-- currently empty report message is set on server side - see ViewReportAction.runReport --%>
        </p>
    </t:putAttribute>
</t:insertTemplate>

<c:choose>
<c:when test="${false}">
<div id="reportContainer" class="novis" style="position:relative;"></div>
</c:when>
<c:otherwise>
<div id="reportContainer" class="" style="position:relative;">
    <c:if test="${isAdhocReportUnit != null && isAdhocReportUnit == 'true'}">
        <spring:message code="log.error.cannot.view.adhocReport"/>
    </c:if>
</div>
</c:otherwise>
</c:choose>

</div>
</body>
</html>
