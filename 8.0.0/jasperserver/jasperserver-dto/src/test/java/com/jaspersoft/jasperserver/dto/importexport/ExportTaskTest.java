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

public class ExportTaskTest extends BaseDTOPresentableTest<ExportTask> {

    private static final String TEST_EXPORT_PARAMETER = "TEST_EXPORT_PARAMETER";
    private static final String TEST_EXPORT_PARAMETER_1 = "TEST_EXPORT_PARAMETER_1";
    private static final List<String> TEST_EXPORT_PARAMETERS = Collections.singletonList(TEST_EXPORT_PARAMETER);
    private static final List<String> TEST_EXPORT_PARAMETERS_1 = Collections.singletonList(TEST_EXPORT_PARAMETER_1);
    private static final List<String> TEST_EXPORT_PARAMETERS_EMPTY = new ArrayList<String>();

    private static final String TEST_URIS_OF_RESOURCE = "TEST_URIS_OF_RESOURCE";
    private static final String TEST_URIS_OF_RESOURCE_1 = "TEST_URIS_OF_RESOURCE_1";
    private static final List<String> TEST_URIS_OF_RESOURCES = Collections.singletonList(TEST_URIS_OF_RESOURCE);
    private static final List<String> TEST_URIS_OF_RESOURCES_1 = Collections.singletonList(TEST_URIS_OF_RESOURCE_1);
    private static final List<String> TEST_URIS_OF_RESOURCES_EMPTY = new ArrayList<String>();

    private static final String TEST_URIS_OF_SCHEDULED_JOB = "TEST_URIS_OF_SCHEDULED_JOB";
    private static final String TEST_URIS_OF_SCHEDULED_JOB_1 = "TEST_URIS_OF_SCHEDULED_JOB_1";
    private static final List<String> TEST_URIS_OF_SCHEDULED_JOBS = Collections.singletonList(TEST_URIS_OF_SCHEDULED_JOB);
    private static final List<String> TEST_URIS_OF_SCHEDULED_JOBS_1 = Collections.singletonList(TEST_URIS_OF_SCHEDULED_JOB_1);
    private static final List<String> TEST_URIS_OF_SCHEDULED_JOBS_EMPTY = new ArrayList<String>();

    private static final String TEST_ROLE_TO_EXPORT = "TEST_ROLE_TO_EXPORT";
    private static final String TEST_ROLE_TO_EXPORT_1 = "TEST_ROLE_TO_EXPORT_1";
    private static final List<String> TEST_ROLES_TO_EXPORT = Collections.singletonList(TEST_ROLE_TO_EXPORT);
    private static final List<String> TEST_ROLES_TO_EXPORT_1 = Collections.singletonList(TEST_ROLE_TO_EXPORT_1);
    private static final List<String> TEST_ROLES_TO_EXPORT_EMPTY = new ArrayList<String>();

    private static final String TEST_USER_TO_EXPORT = "TEST_USER_TO_EXPORT";
    private static final String TEST_USER_TO_EXPORT_1 = "TEST_USER_TO_EXPORT_1";
    private static final List<String> TEST_USERS_TO_EXPORT = Collections.singletonList(TEST_USER_TO_EXPORT);
    private static final List<String> TEST_USERS_TO_EXPORT_1 = Collections.singletonList(TEST_USER_TO_EXPORT_1);
    private static final List<String> TEST_USERS_TO_EXPORT_EMPTY = new ArrayList<String>();

    private static final String TEST_RESOURCE_TYPE = "TEST_RESOURCE_TYPE";
    private static final String TEST_RESOURCE_TYPE_1 = "TEST_RESOURCE_TYPE_1";
    private static final List<String> TEST_RESOURCE_TYPES = Collections.singletonList(TEST_RESOURCE_TYPE);
    private static final List<String> TEST_RESOURCE_TYPES_1 = Collections.singletonList(TEST_RESOURCE_TYPE_1);
    private static final List<String> TEST_RESOURCE_TYPES_EMPTY = new ArrayList<String>();

