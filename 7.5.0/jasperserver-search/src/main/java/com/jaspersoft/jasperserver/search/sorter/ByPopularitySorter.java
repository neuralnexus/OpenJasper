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

package com.jaspersoft.jasperserver.search.sorter;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.search.SearchSorter;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

/**
 */
public class ByPopularitySorter extends SearchSorter implements Serializable {
    @Override
    protected void addOrder(String type, ExecutionContext context, SearchCriteria criteria) {
        // works with Resource.class only, for ResourceItem - skip
    }

    @Override
    protected void addProjection(String type, ExecutionContext context, SearchCriteria criteria) {
//      please do not remove Projections.rowCount() - it's required for bug fix JRS-19427
//      Hibernate is not properly managing columnNames for groupProperty and this will cause errors inside of Hibernate
//      https://hibernate.atlassian.net/browse/HHH-5854
        criteria.addProjection(Projections.projectionList().
                add(Projections.count("accessEvents"), "aec").
                add(Projections.alias(Projections.groupProperty("accessEvents"),"aeid")).
                add(Projections.rowCount()));

        criteria.addOrder(Order.desc("aec")).addOrder(Order.asc("id"));
    }
}