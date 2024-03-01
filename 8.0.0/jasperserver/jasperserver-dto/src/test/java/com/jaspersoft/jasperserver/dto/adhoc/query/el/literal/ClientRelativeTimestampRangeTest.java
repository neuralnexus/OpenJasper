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
package com.jaspersoft.jasperserver.dto.adhoc.query.el.literal;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientRelativeTimestampRangeTest extends BaseDTOTest<ClientRelativeTimestampRange> {
    private static final String DATE_RANGE_VALUE = "YEAR";
    private static final String DATE_RANGE_VALUE_ALTERNATIVE = "DAY";
    private static final String NOT_DATE_RANGE_VALUE = "NOT_DATE_RANGE";

    @Override
    protected List<ClientRelativeTimestampRange> prepareInstancesWithAlternativeParameters() {
        return Collections.singletonList(
                createFullyConfiguredInstance().setValue(DATE_RANGE_VALUE_ALTERNATIVE)
        );
    }

    @Override
    protected ClientRelativeTimestampRange createFullyConfiguredInstance() {
        return new ClientRelativeTimestampRange()
                .setValue(DATE_RANGE_VALUE);
    }

    @Override
    protected ClientRelativeTimestampRange createInstanceWithDefaultParameters() {
        return new ClientRelativeTimestampRange();
    }

    @Override
    protected ClientRelativeTimestampRange createInstanceFromOther(ClientRelativeTimestampRange other) {
        return new ClientRelativeTimestampRange(other);
    }

    @Test
    @Override
    public void generatedStringBeginsWithClassName() {
        assertEquals(DATE_RANGE_VALUE, fullyConfiguredTestInstance.toString());
    }

    @Test
    public void instanceCanBeCreatedWithStringValue() {
        ClientRelativeTimestampRange instance = new ClientRelativeTimestampRange(DATE_RANGE_VALUE);

        assertEquals(DATE_RANGE_VALUE, instance.getValue());
    }

    @Test
    public void nullValueCanNotBeSet() {
        final ClientRelativeTimestampRange instance = new ClientRelativeTimestampRange(DATE_RANGE_VALUE);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.setValue(null);
            }
        });
    }

    @Test
    @Disabled("FIXME: This is handled by an annotation now, please update or remove test")
    public void incorrectTimestampRangeValueCanNotBeSet() {
        final ClientRelativeTimestampRange instance = new ClientRelativeTimestampRange(DATE_RANGE_VALUE);

        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                instance.setValue(NOT_DATE_RANGE_VALUE);
            }
        });
    }
}
