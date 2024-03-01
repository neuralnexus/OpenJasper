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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alexei Skorodumov <askorodumov@tibco.com>
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
public class LinkTest extends BaseDTOTest<Link> {

    @Override
    protected List<Link> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setHref("href2"),
                // with null values
                createFullyConfiguredInstance().setHref(null)
        );
    }

    @Override
    protected Link createFullyConfiguredInstance() {
        Link attribute = new Link();
        attribute.setHref("href");

        return attribute;
    }

    @Override
    protected Link createInstanceWithDefaultParameters() {
        return new Link();
    }

    @Override
    protected Link createInstanceFromOther(Link other) {
        return new Link(other);
    }

}
