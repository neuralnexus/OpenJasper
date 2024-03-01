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

import com.jaspersoft.jasperserver.dto.authority.ClientAttribute;
import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.CustomAssertions.assertNotSameCollection;
import static org.junit.Assert.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class HypermediaAttributesListWrapperTest extends BaseDTOPresentableTest<HypermediaAttributesListWrapper> {

    @Override
    protected List<HypermediaAttributesListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setProfileAttributes(Arrays.asList(new HypermediaAttribute().setDescription("description")
                        , new HypermediaAttribute().setHolder("holder2"))),
                // with null values
                createFullyConfiguredInstance().setProfileAttributes(null)
        );
    }

    @Override
    protected HypermediaAttributesListWrapper createFullyConfiguredInstance() {
        HypermediaAttributesListWrapper attribute = new HypermediaAttributesListWrapper();
        attribute.setProfileAttributes(Arrays.asList(new HypermediaAttribute(), ((HypermediaAttribute) new HypermediaAttribute().setHolder("holder"))));

        return attribute;
    }

    @Override
    protected HypermediaAttributesListWrapper createInstanceWithDefaultParameters() {
        return new HypermediaAttributesListWrapper();
    }

    @Override
    protected HypermediaAttributesListWrapper createInstanceFromOther(HypermediaAttributesListWrapper other) {
        return new HypermediaAttributesListWrapper(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaAttributesListWrapper expected, HypermediaAttributesListWrapper actual) {
        assertNotSameCollection(expected.getProfileAttributes(), actual.getProfileAttributes());
    }

    @Test
    public void createdWithParamsHypermediaAttributesListWrapperTransformsClientAttributeToHypermediaAttribute() {
        ClientAttribute attr = new ClientAttribute().setDescription("description");
        HypermediaAttribute hypermediaAttribute = new HypermediaAttribute(attr);
        List<ClientAttribute> list = Collections.singletonList(attr);
        HypermediaAttributesListWrapper hypermediaAttributesListWrapper2 = new HypermediaAttributesListWrapper(list);

        assertEquals(hypermediaAttribute, hypermediaAttributesListWrapper2.getProfileAttributes().get(0));
    }

    @Test
    public void createdWithParamsHypermediaAttributesListWrapperTransformsClientCopyHypermediaAttributeReference() {
        ClientAttribute attr = new HypermediaAttribute().setHolder("holder");
        List<ClientAttribute> list = Collections.singletonList(attr);
        HypermediaAttributesListWrapper hypermediaAttributesListWrapper2 = new HypermediaAttributesListWrapper(list);

        assertEquals(attr, hypermediaAttributesListWrapper2.getProfileAttributes().get(0));
    }
}
