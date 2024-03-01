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

package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedEngineService;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CachedRepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createCalendarFormatProvider;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createEngineService;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createFilterResolver;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createMessageSource;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.createParameterTypeLookup;
import static com.jaspersoft.jasperserver.inputcontrols.cascade.utils.CascadeTestHelper.setUpApplicationContext;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Anton Fomin
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class BooleanInputControlHandlerTest {

    private ParametersHelper ph;

    private InputControlHandler getHandler() {
        Map<String, Object> mockedServices = new LinkedHashMap<String, Object>();
        CachedRepositoryService cachedRepositoryService = mock(CachedRepositoryService.class);
        mockedServices.put("cachedRepositoryService", cachedRepositoryService);
        mockedServices.put("filterResolver", createFilterResolver());
        mockedServices.put("parameterTypeCompositeLookup", createParameterTypeLookup());
        mockedServices.put("cachedEngineService", mock(CachedEngineService.class));
        mockedServices.put("engineService", createEngineService());
        mockedServices.put("isoCalendarFormatProvider", createCalendarFormatProvider());
        mockedServices.put("messageSource", createMessageSource());

        ApplicationContext context = setUpApplicationContext(mockedServices, "classpath:/com/jaspersoft/jasperserver/inputcontrols/cascade/applicationContext-cascade-test.xml");

        Map<String, Map<String, Object>> config = (Map<String, Map<String, Object>>)context.getBean("inputControlTypeNewConfiguration");
        return (InputControlHandler)config.get("1").get("handler");
    }

    @Test
    public void formatValueNull() throws Exception {
        String typedValue = null;
        String[] expectedValue = new String[] {"false"};

        String[] formattedValue = getHandler().formatValue(typedValue, ph.getInputControl("Boolean"), ph.getInputControlInfo().getInputControlInformation("Boolean"));

        assertThat(expectedValue, is(formattedValue));
    }

    @Before
    public void prepare() {
        ph = new ParametersHelper();
        ph.addParameterAndControlInfo("Boolean", "Boolean", InputControl.TYPE_BOOLEAN, Boolean.class, null, Boolean.TRUE, true);
    }
}
