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

<%@ taglib uri="/spring" prefix="spring"%>

<script type="text/javascript">
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        isEditMode: ${query.editMode},
        resourceIdNotSupportedSymbols: "<spring:message code="${resourceIdNotSupportedSymbols}" javaScriptEscape="true"/>"
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="QueryValidator.error.too.long.query.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="QueryValidator.error.not.empty.query.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="QueryValidator.error.too.long.query.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="QueryValidator.error.not.empty.query.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="QueryValidator.error.invalid.chars.query.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="QueryValidator.error.too.long.query.description" javaScriptEscape="true"/>';
    resource.messages["resource.Query.Title"] = '<spring:message code="resource.Query.Title" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addQuery === "undefined") {
        __jrsConfigs__.addQuery = {};
    }

    __jrsConfigs__.addQuery.localContext = localContext;
    __jrsConfigs__.addQuery.resource = resource;

    // just a hack to use the same script for all 3 steps
    __jrsConfigs__.addQuery.loadResourceLocator = false;

</script>