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
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientFlatDatasetFieldReference.FlatDatasetFieldKind.AGGREGATION_KIND;
import static com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientFlatDatasetFieldReference.FlatDatasetFieldKind.DETAIL_KIND;
import static com.jaspersoft.jasperserver.dto.adhoc.dataset.ClientFlatDatasetFieldReference.FlatDatasetFieldKind.GROUP_KIND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientFlatDatasetFieldReferenceTest extends BaseDTOJSONPresentableTest<ClientFlatDatasetFieldReference> {

    private static final String TEST_KIND = "TEST_KIND";
    private static final String TEST_KIND_ALT = "TEST_KIND_ALT";

    private static final String TEST_GROUP_REF = "TEST_GROUP_REF";
    private static final String TEST_GROUP_REF_ALT = "TEST_GROUP_REF_ALT";

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_ALT = "TEST_TYPE_ALT";

    private static final String TEST_REFERENCE = "TEST_REFERENCE";
    private static final String TEST_REFERENCE_ALT = "TEST_REFERENCE_ALT";

    @Override
    protected List<ClientFlatDatasetFieldReference> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setKind(TEST_KIND_ALT),
                createFullyConfiguredInstance().setGroupRef(TEST_GROUP_REF_ALT),
                (ClientFlatDatasetFieldReference)createFullyConfiguredInstance().setType(TEST_TYPE_ALT),
                (ClientFlatDatasetFieldReference)createFullyConfiguredInstance().setReference(TEST_REFERENCE_ALT),
                createFullyConfiguredInstance().setKind(null),
                createFullyConfiguredInstance().setGroupRef(null),
                (ClientFlatDatasetFieldReference)createFullyConfiguredInstance().setType(null),
                (ClientFlatDatasetFieldReference)createFullyConfiguredInstance().setReference(null)
        );
    }

    @Override
    protected ClientFlatDatasetFieldReference createFullyConfiguredInstance() {
        ClientFlatDatasetFieldReference instance = createInstanceWithDefaultParameters()
                .setKind(TEST_KIND)
                .setGroupRef(TEST_GROUP_REF);
        return (ClientFlatDatasetFieldReference)instance
                .setType(TEST_TYPE)
                .setReference(TEST_REFERENCE);
    }

    @Override
    protected ClientFlatDatasetFieldReference createInstanceWithDefaultParameters() {
        return new ClientFlatDatasetFieldReference();
    }

    @Override
    protected ClientFlatDatasetFieldReference createInstanceFromOther(ClientFlatDatasetFieldReference other) {
        return new ClientFlatDatasetFieldReference(other);
    }

    @Test
    public void verifyFlatDatasetFieldKind_GROUP_KIND() {
        assertEquals("group", GROUP_KIND.toString());
    }

    @Test
    public void verifyFlatDatasetFieldKind_AGGREGATION_KIND() {
        assertEquals("aggregation", AGGREGATION_KIND.toString());
    }
    @Test
    public void verifyFlatDatasetFieldKind_DETAIL_KIND() {
        assertEquals("detail", DETAIL_KIND.toString());
    }

    @Test
    public void verify_FlatDatasetFieldKind_values() {
        ClientFlatDatasetFieldReference.FlatDatasetFieldKind[] values = ClientFlatDatasetFieldReference.FlatDatasetFieldKind.values();
        assertEquals(3, values.length);
        assertTrue(Arrays.asList(values).contains(GROUP_KIND));
        assertTrue(Arrays.asList(values).contains(AGGREGATION_KIND));
        assertTrue(Arrays.asList(values).contains(DETAIL_KIND));
    }
}