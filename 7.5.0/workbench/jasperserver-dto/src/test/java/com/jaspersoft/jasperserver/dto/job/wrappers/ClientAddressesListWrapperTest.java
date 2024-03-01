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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientAddressesListWrapperTest extends BaseDTOPresentableTest<ClientAddressesListWrapper> {

    private static final String TEST_ADDRESS = "TEST_ADDRESS";
    private static final List<String> TEST_ADDRESSES = Collections.singletonList(TEST_ADDRESS);

    private static final String TEST_ADDRESS_1 = "TEST_ADDRESS_1";
    private static final List<String> TEST_ADDRESSES_1 = Collections.singletonList(TEST_ADDRESS_1);

    @Override
    protected List<ClientAddressesListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAddresses(TEST_ADDRESSES_1),
                // null values
                createFullyConfiguredInstance().setAddresses(null)
        );
    }

    @Override
    protected ClientAddressesListWrapper createFullyConfiguredInstance() {
        ClientAddressesListWrapper instance = createInstanceWithDefaultParameters();
        return instance
                .setAddresses(TEST_ADDRESSES);
    }

    @Override
    protected ClientAddressesListWrapper createInstanceWithDefaultParameters() {
        return new ClientAddressesListWrapper();
    }

    @Override
    protected ClientAddressesListWrapper createInstanceFromOther(ClientAddressesListWrapper other) {
        return new ClientAddressesListWrapper(other);
    }
}