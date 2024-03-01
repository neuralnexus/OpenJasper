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
package com.jaspersoft.jasperserver.dto.adhoc.dataset;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientDatasetFieldReferenceTest extends BaseDTOJSONPresentableTest<ClientDatasetFieldReference> {

    private static final String TEST_REFERENCE = "TEST_REFERENCE";
    private static final String TEST_REFERENCE_ALT = "TEST_REFERENCE_ALT";

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_ALT = "TEST_TYPE_ALT";

    @Override
    protected List<ClientDatasetFieldReference> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setReference(TEST_REFERENCE_ALT),
                createFullyConfiguredInstance().setType(TEST_TYPE_ALT),
                createFullyConfiguredInstance().setReference(null),
                createFullyConfiguredInstance().setType(null)
        );
    }

    @Override
    protected ClientDatasetFieldReference createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setReference(TEST_REFERENCE)
                .setType(TEST_TYPE);
    }

    @Override
    protected ClientDatasetFieldReference createInstanceWithDefaultParameters() {
        return new ClientDatasetFieldReference();
    }

    @Override
    protected ClientDatasetFieldReference createInstanceFromOther(ClientDatasetFieldReference other) {
        return new ClientDatasetFieldReference(other);
    }
}