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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<t:insertTemplate template="/WEB-INF/jsp/templates/page.jsp">
    <t:putAttribute name="pageTitle" value="Three Column Layout"/>
    <t:putAttribute name="bodyID" value="columnsThree"/>
    <t:putAttribute name="bodyClass" value="threeColumn"/>
    <t:putAttribute name="moduleName" value="commons.main"/>
	
		<t:putAttribute name="bodyContent" >
		
			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    	<t:putAttribute name="containerClass" value="column decorated primary"/>
			    <t:putAttribute name="containerTitle"><spring:message code="JIF.titleprimary"/></t:putAttribute>
                <t:putAttribute name="headerContent">
                </t:putAttribute>
			    <t:putAttribute name="bodyContent">
					[REQUIRED]
			    </t:putAttribute>
			</t:insertTemplate>
			
			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    	<t:putAttribute name="containerClass" value="column decorated secondary sizeable"/>
		    	<t:putAttribute name="containerElements">
		    		<div class="sizer horizontal"></div>
		    		<button class="button minimize"></button>
		    	</t:putAttribute>
			    <t:putAttribute name="containerTitle"><spring:message code="JIF.titlesecondary"/></t:putAttribute>
			    <t:putAttribute name="bodyContent">
					[REQUIRED]
			    </t:putAttribute>
			</t:insertTemplate>
			
			<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    	<t:putAttribute name="containerClass" value="column decorated tertiary sizeable"/>
		    	<t:putAttribute name="containerElements">
		    		<div class="sizer horizontal"></div>
		    		<button class="button minimize"></button>
		    	</t:putAttribute>
			    <t:putAttribute name="containerTitle"><spring:message code="JIF.titletertiary"/></t:putAttribute>
			    <t:putAttribute name="bodyContent">
					[REQUIRED]
			    </t:putAttribute>
			</t:insertTemplate>
			
	    </t:putAttribute>

</t:insertTemplate>
