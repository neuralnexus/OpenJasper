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
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.testng.annotations.Test;
import java.lang.reflect.Method;

import static org.testng.Assert.*;

/**
 * <p></p>
 *
 * @author Ivan.Chan
 * @version $Id$
 */
public class JSExceptionWrapperMapperTest {

    @Test
    public void getRootException(){
        JSExceptionWrapperMapper jsExceptionWrapperMapper = new JSExceptionWrapperMapper();
        IllegalParameterValueException illegalParameterValueException = new IllegalParameterValueException("dataSource");
        Exception result = null;
        try {
            Method method = JSExceptionWrapperMapper.class.getDeclaredMethod("getRootException", JSExceptionWrapper.class);
            method.setAccessible(true);
            result = (Exception) method.invoke(jsExceptionWrapperMapper, new JSExceptionWrapper( illegalParameterValueException));
        } catch (Exception ex) {
            // ex.printStackTrace();
            // result will be NULL is this case, assertNotNull will return false
        }
        assertNotNull(result);
        assertSame(result, illegalParameterValueException);
    }
}
