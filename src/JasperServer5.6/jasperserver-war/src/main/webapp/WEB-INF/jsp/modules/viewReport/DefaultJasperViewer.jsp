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

<%--
Default rendering HTML fragment for a JR report called from the JasperViewerTag.

 Expects attributes:
    pageIndex:          Integer         Current page in report
    lastPageIndex:      Integer         Greatest page number in report
    page:               String          URL for surrounding page
    exporter:           JRHtmlExporter  The wrapped JasperPrint
    pageIndexParameter:     String      parameter name in URL for paging
--%>

<%@ taglib prefix="t" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="js" uri="/WEB-INF/jasperserver.tld" %>

<%@ page import="net.sf.jasperreports.engine.export.*" %>
<%@ page import="net.sf.jasperreports.engine.*" %>
<%@ page import="com.jaspersoft.jasperserver.war.action.ExporterConfigurationBean" %>
<%@ page import="com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit" %>
<%@ page import="java.io.StringWriter" %>
<%@ page errorPage="/WEB-INF/jsp/modules/system/errorPage.jsp" %>



<div id="reportOutput" class="hidden">

    <%@ include file="DefaultJasperViewerState.jsp" %>


    <%--  Optional Pagination  --%>
    <c:if test="${(innerPagination || param.frame == 0) and !emptyReport and (empty lastPageIndex or lastPageIndex > 0 or configurationBean.paginationForSinglePageReport)}">
        <div id="innerPagination">
            <button type="submit" title="<spring:message code="jasper.report.view.hint.first.page"/>" class="button toLeft" ${pageIndex > 0 ? '' : 'disabled="disabled"'} onclick="javascript:Report.navigateToReportPage(0)"><span class="wrap"><span class="icon"></span></span></button>
            <button type="submit" title="<spring:message code="jasper.report.view.hint.previous.page"/>" class="button left" ${pageIndex > 0 ? '' : 'disabled="disabled"'} onclick="javascript:Report.navigateToReportPage(${pageIndex-1})"><span class="wrap"><span class="icon"></span></span></button>

            <label class="control input text inline" for="currentPage" title="<spring:message code="REPORT_VIEWER_PAGINATION_CONTROLS_CURRENT_PAGE" javaScriptEscape="true"/>">
		        <spring:message code="jasper.report.view.page.intro"/>
			    <input type="text" name="currentPage" value="${pageIndex+1}" onchange="javascript:Report.goToPage(this.value);return false;"/>
			    <spring:message code="jasper.report.view.page.of"/>${lastPageIndex + 1}
            </label>

            <button type="submit" title="<spring:message code="jasper.report.view.hint.next.page"/>" class="button right" ${pageIndex < lastPageIndex ? '' : 'disabled="disabled"'} onclick="javascript:Report.navigateToReportPage(${pageIndex+1})"><span class="wrap"><span class="icon"></span></span></button>
            <button type="submit" title="<spring:message code="jasper.report.view.hint.last.page"/>" class="button toRight" ${pageIndex < lastPageIndex ? '' : 'disabled="disabled"'} onclick="javascript:Report.navigateToReportPage(${lastPageIndex})"><span class="wrap"><span class="icon"></span></span></button>
        </div>
    </c:if>


    <%--  Report Output  --%>

    <c:if test="${!emptyReport}">
        <jsp:useBean id="exporter" type="JRExporter" scope="request"/>
        <jsp:useBean id="isComponentMetadataEmbedded" type="java.lang.Boolean" scope="request"/>
        <%
          exporter.setParameter(JRExporterParameter.OUTPUT_WRITER, out);
          exporter.exportReport();

          if (isComponentMetadataEmbedded) {
              JsonExporter jsonExporter = (JsonExporter) request.getAttribute("jsonExporter");
              StringWriter sw = new StringWriter();
              jsonExporter.setParameter(JRExporterParameter.OUTPUT_WRITER, sw);
              jsonExporter.exportReport();

              String serializedJson = sw.getBuffer().toString();
              out.write("<span id=\"reportComponents\" style=\"display:none\">" + serializedJson.replaceAll("\\s","") + "</span>");
          }
        %>

        <div id="paginationIndexHolder" class="hidden" data-lastPageIndex="${lastPageIndex}"></div>
    </c:if>
    <c:if test="${emptyReport}">
        <div id="emptyReportMessageHolder" class="hidden">${emptyReportMessage}</div>
    </c:if>
</div>