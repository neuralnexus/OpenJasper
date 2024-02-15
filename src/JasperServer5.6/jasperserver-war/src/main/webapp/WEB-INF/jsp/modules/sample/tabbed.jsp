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
    <t:putAttribute name="pageTitle" value="Designer Layout"/>
    <t:putAttribute name="bodyID" value="tabbed"/>
    <t:putAttribute name="bodyClass" value="oneColumn"/>

    
    <t:putAttribute name="bodyContent" >
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
			<t:putAttribute name="containerClass" value="column decorated primary tabbed showingToolBar"/>
		    <t:putAttribute name="containerTitle">Primary</t:putAttribute>
            <t:putAttribute name="moduleName" value="commons.main"/>
		    <t:putAttribute name="headerContent">
    	
		    	<div class="tabs">
					<t:insertTemplate template="/WEB-INF/jsp/templates/control_tabSet.jsp">
		                <t:putAttribute name="type" value="buttons"/>
		                <t:putAttribute name="containerClass" value="horizontal"/>
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
				
				<div class="toolbar">		        					
					<ul class="list buttonSet">
						<li class="leaf"><button class="button capsule text up"><span class="wrap">Sample<span class="icon"></span></span></button></li>		
					</ul>
				</div>
		    </t:putAttribute>
		    
		    <t:putAttribute name="bodyContent">

					content here


			</t:putAttribute>	    
			<t:putAttribute name="footerContent">
				<fieldset id="wizardNav">
					<button class="button action primary up"><span class="wrap"><spring:message code="button.ok" javaScriptEscape="true"/></span><span class="icon"></span></button>
					<button class="button action up"><span class="wrap"><spring:message code="button.cancel" javaScriptEscape="true"/></span><span class="icon"></span></button	
				</fieldset>
    		</t:putAttribute>
		</t:insertTemplate>
			
    </t:putAttribute>

</t:insertTemplate>