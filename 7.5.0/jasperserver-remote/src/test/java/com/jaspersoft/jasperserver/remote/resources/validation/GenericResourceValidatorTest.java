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

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ResourceLookupImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class GenericResourceValidatorTest {
    @InjectMocks
    private GenericResourceValidator<Resource> validator = mock(GenericResourceValidator.class);
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private Resource resource = new ResourceLookupImpl();

    @BeforeClass
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void init() {
        reset(profileAttributesResolver);
        resource.setURIString("/test/uri");
        resource.setLabel("testa");
        resource.setDescription("description");
        reset(validator);
        doCallRealMethod().when(validator).validate(any(Resource.class));
        doCallRealMethod().when(validator).validate(any(Resource.class), eq(false), any(Map.class));
    }

    @Test
    public void validate() {
        validator.validate(resource);
        verify(validator).internalValidate(same(resource), any(List.class), any(Map.class));
    }

    @Test
    public void validate_exception() throws Exception {
        final RuntimeException exception = new RuntimeException();
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                List<Exception> validationErrors = (List<Exception>) invocation.getArguments()[1];

                validationErrors.add(exception);
                return null;
            }
        }).when(validator).internalValidate(same(resource), any(List.class), any(Map.class));
        final List<Exception> exceptions = validator.validate(resource);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
        assertSame(exceptions.get(0), exception);
    }

    @Test
    public void validate_labelContainsAttributePlaceholder_exception() throws Exception {
        when(profileAttributesResolver.containsAttribute(resource.getLabel())).thenReturn(true);
        final List<Exception> exceptions = validator.validate(resource);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void validate_labelIsNull_exception() throws Exception {
        final List<Exception> exceptions = validator.validate(new ResourceLookupImpl());

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void validate_longDescription_exception() throws Exception {
        ResourceLookupImpl rli = new ResourceLookupImpl();
        rli.setDescription("filkRLwnVcsmZoFbTJewiqsohAXWobiqOAwmiVhLDrrd" +
                "dQoyRFOPUMhDDRtPbOdpsBxIFBIZoLgGelTeUhRWSNIHwvkkqSWtKZMGgEhoEECcZWKDmrtfwTZWtbLhkzJvZnjuy" +
                "qQysiIApMkPuWDUcAzUEVcfZYtenGoWXqa" +
                "YXwcaymjrZTfQYNZbNNTjxCQuTgUgXtgtUsdpEUdsEHmBfPDldcPLNSHGDKNcuPzJwjnDiHNruTaoPBGxFcP");
        final List<Exception> exceptions = validator.validate(rli);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void validate_labelIsNull_slipRepoFieldsValidation_success() throws Exception {
        Exception exception = null;
        try {
            validator.validate(new ResourceLookupImpl(), true, new HashMap<String, String[]>());
        } catch (Exception e){
            exception = e;
        }
        assertNull(exception);
    }

    @Test
    public void validate_descriptionContainsAttributePlaceholder_exception() throws Exception {
        when(profileAttributesResolver.containsAttribute(resource.getDescription())).thenReturn(true);
        final List<Exception> exceptions = validator.validate(resource);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void validate_nameContainsAttributePlaceholder_exception() throws Exception {
        when(profileAttributesResolver.containsAttribute(resource.getName())).thenReturn(true);
        final List<Exception> exceptions = validator.validate(resource);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }
}
