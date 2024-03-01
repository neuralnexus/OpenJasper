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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressions;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.adapters.DomELCommonSimpleDateFormats;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientTimestampTest extends BaseDTOPresentableTest<ClientTimestamp> {
    private static final String TIMESTAMP_VALUE_STRING = "2018-07-17T13:55:55.555";
    private static final String TIMESTAMP_VALUE_STRING_NON_PARCELABLE = "nonParse";

    private static final String TO_STRING_PATTERN = "ts'%s'";
    private static final String FORMATTED_TIMESTAMP_TO_STRING = String.format(TO_STRING_PATTERN, TIMESTAMP_VALUE_STRING);
    private static final String NON_PARCELABLE_TIMESTAMP_TO_STRING = String.format(TO_STRING_PATTERN, ClientExpressions.MISSING_REPRESENTATION);

    private static final Timestamp TIMESTAMP_VALUE_ALTERNATIVE = new Timestamp(1111111111111L);

    private ClientELVisitor clientELVisitor;
    private Timestamp timestampValue;

    @BeforeAll
    public void init() throws ParseException {
        Date timestampValueDate = DomELCommonSimpleDateFormats.isoTimestampFormat().parse(TIMESTAMP_VALUE_STRING);
        timestampValue = new Timestamp(timestampValueDate.getTime());
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientTimestamp> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setValue(TIMESTAMP_VALUE_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setValue(null)
        );
    }

    @Override
    protected ClientTimestamp createFullyConfiguredInstance() {
        return new ClientTimestamp()
                .setValue(timestampValue);
    }

    @Override
    protected ClientTimestamp createInstanceWithDefaultParameters() {
        return new ClientTimestamp();
    }

    @Override
    protected ClientTimestamp createInstanceFromOther(ClientTimestamp other) {
        return new ClientTimestamp(other);
    }

    @Test
    @Override
    public void generatedStringBeginsWithClassName() {
        assertEquals(FORMATTED_TIMESTAMP_TO_STRING, fullyConfiguredTestInstance.toString());
    }

    @Test
    public void generatedStringForNullTimestampReturnsExceptionString() {
        ClientTimestamp instance = new ClientTimestamp(((Timestamp) null));

        assertEquals(instance.toString(), NON_PARCELABLE_TIMESTAMP_TO_STRING);
    }

    @Test
    public void instanceCanBeCreatedWithTimestampValue() {
        ClientTimestamp instance = new ClientTimestamp(timestampValue);

        assertEquals(timestampValue, instance.getValue());
    }

    @Test
    public void valueOfForStringParameterReturnsInstance() {
        ClientTimestamp instance = ClientTimestamp.valueOf(TIMESTAMP_VALUE_STRING);

        assertEquals(timestampValue, instance.getValue());
    }

    @Test
    public void valueOfForNonParcelableStringParameterThrowsException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                ClientTimestamp.valueOf(TIMESTAMP_VALUE_STRING_NON_PARCELABLE);
            }
        });
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientTimestamp instance = new ClientTimestamp();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }
}
