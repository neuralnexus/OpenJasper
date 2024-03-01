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
public interface InputControlDataSourceResolver {
    ResourceReference resolveDatasource(InputControl inputControl, ResourceReference dataSourceReference)
            throws CascadeResourceNotFoundException;

    /**
     * DomainFilterResolver needs access to the domain schema, which it can get from
     * the param map. FilterCore doesn't need this, and it would allocate a connection
     * that's not needed.
     */
    Map<String, Object> prepareDomainDataSourceParameters(ResourceReference dataSourceReference)
            throws CascadeResourceNotFoundException;
}
