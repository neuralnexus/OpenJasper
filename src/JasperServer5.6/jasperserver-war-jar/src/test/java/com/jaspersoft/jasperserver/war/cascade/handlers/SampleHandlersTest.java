package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import org.junit.Test;

import java.util.Map;

public class SampleHandlersTest extends InputControlHandlerBaseTest {

    @Test
    public void sampleTestCase() throws Exception {
        String testCaseName = "sampleTestCase";

        InputControl control = getControl("state", testCaseName);
        ReportInputControlInformation info = getControlInfo("state", testCaseName);
        InputControlHandler inputControlHandler = getHandler(testCaseName, InputControl.TYPE_MULTI_SELECT_QUERY);
        Map<String,Object> typedParamsMap = getTypedParamsMap(testCaseName);

        //Actuall call of test method
        InputControlState envelope =
                inputControlHandler.getState(control, null, typedParamsMap, getParameterTypes(testCaseName), info);

        assertControlState(testCaseName, envelope);
    }
}
