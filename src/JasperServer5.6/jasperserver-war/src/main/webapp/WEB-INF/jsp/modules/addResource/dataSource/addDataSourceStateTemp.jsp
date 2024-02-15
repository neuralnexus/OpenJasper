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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<script type="text/javascript">
    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.initOptions = {
        isEditMode: ${dataResource.editMode},
        type: "${type}",
        parentFolderUri: "${parentFolder}",
        resourceIdNotSupportedSymbols: "<spring:message code="${resourceIdNotSupportedSymbols}" javaScriptEscape="true"/>",
        dependentResources: <c:out value="${dataResource.namedProperties.dependentResources}" default="[]" escapeXml="false"/>,
        jdbcDrivers: ${jdbcDriversJSON},
        dynamicUrlPartPattern: ${dynamicUrlPartPattern},
        validationPatterns: ${validationPatterns}
    };

    localContext.flowExecutionKey = "${flowExecutionKey}";

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["labelToLong"] = '<spring:message code="ReportDataSourceValidator.error.too.long.reportDataSource.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="ReportDataSourceValidator.error.too.long.reportDataSource.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.reportDataSource.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="ReportDataSourceValidator.error.too.long.reportDataSource.description" javaScriptEscape="true"/>';
    resource.messages["wordInvalidChars"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.wordCharsOnly" javaScriptEscape="true"/>';
    resource.messages["shouldStartWithLetter"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.shouldStartWithLetter" javaScriptEscape="true"/>';

    resource.messages["driverIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.driverClass" javaScriptEscape="true"/>';
    resource.messages["driverNotInstalled"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.driverNotInstalled" javaScriptEscape="true"/>';
    resource.messages["urlIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.connectionUrl" javaScriptEscape="true"/>';
    resource.messages["dbNameIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.dbNameIsEmpty" javaScriptEscape="true"/>';
    resource.messages["parentFolderIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.parentFolderIsEmpty" javaScriptEscape="true"/>';
    resource.messages["userNameIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.username" javaScriptEscape="true"/>';
    resource.messages["serviceIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.jndiName" javaScriptEscape="true"/>';
    resource.messages["beanNameIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.beanName" javaScriptEscape="true"/>';
    resource.messages["beanMethodIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.beanMethod" javaScriptEscape="true"/>';

    resource.messages["awsAccessKeyIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.awsAccessKey" javaScriptEscape="true"/>';
    resource.messages["awsSecretKeyIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.awsSecretKey" javaScriptEscape="true"/>';
    resource.messages["regionIsEmpty"] = '<spring:message code="ReportDataSourceValidator.error.not.empty.reportDataSource.region" javaScriptEscape="true"/>';

    resource.messages["driverHasInvalidChar"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.reportDataSource.driverClass" javaScriptEscape="true"/>';
    resource.messages["urlHasInvalidChar"] = '<spring:message code="ReportDataSourceValidator.error.invalid.chars.reportDataSource.connectionUrl" javaScriptEscape="true"/>';

    resource.messages["resource.Add.Files.Title"]='<spring:message code="resource.Add.Files.Title" javaScriptEscape="true"/>';

    resource.messages["subDatasourcesNeeded"] = '<spring:message code="ReportDataSourceValidator.error.sub.datasources.needed" javaScriptEscape="true"/>';
    resource.messages["subDatasourcesIdDuplicates"] = '<spring:message code="ReportDataSourceValidator.error.sub.datasources.id.duplicates" javaScriptEscape="true"/>';
    resource.messages["dependenciesTopMessage"] = '<spring:message code="resource.dataSource.virtual.dependencies.top.message" javaScriptEscape="true"/>';
    resource.messages["dependenciesBottomMessage"] = '<spring:message code="resource.dataSource.virtual.dependencies.bottom.message" javaScriptEscape="true"/>';

    resource.messages["button.upload"] = '<spring:message code="button.upload" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.required.field"] = '<spring:message code="required.field" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.dbHost"] = '<spring:message code="resource.dataSource.jdbc.dbHost" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.dbPort"] = '<spring:message code="resource.dataSource.jdbc.dbPort" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.dbName"] = '<spring:message code="resource.dataSource.jdbc.dbName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.sName"] = '<spring:message code="resource.dataSource.jdbc.sName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.informixServerName"] = '<spring:message code="resource.dataSource.jdbc.informixServerName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.urlID"] = '<spring:message code="resource.dataSource.jdbc.url" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.schemaName"] = '<spring:message code="resource.dataSource.jdbc.schemaName" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.driverType"] = '<spring:message code="resource.dataSource.jdbc.driverType" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.fieldCantBeEmpty"] = '<spring:message code="resource.dataSource.jdbc.fieldCantBeEmpty" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.invalidField"] = '<spring:message code="resource.dataSource.jdbc.invalidField" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.requiredTitle"] = '<spring:message code="resource.dataSource.jdbc.requiredTitle" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.driverMissing"] = '<spring:message code="resource.dataSource.jdbc.driverMissing" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.otherDriver"] = '<spring:message code="resource.dataSource.jdbc.otherDriver" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.driverUploadSuccess"] = '<spring:message code="resource.dataSource.jdbc.upload.driverUploadSuccess" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.driverUploadFail"] = '<spring:message code="resource.dataSource.jdbc.upload.driverUploadFail" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.overwriteWarning"] = '<spring:message code="resource.dataSource.jdbc.upload.overwriteWarning" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.wrongExtension"] = '<spring:message code="resource.dataSource.jdbc.upload.wrongExtension" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.addDriverButton"] = '<spring:message code="resource.dataSource.jdbc.upload.addDriverButton" javaScriptEscape="true"/>';
    resource.messages["resource.dataSource.jdbc.upload.editDriverButton"] = '<spring:message code="resource.dataSource.jdbc.upload.editDriverButton" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addDataSource === "undefined") {
        __jrsConfigs__.addDataSource = {};
    }

    __jrsConfigs__.addDataSource.localContext = localContext;
    __jrsConfigs__.addDataSource.resource = resource;

</script>