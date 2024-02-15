<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%--Common IC constants--%>
<%@ page import="com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler"%>

<script type="text/javascript">
    __jrsConfigs__.inputControlsConstants = {};
    __jrsConfigs__.inputControlsConstants.NULL_SUBSTITUTION_VALUE = "<%= InputControlHandler.NULL_SUBSTITUTION_VALUE %>";
    __jrsConfigs__.inputControlsConstants.NULL_SUBSTITUTION_LABEL = "<%= InputControlHandler.NULL_SUBSTITUTION_LABEL %>";
    __jrsConfigs__.inputControlsConstants.NOTHING_SUBSTITUTION_VALUE = "<%= InputControlHandler.NOTHING_SUBSTITUTION_VALUE %>";
</script>

<%--Common IC functionality--%>
<c:if test="${isPro}">
    <script type="text/javascript">
        __jrsConfigs__.inputControlsConstants.DEFAULT_OPTION_TEXT = '<spring:message code="report.options.dialog.default.name" javaScriptEscape="true"/>';
        __jrsConfigs__.inputControlsConstants.CONTROL_DEFAULT_OPTION_TEXT = '<spring:message code="report.options.select.empty.label" javaScriptEscape="true"/>';
        __jrsConfigs__.inputControlsConstants.CONTROL_OPTIONS_TEXT = '<spring:message code="report.options.select.label" javaScriptEscape="true"/>';
    </script>
</c:if>
