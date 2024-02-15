package com.jaspersoft.jasperserver.api.metadata.common.domain.client;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.inject.annotation.TestedObject;

import static org.junit.Assert.assertEquals;

/**
 * Tests ResourceLookupImpl class implementation.
 *
 * @author Yuriy Plakosh
 */
public class ResourceLookupImplTest extends UnitilsJUnit4 {
    @TestedObject
    private ResourceLookupImpl resourceLookup;

    @Test
    public void setURIString() {
        resourceLookup.setURIString("/organization/test_folder/test_resource1");
        assertEquals("Wrong parent folder URI", "/organization/test_folder", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource1", resourceLookup.getName());

        resourceLookup.setURIString("/organization/test_resource2");
        assertEquals("Wrong parent folder URI", "/organization", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource2", resourceLookup.getName());

        resourceLookup.setURIString("/test_resource3");
        assertEquals("Wrong parent folder URI", "/", resourceLookup.getParentFolder());
        assertEquals("Wrong name", "test_resource3", resourceLookup.getName());

        resourceLookup.setURIString("test_resource3");
        assertEquals("Wrong parent folder URI", null, resourceLookup.getParentFolder());
        assertEquals("Wrong name", null, resourceLookup.getName());

        //noinspection NullableProblems
        resourceLookup.setURIString(null);
        assertEquals("Wrong parent folder URI", null, resourceLookup.getParentFolder());
        assertEquals("Wrong name", null, resourceLookup.getName());
    }
}
