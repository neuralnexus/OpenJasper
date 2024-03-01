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

package com.jaspersoft.jasperserver.dto.resources.domain;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 * @see
 */
public class PresentationSingleElementTest extends BaseDTOTest<PresentationSingleElement> {

    @Override
    protected List<PresentationSingleElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setName("name2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setLabelId("labelId2"),
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setDescriptionId("descriptionId2"),
                createFullyConfiguredInstance().setMask("mask2"),
                createFullyConfiguredInstance().setMaskId("maskId2"),
                createFullyConfiguredInstance().setType("type2"),
                createFullyConfiguredInstance().setResourcePath("resourcePath2"),
                createFullyConfiguredInstance().setAggregation("aggregation2"),
                createFullyConfiguredInstance().setHierarchicalName("hierarchical2"),
                createFullyConfiguredInstance().setKind(PresentationSingleElement.Kind.level),
                // with null values
                createFullyConfiguredInstance().setName(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setLabelId(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setDescriptionId(null),
                createFullyConfiguredInstance().setMask(null),
                createFullyConfiguredInstance().setMaskId(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setResourcePath(null),
                createFullyConfiguredInstance().setAggregation(null),
                createFullyConfiguredInstance().setHierarchicalName(null),
                createFullyConfiguredInstance().setKind(null)
        );
    }

    @Override
    protected PresentationSingleElement createFullyConfiguredInstance() {
        return new PresentationSingleElement()
                .setName("name")
                .setLabel("label")
                .setLabelId("labelId")
                .setDescription("description")
                .setDescriptionId("descriptionId")
                .setMask("mask")
                .setMaskId("maskId")
                .setType("type")
                .setResourcePath("resourcePath")
                .setAggregation("aggregation")
                .setHierarchicalName("hierarchical")
                .setKind(PresentationSingleElement.Kind.measure);
    }

    @Override
    protected PresentationSingleElement createInstanceWithDefaultParameters() {
        return new PresentationSingleElement();
    }

    @Override
    protected PresentationSingleElement createInstanceFromOther(PresentationSingleElement other) {
        return new PresentationSingleElement(other);
    }

}