/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.dto.connection;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.common.ResourceLocation;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class XlsFileConnectionTest extends BaseDTOPresentableTest<XlsFileConnection> {

    private static final boolean TEST_HAS_HEADER_LINE = true;
    private static final boolean TEST_HAS_HEADER_LINE_1 = false;

    private static final ResourceLocation TEST_LOCATION_CLIENT_REFERENCE = new ClientReference().setUri("TEST_URI");
    private static final ResourceLocation TEST_LOCATION_CLIENT_REFERENCE_1 = new ClientReference().setUri("TEST_URI_1");

    private static final ResourceLocation TEST_LOCATION_FTP_CONNECTION = new FtpConnection();
    private static final ResourceLocation TEST_LOCATION_LFS_CONNECTION = new LfsConnection();

    @Override
    protected void assertFieldsHaveUniqueReferences(XlsFileConnection expected, XlsFileConnection actual) {
        assertNotSame(expected.getLocation(), actual.getLocation());
    }

    /*
     * Preparing
     */

    @Override
    protected List<XlsFileConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setHasHeaderLine(TEST_HAS_HEADER_LINE_1),
                createFullyConfiguredInstance().setLocation(TEST_LOCATION_CLIENT_REFERENCE_1),
                createFullyConfiguredInstance().setLocation(TEST_LOCATION_FTP_CONNECTION),
                createFullyConfiguredInstance().setLocation(TEST_LOCATION_LFS_CONNECTION),
                createFullyConfiguredInstance().setLocation(null)
        );
    }

    @Override
    protected XlsFileConnection createFullyConfiguredInstance() {
        return new XlsFileConnection()
                .setHasHeaderLine(TEST_HAS_HEADER_LINE)
                .setLocation(TEST_LOCATION_CLIENT_REFERENCE);
    }

    @Override
    protected XlsFileConnection createInstanceWithDefaultParameters() {
        return new XlsFileConnection();
    }

    @Override
    protected XlsFileConnection createInstanceFromOther(XlsFileConnection other) {
        return new XlsFileConnection(other);
    }
}