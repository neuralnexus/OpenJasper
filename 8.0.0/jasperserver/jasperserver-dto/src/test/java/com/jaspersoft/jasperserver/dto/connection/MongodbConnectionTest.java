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

public class MongodbConnectionTest extends BaseDTOPresentableTest<MongodbConnection> {

    private static final String TEST_MONGO_URI = "TEST_MONGO_URI";
    private static final String TEST_MONGO_URI_1 = "TEST_MONGO_URI_1";

    private static final String TEST_USERNAME = "TEST_USERNAME";
    private static final String TEST_USERNAME_1 = "TEST_USERNAME_1";

    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_PASSWORD_1 = "TEST_PASSWORD_1";

    /*
     * Preparing
     */

    @Override
    protected List<MongodbConnection> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setMongoURI(TEST_MONGO_URI_1),
                createFullyConfiguredInstance().setUsername(TEST_USERNAME_1),
                createFullyConfiguredInstance().setPassword(TEST_PASSWORD_1),
                createFullyConfiguredInstance().setMongoURI(null),
                createFullyConfiguredInstance().setUsername(null),
                createFullyConfiguredInstance().setPassword(null)
        );
    }

    @Override
    protected MongodbConnection createFullyConfiguredInstance() {
        return new MongodbConnection()
                .setMongoURI(TEST_MONGO_URI)
                .setUsername(TEST_USERNAME)
                .setPassword(TEST_PASSWORD);
    }

    @Override
    protected MongodbConnection createInstanceWithDefaultParameters() {
        return new MongodbConnection();
    }

    @Override
    protected MongodbConnection createInstanceFromOther(MongodbConnection other) {
        return new MongodbConnection(other);
    }
}
