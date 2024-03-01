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

package com.jaspersoft.jasperserver.dto.adhoc.query.el.range;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientRangeTest extends BaseDTOPresentableTest<ClientRange> {
    private static final ClientNumber CLIENT_FLOAT_START = new ClientNumber(23F);
    private static final ClientNumber CLIENT_FLOAT_END = new ClientNumber(24F);

    private static final ClientRangeBoundary START_BOUNDARY = new ClientRangeBoundary(CLIENT_FLOAT_START);
    private static final ClientRangeBoundary END_BOUNDARY = new ClientRangeBoundary(CLIENT_FLOAT_END);

    private static final ClientRangeBoundary START_BOUNDARY_ALTERNATIVE = new ClientRangeBoundary(new ClientNumber(25F));
    private static final ClientRangeBoundary END_BOUNDARY_ALTERNATIVE = new ClientRangeBoundary(new ClientNumber(26F));

    private ClientELVisitor clientELVisitor;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientRange> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setStart(START_BOUNDARY_ALTERNATIVE),
                createFullyConfiguredInstance().setEnd(END_BOUNDARY_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setStart(null),
                createFullyConfiguredInstance().setEnd(null)
        );
    }

    @Override
    protected ClientRange createFullyConfiguredInstance() {
        return new ClientRange()
                .setStart(START_BOUNDARY)
                .setEnd(END_BOUNDARY);
    }

    @Override
    protected ClientRange createInstanceWithDefaultParameters() {
        return new ClientRange();
    }

    @Override
    protected ClientRange createInstanceFromOther(ClientRange other) {
        return new ClientRange(other);
    }

    @Override
    public void generatedStringBeginsWithClassName() {
        // no need to run this test
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientRange expected, ClientRange actual) {
        assertNotSame(expected.getStart(), actual.getStart());
        assertNotSame(expected.getEnd(), actual.getEnd());
    }

    @Test
    public void visit_clientElVisitor_accepted() {
        ClientRange instance = createFullyConfiguredInstance();
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(instance);
        verify(clientELVisitor).visit(CLIENT_FLOAT_START);
        verify(clientELVisitor).visit(CLIENT_FLOAT_END);
    }

    @Test
    public void toString_emptyClientRange_stringWithMissingStartAndEnd() {
        ClientRange instance = createInstanceWithDefaultParameters();
        String toString = instance.toString();

       assertEquals("($missing$:$missing$)", toString);
    }

    @Test
    public void toString_nullStartClientRange_stringWithMissingEnd() {
        ClientRange instance = createInstanceWithDefaultParameters().setStart(START_BOUNDARY);
        String toString = instance.toString();

        assertEquals("(23.0:$missing$)", toString);
    }

    @Test
    public void toString_nullEndClientRange_stringWithMissingStart() {
        ClientRange instance = createInstanceWithDefaultParameters().setEnd(END_BOUNDARY);
        String toString = instance.toString();

        assertEquals("($missing$:24.0)", toString);
    }

    @Test
    public void toString_clientRange_stringWithStartAndEnd() {
        ClientRange instance = createFullyConfiguredInstance();
        String toString = instance.toString();

        assertEquals("(23.0:24.0)", toString);
    }
}
