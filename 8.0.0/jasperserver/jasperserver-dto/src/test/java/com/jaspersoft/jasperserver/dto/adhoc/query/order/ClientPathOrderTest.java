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

package com.jaspersoft.jasperserver.dto.adhoc.query.order;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOJSONPresentableTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

class ClientPathOrderTest extends BaseDTOJSONPresentableTest<ClientPathOrder> {

    private static final Boolean TEST_ASCENDING = Boolean.TRUE;
    private static final Boolean TEST_ASCENDING_ALT = Boolean.FALSE;

    private static final String TEST_PATH = "TEST_PATH";
    private static final List<String> TEST_PATH_LIST = Collections.singletonList(TEST_PATH);

    private static final String TEST_PATH_ALT = "TEST_PATH_ALT";
    private static final List<String> TEST_PATH_LIST_ALT = Collections.singletonList(TEST_PATH_ALT);

    @Override
    protected List<ClientPathOrder> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setAscending(TEST_ASCENDING_ALT),
                createFullyConfiguredInstance().setPath(TEST_PATH_LIST_ALT),
                createFullyConfiguredInstance().setAscending(null),
                createFullyConfiguredInstance().setPath(null)
        );
    }

    @Override
    protected ClientPathOrder createFullyConfiguredInstance() {
        return new ClientPathOrder()
                .setAscending(TEST_ASCENDING)
                .setPath(TEST_PATH_LIST);
    }

    @Override
    protected ClientPathOrder createInstanceWithDefaultParameters() {
        return new ClientPathOrder();
    }

    @Override
    protected ClientPathOrder createInstanceFromOther(ClientPathOrder other) {
        return new ClientPathOrder(other);
    }

    @Test
    public void createCloneOfNullViaCopyConstructor_throwsException() {
        assertThrows(
                IllegalArgumentException.class,
                new Executable() {
                    @Override
                    public void execute() throws Exception {
                        new ClientPathOrder(null);
                    }
                }
        );
    }
}