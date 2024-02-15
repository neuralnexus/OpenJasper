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
    <t:putAttribute name="pageTitle" value="List Samples"/>
    <t:putAttribute name="bodyID" value="lists"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
    <t:putAttribute name="headerContent" >
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />
		<style type="text/css">

		</style>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" >
	
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlelists"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >
	        
				<table id="sampleGrid">
	        		<thead>
		        		<tr>
		        			<th class="rowHeader">.list</th>
		        			<th><spring:message code="JIF.titleexample"/></th>
		        		</tr>
	        		</thead>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.horizontal</th>
		        			<td><spring:message code="JIF.list.horizontal"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
	        		
	        					<ul class="list horizontal">
									<li>Item</li>
									<li>Item</li>
									<li>Item</li>
									<li class="last">Last Item</li>
								</ul>
								
	        				</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.decorated</th>
		        			<td><spring:message code="JIF.list.decorated"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
	        		
	        					<ul class="list decorated">
									<li>Download the <a class="emphasis" href="lists.jsp#">Evaluation Guide</a></li>
									<li>Visit our Service and <a class="emphasis" href="lists.jsp#">Support Portal</a> (login required)</li>
									<li>Browse our Online <a class="emphasis" href="lists.jsp#">Resource Center</a></li>
									<li>Sign up for <a class="emphasis" href="lists.jsp#">Online and Onsite Training</a></li>
								</ul>
								
	        				</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.stepIndicator</th>
		        			<td><spring:message code="JIF.list.stepIndicator"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
	        		
	        					<ul class="list stepIndicator">
									<li class="leaf"><p class="wrap button" href="#"><b class="icon"></b>Step 1</p></li>
									<li class="leaf"><p class="wrap button" href="#"><b class="icon"></b>Step 2</p>
										<ul class="node">
											<li class="leaf"><p class="wrap button" href="#"><b class="icon"></b>Step 2.a</p></li>
											<li class="leaf selected"><p class="wrap button" href="#"><b class="icon"></b>Step 2.b</p></li>
											<li class="leaf"><p class="wrap button" href="#"><b class="icon"></b>Step 2.c</p></li>
										</ul>
									</li>
									<li class="leaf"><p class="wrap button" href="#"><b class="icon"></b>Step 3</p></li>
									<li class="leaf"><p class="wrap button" href="#"><b class="icon"></b>Step 4</p></li>
								</ul>
								
	        				</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.list.filters</th>
		        			<td><spring:message code="JIF.list.filters"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example noPadding">
								<ul class="list filters">
									<li class="node open">
										<p class="wrap button offLeft header" href="#"><b class="icon"></b><spring:message code="optiongroup"/></p>
										<ul class="list filters">
											<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
											<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
											<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
											<li class="leaf selected"><p class="wrap button toggle down" href="#"><b class="icon"></b>Item</p></li>
											<li class="leaf"><p class="wrap button separator" href="#"><b class="icon"></b></p></li>
										</ul>
									</li>
									<li class="node open">
										<p class="wrap button offLeft header" href="#"><b class="icon"></b><spring:message code="optiongroup"/></p>
										<ul class="list filters">
											<li class="leaf selected"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
											<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
											<li class="node closed">
												<ul class="list filters">
													<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
													<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
													<li class="leaf"><p class="wrap button toggle up" href="#"><b class="icon"></b>Item</p></li>
													<li class="leaf"><p class="wrap button toggle down" href="#"><b class="icon"></b>Item</p></li>
												</ul>
												<p class="wrap button separator" href="#"><b class="icon"></b><a class="more launcher">More Choices&#8230;</a><a class="fewer launcher">Fewer Choices&#8230;</a></p>
											</li>
										</ul>
									</li>
								</ul>
	        				</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.collapsible.folders</th>
		        			<td><spring:message code="JIF.list.collapsible.folders"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
						     	<ul class="list collapsible folders hideRoot">
									<li id="node1" class="node open">
										<p class="wrap button">
											<b class="icon" id="handler1" title=""></b>root
										</p>
										<ul class="list collapsible" id="node1sub">
											<li id="node2" class="node open">
												<p class="wrap button">
													<b class="icon" id="handler2" title=""></b>Organization
												</p>
												<ul class="list collapsible" id="node2sub" style="">
													<li id="node5" class="node open">
														<p class="wrap button">
															<b class="icon" id="handler5" title=""></b>Folder Template
														</p>
														<ul class="list collapsible" id="node5sub" style="">
															<li id="node7" class="node closed">
																<p class="wrap button">
																	<b class="icon" id="handler7" title=""></b>Ad Hoc Components
																</p>
															</li>
															<li id="node8" class="node closed selected">
																<p class="wrap button">
																	<b class="icon" id="handler8" title=""></b>
																	<input type="text" value="Temp"/>
																</p>
															</li>
														</ul>
													</li>
													<li id="node6" class="node closed invalid">
														<p class="wrap button">
															<b class="icon" id="handler6" title=""></b>Organization
														</p>
													</li>
												</ul>
											</li>
											<li id="node3" class="node loading">
												<p class="wrap button">
													<b class="icon" id="handler3" title=""></b>Public
												</p>
											</li>
											<li id="node4" class="node closed disabled">
												<p class="wrap button">
													<b class="icon" id="handler4" title=""></b>Temp
												</p>
											</li>
										</ul>
									</li>
								</ul>
			        			
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.collapsible.type_tables</th>
		        			<td><spring:message code="JIF.list.collapsible.type_tables"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
						     	<ul class="list collapsible collapsible type_tables hideRoot">
									<li class="node open">
										<p class="wrap button"><b class="icon" title=""></b>root</p>
										<ul class="list collapsible">
											<li class="node open table constant">
												<p class="wrap button"><b class="icon" title=""></b>Constants</p>
												<ul class="list collapsible" id="node2sub" style="">
													<li class="leaf column calculated">
														<p class="wrap button"><b class="icon" title=""></b>calcField_1</p>
													</li>
													<li class="leaf column calculated">
														<p class="wrap button"><b class="icon" title=""></b>calcField_2</p>
													</li>
												</ul>
											</li>
										</ul>
										<ul class="list collapsible">
											<li class="node open dataIsland">
												<p class="wrap button"><b class="icon" title=""></b>Data Island</p>
												<ul class="list collapsible">
													<li class="leaf column calculated">
														<p class="wrap button"><b class="icon" title=""></b>calcField_3</p>
													</li>
													<li class="node table open">
														<p class="wrap button"><b class="icon" title=""></b>Table</p>
														<ul class="list collapsible">
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_1</p>
															</li>
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_2</p>
															</li>
														</ul>
													</li>
													<li class="node table closed">
														<p class="wrap button"><b class="icon" title=""></b>Table</p>
														<ul class="list collapsible">
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_1</p>
															</li>
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_2</p>
															</li>
														</ul>
													</li>
													<li class="node table derived open">
														<p class="wrap button"><b class="icon" title=""></b>Derived Table</p>
														<ul class="list collapsible">
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_1</p>
															</li>
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_2</p>
															</li>
														</ul>
													</li>
													<li class="node table derived closed">
														<p class="wrap button"><b class="icon" title=""></b>Derived Table</p>
														<ul class="list collapsible">
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_1</p>
															</li>
															<li class="leaf column">
																<p class="wrap button"><b class="icon" title=""></b>column_2</p>
															</li>
														</ul>
													</li>
												</ul>
											</li>
										</ul>
										<ul class="list collapsible">
											<li class="node closed dataIsland">
												<p class="wrap button"><b class="icon" title=""></b>Data Island</p>
											</li>
										</ul>
									</li>
								</ul>
							</li><!--/root-->
						</ul>
			        			
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.collapsible.type_sets</th>
		        			<td><spring:message code="JIF.list.collapsible.type_sets"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
						     	<ul class="list collapsible type_sets hideRoot">
									<li class="node open">
										<p class="wrap button"><b class="icon"></b>root</p>
										<ul class="list collapsible">
											<li class="node open set">
												<p class="wrap button"><b class="icon"></b>join_1</p>
												<ul class="list collapsible">
													<li class="node open set">
														<p class="wrap button"><b class="icon"></b>All Sales</p>
														<ul class="list collapsible">
															<li class="leaf item"><p class="wrap button"><b class="icon"></b>First Name</p></li>
															<li class="leaf item"><p class="wrap button"><b class="icon"></b>Last Name</p></li>
															<li class="leaf item"><p class="wrap button"><b class="icon"></b>Title</p></li>
															<li class="node open set"><p class="wrap button"><b class="icon"></b>Western Sales</p>
																<ul class="list collapsible">
																	<li class="leaf item"><p class="wrap button"><b class="icon"></b>First Name</p></li>
																	<li class="leaf item"><p class="wrap button"><b class="icon"></b>Last Name</p></li>
																	<li class="leaf item"><p class="wrap button"><b class="icon"></b>Title</p></li>
																</ul>	
															</li>
															<li class="node closed set"><p class="wrap button"><b class="icon"></b>Eastern Sales</p>
																<ul class="list collapsible">
																	<li class="leaf item"><p class="wrap button"><b class="icon"></b>First Name</p></li>
																	<li class="leaf item"><p class="wrap button"><b class="icon"></b>Last Name</p></li>
																	<li class="leaf item"><p class="wrap button"><b class="icon"></b>Title</p></li>
																</ul>	
															</li>
														</ul>	

													</li>
												</ul>
											</li>
											<li class="node closed set"><p class="wrap button"><b class="icon"></b>join_2</p></li>
										</ul>
									</li>
								</ul>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.collapsible.fields.twoColumn</th>
		        			<td><spring:message code="JIF.list.collapsible.fields.twoColumn"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
						     	<ul id="foldersTree" class="list collapsible fields fields tabular twoColumn">
									<li class="node open">
										<div class="wrap button header"><b class="icon" title=""></b><p class="column one">Source Name</p><p class="column two">Display Label</p></div>
										<ul class="list collapsible fields">
											<li class="node open">
												<div class="wrap button"><b class="icon"></b><p class="column one">Accounts</p><p class="column two">Account Name</p></div>
												<ul class="list collapsible fields">
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">Account Name</p><p class="column two">Account Name</p></div></li>
													<li class="leaf selected"><div class="wrap button"><b class="icon"></b><p class="column one">Account City</p><p class="column two"><input type="text" value="Account City"/></p></div></li>
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">Account State</p><p class="column two">Account Name</p></div></li>
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">Account Zip</p><p class="column two">Account Name</p></div></li>
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">Account Industry</p><p class="column two">Account Name</p></div></li>
												</ul>	
											</li>
											<li class="node closed"><div class="wrap button"><b class="icon"></b><p class="column one">Sales</p><p class="column two">Account Name</p></div>
												<ul class="list collapsible fields">
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">First Name</p><p class="column two">Account Name</p></div></li>
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">Last Name</p><p class="column two">Account Name</p></div></li>
													<li class="leaf"><div class="wrap button"><b class="icon"></b><p class="column one">Title</p><p class="column two">Account Name</p></div></li>
												</ul>	
											</li>
										</ul>
									</li>
								</ul>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.list.tabular</th>
		        			<td><spring:message code="JIF.list.tabular"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
						     	<ol class="list tabular">							
						<li class="leaf">
							<div class="wrap header">
								<div class="column one">
									Job ID
								</div>
								<div class="column two">
									Job Name
								</div>
								<div class="column three">
									Owner
								</div>
								<div class="column four">
									State
								</div>
								<div class="column five">
									Last Ran
								</div>
								<div class="column six">
									Next Run
								</div>
								<div class="column seven">
									
								</div>
							</div>
						</li>
						<li class="leaf">
							<div class="wrap">
								<div class="column one">
									4
								</div>
								<div class="column two">
									This is the name
								</div>
								<div class="column three">
									[owner name]|[org name]
								</div>
								<div class="column four">
									[state value]
								</div>
								<div class="column five">
									2010-06-14 10:50
								</div>
								<div class="column six">
									2010-06-28 10:50
								</div>
								<div class="column seven">
									<a class="launcher">Edit</a> | <a class="launcher">Remove</a>
								</div>
							</div>
						</li>
						<li class="leaf">
							<div class="wrap">
								<div class="column one">
									4
								</div>
								<div class="column two">
									This is rather somewhat longer name
								</div>
								<div class="column three">
									[owner name owner name]|[org name]
								</div>
								<div class="column four">
									[state value]
								</div>
								<div class="column five">
									2010-06-14 10:50
								</div>
								<div class="column six">
									2010-06-28 10:50
								</div>
								<div class="column seven">
									<a class="launcher">Edit</a> | <a class="launcher">Remove</a>
								</div>
							</div>
						</li>
					</ol>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.tabular.setLeft.threeColumn</th>
		        			<td><spring:message code="JIF.list.tabular.setLeft.threeColumn"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
						     	<ul id="foldersTree" class="list responsive setLeft tabular threeColumn">
									<li class="node">
										<div class="wrap button header">
											<b class="icon" title=""></b><p class="column one">Header Column 1</p><p class="column two">Header Column 2</p><p class="column three">Header Column 3</p></div>
										<ul class="list responsive collapsible">
											<li class="leaf">
												<div class="wrap button"><b class="icon" title=""></b><p class="column one">column 1</p><p class="column two">column 2</p><p class="column three">column 3</p></div>
											</li>
											<li class="leaf">
												<div class="wrap button"><b class="icon" title=""></b><p class="column one">column 1</p><p class="column two">column 2</p><p class="column three">column 3</p></div>
											</li>
											<li class="leaf">
												<div class="wrap button"><b class="icon" title=""></b><p class="column one">column 1</p><p class="column two">column 2</p><p class="column three">column 3</p></div>
											</li>
										</ul>
									</li>
								</ul>
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.tabular.fourColumn.resources</th>
		        			<td><spring:message code="JIF.list.tabular.fourColumn.resources"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
		        				<ol class="list tabular responsive collapsible resources fourColumn">
									<li class="leaf scheduled">
										<div class="wrap button button">
											<div class="column one">
												<div class="scheduled icon button noBubble"></div>
												<div class="disclosure icon button noBubble"></div>
											</div>
											<div class="column two">
												<h3 class="resourceName"><a>Accounts Report</a></h3>
												<p class="resourceDescription">All Accounts Report</p>
												<p class="resourcePath">/reports/samples</p>
											</div>
											<div class="column three">
												<spring:message code="Report"/>
											</div>
											<div class="column four">
												<p class="modifiedDate" title="Last modified: 2/25/10 8:17AM">Today</p>
											</div>
										</div>
									</li>
									
									<li class="node open">
										<div class="wrap button button">
											<div class="column one">
												<div class="scheduled icon button noBubble"></div>
												<div class="disclosure icon button noBubble"></div>	
											</div>
											<div class="column two">
												<h3 class="resourceName"><a>Cascading multi select example report</a></h3>
												<p class="resourceDescription">Example report with Cascading multi select input controls</p>
												<p class="resourcePath">/reports/samples</p>
											</div>
										
											<div class="column three">
												<spring:message code="Report"/>
											</div>
											<div class="column four">
												<p class="modifiedDate" title="Last modified: 2/25/10 8:17AM">Today</p>
											</div>
										</div>	
										<ol class="list tabular responsive resources twoColumn">
											<li class="leaf scheduled">
												<div class="wrap button button">
													<div class="column one">
														<div class="scheduled icon button noBubble"></div>
													</div>
													<div class="column two">
														<h4 class="resourceName"><a>Report Option One</a></h4>
														<p class="resourceDescription">Example report with Cascading multi select input controls</p>
													</div>
												</div>
											</li>
											<li class="leaf">
												<div class="wrap button button">
													<div class="column one">
														<div class="scheduled icon button noBubble"></div>
													</div>
													<div class="column two">
														<h4 class="resourceName"><a>Report Option Two</a></h4>
														<p class="resourceDescription">Example report with Cascading multi select input controls</p>
													</div>
												</div>
											</li>
										</ol>
									</li>
									
									<li class="leaf">
										<div class="wrap button button">
											<div class="column one">
												<div class="scheduled icon button noBubble"></div>
												<div class="disclosure icon button noBubble"></div>
											</div> 
											<div class="column two">
												<h3 class="resourceName"><a>Cascading multi select topic</a></h3>
												<p class="resourceDescription">Shows cascading input controls. Multi-select and single select queries</p>
												<p class="resourcePath">/adhoc/topics</p>
											</div>
											
											<div class="column three">
												<spring:message code="Report"/>
											</div>
											<div class="column four">
												<p class="modifiedDate" title="Last modified: 2/25/10 8:17AM">Today</p>
											</div>
										</div>	
									</li>
									
								</ol>						     	
						     	
		        			</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">.dragging</th>
		        			<td><spring:message code="JIF.list.dragging"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
								<div class="wrap dragging"><spring:message code="JIF.labelobjectname"/></div>
								
	        				</td>
		        		</tr>
	        		</tbody>
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader">dragMultiple.dragging</th>
		        			<td><spring:message code="JIF.list.dragMultiple.dragging"/></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
								<ul class="list dragMultiple dragging">		
									<li class="leaf"><div class="wrap"><spring:message code="JIF.labelobjectname"/></div></li>
									<li class="leaf"><div class="wrap"><spring:message code="JIF.labelobjectname"/></div></li>
									<li class="leaf"><div class="wrap"><spring:message code="JIF.labelobjectname"/></div></li>
								</ul>			
							</div>
								
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