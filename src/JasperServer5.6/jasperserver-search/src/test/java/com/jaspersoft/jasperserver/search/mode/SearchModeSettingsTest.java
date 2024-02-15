package com.jaspersoft.jasperserver.search.mode;

import com.jaspersoft.jasperserver.search.common.RepositorySearchConfiguration;
import com.jaspersoft.jasperserver.search.state.InitialStateResolver;
import com.jaspersoft.jasperserver.search.state.impl.StateImpl;
import com.jaspersoft.jasperserver.search.util.JSONConverter;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * <p>Tests for {@link SearchModeSettings}</p>
 *
 * @author Yuriy Plakosh
 * @version $Id$
 */
public class SearchModeSettingsTest extends UnitilsJUnit4 {
    @Test
    public void testGettersAndSetters() {
        SearchModeSettings searchModeSettings = new SearchModeSettings();

        assertNull(searchModeSettings.getJsonConverter());
        assertNull(searchModeSettings.getInitialStateResolver());
        assertNull(searchModeSettings.getRepositorySearchConfiguration());

        JSONConverter actualJsonConverter = new JSONConverter();
        InitialStateResolver actualInitialStateResolver = new StateImpl();
        RepositorySearchConfiguration actualRepositorySearchConfiguration = new RepositorySearchConfiguration();

        searchModeSettings.setJsonConverter(actualJsonConverter);
        searchModeSettings.setInitialStateResolver(actualInitialStateResolver);
        searchModeSettings.setRepositorySearchConfiguration(actualRepositorySearchConfiguration);

        assertEquals(actualJsonConverter, searchModeSettings.getJsonConverter());
        assertEquals(actualInitialStateResolver, searchModeSettings.getInitialStateResolver());
        assertEquals(actualRepositorySearchConfiguration, searchModeSettings.getRepositorySearchConfiguration());
    }
}
