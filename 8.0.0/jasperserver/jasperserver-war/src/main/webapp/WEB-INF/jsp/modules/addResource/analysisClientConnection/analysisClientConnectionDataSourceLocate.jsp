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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="resource.analysisConnection.mondrian.locate.page.title"/></t:putAttribute>
    <t:putAttribute name="bodyID" value="addResource_locateDataSource"/>

    <t:putAttribute name="bodyClass" value="oneColumn flow wizard"/>
    <t:putAttribute name="moduleName" value="addResource/analysisClientConnection/locateDataSourceMain"/>
    <t:putAttribute name="headerContent">
        <jsp:include page="analysisClientConnectionDataSourceLocateState.jsp"/>
    </t:putAttribute>

    <t:putAttribute name="bodyContent">
    	<form method="post">
        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
            <t:putAttribute name="containerClass" value="column decorated primary"/>
            <t:putAttribute name="containerTitle"><spring:message code='addResources.analysisClient.title' javaScriptEscape='true'/></t:putAttribute>
            <t:putAttribute name="swipeScroll" value="${isIPad}"/>

            <t:putAttribute name="bodyContent">
                <div id="flowControls">
                </div>
                <div id="stepDisplay">
                    <fieldset class="row instructions">
                        <legend class="offLeft"><span><spring:message code='addResources.analysisClient.instructions' javaScriptEscape='true'/></span></legend>
                        <h2 class="textAccent02"><spring:message code='addResources.analysisClient.locateDataSource' javaScriptEscape='true'/></h2>
                        <h4><!--NOTE: keep h4 markup, but leave empty --></h4>

                        <p class="warning">Error or warning here.</p>
                    </fieldset>

                    <fieldset class="row inputs oneColumn">
                        <legend class="offLeft"><span><spring:message code='addResources.analysisClient.userInputs' javaScriptEscape='true'/></span></legend>

                        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                            <t:putAttribute name="containerClass" value="column noHeader primary"/>

                            <t:putAttribute name="bodyContent">
                                <fieldset class="locationSelector">
                                    <legend class="offLeft"><span><spring:message code='addResources.analysisClient.locateResource' javaScriptEscape='true'/></span></legend>
                                    <ul class="list locations">
                                        <spring:bind path="dataResource.source">
                                        <li id="fromRepo" class="leaf">
											<div class="control radio complex">
												<label class="wrap" for="CONTENT_REPOSITORY" title="<spring:message code='resource.report.repository'/>">
														<spring:message code='addResources.analysisClient.selectDataSource' javaScriptEscape='true'/>
												</label>
												<input id="CONTENT_REPOSITORY" name="${status.expression}" type="hidden" value="CONTENT_REPOSITORY" />
                                                <span class="message warning">error message here</span>
											</div>
                                            </spring:bind>
                                            <spring:bind path="dataResource.selectedUri">
                                                <label class="control browser<c:if test="${status.error}"> error</c:if>" for="resourceUri"  title="<spring:message code='resource.report.repository'/>">
                                                    <input id="resourceUri" type="text" name="${status.expression}" value="${status.value}" />
                                                    <button id="browser_button" type="button" class="button action" >
                                                    	<span class="wrap"><spring:message code="button.browse"/><span class="icon"></span></span>
													</button>
                                                    <c:if test="${status.error}">
                                                        <span class="message warning">${status.errorMessage}</span>
                                                    </c:if>
                                                </label>
                                            </spring:bind>
                                        </li>
                                    </ul>
                                </fieldset>
                            </t:putAttribute>
                        </t:insertTemplate>
                    </fieldset><!--/.row.inputs-->
				</div>
			</t:putAttribute>

            <t:putAttribute name="footerContent">
                <fieldset id="wizardNav">
                    <button id="previous" type="submit" class="button action up" name="_eventId_back"><span class="wrap"><spring:message
                            code='button.previous'/></span><span class="icon"></span></button>
                    <button id="next" type="submit" class="button action up" name="_eventId_next"><span class="wrap"><spring:message
                            code='button.next'/></span><span class="icon"></span></button>
                    <button id="cancel" type="submit" name="_eventId_cancel" class="button action up"><span class="wrap"><spring:message
                            code='button.cancel'/></span><span class="icon"></span></button>
                </fieldset>
            </t:putAttribute>
        </t:insertTemplate>
            <!--/.row.inputs-->
       		<input type="hidden" name="_flowExecutionKey" value="${flowExecutionKey}"/>
		</form>
        <!--/#stepDisplay-->
        <t:insertTemplate template="/WEB-INF/jsp/templates/selectFromRepository.jsp">
            <t:putAttribute name="containerClass">hidden</t:putAttribute>
            <t:putAttribute name="bodyContent">
                <ul id="dsTreeRepoLocation"> </ul>
            </t:putAttribute>
        </t:insertTemplate>

        <div id="ajaxbuffer" class="hidden" ></div>
    </t:putAttribute>
</t:insertTemplate>
