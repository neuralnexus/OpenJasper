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

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.export.modules.ModuleRegister;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseExporterImporter.java 47331 2014-07-18 09:13:06Z kklein $
 */

public class BaseExporterImporter {
	
	protected static final CommandOut commandOut = CommandOut.getInstance();
	
	private String indexFilename;
	private String indexRootElementName;
	private String indexModuleElementName;
	private String indexModuleIdAttributeName;
	private ModuleRegister moduleRegister;
	private CharacterEncodingProvider encodingProvider;
	private String propertyElementName;
	private String propertyNameAttribute;
	private String propertyValueAttribute;
    private String jsVersion;
    public final String VERSION_ATTR = "jsVersion";

    public String getIndexRootElementName() {
		return indexRootElementName;
	}

	public void setIndexRootElementName(String indexRootElement) {
		this.indexRootElementName = indexRootElement;
	}

	public String getIndexModuleElementName() {
		return indexModuleElementName;
	}

	public void setIndexModuleElementName(String indexModuleElementName) {
		this.indexModuleElementName = indexModuleElementName;
	}

	public String getIndexModuleIdAttributeName() {
		return indexModuleIdAttributeName;
	}

	public void setIndexModuleIdAttributeName(String indexModuleIdAttributeName) {
		this.indexModuleIdAttributeName = indexModuleIdAttributeName;
	}

	public ModuleRegister getModuleRegister() {
		return moduleRegister;
	}

	public void setModuleRegister(ModuleRegister moduleRegister) {
		this.moduleRegister = moduleRegister;
	}

	public CharacterEncodingProvider getEncodingProvider() {
		return encodingProvider;
	}
	
	public String getCharacterEncoding() {
		return encodingProvider.getCharacterEncoding();
	}

	public void setEncodingProvider(CharacterEncodingProvider encodingProvider) {
		this.encodingProvider = encodingProvider;
	}

	public String getIndexFilename() {
		return indexFilename;
	}

	public void setIndexFilename(String indexFilename) {
		this.indexFilename = indexFilename;
	}

	public String getPropertyElementName() {
		return propertyElementName;
	}

	public void setPropertyElementName(String propertyElementName) {
		this.propertyElementName = propertyElementName;
	}

	public String getPropertyNameAttribute() {
		return propertyNameAttribute;
	}

	public void setPropertyNameAttribute(String propertyNameAttribute) {
		this.propertyNameAttribute = propertyNameAttribute;
	}

	public String getPropertyValueAttribute() {
		return propertyValueAttribute;
	}

	public void setPropertyValueAttribute(String propertyValueAttribute) {
		this.propertyValueAttribute = propertyValueAttribute;
	}

    public String getJsVersion() {
        return jsVersion;
    }

    public void setJsVersion(String jsVersion) {
        this.jsVersion = jsVersion;
    }
}
