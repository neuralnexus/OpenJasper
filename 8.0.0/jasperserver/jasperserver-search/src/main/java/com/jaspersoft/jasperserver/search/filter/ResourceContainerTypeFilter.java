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
package com.jaspersoft.jasperserver.search.filter;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResourceItemBase;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.ResourceCriterionUtils;
import com.jaspersoft.jasperserver.api.search.SearchCriteria;
import com.jaspersoft.jasperserver.core.util.DBUtil;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;

/**
 * <p>Filters container types elements if they not contains type elements</p>
 *
 * @author Zakhar Tomchenco
 * @version $Id$
 */
public class ResourceContainerTypeFilter extends BaseSearchFilter implements Serializable {
    public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
        final RepositorySearchCriteria repositorySearchCriteria = getTypedAttribute(context, RepositorySearchCriteria.class);

        if (repositorySearchCriteria != null && repositorySearchCriteria.getContainerResourceTypes() != null && !repositorySearchCriteria.getContainerResourceTypes().isEmpty()){

            DetachedCriteria sizeCriteria = DetachedCriteria.forClass(RepoResourceItemBase.class,"item");

            sizeCriteria.createAlias("item.parent", "p2");
            if(CollectionUtils.isNotEmpty(getSubqueryResourceTypes(repositorySearchCriteria))) {
                sizeCriteria.add(DBUtil.getBoundedInCriterion("item.resourceType", getSubqueryResourceTypes(repositorySearchCriteria)));
            }
            sizeCriteria.add(Restrictions.eq("p2.hidden", repositorySearchCriteria.isShowHidden()));

            sizeCriteria.add(ResourceCriterionUtils.getSQLCriterion("(p2x1_.uri = CONCAT(p1_.uri , '/' , this_.name) or p2x1_.uri like CONCAT(p1_.uri , '/' , this_.name , '/%') or (p1_.uri='/' and (p2x1_.uri = CONCAT('/' , this_.name) or p2x1_.uri like CONCAT('/' , this_.name , '/%'))))"));

            criteria.add(Subqueries.exists(sizeCriteria.setProjection(Projections.property("item.name"))));
        }
    }

    protected List<String> getSubqueryResourceTypes(RepositorySearchCriteria repositorySearchCriteria){
        List<String> containerTypes = repositorySearchCriteria.getContainerResourceTypes();
        List<String> baseTypes = new ArrayList<String>(repositorySearchCriteria.getResourceTypes());
        baseTypes.removeAll(containerTypes);

        return baseTypes;
    }
}