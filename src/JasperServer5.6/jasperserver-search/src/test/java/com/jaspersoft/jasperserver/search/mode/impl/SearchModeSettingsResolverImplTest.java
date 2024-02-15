package com.jaspersoft.jasperserver.search.mode.impl;

import com.jaspersoft.jasperserver.search.mode.SearchMode;
import com.jaspersoft.jasperserver.search.mode.SearchModeSettings;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p>Tests for {@link SearchModeSettingsResolverImpl}</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchModeSettingsResolverImplTest extends UnitilsJUnit4 {
    @Test
    public void getSettings() {
        SearchModeSettingsResolverImpl searchModeSettingsResolver = new SearchModeSettingsResolverImpl();

        Map<SearchMode, SearchModeSettings> settingsMap = new HashMap<SearchMode, SearchModeSettings>();

        SearchModeSettings actualSearchModeSettings = new SearchModeSettings();

        settingsMap.put(SearchMode.BROWSE, actualSearchModeSettings);

        searchModeSettingsResolver.setSettingsMap(settingsMap);

        assertEquals(actualSearchModeSettings, searchModeSettingsResolver.getSettings(SearchMode.BROWSE));
    }

    @Test
    public void getSettingsForEmptySettingsMap() {
        SearchModeSettingsResolverImpl searchModeSettingsResolver = new SearchModeSettingsResolverImpl();

        Map<SearchMode, SearchModeSettings> settingsMap = new HashMap<SearchMode, SearchModeSettings>();
        searchModeSettingsResolver.setSettingsMap(settingsMap);

        assertNull(searchModeSettingsResolver.getSettings(SearchMode.BROWSE));
    }
}
