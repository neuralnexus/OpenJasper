/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.remote.settings;

import com.jaspersoft.jasperserver.dto.common.JavaAliasConverter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
/**
 * <p></p>
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
public class DateTimeSettingsProvider implements SettingsProvider {

    private String datePickerDefaultLocale;
    private Map<String, Object> datePickerPropertiesMapping;
    private Map<String, Object> timePickerPropertiesMapping;
    private Map<String, String> dateTimeSettingsPropertyTypeMapping;
    private Map<String, Boolean> applyClientTimezoneFormatting;
    private Boolean enableCache;
    private MessageSource messageSource;

    private Map<String, Map<String, Object>> settingsCache = new ConcurrentHashMap<String, Map<String, Object>>();

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setEnableCache(Boolean enableCache) {
        this.enableCache = enableCache;
    }

    public void setDatePickerPropertiesMapping(Map<String, Object> datePickerPropertiesMapping) {
        this.datePickerPropertiesMapping = datePickerPropertiesMapping;
    }

    public void setDatePickerDefaultLocale(String datePickerDefaultLocale) {
        this.datePickerDefaultLocale = datePickerDefaultLocale;
    }

    public void setTimePickerPropertiesMapping(Map<String, Object> timePickerPropertiesMapping) {
        this.timePickerPropertiesMapping = timePickerPropertiesMapping;
    }

    public void setDateTimeSettingsPropertyTypeMapping(Map<String, String> dateTimeSettingsPropertyTypeMapping) {
        this.dateTimeSettingsPropertyTypeMapping = dateTimeSettingsPropertyTypeMapping;
    }

    public void setApplyClientTimezoneFormatting(Map<String, Boolean> applyClientTimezoneFormatting) {
        this.applyClientTimezoneFormatting = applyClientTimezoneFormatting;
    }

    @Override
    public Object getSettings() {
        String locale = getLocale();

        Map<String, Object> result = settingsCache.get(locale);
        if (result == null) {
            result = new HashMap<String, Object>();
            result.put("datepicker", getPickerSettings(datePickerPropertiesMapping));
            result.put("timepicker", getPickerSettings(timePickerPropertiesMapping));
            result.put("timezoneFormatting", this.getTimeZoneFormatting());

            if (enableCache) {
                settingsCache.put(locale, result);
            }
        }

        return result;
    }

    private String getLocale() {
        return LocaleContextHolder.getLocale().toString().replaceAll("_", "-");
    }

    private Map<String, Boolean> getTimeZoneFormatting() {
        Map<String, Boolean> timeZoneFormatting = new HashMap<String, Boolean>();

        for (Map.Entry<String, Boolean> entry: this.applyClientTimezoneFormatting.entrySet()) {
            String key = entry.getKey();
            Boolean value = entry.getValue();

            String alias = JavaAliasConverter.toAlias(key);

            timeZoneFormatting.put(alias, value);
        }

        return timeZoneFormatting;
    }

    /* DatePicker settings */

    private Map<String, Object> getPickerSettings(Map<String, Object> propsMapping) {

        Map<String, Object> settings = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry: propsMapping.entrySet()) {
            String settingName = entry.getKey();
            Object settingPropertyKey = entry.getValue();

            if (settingPropertyKey instanceof String) {
                Object propertyValue = getSettingValueByKey((String) settingPropertyKey);

                settings.put(settingName, propertyValue);
            } else if (settingPropertyKey instanceof Set) {
                Set<Object> values = new LinkedHashSet<Object>();
                Set<String> setOfSettingPropertyKeys = (Set<String>) settingPropertyKey;

                for (String value: setOfSettingPropertyKeys) {
                    values.add(getSettingValueByKey(value));
                }

                settings.put(settingName, values);
            }
        }

        return settings;
    }

    private Object getSettingValueByKey(String key) {
        Locale rawLocale = LocaleContextHolder.getLocale();

        Object settingValue = messageSource.getMessage(key, new Object[] {}, rawLocale);
        String settingType = dateTimeSettingsPropertyTypeMapping.get(key);

        if (settingType != null) {
            if (settingType.equals("java.lang.Integer")) {
                settingValue = Integer.parseInt((String) settingValue);
            }

            if (settingType.equals("java.lang.Boolean")) {
                if (settingValue.equals("true")) {
                    settingValue = true;
                } else if (settingValue.equals("false")) {
                    settingValue = false;
                }
            }
        }

        return settingValue;
    }
}
