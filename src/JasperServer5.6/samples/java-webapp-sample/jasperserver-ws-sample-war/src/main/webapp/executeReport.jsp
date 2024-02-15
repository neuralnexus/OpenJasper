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
<!-- @page import="net.sf.jasperreports.engine.*,net.sf.jasperreports.engine.export.*,com.jaspersoft.jasperserver.sample.*" -->
<%@page import="com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument" %>
<%@page import="com.jaspersoft.jasperserver.irplugin.wsclient.FileContent" %>
<%@ page import="java.util.*" %>
<%
    final String IC_MULTI_PREFIX = "ic_multi_";
    if (session == null) response.sendRedirect(request.getContextPath() + "/index.jsp");
    com.jaspersoft.jasperserver.sample.WSClient client = (com.jaspersoft.jasperserver.sample.WSClient) session.getAttribute("client");

    if (client == null) response.sendRedirect(request.getContextPath() + "/index.jsp");

    String currentUri = request.getParameter("uri");
    if (currentUri == null || currentUri.length() == 0) currentUri = "/";
    if (currentUri.length() > 1 && currentUri.endsWith("/"))
        currentUri = currentUri.substring(0, currentUri.length() - 1);

    com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor reportUnit = null;
    java.io.OutputStream os = null;
    try {

        java.util.Enumeration enum_params = request.getParameterNames();
        java.util.Map hashMap = new java.util.HashMap();
        while (enum_params.hasMoreElements()) {
            String key = "" + enum_params.nextElement();
            String[] values = request.getParameterValues(key);

            Object value = null;
            if (values.length == 1 && !key.startsWith(IC_MULTI_PREFIX)) {
                value = values[0];
            } else {
                value = Arrays.asList(values);
            }

            key = key.startsWith(IC_MULTI_PREFIX) ? key.substring(IC_MULTI_PREFIX.length()) : key;

            hashMap.put(key, value);
        }


        /*
 *        A webservice to run the report is invoked here.
 *        The result is a map of files. In general the map contains a single
 *        file. For formats that do not contain images, the map contains
 *        the main report and all the related images.
 *        In this sample the images are put in the session, so they can be
 *        retrieved with sequencial calls.
 *      */

        String format = request.getParameter("format");
        java.util.List arguments = new java.util.ArrayList();
        Map files = null;
        
        // Convert the format in a valid format for the web service
        if (request.getParameter("format") == null || request.getParameter("format").equals("html")) {

            arguments.add(new Argument(Argument.RUN_OUTPUT_IMAGES_URI, "image?key=" ));
            arguments.add(new Argument(Argument.RUN_OUTPUT_FORMAT, Argument.RUN_OUTPUT_FORMAT_HTML));

            files = client.runReport(currentUri, hashMap, arguments);

            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            // Let's put in the session all the images, so they can
            // be retrieved using the servelt mapped to the url /images
            Iterator keys = files.keySet().iterator();
            
            while (keys.hasNext())
            {
                String imageName = (String)keys.next();
                if (!imageName.equals("report"))
                {
                    session.setAttribute(imageName, files.get(imageName));
                }
            }

        } else if (request.getParameter("format").equals("pdf")) {

            arguments.add(new Argument(Argument.RUN_OUTPUT_FORMAT, Argument.RUN_OUTPUT_FORMAT_PDF));

            files = client.runReport(currentUri, hashMap, arguments);

            response.setContentType("application/pdf");
        
        } else if (request.getParameter("format").equals("xls")) {

            arguments.add(new Argument(Argument.RUN_OUTPUT_FORMAT, Argument.RUN_OUTPUT_FORMAT_XLS));

            files = client.runReport(currentUri, hashMap, arguments);

            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition", " inline; filename=report.xls");

        }


        FileContent reportFile = (FileContent)files.get("report");

        // Send the output to the client...
        os = response.getOutputStream();
        os.write(reportFile.getData());
        os.flush();



        /*  --------

        The commented code below shows how to get from the webserive
        a JasperPrint instead of a final file format (like PDF) and use
        directly the JasperReports library to create the output
        with the maximum control over the exporter.
        ----------------------------------------------------------

        JasperPrint print = client.runReport(currentUri, hashMap);

        net.sf.jasperreports.engine.JRExporter exporter = null;


        if (request.getParameter("format") == null || request.getParameter("format").equals("html")) {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            exporter = new net.sf.jasperreports.engine.export.JRHtmlExporter();

            JRHyperlinkProducerMapFactory producerFactory = new JRHyperlinkProducerMapFactory();
            producerFactory.addProducer("ReportExecution", new ReportExecutionHyperlinkProducer());
            exporter.setParameter(JRExporterParameter.HYPERLINK_PRODUCER_FACTORY, producerFactory);

            request.getSession().setAttribute(net.sf.jasperreports.j2ee.servlets.ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE, print);
            exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, "image?image=");
        } else if (request.getParameter("format").equals("pdf")) {
            response.setContentType("application/pdf");

            exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
        } else if (request.getParameter("format").equals("xls")) {
            response.setContentType("application/msexcel");
            response.setHeader("Content-Disposition", " inline; filename=report.xls");
            exporter = new net.sf.jasperreports.engine.export.JExcelApiExporter();
        }

        os = response.getOutputStream();
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
        exporter.exportReport();
        */

        return;

    } catch (Exception ex) {

        ex.printStackTrace();
        if (os != null) {
            java.io.PrintWriter pw = new java.io.PrintWriter(os);
            pw.write("<h1>Error running report</h1><code>");
            ex.printStackTrace(pw);
            pw.write("</code>");
        } else {
            out.write("<h1>Error running report</h1><code>");
            out.write(ex.getMessage() + "<br><br>");
            ex.printStackTrace(new java.io.PrintWriter(out));
            out.write(ex.getMessage() + "</code>");

        }

        return;
    }
%>