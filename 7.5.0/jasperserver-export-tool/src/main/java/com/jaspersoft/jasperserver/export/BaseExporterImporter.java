/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import com.jaspersoft.jasperserver.api.security.encryption.PlainCipher;
import com.jaspersoft.jasperserver.api.common.crypto.CipherFactory;
import com.jaspersoft.jasperserver.api.common.util.CharacterEncodingProvider;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.TenantQualified;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.crypto.KeystoreManager;
import com.jaspersoft.jasperserver.crypto.KeystoreProperties;
import com.jaspersoft.jasperserver.export.modules.Attributes;
import com.jaspersoft.jasperserver.export.modules.ModuleRegister;
import com.jaspersoft.jasperserver.export.modules.auth.AuthorityModuleConfiguration;
import com.jaspersoft.jasperserver.export.util.CommandOut;
import com.jaspersoft.jasperserver.export.util.EncryptionParams;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.UnrecoverableKeyException;
import java.util.*;

import static com.jaspersoft.jasperserver.api.common.crypto.Hexer.parse;
import static com.jaspersoft.jasperserver.export.util.CommandUtils.requestConfirmedPassword;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */

public class BaseExporterImporter {

	protected static final CommandOut commandOut = CommandOut.getInstance();

	private static final ThreadLocal<Tenant> tenant = new ThreadLocal<Tenant>();

	private String indexFilename;
	private String indexRootElementName;
	private String indexModuleElementName;
	private String indexModuleIdAttributeName;
	private String brokenDependenceFilename;
	private String brokenDependenceRootElementName;
	private String resourceElementName;
	private ModuleRegister moduleRegister;
	private CharacterEncodingProvider encodingProvider;
	private String propertyElementName;
	private String propertyNameAttribute;
	private String propertyValueAttribute;
	protected PlainCipher cipher;
	protected CipherFactory cipherFactory;
	protected KeystoreManager keystoreManager;
	protected DefaultKeystorePasswdProvider defaultKeystorePasswdProvider;
    private String jsVersion;
    public final String VERSION_ATTR = "jsVersion";
	public final String ENCRYPTION_ATTR = "encrypted";
	public final String ENCRYPTION_TEXT_VALUE = "true";
	public final String ENCRYPTION_KEYALIAS_ATTR = "keyalias";
	public static final String IMPORT_FAILED_DECODE_ERROR_CODE = "import.decode.failed";

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

	public void setBrokenDependenceFilename(String brokenDependenceFilename) {
		this.brokenDependenceFilename = brokenDependenceFilename;
	}

	public String getBrokenDependenceFilename() {
		return brokenDependenceFilename;
	}

	public void setBrokenDependenceRootElementName(String brokenDependenceRootElementName) {
		this.brokenDependenceRootElementName = brokenDependenceRootElementName;
	}

	public String getBrokenDependenceRootElementName() {
		return brokenDependenceRootElementName;
	}

	public void setResourceElementName(String resourceElementName) {
		this.resourceElementName = resourceElementName;
	}

	public String getResourceElementName() {
		return resourceElementName;
	}

	protected static String getAuthenticatedTenantId() {
		String result = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			if (auth.getPrincipal() instanceof TenantQualified) {
				result = ((TenantQualified) auth.getPrincipal()).getTenantId();
			}
		}

		if (result == null) {
			return TenantService.ORGANIZATIONS;
		}

