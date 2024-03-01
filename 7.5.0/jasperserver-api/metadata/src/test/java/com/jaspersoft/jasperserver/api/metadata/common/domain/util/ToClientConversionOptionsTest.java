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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Volodya Sabadosh
 */
public class ToClientConversionOptionsTest {

    @Test
    public void isExpansionEnabled_expandedIsTrueAndLocalResourceIsFalse_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(true);
        assertTrue(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpansionEnabled_expandedIsTrueAndLocalResourceIsTrue_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(true);
        assertTrue(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpansionEnabled_inMemoryResourceIsTrueAndLocalResourceIsTrue_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(true);
        assertTrue(options.isExpansionEnabled(true));
    }

    @Test
    public void isExpansionEnabled_inMemoryResourceIsTrueAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(true);
        assertFalse(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpansionEnabled_inMemoryResourceIsFalseAndLocalResourceIsTrue_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(false);
        assertFalse(options.isExpansionEnabled(true));
    }

    @Test
    public void isExpansionEnabled_inMemoryResourceIsFalseAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(false);
        assertFalse(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpansionEnabled_expandTypesIsNotEmptyAndLocalResourceIsFalse_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpansionEnabled_expandTypesIsNotEmptyAndLocalResourceIsTrue_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpansionEnabled(true));
    }

    @Test
    public void isExpansionEnabled_expandTypesIsEmptyAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.emptySet());
        assertFalse(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpansionEnabled_expandTypesIsEmptyAndLocalResourceIsTrue_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.emptySet());
        assertFalse(options.isExpansionEnabled(true));
    }

    @Test
    public void isExpansionEnabled_ExpandedIsFalseAndInMemoryResourceIsFalseAndLocalResourceIsFalseAndExpandTypesIsNull_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(false).setExpandTypes(null);
        assertFalse(options.isExpansionEnabled(false));
    }

    @Test
    public void isExpanded_expandedIsTrueAndLocalResourceIsFalse_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(true);
        assertTrue(options.isExpanded(null, false));
    }

    @Test
    public void isExpanded_expandedIsTrueAndLocalResourceIsTrue_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(true);
        assertTrue(options.isExpanded(null, false));
    }

    @Test
    public void isExpanded_inMemoryResourceIsTrueAndLocalResourceIsTrue_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(true);
        assertTrue(options.isExpanded(null, true));
    }

    @Test
    public void isExpanded_inMemoryResourceIsTrueAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(true);
        assertFalse(options.isExpanded(null, false));
    }

    @Test
    public void isExpanded_inMemoryResourceIsFalseAndLocalResourceIsTrue_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(false);
        assertFalse(options.isExpanded(null, true));
    }

    @Test
    public void isExpanded_inMemoryResourceIsFalseAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setInMemoryResource(false);
        assertFalse(options.isExpanded(null, false));
    }

    @Test
    public void isExpanded_expandTypesContainsOtherTypeAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertFalse(options.isExpanded("otherType", false));
    }

    @Test
    public void isExpanded_expandTypesContainsOtherTypeAndLocalResourceIsTrue_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertFalse(options.isExpanded("otherType", true));
    }

    @Test
    public void isExpanded_expandTypesContainsSomeTypeAndLocalResourceIsFalse_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpanded("someType", true));
    }

    @Test
    public void isExpanded_expandTypesContainsSomeTypeAndLocalResourceIsTrue_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpanded("someType", true));
    }

    @Test
    public void isExpanded_expandTypesIsEmptyAndLocalResourceIsFalse_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.emptySet());
        assertFalse(options.isExpanded(null, false));
    }

    @Test
    public void isExpanded_expandTypesIsEmptyAndLocalResourceIsTrue_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().
                setExpanded(false).setInMemoryResource(false).setExpandTypes(Collections.emptySet());
        assertFalse(options.isExpanded(null, true));
    }

    @Test
    public void isExpanded_ExpandedIsFalseAndInMemoryResourceIsFalseAndLocalResourceIsFalseAndExpandTypesIsNull_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(false).setExpandTypes(null);
        assertFalse(options.isExpanded(null, false));
    }

    @Test
    public void isExpansionByType_ExpandedIsFalseAndInMemoryResourceIsFalseAndLocalResourceIsFalseAndExpandTypesIsNull_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(false).setExpandTypes(null);
        assertFalse(options.isExpanded(null, false));
    }

    @Test
    public void isExpansionByType_ExpandedIsTrueAndInMemoryResourceIsFalseAndLocalResourceIsFalseAndExpandTypesIsNotNull_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(true).
                setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertFalse(options.isExpansionByType(false));
    }

    @Test
    public void isExpansionByType_ExpandedIsFalseAndInMemoryResourceIsTrueAndLocalResourceIsTrueAndExpandTypesIsNotNull_returnFalse() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(true).setExpandTypes(Collections.singleton("someType"));
        assertFalse(options.isExpansionByType(true));
    }

    @Test
    public void isExpansionByType_ExpandedIsFalseAndInMemoryResourceIsTrueAndLocalResourceIsFalseAndExpandTypesIsNotNull_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(true).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpansionByType(false));
    }

    @Test
    public void isExpansionByType_ExpandedIsFalseAndInMemoryResourceIsFalseAndLocalResourceIsTrueAndExpandTypesIsNotNull_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpansionByType(true));
    }

    @Test
    public void isExpansionByType_ExpandedIsFalseAndInMemoryResourceIsFalseAndLocalResourceIsFalseAndExpandTypesIsNotNull_returnTrue() {
        ToClientConversionOptions options = new ToClientConversionOptions().setExpanded(false).
                setInMemoryResource(false).setExpandTypes(Collections.singleton("someType"));
        assertTrue(options.isExpansionByType(false));
    }

}
