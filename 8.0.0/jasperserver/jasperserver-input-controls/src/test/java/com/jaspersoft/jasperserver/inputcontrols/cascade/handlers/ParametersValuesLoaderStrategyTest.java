package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import org.apache.commons.collections.OrderedMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
@RunWith(MockitoJUnitRunner.class)
public class ParametersValuesLoaderStrategyTest {
    private static final String IC_NAME = "icName";
    private static final String MUTABILITY_CHECK = "mutabilityCheck";

    @InjectMocks
    private ParametersValuesLoaderStrategy strategy;

    private final InputControl inputControl = mock(InputControl.class);

    private final ResourceReference dataSourceReference = mock(ResourceReference.class);


    @Before
    public void setUp() {
        doReturn(IC_NAME).when(inputControl).getName();
    }

    @Test
    public void resultsOrderedMap_emptyParametersAndTypes_null() {
        ResultsOrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null, null, null);
        assertNotNull(result);
        assertNull(result.getOrderedMap());
    }

    @Test
    public void resultsOrderedMap_emptyTypes_null() {
        ResultsOrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, Arrays.asList("value1", "value2")), null
        );
        assertNotNull(result);
        assertNull(result.getOrderedMap());;
    }

    @Test
    public void resultsOrderedMap_emptyParameters_null() {
        ResultsOrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null, null,
                singletonMap(IC_NAME, String.class)
        );
        assertNotNull(result);
        assertNull(result.getOrderedMap());
    }

    @Test
    public void resultsOrderedMap_parametersAndTypesMissInputControl_null() {
        final String anotherIC = "anotherIC";
        ResultsOrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(anotherIC, Arrays.asList("value1", "value2")),
                singletonMap(anotherIC, String.class)
        );
        assertNotNull(result);
        assertNull(result.getOrderedMap());
    }

    @Test
    public void resultsOrderedMap_stringInputControlWithNullValue_null() {
        ResultsOrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, null),
                singletonMap(IC_NAME, String.class)
        );
        assertNotNull(result);
        assertNull(result.getOrderedMap());
    }

    @Test
    public void resultsOrderedMap_stringInputControlWithValue_value() {
        String value = "value";
        OrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, value),
                singletonMap(IC_NAME, String.class)
        ).getOrderedMap();
        assertArrayEquals(new Object[]{value}, (Object[]) result.get(value));
        // Check that returned map is mutable, some code depends on this
        assertNull(result.put(MUTABILITY_CHECK, new Object[]{MUTABILITY_CHECK}));
    }

    @Test
    public void resultsOrderedMap_collectionInputControlWithValues_values() {
        String value1 = "value1";
        String value2 = "value2";
        OrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, Arrays.asList(value1, value2)),
                singletonMap(IC_NAME, List.class)
        ).getOrderedMap();
        assertArrayEquals(new Object[]{value1}, (Object[]) result.get(value1));
        assertArrayEquals(new Object[]{value2}, (Object[]) result.get(value2));
        // Check that returned map is mutable, some code depends on this
        assertNull(result.put(MUTABILITY_CHECK, new Object[]{MUTABILITY_CHECK}));
    }

    @Test
    public void resultsOrderedMap_collectionInputControlWithNullValue_values() {
        String value1 = "value1";
        String value2 = "value2";
        OrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, Arrays.asList(value1, "null", value2)),
                singletonMap(IC_NAME, List.class)
        ).getOrderedMap();
        assertArrayEquals(new Object[]{null}, (Object[]) result.get(null));
        assertArrayEquals(new Object[]{value1}, (Object[]) result.get(value1));
        assertArrayEquals(new Object[]{value2}, (Object[]) result.get(value2));
    }

    @Test
    public void resultsOrderedMap_collectionInputControlWithSingleNullValue_values() {
        OrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, singletonList(null)),
                singletonMap(IC_NAME, List.class)
        ).getOrderedMap();
        assertArrayEquals(new Object[]{null}, (Object[]) result.get(null));
    }

    @Test
    public void resultsOrderedMap_collectionInputControlWithSingleStringNullValue_values() {
        OrderedMap result = strategy.resultsOrderedMap(inputControl, dataSourceReference, null,
                singletonMap(IC_NAME, singletonList("null")),
                singletonMap(IC_NAME, List.class)
        ).getOrderedMap();
        assertArrayEquals(new Object[]{null}, (Object[]) result.get(null));
    }
}