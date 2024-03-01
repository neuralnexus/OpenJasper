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

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import org.junit.Test;

import java.util.List;

import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.buildProperties;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.checkPropValues;
import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.toPropertyValues;
import static java.util.Arrays.*;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class CommonErrorCodeTest {

    @Test
    public void toPropertyValues_propertiesIsNull_returnNull() {
        assertNull(toPropertyValues(null));
    }

    @Test
    public void toPropertyValues_propertiesIncludeOneProperty_returnPropertyValue() {
        List<ClientProperty> propertyList = asList(new ClientProperty().setKey("someKey").setValue("someValue"));
        assertArrayEquals(new String[]{"someValue"}, toPropertyValues(propertyList));
    }

    @Test
    public void buildProperties_paramNamesAndParamValueAreNull_throwIllegalArgumentEx() {
        assertNull(buildProperties(null, null));
    }

    @Test
    public void buildProperties_paramNamesAndParamValuesHaveSizeOne_returnProperties() {
        ClientProperty property = new ClientProperty().setKey("someParamName").setValue("someParamValue");
        assertEquals(singletonList(property),
                buildProperties(new String[]{property.getKey()}, new String[]{property.getValue()}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void buildProperties_paramNamesHasSize2ButParamValuesHasSizeOne_throwIllegalArgumentEx() {
        buildProperties(new String[]{"someParamKey1", "someParamKey2"}, new String[]{"someParamValue"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkPropValues_paramNamesHasSize2ButParamValuesHasSizeOne_throwIllegalArgumentEx() {
        checkPropValues(new String[]{"someParamKey1", "someParamKey2"}, new String[]{"someParamValue"});
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkPropValues_paramNamesIsNullButParamValuesHasSizeOne_throwIllegalArgumentEx() {
        checkPropValues(null, new String[]{"someParamValue"});
    }

}
