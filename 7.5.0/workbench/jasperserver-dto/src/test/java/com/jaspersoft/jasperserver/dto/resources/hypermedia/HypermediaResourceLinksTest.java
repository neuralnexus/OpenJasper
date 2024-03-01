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

package com.jaspersoft.jasperserver.dto.resources.hypermedia;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class HypermediaResourceLinksTest extends BaseDTOTest<HypermediaResourceLinks> {

    @Override
    protected List<HypermediaResourceLinks> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setContent("content2"),
                createFullyConfiguredInstance().setSelf("self2"),
                // with null values
                createFullyConfiguredInstance().setContent(null),
                createFullyConfiguredInstance().setSelf(null)
        );
    }

    @Override
    protected HypermediaResourceLinks createFullyConfiguredInstance() {
        HypermediaResourceLinks HypermediaResourceLinks = new HypermediaResourceLinks();
        HypermediaResourceLinks.setContent("content");
        HypermediaResourceLinks.setSelf("self");
        return HypermediaResourceLinks;
    }

    @Override
    protected HypermediaResourceLinks createInstanceWithDefaultParameters() {
        return new HypermediaResourceLinks();
    }

    @Override
    protected HypermediaResourceLinks createInstanceFromOther(HypermediaResourceLinks other) {
        return new HypermediaResourceLinks(other);
    }
}
