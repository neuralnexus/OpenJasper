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

<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Dialog Samples"/>
    <t:putAttribute name="bodyID" value="dialogs"/>
    <t:putAttribute name="pageClass" value="test"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
    <t:putAttribute name="headerContent" >
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />
		<style type="text/css">
				#sampleGrid .rowHeader {width:3%;}
				#sampleGrid .example {width:40%;padding:0;}
				#sampleGrid .panel {position: relative;}
		</style>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" >
	
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titledialogs"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >
	        
	        	<table id="sampleGrid">
	        		<thead>
		        		<tr>
		        			<td class="rowHeader"></td>
		        			<td class="example"></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"><spring:message code="JIF.titledialogs"/></th>
		        			<th class="example"><spring:message code="JIF.descriptortitledialogs"/></th>
		        		</tr>

	        		</thead>
					
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#login</th>
		        			<td ><spring:message code="JIF.login"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/login.jsp">
                                    <t:putAttribute name="jsEdition" value="pro"/>
                                    <t:putAttribute name="allowUserPasswordChange" value="true"/>
                                    <t:putAttribute name="localeOptions">
                                        <option selected="" value="en_US"> en_US - English (United States) </option>
                                        <option value="en"> en - English </option>
                                        <option value="fr"> fr - French </option>
                                        <option value="it"> it - Italian </option>
                                        <option value="es"> es - Spanish </option>
                                        <option value="de"> de - German </option>
                                        <option value="ro"> ro - Romanian </option>
                                        <option value="ja"> ja - Japanese </option>
                                        <option value="zh_TW"> zh_TW - Chinese (Taiwan) </option>
                                        <option value="zh_CN"> zh_CN - Chinese (China) </option>
                                    </t:putAttribute>
                                    <t:putAttribute name="timezoneOptions">
                                        <option selected="" value="America/Los_Angeles"> America/Los_Angeles - Pacific Standard Time </option>
                                        <option value="America/Denver"> America/Denver - Mountain Standard Time </option>
                                        <option value="America/Chicago"> America/Chicago - Central Standard Time </option>
                                        <option value="America/New_York"> America/New_York - Eastern Standard Time </option>
                                        <option value="Europe/London"> Europe/London - Greenwich Mean Time </option>
                                        <option value="Europe/Berlin"> Europe/Berlin - Central European Time </option>
                                        <option value="Europe/Bucharest"> Europe/Bucharest - Eastern European Time </option>
                                    </t:putAttribute>
                                </t:insertTemplate>
		        			</td>
		        		</tr>
	        		</tbody>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#sortDialog</th>
		        			<td colspan="2"><spring:message code="JIF.sortDialog"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/sortDialog.jsp">
                                	<t:putAttribute name="bodyContent">
									
									    <t:putAttribute name="availableFields">
									     	<ul class="list responsive collapsible fields hideRoot">
												<li class="leaf"><div class="wrap button"><b class="icon button noBubble"></b>Account Name</div></li>
												<li class="leaf"><div class="wrap button"><b class="icon button noBubble"></b>Account City</div></li>
											</ul>
									    </t:putAttribute>
									    
									    <t:putAttribute name="selectedFields">
										    <ul class="list responsive collapsible fields hideRoot column simple">
												<li class="leaf ascending"><div class="wrap button"><b class="icon button noBubble"></b>Account State</div></li>
												<li class="leaf descending"><div class="wrap button"><b class="icon button noBubble"></b>Account Zip</div></li>
											</ul>
									    </t:putAttribute>

                                	</t:putAttribute>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#selectFields</th>
		        			<td colspan="2"><spring:message code="JIF.selectFields"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/selectFields.jsp">
                                	<t:putAttribute name="bodyContent">
									
									    <t:putAttribute name="availableFields">
									     	<ul class="list responsive collapsible fields hideRoot">
												<li class="leaf"><div class="wrap button"><b class="icon"></b>Account Name</div></li>
												<li class="leaf"><div class="wrap button"><b class="icon"></b>Account City</div></li>
											</ul>
									    </t:putAttribute>
									    
									    <t:putAttribute name="selectedFields">
										    <ul class="list responsive collapsible fields hideRoot">
												<li class="leaf"><div class="wrap button"><b class="icon"></b>Account State</div></li>
												<li class="leaf"><div class="wrap button"><b class="icon"></b>Account Zip</div></li>
											</ul>
									    </t:putAttribute>

                                	</t:putAttribute>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#propertiesResource</th>
		        			<td colspan="2"><spring:message code="JIF.propertiesResource"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/propertiesResource.jsp">
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>

					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#permissions</th>
		        			<td ><spring:message code="JIF.permissions"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td colspan="2" class="example">
								<t:insertTemplate template="/WEB-INF/jsp/templates/permissions.jsp">
									<t:putAttribute name="bodyContent">
										<ul class="list setLeft tabular twoColumn">
											<li class="leaf">
												<div class="wrap"><b class="icon" title=""></b>
													<p class="column one"><a class="launcher">ROLE_DEMO</a></p>
													<p class="column two">
														<select name="">
						        							<option selected="selected" value="">No Access</option>
						        							<option value="">Administer</option>
						        							<option value="">Read Only</option>
						        							<option value="">Read + Delete</option>
						        							<option value="">Read + Write + Delete</option>
						        						</select>
													</p>
												</div>
											</li>
											<li class="leaf">
												<div class="wrap"><b class="icon" title=""></b>
													<p class="column one"><a class="launcher">ROLE_DEMO</a></p>
													<p class="column two">
														<select name="">
						        							<option selected="selected" value="">No Access</option>
						        							<option value="">Administer</option>
						        							<option value="">Read Only</option>
						        							<option value="">Read + Delete</option>
						        							<option value="">Read + Write + Delete</option>
						        						</select>
													</p>
												</div>
											</li>
									</t:putAttribute>
                                </t:insertTemplate>

		        			</td>

		        		</tr>
	        		</tbody>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#customURL</th>
		        			<td ><spring:message code="JIF.customUrl"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td colspan="2" class="example">
								
                                <t:insertTemplate template="/WEB-INF/jsp/templates/customURL.jsp">
									<t:putAttribute name="bodyContent">
										<ul class="list setLeft tabular twoColumn">
											<li class="leaf">
												<div class="wrap header"><b class="icon" title=""></b>
													<p class="column one">
														<span class="label">Input Control</span>
													</p>
													<p class="column two">
														<span class="label">URL Parameter</span>
													</p>
												</div>
											</li>
											<li class="leaf">
												<div class="wrap">
													<b class="icon" title=""></b>
													<div class="column one">
														<div class="control checkBox" for="paramCheckbox_Country">
															<label id="label_for_param_1" class="wrap" for="parameter_1">Country</label>
															<input id="paramCheckbox_Country" type="checkbox" value="">
														</div>
													</div>
													
													<div class="column two">
														<label id="label_for_param_1_value" class="control input text" for="paramMapperInput_Country">
															<input id="paramMapperInput_Country" type="text" value="Country">
														</label>
													</div>
												</div>
											</li>
											<li class="leaf">
												<div class="wrap">
													<b class="icon" title=""></b>
													<div class="column one">
														<div class="control checkBox" for="paramCheckbox_Country">
															<label id="label_for_param_2" class="wrap" for="parameter_2">City</label>
															<input id="paramCheckbox_Country" type="checkbox" value="">
														</div>
													</div>
													
													<div class="column two">
														<label id="label_for_param_2_value" class="control input text" for="paramInput_Country">
															<input id="paramInput_Country" type="text" value="City">
														</label>
													</div>
												</div>
											</li>

									</t:putAttribute>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>

	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#systemConfirm</th>
		        			<td colspan="2"><spring:message code="JIF.systemConfirm"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th></th>
		        		</tr>
