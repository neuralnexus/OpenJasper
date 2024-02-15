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

package com.jaspersoft.jasperserver.export.modules;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.export.Parameters;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.io.ObjectSerializer;
import com.jaspersoft.jasperserver.export.util.CommandOut;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: BaseImporterModule.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class BaseImporterModule implements ImporterModule {

	private static final Log log = LogFactory.getLog(BaseImporterModule.class);
	
	protected static final CommandOut commandOut = CommandOut.getInstance();
	
	private String id;
	
	protected ImporterModuleContext importContext;
	protected Parameters params;
	protected ExecutionContext executionContext;
	protected ImportInput input;
	protected Element indexElement;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void init(ImporterModuleContext moduleContext) {
		this.importContext = moduleContext;
		this.params = moduleContext.getImportTask().getParameters();
		this.executionContext = moduleContext.getImportTask().getExecutionContext();
		this.input = moduleContext.getImportTask().getInput();
		this.indexElement = moduleContext.getModuleIndexElement();
	}
	
	protected String getParameterValue(String name) {
		return params.getParameterValue(name);
	}
	
	protected boolean hasParameter(String name) {
		return params.hasParameter(name);
	}

    protected String getSourceJsVersion() {
        return (String) importContext.getAttributes().getAttribute("sourceJsVersion");
    }

    protected String getTargetJsVersion() {
        return (String) importContext.getAttributes().getAttribute("targetJsVersion");
    }
    
    protected boolean isUpgrade() {
        String sourceJsVersion = getSourceJsVersion();
        String targetJsVersion = getTargetJsVersion();

        // upgrade false if target version can not be detected (just in case)
        if (targetJsVersion == null) {
            return false;
        }

        // upgrade true if no jsVersion in input archive is specified
        if (sourceJsVersion == null) {
            return true;
        }

        // upgrade true if upgrading CE to PRO. Multitenancy repo structure is needed
        if (sourceJsVersion.contains("CE") && targetJsVersion.contains("PRO")) {
            return true;
        }
        
        // return false
        return false;
    }

	protected final Object deserialize(String parentPath, String fileName, ObjectSerializer serializer) {
		InputStream in = getFileInput(parentPath, fileName);
		boolean closeIn = true;
		try {
			Object object = serializer.read(in, importContext);
			
			closeIn = false;
			in.close();

			return object;
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeIn) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	protected InputStream getFileInput(String parentPath, String fileName) {
		try {
			return input.getFileInputStream(parentPath, fileName);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected Attributes getContextAttributes() {
		return importContext.getAttributes();
	}

	public ExecutionContext getExecutionContext() {
		return executionContext;
	}
}
