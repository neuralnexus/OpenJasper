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

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>


<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle"><spring:message code="report.scheduling.list.title"/></t:putAttribute>
    <t:putAttribute name="bodyID" value="scheduler_jobSummary"/>
    <t:putAttribute name="bodyClass" value="oneColumn scheduler_jobSummary"/>
    <t:putAttribute name="moduleName" value="scheduler/JobsPage"/>

    <t:putAttribute name="headerContent">

        <jsp:include page="../inputControls/commonInputControlsImports.jsp" />

        <script type="text/javascript">
            __jrsConfigs__.usersTimeZone = "${timezone}";
            __jrsConfigs__.enableSaveToHostFS = "${enableSaveToHostFS}";
        </script>

    </t:putAttribute>


		<t:putAttribute name="bodyContent" >

			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			    <t:putAttribute name="containerClass" value="page hidden column decorated primary showingToolBar"/>
				<t:putAttribute name="containerID" value="list"/>
			    <t:putAttribute name="containerTitle"><spring:message code="report.scheduling.list.header"/> <span class="path">${ownerURI}</span></t:putAttribute>


				<t:putAttribute name="headerContent">
                    <div class="toolbar">
						<ul class="list buttonSet">
							<li class="node open">
								<ul class="list buttonSet">
									<li class="leaf"><button class="button capsule text up backButton">
									    <span class="wrap"><spring:message code="button.back"/></span>
									    <span class="icon"></span>
									</button></li>
								</ul>
							</li>
							<li class="node open">
								<ul class="list buttonSet">
									<li class="leaf"><button class="button capsule text first up scheduleJob">
									    <span class="wrap"><spring:message code="report.scheduling.list.button.new"/></span>
									    <span class="icon"></span>
									</button></li>
									<li class="leaf"><button class="button capsule text last up runJob">
									    <span class="wrap"><spring:message code="report.scheduling.list.button.now"/></span>
									    <span class="icon"></span>
									</button></li>
								</ul>
							</li>
							<li class="node open">
								<ul class="list buttonSet">
									<li class="leaf"><button class="button capsule text up refreshList">
									    <span class="wrap"><spring:message code="report.scheduling.list.button.refresh"/></span>
									    <span class="icon"></span>
									</button></li>
								</ul>
							</li>
						</ul>
					</div>

				</t:putAttribute>

                <%--<t:putAttribute name="swipeScroll" value="${isIPad}"/>--%>

                <t:putAttribute name="bodyContent">


					<div id="jobsSummaryContainer">
                        <!-- header row -->
                        <div class="subheader">
                            <ul id="resultsListHeader" class="list collapsible tabular jobs sixColumn header">
                                <li id="" class="jobs first leaf">
                                    <div class="wrap draggable">
                                        <div class="column one">
                                            <p class="jobID"><spring:message code="report.scheduling.list.header.id"/></p>
                                        </div>
                                        <div class="column two">
                                            <p class="jobName"><spring:message code="report.scheduling.list.header.label"/></p>
                                            <p class="jobOwner"><spring:message code="report.scheduling.list.header.owner"/></p>
                                        </div>
                                        <div class="column three">
                                            <p class="jobState"><spring:message code="report.scheduling.list.header.state"/></p>
                                        </div>
                                        <div class="column four">
                                            <p class="lastRanDate"><spring:message code="report.scheduling.list.header.prevFireTime"/></p>
                                            <p class="nextRunDate"><spring:message code="report.scheduling.list.header.nextFireTime"/></p>
                                        </div>
                                        <div class="column five">
                                            <p class="jobEnabled"><spring:message code="report.scheduling.list.label.enabled"/></p>
                                        </div>
                                        <div class="column six">
                                            <div class="jobEdit"> </div>
                                            <div class="jobDelete"> </div>
                                        </div>
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <!-- end header row -->

                        <!-- body of jobs list -->
                        <div id="resultsContainer" class="body">
                            <ul id="resultsList" class="list collapsible tabular jobs sixColumn"></ul>
                        </div>
                        <!-- end of body of jobs list -->

                    </div>

                </t:putAttribute>
            </t:insertTemplate>

            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                <t:putAttribute name="containerClass" value="page hidden column decorated primary tabbed scheduler_jobSetUp"/>
                <t:putAttribute name="containerID" value="scheduler_editor"/>
                <t:putAttribute name="containerTitle"><spring:message code="report.scheduling.job.edit.editor"/></t:putAttribute>


                <t:putAttribute name="headerContent">

                    <div class="tabs">
                        <ul class="control tabSet buttons horizontal ">
                            <li class="tab first selected" data-tab="schedule">
                                <a class="button">
                                    <span class="wrap"><spring:message code="report.scheduling.schedule"/></span>
                                </a>
                            </li>
                            <li class="tab" data-tab="parameters">
                                <a class="button">
                                    <span class="wrap"><spring:message code="report.scheduling.job.edit.parameters.button"/></span>
                                </a>
                            </li>
                            <li class="tab" data-tab="output">
                                <a class="button">
                                    <span class="wrap"><spring:message code="report.scheduling.job.edit.output"/></span>
                                </a>
                            </li>
                            <li class="tab" data-tab="notifications">
                                <a class="button">
                                    <span class="wrap"><spring:message code="report.scheduling.job.edit.notifications"/></span>
                                </a>
                            </li>
                        </ul>

                        <div class="control tabSet anchor"></div>
                    </div>


                </t:putAttribute>

                <t:putAttribute name="bodyContent">
					<div id="stepDisplay">
	                    <fieldset class="schedule_for">
	                        <legend class="title"><spring:message code='report.scheduling.job.edit.shedulefor'/>:
	                            <span class="path">/public/Samples/Reports/RevenueDetailsReport</span>
	                        </legend>
	                    </fieldset>

	                    <div class="tab tab1 hidden" id="schedulerTab-schedule" data-tab="schedule"><%@ include file="tab.schedule.jsp" %></div>
	                    <div class="tab tab2 hidden" id="schedulerTab-parameters" data-tab="parameters"><%@ include file="tab.parameters.jsp" %></div>
	                    <div class="tab tab3 hidden" id="schedulerTab-output" data-tab="output"><%@ include file="tab.output.jsp" %></div>
	                    <div class="tab tab4 hidden" id="schedulerTab-notifications" data-tab="notifications"><%@ include file="tab.notifications.jsp" %></div>
	
	                    <t:insertTemplate template="/WEB-INF/jsp/templates/selectFromRepository.jsp">
	                        <t:putAttribute name="containerClass">hidden</t:putAttribute>
	                        <t:putAttribute name="bodyContent">
	                            <ul class="responsive collapsible folders hideRoot" id="repoTree"> </ul>
	                        </t:putAttribute>
	                    </t:insertTemplate>
	                </div>
                </t:putAttribute>

                <t:putAttribute name="footerContent">
					<fieldset id="wizardNav">
	                    <button id="save" class="button primary action up" data-action="save">
	                        <span class="wrap"><spring:message code='button.save'/></span><span class="icon"></span>
	                    </button>
	
	                    <button id="submit" class="button primary action up" data-action="submit">
	                        <span class="wrap"><spring:message code='button.submit'/></span><span class="icon"></span>
	                    </button>
	
	                    <button id="cancel" class="button action up">
	                        <span class="wrap"><spring:message code='button.cancel'/></span><span class="icon"></span>
	                    </button>
	                </fieldset>
                </t:putAttribute>

            </t:insertTemplate>

            <t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
                <t:putAttribute name="bodyContent">
                    <p class="message"><spring:message code="report.scheduling.list.no.jobs"/></p>
                </t:putAttribute>
                <t:putAttribute name="containerAttributes">
                    style="position:absolute; top:33%; left:15%; bottom:33%; right:35%; min-width:600px;"
                </t:putAttribute>
            </t:insertTemplate>

            <t:insertTemplate template="/WEB-INF/jsp/templates/standardConfirm.jsp">
                <t:putAttribute name="containerClass">hidden</t:putAttribute>
                <t:putAttribute name="leftButtonId" value="runReportOK"/>
                <t:putAttribute name="rightButtonId" value="runReportCancel"/>
                <t:putAttribute name="okLabel"><spring:message code='report.scheduling.delete.yes'/></t:putAttribute>
                <t:putAttribute name="cancelLabel"><spring:message code='report.scheduling.delete.no'/></t:putAttribute>
            </t:insertTemplate>

            <t:insertTemplate template="/WEB-INF/jsp/templates/saveAs.jsp">
                <t:putAttribute name="containerClass">hidden centered_horz centered_vert</t:putAttribute>
            </t:insertTemplate>

            <t:insertTemplate template="/WEB-INF/jsp/templates/saveValues.jsp">
                <t:putAttribute name="containerClass" value="hidden"/>
            </t:insertTemplate>

            <div id="ajaxbuffer" style="display:none"></div>

        </t:putAttribute>

</t:insertTemplate>