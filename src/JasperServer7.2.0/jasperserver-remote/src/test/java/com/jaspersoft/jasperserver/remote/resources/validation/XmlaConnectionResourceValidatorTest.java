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

import com.jaspersoft.jasperserver.api.metadata.olap.domain.XMLAConnection;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.XMLAConnectionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class XmlaConnectionResourceValidatorTest {
    @InjectMocks
    private final XmlaConnectionResourceValidator validator = new XmlaConnectionResourceValidator();
    @Mock
    private ProfileAttributesResolver profileAttributesResolver;

    private XMLAConnection connection;

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp(){
        connection = new XMLAConnectionImpl();
        connection.setLabel("aa");

        connection.setCatalog("a");
        connection.setDataSource("b");
        connection.setURI("c");
        connection.setUsername("d");
    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(connection);
    }

    @Test
    public void testValidate_noCatalog() throws Exception {
        connection.setCatalog(null);
        final List<Exception> exceptions = validator.validate(connection);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void testValidate_emptyDataSource() throws Exception {
        connection.setDataSource("");
        final List<Exception> exceptions = validator.validate(connection);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

    @Test
    public void testValidate_noUri() throws Exception {
        connection.setURI(null);
        final List<Exception> exceptions = validator.validate(connection);
    }

    @Test
    public void testValidate_emptyUsername() throws Exception {
        connection.setUsername("");
        final List<Exception> exceptions = validator.validate(connection);

        assertNotNull(exceptions);
        assertFalse(exceptions.isEmpty());
    }

}
