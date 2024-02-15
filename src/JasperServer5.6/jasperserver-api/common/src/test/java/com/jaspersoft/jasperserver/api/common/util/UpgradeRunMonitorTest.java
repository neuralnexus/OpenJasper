package com.jaspersoft.jasperserver.api.common.util;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * <p>Tests {@link UpgradeRunMonitor}</p>
 *
 * @author Yuriy Plakosh
 * @version $Id: UpgradeRunMonitorTest.java 24896 2012-09-15 05:10:46Z yuriy.plakosh $
 * @since 4.7.0
 */
public class UpgradeRunMonitorTest extends UnitilsJUnit4 {
    @Test
    public void testUpgradeRun() {
        assertFalse(UpgradeRunMonitor.isUpgradeRun());

        UpgradeRunMonitor.start();

        assertTrue(UpgradeRunMonitor.isUpgradeRun());

        UpgradeRunMonitor.stop();

        assertFalse(UpgradeRunMonitor.isUpgradeRun());
    }
}
