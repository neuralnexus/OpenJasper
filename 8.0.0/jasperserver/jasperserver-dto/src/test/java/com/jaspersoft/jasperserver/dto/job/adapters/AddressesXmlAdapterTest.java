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

package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientAddressesListWrapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class AddressesXmlAdapterTest {

    private static final String TEST_ADDRESS = "TEST_ADDRESS";
    private static final String TEST_ADDRESS_1 = "TEST_ADDRESS_1";
    private static final List<String> TEST_ADDRESSES = Arrays.asList(TEST_ADDRESS, TEST_ADDRESS_1);

    private AddressesXmlAdapter objectUnderTest = new AddressesXmlAdapter();

    @Test
    public void unmarshal_nullValue_nullValue() throws Exception {
        List<String> actual = objectUnderTest.unmarshal(null);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithoutAddresses_nullValue() throws Exception {
        ClientAddressesListWrapper wrapper = createClientAddressesListWrapperWithoutAddresses();

        List<String> actual = objectUnderTest.unmarshal(wrapper);
        assertNull(actual);
    }

    @Test
    public void unmarshal_wrapperWithSomeAddresses_someAddresses() throws Exception {
        ClientAddressesListWrapper wrapper = createClientAddressesListWrapperWithAddresses();

        List<String> actual = objectUnderTest.unmarshal(wrapper);
        assertEquals(TEST_ADDRESSES, actual);
    }

    @Test
    public void marshal_nullValue_nullValue() throws Exception {
        ClientAddressesListWrapper actual = objectUnderTest.marshal(null);
        assertNull(actual);
    }

    @Test
    public void marshal_someAddress_wrapper() throws Exception {
        ClientAddressesListWrapper expected = createClientAddressesListWrapperWithAddresses();
        ClientAddressesListWrapper actual = objectUnderTest.marshal(TEST_ADDRESSES);

        assertEquals(expected, actual);
    }

    /*
     * Helpers
     */

    private ClientAddressesListWrapper createClientAddressesListWrapperWithAddresses() {
        return new ClientAddressesListWrapper().setAddresses(TEST_ADDRESSES);
    }

    private ClientAddressesListWrapper createClientAddressesListWrapperWithoutAddresses() {
        return new ClientAddressesListWrapper();
    }

}