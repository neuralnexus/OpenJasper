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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Query;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.QueryImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class QueryResourceValidatorTest {
    @InjectMocks
    final QueryResourceValidator validator = new QueryResourceValidator();
    @Mock
    private List<String> queryLanguages;

    Query query;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp(){
        reset(queryLanguages);
        when(queryLanguages.contains(anyString())).thenReturn(true);

        query = new QueryImpl();
        query.setLabel("aa");

        query.setLanguage("sql");
        query.setSql("select * from table");
    }

    @Test
    public void testValidate() throws Exception {

        validator.validate(query);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emtyLanguage() throws Exception {
        query.setLanguage("");

        validator.validate(query);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyQuery() throws Exception {
        query.setSql("");

        validator.validate(query);
    }
}
