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

import com.jaspersoft.jasperserver.core.util.StringUtil;
import com.jaspersoft.jasperserver.api.JSSecurityException;
import org.apache.log4j.Logger;
import org.owasp.csrfguard.CsrfGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import static com.jaspersoft.jasperserver.api.security.SecurityConfiguration.isCSRFValidationOn;
import static com.jaspersoft.jasperserver.api.security.validators.Validator.getDefaultEncoding;
import static com.jaspersoft.jasperserver.api.security.validators.Validator.getDefaultEncodingErrorMessage;

/**
 * @author Anton Fomin copied from org.owasp.csrfguard.CsrfGuardFilter
 * @version $Id: JSCsrfGuardFilter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class JSCsrfGuardFilter implements Filter {

    private static final Logger log = Logger.getLogger(JSCsrfGuardFilter.class);

    /**
     * The Map, which holds the following structure:
     * <Request action, <Parameter name, Parameter value pattern>>
     */
    private Map<String, Map<String, Pattern>> protectedActions = null;

    private static final String REQUEST_FILTER_CONFIG = "esapi/Owasp.CsrfGuard.RequestFilterConfig.properties";

    @Autowired
    @Qualifier("messageSource")
    private MessageSource messages;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (isCSRFValidationOn() && filterRequest((HttpServletRequest) request)) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            HttpSession session = httpRequest.getSession(true);

			CsrfGuard csrfGuard = CsrfGuard.getInstance();
			log.info(String.format("CsrfGuard analyzing request %s", httpRequest.getRequestURI()));

            if (session.isNew())
                csrfGuard.writeLandingPage(httpRequest, httpResponse);
            else if (csrfGuard.isValidRequest(httpRequest, httpResponse)) {
                filterChain.doFilter(httpRequest, httpResponse);
            }

            csrfGuard.updateTokens(httpRequest);
        } else {
            filterChain.doFilter(request, response);
        }
	}

    public JSCsrfGuardFilter() {
        /* Load filtering settings from configuration */
        loadRequestFilterConfig();
    }

    /**
     * Finds out whether request contains all specified parameters and their values.
     *
     * @param request
     *      Incoming request
     * @return
     *      true if request URI matches
     */
    private boolean filterRequest(HttpServletRequest request) {
        Map<String, String[]> paramsMap = StringUtil.getDecodedMap(request.getParameterMap(), getDefaultEncoding(), getDefaultEncodingErrorMessage());

        if (paramsMap.size() == 0)
            return false;         //no need to check further against protectedActions

        for (Map.Entry<String, Map<String, Pattern>> actionEntry : protectedActions.entrySet()) {
            Map<String, Pattern> params = actionEntry.getValue();

            int paramsMatch = 0;
            for (Map.Entry<String, Pattern> paramEntry : params.entrySet()) {
                if (paramsMap.containsKey(paramEntry.getKey())) {
                    for (String value : paramsMap.get(paramEntry.getKey())) {
                        if (paramEntry.getValue().matcher(value).matches()) {
                            paramsMatch ++;
                            break;
                        }
                    }
                }
            }
            if (params.size() == paramsMatch) return true;
        }
        return false;
    }

    /**
     * Load properties file with request filter rules
     *
     * @throws JSSecurityException
     */
    private void loadRequestFilterConfig() {
        if (protectedActions != null) return;

        Properties requestFilterConfig = new Properties();

        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(REQUEST_FILTER_CONFIG);
            requestFilterConfig.load(is);
            is.close();
        } catch (Exception e) {
			final String errMsg = "CSRF request filter config cannot be loaded.";
			log.error(errMsg, e);
			throw new JSSecurityException(errMsg, e);
        }
        log.info("CSRF request filter config loaded.");

        protectedActions = resolveProtectedActions(requestFilterConfig);
    }

    /**
     * Resolve CSRF Guards props
     *
     * @param
     *      props Properties from config file
     * @return
     *      Map containing param names and values grouped by request action
     *
     * @throws JSSecurityException
     */
    public Map<String, Map<String, Pattern>> resolveProtectedActions(Properties props) {

        Map<String /*Request Action*/,
        Map<String /*Parameter Name*/,
        Pattern /*Parameter Value pattern*/>> actions = new HashMap<String, Map<String, Pattern>>();

        try {
            /* Get action separator - <action><action separator><parameter name>, e.g. CreateUser|_eventId */
            String separatorPropName = "ActionSeparator";
            String separator = props.getProperty(separatorPropName, "|");

            /* Iterate through all props */
            for (String propName : props.stringPropertyNames()) {

                /* Skip action for separator */
                if (propName.equals(separatorPropName)) continue;

                /* Pick out action and param names separated by action separator */
                String[] propNameArray = propName.split("\\" + separator);
                String action = propNameArray[0];
                String param = propNameArray[1];

                /* Insert new param into action map */
                final Pattern actionPattern = Pattern.compile(props.getProperty(propName), Pattern.CASE_INSENSITIVE);
                if (actions.containsKey(action)) {
                    Map<String, Pattern> params = actions.get(action);
                    params.put(param, actionPattern);
                /* Insert new action into map */
                } else {
                    Map<String, Pattern> params = new HashMap<String, Pattern>();
                    params.put(param, actionPattern);
                    actions.put(action, params);
                }
            }
            log.info("CSRF request filter config resolved.");
        } catch (Exception e) {
			final String errMsg = "CSRF request filter parameters cannot be resolved.";
			log.error(errMsg, e);
			throw new JSSecurityException(errMsg, e);
        }
        return actions;
    }

	public void init(FilterConfig filterConfig) throws ServletException {
        /** nothing to do **/
	}

    public void destroy() {
		/** nothing to do **/
	}

    public MessageSource getMessages() {
        return messages;
    }

    public void setMessages(MessageSource messages) {
        this.messages = messages;
    }
}
