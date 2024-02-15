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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.visitor;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.visitor.RelationsVisitor;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.activity.ReadContentReferenceActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.representation.HypermediaRepresentation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.dto.ContentReference;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.representation.ContentReferenceCollectionRepresentation;

import java.util.List;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class ContentReferenceCollectionRelationsVisitor extends RelationsVisitor<ContentReferenceCollectionRepresentation> {

    public void resolve(ReadContentReferenceActivity contentReferenceActivity, Relation relation, Boolean isLink){
        if(!isLink){
            List<ContentReference> contentReferences = representation.getBody();
            for (ContentReference content : contentReferences) {
                contentReferenceActivity.setData(content);
                HypermediaRepresentation childRepresentation = (HypermediaRepresentation) contentReferenceActivity.proceed();
                representation.addEmbedded(relation, childRepresentation);
            }
        }
    }

}
