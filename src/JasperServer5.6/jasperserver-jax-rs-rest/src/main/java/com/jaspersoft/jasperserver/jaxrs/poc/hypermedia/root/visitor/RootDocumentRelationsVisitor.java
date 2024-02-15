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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.root.visitor;

import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.visitor.RelationsVisitor;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.activity.ReadContentReferenceCollectionActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.resource.activity.SearchResourcesActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.activity.ReadUserWorkflowCollectionActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.root.representation.RootDocumentRepresentation;
import com.jaspersoft.jasperserver.search.mode.AccessType;
import com.jaspersoft.jasperserver.search.service.RepositorySearchCriteria;
import org.springframework.security.context.SecurityContextHolder;

import java.util.Arrays;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

public class RootDocumentRelationsVisitor extends RelationsVisitor<RootDocumentRepresentation> {


    public void resolve(ReadContentReferenceCollectionActivity activity, Relation relation, Boolean isLink){

        GenericRequest genericRequest = new GenericRequest().addParam("group", "popular");

        activity.setGenericRequest(genericRequest);

        if(!isLink){
            activity.setData(activity.findData(genericRequest));
            representation.addEmbedded(relation, (HypermediaRepresentation) activity.proceed());
        }else{
            representation.addLink(activity.buildLink());
        }

    }

    public void resolve(ReadUserWorkflowCollectionActivity activity, Relation relation, Boolean isLink){

        GenericRequest genericRequest = new GenericRequest().addParam("parentName", "main");

        activity.setGenericRequest(genericRequest);

        if(!isLink){
            activity.setData(activity.findData(genericRequest));
            representation.addEmbedded(relation,(HypermediaRepresentation) activity.proceed());
        }else{
            representation.addLink(activity.buildLink());
        }
    }

    public void resolve(SearchResourcesActivity activity, Relation relation, Boolean isLink){

        RepositorySearchCriteria criteria = activity.getCriteria();

        criteria.setResourceTypes(Arrays.asList(
                ResourceMediaType.REPORT_UNIT_CLIENT_TYPE,
                ResourceMediaType.ADHOC_DATA_VIEW_CLIENT_TYPE,
                ResourceMediaType.OLAP_UNIT_CLIENT_TYPE,
                ResourceMediaType.DASHBOARD_CLIENT_TYPE
        ));

        criteria.setExcludeRelativePaths(Arrays.asList("/temp", "/adhoc/topics", "/public/adhoc/topics"));

        criteria.setAccessType(AccessType.VIEWED);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        criteria.setSortBy("accessTime");

        criteria.setUser(user);

        criteria.setMaxCount(10);

        if (!isLink){
            activity.setData(activity.findData(null));
            representation.addEmbedded(relation, (HypermediaRepresentation) activity.proceed());
        }else{
            activity.setCriteria(criteria);
            representation.addLink(activity.buildLink());
        }

    }

}
