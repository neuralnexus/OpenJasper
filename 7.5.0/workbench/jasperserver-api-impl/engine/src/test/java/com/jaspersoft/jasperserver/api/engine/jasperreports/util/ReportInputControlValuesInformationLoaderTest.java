/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.AbstractMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ReportInputControlValuesInformationLoader}
 *
 * @author Sergey Prilukin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class ReportInputControlValuesInformationLoaderTest {

    @Test
    public void ensureValuesInfoResolvedIfKeyPresentInMessageSource() throws Exception {
        MessageSource messageSource = mockMessageSource(map(entry("key", "value")));
        final ListOfValues listOfValues = mockListOfValues(getLOVI("$R{key}", "tttt"));
        ReportInputControlValuesInformation info =
                ReportInputControlValuesInformationLoader.getReportInputControlValuesInformation(listOfValues, null, messageSource);

        assertNotNull(info);
        assertEquals("value", info.getInputControlValueInformation("$R{key}").getPromptLabel());
    }

    @Test
    public void ensureValuesInfoResolvedIfKeyPresentInResourceBundle() throws Exception {
        ResourceBundle reportResourceBundle = new ResourceBundleMock(map(entry("key", "value")));
        final ListOfValues listOfValues = mockListOfValues(getLOVI("$R{key}", "tttt"));
        ReportInputControlValuesInformation info =
                ReportInputControlValuesInformationLoader.getReportInputControlValuesInformation(listOfValues, reportResourceBundle, null);

        assertNotNull(info);
        assertEquals("value", info.getInputControlValueInformation("$R{key}").getPromptLabel());
    }

    @Test
    public void ensureValuesInfoResolvedIfNoMessagesPresent() throws Exception {
        MessageSource messageSource = mockMessageSource(map(entry("key1", "messageSourceValue")));
        ResourceBundle reportResourceBundle = new ResourceBundleMock(map(entry("key1", "reportBundleValue")));
        final ListOfValues listOfValues = mockListOfValues(getLOVI("$R{key}", "tttt"));
        ReportInputControlValuesInformation info =
                ReportInputControlValuesInformationLoader.getReportInputControlValuesInformation(listOfValues, reportResourceBundle, messageSource);

        assertNotNull(info);
        assertEquals("$R{key}", info.getInputControlValueInformation("$R{key}").getPromptLabel());
    }

    private ListOfValues mockListOfValues(ListOfValuesItem... items) {
        final ListOfValuesImpl listOfValues = new ListOfValuesImpl();
        for (ListOfValuesItem item: items) {
            listOfValues.addValue(item);
        }

        return listOfValues;
    }

    private ListOfValuesItem getLOVI(String label, String value) {
        final ListOfValuesItemImpl listOfValuesItem = new ListOfValuesItemImpl();
        listOfValuesItem.setLabel(label);
        listOfValuesItem.setValue(value);
        return listOfValuesItem;
    }

    private MessageSource mockMessageSource(final Map<String, String> strings) {
        MessageSource messageSourceMock = mock(MessageSource.class);
        when(messageSourceMock.getMessage(any(), any(), any(Locale.class)))
                .thenAnswer(invocation -> {
                    String key = invocation.getArgument(0);
                    if (key == null) {
                        throw new NullPointerException();
                    }

                    if (!strings.containsKey(key)) {
                        throw new NoSuchMessageException(key);
                    }

                    return strings.get(key);
                });

        return messageSourceMock;
    }

    private Map.Entry<String, String> entry(String key, String value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    private Map<String, String> map(Map.Entry<String, String>... strings) {
        Map<String, String> stringsMap = new HashMap<>(strings.length);
        for (Map.Entry<String, String> entry: strings) {
            stringsMap.put(entry.getKey(), entry.getValue());
        }

        return stringsMap;
    }

    static class ResourceBundleMock extends ResourceBundle {
        private Map<String, String> strings;

        ResourceBundleMock(Map<String, String> strings) {
            this.strings = strings;
        }

        @Override
        protected Object handleGetObject(String key) {
            return strings.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return null;
        }
    }

}
