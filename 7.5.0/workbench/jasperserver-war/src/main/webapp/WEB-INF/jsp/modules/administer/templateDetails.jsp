<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
  ~ http://www.jaspersoft.com.
  ~
  ~ Unless you have purchased a commercial license agreement from Jaspersoft,
  ~ the following license terms apply:
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU Affero General Public License as
  ~ published by the Free Software Foundation, either version 3 of the
  ~ License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU Affero General Public License for more details.
  ~
  ~ You should have received a copy of the GNU Affero General Public License
  ~ along with this program. If not, see <http://www.gnu.org/licenses/>.
  --%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

    <js:xssNonce/>
    <t:insertTemplate template="/WEB-INF/jsp/templates/detail.jsp">
                <t:putAttribute name="containerClass" value="sizeable hidden"/>
                <t:putAttribute name="bodyContent">	    
					<ul>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_QUERY"/></h4>
							<p class="data" id="detQuery"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_DATASOURCE"/></h4>
							<p class="data" id="detDataSourceURI"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_PARAMS"/></h4>
							<p class="data" id="detParameters"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_AGE"/></h4>
							<p class="data" id="detAge"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_LAST_USED"/></h4>
							<p class="data" id="detTime"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_ROWS"/></h4>
							<p class="data" id="detRows"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_QUERY_TIME"/></h4>
							<p class="data" id="detQueryTime"></p>
						</li>
						<li>
							<h4 class="label"><spring:message code="ADH_270_CACHE_FETCH_TIME"/></h4>
							<p class="data" id="detFetchTime"></p>
						</li>
					</ul>
	</t:putAttribute>
</t:insertTemplate>						