		return result;
	}

	public static Tenant getTenant() {
		return tenant.get();
	}

	public static void setTenant(Tenant t) {
		tenant.set(t);
	}

	public PlainCipher getCipher() {
		return cipher;
	}

	public void setCipher(PlainCipher cipher) {
		this.cipher = cipher;
	}

	public CipherFactory getCipherFactory() {
		return cipherFactory;
	}

	public void setCipherFactory(CipherFactory cipherFactory) {
		this.cipherFactory = cipherFactory;
	}

	public void setKeystoreManager(KeystoreManager keystoreManager) {
		this.keystoreManager = keystoreManager;
	}

	public void setDefaultKeystorePasswdProvider(DefaultKeystorePasswdProvider defaultKeystorePasswdProvider) {
		this.defaultKeystorePasswdProvider = defaultKeystorePasswdProvider;
	}

	/**
	 * Returns a map, which links old tenant id to new unique tenant id
	 */
	protected static Map<String, String> getNewGeneratedTenantIds(Attributes contextAttributes) {
		final String ATTRIBUTE_NAME = "newUniqueTenantIdsMap";
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) contextAttributes.getAttribute(ATTRIBUTE_NAME);
		if (map == null) {
			map = new HashMap<String, String>();
			contextAttributes.setAttribute(ATTRIBUTE_NAME, map);
		}

		return map;
	}

	protected PlainCipher getCipher(final EncryptionParams params, final Attributes contextAttributes, final Properties properties) {
		Optional<String> secretKeyParam = params.getSecretKey();
		Optional<String> keyAliasParam = params.getKeyAlias();
		Optional<String> keyPasswdParam = params.getKeyPasswd();

		final String sourceVersion = (String) contextAttributes.getAttribute("sourceJsVersion");

		CipherFactory cipherFactory = getCipherFactory();
		final CipherFactory fallbackFactory = cipherFactory.getFallbackFactory();

		// Fallback to older decipher for 7.2 to ease upgrade
		Scanner vParser;
		if (fallbackFactory != null && sourceVersion != null
				&& (vParser = new Scanner(sourceVersion).useDelimiter("\\.")).hasNextInt()) {

			int major = vParser.nextInt(), minor = vParser.hasNextInt() ? vParser.nextInt() : 0 ;

			if (major < 7 || (major == 7 && minor <= 2)) {
				cipherFactory = fallbackFactory;
			}
		}
		String uuidAlias = properties.getProperty(ENCRYPTION_KEYALIAS_ATTR);

		PlainCipher cipher;

		try {
			this.keystoreManager.reload();

			if (secretKeyParam.isPresent() && !secretKeyParam.get().isEmpty()) {
				byte[] bytes = parse(secretKeyParam.get());

				SecretKey key = new SecretKeySpec(bytes, params.getKeyAlg().orElse(this.cipher.getKeyAlgorithm()));
				cipher = cipherFactory
						.fork(key, params.getKeyUuid().orElse(null))
						.getObject();

			} else if (keyAliasParam.isPresent() && !keyAliasParam.get().isEmpty()) {
				try {
					KeystoreProperties keystoreProperties = this.keystoreManager.getKeystoreProperties(keyAliasParam.get());
					String importKeyAlias = keystoreProperties.getKeyAlis() != null ? keystoreProperties.getKeyAlis() : keyAliasParam.get();
					String importKeyPasswd = resolvePassword(keyPasswdParam, keystoreProperties.getKeyPasswd());

					final CipherFactory factory = cipherFactory.fork(importKeyAlias, importKeyPasswd);
					if (keystoreProperties.getKeyUuid() != null) {
						factory.setKeyUuid(keystoreProperties.getKeyUuid());
					}
					cipher = factory.getObject();

				} catch (Exception e) {
					cipher = cipherFactory
							.fork(keyAliasParam.get(), resolvePassword(keyPasswdParam, null))
							.getObject();
				}

			} else if (uuidAlias != null && !uuidAlias.isEmpty()
					&& !uuidAlias.equalsIgnoreCase(this.cipher.getKeyUuid())
					&& this.keystoreManager.containsAlias(uuidAlias)) {
				KeystoreProperties keystoreProperties = this.keystoreManager.getKeystoreProperties(uuidAlias);
				String importKeyAlias = keystoreProperties.getKeyAlis() != null ? keystoreProperties.getKeyAlis() : uuidAlias;
				String importKeyPasswd = resolvePassword(keyPasswdParam, keystoreProperties.getKeyPasswd());

				cipher = cipherFactory
						.fork(importKeyAlias, importKeyPasswd != null ? importKeyPasswd : "")
						.getObject();

			} else if (cipherFactory == fallbackFactory) {
				cipher = cipherFactory.getObject();

			} else {
				cipher = this.cipher;
			}

		} catch (Exception e) {
			if(e.getCause().getCause() instanceof UnrecoverableKeyException && e.getMessage().contains("password maybe invalid")) {
				commandOut.error("Unable to retrieve key, password may be invalid");
			}
			else {
				commandOut.error("Failed to initialize cipher. Using default import-export cipher.", e);
			}
			throw new RuntimeException(e.getMessage(), e);
		}

		return cipher;
	}

	private String resolvePassword(Optional<String> keyPasswdParam, String defaultPasswd) {
		String keyPasswd;
		if (keyPasswdParam.isPresent()) {
			keyPasswd = keyPasswdParam.get();
			if (keyPasswd.isEmpty()) {
				keyPasswd = requestConfirmedPassword(
						"Please enter key password: ",
						"Please confirm key password: ");
			}
			if (keyPasswd == null) {
				keyPasswd = "";
			}

		} else if (defaultPasswd != null) {
			keyPasswd = defaultPasswd;

		} else {
			if (defaultKeystorePasswdProvider != null) {
				keyPasswd = defaultKeystorePasswdProvider.getDefaultPassword();
			} else {
				keyPasswd = requestConfirmedPassword(
						"Please enter key password: ",
						"Please confirm key password: ");
			}
		}
		return keyPasswd;
	}
}