-->
		        		<tr>
		        			<td class="example">
		        				<t:insertTemplate template="/WEB-INF/jsp/templates/systemConfirm.jsp">
								    <t:putAttribute name="messageContent">
											<spring:message code="JIF.labelmessage"/>
								    </t:putAttribute>								
								</t:insertTemplate>		        			
		        			</td>
		        		</tr>
	        		</tbody>

					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#standardConfirm</th>
		        			<td colspan="2"><spring:message code="JIF.standardConfirm"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td colspan="2" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/standardConfirm.jsp">
                                    <t:putAttribute name="bodyContent">
                                        <p class="message"><spring:message code="JIF.standardConfirmSampleOne"/></p>
                                        <p class="message"><spring:message code="JIF.standardConfirmSampleTwo"/></p>
                                    </t:putAttribute>
                                    <t:putAttribute name="okLabel" value="Yes"/>
                                    <t:putAttribute name="cancelLabel" value="No"/>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>
	        							<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#standardAlert</th>
		        			<td colspan="2"><spring:message code="JIF.standardAlert"/></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td colspan="2" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/standardAlert.jsp">
                                    <t:putAttribute name="bodyContent">
                                        <p class="message">The report cannot be rendered.</p>
                                    </t:putAttribute>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>

	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#loading</th>
		        			<td colspan="2"><spring:message code="JIF.loading"/></td>
		        		</tr>
		        		<tr>
		        			<td colspan="2" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/loading.jsp">
                                    <t:putAttribute name="containerID" value="testloading"/>
                                </t:insertTemplate>
		        			</td>
		        			
		        			<td colspan="2" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/loading.jsp">
                                    <t:putAttribute name="containerID" value="testLoadingCancellable"/>
                                	<t:putAttribute name="containerClass" value="cancellable"/>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#detail</th>
		        			<td colspan="2"><spring:message code="JIF.detail"/></td>
		        		</tr>
		        		<tr>
		        			<td colspan="2" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/detail.jsp">
                                    <t:putAttribute name="bodyContent">
                                        <div class="FPOonly c"></div>
                                    </t:putAttribute>
                                </t:insertTemplate>
		        			</td>
		        			
		        			<td colspan="2" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/detail.jsp">
                                	<t:putAttribute name="containerClass" value="sizeable"/>
                                	<t:putAttribute name="bodyContent">
                                        <div class="FPOonly c"></div>
                                    </t:putAttribute>
                                </t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>

