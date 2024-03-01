/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.api.common.crypto.Hexer;
import com.jaspersoft.jasperserver.api.common.util.ExportRunMonitor;
import com.jaspersoft.jasperserver.api.common.util.spring.StaticApplicationContext;
import com.jaspersoft.jasperserver.dto.common.WarningDescriptor;
import com.jaspersoft.jasperserver.export.io.ExportOutput;
import com.jaspersoft.jasperserver.export.modules.*;
import com.jaspersoft.jasperserver.export.modules.common.CipherLookup;
import com.jaspersoft.jasperserver.export.modules.common.ExportImportWarningCode;
import com.jaspersoft.jasperserver.export.service.ImportExportService;
import com.jaspersoft.jasperserver.export.service.impl.ImportExportServiceImpl;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;

import static com.jaspersoft.jasperserver.crypto.KeystoreManager.KS_TYPE;
import static com.jaspersoft.jasperserver.export.util.CommandUtils.requestConfirmedPassword;
import static java.lang.Integer.parseInt;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */

public class ExporterImpl extends BaseExporterImporter implements Exporter {

	private static final Log log = LogFactory.getLog(ExporterImpl.class);

	public static final String KEY_ALIAS_UUID_TITLE = "Key Alias (UUID): ";
	public static final String SECRET_KEY_TITLE     = "      Secret Key: ";

	protected class ModuleContextImpl implements ExporterModuleContext {
		
		private final String moduleId;
		
		public ModuleContextImpl(final String moduleId) {
			this.moduleId = moduleId;
		}

		public String getCharacterEncoding() {
			return ExporterImpl.this.getCharacterEncoding();
		}

		public ExportTask getExportTask() {
			return task;
		}

		public Element getModuleIndexElement() {
			return getIndexModuleElement(moduleId);
		}

		public ModuleRegister getModuleRegister() {
			return ExporterImpl.this.getModuleRegister();
		}
	}
	
	private String xmlVersion;
	
	protected ExportTask task;
	protected ExportOutput output;
	
	private Element indexRootElement;
	private Map moduleIndexElements;


	public String getXmlVersion() {
		return xmlVersion;
	}

	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	public Element getIndexRootElement() {
		return indexRootElement;
	}

	protected Element getIndexModuleElement(String moduleId) {
		return (Element) moduleIndexElements.get(moduleId);
	}
	
	public ExportTask getExportTask() {
		return task;
	}
	
	public void setTask(ExportTask task) {
		this.task = task;
		this.output = task == null ? null :  task.getOutput();
	}
	
	public ExportOutput getOutput() {
		return output;
	}

