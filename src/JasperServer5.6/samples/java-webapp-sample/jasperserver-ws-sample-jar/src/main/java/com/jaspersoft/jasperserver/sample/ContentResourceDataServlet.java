/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.sample;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.Argument;
import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author lucian
 */
public class ContentResourceDataServlet extends HttpServlet {

    private static final String WS_CLIENT_SESSION_NAME = "client";
    private static final String INVALID_SESSION_REDIRECT = "/index.jsp";
    private static final int BUFFER_SIZE = 65536;


    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        WSClient wsClient = session == null ? null : (WSClient) session.getAttribute(WS_CLIENT_SESSION_NAME);
        if (wsClient == null) {
            response.sendRedirect(request.getContextPath() + INVALID_SESSION_REDIRECT);
            return;
        }

        String resourceURI = request.getPathInfo();

        List args = new ArrayList(1);
        args.add(new Argument(Argument.NO_SUBRESOURCE_DATA_ATTACHMENTS, null));

        File tmpDir = (File) getServletContext().getAttribute("javax.servlet.context.tmpdir");
        File tmpFile = File.createTempFile("contentResource", ".data", tmpDir);

        try {
            ResourceDescriptor resourceDescriptor = wsClient.get(resourceURI, tmpFile, args);

            String contentType = resourceDescriptor.getResourcePropertyValue(ResourceDescriptor.PROP_CONTENT_RESOURCE_TYPE);
            if (contentType.equals("html")) {
                response.setContentType("text/html;charset=UTF-8");
            } else if (contentType.equals("pdf")) {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "inline; filename=\"" + resourceDescriptor.getName() + "\"");
            } else if (contentType.equals("xls")) {
                response.setContentType("application/xls");
                response.setHeader("Content-Disposition", "inline; filename=\"" + resourceDescriptor.getName() + "\"");
            } else if (contentType.equals("rtf")) {
                response.setContentType("application/rtf");
                response.setHeader("Content-Disposition", "inline; filename=\"" + resourceDescriptor.getName() + "\"");
            } else if (contentType.equals("csv")) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "inline; filename=\"" + resourceDescriptor.getName() + "\"");
            }

            response.setContentLength((int) tmpFile.length());

            writeFileContent(response, tmpFile);
        } catch (Exception e) {
            throw new ServletException(e);
        } finally {
            tmpFile.delete();
        }
    }

    protected void writeFileContent(HttpServletResponse resp, File file) throws IOException {
        ServletOutputStream out = resp.getOutputStream();
        FileInputStream fileIn = new FileInputStream(file);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            int read;
            while ((read = fileIn.read(buf)) > 0) {
                out.write(buf, 0, read);
            }
        } finally {
            fileIn.close();
        }
        out.flush();
        out.close();
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Content resource data servlet";
    }
    // </editor-fold>
}
