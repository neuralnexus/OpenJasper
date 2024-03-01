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

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<js:out javaScriptEscape="true">
<script type="text/javascript">
    <js:xssNonce type="javascript"/>

    __jrsConfigs__.addDataSource = {};

    __jrsConfigs__.addDataSource.initOptions = {
        isEditMode: ${dataResource.editMode},
        parentFolderUri: "",
        resourceIdNotSupportedSymbols: "${resourceIdNotSupportedSymbols}",
        dependentResources: JSON.parse('${not empty dataResource.namedProperties.dependentResources ? dataResource.namedProperties.dependentResources : "[]" }')
    };

    __jrsConfigs__.addDataSource.initOptions.resourceUri = "${not dataResource.editMode ? "" : dataResource.reportDataSource.URIString}";
</script>
</js:out>