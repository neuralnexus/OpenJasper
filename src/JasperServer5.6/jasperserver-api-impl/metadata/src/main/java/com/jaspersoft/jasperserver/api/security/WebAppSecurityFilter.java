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
package com.jaspersoft.jasperserver.api.security;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.jaspersoft.jasperserver.api.security.validators.Validator.validateRequestParams;

/**
 * Filter level input validation for parameter/values in the request.
 */
public class WebAppSecurityFilter implements Filter {
    private static final Logger LOG = Logger.getLogger(WebAppSecurityFilter.class);
    private static final String AJAX_REQUEST = "AJAXRequest";
    private static final String AJAX_REQUEST_HEADER_FLAG = "x-requested-with";
    private static final String ERR_MSG_INPUT_VALIDATION = "message.validation.input";
    private static final String AJAX_RESPONSE_HEADER = "JasperServerError";
    private static final String AJAX_RESPONSE_HEADER_STATUS = "true";
    private static final String ERR_PAGE_MSG_KEY = "stacktrace";
    private static final String ERR_PAGE_SHOW_MSG = "showStacktraceMessage";

    private FilterConfig filterConfig = null;
    private MessageSource messages;
    private String redirectUrl;
    private LocaleResolver localeResolver;
    private boolean showStacktraceMessage = true;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.getSession(true).setAttribute(ERR_PAGE_SHOW_MSG, showStacktraceMessage);

        LocaleContextHolder.setLocale(localeResolver.resolveLocale(httpRequest));
        if (!validateRequestParams(httpRequest))
            processInvalidRequest(httpRequest, httpResponse);
        else
            filterChain.doFilter(httpRequest, response);
    }

    private void processInvalidRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOG.warn("Request " + request.getRequestURL() + " was deemed invalid.");

        if (isTextOnlyResponse(request))
            sendTextOnly(response);
        else
            sendErrorRedirect(request, response);
    }

    /*
     * the header is set in core.ajax.js
     * see doPost and doGet
     */
    protected boolean isTextOnlyResponse(HttpServletRequest request)
    {
        // It's enough just to check whether this header is present, and do not rely on it's value,
        // because jQuery sets it's own value for this header by default.
        return request.getHeader(AJAX_REQUEST_HEADER_FLAG) != null;
    }

    private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
    	HttpSession session = request.getSession(true);
        session.setAttribute(ERR_PAGE_SHOW_MSG, true);
        session.setAttribute( ERR_PAGE_MSG_KEY, messages.getMessage(ERR_MSG_INPUT_VALIDATION,new Object[]{}, LocaleContextHolder.getLocale()) );
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + redirectUrl));
    }

    protected void sendTextOnly(HttpServletResponse response)
    {
        response.setHeader(AJAX_RESPONSE_HEADER, AJAX_RESPONSE_HEADER_STATUS);
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = null;
        try
        {
            out = response.getWriter();
            out.write( messages.getMessage(ERR_MSG_INPUT_VALIDATION,new Object[]{}, LocaleContextHolder.getLocale()) );
            out.close();
        }
        catch (IOException ioe)
        {
            LOG.error(ioe);
        }
    }

    public MessageSource getMessages() {
        return messages;
    }

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }
    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public boolean isShowStacktraceMessage() {
        return showStacktraceMessage;
    }

    public void setShowStacktraceMessage(boolean showStacktraceMessage) {
        this.showStacktraceMessage = showStacktraceMessage;
    }
}
