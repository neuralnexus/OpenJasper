<%--
  ~ Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~ Licensed under commercial Jaspersoft Subscription License Agreement
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="menu.aws.settings"/></t:putAttribute>
    <t:putAttribute name="bodyID" value="awsOptions"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="admin.options.page"/>

    <t:putAttribute name="headerContent">

        <%@ include file="administerState.jsp" %>
        <%@ include file="../common/jsEdition.jsp" %>
        <script type="text/javascript">
            __jrsConfigs__.Administer = Administer;
        </script>
    </t:putAttribute>
    <t:putAttribute name="bodyContent">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value="settings"/>
            <t:putAttribute name="containerClass" value="column decorated primary"/>
            <t:putAttribute name="containerTitle"><spring:message code="menu.aws.settings"/></t:putAttribute>
            <t:putAttribute name="bodyClass" value=""/>
            <t:putAttribute name="bodyContent">
                <ol class="list settings">
                    <li class="node">
                        <div class="wrap">
                            <h2 class="title settingsGroup"><spring:message code="aws.db.security.group.settings.title"/></h2>
                        </div>
                        <p class="description"><spring:message code="aws.db.security.group.settings.title.description"/></p>
                        <ol class="list settings">
                            <%@include file="awsConfigurationInclude.jsp"%>

                            <%
                                request.setAttribute("oName", "aws.db.security.group.changes.enabled");
                                request.setAttribute("oDesc", "aws.db.security.group.changes.enabled.explain");
                                request.setAttribute("oLabelCode", "aws.db.security.group.changes.enabled");
                                request.setAttribute("oValue", request.getAttribute("aws.db.security.group.changes.enabled"));
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />
                            <%
                                request.setAttribute("oName", "aws.db.security.group.name");
                                request.setAttribute("oDesc", "aws.db.security.group.name.explain");
                                request.setAttribute("oLabelCode", "aws.db.security.group.name");
                                request.setAttribute("oValue", request.getAttribute("aws.db.security.group.name"));
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />
                            <%
                                request.setAttribute("oName", "aws.db.security.group.description");
                                request.setAttribute("oDesc", "aws.db.security.group.description.explain");
                                request.setAttribute("oLabelCode", "aws.db.security.group.description");
                                request.setAttribute("oValue", request.getAttribute("aws.db.security.group.description"));
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />
                            <%
                                request.setAttribute("oName", "aws.db.security.group.ingressPublicIp");
                                request.setAttribute("oDesc", "aws.db.security.group.ingressPublicIp.explain");
                                request.setAttribute("oLabelCode", "aws.db.security.group.ingressPublicIp");
                                request.setAttribute("oValue", request.getAttribute("aws.db.security.group.ingressPublicIp"));
                            %>
                            <jsp:include page="templateInputText.jsp" flush="true" />

                            <%
                                request.setAttribute("oName", "aws.db.security.group.suppressEc2CredentialsWarnings");
                                request.setAttribute("oDesc", "aws.db.security.group.suppressEc2CredentialsWarnings.explain");
                                request.setAttribute("oLabelCode", "aws.db.security.group.suppressEc2CredentialsWarnings");
                                request.setAttribute("oValue", request.getAttribute("aws.db.security.group.suppressEc2CredentialsWarnings"));
                            %>
                            <jsp:include page="templateCheckbox.jsp" flush="true" />

                        </ol>
                    </li>
                </ol>
            </t:putAttribute>
            <t:putAttribute name="footerContent">
            </t:putAttribute>
        </t:insertTemplate>

        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerID" value="serverSettingsMenu"/>
            <t:putAttribute name="containerClass" value="column decorated secondary sizeable"/>
            <t:putAttribute name="containerElements">
                <div class="sizer horizontal"></div>
                <button class="button minimize"></button>
            </t:putAttribute>
            <t:putAttribute name="containerTitle"><spring:message code="menu.settings"/></t:putAttribute>
            <t:putAttribute name="bodyClass" value=""/>
            <t:putAttribute name="bodyContent">
                <!--
                NOTE: these objects serve as navigation links, load respective pages
                -->
                <ul class="list responsive filters">
                    <li class="leaf"><p class="wrap button" id="navLogSettings"><b class="icon"></b><spring:message code="menu.log.Settings"/></p></li>
                    <c:choose>
                        <c:when test="${isProVersion}">
                            <c:set var="analysisOptionsId" value="navAnalysisOptions"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="analysisOptionsId" value="navAnalysisOptionsCE"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${isProVersion}">
                        <li class="leaf"><p class="wrap button" id="navDesignerOptions"><b class="icon"></b><spring:message code="menu.adhoc.options"/></p></li>
                        <li class="leaf"><p class="wrap button" id="navDesignerCache"><b class="icon"></b><spring:message code="menu.adhoc.cache"/></p></li>
                    </c:if>
                    <li class="leaf"><p class="wrap button" id="${analysisOptionsId}"><b class="icon"></b><spring:message code="menu.mondrian.properties"/></p></li>
                    <li class="leaf selected"><p class="wrap button" id="navAwsSettings"><b class="icon"></b><spring:message code="menu.aws.settings"/></p></li>

                        <li class="leaf"><p class="wrap button" id="navImport"><b class="icon"></b><spring:message code="import.import"/></p></li>
                        <li class="leaf"><p class="wrap button" id="navExport"><b class="icon"></b><spring:message code="export.export"/></p></li>

                    <li class="leaf" disabled="disabled"><p class="wrap separator" href="#"><b class="icon"></b></p></li>
                </ul>            </t:putAttribute>
            <t:putAttribute name="footerContent">
            </t:putAttribute>
        </t:insertTemplate>

    </t:putAttribute>

</t:insertTemplate>
