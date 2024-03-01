/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InputControl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValuesItem;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.inputcontrols.cascade.CascadeResourceNotFoundException;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jaspersoft.jasperserver.inputcontrols.cascade.handlers.InputControlHandler.TOTAL_COUNT;


/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public interface ValuesLoader {
    String CRITERIA = "criteria";

    String INCLUDE_TOTAL_COUNT = "includeTotalCount";
    String SELECTED_ONLY_INTERNAL = "&selectedOnly";
    String SKIP_FETCHING_IC_VALUES_FROM_DB = "skipFetchingICValuesFromDB";

    List<String> REQUIRED_IC_PARAMETERS = Arrays.asList(
            INCLUDE_TOTAL_COUNT,
            SELECTED_ONLY_INTERNAL,
            SKIP_FETCHING_IC_VALUES_FROM_DB
    );

    /**
     * load input control state values from either database or cache.
     * @param inputControl
     * @param dataSource
     * @param parameters
     * @param parameterTypes
     * @param info
     * @return
     * @throws CascadeResourceNotFoundException
     */
    List<ListOfValuesItem> loadValues(InputControl inputControl, ResourceReference dataSource, Map<String, Object> parameters, Map<String, Class<?>> parameterTypes, ReportInputControlInformation info, boolean isSingleSelect) throws CascadeResourceNotFoundException;

    /**
     *
     * @param inputControl
     * @param dataSource
     * @return
     * @throws CascadeResourceNotFoundException
     */
    Set<String> getMasterDependencies(InputControl inputControl, ResourceReference dataSource) throws CascadeResourceNotFoundException;

    /**
     * Filter by search criteria when provided and then add item to results.
     * @param criteria
     * @param results
     * @param item
     * @return InputControlValues
     */
    default List<ListOfValuesItem> checkCriteriaAndAddItem(String criteria, List<ListOfValuesItem> results, ListOfValuesItem item) {
        if (criteria == null || StringUtils.containsIgnoreCase(item.getLabel(), criteria)) {
            results.add(item);
        }
        return results;
    }

    /**
     * Get criteria from parameters.
     * @param inputControl
     * @param parameters
     * @return criteria
     */
    default String getCriteria(InputControl inputControl, Map<String, Object> parameters) {
        Object criteria = parameters.get(inputControl.getName() + "_" + CRITERIA);
        return (criteria instanceof String) ? (String) criteria : null;
    }

    /**
     * @param parameters
     * @param size
     */
    default void addTotalCountToParameters(Map<String, Object> parameters, int size) {
        if (parameters.get(INCLUDE_TOTAL_COUNT) != null && parameters.get(INCLUDE_TOTAL_COUNT).equals("true")) {
            parameters.put(TOTAL_COUNT, size);
        }
    }

}
