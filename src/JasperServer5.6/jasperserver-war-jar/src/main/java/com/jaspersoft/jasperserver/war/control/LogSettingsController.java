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
package com.jaspersoft.jasperserver.war.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.jaspersoft.jasperserver.api.common.properties.Log4jPropertyChanger;
import com.jaspersoft.jasperserver.api.common.properties.PropertiesManagementService;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl.LogSettingsDiagnosticService;


public class LogSettingsController implements Controller, ResourceLoaderAware, InitializingBean {
	
	final static String PROPERTY_PREFIX = "log4j.";
    private static Logger log = Logger.getLogger(LogSettingsController.class);

    private Map<Locale, Map<String, String>> loggerDescriptionsByLocale =
            Collections.synchronizedMap(new HashMap<Locale, Map<String, String>>());
    private Map<String, String> loggers = Collections.synchronizedMap(new LinkedHashMap<String, String>());

    private List<String> loggerDescriptionFiles;
    private ResourceLoader resourceLoader;
    private MessageSource messageSource;

    protected PropertiesManagementService propertiesManagementService;

    @Autowired(required = false)
    @Qualifier("logSettingsDiagnosticService")
    protected LogSettingsDiagnosticService logSettingsDiagnosticService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String loggerArg = request.getParameter("logger");
        String levelArg = request.getParameter("level");

        // check if we're getting an update
        if (loggerArg != null && levelArg != null) { 
        	getPropertiesManagementService().setProperty(PROPERTY_PREFIX+loggerArg, levelArg);
            loggers.put(loggerArg, levelArg);
        }

        // refresh everything, in case the properties have been updated
        // via property editor, DB, export/import, etc.
        loggers = Collections.synchronizedMap(new LinkedHashMap<String, String>());
        afterPropertiesSet();

        ModelAndView mav = new ModelAndView("modules/administer/logSettings");
        mav.addObject("loggers", loggers);
        mav.addObject("loggerDesc", loggerDescriptionsByLocale.get(LocaleContextHolder.getLocale()));
        return mav;
    }

    /**
     * Loads the loggers list with the keys (leaves levels blank)
     */
    private void initLoggersFromPropFiles() {

        if (loggers.isEmpty()) {
            if (loggerDescriptionFiles == null || loggerDescriptionFiles.isEmpty()) {
                throw new IllegalStateException("No logger description files specified.");
            }

            //just putting in the logger keys here
            for (String file : loggerDescriptionFiles) {
                try {
                    String props = loadWebappFile(file);

                    if (props != null) {
                        Properties p = new Properties();
                        p.load(new ByteArrayInputStream(props.getBytes()));

                        for (Object key : p.keySet()) {
                        	loggers.put((String) key, null);
                        }
                    }
                } catch (IOException e) {
                    log.warn("problem loading log descriptions", e);
                }
            }
        }

    }

    /**
     * Loads the descriptions from properties files into a Map by locale
     */
    private void initLocalizedDescriptions() {

        Locale locale = LocaleContextHolder.getLocale();

        if (!loggerDescriptionsByLocale.containsKey(locale)) {
            Map<String, String> loggerDescriptions = new LinkedHashMap<String, String>();

            for (String key : loggers.keySet()) {
                loggerDescriptions.put(key, messageSource.getMessage(key, null, "", locale));
            }

            loggerDescriptionsByLocale.put(locale, loggerDescriptions);
        }
    }

    /**
     * Loads a file located somewhere in the webapp folder.
     *
     * @param path the path to the file in the webapp folder.
     *
     * @return the content of the file as a string.
     *
     * @throws IOException exception when loading file.
     */
    private String loadWebappFile(String path) throws IOException {
        // use resource loader to look up something in servlet context
        org.springframework.core.io.Resource test = resourceLoader.getResource(path);
        InputStream is = test.getInputStream();
        // read it into a string and return it
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int n;
        while ((n = is.read(bytes)) > 0) {
            baos.write(bytes, 0, n);
        }
        return baos.toString();
    }

    /* (non-Javadoc)
      * @see org.springframework.context.ResourceLoaderAware#setResourceLoader(org.springframework.core.io.ResourceLoader)
      */
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void setLoggerDescriptionFiles(List<String> loggerDescriptionFiles) {
        this.loggerDescriptionFiles = loggerDescriptionFiles;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

	public PropertiesManagementService getPropertiesManagementService() {
		return propertiesManagementService;
	}

	public void setPropertiesManagementService(
			PropertiesManagementService propertiesManagementService) {
		this.propertiesManagementService = propertiesManagementService;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {

        initLoggersFromPropFiles();
        initLocalizedDescriptions();

        for (String logKey : loggers.keySet()) {
			Logger log = Logger.getLogger(logKey);
			String level = log.getEffectiveLevel().toString();
            loggers.put(logKey, level);

		}
        updateLoggersFromPropMgmtService();

        if (logSettingsDiagnosticService != null) {
            logSettingsDiagnosticService.initLoggers(loggers);
        }
    }

    /**
     * Overwrites logger names and levels from PropertiesManagementService
     *
     * @see PropertiesManagementService
     * @since 5.0
     *
     */
    private void updateLoggersFromPropMgmtService() {

        for (Map.Entry<String,String> entry : (Set<Map.Entry<String,String>>)(getPropertiesManagementService().entrySet())) {
            String key = entry.getKey();
            if (key.startsWith(Log4jPropertyChanger.PROPERTY_PREFIX)) {
                loggers.put(Log4jPropertyChanger.parseKey(key), entry.getValue());
            }
        }

    }

}

