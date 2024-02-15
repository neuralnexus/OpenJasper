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

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Panel Samples"/>
    <t:putAttribute name="bodyID" value="panels"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
    <t:putAttribute name="headerContent" >
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />
		<style type="text/css">
            #sampleGrid .rowHeader {width:3%;}
            #sampleGrid .example {width:40%;padding:0;}
        </style>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" >
	
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlepanels"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >

	        	<table id="sampleGrid">
	        		<thead>
		        		<tr>
		        			<td class="rowHeader"></td>
		        			<td class="example"></td>
		        			<td class="example"></td>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader">.panel</th>
		        			<th colspan="2" class="example"><spring:message code="JIF.titlegeneralpaneltypes"/></th>
		        		</tr>
		        	
	        		</thead>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">.tooltip</th>
		        			<td colspan="2"><spring:message code="JIF.tooltip"/></td>
		        		</tr>
		        		<tr>
		        			<th>[default]</th>
		        			<th></th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
		        				        					
								<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
								    <t:putAttribute name="containerClass" value="panel info tooltip"/>
								    <t:putAttribute name="bodyContent">
										<p class="message label"><spring:message code="JIF.label"/></p>
										<p class="message">The detailed info</p>
								    </t:putAttribute>
								</t:insertTemplate>

		        			</td>
		        			
		        			<td class="example">
		        				
		        			</td>

		        		</tr>
	        		</tbody>
					<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">.info</th>
		        			<td colspan="2"><spring:message code="JIF.info"/></td>
		        		</tr>
		        		<tr>
		        			<th>[default]</th>
		        			<th>.fillParent</th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
		        				        					
								<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
									<t:putAttribute name="containerClass" value="panel info"/>
								    <t:putAttribute name="bodyContent">
										<div class="FPOonly c"></div>
								    </t:putAttribute>
								</t:insertTemplate>

		        			</td>
		        			
		        			<td class="example">
		        				<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
									<t:putAttribute name="containerClass" value="panel info fillParent"/>
								    <t:putAttribute name="bodyContent">
										<div class="FPOonly c"></div>
								    </t:putAttribute>
								</t:insertTemplate>
		        			</td>

		        		</tr>
	        		</tbody>
	        			        		
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">.pane</th>
		        			<td colspan="2"><spring:message code="JIF.pane"/></td>
		        		</tr>
		        		<tr>
		        			<th>[default]</th>
		        			<th>.sizeable</th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
	                            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
	                            	<t:putAttribute name="containerClass" value="panel pane"/>	                            	
	                                <t:putAttribute name="containerTitle" value="Pane Title"/>
	                                <t:putAttribute name="bodyContent">
	                                    <div class="FPOonly c"></div>
	                                </t:putAttribute>
	                            </t:insertTemplate>
		        			</td>
		        			<td class="example">
	                            <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
	                                <t:putAttribute name="containerClass" value="panel pane sizeable"/>
	                                <t:putAttribute name="containerTitle" value="Pane Title"/>
	                                <t:putAttribute name="containerElements">
	                            		<div class="sizer vertical"></div>
	                            	</t:putAttribute>
	                                <t:putAttribute name="containerTitle" value="Pane Title"/>
	                                <t:putAttribute name="bodyContent">
	                                    <div class="FPOonly c"></div>
	                                </t:putAttribute>
	                            </t:insertTemplate>
		        			</td>
		        		</tr>
	        		</tbody>

	        		
	        		<tbody>
		        		<tr>
		        			<th class="rowHeader" rowspan="2">.pane.filter</th>
		        			<td colspan="2"><spring:message code="JIF.pane.filter"/>
		        		</tr>
		        		<tr>
		        			<th>[default]</th>
		        			<th>.sizeable</th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
		        			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                <t:putAttribute name="containerClass" value="panel pane filter"/>
                                <t:putAttribute name="headerContent">
                            		<div class="button disclosure noBubble"></div>
                            		<b class="mutton"></b>
                            	</t:putAttribute>
						    	<t:putAttribute name="containerTitle" value="Country"/>  
							    <t:putAttribute name="bodyContent">
									 <fieldset class="options">
							            <legend class="offLeft"><span>Condition</span></legend>
							            <select class="">
							                <option selected="selected" value="in">is one of</option>
							                    <option value="notin">is not one of</option>
							                    <option value="equals">equals</option>
							                    <option value="notEqual">is not equal to</option>
							                    <option value="contains">contains</option>
							                    <option value="notcontains">does not contain</option>
							                    <option value="startsWith">starts with</option>
							                    <option value="notstartsWith">does not start with</option>
							                    <option value="endsWith">ends with</option>
							                    <option value="notendsWith">does not end with</option>
							                </select>
							        </fieldset>
							
							        <fieldset class="values">
							            <input type="text" class="single-input">
							        </fieldset>
							        <fieldset class="all hidden">
							            <legend class="offLeft"><span>Reset Filter to All Values</span></legend>
							            <input type="checkbox" checked="checked" class="options up" value="All" name="ShipCountryPodfilter_2_All">
							            <label for="ShipCountryPodfilter_2_allLabel">
							                All</label>
							        </fieldset>	
							    </t:putAttribute>
							</t:insertTemplate>
		        			</td>
		        			<td  class="example">
		        				<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                <t:putAttribute name="containerClass" value="panel pane filter sizeable"/>
                                <t:putAttribute name="headerContent">
                            		<div class="button disclosure noBubble"></div>
                            		<div class="sizer vertical"></div>
                            		<b class="mutton"></b>
                            	</t:putAttribute>
						    	<t:putAttribute name="containerTitle" value="Country"/> 
                                    <t:putAttribute name="bodyContent">
                                        <fieldset id="filter-container">
								            <fieldset class="options">
								            <legend class="offLeft"><span>Condition</span></legend>
								            <select class="">
								                <option selected="selected" value="in">is one of</option>
								                    <option value="notin">is not one of</option>
								                    <option value="equals">equals</option>
								                    <option value="notEqual">is not equal to</option>
								                    <option value="contains">contains</option>
								                    <option value="notcontains">does not contain</option>
								                    <option value="startsWith">starts with</option>
								                    <option value="notstartsWith">does not start with</option>
								                    <option value="endsWith">ends with</option>
								                    <option value="notendsWith">does not end with</option>
								                </select>
								        </fieldset>
								
								        <fieldset class="values" id="ShipCountryPodfilter_2_filterInputContainer">
								                <select multiple="multiple">
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
								            </fieldset>
								        <fieldset class="all" id="ShipCountryPodfilter_2_allOption">
								            <legend class="offLeft"><span>Reset Filter to All Values</span></legend>
								            <input type="checkbox" checked="checked" class="options up" id="ShipCountryPodfilter_2_all" value="All" name="ShipCountryPodfilter_2_All">
								            <label for="ShipCountryPodfilter_2_allLabel">
								                All</label>
								        </fieldset>
                                    </t:putAttribute>
                                </t:insertTemplate>
		        			</td>
		        		</tr>
	        		</tbody>


					<tbody>
						<tr>
							<th class="rowHeader" rowspan="2">.dialog.inlay</th>
							<td colspan="2"><spring:message code="JIF.dialog.inlay"/></td>
						</tr>
						<tr>
		        			<th>[default]</th>
		        			<th>.dialog.inlay.fillparent</th>
		        		</tr>
						<tr>
							<th class="rowHeader"></th>
							<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                	<t:putAttribute name="containerClass" value="panel dialog inlay"/>
                                    <t:putAttribute name="containerTitle" value="Properties" />
                                    <t:putAttribute name="bodyContent">
                                       <div class="FPOonly c"></div>
                                    </t:putAttribute>
                                    <t:putAttribute name="footerContent">
                                    	<div class="cosmetic left"></div>
                                    	<div class="cosmetic right"></div>
                                    </t:putAttribute>
                                </t:insertTemplate>
							</td>
							<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                	<t:putAttribute name="containerClass" value="panel dialog inlay fillParent"/>
                                    <t:putAttribute name="containerTitle" value="Properties" />
                                    <t:putAttribute name="bodyContent">
                                        <div class="FPOonly c"></div>
                                    </t:putAttribute>
                                    <t:putAttribute name="footerContent">
                                    	<div class="cosmetic left"></div>
                                    	<div class="cosmetic right"></div>
                                    </t:putAttribute>
                                </t:insertTemplate>
							</td>
						</tr>
					</tbody>
					<tbody>
						<tr>
							<th class="rowHeader" rowspan="2">.dialog.inlay.filter</th>
							<td colspan="2"><spring:message code="JIF.dialog.inlay.filter"/></td>
						</tr>
						<tr>
		        			<th>[default]</th>
		        			<th>.dialog.inlay.fillparent</th>
		        		</tr>
						<tr>
							<th class="rowHeader"></th>
							<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
												    <t:putAttribute name="containerID" value="[fieldName]_filter"/>											
												    <t:putAttribute name="containerClass" value="panel dialog inlay filter noHeader"/>
												    <t:putAttribute name="bodyContent">
												    <t:putAttribute name="bodyClass" value=""/>
														    	<fieldset class="column one">
															    	<span class="fieldName">Country</span>
																	<label class="control select inline" for="" title="Filter operation">
																		<span class="wrap offLeft">Filter Operation:</span>
																		<select>
																		    <option selected="selected" value="in">is one of</option>
																		    <option value="notin">is not one of</option>
																		    <option value="equals">equals</option>
																		    <option value="notEqual">is not equal to</option>
																		    <option value="contains">contains</option>
																		    <option value="notcontains">does not contain</option>
																		    <option value="startsWith">starts with</option>
																		    <option value="notstartsWith">does not start with</option>
																		    <option value="endsWith">ends with</option>
																		    <option value="notendsWith">does not end with</option>
																		</select>
																		<span class="message warning">error message here</span>
																	</label>
																</fieldset>
																<fieldset class="column two control pickWells">
																	<div class="control combo availableValues" title="Available Values">
						                       							<span class="wrap"><spring:message code="JIF.labelavailablevalues"/>:</span>
						                        						<t:insertTemplate template="/WEB-INF/jsp/templates/control_searchLockup.jsp">
																	    </t:insertTemplate>
						                        						 <select multiple class="">
						                        							 <option selected="selected" title="Argentina" value="Argentina">Argentina</option>
																		    <option  title="Austria" value="Austria">Austria</option>
																		    <option  title="Belgium" value="Belgium">Belgium</option>
																		    <option  title="Brazil" value="Brazil">Brazil</option>
																		    <option  title="Canada" value="Canada">Canada</option>
																		    <option  title="Denmark" value="Denmark">Denmark</option>
																		    <option  title="Finland" value="Finland">Finland</option>
																		    <option  title="France" value="France">France</option>
																		    <option  title="Germany" value="Germany">Germany</option>
																		    <option  title="Ireland" value="Ireland">Ireland</option>
																		    <option  title="Italy" value="Italy">Italy</option>
																		    <option  title="Mexico" value="Mexico">Mexico</option>
																		    <option  title="Norway" value="Norway">Norway</option>
																		    <option  title="Poland" value="Poland">Poland</option>
																		    <option  title="Portugal" value="Portugal">Portugal</option>
																		    <option  title="Spain" value="Spain">Spain</option>
																		    <option  title="Sweden" value="Sweden">Sweden</option>
																		    <option  title="Switzerland" value="Switzerland">Switzerland</option>
																		    <option  title="UK" value="UK">UK</option>
																		    <option  title="USA" value="USA">USA</option>
																		    <option  title="Venezuela" value="Venezuela">Venezuela</option>
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
																</t:putAttribute>
												    <t:putAttribute name="footerContent">
														<button class="button action primary up">
												    		<span class="wrap"><spring:message code="button.ok"/></span>
												    		<span class="icon"></span>
												    	</button>
            											<button class="button action up">
            												<span class="wrap"><spring:message code="button.cancel"/>
            												<span class="icon"></span>
            											</button>
            											<label class="control checkBox lock" for="editable" title="Select to prevent the values from being changed">
		                       								<span class="wrap">Locked</span>
		                        							<input id="editable" type="checkbox"/>
		                   								</label>
					                                    <div class="cosmetic left"></div>
					                                    <div class="cosmetic right"></div>
												    </t:putAttribute>
												</t:insertTemplate>
							</td>
							<td class="example">
                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
     												<t:putAttribute name="containerClass" value="panel dialog inlay filter noHeader"/>
												    <t:putAttribute name="containerID" value="[fieldName]_filter"/>
												    
												    
												    <t:putAttribute name="bodyContent">
												    <t:putAttribute name="bodyClass" value=""/>
														    	<fieldset class="column one">
															    	<span class="fieldName">Received Date</span>
																	<label class="control select inline" for="" title="Filter operation">
																		<span class="wrap offLeft">Filter Operation:</span>
																		<select>
																		    <option selected="selected" value="equals">equals</option>
														                    <option value="notEqual">not equal to</option>
														                    <option value="greaterThan">contains</option>
														                    <option value="lessThan">does not contain</option>
														                    <option value="greaterThanOrEqual">starts with</option>
														                    <option value="lessThanOrEqual">ends with</option>
																		</select>
																		<span class="message warning">error message here</span>
																	</label>
																	<span class="fieldName">Shipped Date</span>
																	<button id="swap" class="button options up"><span class="wrap">Swap</span><span class="icon"></span></button>
																</fieldset>
																
																
																</t:putAttribute>
												    <t:putAttribute name="footerContent">
														<button class="button action primary up">
												    		<span class="wrap"><spring:message code="button.ok"/></span>
												    		<span class="icon"></span>
												    	</button>
            											<button class="button action up">
            												<span class="wrap"><spring:message code="button.cancel"/>
            												<span class="icon"></span>
            											</button>
            											<label class="control checkBox lock" for="editable" title="Select to prevent the values from being changed">
		                       								<span class="wrap">Locked</span>
		                        							<input id="editable" type="checkbox"/>
		                   								</label>
					                                    <div class="cosmetic left"></div>
					                                    <div class="cosmetic right"></div>
												    </t:putAttribute>
												</t:insertTemplate>
							</td>
						</tr>
					</tbody>


	        		<tbody>
		        		<tr>
							<th class="rowHeader" rowspan="2">.dialog.overlay</th>
							<td colspan="2"><spring:message code="JIF.dialog.overlay1"/></td>
						</tr>
						<tr>
		        			<th>[default]</th>
		        			<th>.dialog.sizeable</th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">
		        				<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                	<t:putAttribute name="containerClass" value="panel dialog overlay"/>
                                    <t:putAttribute name="containerTitle" value="Dialog Panel" />
                                    <t:putAttribute name="bodyContent">
                                        <div class="FPOonly b"></div>
                                    </t:putAttribute>
		        				</t:insertTemplate>

		        			</td>
		        			
		        			<td class="example">
		        				        					
                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                	<t:putAttribute name="containerClass" value="panel dialog overlay sizeable"/>
                                	 <t:putAttribute name="containerElements">
	                            		<div class="sizer diagonal"></div>
	                            	</t:putAttribute>
                                    <t:putAttribute name="containerTitle" value="Dialog Panel" />
                                    <t:putAttribute name="bodyContent">
                                        <div class="FPOonly a"></div>
                                    </t:putAttribute>
                                </t:insertTemplate>

		        			</td>
		        			
		        		</tr>
	        		</tbody>

                    <tbody>
		        		<tr>
							<th class="rowHeader" rowspan="2">.dialog.overlay</th>
							<td colspan="2"><spring:message code="JIF.dialog.overlay2"/></td>
						</tr>
						<tr>
		        			<th>[default]</th>
		        			<th>.dialog.overlay.sizeable</th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">

                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                	<t:putAttribute name="containerClass" value="panel dialog overlay"/>
                                    <t:putAttribute name="containerTitle" value="Dialog Panel" />
                                    <t:putAttribute name="bodyContent">
                                        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
											<t:putAttribute name="containerClass" value="control groupBox"/>
                                            <t:putAttribute name="bodyContent" >
                                                <div class="FPOonly c"></div>
                                            </t:putAttribute>
                                        </t:insertTemplate>
                                    </t:putAttribute>
                                </t:insertTemplate>
                                
		        			</td>
		        			
		        			<td class="example">

                                <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
                                	<t:putAttribute name="containerClass" value="panel dialog overlay sizeable"/>
                                	 <t:putAttribute name="containerElements">
	                            		<div class="sizer diagonal"></div>
	                            	</t:putAttribute>
                                    <t:putAttribute name="bodyContent">
                                        <t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
											<t:putAttribute name="containerClass" value="control groupBox"/>
                                            <t:putAttribute name="bodyContent">
                                                <div class="FPOonly c"></div>
                                            </t:putAttribute>
                                        </t:insertTemplate>
                                    </t:putAttribute>
                                </t:insertTemplate>
		        			</td>

		        			
		        		</tr>
	        		</tbody>
                    <tbody>
		        		<tr>
							<th class="rowHeader" rowspan="2">.</th>
							<td colspan="2">Generator Sample</td>
						</tr>
						<tr>
		        			<th>[default]</th>
		        			<th></th>
		        		</tr>
		        		<tr>
		        			<th class="rowHeader"></th>
		        			<td class="example">

                                <t:insertTemplate template="/WEB-INF/jsp/templates/generatorSelect.jsp">
                                </t:insertTemplate>

                            </td>

		        			<td class="example">
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