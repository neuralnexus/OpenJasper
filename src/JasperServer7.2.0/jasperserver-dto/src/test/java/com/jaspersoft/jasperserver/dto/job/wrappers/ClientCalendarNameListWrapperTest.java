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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientCalendarNameListWrapperTest extends BaseDTOPresentableTest<ClientCalendarNameListWrapper> {

    private static final String TEST_CALENDAR_NAME = "TEST_CALENDAR_NAME";
    private static final List<String> TEST_CALENDAR_NAMES = Collections.singletonList(TEST_CALENDAR_NAME);

    private static final String TEST_CALENDAR_NAME_1 = "TEST_CALENDAR_NAME_1";
    private static final List<String> TEST_CALENDAR_NAMES_1 = Collections.singletonList(TEST_CALENDAR_NAME_1);

    @Override
    protected List<ClientCalendarNameListWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setCalendarNames(TEST_CALENDAR_NAMES_1),
                // null values
                createFullyConfiguredInstance().setCalendarNames(null)
        );
    }

    @Override
    protected ClientCalendarNameListWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setCalendarNames(TEST_CALENDAR_NAMES);
    }

    @Override
    protected ClientCalendarNameListWrapper createInstanceWithDefaultParameters() {
        return new ClientCalendarNameListWrapper();
    }

    @Override
    protected ClientCalendarNameListWrapper createInstanceFromOther(ClientCalendarNameListWrapper other) {
        return new ClientCalendarNameListWrapper(other);
    }
}