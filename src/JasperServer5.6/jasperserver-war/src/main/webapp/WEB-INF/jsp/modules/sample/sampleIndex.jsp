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

<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
    <t:putAttribute name="containerClass" value="column decorated secondary sizeable"/>
		    	<t:putAttribute name="containerElements">
		    		<div class="sizer horizontal"></div>
		    		<button class="button minimize"></button>
		    	</t:putAttribute>
    <t:putAttribute name="containerTitle">Index to Sample Galleries</t:putAttribute>
    <t:putAttribute name="bodyClass" value=""/>
    <t:putAttribute name="bodyContent" >
		<ul class="list filters">
			<li class="node">
				<p href="#" class="wrap header"><b class="icon"></b>Components</p>
				<ul class="list filters">
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=typography"><b class="icon"></b>Typography</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=buttons"><b class="icon"></b>Buttons</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=lists"><b class="icon"></b>Lists</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=menus"><b class="icon"></b>Menus</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=controls"><b class="icon"></b>Controls</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=panels"><b class="icon"></b>Panels</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=dialogs"><b class="icon"></b>Dialogs</a></li>
					<li class="leaf"><p class="wrap separator" href="#"><b class="icon"></b></p></li>
				</ul>
			</li>
			<li class="node">
				<p href="#" class="wrap header"><b class="icon"></b>Standard Layouts</p>
				<ul class="list filters">
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=columnsOne"><b class="icon"></b>One Column</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=columnsTwo"><b class="icon"></b>Two Column</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=columnsThree"><b class="icon"></b>Three Column</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=flow"><b class="icon"></b>Flow</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=wizard"><b class="icon"></b>Wizard</a></li>
					<li class="leaf"><a class="wrap button" href="flow.html?_flowId=sampleFlow&page=tabbed"><b class="icon"></b>Tabbed</a></li>
					<li class="leaf"><p class="wrap separator" href="#"><b class="icon"></b></p></li>
				</ul>
			</li>
		</ul>
		
    </t:putAttribute>
    <t:putAttribute name="footerContent">
    </t:putAttribute>
</t:insertTemplate>