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

package com.jaspersoft.jasperserver.export.io;

import java.util.Properties;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.export.Parameters;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: FileSystemInputFactory.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class FileSystemInputFactory implements ImportInputFactory, FileSystemInputManager {

	private String inputDirParameter;
	private PathProcessorFactory pathProcessorFactory;
	private String propertyPathProcessorId;

	public boolean matches(Parameters parameters) {
		return parameters.hasParameter(inputDirParameter);
	}
	
	public ImportInput createInput(Parameters parameters) {
		String rootDir = parameters.getParameterValue(inputDirParameter);
		if (rootDir == null) {
			throw new JSException("jsexception.no.import.folder.specified");
		}
		
		String processorId = pathProcessorFactory.getDefaultInputProcessor();
		PathProcessor processor = pathProcessorFactory.getProcessor(processorId);
		return new FileSystemInput(rootDir, processor, this);
	}

	public void updateInputProperties(FileSystemInput input,
			Properties properties) {
		String pathProcessorId = properties.getProperty(propertyPathProcessorId);
		if (pathProcessorId != null && !pathProcessorId.equals(pathProcessorFactory.getDefaultInputProcessor())) {
			PathProcessor processor = pathProcessorFactory.getProcessor(pathProcessorId);
			input.setPathProcessor(processor);
		}
	}

	public String getInputDirParameter() {
		return inputDirParameter;
	}

	public void setInputDirParameter(String inputDirParameter) {
		this.inputDirParameter = inputDirParameter;
	}

	public String getPropertyPathProcessorId() {
		return propertyPathProcessorId;
	}

	public void setPropertyPathProcessorId(String propertyPathProcessorId) {
		this.propertyPathProcessorId = propertyPathProcessorId;
	}

	public PathProcessorFactory getPathProcessorFactory() {
		return pathProcessorFactory;
	}

	public void setPathProcessorFactory(PathProcessorFactory pathProcessorFactory) {
		this.pathProcessorFactory = pathProcessorFactory;
	}

}
