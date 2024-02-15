package com.jaspersoft.jasperserver.search.mode;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;

/**
 * <p>Tests {@link SearchMode} class.</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchModeTest extends UnitilsJUnit4 {
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