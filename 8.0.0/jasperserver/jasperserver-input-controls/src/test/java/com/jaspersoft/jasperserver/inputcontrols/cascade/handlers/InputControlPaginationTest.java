package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.inputcontrols.cascade.InputControlValidationException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
public class InputControlPaginationTest {

    private final InputControlPagination inputControlPagination = new InputControlPagination();

    @Test
    public void getOffset_withAnyParameterValue() {
        Map<String, Object> parameters = new HashMap<>();
        InputControl inputControl = mock(InputControl.class);
        when(inputControl.getName()).thenReturn("Country");

        assertEquals(0, inputControlPagination.getOffset(inputControl, parameters, 10, null));

        parameters.put("Country_offset", "1");
        assertEquals(1, inputControlPagination.getOffset(inputControl, parameters, 10, null));
        // with negative offset
        try {
            parameters.put("Country_offset", "-1");
            inputControlPagination.getOffset(inputControl, parameters, 10, null);
            fail("expected InputControlValidationException");
        } catch (InputControlValidationException e) {
            assertNotNull(e);
        }

        //with offset equal to size
        try {
            parameters.put("Country_offset", "10");
            inputControlPagination.getOffset(inputControl, parameters, 10, null);
            fail("expected InputControlValidationException");
        } catch (InputControlValidationException e) {
            assertNotNull(e);
        }
    }

    @Test
    public void getLimit_withAnyParameterValue() {
        Map<String, Object> parameters = new HashMap<>();
        InputControl inputControl = mock(InputControl.class);
        when(inputControl.getName()).thenReturn("Country");

        assertEquals(Integer.MAX_VALUE, inputControlPagination.getLimit(inputControl, parameters, null));

        parameters.put("Country_limit", "2");
        assertEquals(2, inputControlPagination.getLimit(inputControl, parameters, null));

        // with negative limit
        try {
            parameters.put("Country_limit", "-1");
            inputControlPagination.getLimit(inputControl, parameters, null);
            fail("expected InputControlValidationException");
        } catch (InputControlValidationException e) {
            assertNotNull(e);
        }
    }

}