<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#saveAs</th>
		        			<td colspan="2"><spring:message code="JIF.saveAs"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/saveAs.jsp">
									<t:putAttribute name="bodyContent" >
										<ul class="list collapsible folders">
											<li class="folders node open">
												<p class="wrap button draggable">
													<b class="icon"></b>Organization
												</p>
												<ul class="list collapsible">
													<li class="folders node open">
														<p class="wrap button draggable">
															<b class="icon"></b>Ad Hoc Components
														</p>
														<ul class="list collapsible">
															<li class="folders node closed">
																<p class="wrap button draggable">
																	<b class="icon"></b>Ad Hoc Reports
																</p>
															</li>
															<li class="folders node closed">
																<p class="wrap button draggable">
																	<b class="icon"></b>Topics
																</p>
															</li>
														</ul>
													</li>
													<li id="node14" class="folders node closed" tabindex="-1">
														<p id="anonymous_element_12" class="wrap button draggable">
															<b id="handler14" class="icon"></b>Dashboards
														</p>
													</li>
													<li id="node14" class="folders node closed" tabindex="-1">
														<p id="anonymous_element_12" class="wrap button draggable">
															<b id="handler14" class="icon"></b>Reports
														</p>
													</li>
												</ul>
											</li>
										</ul>
                                	 </t:putAttribute>
                                </t:insertTemplate>

		        			</td>

		        		</tr>
	        		</tbody>
