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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

/**
 * Created by mchan on 3/21/2018.
 */
public class ErrorAdapterTest {

    @Test
    public void testToStrings_withNull() throws Exception {
        List<Exception> errors = new ArrayList<Exception>();
        ErrorsAdapter errorsAdapter = new ErrorsAdapter(errors, null);
        errorsAdapter.reject("errorCode", null, "defaultMessage");
        assertNotNull(errors);
        assertFalse(errors.isEmpty());
        assertNull(((ErrorDescriptorException) errors.get(0)).getErrorDescriptor().getParameters());
    }
}
