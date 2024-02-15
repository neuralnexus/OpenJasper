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

<script type="text/javascript">
    if (typeof Administer === "undefined") {
        Administer = {};
    }

    if (typeof Administer._messages === "undefined") {
        Administer._messages = {};
    }

    Administer.flowExecutionKey = '${flowExecutionKey}';

    // Analysis and Designer Options messages
    Administer._messages["JAM_018_ERROR"]  = '<spring:message code="JAM_018_ERROR" javaScriptEscape="true"/>';
    Administer._messages["JAM_019_WHOLE_NUMBER_ERROR"]  = '<spring:message code="JAM_019_WHOLE_NUMBER_ERROR" javaScriptEscape="true"/>';
    Administer._messages["JAM_020_RATIO_NUMBER_ERROR"]  = '<spring:message code="JAM_020_RATIO_NUMBER_ERROR" javaScriptEscape="true"/>';
    Administer._messages["JAM_048_ZERO_OR_GREATER"]  = '<spring:message code="JAM_048_ZERO_OR_GREATER" javaScriptEscape="true"/>';
    Administer._messages["JAM_049_ONE_OR_GREATER"]  = '<spring:message code="JAM_049_ONE_OR_GREATER" javaScriptEscape="true"/>';
    Administer._messages["JAM_050_ONE_TO_99"]  = '<spring:message code="JAM_050_ONE_TO_99" javaScriptEscape="true"/>';
    Administer._messages["JAM_051_INVALID_CLASS"]  = '<spring:message code="JAM_051_INVALID_CLASS" javaScriptEscape="true"/>';
    Administer._messages["JAM_056_UPDATED"]  = '<spring:message code="JAM_056_UPDATED" javaScriptEscape="true"/>';
    Administer._messages["JAM_057_DONE"]  = '<spring:message code="JAM_057_DONE" javaScriptEscape="true"/>';

    // Ad Hoc Cache messages
    Administer._messages["ADH_270_CACHE_QUERY_CLEARED"]  = '<spring:message code="ADH_270_CACHE_QUERY_CLEARED" javaScriptEscape="true"/>';
    Administer._messages["ADH_270_CACHE_NO_QUERY_FOUND"]  = '<spring:message code="ADH_270_CACHE_NO_QUERY_FOUND" javaScriptEscape="true"/>';
    Administer._messages["ADH_270_CACHE_All_CLEARED"]  = '<spring:message code="ADH_270_CACHE_All_CLEARED" javaScriptEscape="true"/>';

    Administer.urlContext = "${pageContext.request.contextPath}";

    __jrsConfigs__.Administer = Administer;

</script>