<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#manageDataSource</th>
		        			<td colspan="2"><spring:message code="JIF.manageDataSource"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/manageDataSource.jsp">
                                	 <t:putAttribute name="bodyContent" >
										<ul class="list collapsible folders">
											<li class="folders node open">
												<p class="wrap button draggable">
													<b class="icon"></b>Organization
												</p>
												<ul class="list collapsible">
													<li class="folders node open">
														<p class="wrap button draggable">
															<b class="icon"></b>Analysis Components
														</p>
														<ul class="list collapsible">
															<li class="folders node open">
																<p class="wrap button draggable">
																	<b class="icon"></b>Analysis Data Sources
																</p>
																<ul id="node6sub" class="list collapsible">
																	<li id="node7" class="folders leaf" tabindex="-1">
																		<p id="anonymous_element_20" class="wrap button draggable">
																			<b id="handler7" class="icon"></b>Foodmart Data Source
																		</p>
																	</li>
																	<li id="node7" class="folders leaf" tabindex="-1">
																		<p id="anonymous_element_20" class="wrap button draggable">
																			<b id="handler7" class="icon"></b>Foodmart Data Source JNDI
																		</p>
																	</li>
																	<li id="node7" class="folders leaf" tabindex="-1">
																		<p id="anonymous_element_20" class="wrap button draggable">
																			<b id="handler7" class="icon"></b>SugarCRM Data Source
																		</p>
																	</li>
																	<li id="node7" class="folders leaf" tabindex="-1">
																		<p id="anonymous_element_20" class="wrap button draggable">
																			<b id="handler7" class="icon"></b>SugarCRM Data Source JNDI
																		</p>
																	</li>
																</ul>
																
															</li>
															<li class="folders node closed">
																<p class="wrap button draggable">
																	<b class="icon"></b>Topics
																</p>
															</li>
														</ul>
													</li>
													<li id="node14" class="folders node closed" tabindex="-1">
														<p id="anonymous_element_12" class="wrap button draggable">
															<b id="handler14" class="icon"></b>Data Sources
														</p>
													</li>
													<li id="node14" class="folders node closed" tabindex="-1">
														<p id="anonymous_element_12" class="wrap button draggable">
															<b id="handler14" class="icon"></b>Performance
														</p>
													</li>
												</ul>
											</li>
										</ul>
                                	 </t:putAttribute>
                                </t:insertTemplate>

		        			</td>

		        		</tr>
	        		</tbody>

					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#saveValues</th>
		        			<td colspan="2"><spring:message code="JIF.saveValues"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">
                            <t:insertTemplate template="/WEB-INF/jsp/templates/saveValues.jsp">
                            </t:insertTemplate>
							</td>
		        		</tr>
	        		</tbody>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#addFolder</th>
		        			<td colspan="2"><spring:message code="JIF.addFolder"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">

                            <t:insertTemplate template="/WEB-INF/jsp/templates/addFolder.jsp">
                            </t:insertTemplate>
							</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#selectFile</th>
		        			<td colspan="2"><spring:message code="JIF.selectFile"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">

                            <t:insertTemplate template="/WEB-INF/jsp/templates/selectFile.jsp">
                            	<t:putAttribute name="containerTitle">Add Security File</t:putAttribute>
                            </t:insertTemplate>
							</td>
		        		</tr>
	        		</tbody>
	        		<%-- 
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#selectPalette</th>
		        			<td colspan="2"><spring:message code="JIF.selectPalette"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">

                            <t:insertTemplate template="/WEB-INF/jsp/templates/selectPalette.jsp"></t:insertTemplate>
							</td>
		        		</tr>
	        		</tbody>
	        		--%>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#selectFromRepository</th>
		        			<td colspan="2"><spring:message code="JIF.selectFromRepository"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">

                            <t:insertTemplate template="/WEB-INF/jsp/templates/selectFromRepository.jsp">
                            	<t:putAttribute name="containerTitle">Select from the Repository</t:putAttribute>
                                <t:putAttribute name="bodyContent">
                                <ul class="list collapsible folders" id="addFileTreeRepoLocation" style="position:relative;">
                                	<li id="node2" class="folder node open" style="line-height:1.9em">
                                		<p class="wrap">
                                			<b class="icon" id="handler2"></b>
                                			Organization
                                		</p>
										<ul class="list collapsible" id="node2sub">
											<li id="node3" class="node closed ">
												<p class="wrap"><b class="icon" id="handler3"></b>Ad Hoc Components</p>
											</li>
											<li id="node4" class="node closed ">
												<p class="wrap"><b class="icon" id="handler4"></b>Analysis Components</p>
											</li>
			                                <li id="node5" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler5"></b>Content Files</p>
			                                </li>
			                                <li id="node6" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler6"></b>Dashboards</p>
			                                </li>
			                                <li id="node7" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler7"></b>Data Sources</p>
			                                </li>
			                                <li id="node9" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler9"></b>Domains</p>
			                                </li>
			                                <li id="node10" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler10"></b>Images</p>
			                                </li>
			                                <li id="node8" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler8"></b>Input Data Types</p>
			                                </li>
			                                <li id="node11" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler11"></b>Organizations</p>
			                                </li>
			                                <li id="node12" class="node closed ">
			                                    <p class="wrap"><b class="icon" id="handler12"></b>Reports</p>
			                                </li>
										</ul>
									</li>
									<li id="node13" class="node closed" style="line-height:1.9em">
										<p class="wrap"><b class="icon" id="handler13"></b>Public</p>
									</li>
								</ul>
                                </t:putAttribute>
                            </t:insertTemplate>
							</td>
		        		</tr>
	        		</tbody>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#aboutBox</th>
		        			<td colspan="2"><spring:message code="JIF.aboutBox"/></td>
		        		</tr>

		        		<tr>
		        			<td class="example">

                                <t:insertTemplate template="/WEB-INF/jsp/templates/aboutBox.jsp">
                                    <t:putAttribute name="containerID" value="testaboutBox"/>
                                    <t:putAttribute name="bodyContent">
                                        <p class="message">Product Version: <span class="emphasis">4.7.0</span></p>
                                        <p class="message">Build: <span class="emphasis">20120601_1045</span></p>
                                    </t:putAttribute>
                                </t:insertTemplate>
							</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#calcFieldMeasure</th>
		        			<td colspan="2">
                                <p><spring:message code="JIF.calculatedFields"/></p>
		        			</td>
		        		</tr>
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                    <t:putAttribute name="containerClass">panel dialog overlay </t:putAttribute>
                                    <t:putAttribute name="containerTitle" cascade="false">New Calculated Field</t:putAttribute>
                                    <t:putAttribute name="containerID" value="calcFieldMeasure" />
                                    <t:putAttribute name="headerClass" value="mover"/>
                                    <t:putAttribute name="bodyID" cascade="true">calculatedFields-container</t:putAttribute>
                                    <t:putAttribute name="bodyContent">

                                        <div class="section" id="measureNameSection">
                                            <div class="group noIndent">
                                                <label class="control input" for="fieldLabelInput" title="Field Name:">
                                                    <span class="wrap">Field Name:</span>
                                                    <input type="text" value="New Field" tabindex="1" id="fieldLabelInput">
                                                    <span data-field="fieldLabel" class="message warning">error message here</span>
                                                </label>
                                            </div>
                                        </div>

                                        <div class="section">
                                            <div class="content tabbed">
                                                <div class="header">
                                                    <ul class="control tabSet buttons horizontal " id="calcFieldMeasureTabs">
                                                        <li class="tab first selected" tabid="" id="formulaBuilderTab">
                                                            <a class="button">
                                                                <span class="wrap">Formula Builder</span>
                                                            </a>
                                                        </li>
                                                        <li class="tab" tabid="" id="summaryCalcTab">
                                                            <a disabled="disabled" class="button">
                                                                <span class="wrap">Summary Calculation</span>
                                                            </a>
                                                        </li>
                                                    </ul>
                                                </div>
                                                <div class="body section noTitle">

                                                    <div class="section noTitle " id="formulaBuilderSection">
                                                        <div class="group">
                                                            <label class="control shorter textArea" for="">
                                                                <span class="wrap">Formula:</span>
                                                                <textarea></textarea>
                                                            </label>
                                                        </div>
                                                        <div class="group inputSet">
                                                            <ul class="" id="expressionEditorOperators">
                                                                <li type="" name="" value="add" class="button operator"><span class="icon icon-add"></span></li>
                                                                <li type="" name="" value="subtract" class="button operator"><span class="icon icon-subtract"></span></li>
                                                                <li type="" name="" value="multiply" class="button operator"><span class="icon icon-multiply"></span></li>
                                                                <li type="" name="" value="divide" class="button operator"><span class="icon icon-divide"></span></li>
                                                                <li type="" name="" value="percent" class="button operator"><span class="icon icon-percent"></span></li>
                                                                <li type="" name="" value="parenLeft" class="button operator"><span class="icon icon-parenLeft"></span></li>
                                                                <li type="" name="" value="parenRight" class="button operator"><span class="icon icon-parenRight"></span></li>
                                                                <li type="" name="" value="colon" class="button operator"><span class="icon icon-colon"></span></li>
                                                                <li type="" name="" value="and" class="button operator"><span class="icon icon-and"></span></li>
                                                                <li type="" name="" value="or" class="button operator"><span class="icon icon-or"></span></li>
                                                                <li type="" name="" value="not" class="button operator"><span class="icon icon-not"></span></li>
                                                                <li type="" name="" value="in" class="button operator"><span class="icon icon-in"></span></li>
                                                                <li type="" name="" value="startsWith" class="button operator"><span class="icon icon-startsWith"></span></li>
                                                                <li type="" name="" value="endsWith" class="button operator"><span class="icon icon-endsWith"></span></li>
                                                                <li type="" name="" value="contains" class="button operator"><span class="icon icon-contains"></span></li>
                                                                <li type="" name="" value="equal" class="button operator"><span class="icon icon-equal"></span></li>
                                                                <li type="" name="" value="notEqual" class="button operator"><span class="icon icon-notEqual"></span></li>
                                                                <li type="" name="" value="greaterThan" class="button operator"><span class="icon icon-greaterThan"></span></li>
                                                                <li type="" name="" value="lessThan" class="button operator"><span class="icon icon-lessThan"></span></li>
                                                                <li type="" name="" value="greaterEqual" class="button operator"><span class="icon icon-greaterEqual"></span></li>
                                                                <li type="" name="" value="lessEqual" class="button operator"><span class="icon icon-lessEqual"></span></li>
                                                            </ul>
                                                        </div>
                                                        <ul class="list inputRow">
                                                            <!-- Available Fields -->
                                                            <li class="node">
                                                                <div class="group" id="expressionEditorFieldsList">
                                                                    <fieldset class="control standAlone pickList">
                                                                        <label class="control standAlone availableValues" for="" title="">
                                                                            <span class="wrap">Fields:</span>
                                                                            <ul class="list pickList">
                                                                                <li itemid="ShipAddress" class="node "><p class="wrap"><b class="icon field"></b>Address</p></li>
                                                                                <li itemid="ShipCity" class="node "><p class="wrap"><b class="icon field"></b>City</p></li>
                                                                                <li itemid="ShipCountry" class="node "><p class="wrap"><b class="icon field"></b>Country</p></li>
                                                                                <li itemid="CustomerID" class="node "><p class="wrap"><b class="icon field"></b>Customer ID</p></li>
                                                                                <li itemid="OrderDate" class="node "><p class="wrap"><b class="icon field"></b>Date ordered</p></li>
                                                                                <li itemid="RequiredDate" class="node "><p class="wrap"><b class="icon field"></b>Date required</p></li>
                                                                                <li itemid="ShippedDate" class="node "><p class="wrap"><b class="icon field"></b>Date shipped</p></li>
                                                                                <li itemid="EmployeeID" class="node "><p class="wrap"><b class="icon measure"></b>Employee ID</p></li>
                                                                                <li itemid="ShipName" class="node "><p class="wrap"><b class="icon field"></b>Name</p></li>
                                                                                <li itemid="OrderId" class="node "><p class="wrap"><b class="icon measure"></b>Order ID</p></li>
                                                                                <li itemid="ShipPostalCode" class="node "><p class="wrap"><b class="icon field"></b>Postal code</p></li>
                                                                                <li itemid="ShipRegion" class="node "><p class="wrap"><b class="icon field"></b>Region</p></li>
                                                                                <li itemid="ShipVia" class="node "><p class="wrap"><b class="icon measure"></b>Shipped via</p></li>
                                                                                <li itemid="Freight" class="node "><p class="wrap"><b class="icon measure"></b>Shipping charge</p></li>
                                                                            </ul>
                                                                        </label>
                                                                    </fieldset>
                                                                </div>
                                                            </li>

                                                            <!-- Functions -->
                                                            <li class="node">
                                                                <div class="group" id="expressionEditorFunctionsList">
                                                                    <fieldset class="control standAlone pickList">
                                                                        <label class="control standAlone availableValues" for="" title="">
                                                                            <span class="wrap">Functions:</span>
                                                                            <ul class="list pickList">
                                                                                <li itemid="startsWith" class="node "><p class="wrap"><b class="icon function"></b>startsWith</p></li>
                                                                                <li itemid="endsWith" class="node "><p class="wrap"><b class="icon function"></b>endsWith</p></li>
                                                                                <li itemid="contains" class="node "><p class="wrap"><b class="icon function"></b>contains</p></li>
                                                                                <li itemid="length" class="node "><p class="wrap"><b class="icon function"></b>length</p></li>
                                                                                <li itemid="isNull" class="node "><p class="wrap"><b class="icon function"></b>isNull</p></li>
                                                                                <li itemid="round" class="node "><p class="wrap"><b class="icon function"></b>round</p></li>
                                                                                <li itemid="abs" class="node "><p class="wrap"><b class="icon function"></b>abs</p></li>
                                                                                <li itemid="Mid" class="node "><p class="wrap"><b class="icon function"></b>Mid</p></li>
                                                                                <li itemid="least" class="node "><p class="wrap"><b class="icon function"></b>least</p></li>
                                                                                <li itemid="greatest" class="node "><p class="wrap"><b class="icon function"></b>greatest</p></li>
                                                                                <li itemid="Concatenate" class="node "><p class="wrap"><b class="icon function"></b>Concatenate</p></li>
                                                                                <li itemid="DayName" class="node "><p class="wrap"><b class="icon function"></b>DayName</p></li>
                                                                                <li itemid="Year" class="node "><p class="wrap"><b class="icon function"></b>Year</p></li>
                                                                            </ul>
                                                                        </label>
                                                                    </fieldset>
                                                                </div>
                                                            </li>

                                                            <!-- Function Description -->
                                                            <li class="node">
                                                                <div class="group">
                                                                    <span class="wrap label">Function Description:</span>
                                                                    <div class="description">
                                                                        <p> no description</p>
                                                                        <p> </p>
                                                                    </div>
                                                                </div>
                                                                <div class="group">
                                                                    <div class="control checkBox">
                                                                        <label class="wrap" for="showArgs" title="There must be a title">Show arguments in formula</label>
                                                                        <input type="checkbox" class="" id="showArgs">
                                                                    </div>
                                                                </div>
                                                            </li>
                                                        </ul>

                                                        <!-- Validate button -->
                                                        <div class="group" id="expressionEditorValidate">
                                                            <button class="button action" type="button">
                                                                <span class="wrap">Validate
                                                                    <span class="icon"></span>
                                                                </span>
                                                            </button>
                                                            <span class="message warning"></span>
                                                        </div>
                                                    </div>

                                                </div>
                                            </div>
                                        </div>
                                    </t:putAttribute>
                                    <t:putAttribute name="footerContent">
                                        <button class="button action primary up doneButton">
                                            <span class="wrap">Create Field</span><span class="icon"></span>
                                        </button>
                                        <button class="button action up cancelButton">
                                            <span class="wrap">Cancel<span class="icon"></span></span>
                                        </button>
                                    </t:putAttribute>
                                </t:insertTemplate>

		        			</td>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#addUser</th>
		        			<td colspan="2"><p><spring:message code="JIF.addUser"/></p></td>
		        		</tr>
		        		<!--
