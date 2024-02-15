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

package com.jaspersoft.jasperserver.export.modules.repository;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ResourceReference;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.FolderImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.util.DataContainerStreamUtil;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Tenant;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.TenantService;
import com.jaspersoft.jasperserver.export.modules.BaseImporterModule;
import com.jaspersoft.jasperserver.export.modules.ImporterModuleContext;
import com.jaspersoft.jasperserver.export.modules.common.TenantQualifiedName;
import com.jaspersoft.jasperserver.export.modules.repository.beans.FolderBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.PermissionRecipient;
import com.jaspersoft.jasperserver.export.modules.repository.beans.RepositoryObjectPermissionBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceBean;
import com.jaspersoft.jasperserver.export.modules.repository.beans.ResourceReferenceBean;
import com.jaspersoft.jasperserver.export.util.PathUtils;
import com.jaspersoft.jasperserver.export.util.PathUtils.SplittedPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.SpringSecurityException;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ResourceImporter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ResourceImporter extends BaseImporterModule implements ResourceImportHandler, InitializingBean {
	
	public final static String ATTRIBUTE_UPDATE_RESOURCES = "updateResources";
	
	private final static Log log = LogFactory.getLog(ResourceImporter.class);
	
	private ResourceModuleConfiguration configuration;
    private TenantService tenantService;
	private String prependPathArg;
	private String updateArg;
	private String includeSettingsArg;
	private String skipThemesArgument;
    private List<String> rootSubTenantFolderUris;
    private Pattern orgPattern = Pattern.compile("((/" + TenantService.ORGANIZATIONS + "/[^/]+)*)");
    private Pattern themesPattern = Pattern.compile(".*/themes/.*");

	protected RepositoryService repository;
	protected String prependPath;
	private boolean update;

	protected Set importedURIs;
	private LinkedList folderQueue;
	private LinkedList resourceQueue;
	private Map roles;
	private Map users;

	public void afterPropertiesSet() {
		this.repository = configuration.getRepository();
	}

	public void init(ImporterModuleContext moduleContext) {
		super.init(moduleContext);
		configuration.setApplicationContext((ApplicationContext) moduleContext.getAttributes().getAttribute("appContext"));
		prependPath = getPrependPath();
		update = getUpdateFlag();
        //Retrieve all root subtenants
        List<Tenant> allRootSubTenantList = tenantService.getAllSubTenantList(executionContext, TenantService.ORGANIZATIONS);
        rootSubTenantFolderUris = new ArrayList<String>();
        for (Tenant tenant : allRootSubTenantList) {
            rootSubTenantFolderUris.add(tenant.getTenantFolderUri());
        }
	}

	protected String getPrependPath() {
		String path = getParameterValue(getPrependPathArg());
		if (path != null) {
			path = PathUtils.normalizePath(path);
			if (path.length() == 0 || path.equals(Folder.SEPARATOR)) {
				path = null;
			} else if (!path.startsWith(Folder.SEPARATOR)) {
				path = Folder.SEPARATOR + path;
			}
		}
		return path;
	}

	protected boolean getUpdateFlag() {
		return hasParameter(getUpdateArg());
	}

    protected boolean getIncludeSettingsFlag() {
		return hasParameter(getIncludeSettingsArg());
	}

    protected boolean skipThemes() {
		return hasParameter(getSkipThemesArgument());
	}

	protected boolean isUpdate() {
		return update;
	}
	
	public List<String> process() {
		initProcess();
		
		createPrependFolder();
		
		queueEntryFolders();
		queueEntryResources();
		
		while (!folderQueue.isEmpty() || !resourceQueue.isEmpty()) {
			if (folderQueue.isEmpty()) {
				String uri = (String) resourceQueue.removeFirst();
				try {
                    importResource(uri, false);
                } catch (ResourceBeanDataNotFoundException e) {
                    final String msg;
                    if (uri.equals(e.getMessage())) {
                        msg = "Reference resource \"" + e.getMessage() +
                                "\" not found.";
                    } else {
                        msg = "Reference resource \"" + e.getMessage() +
                                "\" not found when importing resource \"" + uri + "\".";
                    }
                    commandOut.warn(msg);
                    fileBrokenResource(msg);
                }
			} else {
				String uri = (String) folderQueue.removeFirst();
				importFolder(uri, true);
			}
		}
        return null;
	}

    private void fileBrokenResource(String message) {
        File file = new File("import-errors.log");
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(file, true));
            out.write(message);
            out.write('\n');
        } catch (IOException e) {
            commandOut.error("Error writing to import.log", e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e1) {}
            }
        }
    }

    protected void queueEntryFolders() {
		List entryFolders = new ArrayList();
		for (Iterator it = indexElement.elementIterator(configuration.getFolderIndexElement()); it.hasNext(); ) {
			Element folderElement = (Element) it.next();
			entryFolders.add(folderElement.getText());
		}

		if (!entryFolders.isEmpty()) {
			Collections.sort(entryFolders);
			for (Iterator it = entryFolders.iterator(); it.hasNext();) {
				String uri = (String) it.next();
				folderQueue.addLast(uri);
			}
		}
	}

	protected void queueEntryResources() {
		for (Iterator it = indexElement.elementIterator(configuration.getResourceIndexElement()); it.hasNext(); ) {
			Element resourceElement = (Element) it.next();
			String uri = resourceElement.getText();
			resourceQueue.addLast(uri);
		}
	}

	protected void createPrependFolder() {
		if (prependPath != null) {
			LinkedList toCreateURIs = new LinkedList();
			for (String path = prependPath; 
					repository.getFolder(executionContext, path) == null; 
					path = PathUtils.splitPath(path).parentPath) {
				toCreateURIs.addFirst(path);
			}
			
			while(!toCreateURIs.isEmpty()) {
				String path = (String) toCreateURIs.removeFirst();
				Folder folder = createFolder(path);

				commandOut.debug("About to save folder " + path);
				try{
				    repository.saveFolder(executionContext, folder);
                } catch (SpringSecurityException er) {
                    this.updateSecuredResource(executionContext, folder);
                }
 			}
		}
	}

	protected void initProcess() {
		importedURIs = new HashSet();
		folderQueue = new LinkedList();
		resourceQueue = new LinkedList();
		
		roles = new HashMap();
		users = new HashMap();
	}

	protected void importFolder(String uri, boolean detailsRequired) {
		if (importedURIs.contains(uri) || skipResource(uri)) {
			return;
		}

        // All organization folder uris have pattern /organizations/org_1../organizations/org2/folder except of template folder
        // which has pattern /organizations/org_template, so we have to skip it.
        String tenantFolderUri = "";
        int orgTeplateIndex = uri.indexOf("/" + TenantService.ORGANIZATIONS + "/" + TenantService.ORG_TEMPLATE);
        if (orgTeplateIndex >= 0) {
            tenantFolderUri = uri.substring(0, orgTeplateIndex);
        } else {
            Matcher m = orgPattern.matcher(uri);
            if (m.find()) {
                tenantFolderUri = m.group(1);
            }
        }

        if (!(rootSubTenantFolderUris.contains(tenantFolderUri) || tenantFolderUri.equals(""))) {
            commandOut.info("Folder with the uri" + uri+" is attached to not existing organization. Not imported");
            return;
        }

		FolderBean folderBean = getFolderDetails(uri, detailsRequired);

		String importUri = prependedPath(uri);
		Folder folder = repository.getFolder(executionContext, importUri);

        if (executionContext.getAttributes() == null) {
            executionContext.setAttributes(new ArrayList());
        }
        if (!executionContext.getAttributes().contains(RepositoryService.IS_IMPORTING)) {
            executionContext.getAttributes().add(RepositoryService.IS_IMPORTING);
        }

		if (folder == null) {
			ensureParent(uri);

			if (folderBean == null) {
				folder = createFolder(importUri);
			} else {
				folder = createFolder(folderBean);
			}

            try{
                commandOut.debug("About to save folder " + importUri);
                repository.saveFolder(executionContext, folder);
            } catch (SpringSecurityException er) {
                this.updateSecuredResource(executionContext, folder);
            }

            if (folderBean != null) {
                setPermissions(folder, folderBean.getPermissions(), false);
            }

            commandOut.info("Created repository folder " + importUri);
		} else if (update && folderBean != null) {
            folder.setLabel(folderBean.getLabel());
            folder.setDescription(folderBean.getDescription());

            try {
                commandOut.debug("About to save folder " + importUri);
                repository.saveFolder(executionContext, folder);
            } catch (SpringSecurityException er) {
                this.updateSecuredResource(executionContext, folder);
            }
            commandOut.info("Updating folder " + importUri);

            if (folderBean.isExportedWithPermissions()){
                configuration.getPermissionService().deleteObjectPermissionForObject(executionContext, folder);
            }
            setPermissions(folder, folderBean.getPermissions(), false);
        } else {
			if (folderBean != null) {
                commandOut.info("Folder " + importUri + " already exists, importing permissions only");
				
				setPermissions(folder, folderBean.getPermissions(), true);
			}
		}
		
		importedURIs.add(uri);
		
		if (folderBean != null) {
			queueSubFolders(uri, folderBean);
			
			queueResources(uri, folderBean);
		}
	}

    private boolean skipResource(String uri) {
        return themesPattern.matcher(uri).matches() && skipThemes();
    }

    protected void queueSubFolders(String uri, FolderBean folderBean) {
		String[] subFolders = folderBean.getSubFolders();
		if (subFolders != null) {
			for (int i = 0; i < subFolders.length; i++) {
				String subfolderURI = appendPath(uri, subFolders[i]);
				folderQueue.addLast(subfolderURI);
			}
		}
	}

	protected void queueResources(String uri, FolderBean folderBean) {
		String[] resources = folderBean.getResources();
		if (resources != null) {
			for (int i = 0; i < resources.length; i++) {
				String resourceUri = appendPath(uri, resources[i]);
				resourceQueue.addLast(resourceUri);
			}
		}
	}

	protected String prependedPath(String uri) {
		return PathUtils.concatPaths(prependPath, uri);
	}

	protected String appendPath(String uri, String name) {
		String subUri;
		if (uri.equals(Folder.SEPARATOR)) {
			subUri = Folder.SEPARATOR + name;
		} else {
			subUri = uri + Folder.SEPARATOR + name;
		}
		return subUri;
	}

	protected FolderBean getFolderDetails(String uri, boolean required) {
		FolderBean folderBean = null;
		String folderPath = PathUtils.concatPaths(configuration.getResourcesDirName(), uri);
		if (input.fileExists(folderPath, configuration.getFolderDetailsFileName())) {
			folderBean = (FolderBean) deserialize(folderPath, configuration.getFolderDetailsFileName(), configuration.getSerializer());
		} else {
			if (required) {
                // Adding non localized message cause import-export tool does not support localization.
                StringBuilder message = new StringBuilder("Folder details for folder ");
                message.append(uri);
                message.append(" were not found in the import information.");
                commandOut.info(message.toString());
				throw new JSException(message.toString());       
			}
 		}
		return folderBean;
	}
	
	protected void ensureParent(String uri) {
		SplittedPath splitPath = PathUtils.splitPath(uri);
		if (splitPath != null && splitPath.parentPath != null) {
			importFolder(splitPath.parentPath, false);
		}
	}

	protected Folder createFolder(String uri) {
		Folder folder = new FolderImpl();
		SplittedPath splPath = PathUtils.splitPath(uri);
		folder.setParentFolder(splPath.parentPath);
		folder.setName(splPath.name);
		folder.setLabel(splPath.name);
		return folder;
	}
	
	protected Folder createFolder(FolderBean folderBean) {
		Folder folder = new FolderImpl();
		folderBean.copyTo(folder);
		folder.setCreationDate(folderBean.getCreationDate());
		folder.setUpdateDate(folderBean.getUpdateDate());
		folder.setParentFolder(prependedPath(folder.getParentFolder()));
		return folder;
	}

	protected String importResource(String uri, boolean ignoreMissing) {
		String importUri = prependedPath(uri);
		if (!importedURIs.contains(uri) && !skipResource(uri)) {
			if (ignoreMissing && !hasResourceBeanData(uri)) {
				commandOut.info("Resource " + uri + " data missing from the catalog, skipping from import");
			} else {
                if (!hasResourceBeanData(uri)) {
                    throw new ResourceBeanDataNotFoundException(uri);
                }

                boolean importingSettings = uri.equals(configuration.getUriOfSettingsList());
                if (importingSettings) {
                    update = getIncludeSettingsFlag();
                }

 				Resource resource = repository.getResource(executionContext, importUri);

                if (executionContext.getAttributes() == null) {
                    executionContext.setAttributes(new ArrayList());
                }
                if (!executionContext.getAttributes().contains(RepositoryService.IS_IMPORTING)) {
                    executionContext.getAttributes().add(RepositoryService.IS_IMPORTING);
                }

				if (resource == null) {
					ensureParent(uri);
					
					ResourceBean bean = readResourceBean(uri);
					resource = createResource(bean);

                    commandOut.debug("About to save resource " + importUri);
                    try {
                        repository.saveResource(executionContext, resource);
                    } catch (SpringSecurityException er) {
                        this.updateSecuredResource(executionContext, resource);
                    }

                    setPermissions(resource, bean.getPermissions(), false);
					
					commandOut.info("Imported resource " + importUri);
				} else if (update) {
					registerUpdateResource(importUri);
					
					ResourceBean bean = readResourceBean(uri);
					Resource updated = createResource(bean);
					
					if (resource.isSameType(updated)) {
						// We need to re-read the object to get the latest version
						// this is a fix to bug # 27803
						// http://bugzilla.jaspersoft.com/show_bug.cgi?id=27803
						Resource resource2 = repository.getResource(executionContext, importUri);
						updated.setVersion(resource2.getVersion());
                        commandOut.debug("About to save resource " + importUri);
                        try {
                            repository.saveResource(executionContext, updated);
                        } catch (SpringSecurityException er) {
                            this.updateSecuredResource(executionContext, resource);
                        }
                        if (bean.isExportedWithPermissions()){
                            configuration.getPermissionService().deleteObjectPermissionForObject(executionContext, resource);
                        }
                        setPermissions(resource, bean.getPermissions(), false);

                        commandOut.info("Updated resource " + importUri);
					} else {
						commandOut.warn("Resource " + importUri 
								+ " already exists in the repository and has a different type than in the catalog, not updating");
					}
				} else {
					commandOut.warn("Resource " + importUri + " already exists, not importing");
				}

                if (importingSettings) {
                    update = getUpdateFlag();
                }
				
				importedURIs.add(uri);
			}
		}
		return importUri;
	}

    protected void updateSecuredResource(ExecutionContext context, Resource resource) {
        commandOut.info("Access denied for " + resource.getURIString());
    }

	protected void registerUpdateResource(String resourceUri) {
		Set updateResources = (Set) getContextAttributes().getAttribute(
				ATTRIBUTE_UPDATE_RESOURCES);
		if (updateResources == null) {
			updateResources = new HashSet();
			getContextAttributes().setAttribute(ATTRIBUTE_UPDATE_RESOURCES, 
					updateResources);
		}
		
		updateResources.add(resourceUri);
	}

	protected Resource createResource(ResourceBean bean) {
		Class resourceItf = configuration.getCastorBeanMappings().getInterface(bean.getClass());
		Resource resource = repository.newResource(executionContext, resourceItf);
		bean.copyTo(resource, this);
		resource.setParentFolder(prependedPath(resource.getParentFolder()));
		return resource;
	}

	protected boolean hasResourceBeanData(String uri) {
		String resourceFileName = getResourceFileName(uri);
		return input.fileExists(configuration.getResourcesDirName(), resourceFileName);
	}
	
	protected ResourceBean readResourceBean(String uri) {
		String resourceFileName = getResourceFileName(uri);
		ResourceBean bean = (ResourceBean) deserialize(configuration.getResourcesDirName(), resourceFileName, configuration.getSerializer());
		return bean;
	}
	
	protected String getResourceFileName(String uri) {
		return uri + ".xml";
	}

    @Override
    public String getSourceJsVersion() {
        return super.getSourceJsVersion();
    }

    @Override
    public String getTargetJsVersion() {
        return super.getTargetJsVersion();
    }

    public ResourceReference handleReference(ResourceReferenceBean beanReference) {
		ResourceReference reference;
		if (beanReference == null) {
			reference = null;
		} else if (beanReference.isLocal()) {
			ResourceBean localResBean = beanReference.getLocalResource();
			Resource localRes = createResource(localResBean);
			reference = new ResourceReference(localRes);
		} else {
			String referenceURI = beanReference.getExternalURI();
			if (referenceURI == null || referenceURI.equals("")) {
                return null;
            }
			importResource(referenceURI, false);
			reference = new ResourceReference(prependedPath(referenceURI));
		}
		return reference;
	}

	public byte[] handleData(ResourceBean resourceBean, String dataFile, String providerId) {
		String filename = PathUtils.concatPaths(resourceBean.getFolder(), dataFile);
		InputStream dataInput = getFileInput(configuration.getResourcesDirName(), filename);
		boolean closeInput = true;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataContainerStreamUtil.pipeData(dataInput, out);
			
			closeInput = false;
			dataInput.close();
			
			return out.toByteArray();
		} catch (IOException e) {
			log.error(e);
			throw new JSExceptionWrapper(e);
		} finally {
			if (closeInput) {
				try {
					dataInput.close();
				} catch (IOException e) {
					log.error(e);
				}
			}
		}
	}

	public Resource handleResource(ResourceBean resource) {
		Resource res = createResource(resource);
		return res;
	}

	public String handleResource(String uri) {
		return handleResource(uri, false);
	}

	public String handleResource(String uri, boolean ignoreMissing) {
		return importResource(uri, ignoreMissing);
	}

	protected void setPermissions(InternalURI object, RepositoryObjectPermissionBean[] permissions, boolean checkExisting) {
		if (permissions != null) {
			for (int i = 0; i < permissions.length; i++) {
				RepositoryObjectPermissionBean permissionBean = permissions[i];
				setPermission(object, permissionBean, checkExisting);
			}
		}
	}

	protected void setPermission(InternalURI object, RepositoryObjectPermissionBean permissionBean, boolean checkExisting) {
		ObjectPermissionService permissionsService = configuration.getPermissionService();
		
		PermissionRecipient permissionRecipient = permissionBean.getRecipient();
		Object recipient;
		String recipientType = permissionRecipient.getRecipientType();
		if (recipientType.equals(configuration.getPermissionRecipientRole())) {
			recipient = getRole(permissionRecipient);
			if (recipient == null) {
				commandOut.warn("Role " + permissionRecipient + " not found, skipping permission of " + object.getURI());
			}
		} else if (recipientType.equals(configuration.getPermissionRecipientUser())) {
			recipient = getUser(permissionRecipient);
			if (recipient == null) {
				commandOut.warn("User " + permissionRecipient + " not found, skipping permission of " + object.getURI());
			}
		} else {
			recipient = null;
			commandOut.warn("Unknown object permission recipient type " + recipientType + ", skipping permission of " + object.getURI());
		}
		
		if (recipient != null) {
			boolean existing;
			if (checkExisting) {
				List permissions = permissionsService.getObjectPermissionsForObjectAndRecipient(executionContext, object, recipient);
				existing = permissions != null && !permissions.isEmpty();
			} else {
				existing = false;
			}
			if (existing) {
				if (log.isInfoEnabled()) {
					log.info("Permission on " + object.getURI() + " for " + permissionRecipient + " already exists, skipping.");
				}
			} else {
				ObjectPermission permission = permissionsService.newObjectPermission(executionContext);
				permission.setURI(object.getURI());
				permission.setPermissionMask(permissionBean.getPermissionMask());
				permission.setPermissionRecipient(recipient);
				
				permissionsService.putObjectPermission(executionContext, permission);
			}
		}		
	}

	protected Role getRole(TenantQualifiedName roleName) {
		Role role;
		if (roles.containsKey(roleName)) {
			role = (Role) roles.get(roleName);
		} else {
			role = loadRole(roleName);
			roles.put(roleName, role);
		}
		return role;
	}

	protected Role loadRole(TenantQualifiedName roleName) {
		return configuration.getAuthorityService().getRole(executionContext, roleName.getName());
	}

	protected User getUser(TenantQualifiedName username) {
		User user;
		if (users.containsKey(username)) {
			user = (User) users.get(username);
		} else {
			user = loadUser(username);
			users.put(username, user);
		}
		return user;
	}

	protected User loadUser(TenantQualifiedName username) {
		return configuration.getAuthorityService().getUser(executionContext, username.getName());
	}

	public ResourceModuleConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ResourceModuleConfiguration configuration) {
		this.configuration = configuration;
	}

    public TenantService getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    public String getPrependPathArg() {
		return prependPathArg;
	}

	public void setPrependPathArg(String prependPathArg) {
		this.prependPathArg = prependPathArg;
	}

	public String getUpdateArg() {
		return updateArg;
	}

	public void setUpdateArg(String updateArg) {
		this.updateArg = updateArg;
	}

    public String getIncludeSettingsArg() {
        return includeSettingsArg;
    }

    public void setIncludeSettingsArg(String includeSettingsArg) {
        this.includeSettingsArg = includeSettingsArg;
    }

    public String getSkipThemesArgument() {
        return skipThemesArgument;
    }

    public void setSkipThemesArgument(String skipThemesArgument) {
        this.skipThemesArgument = skipThemesArgument;
    }
}
