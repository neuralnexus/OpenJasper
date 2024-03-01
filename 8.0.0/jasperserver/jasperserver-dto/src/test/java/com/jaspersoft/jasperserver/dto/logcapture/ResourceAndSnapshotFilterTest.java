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

package com.jaspersoft.jasperserver.dto.logcapture;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class ResourceAndSnapshotFilterTest extends BaseDTOPresentableTest<ResourceAndSnapshotFilter> {

    @Override
    protected List<ResourceAndSnapshotFilter> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setResourceUri("uri2"),
                createFullyConfiguredInstance().setIncludeDataSnapshots(false),
                // with null values
                createFullyConfiguredInstance().setResourceUri(null),
                createFullyConfiguredInstance().setIncludeDataSnapshots(null)
        );
    }

    @Override
    protected ResourceAndSnapshotFilter createFullyConfiguredInstance() {
        ResourceAndSnapshotFilter resourceAndSnapshotFilter = new ResourceAndSnapshotFilter();
        resourceAndSnapshotFilter.setResourceUri("uri");
        resourceAndSnapshotFilter.setIncludeDataSnapshots(true);
        return resourceAndSnapshotFilter;
    }

    @Override
    protected ResourceAndSnapshotFilter createInstanceWithDefaultParameters() {
        return new ResourceAndSnapshotFilter();
    }

    @Override
    protected ResourceAndSnapshotFilter createInstanceFromOther(ResourceAndSnapshotFilter other) {
        return new ResourceAndSnapshotFilter(other);
    }

    @Test
    public void exportEnabledWhenIncludedDataSnapshotAndResourceUri() {
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(true, result);
    }

    @Test
    public void exportDisabledWhenNotIncludedDataSnapshot() {
        fullyConfiguredTestInstance.setIncludeDataSnapshots(false);
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(false, result);
    }

    @Test
    public void exportDisabledWhenNullResourceUri() {
        fullyConfiguredTestInstance.setResourceUri(null);
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(false, result);
    }

    @Test
    public void exportDisabledWhenEmptyResourceUri() {
        fullyConfiguredTestInstance.setResourceUri("");
        boolean result = fullyConfiguredTestInstance.exportEnabled();
        assertEquals(false, result);
    }

    @Test
    public void resourceUriIsSetWhenResourceUri() {
        boolean result = fullyConfiguredTestInstance.resourceUriSet();
        assertEquals(true, result);
    }

    @Test
    public void resourceUriIsNotSetWhenNullResourceUri() {
        fullyConfiguredTestInstance.setResourceUri(null);
        boolean result = fullyConfiguredTestInstance.resourceUriSet();
        assertEquals(false, result);
    }

    @Test
    public void resourceUriIsNotSetWhenEmptyResourceUri() {
        fullyConfiguredTestInstance.setResourceUri("");
        boolean result = fullyConfiguredTestInstance.resourceUriSet();
        assertEquals(false, result);
    }

    @Test
    public void dataSnapshotExportEnabledWhenResourceUriSetAndIncludeDataSnapShot() {
        boolean result = fullyConfiguredTestInstance.exportDatasnapshotEnabled();
        assertEquals(true, result);
    }

    @Test
    public void dataSnapshotExportDisabledWhenResourceUriIsNotSet() {
        fullyConfiguredTestInstance.setResourceUri("");
        boolean result = fullyConfiguredTestInstance.exportDatasnapshotEnabled();
        assertEquals(false, result);
    }

    @Test
    public void dataSnapshotExportDisabledWhenNotIncludeDataShapShot() {
        fullyConfiguredTestInstance.setIncludeDataSnapshots(false);
        boolean result = fullyConfiguredTestInstance.exportDatasnapshotEnabled();
        assertEquals(false, result);
    }

    @Test
    public void resourceUriDoesNotMatchWhenResourceUriIsNotSet() {
        fullyConfiguredTestInstance.setResourceUri("");
        boolean result = fullyConfiguredTestInstance.resourceUriMatch("test");
        assertEquals(false, result);
    }

    @Test
    public void resourceUriMatchWhenResourceUriIsTheSame() {
        fullyConfiguredTestInstance.setResourceUri("test");
        boolean result = fullyConfiguredTestInstance.resourceUriMatch("test");
        assertEquals(true, result);
    }
}
