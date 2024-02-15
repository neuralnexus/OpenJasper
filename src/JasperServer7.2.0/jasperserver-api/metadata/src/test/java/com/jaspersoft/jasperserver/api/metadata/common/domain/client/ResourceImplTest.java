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

import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceVisitor;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

/**
 * @author Volodya Sabadosh
 */
public class ResourceImplTest extends UnitilsJUnit4 {
    private Mock<ResourceVisitor> visitor;

    @Test
    public void accept_success() {
        ResourceImpl resource = new ResourceImpl() {
            @Override
            protected Class getImplementingItf() {
                return null;
            }
        };

        resource.accept(visitor.getMock());

        visitor.assertInvoked().visit(resource);
    }
}
