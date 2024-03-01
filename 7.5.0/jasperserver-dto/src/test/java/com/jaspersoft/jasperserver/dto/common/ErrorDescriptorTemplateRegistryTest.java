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

import org.junit.Test;

import static com.jaspersoft.jasperserver.dto.common.CommonErrorCode.MANDATORY_PARAMETER_ERROR;
import static com.jaspersoft.jasperserver.dto.common.ErrorDescriptorTemplateRegistry.fromCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Volodya Sabadosh
 */
public class ErrorDescriptorTemplateRegistryTest {

    @Test
    public void fromCode_registeredTemplate_returnDescriptorTemplate() {
        assertEquals(MANDATORY_PARAMETER_ERROR, fromCode(MANDATORY_PARAMETER_ERROR.getCode()));
    }

    @Test
    public void fromCode_noneRegisteredTemplate_returnNull() {
        assertNull(fromCode("none.existing.error.code"));
    }

}
