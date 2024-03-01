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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class TextFileQueryTest extends BaseDTOTest<TextFileQuery> {

    private static final Integer TEST_OFFSET = 100;
    private static final Integer TEST_OFFSET_1 = 1001;

    private static final Integer TEST_LIMIT = 101;
    private static final Integer TEST_LIMIT_1 = 1011;

    private static final TextFileSelect TEST_SELECT = new TextFileSelect().setColumns(Collections.singletonList("TEST_COLUMN"));
    private static final TextFileSelect TEST_SELECT_1 = new TextFileSelect().setColumns(Collections.singletonList("TEST_COLUMN_1"));

    private static final TextFileConvert TEST_CONVERT = new TextFileConvert().setRules(Collections.singletonList(new TextFileCastConversionRule().setType("TEST_TYPE")));
    private static final TextFileConvert TEST_CONVERT_1 = new TextFileConvert().setRules(Collections.singletonList(new TextFileCastConversionRule().setType("TEST_TYPE_1")));

    @Override
    protected List<TextFileQuery> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setOffset(TEST_OFFSET_1),
                createFullyConfiguredInstance().setLimit(TEST_LIMIT_1),
                createFullyConfiguredInstance().setSelect(TEST_SELECT_1),
                createFullyConfiguredInstance().setConvert(TEST_CONVERT_1)
        );
    }

    @Override
    protected TextFileQuery createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setOffset(TEST_OFFSET)
                .setLimit(TEST_LIMIT)
                .setSelect(TEST_SELECT)
                .setConvert(TEST_CONVERT);
    }

    @Override
    protected TextFileQuery createInstanceWithDefaultParameters() {
        return new TextFileQuery();
    }

    @Override
    protected TextFileQuery createInstanceFromOther(TextFileQuery other) {
        return new TextFileQuery(other);
    }

    @Override
    protected void assertFieldsHaveUniqueReferences(TextFileQuery expected, TextFileQuery actual) {
        assertNotSame(expected.getConvert(), actual.getConvert());
        assertNotSame(expected.getSelect(), actual.getSelect());
    }
}