	public void performExport() {
		try {
			output.open();
			boolean close = true;
			try {
				process(new MapAttributes());
				
				close = false;
				output.close();
			} finally {
				BaseExporterImporter.setTenant(null);
				if (close) {
					try {
						output.close();
					} catch (IOException ioe) {
						log.info("Error while closing output", ioe);
					}
				}
			}		
		} catch (Exception e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected void process(Attributes contextAttributes) {
		Document indexDocument = DocumentHelper.createDocument();
		indexRootElement = indexDocument.addElement(getIndexRootElementName());
		ExportRunMonitor.start();
		try {
			invokeModules(contextAttributes);
		} finally {
			ExportRunMonitor.stop();
		}
		setOutputProperties();
		writeDocument(indexDocument, getIndexFilename());
		writeExportWarnings();
	}

	protected void setOutputProperties() {
		Properties properties = output.getOutputProperties();

		String organizationId = getExportTask().getParameters().getParameterValue(ImportExportServiceImpl.ORGANIZATION);
		if (StringUtils.isBlank(organizationId)) {
			properties.put(ImportExportService.ROOT_TENANT_ID, getAuthenticatedTenantId());
		} else {
			properties.put(ImportExportService.ROOT_TENANT_ID, organizationId);
		}

		for (Enumeration it = properties.propertyNames(); it.hasMoreElements();) {
			String property = (String) it.nextElement();
			String value = properties.getProperty(property);
			setOutputProperty(property, value);
		}
        String version = super.getJsVersion();
        if (version != null && !version.equals("")) {
            setOutputProperty(VERSION_ATTR,version);
        }
        PlainCipher plainCipher = CipherLookup.INSTANCE.get();
        setOutputProperty(ENCRYPTION_ATTR, plainCipher.encode(ENCRYPTION_TEXT_VALUE));
    }

	protected void setOutputProperty(String property, String value) {
		Element propElement = indexRootElement.addElement(getPropertyElementName());
		propElement.addAttribute(getPropertyNameAttribute(), property);
		if (value != null) {
			propElement.addAttribute(getPropertyValueAttribute(), value);
		}
	}

	protected void invokeModules(Attributes contextAttributes) {
		List modules = getModuleRegister().getExporterModules();
		if (this.task.getAttributes() == null) {
			this.task.setAttributes(contextAttributes);
		}
		contextAttributes.setAttribute("appContext", StaticApplicationContext.getApplicationContext());
		contextAttributes.setAttribute("targetJsVersion",super.getJsVersion());

		final EncryptionParams params = new EncryptionParams(this.task.getParameters());

		SecretKey key = null;
		PlainCipher cipher;
		if (params.hasGenKey()) {
			try {
				key = generateSecret(params);
				cipher = getCipherFactory().fork(key).getObject();

			} catch (Exception e) {
				commandOut.error("Failed to initialize cipher for generated key.");
				throw new RuntimeException(e);
			}

		} else {
			try {
				cipher = getCipher(params, contextAttributes, new Properties());
			}  catch (Exception e) {
				commandOut.error("Failed to retrieve cipher for associated key.");
				throw new RuntimeException(e);
			}
		}

		CipherLookup.INSTANCE.set(cipher);
		setOutputProperty(ENCRYPTION_KEYALIAS_ATTR, cipher.getKeyUuid());

		moduleIndexElements = new HashMap();
		for (Iterator it = modules.iterator(); it.hasNext();) {
            if (Thread.interrupted()){
                throw new RuntimeException("Cancelled");
            }
			ExporterModule module = (ExporterModule) it.next();
			ModuleContextImpl moduleContext = new ModuleContextImpl(module.getId());

			module.init(moduleContext);
			if (module.toProcess()) {
				commandOut.debug("Module " + module.getId() + " processing");
				
				createModuleElement(module);
				try {
					module.process();
				} catch (NullPointerException e) {
					commandOut.error(e.getMessage(), e);
					throw e; //handling everything as before
				}
			}			
		}
		printSecret(cipher, key, params);
	}

	private void printSecret(PlainCipher cipher, SecretKey key, EncryptionParams params) {

		if (params.getDestKeyStore().isPresent()) {
			String destStorePass = null;
			if (params.getDestStorePasswd().isPresent()) {
				destStorePass = params.getDestStorePasswd().get();

				if (destStorePass.isEmpty()) {
					destStorePass = requestConfirmedPassword(
							"Please enter keystore password: ",
							"Please confirm keystore password: ");
				}
			} else {
				if (defaultKeystorePasswdProvider != null) {
					destStorePass = defaultKeystorePasswdProvider.getDefaultPassword();
				}
				if (destStorePass == null || destStorePass.isEmpty()) {
					throw new RuntimeException("Error: Empty store password. Missing '--deststorepass' option");
				}
			}

			String destKeyPasswd = destStorePass;
			if (params.getDestKeyPasswd().isPresent()) {
				destKeyPasswd = params.getDestKeyPasswd().get();

				if (destKeyPasswd.isEmpty()) {
					destKeyPasswd = requestConfirmedPassword(
							"Please enter key password: ",
							"Please confirm key password: ");
				}
			}
			if(destKeyPasswd == null || destKeyPasswd.isEmpty()) {
				throw new RuntimeException("Error: Empty key password");
			}
//			String keyAlias = cipher.getKeyUuid();
			final Key cipherKey = cipher.getKey();

			final File storeFile = new File(params.getDestKeyStore().get());
			final KeyStore store;
			if (storeFile.exists()) {
				try (FileInputStream ksFis = new FileInputStream(storeFile)) {
					store = KeyStore.getInstance(KS_TYPE);
					store.load(ksFis, destStorePass.toCharArray());
				} catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
					throw new RuntimeException(e);
				}
			} else {
				try {
					store = KeyStore.getInstance(KS_TYPE);
					store.load(null);
				} catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
					throw new RuntimeException(e);
				}
			}

			try (FileOutputStream ksFos = new FileOutputStream(storeFile)) {
				final char[] keyPassword = destKeyPasswd.toCharArray();

				store.setKeyEntry(cipher.getKeyUuid(), cipherKey, keyPassword, null);
				System.out.println(KEY_ALIAS_UUID_TITLE + cipher.getKeyUuid());

				if(params.getKeyAlias().isPresent() && isNotBlank(params.getKeyAlias().get())) {
					store.setKeyEntry(params.getKeyAlias().get(), cipherKey, keyPassword, null);
				}

				store.store(ksFos, destStorePass.toCharArray());
			} catch (KeyStoreException | NoSuchAlgorithmException | IOException | CertificateException e) {
				throw new RuntimeException(e);
			}
		}

		if(params.hasGenKey() && key != null)  {
			System.out.println(SECRET_KEY_TITLE + Hexer.stringify(key.getEncoded()));
			System.out.println(KEY_ALIAS_UUID_TITLE + cipher.getKeyUuid());
		}
	}

