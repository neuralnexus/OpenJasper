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

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class LfsConnectionTest extends BaseDTOPresentableTest<LfsConnection> {

    private static final String TEST_PATH = "TEST_PATH";
    private static final String TEST_PATH_1 = "TEST_PATH_1";

    /*
     * Preparing
     */

    @Override
    protected List<LfsConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setPath(TEST_PATH_1),
                createFullyConfiguredInstance().setPath(null)
        );
    }

    @Override
    protected LfsConnection createFullyConfiguredInstance() {
        return new LfsConnection()
                .setPath(TEST_PATH);
    }

    @Override
    protected LfsConnection createInstanceWithDefaultParameters() {
        return new LfsConnection();
    }

    @Override
    protected LfsConnection createInstanceFromOther(LfsConnection other) {
        return new LfsConnection(other);
    }
}
