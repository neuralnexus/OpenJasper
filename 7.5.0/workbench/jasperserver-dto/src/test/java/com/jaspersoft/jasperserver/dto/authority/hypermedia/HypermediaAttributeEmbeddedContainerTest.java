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

package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;

import java.util.Arrays;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;

/**
 * @author Alexei Skorodumov <askorodumov@tibco.com>
 * @version $Id$
 */
public class HypermediaAttributeEmbeddedContainerTest extends BaseDTOPresentableTest<HypermediaAttributeEmbeddedContainer> {

    @Override
    protected List<HypermediaAttributeEmbeddedContainer> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setRepositoryPermissions(Arrays.asList(new RepositoryPermission().setUri("uri"), new RepositoryPermission().setMask(24))),
                // with null values
                createFullyConfiguredInstance().setRepositoryPermissions(null)
        );
    }

    @Override
    protected HypermediaAttributeEmbeddedContainer createFullyConfiguredInstance() {
        HypermediaAttributeEmbeddedContainer attribute = new HypermediaAttributeEmbeddedContainer();
        attribute.setRepositoryPermissions(Arrays.asList(new RepositoryPermission(), new RepositoryPermission().setMask(23)));

        return attribute;
    }

    @Override
    protected HypermediaAttributeEmbeddedContainer createInstanceWithDefaultParameters() {
        return new HypermediaAttributeEmbeddedContainer();
    }

    @Override
    protected HypermediaAttributeEmbeddedContainer createInstanceFromOther(HypermediaAttributeEmbeddedContainer other) {
        return new HypermediaAttributeEmbeddedContainer(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaAttributeEmbeddedContainer expected, HypermediaAttributeEmbeddedContainer actual) {
        assertNotSameCollection(expected.getRepositoryPermissions(), actual.getRepositoryPermissions());
    }
}
