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

package com.jaspersoft.jasperserver.dto.connection.query;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class TextFileCastConversionRuleTest extends BaseDTOTest<TextFileCastConversionRule> {

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_1 = "TEST_TYPE_1";

    private static final String TEST_COLUMN = "TEST_COLUMN";
    private static final String TEST_COLUMN_1 = "TEST_COLUMN_1";

    @Override
    protected List<TextFileCastConversionRule> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                createFullyConfiguredInstance().setColumn(TEST_COLUMN_1)
        );
    }

    @Override
    protected TextFileCastConversionRule createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setType(TEST_TYPE)
                .setColumn(TEST_COLUMN);
    }

    @Override
    protected TextFileCastConversionRule createInstanceWithDefaultParameters() {
        return new TextFileCastConversionRule();
    }

    @Override
    protected TextFileCastConversionRule createInstanceFromOther(TextFileCastConversionRule other) {
        return new TextFileCastConversionRule(other);
    }

}
