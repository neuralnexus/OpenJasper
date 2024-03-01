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

package com.jaspersoft.jasperserver.dto.common;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class PatchItemTest extends BaseDTOPresentableTest<PatchItem> {

    @Override
    protected List<PatchItem> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setField("field2"),
                createFullyConfiguredInstance().setValue("value2"),
                createFullyConfiguredInstance().setExpression("expression2"),
                // with null values
                createFullyConfiguredInstance().setField(null),
                createFullyConfiguredInstance().setValue(null),
                createFullyConfiguredInstance().setExpression(null)
        );
    }

    @Override
    protected PatchItem createFullyConfiguredInstance() {
        PatchItem patchItem = new PatchItem();
        patchItem.setField("field1");
        patchItem.setValue("value1");
        patchItem.setExpression("expression1");
        return patchItem;
    }

    @Override
    protected PatchItem createInstanceWithDefaultParameters() {
        return new PatchItem();
    }

    @Override
    protected PatchItem createInstanceFromOther(PatchItem other) {
        return new PatchItem(other);
    }

    @Test
    public void toStringForPatchItemsWithExpressionIsCorrect() {
        PatchItem patchItem1 = new PatchItem().setExpression("expression1");

        assertEquals("expression1", patchItem1.toString());
    }

    @Test
    public void toStringForPatchItemsWithNoExpressionIsCorrect() {
        PatchItem patchItem1 = new PatchItem().setField("field").setValue("value");

        assertEquals("field = \"value\"", patchItem1.toString());
    }

    @Test
    public void toStringForPatchItemsWithNoExpressionAndNoValueIsCorrect() {
        PatchItem patchItem1 = new PatchItem().setField("field");

        assertEquals("field = null", patchItem1.toString());
    }

    @Override
    public void generatedStringBeginsWithClassName() {
        // toStringMethodHasCustom logic
    }
}
