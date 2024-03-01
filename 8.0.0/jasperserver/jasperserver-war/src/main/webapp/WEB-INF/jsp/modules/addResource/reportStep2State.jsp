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
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        <js:out javaScriptEscape="true">
        jrxmlFileResourceAlreadyUploaded: "${jrxmlFileResourceAlreadyUploaded}",
        isEditMode: ${wrapper.editMode},
        type: "jrxml",
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}"
        </js:out>
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["resource.Report.Title"] = '<spring:message code="resource.Report.Title" javaScriptEscape="true"/>';
    resource.messages["labelToLong"] = '<spring:message code="ReportDetailsValidator.error.too.long.reportUnit.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="ReportDetailsValidator.error.not.empty.reportUnit.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="ReportDetailsValidator.error.too.long.reportUnit.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="ReportDetailsValidator.error.not.empty.reportUnit.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="ReportDetailsValidator.error.invalid.chars.reportUnit.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="ReportDetailsValidator.error.too.long.reportUnit.description" javaScriptEscape="true"/>';
    resource.messages["filePathIsEmpty"] = '<spring:message code="ReportDetailsValidator.error.invalid.jrxml.source" javaScriptEscape="true"/>';
    resource.messages["resourceUriIsEmpty"] = '<spring:message code="ReportDetailsValidator.error.not.reusable.jrxmlUri" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addJasperReport === "undefined") {
        __jrsConfigs__.addJasperReport = {};
    }

    __jrsConfigs__.addJasperReport.localContext = localContext;
    __jrsConfigs__.addJasperReport.resource = resource;

</script>
