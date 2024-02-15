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
    <t:putAttribute name="pageTitle" value="Control Samples"/>
    <t:putAttribute name="bodyID" value="controls"/>
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
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlecontrols"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >
	        
			<table id="sampleGrid">
				        		<thead>
					        		<tr>
					        			<th><spring:message code="JIF.titlecontrols"/></th>
					        			<th><spring:message code="JIF.titleexample"/></th>
					        		</tr>
				        		</thead>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader"><spring:message code="JIF.titlenoteoninputcontrols"/></th>
					        			<td>
					        				<spring:message code="JIF.note"/>
					        				<ul>
					        					<li><spring:message code="JIF.noteitem1"/></li>
					        					<li></li><spring:message code="JIF.noteitem2"/></li>
					        					<li><spring:message code="JIF.noteitem3"/></li>
					        					<li><spring:message code="JIF.noteitem4"/></li>
					        				</ul>
					        			</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.input.text</th>
					        			<td><spring:message code="JIF.control.input.text"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

				        					<label class="control input text" for="input_1" title="There must be a title">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						<input class="" id="input_1" type="text" value=""/>
                        						<span class="message warning">error message here</span>
                   							</label>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.input.file</th>
					        			<td><spring:message code="JIF.control.input.file"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

				        					<label class="control input file" for="input_2" title="There must be a title">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						<input class="" id="input_2" type="file" value=""/>
                        						<span class="message warning">error message here</span>
                   							</label>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.browser</th>
					        			<td><spring:message code="JIF.control.browser"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

				        					<label class="control browser" for="repoPath" title="Define a path in the repository">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						<input class="" id="repoPath" type="text" value=""/>
                        						<button class="button action" id="browser_button" type="button"><span class="wrap"><spring:message code="button.browse"/><span class="icon"></span></span></button>
                        						<span class="message warning">error message here</span>
                   							</label>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.picker</th>
					        			<td><spring:message code="JIF.control.picker"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

				        					<label class="control picker" for="input_2" title="Calendar picker">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						<span id="input_calendar_wrapper"></span>
                        						<span class="message warning">error message here</span>
                   							</label>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader"><spring:message code="JIF.titledynarchcalendarsample"/></th>
					        			<td><spring:message code="JIF.notedynarchcalendarsample"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td id="calendarSample" class="example">

				        					<t:insertTemplate template="/WEB-INF/jsp/modules/sample/calendarSample.jsp"/>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.input.password</th>
					        			<td><spring:message code="JIF.control.input.password"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

				        					<label class="control input password" for="input_3" title="There must be a title">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						<input class="" id="input_3" type="password" value=""/>
                        						<span class="message warning">error message here</span>
                   							</label>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.textArea</th>
					        			<td><spring:message code="JIF.control.textArea"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

				        					<label class="control textArea" for="input_8">
                        						<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/>:</span>
                        						<textarea id="input_8"/></textarea>
                        						<span class="message warning">error message here</span>
                    						</label>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.checkbox</th>
					        			<td><spring:message code="JIF.control.checkbox"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
                                                <div class="control checkBox">
                                                    <label class="wrap" for="input_4" title="There must be a title">
                                                        <spring:message code="JIF.labelvisiblelabel"/>
                                                    </label>
                                                    <input class="" id="input_4" type="checkbox" value=""/>
                                                </div>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.list.inputSet .control.checkbox</th>
					        			<td><spring:message code="JIF.list.inputSet.control.checkbox"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
				        					<ul class="list inputSet">
				        						<li class="leaf">
                                                    <div class="control checkBox">
                                                        <label class="wrap" for="input_11" title="There must be a title">
                                                            <spring:message code="JIF.labelvisiblelabel"/>
                                                        </label>
                                                        <input class="" id="input_11" type="checkbox" value=""/>
                                                    </div>
				        						</li>
				        						<li class="leaf">
                                                    <div class="control checkBox">
                                                        <label class="wrap" for="input_5" title="There must be a title">
                                                            <spring:message code="JIF.labelvisiblelabel"/>
                                                        </label>
                                                        <input class="" id="input_5" type="checkbox" value=""/>
                                                    </div>
				        						</li>
				        					</ul>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.list.inputSet .control.radio</th>
					        			<td><spring:message code="JIF.list.inputSet.control.radio"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
											<ul class="list inputSet">
				        						<li class="leaf">
                                                    <div class="control radio">
                                                        <label class="wrap" for="input_6" title="There must be a title">
                                                            <spring:message code="JIF.labelvisiblelabel"/>
                                                        </label>
                                                        <input class="" id="input_6" type="radio" name="sample" value=""/>
                                                    </div>
	                   							</li>
	                   							<li class="leaf">
                                                    <div class="control radio">
                                                        <label class="wrap" for="input_7" title="There must be a title">
                                                            <spring:message code="JIF.labelvisiblelabel"/>
                                                        </label>
                                                        <input class="" id="input_7" type="radio" name="sample" value=""/>
                                                    </div>
	                   							</li>
											</ul>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.select</th>
					        			<td><spring:message code="JIF.control.select"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
				        					<label class="control select" for="input_9" title="There must be a title">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						 <select id="input_9" name="">
                        							<option value="">Option 1</option>
                        							<option value="">Option 2</option>
                        							<option value="">Option 3</option>
                        						</select>
                        						<span class="message warning">error message here</span>
                   							</label>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.select.multiple</th>
					        			<td><spring:message code="JIF.control.select.multiple"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
				        					<label class="control select multiple" for="input_10" title="There must be a title">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						 <select id="input_10" name="" multiple="multiple">
                        							<option selected="selected" value="">-- <spring:message code="dialog.file.selectType"/> --</option>
                        							<option value="">Option 1</option>
                        							<option value="">Option 2</option>
                        							<option value="">Option 3</option>
                        						 </select>
                        						 <span class="message warning">error message here</span>
                   							</label>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.pickWells</th>
					        			<td><spring:message code="JIF.control.pickWells"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
				        					<fieldset class="control pickWells">
												<div class="control combo availableValues" title="There must be a title">
	                       							<span class="wrap"><spring:message code="JIF.labelavailablevalues"/>:</span>
	                        						<t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
												    </t:insertTemplate>
	                        						 <select multiple class="">
	                        							<option  title="Austria" value="Austria">Austria</option>
													    <option  title="Belgium" value="Belgium">Belgium</option>
													    <option  title="Brazil" value="Brazil">Brazil</option>
	                        						</select>
	                        						<span class="message warning">error message here</span>
	                   							</div>
												<label class="control select multiple selectedValues" for="" title="Selected Values">
													<span class="wrap"><spring:message code="JIF.labelselectedvalues"/>:</span>
													<select multiple="multiple">
													    
													</select>
													<span class="message warning">error message here</span>
												</label>
											</fieldset>
				        				</td>
					        		</tr>
				        		</tbody>
				        		
				        		
				        		
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.combo</th>
					        			<td><spring:message code="JIF.control.combo"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
				        					<div class="control combo" title="There must be a title">
                       							<span class="wrap"><spring:message code="JIF.labelvisiblelabel"/></span>
                        						<t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
											    </t:insertTemplate>
                        						 <select class="">
                        							<option value="">Option 1</option>
                        							<option value="">Option 2</option>
                        							<option value="">Option 3</option>
                        							<option selected="selected" class="empty" title="null" value="null"></option>
                        						</select>
                        						<span class="message warning">error message here</span>
                   							</div>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.path</th>
					        			<td><spring:message code="JIF.control.path"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
				        					<ul id="filterPath" class="control path">
				        						<li class="label"><p class="wrap"><spring:message code="JIF.labeloptionallabel"/></p></li>
												<li class="step"><p class="wrap">All</p></li>
												<li class="step last"><p class="wrap">Reports</p></li>
											</ul>
				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.tab.text.horizontal.responsive</th>
					        			<td><spring:message code="JIF.tab.text.horizontal.responsive"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">												
	                                            <ul class="tabSet control text horizontal responsive">
													<li class="label"><p class="wrap">Sort by:</p></li>
													<li class="tab selected"><p class="wrap button">Name</p></li>
													<li  class="tab"><p class="wrap button">Type</p></li>
													<li class="tab"><p class="wrap button">Modified Date</p></li>
													<li class="tab last"><p class="wrap button">Relevance</p></li>
												</ul>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.tabs.horizontal</th>
					        			<td><spring:message code="JIF.tabs.horizontal"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
											
                                            <div class="tabs">
											<t:insertTemplate template="/WEB-INF/jsp/templates/control_tabSet.jsp">
                                                <t:putAttribute name="type" value="buttons"/>
                                                <t:putAttribute name="containerClass" value="horizontal"/>
                                                <t:putListAttribute name="tabset">
                                                    <t:addListAttribute>
                                                        <t:addAttribute>option_1</t:addAttribute>
                                                        <t:addAttribute>Option 1</t:addAttribute>
                                                    </t:addListAttribute>
                                                    <t:addListAttribute>
                                                        <t:addAttribute>option_2</t:addAttribute>
                                                        <t:addAttribute>Option 2</t:addAttribute>
                                                        <t:addAttribute>selected</t:addAttribute>
                                                    </t:addListAttribute>
                                                    <t:addListAttribute>
                                                        <t:addAttribute>option_3</t:addAttribute>
                                                        <t:addAttribute>Option 3</t:addAttribute>
                                                    </t:addListAttribute>
                                                </t:putListAttribute>
                                            </t:insertTemplate>
                                            </div>

				        				</td>
					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.tabs.vertical</th>
					        			<td><spring:message code="JIF.tabs.vertical"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
                                            <t:insertTemplate template="/WEB-INF/jsp/templates/control_tabSet.jsp">
                                                <t:putAttribute name="type" value="buttons"/>
                                                <t:putAttribute name="containerClass" value="vertical"/>
                                                <t:putListAttribute name="tabset">
                                                    <t:addListAttribute>
                                                        <t:addAttribute>source</t:addAttribute>
                                                        <t:addAttribute>Source</t:addAttribute>
                                                        <t:addAttribute>selected</t:addAttribute>
                                                    </t:addListAttribute>
                                                    <t:addListAttribute>
                                                        <t:addAttribute>fields</t:addAttribute>
                                                        <t:addAttribute>Fields</t:addAttribute>
                                                    </t:addListAttribute>
                                                    <t:addListAttribute>
                                                        <t:addAttribute>filters</t:addAttribute>
                                                        <t:addAttribute>Filters</t:addAttribute>
                                                    </t:addListAttribute>
                                                    <t:addListAttribute>
                                                        <t:addAttribute>labels</t:addAttribute>
                                                        <t:addAttribute>Labels</t:addAttribute>
                                                    </t:addListAttribute>
                                                    <t:addListAttribute>
                                                        <t:addAttribute>save</t:addAttribute>
                                                        <t:addAttribute>Save</t:addAttribute>
                                                    </t:addListAttribute>
                                                </t:putListAttribute>
                                            </t:insertTemplate>

					        			</td>
					        		</tr>
				        		</tbody>

				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.searchLockup</th>
					        			<td><spring:message code="JIF.searchLockup"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">
											<t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
											    </t:insertTemplate>
				        					<!--
