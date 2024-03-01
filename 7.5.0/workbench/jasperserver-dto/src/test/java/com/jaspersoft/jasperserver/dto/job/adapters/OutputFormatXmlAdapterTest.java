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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientReportJobOutputFormatsWrapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class OutputFormatXmlAdapterTest {

    private static final String TEST_FORMAT_AS_STRING = "PDF";
    private static final Set<String> TEST_FORMATS_AS_STRINGS = new HashSet<String>(Collections.singletonList(TEST_FORMAT_AS_STRING));

    private static final String TEST_FORMAT_AS_STRING_ILLIGAL = "PDF_";
    private static final Set<String> TEST_FORMATS_AS_STRINGS_ILLIGAL = new HashSet<String>(Collections.singletonList(TEST_FORMAT_AS_STRING_ILLIGAL));

    private static final OutputFormat TEST_FORMAT = OutputFormat.PDF;
    private static final Set<OutputFormat> TEST_FORMATS = new HashSet<OutputFormat>(Collections.singletonList(TEST_FORMAT));

    private OutputFormatXmlAdapter objectUnderTest = new OutputFormatXmlAdapter();

    @Test
    public void unmarshal_nullValue_nullValue() throws Exception {
        Set<OutputFormat> actual = objectUnderTest.unmarshal(null);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithNullFormats_nullValue() throws Exception {
        ClientReportJobOutputFormatsWrapper wrapper = createClientReportJobOutputFormatsWrapperWithNullFormats();

        Set<OutputFormat> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithoutFormats_nullValue() throws Exception {
        ClientReportJobOutputFormatsWrapper wrapper = createClientReportJobOutputFormatsWrapperWithoutFormats();

        Set<OutputFormat> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithSomeFormats_someFormats() throws Exception {
        ClientReportJobOutputFormatsWrapper wrapper = createClientReportJobOutputFormatsWrapperWithFormats();

        Set<OutputFormat> actual = objectUnderTest.unmarshal(wrapper);
        assertEquals(TEST_FORMATS, actual);
    }

    @Test
    public void unmarshal_wrapperWithIlligalFormats_throwsException_IllegalArgumentException() {
        final ClientReportJobOutputFormatsWrapper wrapper = createClientReportJobOutputFormatsWrapperWithIllegalFormats();

        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        objectUnderTest.unmarshal(wrapper);
                    }
                }
        );
    }

    @Test
    public void marshal_nullValue_nullValue() throws Exception {
        ClientReportJobOutputFormatsWrapper actual = objectUnderTest.marshal(null);
        assertNull(actual);
    }

    @Test
    public void marshal_empty_nullValue() throws Exception {
        ClientReportJobOutputFormatsWrapper actual = objectUnderTest.marshal(new HashSet<OutputFormat>());
        assertNull(actual);
    }

    @Test
    public void marshal_someFormats_wrapper() throws Exception {
        ClientReportJobOutputFormatsWrapper expected = createClientReportJobOutputFormatsWrapperWithFormats();
        ClientReportJobOutputFormatsWrapper actual = objectUnderTest.marshal(TEST_FORMATS);

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private ClientReportJobOutputFormatsWrapper createClientReportJobOutputFormatsWrapperWithIllegalFormats() {
        return new ClientReportJobOutputFormatsWrapper().setFormats(TEST_FORMATS_AS_STRINGS_ILLIGAL);
    }

    private ClientReportJobOutputFormatsWrapper createClientReportJobOutputFormatsWrapperWithFormats() {
        return new ClientReportJobOutputFormatsWrapper().setFormats(TEST_FORMATS_AS_STRINGS);
    }

    private ClientReportJobOutputFormatsWrapper createClientReportJobOutputFormatsWrapperWithNullFormats() {
        return new ClientReportJobOutputFormatsWrapper().setFormats(new TreeSet<String>());
    }

    private ClientReportJobOutputFormatsWrapper createClientReportJobOutputFormatsWrapperWithoutFormats() {
        return new ClientReportJobOutputFormatsWrapper().setFormats(null);
    }

}