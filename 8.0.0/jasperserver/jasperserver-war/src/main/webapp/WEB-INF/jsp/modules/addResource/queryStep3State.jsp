<%@ page contentType="text/html; charset=utf-8" %>
<%--
  ~ Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<script type="text/javascript">
    <js:xssNonce type="javascript"/>
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        <js:out>
        isEditMode: ${query.editMode}
        </js:out>
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["resource.Query.Title"] = '<spring:message code="resource.Query.Title" javaScriptEscape="true"/>';
    resource.messages["queryInvalid"] = '<spring:message code="resource.query.defineQuery" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addQuery === "undefined") {
        __jrsConfigs__.addQuery = {};
    }

    __jrsConfigs__.addQuery.localContext = localContext;
    __jrsConfigs__.addQuery.resource = resource;
</script>