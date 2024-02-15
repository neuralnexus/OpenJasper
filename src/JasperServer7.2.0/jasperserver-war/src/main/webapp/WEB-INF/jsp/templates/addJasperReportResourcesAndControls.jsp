<%@ page contentType="text/html; charset=utf-8" %>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

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

<script id="addJasperReportNonSuggestedResourceTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one">
                {{ if (canChangeResources) { }}
                <a class="emphasis" href="#">{{-name}}</a>
                {{ } else { }}
                {{-name}}
                {{ } }}
            </p>
            <p class="column two">{{-fileType}}</p>
            <p class="column three">
                {{ if (canChangeResources) {}}
                <a class="launcher" href="#"><spring:message code="resource.report.remove"/></a>
                {{ } }}
            </p>
        </div>
        <js:xssNonce/>
    </li>
</script>

<script id="addJasperReportSuggestedResourceTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one"><a class="emphasis" href="#">{{-label}}</a></p>
            <p class="column two">{{-fileType}}</p>
            <p class="column three">
                {{if (located) { }}
                <spring:message code="jsp.listResources.added"/>
                {{ } else { }}
                <a class="launcher" href="#"><spring:message code="jsp.listResources.addNow"/></a>
                {{ } }}
            </p>
        </div>
        <js:xssNonce/>
    </li>
</script>

<script id="addJasperReportAddResourceTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one">
            {{ if (canChangeResources) { }}
                <a class="launcher" href="#"><spring:message code="resource.report.addResource"/></a>
            {{ } }}
            </p>
            <p class="column two"></p>
            <p class="column three"></p>
        </div>
        <js:xssNonce/>
    </li>
</script>

<script id="addJasperReportNonSuggestedControlTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one">
                {{ if (canChangeResources) { }}
                    {{if (local) { }}
                        <a class="emphasis" href="#">{{-label}}</a>
                    {{ } else { }}
                        <a class="emphasis" href="#">{{-referenceURI}}</a>
                    {{ } }}
                {{ } else { }}
                    {{if (local) { }}
                        {{-label}}
                    {{ } else { }}
                        {{-referenceURI}}
                    {{ } }}
                {{ } }}
            </p>
            <p class="column two">{{-type}} <spring:message code="jsp.listResources.inputControl"/></p>
            <p class="column three">
                {{ if (canChangeResources) { }}
                    <a class="launcher" href="#"><spring:message code="resource.report.remove"/></a>
                {{ } }}
            </p>
        </div>
        <js:xssNonce/>
    </li>
</script>

<script id="addJasperReportSuggestedControlTemplate" type="text/mustache">
    <li class="leaf">
        <div class="wrap">
            <b class="icon" title=""></b>
            <p class="column one"><a class="emphasis" href="#">{{-label}}</a></p>
            <p class="column two">{{-type}} <spring:message code="jsp.listResources.inputControl"/></p>
            <p class="column three">
                {{if (located) { }}
                    <a class="launcher" href="#"><spring:message code="resource.report.remove"/></a>
                {{ } else { }}
                    <spring:message code="jsp.listResources.notAdded"/>
                {{ } }}
            </p>
        </div>
        <js:xssNonce/>
    </li>
</script>

<script id="addJasperReportAddControlTemplate" type="text/mustache">
    <li class="leaf">
        <js:xssNonce/>
        {{if (canChangeResources) { }}
            <div class="wrap">
                <b class="icon" title=""></b>
                <p class="column one">
                    <!-- NOTE: this link invokes #selectFile -->
                    <a class="launcher" href="#"><spring:message code="resource.report.addInputControl"/></a>
                </p>
                <p class="column two"></p>
                <p class="column three"></p>
            </div>
        {{ } }}
    </li>
</script>


