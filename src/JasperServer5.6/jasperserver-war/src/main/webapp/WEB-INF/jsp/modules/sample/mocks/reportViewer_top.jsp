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
<%@ taglib uri="/spring" prefix="spring"%>
<!-- 
*** DEVELOPMENT NOTES ***

CHANGES WITH RESPECT TO 3.7 FUNCTIONALITY
1). 'Top of Page' option for input controls is now 'In Page' with appearance matching other column layout pages
2). Markup for all 3 input control display cases included in this mock
	- 'In Page' is the default
	- 'Pop-Up' is seen by changing class attribute of <body> element 'twoColumn' -> 'oneColumn'
	- 'Separate Page' is seen by changing class attribute of <body> element 'twoColumn' -> 'controlPage'
	
	Unless there is a good reason not to, recommend that production version of the cases be based 
	from the same .jsp, as this mock demonstrates, to make sure that appearances stay consistent over time.
3). The table representing the displayed report must have these class attributes 'report centered_horz'

PROVIDING FEEDBACK TO USER
  - Form submission buttons should be set to disabled="disabled" until onChange event occurs on an input
  - Form submission buttons should be again made disabled after a successful submit
  - No need to send message to #systemConfirm on successful submit as redraw of report is enough feedback
  - In cases that require inputs be submitted BEFORE generating report, add 'nothingToDisplay' to <body> class
  - In Pop-up input case, dialog is NOT modal, #pageDimmer should NOT be turned on

   
FINALLY
 Do not include these notes, or any HTML comment below that begins 'NOTE: ...' in the production page
-->



<%
	// This is only for mock! Enables report options list on Input Controls dialog.

    com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup option = new com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl();
    java.util.List list = new java.util.ArrayList();
    option.setURIString("/exp2006");
    option.setLabel("Expences over 2006");
    list.add(option);
	request.setAttribute("reportOptionsList",list);
	request.setAttribute("reportOptionsURI","/exp2006");
%>


