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
<%@ taglib uri="/spring" prefix="spring"%>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Wizard Layout"/>
    <t:putAttribute name="bodyID" value="wizard"/>
    <t:putAttribute name="bodyClass" value="flow wizard oneColumn oneStep"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
    
    <t:putAttribute name="bodyContent" >
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			<t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlewizard"/> (<spring:message code="JIF.titlewizardparenthesis"/>)</t:putAttribute>
            <t:putAttribute name="headerContent">
            </t:putAttribute>
		    <t:putAttribute name="bodyClass" value="flow wizard"/>
		    <!-- NOTE: four possible cases for bodyClass that control #wizardNav buttons: 
		         - .flow.wizard.firstStep; 
		         - .flow.wizard.lastStep; 
		         - .flow.wizard.oneStep;
		         - .flow.wizard [the general case]
			-->
		    <t:putAttribute name="bodyContent">
			<div id="flowControls">
				<ul class="list stepIndicator">
					<li class="leaf"><p class="wrap" href="#"><b class="icon"></b>Step 1</p></li>
					<li class="leaf"><p class="wrap" href="#"><b class="icon"></b>Step 2</p>
						<ul class="node">
							<li class="leaf"><p class="wrap" href="#"><b class="icon"></b>Step 2.a</p></li>
							<li class="leaf selected"><p class="wrap" href="#"><b class="icon"></b>Step 2.b</p></li>
							<li class="leaf"><p class="wrap" href="#"><b class="icon"></b>Step 2.c</p></li>
						</ul>
					</li>
					<li class="leaf"><p class="wrap" href="#"><b class="icon"></b>Step 3</p></li>
					<li class="leaf"><p class="wrap" href="#"><b class="icon"></b>Step 4</p></li>
				</ul>
			</div>						
				<div id="stepDisplay">
					<fieldset class="row instructions">
						<legend class="offLeft"><span>Instructions</span></legend>
						<h2 class="textAccent02"><spring:message code="JIF.labelcalltoaction"/></h2>
						<h4><spring:message code="JIF.labelcallexplanation"/></h4>
					</fieldset>
				
					<fieldset class="row inputs oneColumn">
						<legend class="offLeft"><span>User Inputs</span></legend>
						
							<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
							    <t:putAttribute name="containerClass" value="column primary noHeader"/>
							    <t:putAttribute name="containerTitle"> </t:putAttribute>

							    <t:putAttribute name="bodyContent">
									<h3><spring:message code="JIF.wizardaboutPara1"/></h3>
									<h3><spring:message code="JIF.wizardaboutPara2"/> <a href="flow.html?_flowId=sampleFlow&page=flow"><spring:message code="JIF.wizardaboutLink1"/></a></h3>
							    </t:putAttribute>

							</t:insertTemplate>
					
					</fieldset><!--/.row.inputs-->
					
				</div><!--/#stepDisplay-->

				<t:putAttribute name="footerContent">
					<fieldset id="wizardNav" >
						<button id="previous" class="button action up"><span class="wrap"><spring:message code='button.previous'/></span><span class="icon"></span></button>
						<button id="next" class="button action up"><span class="wrap"><spring:message code='button.next'/></span><span class="icon"></span></button>
						<button id="done" class="button primary action up"><span class="wrap"><spring:message code='button.submit'/></span><span class="icon"></span></button>
						<button id="cancel" class="button action up"><span class="wrap"><spring:message code='button.cancel'/></span><span class="icon"></span></button>						
				    </fieldset>				
				</t:putAttribute>
			</t:putAttribute>	    
		</t:insertTemplate>

    </t:putAttribute>

</t:insertTemplate>