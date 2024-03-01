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
import static org.junit.jupiter.api.Assertions.assertNotSame;


/**
 * @author Alexei Skorodumov <askorodumov@tibco.com>
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id: WarningDescriptorTest.java 63760 2016-07-05 18:59:28Z agodovan $
 */

public class WarningDescriptorTest extends BaseDTOPresentableTest<WarningDescriptor> {

    @Override
    protected List<WarningDescriptor> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCode("code2"),
                createFullyConfiguredInstance().setMessage("message2"),
                createFullyConfiguredInstance().setParameters(new String[]{"param3", "param4"}),
                // with null values
                createFullyConfiguredInstance().setCode(null),
                createFullyConfiguredInstance().setMessage(null),
                createFullyConfiguredInstance().setParameters(null)
        );
    }

    @Override
    protected WarningDescriptor createFullyConfiguredInstance() {
        WarningDescriptor warningDescriptor = new WarningDescriptor();
        warningDescriptor.setCode("code");
        warningDescriptor.setMessage("message");
        warningDescriptor.setParameters(new String[]{"param1", "param2"});

        return warningDescriptor;
    }

    @Override
    protected WarningDescriptor createInstanceWithDefaultParameters() {
        return new WarningDescriptor();
    }

    @Override
    protected WarningDescriptor createInstanceFromOther(WarningDescriptor other) {
        return new WarningDescriptor(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(WarningDescriptor expected, WarningDescriptor actual) {
        assertNotSame(expected.getParameters(), actual.getParameters());
    }

    @Test
    public void createdWarningDescriptorsAreEquals() {
        WarningDescriptor warningDescriptor1 = createFullyConfiguredInstance();
        WarningDescriptor warningDescriptor2 = new WarningDescriptor(warningDescriptor1.getCode(), warningDescriptor1.getParameters(), warningDescriptor1.getMessage());

        assertEquals(warningDescriptor1, warningDescriptor2);
    }
}
