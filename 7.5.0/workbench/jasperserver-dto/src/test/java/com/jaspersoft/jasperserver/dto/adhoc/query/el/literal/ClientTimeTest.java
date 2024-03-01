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

import java.sql.Time;
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

public class ClientTimeTest extends BaseDTOPresentableTest<ClientTime> {
    private static final String TIME_VALUE_STRING = "13:55:55.555";
    private static final String TIME_VALUE_STRING_NON_PARCELABLE = "nonParse";

    private static final String TO_STRING_PATTERN = "t'%s'";
    private static final String FORMATTED_TIME_TO_STRING = String.format(TO_STRING_PATTERN, TIME_VALUE_STRING);
    private static final String NON_PARCELABLE_TIME_TO_STRING = String.format(TO_STRING_PATTERN, ClientExpressions.MISSING_REPRESENTATION);

    private static final Time TIME_VALUE_ALTERNATIVE = new Time(1111111111111L);

    private ClientELVisitor clientELVisitor;
    private Time timeValue;

    @BeforeAll
    public void init() throws ParseException {
        Date timeValueDate = DomELCommonSimpleDateFormats.timeFormat().parse(TIME_VALUE_STRING);
        timeValue = new Time(timeValueDate.getTime());
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientTime> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setValue(TIME_VALUE_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setValue(null)
        );
    }

    @Override
    protected ClientTime createFullyConfiguredInstance() {
        return new ClientTime()
                .setValue(timeValue);
    }

    @Override
    protected ClientTime createInstanceWithDefaultParameters() {
        return new ClientTime();
    }

    @Override
    protected ClientTime createInstanceFromOther(ClientTime other) {
        return new ClientTime(other);
    }

    @Test
    @Override
    public void generatedStringBeginsWithClassName() {
        assertEquals(FORMATTED_TIME_TO_STRING, fullyConfiguredTestInstance.toString());
    }

    @Test
    public void generatedStringForNullTimeReturnsExceptionString() {
        ClientTime instance = new ClientTime(((Time) null));

        assertEquals(instance.toString(), NON_PARCELABLE_TIME_TO_STRING);
    }

    @Test
    public void instanceCanBeCreatedWithTimeValue() {
        ClientTime instance = new ClientTime(timeValue);

        assertEquals(timeValue, instance.getValue());
    }

    @Test
    public void valueOfForStringParameterReturnsInstance() {
        ClientTime instance = ClientTime.valueOf(TIME_VALUE_STRING);

        assertEquals(timeValue, instance.getValue());
    }

    @Test
    public void valueOfForNonParcelableStringParameterThrowsException() {
        assertThrows(IllegalArgumentException.class, new Executable() {
            @Override
            public void execute() {
                ClientTime.valueOf(TIME_VALUE_STRING_NON_PARCELABLE);
            }
        });
    }

    @Test
    public void clientELVisitorCanBeAccepted() {
        ClientTime instance = new ClientTime();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
    }
}
