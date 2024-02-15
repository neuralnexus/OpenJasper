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
package com.jaspersoft.jasperserver.api.metadata.olap.domain.impl.hibernate;

import com.jaspersoft.jasperserver.api.JSException;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import org.hibernate.Hibernate;
import org.hibernate.lob.SerializableBlob;

import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.client.OlapUnitImpl;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.sql.SQLException;

/**
 * @author sbirney
 *
 * @hibernate.joined-subclass table="OlapUnit"
 * @hibernate.joined-subclass-key column="id"
 */
public class RepoOlapUnit extends RepoResource {

    private RepoOlapClientConnection olapClientConn = null;
    private String mdxQuery;
    private SerializableBlob olapViewOptions;

    /**
     * @hibernate.many-to-one
     *              column="olapClientConnection"
     *
     */
    public RepoOlapClientConnection getOlapClientConnection() {
	return olapClientConn;
    }

    /**
     *
     */
    public void setOlapClientConnection(RepoOlapClientConnection r) {
	olapClientConn = r;
    }

    /**
     * @hibernate.property column="mdx_query" type="string" length="2000" not-null="true"
     */

    public String getMdxQuery() {
	return mdxQuery;
    }

    public void setMdxQuery(String s) {
	mdxQuery = s;
    }

    public SerializableBlob getOlapViewOptions() {
        return olapViewOptions;
    }

    public void setOlapViewOptions(SerializableBlob sb) {
    	olapViewOptions = sb;
    }

    protected void copyTo(Resource clientRes, ResourceFactory resourceFactory) {
        super.copyTo(clientRes, resourceFactory);

        OlapUnit view = (OlapUnit) clientRes;
        view.setOlapClientConnection(getClientReference(getOlapClientConnection(), resourceFactory));
        view.setMdxQuery(getMdxQuery());
        // do not do de-serialization here due to spring network would try to passing thing around.  The de-serialization
        // is done when it's needed (OlapModelController)
        
        // Hmmm the problem with pushing the deserialization to later is that some DBMSs and JDBC drivers, like Ingres
        // want to read a blob in a transaction, and leaving the deserialization to later will break things.
        
        // Let's try deserializing now. This will make things slower if you are reading and not really wanting to 
        // access the options ,like in repo browsing
        
        if (getOlapViewOptions() != null) {
            Object state = null;
            try {
                XMLDecoder d = new XMLDecoder(new BufferedInputStream(((SerializableBlob)getOlapViewOptions()).getBinaryStream()));
                state = d.readObject();
                d.close();
            } catch (SQLException e) {
                    throw new JSException(e);
            }
            view.setOlapViewOptions(state);
        }
    }

    protected void copyFrom(Resource clientRes, ReferenceResolver referenceResolver) {
        super.copyFrom(clientRes, referenceResolver);
        OlapUnit view = (OlapUnit) clientRes;
        copyOlapClientConnection(referenceResolver, view);
        setMdxQuery( view.getMdxQuery() );
        copyOlapViewOptions(view);

    }


    private void copyOlapClientConnection(ReferenceResolver referenceResolver, OlapUnit view) {
	ResourceReference conn = view.getOlapClientConnection();
	RepoOlapClientConnection repoMC
	    = (RepoOlapClientConnection) getReference(conn, RepoOlapClientConnection.class, referenceResolver);
	setOlapClientConnection(repoMC);
    }

    private void copyOlapViewOptions(OlapUnit view) {
        // if it is an instance of SerializableBlob, then no need to perform the serialization: this is for the repo admin flow
        // otherwise, serialize it to XML and set the blob
        if (view.getOlapViewOptions() != null && !(view.getOlapViewOptions() instanceof SerializableBlob)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XMLEncoder e = new XMLEncoder(new BufferedOutputStream(baos));
            e.writeObject(view.getOlapViewOptions());
            e.flush();
            e.close();
            setOlapViewOptions((SerializableBlob) Hibernate.createBlob(baos.toByteArray()));
        }
    }

    protected Class getClientItf() {
	return OlapUnit.class;
    }



}