<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="[report name]"/>
    <t:putAttribute name="bodyID" value="reportViewer"/>
    <t:putAttribute name="bodyClass" value="oneColumn"/>
	
		<t:putAttribute name="bodyContent" >
		
			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			    <t:putAttribute name="containerClass" value="column decorated primary showingToolBar"/>
			    <t:putAttribute name="containerTitle">Report Viewer</t:putAttribute>
			    <t:putAttribute name="headerContent">
			    	<div class="toolbar">		        					
						<ul class="list buttonSet">
							<li class="leaf"><button class="button capsule up first"><span class="wrap"><spring:message code="button.back"/><span class="icon"></span></span></button></li>
					<!-- 
					NOTE: 
					IMPORTANT: #controls is a toggle button in the case of top controls.
					This means it must have the 'down' attribute while the .pane.inputControls is displayed, 
					and 'up' when the controls are not displayed.
					When it has 'up' attribute, then add 'hidden' to .pane.inputControls
					
					#controls.up ==> .pane.inputControls.hidden
					#controls.down ==> .pane.inputControls
				    -->	
							
							<li id="controls" class="leaf"><button class="button capsule down toggle middle"><span class="wrap"><spring:message code="button.controls"/><span class="icon"></span></span></button></li>							
							<li class="leaf"><button class="button capsule mutton up last"><span class="wrap"><spring:message code="button.export"/><span class="icon"></span><span class="indicator"></span></span></button></li>
						</ul>
					</div>
					
			    </t:putAttribute>
			    <t:putAttribute name="bodyContent">
			    	<!-- 
					NOTE: this element is to be displayed in case when input values must be submitted before report can be displayed.
					It is turned on by adding the class 'nothingToDisplay' to the <body> element of the page
				    -->
					
	                <t:insertTemplate template="/WEB-INF/jsp/templates/nothingToDisplay.jsp">
						<t:putAttribute name="bodyContent">
							<p class="message">You must apply input values before the report can be displayed.</p>
					    </t:putAttribute>
					</t:insertTemplate>
					
					<!-- start controls panel -->
					
					<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			    		<t:putAttribute name="containerClass" value="panel pane inputControls"/>
			    		<t:putAttribute name="headerContent">
							<%--<label id="savedValuesSelector" class="control select inline" for="savedValues" title="Select saved values">
								<span class="wrap"><spring:message code="report.options.select.label"/></span>
			                    <select id="savedValues"  name="reportOptionsURI">
			                        <option selected="selected" value=""><spring:message code="report.options.select.empty.label"/></option>
			                    </select>
							</label>--%>
			    		</t:putAttribute>
			    		
			    		<t:putAttribute name="bodyContent">
			    			<!-- NOTE: this instance of .list.inputControls is just for mock -->
			    			<ul class="list inputControls">
						   		<li class="leaf">
						        	<label class="control select" for="" title="Filter value">
		       							<span class="wrap">Country:</span>
						                <select>
						                    <option selected="selected" title="Argentina" value="Argentina">
						                                Argentina</option>
						                    <option selected="selected" title="Austria" value="Austria">
						                                Austria</option>
						                    <option selected="selected" title="Belgium" value="Belgium">
						                                Belgium</option>
						                    <option selected="selected" title="Brazil" value="Brazil">
						                                Brazil</option>
						                    <option selected="selected" title="Canada" value="Canada">
						                                Canada</option>
						                    <option selected="selected" title="Denmark" value="Denmark">
						                                Denmark</option>
						                    <option selected="selected" title="Finland" value="Finland">
						                                Finland</option>
						                    <option selected="selected" title="France" value="France">
						                                France</option>
						                    <option selected="selected" title="Germany" value="Germany">
						                                Germany</option>
						                    <option selected="selected" title="Ireland" value="Ireland">
						                                Ireland</option>
						                    <option selected="selected" title="Italy" value="Italy">
						                                Italy</option>
						                    <option selected="selected" title="Mexico" value="Mexico">
						                                Mexico</option>
						                    <option selected="selected" title="Norway" value="Norway">
						                                Norway</option>
						                    <option selected="selected" title="Poland" value="Poland">
						                                Poland</option>
						                    <option selected="selected" title="Portugal" value="Portugal">
						                                Portugal</option>
						                    <option selected="selected" title="Spain" value="Spain">
						                                Spain</option>
						                    <option selected="selected" title="Sweden" value="Sweden">
						                                Sweden</option>
						                    <option selected="selected" title="Switzerland" value="Switzerland">
						                                Switzerland</option>
						                    <option selected="selected" title="UK" value="UK">
						                                UK</option>
						                    <option selected="selected" title="USA" value="USA">
						                                USA</option>
						                    <option selected="selected" title="Venezuela" value="Venezuela">
						                                Venezuela</option>
						                    </select>
						                    <span class="message warning">error message here</span>
						            	</label>
								</li>
								<li class="leaf">
						            <label class="control input text" for="" title="Filter value">
		       							<span class="wrap">Order ID:</span>
		        						<input class="" type="text" value=""/>
		        						<span class="message warning">error message here</span>
		   							</label>	
								</li>
								<li class="leaf">
					                <label class="control picker" for="input_2" title="Calendar picker">
	            						<span class="wrap">Date</span>
	            						<input class="" id="input_calendar" type="text" value=""/>
	            						<button class="button picker" id="input_calendar_button"></button>
	            						<span class="message warning">error message here</span>									
	                   				</label>
								</li>
							</ul> 
			    		
			    		</t:putAttribute>
			    		
			    		<t:putAttribute name="footerContent">
			    			<button id="apply" class="button action primary up"><span class="wrap"><spring:message code="button.apply" javaScriptEscape="true"/><span class="icon"></span></span></button>
	         				<button id="reset" class="button action up"><span class="wrap"><spring:message code="button.reset" javaScriptEscape="true"/><span class="icon"></span></span></button>
	         				<button id="save" class="button action up"><span class="wrap"><spring:message code="button.save" javaScriptEscape="true"/></span><span class="icon"></span></span></button>
			    		
			    		</t:putAttribute>
			    		
			    	</t:insertTemplate>
					<!-- end controls panel -->
					
					<!-- 
					NOTE: jsp:include below is for mock only; in production actual report is inserted here
				    -->
					 
					 <center>
					 	
<div id="reportContainer">
					 		<jsp:include page="jrTestReport_1.jsp"/>
					 	</div>

					 </center>
					 					 
			    </t:putAttribute>
			    <t:putAttribute name="footerContent">
						<div class="control paging centered_horz">
							<button class="button action square move toLeft up"><span class="wrap"><span class="icon"></span></span></button>
							<button class="button action square move left up"><span class="wrap"><span class="icon"></span></span></button>
							<label class="control input text inline" for="currentPage" title="current page">
       							<span class="wrap">Page</span>
        						<input class="" id="currentPage" type="text" value=""/>
        						<span class="wrap">of 31</span>
   							</label>
							<button class="button action square move right up"><span class="wrap"><span class="icon"></span></span></button>
							<button class="button action square move toRight up"><span class="wrap"><span class="icon"></span></span></button>
						</div>
			    </t:putAttribute>
			</t:insertTemplate>

	    </t:putAttribute>
		
</t:insertTemplate>
