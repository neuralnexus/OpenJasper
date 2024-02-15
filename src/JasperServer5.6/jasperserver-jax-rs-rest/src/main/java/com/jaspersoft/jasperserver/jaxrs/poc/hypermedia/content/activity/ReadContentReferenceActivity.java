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

package com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.activity;

import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.Relation;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.GenericRequest;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.common.activity.AbstractActivity;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.data.ContentReferenceStorage;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.dto.ContentReference;
import com.jaspersoft.jasperserver.jaxrs.poc.hypermedia.content.representation.ContentReferenceRepresentation;

import javax.annotation.Resource;

/**
 * @author: Igor.Nesterenko
 * @version: ${Id}
 */
public class ReadContentReferenceActivity extends AbstractActivity<ContentReferenceRepresentation, ContentReference> {

    @Resource
    private ContentReferenceStorage contentStorage;

    @Override
    public Relation getOwnRelation() {
        return Relation.contentReference;
    }

    @Override
    public ContentReference findData(GenericRequest request){
        return contentStorage.findById((String) request.getParams().get("id"));
    }

    @Override
    public ContentReferenceRepresentation buildRepresentation() {
        return new ContentReferenceRepresentation(data);
    }
}
