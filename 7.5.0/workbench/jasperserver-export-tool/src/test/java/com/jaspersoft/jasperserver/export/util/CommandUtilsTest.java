package com.jaspersoft.jasperserver.export.util;


import com.jaspersoft.jasperserver.export.Parameters;
import org.junit.Test;

import static org.junit.Assert.*;


public class CommandUtilsTest {

    @Test
    public void parse() {
        final Parameters parameters = CommandUtils.parse(new String[]{"--import","--keypass=B(#?7qA;C(tpO51-!I:8(~."});
        assertNotNull(parameters);
    }
}
