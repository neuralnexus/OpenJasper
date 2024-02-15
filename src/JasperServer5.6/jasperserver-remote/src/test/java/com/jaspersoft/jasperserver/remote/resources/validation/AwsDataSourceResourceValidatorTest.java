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
import com.jaspersoft.jasperserver.api.common.service.JdbcDriverService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AwsReportDataSourceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.LinkedList;
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
public class AwsDataSourceResourceValidatorTest {
    @InjectMocks
    private final AwsDataSourceResourceValidator validator = new AwsDataSourceResourceValidator();
    @Mock JdbcDriverService jdbcDriverService;
    @Spy  List<String> awsRegions = new LinkedList<String>();

    private AwsReportDataSource dataSource;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        reset(jdbcDriverService);
        when(jdbcDriverService.isRegistered(anyString())).thenReturn(true);

        awsRegions.add("amazon");

        dataSource = new AwsReportDataSourceImpl();
        dataSource.setLabel("tets");

        dataSource.setAWSAccessKey("fas");
        dataSource.setAWSSecretKey("faf");
        dataSource.setAWSRegion(awsRegions.get(0));
        dataSource.setConnectionUrl("jdbc:ajfa");
        dataSource.setDbName("aajfa");
        dataSource.setDriverClass("ajfa");
        dataSource.setUsername("ajfa");
        dataSource.setTimezone("America/New_York");
    }

    @Test
    public void testValidate() throws Exception {
        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_no_accessKey() throws Exception {
        dataSource.setAWSAccessKey(null);
        dataSource.setAWSSecretKey("secret_key");

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noSecretKey() throws Exception {
        dataSource.setAWSSecretKey(null);
        dataSource.setAWSAccessKey("Access_key");

        validator.validate(dataSource);
    }

    @Test
    public void testValidate_no_keys() throws Exception {
        dataSource.setAWSAccessKey(null);
        dataSource.setAWSSecretKey(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noConnection() throws Exception {
        dataSource.setConnectionUrl(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noDriver() throws Exception {
        dataSource.setDriverClass(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_unknownDriver() throws Exception {
        reset(jdbcDriverService);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noUsername() throws Exception {
        dataSource.setUsername(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_noRegion() throws Exception {
        dataSource.setAWSRegion(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_nodbName() throws Exception {
        dataSource.setDbName(null);

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_invalidTimezione() throws Exception {
        dataSource.setTimezone("#$%^&*(OL)");

        validator.validate(dataSource);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_invalidRegion() throws Exception {
        dataSource.setAWSRegion("#$%^&*(OL)");

        validator.validate(dataSource);
    }
}

