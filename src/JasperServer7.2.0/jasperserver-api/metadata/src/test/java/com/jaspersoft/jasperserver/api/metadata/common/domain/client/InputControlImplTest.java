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
package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceVisitor;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

/**
 * @author Volodya Sabadosh
 */
public class InputControlImplTest extends UnitilsJUnit4 {
    private Mock<ResourceReference> dataTypeReference;
    private Mock<ResourceReference> listOfValuesReference;
    private Mock<ResourceReference> queryReference;
    private Mock<ResourceVisitor> visitor;

    @Test
    public void accept_notNullDependencies_success() {
        InputControlImpl inputControl = new InputControlImpl();
        inputControl.setDataType(dataTypeReference.getMock());
        inputControl.setListOfValues(listOfValuesReference.getMock());
        inputControl.setQuery(queryReference.getMock());

        inputControl.accept(visitor.getMock());

        dataTypeReference.assertInvoked().accept(visitor.getMock());
        listOfValuesReference.assertInvoked().accept(visitor.getMock());
        queryReference.assertInvoked().accept(visitor.getMock());
        visitor.assertInvoked().visit(inputControl);
    }

    @Test
    public void accept_nullDependencies_success() {
        InputControlImpl inputControl = new InputControlImpl();

        inputControl.accept(visitor.getMock());

        visitor.assertInvoked().visit(inputControl);
    }

}
