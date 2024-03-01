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

public class ClientExcludeDaysWrapperTest extends BaseDTOPresentableTest<ClientExcludeDaysWrapper> {

    private static final String TEST_EXCLUDE_DAY = "TEST_EXCLUDE_DAY";
    private static final List<String> TEST_EXCLUDE_DAYS = Collections.singletonList(TEST_EXCLUDE_DAY);

    private static final String TEST_EXCLUDE_DAY_1 = "TEST_EXCLUDE_DAY_1";
    private static final List<String> TEST_EXCLUDE_DAYS_1 = Collections.singletonList(TEST_EXCLUDE_DAY_1);

    @Override
    protected List<ClientExcludeDaysWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setExcludeDays(TEST_EXCLUDE_DAYS_1),
                // null values
                createFullyConfiguredInstance().setExcludeDays(null)
        );
    }

    @Override
    protected ClientExcludeDaysWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setExcludeDays(TEST_EXCLUDE_DAYS);
    }

    @Override
    protected ClientExcludeDaysWrapper createInstanceWithDefaultParameters() {
        return new ClientExcludeDaysWrapper();
    }

    @Override
    protected ClientExcludeDaysWrapper createInstanceFromOther(ClientExcludeDaysWrapper other) {
        return new ClientExcludeDaysWrapper(other);
    }
}