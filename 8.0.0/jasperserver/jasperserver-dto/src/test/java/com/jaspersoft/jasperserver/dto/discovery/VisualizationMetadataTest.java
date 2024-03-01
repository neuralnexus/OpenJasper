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

package com.jaspersoft.jasperserver.dto.discovery;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * @author Olexandr Dahno <odahno@tibco.com>
 */

public class VisualizationMetadataTest extends BaseDTOPresentableTest<VisualizationMetadata> {

    private static final List<Parameter> TEST_PARAMETERS = Collections.singletonList(
            new Parameter().setId("TEST_ID")
    );
    private static final List<Parameter> TEST_PARAMETERS_1 = Collections.singletonList(
            new Parameter().setId("TEST_ID_1")
    );

    private static final List<Parameter> TEST_OUTPUT_PARAMETERS = Collections.singletonList(
            new Parameter().setId("TEST_ID")
    );
    private static final List<Parameter> TEST_OUTPUT_PARAMETERS_1 = Collections.singletonList(
            new Parameter().setId("TEST_ID_1")
    );
    private static final String TEST_REPOSITORY_TYPE = "TEST_REPOSITORY_TYPE";
    private static final String TEST_REPOSITORY_TYPE_1 = "TEST_REPOSITORY_TYPE_1";

    @Override
    protected void assertFieldsHaveUniqueReferences(VisualizationMetadata expected, VisualizationMetadata actual) {
        assertNotSame(expected.getParameters(), actual.getParameters());
        assertNotSame(expected.getParameters().get(0), actual.getParameters().get(0));

        assertNotSame(expected.getOutputParameters(), actual.getOutputParameters());
        assertNotSame(expected.getOutputParameters().get(0), actual.getOutputParameters().get(0));
    }

    /*
     * Preparing
     */

    @Override
    protected List<VisualizationMetadata> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setParameters(TEST_PARAMETERS_1),
                createFullyConfiguredInstance().setOutputParameters(TEST_OUTPUT_PARAMETERS_1),
                createFullyConfiguredInstance().setRepositoryType(TEST_REPOSITORY_TYPE_1),
                // null values
                createFullyConfiguredInstance().setParameters(null),
                createFullyConfiguredInstance().setOutputParameters(null),
                createFullyConfiguredInstance().setRepositoryType(null)
        );
    }

    @Override
    protected VisualizationMetadata createFullyConfiguredInstance() {
        return new VisualizationMetadata()
                .setParameters(TEST_PARAMETERS)
                .setOutputParameters(TEST_OUTPUT_PARAMETERS)
                .setRepositoryType(TEST_REPOSITORY_TYPE);
    }

    @Override
    protected VisualizationMetadata createInstanceWithDefaultParameters() {
        return new VisualizationMetadata();
    }

    @Override
    protected VisualizationMetadata createInstanceFromOther(VisualizationMetadata other) {
        return new VisualizationMetadata(other);
    }

}
