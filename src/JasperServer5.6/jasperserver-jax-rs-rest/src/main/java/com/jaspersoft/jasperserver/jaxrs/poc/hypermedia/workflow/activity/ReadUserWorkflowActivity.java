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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.activity;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.AbstractActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.Activity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.embedded.EmbeddedElement;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.data.UserWorkflowStorage;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.dto.UserWorkflow;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.workflow.representation.UserWorkflowRepresentation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */

@Component
public class ReadUserWorkflowActivity extends AbstractActivity<UserWorkflowRepresentation, UserWorkflow> {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private UserWorkflowStorage userWorkflowStorage;

    public ReadUserWorkflowActivity() {
        super();
    }

    public ReadUserWorkflowActivity(Map<Relation, Activity> relations, List<Relation> linkRelations) {
        super(relations, linkRelations);
    }

    @Override
    public EmbeddedElement proceed() {
        //workaround to resolve circular dependency with workflow collection activity
        relations.put(Relation.workflows, (Activity)applicationContext.getBean("workflowCollectionActivity"));
        return super.proceed();
    }

    @Override
    public UserWorkflow findData(GenericRequest request){
        return userWorkflowStorage.findByName((String)request.getParams().get("name"));
    }

    @Override
    public UserWorkflowRepresentation buildRepresentation() {
        return new UserWorkflowRepresentation(data);
    }

    @Override
    public Relation getOwnRelation() {
        return Relation.workflow;
    }

}
