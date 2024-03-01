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
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ClientReportJobOutputFormatsWrapperTest extends BaseDTOPresentableTest<ClientReportJobOutputFormatsWrapper> {

    private static final String TEST_FORMAT = "TEST_FORMAT";
    private static final Set<String> TEST_FORMATS = Collections.unmodifiableSet(
            new TreeSet<String>(Collections.singletonList(TEST_FORMAT))
    );

    private static final String TEST_FORMAT_1 = "TEST_FORMAT_1";
    private static final Set<String> TEST_FORMATS_1 = Collections.unmodifiableSet(
            new TreeSet<String>(Collections.singletonList(TEST_FORMAT_1))
    );

    @Override
    protected List<ClientReportJobOutputFormatsWrapper> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setFormats(TEST_FORMATS_1),
                // null values
                createFullyConfiguredInstance().setFormats(null)
        );
    }

    @Override
    protected ClientReportJobOutputFormatsWrapper createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setFormats(TEST_FORMATS);
    }

    @Override
    protected ClientReportJobOutputFormatsWrapper createInstanceWithDefaultParameters() {
        return new ClientReportJobOutputFormatsWrapper();
    }

    @Override
    protected ClientReportJobOutputFormatsWrapper createInstanceFromOther(ClientReportJobOutputFormatsWrapper other) {
        return new ClientReportJobOutputFormatsWrapper(other);
    }
}