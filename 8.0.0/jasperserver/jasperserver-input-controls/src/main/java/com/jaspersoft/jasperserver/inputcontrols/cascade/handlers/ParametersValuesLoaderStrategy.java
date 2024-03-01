package com.jaspersoft.jasperserver.inputcontrols.cascade.handlers;

import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.ListOrderedMap;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Maps values directly from parameters to OrderedMap which is used further by QueryValuesLoader to convert them to ListOfValuesItems.
 * Parameter's map contains all selected input controls values loaded from topic XML, and this class uses them to avoid additional
 * triggering additional database SQL query request.
 * </p>
 *
 * @author Vlad Zavadskyi
 */
@Service
public class ParametersValuesLoaderStrategy implements ValuesLoaderStrategy {
    @Override
    public ResultsOrderedMap resultsOrderedMap(InputControl inputControl,
                                               ResourceReference dataSourceForQuery,
                                               String criteria,
                                               Map<String, Object> parameters,
                                               Map<String, Class<?>> parameterTypes) {
        final String inputControlName = inputControl.getName();
        final ResultsOrderedMap.Builder builder = new ResultsOrderedMap.Builder();

        if (MapUtils.isEmpty(parameters) || MapUtils.isEmpty(parameterTypes)) return builder.build();
        if (parameters.get(inputControlName) == null || parameterTypes.get(inputControlName) == null) return builder.build();

        Object inputControlValue = parameters.get(inputControlName);
        ListOrderedMap result = new ListOrderedMap();

        Class<?> parametersType = parameterTypes.get(inputControlName);
        if (Collection.class.isAssignableFrom(parametersType)) {
            Collection<?> collection = (Collection<?>) inputControlValue;
            collection.forEach(value -> {
                if (isNull(value)) {
                    result.put(0, null, new Object[]{null});
                } else {
                    result.put(value, new Object[]{value});
                }
            });
        } else {
            result.put(inputControlValue, new Object[]{inputControlValue});
        }

        return builder.setOrderedMap(result).build();
    }

    private boolean isNull(Object object) {
        return object == null || "null".equals(object);
    }
}
