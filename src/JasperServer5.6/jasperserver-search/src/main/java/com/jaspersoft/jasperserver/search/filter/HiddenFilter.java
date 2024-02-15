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

package com.jaspersoft.jasperserver.search.filter;

import java.io.Serializable;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceLookup;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class HiddenFilter extends BaseSearchFilter implements Serializable {
    @Override
    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        RepositorySearchCriteria searchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        if (searchCriteria == null || !searchCriteria.isShowHidden())  {
            String p = criteria.getAlias("parent", "p");
            criteria.add(Restrictions.eq(p + ".hidden", Boolean.FALSE));

            if (Folder.class.getName().equals(type)){
                criteria.add(Restrictions.eq("hidden", Boolean.FALSE));
            }

            if (ResourceLookup.class.getName().equals(type)){
                criteria.add(Restrictions.or(Restrictions.isNull("hidden"), Restrictions.eq("hidden", Boolean.FALSE)));
            }
        }
    }
}
