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
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class BasicResourceValidatorTest {
    private final BasicResourceValidator validator = new BasicResourceValidator();
    private Folder resource;

    @BeforeMethod
    public void setUp(){
        resource = new FolderImpl();
    }

    @Test
    public void testValidate() throws Exception {
        resource.setLabel("Label");
        resource.setDescription("description");

        validator.validate(resource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_xss_label() throws Exception {
        resource.setLabel("<Label>");
        resource.setDescription("description");

        validator.validate(resource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_xss_description() throws Exception {
        resource.setLabel("Label");
        resource.setDescription("<description>");

        validator.validate(resource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_long_description() throws Exception {
        resource.setLabel("Label");
        resource.setDescription("LabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabel1LabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabel12345678901234567890-");

        validator.validate(resource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_long_label() throws Exception {
        resource.setLabel("LabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabelLabel12345678901234567890-");
        resource.setDescription("tion");

        validator.validate(resource);
    }
}