<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
-->
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/addUser.jsp"/>
		        			</td>

		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#addRole</th>
		        			<td colspan="2"><p><spring:message code="JIF.addRole"/></p></td>
		        		</tr>
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/addRole.jsp"/>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#uploadTheme</th>
		        			<td colspan="2"><p><spring:message code="JIF.uploadTheme"/></p></td>
		        		</tr>
		        		<tr>
		        			<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/uploadTheme.jsp"/>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#heartbeatOptin</th>
		        			<td colspan="2"><p><spring:message code="JIF.heartbeatOptin"/></p></td>
		        		</tr>
		        		<tr>
		        			<td class="example">
		        				<t:insertTemplate template="/WEB-INF/jsp/templates/heartbeatOptin.jsp">
		        					<t:putAttribute name="containerID" value="testheartbeatOptin"/>
		        				</t:insertTemplate>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">#generatorSelect</th>
		        			<td colspan="2"><p><spring:message code="JIF.generatorSelect"/></p></td>
		        		</tr>
		        		<tr>
		        			<td id="generatorSelect" class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/generatorSelect.jsp">
                                    <t:putAttribute name="containerID" value="testgeneratorSelect"/>
                                </t:insertTemplate>
                            </td>
		        		</tr>
	        		</tbody>
				 </table>
	        
			</t:putAttribute>
		    <t:putAttribute name="footerContent">
		    	<!-- custom content here; remove this comment -->
		    </t:putAttribute>
		</t:insertTemplate>		
		
		<t:insertTemplate template="/WEB-INF/jsp/modules/sample/sampleIndex.jsp"/>

    </t:putAttribute>
</t:insertTemplate>