<form class="control searchLockup">
				        						<label for="searchInput" class="offLeft">Search</label>
												<input class="" id="searchInput"/>
												<b class="right"><button class="button searchClear"></button></b>
												<button class="button search up"></button>
											</form>
-->

				        				</td>
					        		</tr>
				        		</tbody>

				        		<tbody>
					        		<tr>
					        			<th class="rowHeader" rowspan="2">.groupBox</th>
					        			<td colspan="2"><spring:message code="JIF.groupBox"/></td>
					        		</tr>
					        		<tr>
					        			<th>[default]</th>
					        			<th>.fillParent</th>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">        					
                                            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
												<t:putAttribute name="containerClass" value="control groupBox"/>
                                                <t:putAttribute name="bodyContent">
                                                    <div class="FPOonly c"></div>
                                                </t:putAttribute>
                                            </t:insertTemplate>
					        			</td>
					        			<td class="example">        					

                                            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
												<t:putAttribute name="containerClass" value="control groupBox fillParent"/>
                                                <t:putAttribute name="bodyContent">
                                                    <div class="FPOonly c"></div>
                                                </t:putAttribute>
                                            </t:insertTemplate>
					        			</td>

					        		</tr>
				        		</tbody>
				        		<tbody>
					        		<tr>
					        			<th class="rowHeader">.control.paging</th>
					        			<td><spring:message code="JIF.control.paging"/></td>
					        		</tr>
					        		<tr>
					        			<th class="rowHeader"></th>
					        			<td class="example">

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

<!--<script>-->
	<!--//look at me breaking all the rules - but we need to test calendar -->
    <!--jQuery("#input_calendar").datetimepicker({-->
        <!--dateFormat: 'yy-mm-dd',-->
        <!--showOn: "button",-->
        <!--buttonText:"",-->
        <!--changeYear:true,-->
        <!--changeMonth: true,-->
        <!--showButtonPanel: true-->
    <!--}).next().addClass('button').addClass('picker');-->
    <!--// Prototype.js compatibility-->
    <!--jQuery('#${id}')[0].getValue = function(){return jQuery(this).val()}-->
<!--</script>-->

    </t:putAttribute>

</t:insertTemplate>

	
			  

