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

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationDetailImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationResultImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.OlapUnitImpl;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class OlapUnitValidatorTest {
    @InjectMocks
    private final OlapUnitValidator validator = new OlapUnitValidator();
    @Mock
    private OlapConnectionService olapConnectionService;
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private OlapUnit unit;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
        when(olapConnectionService.validate(nullable(ExecutionContext.class), any(OlapUnit.class))).thenReturn(new ValidationResultImpl());
    }

    @BeforeMethod
    public void setUp(){
        unit = new OlapUnitImpl();
        unit.setLabel("label");
        unit.setOlapClientConnectionReference("/a");
        unit.setMdxQuery("SELECT\n" +
                "   { [Measures].[Store Sales] } ON COLUMNS,\n" +
                "   { [Date].[2002], [Date].[2003] } ON ROWS\n" +
                "FROM Sales\n" +
                "WHERE ( [Store].[USA].[CA] )");


    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(unit);
    }

    @Test
    public void testValidate_noQuery() throws Exception {
        unit.setMdxQuery(null);
        final List<Exception> exceptions = validator.validate(unit);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void testValidate_emptyQuery() throws Exception {
        unit.setMdxQuery("");
        final List<Exception> exceptions = validator.validate(unit);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void testValidate_noConnection() throws Exception {
        unit.setOlapClientConnectionReference(null);
        final List<Exception> exceptions = validator.validate(unit);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void testValidate_invalidQuery() throws Exception {
        unit.setMdxQuery("invalidMDX");
        ValidationDetailImpl validationDetail = new ValidationDetailImpl();
        ValidationDetailImpl detail = new ValidationDetailImpl();
        detail.setValidationClass(OlapUnit.class);
        detail.setName(unit.getName());
        detail.setLabel(unit.getLabel());
        detail.setResult(ValidationResult.STATE_ERROR);
        detail.setException(new Exception());
        detail.setSource(unit.getMdxQuery());
        detail.setMessage("mdxQuery");
        ValidationResultImpl validationResult = new ValidationResultImpl();
        validationResult.addValidationDetail(detail);
        when(olapConnectionService.validate(nullable(ExecutionContext.class), any(OlapUnit.class))).thenReturn(validationResult);
        final List<Exception> exceptions = validator.validate(unit);
        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }


}
