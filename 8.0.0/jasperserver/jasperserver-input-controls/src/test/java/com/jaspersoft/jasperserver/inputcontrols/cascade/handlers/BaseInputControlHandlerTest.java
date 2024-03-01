package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.InputControlState;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.converters.DataConverterService;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.ValuesLoader.SELECTED_ONLY_INTERNAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public abstract class BaseInputControlHandlerTest {

    private static final String INPUT_CONTROL_NAME = "inputControl";

    @Mock
    protected ValuesLoader loader;

    @Mock
    protected DataConverterService dataConverterService;

    @Mock
    protected InputControl inputControl;

    @Mock
    protected ResourceReference dataSource;

    @Mock
    protected ReportInputControlInformation reportInputControlInformation;

    @Mock
    protected MessageSource messageSource;

    @Spy
    protected InputControlPagination inputControlPagination;

    protected List<ListOfValuesItem> values;

    protected InputControlState state;

    @Before
    public void setUp() throws CascadeResourceNotFoundException {
        values = getListOfValuesItem();
        state = new InputControlState();

        doReturn(INPUT_CONTROL_NAME).when(inputControl).getName();
        doAnswer(invocation -> invocation.getArgument(0)).when(dataConverterService)
                .convertSingleValue(anyString(), any(InputControl.class), any());
        doAnswer(invocation -> invocation.getArgument(0)).when(dataConverterService)
                .formatSingleValue(anyString(), any(InputControl.class), any());
    }

    protected void setLimit(Map<String, Object> parameters, int limit) {
        parameters.put(INPUT_CONTROL_NAME + "_" + InputControlPagination.LIMIT, String.valueOf(limit));
    }

    protected void setOffset(Map<String, Object> parameters, int offset) {
        parameters.put(INPUT_CONTROL_NAME + "_" + InputControlPagination.OFFSET, String.valueOf(offset));
    }

    protected void setSelectedOnly(Map<String, Object> parameters, boolean selectedOnly) {
        parameters.put(SELECTED_ONLY_INTERNAL, Boolean.toString(selectedOnly));
    }

    protected void setIncomingValue(Map<String, Object> parameters, Object incomingValue, InputControl inputControl) {
        parameters.put(inputControl.getName(), incomingValue);
    }

    protected List<ListOfValuesItem> getListOfValuesItem() {
        List<ListOfValuesItem> values = new ArrayList<>();

        values.add(listOfValuesItem("USA", "USA"));
        values.add(listOfValuesItem("Canada", "Canada"));
        values.add(listOfValuesItem("Mexico", "Mexico"));

        return values;
    }

    protected void mockLoadValues(List<ListOfValuesItem> values) throws CascadeResourceNotFoundException {
        doReturn(values).when(loader).loadValues(
                eq(inputControl), eq(dataSource), anyMap(), anyMap(),
                eq(reportInputControlInformation), anyBoolean()
        );
    }

    protected ListOfValuesItem listOfValuesItem(String label, Object value) {
        ListOfValuesItem item = new ListOfValuesItemImpl();
        item.setLabel(label);
        item.setValue(value);
        return item;
    }
}