    private static final String TEST_ORGANIZATION = "TEST_ORGANIZATION";
    private static final String TEST_ORGANIZATION_1 = "TEST_ORGANIZATION_1";

    private static final String TEST_IMPORT_EXPORT_KEY_ALIAS = "TEST_IMPORT_EXPORT_KEY_ALIAS";
    private static final String TEST_IMPORT_EXPORT_KEY_ALIAS_ALT = "TEST_IMPORT_EXPORT_KEY_ALIAS_ALT";

    @Override
    protected void assertFieldsHaveUniqueReferences(ExportTask expected, ExportTask actual) {
        assertNotSame(expected.getParameters(), actual.getParameters());
        assertNotSame(expected.getUris(), actual.getUris());
        assertNotSame(expected.getScheduledJobs(), actual.getScheduledJobs());
        assertNotSame(expected.getRoles(), actual.getRoles());
        assertNotSame(expected.getUsers(), actual.getUsers());
        assertNotSame(expected.getResourceTypes(), actual.getResourceTypes());
    }

    /*
     * Preparing
     */

    @Override
    protected List<ExportTask> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setParameters(TEST_EXPORT_PARAMETERS_1),
                createFullyConfiguredInstance().setParameters(TEST_EXPORT_PARAMETERS_EMPTY),
                createFullyConfiguredInstance().setUris(TEST_URIS_OF_RESOURCES_1),
                createFullyConfiguredInstance().setUris(TEST_URIS_OF_RESOURCES_EMPTY),
                createFullyConfiguredInstance().setScheduledJobs(TEST_URIS_OF_SCHEDULED_JOBS_1),
                createFullyConfiguredInstance().setScheduledJobs(TEST_URIS_OF_SCHEDULED_JOBS_EMPTY),
                createFullyConfiguredInstance().setRoles(TEST_ROLES_TO_EXPORT_1),
                createFullyConfiguredInstance().setRoles(TEST_ROLES_TO_EXPORT_EMPTY),
                createFullyConfiguredInstance().setUsers(TEST_USERS_TO_EXPORT_1),
                createFullyConfiguredInstance().setUsers(TEST_USERS_TO_EXPORT_EMPTY),
                createFullyConfiguredInstance().setResourceTypes(TEST_RESOURCE_TYPES_1),
                createFullyConfiguredInstance().setResourceTypes(TEST_RESOURCE_TYPES_EMPTY),
                createFullyConfiguredInstance().setOrganization(TEST_ORGANIZATION_1),
                createFullyConfiguredInstance().setKeyAlias(TEST_IMPORT_EXPORT_KEY_ALIAS_ALT),
                // null values
                createFullyConfiguredInstance().setParameters(null),
                createFullyConfiguredInstance().setUris(null),
                createFullyConfiguredInstance().setScheduledJobs(null),
                createFullyConfiguredInstance().setRoles(null),
                createFullyConfiguredInstance().setUsers(null),
                createFullyConfiguredInstance().setResourceTypes(null),
                createFullyConfiguredInstance().setOrganization(null)
        );
    }

    @Override
    protected ExportTask createFullyConfiguredInstance() {
        return new ExportTask()
                .setParameters(TEST_EXPORT_PARAMETERS)
                .setUris(TEST_URIS_OF_RESOURCES)
                .setScheduledJobs(TEST_URIS_OF_SCHEDULED_JOBS)
                .setRoles(TEST_ROLES_TO_EXPORT)
                .setUsers(TEST_USERS_TO_EXPORT)
                .setResourceTypes(TEST_RESOURCE_TYPES)
                .setOrganization(TEST_ORGANIZATION)
                .setKeyAlias(TEST_IMPORT_EXPORT_KEY_ALIAS);
    }

    @Override
    protected ExportTask createInstanceWithDefaultParameters() {
        return new ExportTask();
    }

    @Override
    protected ExportTask createInstanceFromOther(ExportTask other) {
        return new ExportTask(other);
    }
}
