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
package com.jaspersoft.jasperserver.export.modules.repository.beans;

/**
 * @author tkavanagh
 * @version $Id: ResourceBean.java 47331 2014-07-18 09:13:06Z kklein $
 */

import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.Cipherer;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceExportHandler;
import com.jaspersoft.jasperserver.export.modules.repository.ResourceImportHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * The following information applies to all "bean" classes used to marshal and 
 * unmarshal between objects and xml.
 *
 * Castor is used to handle the marshal/unmarshal between java object and xml.
 * To work nicely with castor, which will handle moving between an object bean
 * and it's XML representation, we make sure that all methods conform to the
 * get/set pattern. Particularly, we make sure that boolean methods do this. 
 * So, instead of hasData(), we will use getHasData() and setHasData(). This 
 * way castor's output XML is cleaner and easier to read.
 *
 * As an example, for a FileResource image the output took the following form:
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <file-resource-bean is-reference="false" is-new="false" version="0" has-data="false">
 *   <name>JRLogo</name>
 *   <resource-type>com.jaspersoft.jasperserver.api.metadata.common.domain.FileResource</resource-type>
 *   <creation-date>2006-05-16T18:20:30.000-07:00</creation-date>
 *   <update-date>2006-05-16T18:20:30.000-07:00</update-date>
 *   <uri-string>/images/JRLogo</uri-string>
 *   <parent-folder>/images</parent-folder>
 *   <label>JR logo</label>
 *   <description>JR logo</description>
 *   <file-type>img</file-type>
 * </file-resource-bean> 
 *
 * Primitive types (boolean, int) get written as attributes (ie. is-reference). 
 * Primitive type are always written out with a value. If they are un-set they
 * receive a default value, in this example, of false or zero.
 *
 * Object types (String, Date) get written out as their "toString" values, and
 * are written out as node elements (ie. <name>JRLogo</name>). 
 * If an object type is null, then there is no element written out. For,
 * example, in the xml generated above referenceUri was null so you 
 * do not see it included in the output xml.
 *
 */

/**
 *
 * The classes that inherit from ResourceBean are used to go back and forth
 * between the java objects they represent and XML. We are currently using
 * the Castor library in order to marshal and unmarshal between java and 
 * XML. 
 *
 * One thing to note is that Castor handles primitive fields such as int and
 * boolean as XML attributes of the top level XML tag for the particular 
 * object. Castor automatically gives default values for these primitives.
 * So, if something like boolean hasData() has not been explicitly set, Castor
 * will give it a default value of "false". For int the default value is 
 * zero.
 *
 * Furthermore, if a String value such as String setDescrption() does not
 * have a value (is null) it will not be included in the output (unmarshal)
 * XML. So, this is different than how primitives are dealt with.
 *
 *
 */

public abstract class ResourceBean {
	protected final Logger log = LogManager.getLogger(getClass());

	//TODO use constants from EncryptionEngine instead
	public static final String ENCRYPTION_PREFIX = "ENC<";
	public static final String ENCRYPTION_SUFFIX = ">";

	protected static final Cipherer importExportCipher =
			(Cipherer) StaticApplicationContext.getApplicationContext().getBean("importExportCipher");

	/*
	 * The following come from the Resource interface 
	 */
	private String name;
	private String label;
	private String description;
	private String folder;
	private Date creationDate;
	private Date updateDate;
	private int version;
	private RepositoryObjectPermissionBean[] permissions;

	/*
		 *  Export specific data
		 */
	private boolean exportedWithPermissions;

	/**
	 * This method is called by exportHandler (com.jaspersoft.jasperserver.export.modules.repository.ResourceExporter#handleResource)
	 * to convert the exported repository resource res to a ResourceBean (exported DAO).
	 *
	 * @param res   - exported resource
	 * @param exportHandler  - the caller - ResourceExporter
	 */
	public final void copyFrom(Resource res, ResourceExportHandler exportHandler) {
		setName(res.getName());
		setLabel(res.getLabel());
		setDescription(res.getDescription());
		setFolder(res.getParentFolder());
		setCreationDate(res.getCreationDate());
		setUpdateDate(res.getUpdateDate());
		setVersion(res.getVersion());

		additionalCopyFrom(res, exportHandler);
	}

	/**
	 * customization addendum to copyFrom.
	 * <p>
	 * (from copyFrom) This method is called by exportHandler (com.jaspersoft.jasperserver.export.modules.repository.ResourceExporter#handleResource)
	 * to convert the exported repository resource res to a ResourceBean (exported DAO).
	 * </p>
	 * @param res - exported resource
	 * @param exportHandler - the caller - ResourceExporter
	 */
	protected abstract void additionalCopyFrom(Resource res, ResourceExportHandler exportHandler);

	/**
	 * This method is called by importHandler (com.jaspersoft.jasperserver.export.modules.repository.ResourceImporter#handleResource)
	 * to convert the imported resource res into the repository DAO.
	 *
	 * @param res  - imported resource
	 * @param importHandler  - the caller - ResourceImporter
	 */
	public final void copyTo(Resource res, ResourceImportHandler importHandler) {
		res.setName(getName());
		res.setLabel(getLabel());
		res.setDescription(getDescription());
		res.setParentFolder(getFolder());
		res.setUpdateDate(getUpdateDate());

		additionalCopyTo(res, importHandler);
	}

	/**
	 * customization addendum to copyTo.
	 * <p>
	 * (from copyTo) This method is called by importHandler (com.jaspersoft.jasperserver.export.modules.repository.ResourceImporter#handleResource)
	 * to convert the imported resource res into the repository DAO.
	 * </p>
	 * @param res - imported resource
	 * @param importHandler  - the caller - ResourceImporter
	 */
	protected abstract void additionalCopyTo(Resource res, ResourceImportHandler importHandler);

	protected final ResourceReferenceBean[] handleReferences(List references, ResourceExportHandler exportHandler) {
		ResourceReferenceBean[] refBeans;
		if (references == null || references.isEmpty()) {
			refBeans = null;
		} else {
			refBeans = new ResourceReferenceBean[references.size()];
			int c = 0;
			for (Iterator it = references.iterator(); it.hasNext(); ++c) {
				ResourceReference controlRef = (ResourceReference) it.next();
				refBeans[c] = exportHandler.handleReference(controlRef);
			}
		}
		return refBeans;
	}

	protected List<ResourceReference> handleReferences(ResourceReferenceBean[] beanReferences,
													   ResourceImportHandler importHandler) {
		List<ResourceReference> references;
		if (beanReferences == null) {
			references = null;
		} else {
			references = new ArrayList<ResourceReference>(beanReferences.length);
			for (ResourceReferenceBean beanReference : beanReferences) {
				ResourceReference reference = importHandler.handleReference(beanReference);
				references.add(reference);
			}
		}
		return references;
	}

	/*
		 * getters and setters
		 */

	public String getName()	{
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folderUri) {
		this.folder = folderUri;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public RepositoryObjectPermissionBean[] getPermissions() {
		return permissions;
	}

	public void setPermissions(RepositoryObjectPermissionBean[] permissions) {
		this.permissions = permissions;
	}

	public boolean isExportedWithPermissions() {
		return exportedWithPermissions;
	}

	public void setExportedWithPermissions(boolean exportedWithPermissions) {
		this.exportedWithPermissions = exportedWithPermissions;
	}
}
