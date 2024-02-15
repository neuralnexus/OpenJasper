/*
* Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
* http://www.jaspersoft.com.
*
* Unless you have purchased  a commercial license agreement from Jaspersoft,
* the following license terms  apply:
*
* This program is free software: you can redistribute it and/or  modify
* it under the terms of the GNU Affero General Public License  as
* published by the Free Software Foundation, either version 3 of  the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero  General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public  License
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: GenericResourceValidatorTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class GenericResourceValidatorTest {
    private GenericResourceValidator<Resource> validator = mock(GenericResourceValidator.class);
    private Resource resource = new ResourceLookupImpl();

    @BeforeMethod
    public void init(){
        resource.setURIString("/test/uri");
        resource.setLabel("testa");
        reset(validator);
        doCallRealMethod().when(validator).validate(resource);
    }

    @Test
    public void validate(){
        validator.validate(resource);
        verify(validator).internalValidate(same(resource), any(ValidationErrors.class));
    }

    @Test(expectedExceptions = JSValidationException.class)
    public void validate_exception(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ValidationErrors validationErrors = (ValidationErrors) invocation.getArguments()[1];
                validationErrors.add(new ValidationErrorImpl());
                return null;
            }
        }).when(validator).internalValidate(same(resource), any(ValidationErrors.class));
        validator.validate(resource);
    }
}
