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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Alexei Skorodumov <askorodumov@tibco.com>
 * @version $Id$
 */
public class HypermediaAttributeLinksTest extends BaseDTOPresentableTest<HypermediaAttributeLinks> {

    @Override
    protected List<HypermediaAttributeLinks> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setPermission(new Link().setHref("href2")),
                // with null values
                createFullyConfiguredInstance().setPermission(null)
        );
    }

    @Override
    protected HypermediaAttributeLinks createFullyConfiguredInstance() {
        HypermediaAttributeLinks attribute = new HypermediaAttributeLinks();
        attribute.setPermission(new Link().setHref("href"));

        return attribute;
    }

    @Override
    protected HypermediaAttributeLinks createInstanceWithDefaultParameters() {
        return new HypermediaAttributeLinks();
    }

    @Override
    protected HypermediaAttributeLinks createInstanceFromOther(HypermediaAttributeLinks other) {
        return new HypermediaAttributeLinks(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(HypermediaAttributeLinks expected, HypermediaAttributeLinks actual) {
        assertNotSame(expected.getPermission(), actual.getPermission());
    }
}
