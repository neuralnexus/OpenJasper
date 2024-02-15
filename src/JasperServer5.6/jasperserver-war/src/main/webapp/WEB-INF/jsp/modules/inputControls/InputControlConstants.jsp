<%@ taglib uri="/spring" prefix="spring"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%--Common IC constants--%>
<%@ page import="com.jaspersoft.jasperserver.war.cascade.handlers.InputControlHandler"%>
<script type="text/javascript">
    if (typeof ControlsBase === "undefined") {
        ControlsBase = {};
    }
    ControlsBase.NULL_SUBSTITUTION_VALUE = "<%= InputControlHandler.NULL_SUBSTITUTION_VALUE %>";
    ControlsBase.NULL_SUBSTITUTION_LABEL = "<%= InputControlHandler.NULL_SUBSTITUTION_LABEL %>";
    ControlsBase.NOTHING_SUBSTITUTION_VALUE = "<%= InputControlHandler.NOTHING_SUBSTITUTION_VALUE %>";
</script>

<c:if test="${isPro}">
    <script type="text/javascript">
            ControlsBase.DEFAULT_OPTION_TEXT = '<spring:message code="report.options.dialog.default.name" javaScriptEscape="true"/>';
            ControlsBase.CONTROL_DEFAULT_OPTION_TEXT = '<spring:message code="report.options.select.empty.label" javaScriptEscape="true"/>';
            ControlsBase.CONTROL_OPTIONS_TEXT = '<spring:message code="report.options.select.label" javaScriptEscape="true"/>';
    </script>
</c:if>