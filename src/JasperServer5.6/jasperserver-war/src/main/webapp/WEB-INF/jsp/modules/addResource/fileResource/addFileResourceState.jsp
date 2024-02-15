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
        organizationId: "${organizationId}",
        publicFolderUri: "${publicFolderUri}",
        expectedType: "${expectedFileType}",
        folderURI: "${fileResource.fileResource.parentFolder}",
        isEditMode: ${fileResource.editMode},
        resourceIdNotSupportedSymbols: "<spring:message code="${resourceIdNotSupportedSymbols}" javaScriptEscape="true"/>"
    };

    if (typeof resource === "undefined") {
        resource = {};
    }

    if (typeof resource.messages === "undefined") {
        resource.messages = {};
    }

    resource.messages["typeIsNotValid"] = '<spring:message code="resource.file.extension" javaScriptEscape="true"/>';
    resource.messages["pathIsEmpty"] = '<spring:message code="FileResourceValidator.error.no.file.source" javaScriptEscape="true"/>';
    resource.messages["labelToLong"] = '<spring:message code="FileResourceValidator.error.too.long.fileResource.label" javaScriptEscape="true"/>';
    resource.messages["labelIsEmpty"] = '<spring:message code="FileResourceValidator.error.not.empty.fileResource.label" javaScriptEscape="true"/>';
    resource.messages["resourceIdToLong"] = '<spring:message code="FileResourceValidator.error.too.long.fileResource.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdIsEmpty"] = '<spring:message code="FileResourceValidator.error.not.empty.fileResource.name" javaScriptEscape="true"/>';
    resource.messages["resourceIdInvalidChars"] = '<spring:message code="FileResourceValidator.error.invalid.chars.fileResource.name" javaScriptEscape="true"/>';
    resource.messages["descriptionToLong"] = '<spring:message code="FileResourceValidator.error.too.long.fileResource.description" javaScriptEscape="true"/>';
    resource.messages["resource.Add.Files.Title"] = '<spring:message code="resource.Add.Files.Title" javaScriptEscape="true"/>';

    if (typeof __jrsConfigs__.addFileResource === "undefined") {
        __jrsConfigs__.addFileResource = {};
    }

    __jrsConfigs__.addFileResource.localContext = localContext;
    __jrsConfigs__.addFileResource.resource = resource;

</script>
