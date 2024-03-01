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

package com.jaspersoft.jasperserver.search.mode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>Tests {@link SearchMode} class.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchModeTest {
    @Test
    public void getModeForCorrectModeString() {
        assertEquals(SearchMode.BROWSE, SearchMode.getMode("BROWSE"));
        assertEquals(SearchMode.SEARCH, SearchMode.getMode("SEARCH"));
        assertEquals(SearchMode.LIBRARY, SearchMode.getMode("LIBRARY"));

        assertEquals(SearchMode.BROWSE, SearchMode.getMode("browse"));
        assertEquals(SearchMode.SEARCH, SearchMode.getMode("search"));
        assertEquals(SearchMode.LIBRARY, SearchMode.getMode("library"));
    }

    @Test
    public void getModeForIncorrectModeString() {
        assertEquals(SearchMode.BROWSE, SearchMode.getMode("INCORRECT_MODE"));
    }

    @Test
    public void getModeForNullModeString() {
        assertEquals(SearchMode.BROWSE, SearchMode.getMode(null));
    }

    @Test
    public void testToString() {
        assertEquals("browse", SearchMode.BROWSE.toString());
        assertEquals("search", SearchMode.SEARCH.toString());
        assertEquals("library", SearchMode.LIBRARY.toString());
    }
}