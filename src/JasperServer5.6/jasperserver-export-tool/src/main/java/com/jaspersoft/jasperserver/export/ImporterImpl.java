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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.export.io.ImportInput;
import com.jaspersoft.jasperserver.export.modules.Attributes;
import com.jaspersoft.jasperserver.export.modules.ImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.MapAttributes;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ImporterImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ImporterImpl extends BaseExporterImporter implements Importer {
	
	private static final Log log = LogFactory.getLog(ImporterImpl.class);

	protected class ModuleContextImpl implements ImporterModuleContext {
		
		private final Element moduleElement;
		private final Attributes attributes;

		public ModuleContextImpl(final Element moduleElement, 
				final Attributes attributes) {
			this.moduleElement = moduleElement;
			this.attributes = attributes;
		}

		public String getCharacterEncoding() {
			return ImporterImpl.this.getCharacterEncoding();
		}

		public ImportTask getImportTask() {
			return task;
		}

		public Element getModuleIndexElement() {
			return moduleElement;
		}

		public Attributes getAttributes() {
			return attributes;
		}
		
	}
	
	protected ImportTask task;
	protected ImportInput input;

	public void setTask(ImportTask task) {
		this.task = task;
		this.input = task == null ? null : task.getInput();
	}

	public void performImport() {
		try {
			input.open();
			boolean close = true;
			try {
				process();
				
				close = false;
				input.close();
			} finally {
				if (close) {
					try {
						input.close();
					} catch (IOException ioe) {
						log.info("Error while closing input", ioe);
					}
				}
			}			
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void process() {
		Document indexDocument = readIndexDocument();
		Element indexRoot = indexDocument.getRootElement();
		
		Properties properties = new Properties();
		for (Iterator it = indexRoot.elementIterator(getPropertyElementName()); it.hasNext(); ) {
			Element propElement = (Element) it.next();
			String propKey = propElement.attribute(getPropertyNameAttribute()).getValue();
			Attribute valueAttr = propElement.attribute(getPropertyValueAttribute());
			String value = valueAttr == null ? null : valueAttr.getValue();
			properties.setProperty(propKey, value);
		}
		input.propertiesRead(properties);
		
		Attributes contextAttributes = createContextAttributes();
        contextAttributes.setAttribute("sourceJsVersion",properties.getProperty(VERSION_ATTR));
        contextAttributes.setAttribute("targetJsVersion",super.getJsVersion());

		for (Iterator it = indexRoot.elementIterator(getIndexModuleElementName()); it.hasNext(); ) {
            if (Thread.interrupted()){
                throw new RuntimeException("Cancelled");
            }
			Element moduleElement = (Element) it.next();
			String moduleId = moduleElement.attribute(getIndexModuleIdAttributeName()).getValue();
			ImporterModule module = getModuleRegister().getImporterModule(moduleId);
			if (module == null) {
				throw new JSException("jsexception.import.module.not.found", new Object[] {moduleId});
			}

			commandOut.debug("Invoking module " + module);

            contextAttributes.setAttribute("appContext",this.task.getApplicationContext());
			ModuleContextImpl moduleContext = new ModuleContextImpl(moduleElement,
					contextAttributes);
			module.init(moduleContext);

            List<String> messages = new ArrayList<String>();
            ImportRunMonitor.start();
            try{
			    List<String> moduleMessages = module.process();
                if(moduleMessages != null) {
                    messages.addAll(moduleMessages);
                }
            } finally {
                ImportRunMonitor.stop();
                for(String message: messages) {
                    commandOut.info(message);
                }
            }
		}
	}

	protected Attributes createContextAttributes() {
		Attributes contextAttributes = new MapAttributes();
		return contextAttributes;
	}

	protected Document readIndexDocument() {
		InputStream indexInput = getIndexInput();
		boolean close = true;
		try {
			SAXReader reader = new SAXReader();
			reader.setEncoding(getCharacterEncoding());
			Document document = reader.read(indexInput);
			
			close = false;
			indexInput.close();
			
			return document;
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} catch (DocumentException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (close) {
				try {
					indexInput.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}		
	}

	protected InputStream getIndexInput() {
		try {
			return input.getFileInputStream(getIndexFilename());
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

}
