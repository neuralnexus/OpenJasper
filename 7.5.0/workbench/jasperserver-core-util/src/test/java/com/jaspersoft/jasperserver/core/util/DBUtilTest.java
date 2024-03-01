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

package com.jaspersoft.jasperserver.core.util;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DBUtilTest {

    @Test
    public void getBoundedInCriterion_withMoreRecords() {
        Disjunction expectedCriteria = Restrictions.disjunction();
        Set<Integer> set = new HashSet<Integer>();

        // Add entities to list
        for(int i=0; i<2500; i++){
            set.add(i);
        }
        expectedCriteria.add(Restrictions.in("fieldName", new ArrayList(set).subList(0,900)));
        expectedCriteria.add(Restrictions.in("fieldName", new ArrayList(set).subList(900,1800)));
        expectedCriteria.add(Restrictions.in("fieldName", new ArrayList(set).subList(1800,2500)));
        Disjunction actualCriteria =  (Disjunction)DBUtil.getBoundedInCriterion("fieldName", set);
        assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void getFixedInCriterion_withRecords_LessThan900() {
        Disjunction expectedCriteria = Restrictions.disjunction();
        List<String> list = new ArrayList<String>();

        for(int i=0; i<500; i++){
            list.add(String.valueOf(i));
        }
        expectedCriteria.add(Restrictions.in("fieldName", list));
        Disjunction actualCriteria =  (Disjunction)DBUtil.getBoundedInCriterion("fieldName", list);
        assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }

    @Test
    public void getFixedInCriterion_withEmptyRecords() {
        Conjunction expectedCriteria = Restrictions.conjunction();
        List<Integer> list = new ArrayList<Integer>();
        Criterion actualCriteria = DBUtil.getBoundedInCriterion("fieldName", list);
        assertEquals(expectedCriteria.toString(), actualCriteria.toString());
    }
}
