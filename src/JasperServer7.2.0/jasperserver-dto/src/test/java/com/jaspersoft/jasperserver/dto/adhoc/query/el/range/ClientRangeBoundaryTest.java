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

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpression;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientLiteral;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ast.ClientELVisitor;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNull;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ClientRangeBoundaryTest extends BaseDTOPresentableTest<ClientRangeBoundary> {
    private static final ClientExpression BOUNDARY = new ClientNumber(23F);

    private static final ClientExpression BOUNDARY_ALTERNATIVE = new ClientNumber(24F);

    private ClientELVisitor clientELVisitor;

    @BeforeEach
    public void init() {
        clientELVisitor = Mockito.mock(ClientELVisitor.class);
    }

    @Override
    protected List<ClientRangeBoundary> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setBoundary(BOUNDARY_ALTERNATIVE),
                // with null values
                createFullyConfiguredInstance().setBoundary(null)
        );
    }

    @Override
    protected ClientRangeBoundary createFullyConfiguredInstance() {
        return new ClientRangeBoundary()
                .setBoundary(BOUNDARY);
    }

    @Override
    protected ClientRangeBoundary createInstanceWithDefaultParameters() {
        return new ClientRangeBoundary();
    }

    @Override
    protected ClientRangeBoundary createInstanceFromOther(ClientRangeBoundary other) {
        return new ClientRangeBoundary(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(ClientRangeBoundary expected, ClientRangeBoundary actual) {
        assertNotSame(expected.getBoundary(), actual.getBoundary());
    }

    @Test
    public void visit_clientElVisitor_accepted() {
        ClientNull clientExpression = new ClientNull();
        ClientRangeBoundary instance = new ClientRangeBoundary(clientExpression);
        instance.accept(clientELVisitor);

        verify(clientELVisitor).visit(clientExpression);
    }

    @Test
    public void visit_noBoundary_notAccepted() {
        ClientRangeBoundary instance = new ClientRangeBoundary();
        instance.accept(clientELVisitor);

        verify(clientELVisitor, never()).visit(instance);
    }

    @Test
    public void constructor_clientLiteral_instance() {
        ClientLiteral clientLiteral = new ClientNull();

        ClientRangeBoundary instance = new ClientRangeBoundary(clientLiteral);
        assertEquals(clientLiteral, instance.getBoundary());
    }

    @Test
    public void constructor_clientExpression_instance() {
        ClientRangeBoundary instance = new ClientRangeBoundary(BOUNDARY);
        assertEquals(BOUNDARY, instance.getBoundary());
    }
}
