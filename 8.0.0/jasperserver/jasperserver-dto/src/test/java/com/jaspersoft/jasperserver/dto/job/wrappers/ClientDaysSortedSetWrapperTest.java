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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientDaysSortedSetWrapperTest extends BaseDTOPresentableTest<ClientDaysSortedSetWrapper> {

    private static final String TEST_DAY = "TEST_DAY";
    private static final SortedSet<String> TEST_DAYS = Collections.unmodifiableSortedSet(
            new TreeSet<String>(Collections.singletonList(TEST_DAY))
    );

    private static final String TEST_DAY_1 = "TEST_DAY_1";
    private static final SortedSet<String> TEST_DAYS_1 = Collections.unmodifiableSortedSet(
            new TreeSet<String>(Collections.singletonList(TEST_DAY_1))
    );

    @Override
    protected List<ClientDaysSortedSetWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setDays(TEST_DAYS_1),
                // null values
                createFullyConfiguredInstance().setDays(null)
        );
    }

    @Override
    protected ClientDaysSortedSetWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setDays(TEST_DAYS);
    }

    @Override
    protected ClientDaysSortedSetWrapper createInstanceWithDefaultParameters() {
        return new ClientDaysSortedSetWrapper();
    }

    @Override
    protected ClientDaysSortedSetWrapper createInstanceFromOther(ClientDaysSortedSetWrapper other) {
        return new ClientDaysSortedSetWrapper(other);
    }
}