	private SecretKey generateSecret(EncryptionParams params) {
		SecretKey key;
		final String defaultKeySize = String.valueOf(cipher.getKeySize());

		if (params.getKeyAlg().isPresent() && params.getKeySize().isPresent()) {
			key = cipher.generateSecret(
					params.getKeyAlg().orElse(cipher.getKeyAlgorithm()),
					parseInt(params.getKeySize().orElse(defaultKeySize)));

		} else if (params.getKeyAlg().isPresent() ){
			key = cipher.generateSecret(
					params.getKeyAlg().orElse(cipher.getKeyAlgorithm()));

		} else if (params.getKeySize().isPresent() ){
			key = cipher.generateSecret(
					parseInt(params.getKeySize().orElse(defaultKeySize)));

		} else {
			key = cipher.generateSecret();
		}
		return key;
	}

	protected void createModuleElement(ExporterModule module) {		
		Element moduleElement = indexRootElement.addElement(getIndexModuleElementName());
		moduleElement.addAttribute(getIndexModuleIdAttributeName(), module.getId());
		moduleIndexElements.put(module.getId(), moduleElement);
	}

	private void writeDocument(Document document, String filename) {
		OutputStream output = getDocumentOutput(filename);
		boolean closeOutput = true;
		try {
			OutputFormat format = new OutputFormat();
			format.setEncoding(getCharacterEncoding());
			XMLWriter writer = new XMLWriter(output, format);
			writer.write(document);

			closeOutput = false;
			output.close();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeOutput) {
				try {
					output.close();
				} catch (IOException e) {
					log.error("Error while closing output", e);
				}
			}
		}
	}

	private OutputStream getDocumentOutput(String filename) {
		try {
			return output.getFileOutputStream(filename);
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		}
	}

	private void writeExportWarnings() {
		if (task.getWarnings().isEmpty()) return;

		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement(getBrokenDependenceRootElementName());
		for (WarningDescriptor warningDescriptor : task.getWarnings()) {
			if (warningDescriptor.getCode().equals(ExportImportWarningCode.EXPORT_BROKEN_DEPENDENCY.toString())) {
				for (String str : warningDescriptor.getParameters()) {
					Element element = rootElement.addElement(getResourceElementName());
					element.addText(str);
				}
			}
		}
		writeDocument(document, getBrokenDependenceFilename());
	}
}
