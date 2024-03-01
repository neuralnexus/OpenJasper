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

package com.jaspersoft.jasperserver.dto.importexport;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class ImportTaskTest extends BaseDTOPresentableTest<ImportTask> {

    private static final String TEST_ORGANIZATION = "TEST_ORGANIZATION";
    private static final String TEST_ORGANIZATION_1 = "TEST_ORGANIZATION_1";

    private static final String TEST_BROKEN_DEPENDENCIES = "TEST_BROKEN_DEPENDENCIES";
    private static final String TEST_BROKEN_DEPENDENCIES_1 = "TEST_BROKEN_DEPENDENCIES_1";

    private static final String TEST_PARAMETER = "TEST_PARAMETER";
    private static final String TEST_PARAMETER_1 = "TEST_PARAMETER_1";
    private static final List<String> TEST_PARAMETERS = Collections.singletonList(TEST_PARAMETER);
    private static final List<String> TEST_PARAMETERS_1 = Collections.singletonList(TEST_PARAMETER_1);
    private static final List<String> TEST_PARAMETERS_EMPTY = new ArrayList<String>();

    private static final String TEST_IMPORT_EXPORT_KEY_ALIAS = "TEST_IMPORT_EXPORT_KEY_ALIAS";
    private static final String TEST_IMPORT_EXPORT_KEY_ALIAS_ALT= "TEST_IMPORT_EXPORT_KEY_ALIAS_ALT";
    private static final String TEST_IMPORT_EXPORT_SECRET_KEY = "TEST_IMPORT_EXPORT_SECRET_KEY";
    private static final String TEST_IMPORT_EXPORT_SECRET_KEY_ALT = "TEST_IMPORT_EXPORT_SECRET_KEY_ALT";
    private static final String TEST_IMPORT_EXPORT_SECRET_URI = "TEST_IMPORT_EXPORT_SECRET_URI";
    private static final String TEST_IMPORT_EXPORT_SECRET_URI_ALT = "TEST_IMPORT_EXPORT_SECRET_URI_ALT";

    @Override
    protected void assertFieldsHaveUniqueReferences(ImportTask expected, ImportTask actual) {
        assertNotSame(expected.getParameters(), actual.getParameters());
    }

    /*
     * Preparing
     */

    @Override
    protected List<ImportTask> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setOrganization(TEST_ORGANIZATION_1),
                createFullyConfiguredInstance().setBrokenDependencies(TEST_BROKEN_DEPENDENCIES_1),
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_1),
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_EMPTY),
                createFullyConfiguredInstance().setKeyAlias(TEST_IMPORT_EXPORT_KEY_ALIAS_ALT),
                createFullyConfiguredInstance().setSecretKey(TEST_IMPORT_EXPORT_SECRET_KEY_ALT),
                createFullyConfiguredInstance().setSecretUri(TEST_IMPORT_EXPORT_SECRET_URI_ALT),
                // null values
                createFullyConfiguredInstance().setOrganization(null),
                createFullyConfiguredInstance().setBrokenDependencies(null),
                createFullyConfiguredInstance().setParameters(null)
        );
    }

    @Override
    protected ImportTask createFullyConfiguredInstance() {
        return new ImportTask()
                .setOrganization(TEST_ORGANIZATION)
                .setBrokenDependencies(TEST_BROKEN_DEPENDENCIES)
                .setParameters(TEST_PARAMETERS)
                .setKeyAlias(TEST_IMPORT_EXPORT_KEY_ALIAS)
                .setSecretKey(TEST_IMPORT_EXPORT_SECRET_KEY)
                .setSecretUri(TEST_IMPORT_EXPORT_SECRET_URI);
    }

    @Override
    protected ImportTask createInstanceWithDefaultParameters() {
        return new ImportTask();
    }

    @Override
    protected ImportTask createInstanceFromOther(ImportTask other) {
        return new ImportTask(other);
    }

}
