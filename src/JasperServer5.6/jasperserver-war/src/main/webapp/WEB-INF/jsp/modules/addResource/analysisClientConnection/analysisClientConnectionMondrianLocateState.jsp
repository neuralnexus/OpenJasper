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

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<script type="text/javascript">
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        editMode: "${connectionWrapper.editMode}",
        type: "${connectionWrapper.type}",
        ParentFolderUri: "${parentFolder}"
    };

    if (typeof resourceMondrianLocate === "undefined") {
        resourceMondrianLocate = {};
    }

    if (typeof resourceMondrianLocate.messages === "undefined") {
        resourceMondrianLocate.messages = {};
    }

    resourceMondrianLocate.messages["resource.AnalysisConnectionMmondrianLocate.Title"]  = '<spring:message code="resource.AnalysisConnectionMmondrianLocate.Title" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.locateConnectionSource === "undefined") {
        __jrsConfigs__.locateConnectionSource = {};
    }

    __jrsConfigs__.locateConnectionSource.localContext = localContext;
    __jrsConfigs__.locateConnectionSource.resourceMondrianLocate = resourceMondrianLocate;

</script>