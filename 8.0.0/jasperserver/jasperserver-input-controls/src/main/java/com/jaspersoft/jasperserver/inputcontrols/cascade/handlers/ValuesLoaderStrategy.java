package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;

import java.util.Map;

/**
 * <p></p>
 *
 * @author Vlad Zavadskyi
 */
@FunctionalInterface
public interface ValuesLoaderStrategy {
    ResultsOrderedMap resultsOrderedMap(InputControl inputControl,
                                        ResourceReference dataSourceForQuery,
                                        String criteria,
                                        Map<String, Object> parameters,
                                        Map<String, Class<?>> parameterTypes) throws CascadeResourceNotFoundException;
}
