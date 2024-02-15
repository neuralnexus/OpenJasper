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
package com.jaspersoft.jasperserver.remote.settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p></p>
 *
 * @author Sergey Prilukin
 * @version $Id: DateTimeSettingsProvider.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class DateTimeSettingsProvider implements SettingsProvider {

    private Pattern datePickerSettingsPattern;
    private Integer datePickerSettingsPatternGroup;
    private String datePickerDefaultLocale;
    private String datePickerSettingsPathTemplate;
    private Map<String, String> datePickerPropertiesMapping;
    private Map<String, String> timePickerPropertiesMapping;
    private Boolean enableCache;
    private MessageSource messageSource;

    private Map<String, Map<String, Object>> settingsCache = new ConcurrentHashMap<String, Map<String, Object>>();

    public void setDatePickerSettingsPattern(Pattern datePickerSettingsPattern) {
        this.datePickerSettingsPattern = datePickerSettingsPattern;
    }

    public void setDatePickerSettingsPatternGroup(Integer datePickerSettingsPatternGroup) {
        this.datePickerSettingsPatternGroup = datePickerSettingsPatternGroup;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }

    public void setDatePickerPropertiesMapping(Map<String, String> datePickerPropertiesMapping) {
        this.datePickerPropertiesMapping = datePickerPropertiesMapping;
    }

    public void setDatePickerSettingsPathTemplate(String datePickerSettingsPathTemplate) {
        this.datePickerSettingsPathTemplate = datePickerSettingsPathTemplate;
    }

    public void setDatePickerDefaultLocale(String datePickerDefaultLocale) {
        this.datePickerDefaultLocale = datePickerDefaultLocale;
    }

    public void setTimePickerPropertiesMapping(Map<String, String> timePickerPropertiesMapping) {
        this.timePickerPropertiesMapping = timePickerPropertiesMapping;
    }

    @Override
    public Object getSettings() {
        String locale = getLocale();

        Map<String, Object> result = settingsCache.get(locale);
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put("datepicker", getDatePickerSettings(locale));
            result.put("timepicker", getTimePickerSettings());

            if (enableCache) {
                settingsCache.put(locale, result);
            }
        }

        return result;
    }

    private String getLocale() {
        return LocaleContextHolder.getLocale().toString().replaceAll("_", "-");
    }

    /* DatePicker settings */

    private Map<String, Object> getDatePickerSettings(String locale) {
        String content = getFileContentOrDefault(locale);
        String settingsJSON = getSettingsFromFileContent(content);
        Map<String, Object> result = convertToJSONObject(settingsJSON);
        customizeSettings(result);

        return result;
    }

    private File getSettingsFile(String path) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        ServletContext servletContext = request.getSession().getServletContext();
        String realPath = servletContext.getRealPath(path);

        return new File(realPath);
    }

    private String getFileContentOrDefault(String locale) {
        String settingsFile = String.format(datePickerSettingsPathTemplate, locale);
        String fileContent = getFileContent(settingsFile);
        if (fileContent == null) {
            fileContent = getFileContent(String.format(datePickerSettingsPathTemplate, datePickerDefaultLocale));
            if (fileContent == null) {
                throw new RuntimeException("No default settings present");
            }
        }

        return fileContent;
    }

    private String getFileContent(String path) {
        File file = getSettingsFile(path);
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            return FileUtils.readFileToString(file, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSettingsFromFileContent(String content) {
        Matcher regexMatcher = datePickerSettingsPattern.matcher(content);
        if (regexMatcher.find()) {
            return regexMatcher.group(datePickerSettingsPatternGroup);
        } else {
            throw new RuntimeException("Settings has incorrect format: " + content);
        }
    }

    private Map<String, Object> convertToJSONObject(String jsonAsString) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            return mapper.readValue(jsonAsString, new TypeReference<Map<String, Object>>() { });
        } catch (IOException e) {
            throw new RuntimeException("Error during parsing JSON. Invalid content" + jsonAsString);
        }
    }

    private void customizeSettings(Map<String, Object> settings) {
        Locale locale = LocaleContextHolder.getLocale();

        for (Map.Entry<String, String> entry: datePickerPropertiesMapping.entrySet()) {
            String propertyValue = messageSource.getMessage(entry.getValue(),
                    new Object[] {}, locale);
            settings.put(entry.getKey(), propertyValue);
        }
    }

    /* TimePicker settings */

    private Map<String, Object> getTimePickerSettings() {
        Map<String, Object> result = new HashMap<String, Object>();
        Locale locale = LocaleContextHolder.getLocale();

        for (Map.Entry<String, String> entry: timePickerPropertiesMapping.entrySet()) {
            String propertyValue = messageSource.getMessage(entry.getValue(),
                    new Object[] {}, locale);
            result.put(entry.getKey(), propertyValue);
        }

        return result;
    }
}
