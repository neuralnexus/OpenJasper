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

package com.jaspersoft.jasperserver.dto.adhoc.query.expansion;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientLevelExpansionTest extends BaseDTOTest<ClientLevelExpansion> {
    private static final String LEVEL_REFERENCE = "levelReference";
    private static final String LEVEL_REFERENCE_ALTERNATIVE = "levelReferenceAlternative";

    @Override
    protected List<ClientLevelExpansion> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAggregationLevel(false),
                createFullyConfiguredInstance().setExpanded(false),
                createFullyConfiguredInstance().setLevelReference(LEVEL_REFERENCE_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setAggregationLevel(null),
                createFullyConfiguredInstance().setLevelReference(null)
        );
    }

    @Override
    protected ClientLevelExpansion createFullyConfiguredInstance() {
        return new ClientLevelExpansion()
                .setAggregationLevel(true)
                .setExpanded(true)
                .setLevelReference(LEVEL_REFERENCE);
    }

    @Override
    protected ClientLevelExpansion createInstanceWithDefaultParameters() {
        return new ClientLevelExpansion();
    }

    @Override
    protected ClientLevelExpansion createInstanceFromOther(ClientLevelExpansion other) {
        return new ClientLevelExpansion(other);
    }

    @Test
    public void getReturnsLevelReference() {
        ClientLevelExpansion instance = createFullyConfiguredInstance();
        assertEquals(LEVEL_REFERENCE, instance.get());
    }

    @Test
    public void getFieldReferenceReturnsLevelReference() {
        ClientLevelExpansion instance = createFullyConfiguredInstance();
        assertEquals(LEVEL_REFERENCE, instance.getFieldReference());
    }
}
