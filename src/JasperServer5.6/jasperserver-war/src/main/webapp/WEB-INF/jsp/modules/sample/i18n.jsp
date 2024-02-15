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
    <t:putAttribute name="pageTitle" value="Dialog Samples"/>
    <t:putAttribute name="bodyID" value="dialogs"/>
    <t:putAttribute name="pageClass" value="test"/>
    <t:putAttribute name="bodyClass" value="twoColumn"/>
    <t:putAttribute name="moduleName" value="example/i18nPluginsUsage"/>
    <t:putAttribute name="headerContent" >
        <link rel="stylesheet" href="${pageContext.request.contextPath}/<spring:theme code='samples.css'/>" type="text/css" />
		<style type="text/css">
				#sampleGrid .rowHeader {width:3%;}
				#sampleGrid .example {width:40%;padding:0;}
				.panel {position: relative;}
		</style>
    </t:putAttribute>
    <t:putAttribute name="bodyContent" >
	
		<t:insertTemplate template="/WEB-INF/jsp/templates/container.jsp">
		    <t:putAttribute name="containerClass" value="column decorated primary"/>
		    <t:putAttribute name="containerTitle" value="Internationalization demo"/>
		    <t:putAttribute name="bodyClass" value="oneColumn"/>
		    <t:putAttribute name="bodyContent" >

                <table>
                    <tr><td>Depends from current locale (jasperserver_messages):</td><td><h1 id="localized-message-jrs"></h1></td></tr>
                    <tr><td>Depends from current locale (calendar):</td><td><h1 id="localized-message-calendar"></h1></td></tr>
                    <tr><td>Depends from current locale (CommonBundle):</td><td><h1 id="localized-message-common"></h1></td></tr>
                </table>

			</t:putAttribute>
		    <t:putAttribute name="footerContent">
		    	<!-- custom content here; remove this comment -->
		    </t:putAttribute>
		</t:insertTemplate>		
		
		<t:insertTemplate template="/WEB-INF/jsp/modules/sample/sampleIndex.jsp"/>

    </t:putAttribute>
</t:insertTemplate>
