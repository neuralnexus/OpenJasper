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
  ~ License, or (at your step) any later version.
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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Flow Layout: Multi-Step"/>
    <t:putAttribute name="bodyID" value="flow"/>
    <t:putAttribute name="bodyClass" value="flow wizard oneColumn"/>
    
    <t:putAttribute name="bodyContent" >
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			<t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titleprimary"/></t:putAttribute>
            <t:putAttribute name="moduleName" value="commons.main"/>
		    <t:putAttribute name="bodyClass" value="flow"/>
            <t:putAttribute name="headerContent">
            </t:putAttribute>
		    <t:putAttribute name="bodyContent">
			<div id="flowControls">
				<t:insertTemplate template="/WEB-INF/jsp/templates/control_tabSet.jsp">
                    <t:putAttribute name="type" value="buttons"/>
                    <t:putAttribute name="containerClass" value="vertical"/>
                    <t:putListAttribute name="tabset">
                        <t:addListAttribute>
                            <t:addAttribute>step1</t:addAttribute>
                            <t:addAttribute>Step 1</t:addAttribute>
                            <t:addAttribute>selected</t:addAttribute>
                        </t:addListAttribute>
                        <t:addListAttribute>
                            <t:addAttribute>step2</t:addAttribute>
                            <t:addAttribute>Step 2</t:addAttribute>
                        </t:addListAttribute>
                        <t:addListAttribute>
                            <t:addAttribute>step3</t:addAttribute>
                            <t:addAttribute>Step 3</t:addAttribute>
                        </t:addListAttribute>
                    </t:putListAttribute>
                </t:insertTemplate>
			</div>						
				<div id="stepDisplay">
					<fieldset class="row instructions">
						<legend class="offLeft"><span>Instructions</span></legend>
						<h2 class="textAccent02"><spring:message code="JIF.titlecalltoaction"/></h2>
						<h4><spring:message code="JIF.labelcallexplanation"/></h4>
					</fieldset>
				
					<fieldset class="row inputs oneColumn">
						<legend class="offLeft"><span>User Inputs</span></legend>
						
							<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
							    <t:putAttribute name="containerClass" value="column primary"/>
							    <t:putAttribute name="containerTitle"><spring:message code="JIF.labelsteptitle"/></t:putAttribute>

							    <t:putAttribute name="bodyContent">
									<spring:message code="JIF.labelcontenthere"/>
							    </t:putAttribute>

							</t:insertTemplate>
												
					</fieldset><!--/.row.inputs-->
					
					</div><!--/#stepDisplay-->
				</t:putAttribute>
					<t:putAttribute name="footerContent">
						<fieldset dos"wizardNav" >
							<button id="done" class="button primary action up"><span class="wrap"><spring:message code='button.submit'/></span><span class="icon"></span></button>
							<button id="cancel" class="button action up"><span class="wrap"><spring:message code='button.cancel'/></span><span class="icon"></span></button>						
					    </fieldset>				
					</t:putAttribute>	    
		</t:insertTemplate>
		
    
    </t:putAttribute>

</t:insertTemplate>