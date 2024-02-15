/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.api.search;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;

import javax.annotation.Resource;

/**
 */
public abstract class SearchSorter {
    @Resource(name = "queryModificationEvaluator")
    private QueryModificationEvaluator evaluator;

    public final void applyOrder(String type, ExecutionContext context, SearchCriteria criteria) {
        if (evaluator == null || evaluator.useFullResource(context)) {
            addProjection(type, context, criteria);
        }
        addOrder(type, context, criteria);
    }

    protected abstract void addOrder(String type, ExecutionContext context, SearchCriteria criteria);
    protected abstract void addProjection(String type, ExecutionContext context, SearchCriteria criteria);
}
