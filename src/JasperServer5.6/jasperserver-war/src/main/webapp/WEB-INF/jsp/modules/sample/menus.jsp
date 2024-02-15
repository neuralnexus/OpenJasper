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
    <t:putAttribute name="pageTitle" value="Menu Samples"/>
    <t:putAttribute name="bodyID" value="menus"/>
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
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlemenus"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >
	        
			<table id="sampleGrid">
        		<thead>
	        		<tr>
	        			<th>.menu</th>
	        			<th><spring:message code="JIF.titleexample"/></th>
	        		</tr>
        		</thead>
        		<tbody>
	        		<tr>
	        			<th class="rowHeader">.vertical.context</th>
	        			<td><spring:message code="JIF.menus.vertical.context"/></td>
	        		</tr>
	        		<tr>
	        			<th class="rowHeader"></th>
	        			<td class="example">
        					<div class="menu vertical context">
						    <t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
								<ul class="content">
									<li class="leaf">
										<p class="wrap button"><span class="icon"></span>Add Folder&#8230;</p>
									</li>
									<li class="node open">
										<p class="wrap button"><span class="icon"></span>Add Resource</p>
										<div class="sub menu vertical context">
									    	<t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
											<ul class="content">
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
												<li class="node closed"><p class="wrap button"><span class="icon"></span>Item</p>
													<div class="sub menu vertical context">
												    	<t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
														<ul class="content">
															<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
															<li class="node open"><p class="wrap button"><span class="icon"></span>Item</p>
																<div class="sub menu vertical context">
															    	<t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
																	<ul class="content">
																		<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
																		<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
																		<li class="leaf separator"></li>
																		<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
																		<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
																	</ul><!-- /content -->	   	 
																</div><!-- /menu -->
															</li>
															<li class="leaf separator"></li>
															<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
															<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
														</ul><!-- /content -->	   	 
													</div><!-- /menu -->
												</li>
												<li class="leaf separator"></li>
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
											</ul><!-- /content -->	   	 
										</div><!-- /menu -->
									</li>
									<li class="node closed">
										<p class="wrap button"><span class="icon"></span>Add Something Else</p>
										<div class="sub menu vertical context">
									    	<t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
											<ul class="content">
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
												<li class="leaf separator"></li>
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
												<li class="leaf"><p class="wrap button"><span class="icon"></span>Item</p></li>
											</ul><!-- /content -->	   	 
										</div><!-- /menu -->
									</li>
									<li class="leaf separator"></li>
									<li class="leaf"><p class="wrap button"><span class="icon"></span>Copy</p></li>
									<li class="leaf"><p class="wrap button"><span class="icon"></span>Cut</p></li>
									<li class="leaf"><p class="wrap button"><span class="icon"></span>Delete</p></li>
									<li class="leaf separator"></li>
									<li class="leaf requiresInput up"><p class="wrap button"><span class="icon"></span>Properties&#8230;</p></li>
									<li class="leaf requiresInput up"><p class="wrap button"><span class="icon"></span>Permissions&#8230;</p></li>
								</ul><!-- /content -->	   	 
							</div><!-- /menu -->			        			
	        			</td>
	        		</tr>
        		</tbody>

        		<tbody>
	        		<tr>
	        			<th class="rowHeader">.vertical.dropDown</th>
	        			<td><spring:message code="JIF.menus.vertical.dropDown"/></td>
	        		</tr>
	        		<tr>
	        			<th class="rowHeader"></th>
	        			<td class="example">
        					<div class="menu vertical dropDown">
						    	<t:insertTemplate template="/WEB-INF/jsp/templates/utility_cosmetic.jsp"/>
								<ul class="content">
									<li class="leaf"><p class="wrap button toggle down"><span class="icon"></span>Filter Pane</p></li>
									<li class="leaf"><p class="wrap button toggle"><span class="icon"></span>Title</p></li>
									<li class="leaf separator"></li>
									<li class="leaf"><p class="wrap button toggle down"><span class="icon"></span>Portrait</p></li>
									<li class="leaf"><p class="wrap button toggle"><span class="icon"></span>Landscape</p></li>
									<li class="leaf separator"></li>
									<li class="leaf"><p class="wrap button toggle down"><span class="icon"></span>Letter Size</p></li>
									<li class="leaf"><p class="wrap button toggle"><span class="icon"></span>A4 Size</p></li>
									<li class="leaf"><p class="wrap button toggle"><span class="icon"></span>Actual Size</p></li>
								</ul><!-- /content -->	   	 
							</div><!-- /menu -->			        			
	        			</td>
	        		</tr>
        		</tbody>

        		<tbody>
	        		<tr>
	        			<th class="rowHeader">.horizontal.primaryNav</th>
	        			<td><spring:message code="JIF.menus.horizontal.primaryNav"/></td>
	        		</tr>
	        		<tr>
	        			<th class="rowHeader"></th>
	        			<td class="example horizontalNav">	
							<div class="header">
								<div class="menu horizontal primaryNav">
									<ul>
										<li id="main_home" class="leaf"><p class="wrap button"><span class="icon"></span>Home</p></li>
										<li id="main_library" class="node"><p class="wrap button">Library</p></li>
										<li id="main_view" class="node"><p class="wrap button"><span class="icon"></span>View</p></li>
										<li id="main_manage" class="node"><p class="wrap button"><span class="icon"></span>Manage</p></li>
										<li id="main_create" class="node"><p class="wrap button"><span class="icon"></span>Create</p></li>
									</ul>
								</div>
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