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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceVisitor;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * @author Volodya sabadosh
 */
public class MondrianConnectionImplTest {
    private ResourceReference schemaReference = mock(ResourceReference.class);
    private ResourceReference datasourceReference = mock(ResourceReference.class);
    private ResourceVisitor visitor = mock(ResourceVisitor.class);

    @Test
    public void accept_notNullDependencies_success() {
        MondrianConnectionImpl mondrianConnection = new MondrianConnectionImpl();
        mondrianConnection.setDataSource(datasourceReference);
        mondrianConnection.setSchema(schemaReference);

        mondrianConnection.accept(visitor);

        verify(schemaReference, times(1)).accept(visitor);
        verify(datasourceReference, times(1)).accept(visitor);
        verify(visitor, times(1)).visit(mondrianConnection);
    }

    @Test
    public void accept_nullDependencies_success() {
        MondrianConnectionImpl mondrianConnection = new MondrianConnectionImpl();

        mondrianConnection.accept(visitor);

        verify(visitor, times(1)).visit(mondrianConnection);
    }

}
