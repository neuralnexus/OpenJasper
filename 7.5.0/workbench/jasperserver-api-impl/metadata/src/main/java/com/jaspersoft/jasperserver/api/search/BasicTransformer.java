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

package com.jaspersoft.jasperserver.api.search;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BasicTransformer implements ResultTransformer {
    public List<Long> transformToIdList(List resultList) {
        List<Long> idList = new ArrayList<Long>();

        if (resultList != null) {
            for (Object result : resultList) {
                Object[] values = (Object[]) result;

                idList.add((Long) values[0]);
            }
        }

        return idList;
    }

    public Integer transformToCount(List resultList) {
        int count = 0;

        // TODO:  HibernateUpgrade 26.08.2016 - new Hibernate returning Long values, most probably we need to refactor all classes which depend from this transformer to handle Long (currently Integer)
        if (resultList.size() == 1) { // In this case we have count for single classes
            Object result = resultList.get(0);
            count = (result instanceof Integer ? (Integer)result : ((Long) result).intValue());
        } else if (resultList.size() > 1) { // In this case we have count for several derived classes
            for (Object obj : resultList) {
                count += (obj instanceof Integer ? (Integer)obj : ((Long) obj).intValue());;
            }
        }
        return count;
    }
}
