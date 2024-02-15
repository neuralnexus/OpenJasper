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
<%@ page contentType="text/html" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib uri="/spring" prefix="spring"%>

<script type="text/javascript">

    if (typeof orgModule === "undefined") {
        orgModule = {};
    }

    if (typeof orgModule.messages === "undefined") {
        orgModule.messages = {};
    }

    <%--orgModule.messages[""]  = '<spring:message code="" javaScriptEscape="true"/>';--%>
    orgModule.messages['roleNameIsEmpty'] = '<spring:message code="jsp.roleManager.roleCreator.validation.roleNameIsEmpty" javaScriptEscape="true"/>';
    orgModule.messages['roleNameIsAlreadyInUse'] = '<spring:message code="jsp.userManager.userCreator.mustBeUnique" javaScriptEscape="true"/>';
    orgModule.messages['addRole'] = '<spring:message code="jsp.roleManager.roleCreator.add" javaScriptEscape="true"/>';
    orgModule.messages['addRoleTo'] = '<spring:message code="jsp.roleManager.roleCreator.addTo" javaScriptEscape="true"/>';
    orgModule.messages['deleteMessage'] = '<spring:message code="jsp.roleManager.deleteRoleMessage" javaScriptEscape="true"/>';
    orgModule.messages['deleteAllMessage'] = '<spring:message code="jsp.roleManager.deleteAllRolesMessage" javaScriptEscape="true"/>';
    orgModule.messages['cancelEdit'] = '<spring:message code="jsp.roleManager.cancelRoleEditMessage" javaScriptEscape="true"/>';
    orgModule.messages['unsupportedSymbols'] = '<spring:message code="jsp.userManager.userCreator.notSupportedSymbols" javaScriptEscape="true"/>';
    orgModule.messages['validationErrors'] = '<spring:message code="jsp.userAndRoleManager.validation.errors" javaScriptEscape="true"/>';
    orgModule.messages['attribute.name.empty'] = '<spring:message code="jsp.userAndRoleManager.attribute.name.empty" javaScriptEscape="true"/>';
    orgModule.messages['attribute.value.empty'] = '<spring:message code="jsp.userAndRoleManager.attribute.value.empty" javaScriptEscape="true"/>';
    orgModule.messages['attribute.name.too.long'] = '<spring:message code="jsp.userAndRoleManager.attribute.name.too.long" javaScriptEscape="true"/>';
    orgModule.messages['attribute.value.too.long'] = '<spring:message code="jsp.userAndRoleManager.attribute.value.too.long" javaScriptEscape="true"/>';
    orgModule.messages['attribute.name.already.exist'] = '<spring:message code="jsp.userAndRoleManager.attribute.name.already.exist" javaScriptEscape="true"/>';

    if (typeof localContext === "undefined") {
        localContext = {};
    }

    localContext.flowExecutionKey = '${flowExecutionKey}';

    // Initialization of repository search init object.
    localContext.roleMngInitOptions = {
        state: ${state},
        defaultRole: '<spring:message code="${defaultRole}" javaScriptEscape="true"/>',
        defaultEntity: '<spring:message code="${defaultEntity}" javaScriptEscape="true"/>',
        currentUser: '<spring:message code="${signedUser}" javaScriptEscape="true"/>',
        currentUserRoles: ${currentUserRoles}
    };

    orgModule.Configuration = ${configuration};


    if (typeof __jrsConfigs__.roleManagement === "undefined") {
        __jrsConfigs__.roleManagement = {};
    }

    __jrsConfigs__.roleManagement.orgModule = orgModule;
    __jrsConfigs__.roleManagement.localContext = localContext;

</script>
