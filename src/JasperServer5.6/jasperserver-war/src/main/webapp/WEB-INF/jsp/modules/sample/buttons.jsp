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
    <t:putAttribute name="pageTitle" value="Button Gallery"/>
    <t:putAttribute name="bodyID" value="buttons"/>
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
		    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlebuttons"/></t:putAttribute>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent">
	        
			<table id="sampleGrid" class="">
				<thead>
				<tr>
					<th></th>
					<th colspan="7"><spring:message code="JIF.titlestate"/></th>
				</tr>
				<tr>
					<th class="rowHeader">.button</th>
					<th>.up</th>
					<th>.up.over</th>
					<th>.up.pressed</th>
					<th>.disabled</th>
					<th>.down</th>		        			
					<th>.down.over</th>
					<th>.down.pressed</th>
				</tr>
				</thead>
		
		
		<tbody>
				<tr>
					<th class="rowHeader">.action</th>
					<td colspan="7"><spring:message code="JIF.button.action"/></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action up"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action up over"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action up pressed"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action disabled"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.action.primary</th>
					<td colspan="7"><spring:message code="JIF.button.action.primary"/></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action primary up"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action primary up over"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action primary up pressed"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action primary disabled"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.action.primary.jumbo</th>
					<td colspan="7"><spring:message code="JIF.button.action.primary.jumbo"/></td>
				</tr>
				<tr>
					<td></td>
					<td><a class="button action jumbo up"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></a></td>
					<td><a class="button action jumbo up over"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></a></td>
					<td><a class="button action jumbo up pressed"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></a></td>
					<td><a class="button action jumbo disabled"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></a></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>				
				<tbody>
				<tr>
					<th class="rowHeader">.action.square</th>
					<td colspan="7"><spring:message code="JIF.button.action.square"/></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square up"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action square up over"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action square up pressed"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button action square disabled"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
		
				<tbody>
				<tr>
					<th class="rowHeader">.options</th>
					<td colspan="7"><spring:message code="JIF.button.options"/></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button options up"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button options up over"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button options up pressed"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td><button class="button options disabled"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.capsule</th>
					<td colspan="7"><spring:message code="JIF.button.capsule"/></td>
				</tr>
				<tr class="">
					<td class="rowHeader"></td>
					<td><div class="toolbar"><button class="button capsule text up"><span class="wrap"><spring:message code="JIF.label"/></span><span class="icon"></span></button></div></td>
					<td><div class="toolbar"><button class="button capsule text up over"><span class="wrap"><spring:message code="JIF.label"/></span></div></button></td>
					<td><div class="toolbar"><button class="button capsule text up pressed"><span class="wrap"><spring:message code="JIF.label"/></span></div></button></td>
					<td><div class="toolbar"><button class="button capsule text disabled"><span class="wrap"><spring:message code="JIF.label"/></span></div></button></td>
					<td><div class="toolbar"><button class="button capsule text down"><span class="wrap"><spring:message code="JIF.label"/></span></div></button></td>
					<td><div class="toolbar"><button class="button capsule text down over"><span class="wrap"><spring:message code="JIF.label"/></span></div></button></td>
					<td><div class="toolbar"><button class="button capsule text down pressed"><span class="wrap"><spring:message code="JIF.label"/></span></div></button></td>
				</tr>
				</tbody>
		
				<tbody>
				<tr>
					<th class="rowHeader">.buttonSet.capsule</th>
					<td colspan="7"><spring:message code="JIF.button.buttonSet.capsule"/></td>
				</tr>
				<tr class="toolbarContainer">
					<td class="rowHeader"></td>
					<td colspan="7">
						<div class="toolbar">		        					
							<ul class="list buttonSet">
		    					<li class="leaf"><button class="button capsule text up first"><span class="wrap"><spring:message code="JIF.labelup"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text up over middle"><span class="wrap"><spring:message code="JIF.labelupover"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text up pressed middle"><span class="wrap"><spring:message code="JIF.labeluppressed"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text up disabled middle"><span class="wrap"><spring:message code="JIF.labelupdisabled"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text down over middle"><span class="wrap"><spring:message code="JIF.labeldownover"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text down pressed middle"><span class="wrap"><spring:message code="JIF.labeldownpressed"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text down last"><span class="wrap"><spring:message code="JIF.labeldown"/></span><span class="icon"></span></button></li>
		    				</ul>		        					
							<ul class="list buttonSet">
		    					<li class="leaf"><button class="button capsule text down first"><span class="wrap"><spring:message code="JIF.labeldown"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text down disabled middle"><span class="wrap"><spring:message code="JIF.labeldowndisabled"/></span><span class="icon"></span></button></li>
		    					<li class="leaf"><button class="button capsule text up last"><span class="wrap"><spring:message code="JIF.labelup"/></span><span class="icon"></span></button></li>
		    				</ul>		        					
							<ul class="list buttonSet">
								<li class="leaf"><button class="button capsule text down"><span class="wrap"><spring:message code="JIF.labeldown"/></span><span class="icon"></span></button></li>
							</ul>		
						</div>
					</td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.search</th>
					<td colspan="7"><spring:message code="JIF.button.search"/></td>
				</tr>
				<tr id="frame">
					<td></td>
					<td><button class="button search up"></button></td>
					<td><button class="button search up over"></button></td>
					<td><button class="button search up pressed"></button></td>
					<td><button class="button search disabled"></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
			<tbody>
				<tr>
					<th class="rowHeader">.open .button.disclosure</th>
					<td colspan="7"></td>
				</tr>
				<tr class="open">
					<td></td>
					<td><button class="button disclosure"></button></td>
					<td><button class="button disclosure over"></button></td>
					<td><button class="button disclosure pressed"></button></td>
					<td><button class="button disclosure disabled"></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<th class="rowHeader">.closed .button.disclosure</th>
					<td colspan="7"></td>
				</tr>
				<tr class="closed">
					<td></td>
					<td><button class="button disclosure"></button></td>
					<td><button class="button disclosure over"></button></td>
					<td><button class="button disclosure pressed"></button></td>
					<td><button class="button disclosure disabled"></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<th class="rowHeader">.button.minimize</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button minimize"></button></td>
					<td><button class="button minimize over"></button></td>
					<td><button class="button minimize pressed"></button></td>
					<td><button class="button minimize disabled"></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<th class="rowHeader">.button.picker</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button picker"></button></td>
					<td><button class="button picker over"></button></td>
					<td><button class="button picker pressed"></button></td>
					<td><button class="button picker disabled"></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
			<tbody>
				<tr>
					<th class="rowHeader">.move.right</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move right up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move right up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move right up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move right disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
					<td></td>
				</tr>
			</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.left</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move left up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move left up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move left up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move left disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.toRight</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move toRight up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toRight up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toRight up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toRight disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.toLeft</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move toLeft up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toLeft up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toLeft up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toLeft disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.upward</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move upward up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move upward up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move upward up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move upward disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.downward</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move downward up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move downward up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move downward up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move downward disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.toTop</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move toTop up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toTop up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toTop up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toTop disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
				</tr>
				</tbody>
				<tbody>
				<tr>
					<th class="rowHeader">.move.toBottom</th>
					<td colspan="7"></td>
				</tr>
				<tr>
					<td></td>
					<td><button class="button action square move toBottom up"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toBottom up over"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toBottom up pressed"><span class="wrap"><span class="icon"></span></span></button></td>
					<td><button class="button action square move toBottom disabled"><span class="wrap"><span class="icon"></span></span></button></td>
					<td></td>
					<td></td>
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