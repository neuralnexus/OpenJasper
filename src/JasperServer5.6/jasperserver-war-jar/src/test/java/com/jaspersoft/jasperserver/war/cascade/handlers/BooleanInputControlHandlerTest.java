package com.jaspersoft.jasperserver.war.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.war.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.war.cascade.CachedRepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.MockUnitils;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.war.cascade.utils.CascadeTestHelper.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * @author Anton Fomin
 * @version $Id: BooleanInputControlHandlerTest.java 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */
public class BooleanInputControlHandlerTest extends UnitilsJUnit4 {

    private Mock<CachedEngineService> cachedEngineService;

    ParametersHelper ph;

    private InputControlHandler getHandler() {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        Mock<CachedRepositoryService> cachedRepositoryService = MockUnitils.createMock(CachedRepositoryService.class);
        mockedServices.put("cachedRepositoryService", cachedRepositoryService.getMock());
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("cachedEngineService", cachedEngineService.getMock());
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/war/cascade/applicationContext-cascade-test.xml");

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>)context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get("1").get("handler");
    }

    @Test
    public void formatValueNull() throws Exception {

        String typedValue = null;
        String[] expectedValue = new String[] {"false"};

        String[] formattedValue = getHandler().formatValue(typedValue, ph.getInputControl("Boolean"), ph.getInputControlInfo().getInputControlInformation("Boolean"));

        assertReflectionEquals(expectedValue, formattedValue);
    }

    @Before
    public void prepare() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo("Boolean", "Boolean", InputControl.TYPE_BOOLEAN, Boolean.class, null, Boolean.TRUE, true);
    }
}
