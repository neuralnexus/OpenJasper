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

package com.jaspersoft.jasperserver.dto.authority.hypermedia;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Alexei Skorodumov <askorodumov@tibco.com>
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
public class HypermediaAttributeTest extends BaseDTOPresentableTest<HypermediaAttribute> {

    @Override
    protected List<HypermediaAttribute> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("testName2"),
                createFullyConfiguredInstance().setValue("testValue2"),
                createFullyConfiguredInstance().setPermissionMask(33),
                createFullyConfiguredInstance().setSecure(false),
                createFullyConfiguredInstance().setInherited(false),
                createFullyConfiguredInstance().setDescription("testDescription2"),
                createFullyConfiguredInstance().setHolder("testHolder2"),
                createFullyConfiguredInstance().setEmbedded(new HypermediaAttributeEmbeddedContainer()),
                createFullyConfiguredInstance().setLinks(new HypermediaAttributeLinks()),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setValue(null),
                createFullyConfiguredInstance().setPermissionMask(null),
                createFullyConfiguredInstance().setSecure(null),
                createFullyConfiguredInstance().setInherited(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setHolder(null),
                createFullyConfiguredInstance().setEmbedded(null),
                createFullyConfiguredInstance().setLinks(null)
        );
    }

    @Override
    protected HypermediaAttribute createFullyConfiguredInstance() {
        HypermediaAttribute attribute = new HypermediaAttribute();
        attribute.setName("testName");
        attribute.setValue("testValue");
        attribute.setPermissionMask(32);
        attribute.setSecure(true);
        attribute.setInherited(true);
        attribute.setDescription("testDescription");
        attribute.setHolder("testHolder");
        attribute.setEmbedded(new HypermediaAttributeEmbeddedContainer().setRepositoryPermissions(Collections.singletonList(new RepositoryPermission())));
        attribute.setLinks(new HypermediaAttributeLinks().setPermission(new Link().setHref("href")));

        return attribute;
    }

    @Override
    protected HypermediaAttribute createInstanceWithDefaultParameters() {
        return new HypermediaAttribute();
    }

    @Override
    protected HypermediaAttribute createInstanceFromOther(HypermediaAttribute other) {
        return new HypermediaAttribute(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaAttribute expected, HypermediaAttribute actual) {
        assertNotSame(expected.getEmbedded(), actual.getEmbedded());
        assertNotSame(expected.getLinks(), actual.getLinks());
    }
}
