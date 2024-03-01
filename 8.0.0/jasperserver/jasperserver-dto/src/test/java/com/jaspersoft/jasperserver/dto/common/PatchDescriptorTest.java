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

package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class PatchDescriptorTest extends BaseDTOPresentableTest<PatchDescriptor> {

    @Override
    protected List<PatchDescriptor> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setVersion(3),
                createFullyConfiguredInstance().setItems(Arrays.asList(new PatchItem().setExpression("expression2"),
                        new PatchItem().setValue("24").setField("field2"))),
                // with null values
                createFullyConfiguredInstance().setItems(null)
        );
    }

    @Override
    protected PatchDescriptor createFullyConfiguredInstance() {
        PatchDescriptor patchDescriptor = new PatchDescriptor();
        patchDescriptor.setVersion(2);
        patchDescriptor.setItems(Arrays.asList(new PatchItem().setExpression("expression"),
                new PatchItem().setValue("23").setField("field")));
        return patchDescriptor;
    }

    @Override
    protected PatchDescriptor createInstanceWithDefaultParameters() {
        return new PatchDescriptor();
    }

    @Override
    protected PatchDescriptor createInstanceFromOther(PatchDescriptor other) {
        return new PatchDescriptor(other);
    }


    @Test
    public void createdPatchDescriptorWithOneParamsReturnCorrectInstance() {
        PatchDescriptor patchDescriptor = new PatchDescriptor(4);

        assertEquals(4, patchDescriptor.getVersion());
    }

    @Test
    public void createdPatchDescriptorWithTwoParamsReturnCorrectInstance() {
        PatchDescriptor patchDescriptor1 = createFullyConfiguredInstance();
        PatchDescriptor patchDescriptor2 = new PatchDescriptor(Arrays.asList(new PatchItem().setExpression("expression")
                , new PatchItem().setValue("23").setField("field")), 2);

        assertEquals(patchDescriptor1, patchDescriptor2);
    }

    @Test
    public void addingPatchDescriptorFieldCreatesNewList() {
        PatchDescriptor patchDescriptor1 = new PatchDescriptor();
        patchDescriptor1.field("test", "testValue");

        assertEquals(1, patchDescriptor1.getItems().size());
    }

    @Test
    public void addingPatchDescriptorFieldUpdateItemsList() {
        PatchDescriptor patchDescriptor1 = new PatchDescriptor().setItems(new ArrayList<PatchItem>() {{
            add(new PatchItem());
        }});
        patchDescriptor1.field("test", "testValue");

        ArrayList<PatchItem> items = new ArrayList<PatchItem>() {{
            add(new PatchItem());
        }};
        items.add(new PatchItem().setField("test").setValue("testValue"));

        assertEquals(items, patchDescriptor1.getItems());
    }

    @Test
    public void addingPatchDescriptorExpressionCreatesNewList() {
        PatchDescriptor patchDescriptor1 = new PatchDescriptor();
        patchDescriptor1.expression("test");

        assertEquals(1, patchDescriptor1.getItems().size());
    }

    @Test
    public void addingPatchDescriptorExpressionUpdateItemsList() {
        PatchDescriptor patchDescriptor1 = new PatchDescriptor().setItems(new ArrayList<PatchItem>() {{
            add(new PatchItem());
        }});
        patchDescriptor1.expression("test");

        ArrayList<PatchItem> items = new ArrayList<PatchItem>() {{
            add(new PatchItem());
        }};
        items.add(new PatchItem().setExpression("test"));

        assertEquals(items, patchDescriptor1.getItems());
    }
}
