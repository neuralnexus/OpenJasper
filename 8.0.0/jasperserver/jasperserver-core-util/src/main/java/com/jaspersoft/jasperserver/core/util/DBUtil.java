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

package com.jaspersoft.jasperserver.core.util;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DBUtil {
    /**
     * Limit the No. of entities for an IN operator.
     */
    private static final int maxInElements = 900;

    /**
     * Fixes "in" statement limitation. For some databases (Oracle) there is limitation on maximum number of elements
     * inside it.
     *
     * @param idCollection the id Collection.
     *
     * @return fixes "in" criterion.
     */
    public static Criterion getBoundedInCriterion(String propertyName, Collection idCollection) {
        Disjunction disjunction = Restrictions.disjunction();
        List idList;
        int fromIndex = 0;

        // apply no restriction - empty conjunction is always true
        if(CollectionUtils.isEmpty(idCollection)) {
            return Restrictions.conjunction();
        }

        if (idCollection instanceof List) {
            idList = (List)idCollection;
        } else {
            idList = (List)idCollection.parallelStream().collect(Collectors.toList());
        }

        while (maxInElements > 0 && fromIndex < idList.size()) {
            int toIndex = Math.min(fromIndex + maxInElements, idList.size());
            List idSubList = idList.subList(fromIndex, toIndex);
            disjunction.add(Restrictions.in(propertyName, idSubList));
            fromIndex += maxInElements;
        }

        return disjunction;
    }
}
