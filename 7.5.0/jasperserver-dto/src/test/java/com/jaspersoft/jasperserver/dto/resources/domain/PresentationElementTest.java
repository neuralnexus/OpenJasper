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

package com.jaspersoft.jasperserver.dto.resources.domain;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @author ativodar
 * @version $Id$
 * @see
 */
public class PresentationElementTest extends BaseDTOTest<PresentationElement> {

    @Override
    protected List<PresentationElement> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDescription("description2"),
                createFullyConfiguredInstance().setDescriptionId("descriptionId2"),
                createFullyConfiguredInstance().setLabel("label2"),
                createFullyConfiguredInstance().setLabelId("labelId2"),
                ((PresentationElement) createFullyConfiguredInstance().setName("name2")),
                // with null values
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setDescriptionId(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setLabelId(null),
                ((PresentationElement) createFullyConfiguredInstance().setName(null))
        );
    }

    @Override
    protected PresentationElement createFullyConfiguredInstance() {
        PresentationElement presentationElement = new PresentationElement();
        presentationElement.setDescription("description");
        presentationElement.setDescriptionId("descriptionId");
        presentationElement.setLabel("label");
        presentationElement.setLabelId("labelId");
        presentationElement.setName("name");
        return presentationElement;
    }

    @Override
    protected PresentationElement createInstanceWithDefaultParameters() {
        return new PresentationElement();
    }

    @Override
    protected PresentationElement createInstanceFromOther(PresentationElement other) {
        return new PresentationElement(other);
    }
}