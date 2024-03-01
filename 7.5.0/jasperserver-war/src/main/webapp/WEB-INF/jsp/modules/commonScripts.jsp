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

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>
<%@ taglib uri="/spring" prefix="spring"%>

<jsp:include page="setScriptOptimizationProps.jsp"/>

<script type="text/javascript" src="${scriptsUri}/runtime_dependencies/requirejs/require.js"></script>
<script type="text/javascript" src="${scriptsUri}/require.config.js"></script>
<script type="text/javascript">
<js:xssNonce type="javascript"/>

require.config({
    baseUrl: "${scriptsUri}"
});
</script>

<script type="text/javascript" src="${pageContext.request.contextPath}/runtime/${jsOptimizationProperties.runtimeHash}/JavaScriptServlet"></script>

<c:if test="${optimizeJavascript == true}">
    <%--
        Prototypejs is excluded from uglifing since it has troubles with uglifying.
        So in case if js optimization is enabled - load it as usual javascript file from not optimized sources.
    --%>
    <script type="text/javascript" src="${notOptimizedScriptsUri}/runtime_dependencies/prototype/dist/prototype.js"></script>

    <%-- Hack to skip loading of prototype from optimized folder since it's already loaded--%>
    <script type="text/javascript">
        define("prototype", $);
    </script>
</c:if>