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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class HeartbeatDatabaseInfoTest {
    private static final String DATABASE_NAME = "databaseName";
    private static final String DATABASE_VERSION = "databaseVersion";
    private static final String KEY_FOR_NULL = null + "|" + null;
    private static final String KEY = DATABASE_NAME + "|" + DATABASE_NAME;

    private static final String REPO_DB_NAME_KEY = "repoDbName[]";
    private static final String REPO_DB_VERSION_KEY = "repoDbVersion[]";
    private static final String REPO_DB_COUNT_KEY = "repoDbCount[]";


    private HeartbeatDatabaseInfo objectUnderTest = new HeartbeatDatabaseInfo();
    private HeartbeatCall heartbeatCall = mock(HeartbeatCall.class);

    @Test
    void getAndSet_instanceWithDefaultValues() {
        final HeartbeatDatabaseInfo instance = new HeartbeatDatabaseInfo();

        assertAll("an instance with default values",
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getDatabaseName());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getDatabaseVersion());
                    }
                });
    }

    @Test
    void getAndSet_fullyConfiguredInstance() {
        final HeartbeatDatabaseInfo instance = new HeartbeatDatabaseInfo();
        instance.setDatabaseName(DATABASE_NAME);
        instance.setDatabaseVersion(DATABASE_VERSION);

        assertAll("a fully configured instance",
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(DATABASE_NAME, instance.getDatabaseName());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(DATABASE_VERSION, instance.getDatabaseVersion());
                    }
                });
    }

    @Test
    void getKey_instanceWithDefaultValues_null() {
        assertEquals(KEY_FOR_NULL, objectUnderTest.getKey());
    }

    @Test
    void getKey_fullyConfiguredInstance_serviceClass() {
        objectUnderTest.setDatabaseName(DATABASE_NAME);

        assertEquals(KEY, objectUnderTest.getKey());
    }

    @Test
    void contributeToHttpCall_instanceWithDefaultValues_repoDbNameAndRepoDbVersionAndCountParamIsContributed() {
        objectUnderTest.contributeToHttpCall(heartbeatCall);

        verify(heartbeatCall).addParameter(REPO_DB_NAME_KEY, "");
        verify(heartbeatCall).addParameter(REPO_DB_VERSION_KEY, "");
        verify(heartbeatCall).addParameter(REPO_DB_COUNT_KEY, "0");
    }

    @Test
    void contributeToHttpCall_fullyConfiguredInstance_repoDbNameAndRepoDbVersionAndCountParamIsContributed() {
        objectUnderTest.setDatabaseName(DATABASE_NAME);
        objectUnderTest.setDatabaseVersion(DATABASE_VERSION);
        objectUnderTest.incrementCount();

        objectUnderTest.contributeToHttpCall(heartbeatCall);

        verify(heartbeatCall).addParameter(REPO_DB_NAME_KEY, DATABASE_NAME);
        verify(heartbeatCall).addParameter(REPO_DB_VERSION_KEY, DATABASE_VERSION);
        verify(heartbeatCall).addParameter(REPO_DB_COUNT_KEY, "1");
    }
}
