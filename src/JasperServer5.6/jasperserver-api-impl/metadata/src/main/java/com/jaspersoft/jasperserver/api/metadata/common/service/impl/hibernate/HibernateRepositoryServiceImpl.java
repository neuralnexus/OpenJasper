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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate;

import com.jaspersoft.jasperserver.api.JSDuplicateResourceException;
import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrorFilter;
import com.jaspersoft.jasperserver.api.common.domain.ValidationErrors;
import com.jaspersoft.jasperserver.api.common.domain.impl.ExecutionContextImpl;
import com.jaspersoft.jasperserver.api.common.domain.impl.ValidationErrorsImpl;
import com.jaspersoft.jasperserver.api.common.service.ClassMappingsObjectFactory;
import com.jaspersoft.jasperserver.api.common.util.CollatorFactory;
import com.jaspersoft.jasperserver.api.common.util.DefaultCollatorFactory;
import com.jaspersoft.jasperserver.api.logging.access.context.AccessContext;
import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.*;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedRepoObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.JSResourceNotFoundException;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryEventListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceValidator;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.*;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.*;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.RepositoryUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.ResourceCriterionUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.SortingUtils;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util.UpdateDatesIndicator;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportUnit;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterCriteria;
import com.jaspersoft.jasperserver.api.metadata.view.domain.FilterElement;
import com.jaspersoft.jasperserver.api.search.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.Collator;
import java.util.*;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: HibernateRepositoryServiceImpl.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class HibernateRepositoryServiceImpl extends HibernateDaoImpl implements HibernateRepositoryService, ReferenceResolver, RepoManager, ApplicationContextAware {
	
	private static final Log log = LogFactory.getLog(HibernateRepositoryServiceImpl.class);
	
	protected static final int RESOURCE_NAME_LENGHT = 100;
    protected int resourceUriLength = -1;

	protected static final String TEMP_NAME_PREFIX = "*";
	protected static final int TEMP_NAME_PREFIX_LENGTH = TEMP_NAME_PREFIX.length();
	protected static final String CHILDREN_FOLDER_SUFFIX = "_files";
	protected static final String COPY_GENERATED_NAME_SEPARATOR = "_";

	protected static final Map<String, Object> CLIENT_CLONE_OPTIONS;
	static {
		HashMap<String, Object> options = new HashMap<String, Object>();
		options.put(RepoResource.CLIENT_OPTION_FULL_DATA, null);
		options.put(RepoResource.CLIENT_OPTION_AS_NEW, null);
		CLIENT_CLONE_OPTIONS = Collections.unmodifiableMap(options);
	}

	private ResourceFactory resourceFactory;
	private ResourceFactory persistentClassMappings;
	private ClassMappingsObjectFactory validatorMappings;
	private ThreadLocal tempNameResources;
	
	private CollatorFactory collatorFactory = new DefaultCollatorFactory();
	
	private List repositoryListeners = new ArrayList();
	private RepositorySecurityChecker securityChecker;
	private boolean lockFoldersOnPathChange = true;

    private AuditContext auditContext;
    private AccessContext accessContext;

    private QueryModificationEvaluator queryModificationEvaluator;

    private HibernateSaveUpdateDeleteListener hibernateSaveUpdateDeleteListener;

    private ApplicationContext applicationContext; //needed for validation settings


	public HibernateRepositoryServiceImpl() {
		tempNameResources = new ThreadLocal();
	}

    /**
     *
     * @param sessionFactory
     * @return
     */
    @Override
    protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
        //return super.createHibernateTemplate(sessionFactory);
        return new HibernateDaoTemplate(sessionFactory) {
            @Override
            public Serializable save(Object entity) throws DataAccessException {
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.beforeSave(entity, this);
                }
                Serializable ret = super.save(entity);
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.afterSave(entity, this);
                }
                return ret;
            }

            @Override
            public void update(Object entity) throws DataAccessException {
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.beforeUpdate(entity, this);
                }
                super.update(entity);
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.afterUpdate(entity, this);
                }
            }

            @Override
            public void saveOrUpdate(Object entity) throws DataAccessException {
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.beforeSaveOrUpdate(entity, this);
                }
                super.saveOrUpdate(entity);
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.afterSaveOrUpdate(entity, this);
                }
            }

            @Override
            public void delete(Object entity) throws DataAccessException {
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.beforeDelete(entity, this);
                }
                super.delete(entity);
                if (hibernateSaveUpdateDeleteListener != null) {
                    hibernateSaveUpdateDeleteListener.afterDelete(entity, this);
                }
            }
        };
    }

    public ResourceFactory getPersistentClassMappings() {
		return persistentClassMappings;
	}

    public AuditContext getAuditContext() {
        return auditContext;
    }

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    public AccessContext getAccessContext() {
        return accessContext;
    }

    public void setAccessContext(AccessContext accessContext) {
        this.accessContext = accessContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setPersistentClassMappings(ResourceFactory persistentClassMappings) {
		this.persistentClassMappings = persistentClassMappings;
	}

	public ResourceFactory getResourceFactory() {
		return resourceFactory;
	}

	public void setResourceFactory(ResourceFactory resourceFactory) {
		this.resourceFactory = resourceFactory;
	}

	public ClassMappingsObjectFactory getValidatorMappings() {
		return validatorMappings;
	}

	public void setValidatorMappings(ClassMappingsObjectFactory validatorMappings) {
		this.validatorMappings = validatorMappings;
	}

	public ResourceValidator getValidator(Resource resource) {
		return resource == null ? null : 
			(ResourceValidator) validatorMappings.getClassObject(resource.getResourceType());
	}

	public RepositorySecurityChecker getSecurityChecker() {
		return securityChecker;
	}

	public void setSecurityChecker(RepositorySecurityChecker securityChecker) {
		this.securityChecker = securityChecker;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public Resource getResource(ExecutionContext context, final String uri) {
		return getResourceUnsecure(context, uri);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public Resource getResourceUnsecure(ExecutionContext context, final String uri) {
		return (Resource) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResource(uri, null);
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public Resource getResource(ExecutionContext context, final String uri, final Class resourceType) {
		return (Resource) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResource(uri, resourceType);
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public <T extends Resource> T makeResourceCopy(ExecutionContext context, final String uri, final int version, 
			final Class<T> resourceType, final String copyURI) {
		return (T) executeCallback(new DaoCallback() {
			public Object execute() {
				Resource resourceCopy = loadResource(uri, resourceType, CLIENT_CLONE_OPTIONS, version);
				if (resourceCopy != null) {
					String folderPath = RepositoryUtils.getParentPath(copyURI);
					String name = RepositoryUtils.getName(copyURI);
					resourceCopy.setParentFolder(folderPath);
					resourceCopy.setName(name);
				}				
				return resourceCopy;
			}
		});
	}

    private void auditResourceActivity(final String eventType, final String uri, final String resourceType) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent(eventType, uri, resourceType);
            }
        });
    }

    private void auditResourceCopyAndMove(final String eventType, final String sourceUri,
                                          final String destUri, final String resourceType) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                AuditEvent auditEvent = auditContext.createAuditEvent(eventType, sourceUri, resourceType);
                auditContext.addPropertyToAuditEvent("destFolderUri", destUri, auditEvent);
            }
        });
    }

    private void auditFolderActivity(final String eventType, final String uri) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                auditContext.createAuditEvent(eventType, uri, Folder.class.getName());
            }
        });
    }

    private void auditFolderActivity(final String eventType, final Folder folder) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                AuditEvent auditEvent  = auditContext.createAuditEvent(eventType, folder.getURI(), Folder.class.getName());
                auditContext.addPropertyToAuditEvent("folderName", folder.getName(), auditEvent);
                auditContext.addPropertyToAuditEvent("folderLabel", folder.getLabel(), auditEvent);
                auditContext.addPropertyToAuditEvent("folderDescription", folder.getDescription(), auditEvent);
            }
        });
    }

    private void auditFolderCopyAndMove(final String eventType, final String sourceUri,
                                          final String destUri) {
        auditContext.doInAuditContext(new AuditContext.AuditContextCallback() {
            public void execute() {
                AuditEvent auditEvent = auditContext.createAuditEvent(eventType, sourceUri, Folder.class.getName());
                auditContext.addPropertyToAuditEvent("destFolderUri", destUri, auditEvent);
            }
        });
    }

    private void closeAuditEvent(final String eventType) {
        auditContext.doInAuditContext(eventType, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

    private void logAccessResource(final RepoResource repoResource, final boolean updating) {
        accessContext.doInAccessContext(new AccessContext.AccessContextCallback() {
            public void fillAccessEvent(AccessEvent accessEvent) {
                accessEvent.setResource((Resource)repoResource.toClient(resourceFactory));
                accessEvent.setUpdating(updating);
            }
        });
    }
    
	protected Resource loadResource(final String uri, Class resourceType) {
		return loadResource(uri, resourceType, null, null);
	}

	protected Resource loadResource(final String uri, Class resourceType, Map<String, Object> clientOptions, 
			Integer expectedVersion) {
		Class persistentClass = resourcePersistentClass(resourceType);

		RepoResource repoResource = findByURI(persistentClass, uri, false);
		Resource resource;
		if (repoResource == null) {
			resource = null;
		} else {
			if (expectedVersion != null && expectedVersion.intValue() != repoResource.getVersion()) {
				throw new JSException("Resource " + uri + " has version " + repoResource.getVersion()
						+ ", was expecting " + expectedVersion);
			}
			
            auditResourceActivity("accessResource", uri, repoResource.getClientType().getName());
            logAccessResource(repoResource, false);
			resource = (Resource) repoResource.toClient(resourceFactory, clientOptions);
            closeAuditEvent("accessResource");
		}
		return resource;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public ResourceLookup getResourceLookupUnsecure(ExecutionContext context, final String uri) {
		return (ResourceLookup) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResourceLookup(uri, null);
			}
		});
	}
	
	protected ResourceLookup loadResourceLookup(final String uri, Class resourceType) {
		Class persistentClass = resourcePersistentClass(resourceType);

		RepoResource repoResource = findByURI(persistentClass, uri, false);
		ResourceLookup resource;
		if (repoResource == null) {
			resource = null;
		} else {
			resource = repoResource.toClientLookup();
		}
		return resource;
	}

	protected Class resourcePersistentClass(Class resourceType) {
		Class persistentClass;
		if (resourceType == null) {
			persistentClass = RepoResource.class;
		} else {			
			persistentClass = getPersistentClassMappings().getImplementationClass(resourceType);
		}
		return persistentClass;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveFolder(ExecutionContext context, final Folder folder) {
        final RepoManager manager = this;
        final ExecutionContext executionContext = context;

		executeWriteCallback(new DaoCallback() {
			public Object execute() {
                RepoFolder repoFolder = getFolder(folder.getURIString(), false);
                validateResourceUriLength(folder.getURIString().length());

                String eventType;

                if (folder.isNew()) {
                    eventType = "createFolder";
                    auditFolderActivity(eventType, folder);
                    if (repoFolder != null || resourceExists(folder.getURIString())) {
                        throw new JSDuplicateResourceException("jsexception.folder.already.exists", new Object[] {folder.getURIString()});
                    }

                    repoFolder = createNewRepoFolder();

                    if (log.isDebugEnabled()) {
                        log.debug("Creating new folder " + folder.getURIString());
                    }
                } else {
                    eventType = "updateFolder";
                    auditFolderActivity(eventType, folder);
                    if (repoFolder == null) {
                        String quotedURI = "\"" + folder.getURIString() + "\"";
                        throw new JSException("jsexception.folder.not.found", new Object[] {quotedURI});
                    }
                    repoFolder.setUpdateDate(new Date());
                }

                String parentURI = folder.getParentFolder();
                RepoFolder parent;
                if (parentURI == null && folder.getName().equals(Folder.SEPARATOR)) {
                    parent = null;
                } else {
                    parent = getFolder(parentURI, true);
                }

                //TODO don't set parent, name when updating
                repoFolder.set(folder, parent, manager);

                // Set the original updateDate of the folder if it is being imported
                if (executionContext != null
                        && executionContext.getAttributes() != null
                        && executionContext.getAttributes().contains(RepositoryService.IS_IMPORTING)
                        && folder.getUpdateDate() != null) {
                    repoFolder.setUpdateDate(folder.getUpdateDate());
                }

                getHibernateTemplate().saveOrUpdate(repoFolder);
                closeAuditEvent(eventType);
				return null;
			}
		});
	}

	protected RepoFolder createNewRepoFolder() {
		RepoFolder repoFolder = new RepoFolder();
		repoFolder.setCreationDate(getOperationTimestamp());
		repoFolder.setUpdateDate(getOperationTimestamp());
		return repoFolder;
	}


	protected RepoFolder getFolder(String uri, boolean required) {
		if (uri == null || uri.length() == 0 || uri.equals(Folder.SEPARATOR)) {
			return getRootFolder();
		}
		
		// Deal with URIs that come with "repo:" on the front
		
		final String repoURIPrefix = Resource.URI_PROTOCOL + ":";
		String workUri = uri.startsWith(repoURIPrefix) ? uri.substring(repoURIPrefix.length()) : uri;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
		criteria.add(Restrictions.naturalId().set("URI", workUri));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List foldersList = getHibernateTemplate().findByCriteria(criteria);
		RepoFolder folder;
		if (foldersList.isEmpty()) {
			if (required) {
				String quotedURI = "\"" + uri + "\"";
				throw new JSResourceNotFoundException("jsexception.folder.not.found.at", new Object[] {quotedURI});
			}

			log.debug("Folder not found at \"" + uri + "\"");
			folder = null;
		} else {
			folder = (RepoFolder) foldersList.get(0);
		}
		return folder;
	}

	protected RepoFolder getRootFolder() {
		DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
		criteria.add(Restrictions.naturalId().set("URI", Folder.SEPARATOR));
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List foldersList = getHibernateTemplate().findByCriteria(criteria);
		RepoFolder root;
		if (foldersList.isEmpty()) {
			root = null;
		} else {
			root = (RepoFolder) foldersList.get(0);
		}
		return root;
	}

	public ValidationErrors validateResource(ExecutionContext context, final Resource resource, final ValidationErrorFilter filter) {
		return validate(resource, filter);
	}

	protected ValidationErrors validate(final Resource resource,
			final ValidationErrorFilter filter) {
		ResourceValidator validator = getValidator(resource);
		
		if (validator != null) {
			return validator.validate(resource, filter);
		}
		
		return new ValidationErrorsImpl();
	}

	public ValidationErrors validateFolder(ExecutionContext context, Folder folder, ValidationErrorFilter filter) {
		return validate(folder, filter);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveResource(ExecutionContext executionContext, Resource res) {
        saveResourceWithFlush(executionContext, res, true);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void saveResourceNoFlush(ExecutionContext executionContext, Resource res) {
        saveResourceWithFlush(executionContext, res, false);
    }

    private void saveResourceWithFlush(ExecutionContext executionContext, Resource res, boolean flush) {
        initTempNameResources();
        final ExecutionContext context = executionContext;
        final Resource resource = res;
        try {
			executeWriteCallback(new DaoCallback() {
				public Object execute() {
					RepoResource repo = getRepoResource(resource);
                    String eventType;
                    boolean isImporting = context != null && context.getAttributes() != null && context.getAttributes().contains(RepositoryService.IS_IMPORTING);
                    boolean isOverwriting = context != null && context.getAttributes() != null && context.getAttributes().contains(RepositoryService.IS_OVERWRITING);

                    if (repo.isNew()) {
                        eventType = "saveResource";
                        auditResourceActivity(eventType, resource.getURI(), resource.getResourceType());
                    } else {
                        eventType = isOverwriting ? "overwriteResource" : "updateResource";
                        auditResourceActivity(eventType, resource.getURI(), repo.getClientType().getName());
                    }

                    // Set the appropriate creationDate and updateDate against the normal, import or overwrite modes.
                    // Now local resources are also affected in case of importing or overwriting.
                    if (isImporting || isOverwriting) {
                        UpdateDatesIndicator.required(getOperationTimestamp(), !isImporting);

                        try {
                            repo.copyFromClient(resource, HibernateRepositoryServiceImpl.this);

                            UpdateDatesIndicator.clean();
                        } catch (RuntimeException e) {
                            UpdateDatesIndicator.clean();

                            throw e;
                        }
                    } else { // For regular cases just set updateDate of current (top level) resource to operational.
                        repo.copyFromClient(resource, HibernateRepositoryServiceImpl.this);

                        repo.setUpdateDate(getOperationTimestamp());
                    }

					RepoResource repositoryResource = repo;
					getHibernateTemplate().saveOrUpdate(repositoryResource);

                    logAccessResource(repositoryResource, true);
                    closeAuditEvent(eventType);
					return null;
				}
			}, flush);
			
			if (tempNameResources() != null && !tempNameResources().isEmpty()) {
				executeWriteCallback(new DaoCallback() {
					public Object execute() {
						HibernateTemplate template = getHibernateTemplate();
						for (Iterator it = tempNameResources().iterator(); it.hasNext();) {
							RepoResource res = (RepoResource) it.next();
							res.setName(res.getName().substring(TEMP_NAME_PREFIX_LENGTH));
							
							RepoFolder childrenFolder = res.getChildrenFolder();
							if (childrenFolder != null) {
								childrenFolder.setName(childrenFolder.getName().substring(TEMP_NAME_PREFIX_LENGTH));
								refreshFolderPaths(childrenFolder);
							}
							
							template.save(res);
						}
						return null;
					}
				});
			}
		} finally {
			resetTempNameResources();
		}
    }

    @Deprecated
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void saveResource(ExecutionContext context, final Resource resource, boolean updateCreationDate) {

        // Since this method is deprecated it invokes the main saveResource with appropriate context parameter.

        if (updateCreationDate) {
            if (context == null) {
                context = new ExecutionContextImpl();
            }
            if (context.getAttributes() == null) {
                context.setAttributes(new ArrayList());
            }
            context.getAttributes().add(RepositoryService.IS_OVERWRITING);
        }

        saveResource(context,resource);
	}

	protected void initTempNameResources() {
		// re-entrance support
        if (tempNameResources() == null) {
		    tempNameResources.set(new HashSet());
        }
	}

	protected void resetTempNameResources() {
		tempNameResources.set(null);
	}
	
	protected Set tempNameResources() {
		return (Set) tempNameResources.get();	
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public RepoResource getRepoResource(Resource resource) {
		Class persistentClass = getPersistentClassMappings().getImplementationClass(resource.getClass());
		if (persistentClass == null) {
			String quotedResource = "\"" + resource.getClass().getName() + "\"";
			throw new JSException("jsexception.no.persistent.class.mapped.to", new Object[] {quotedResource});
		}

		RepoResource repo;
		if (resource.isNew()) {
			if (pathExists(resource.getURIString())) {
				String quotedResource = "\"" + resource.getURIString() + "\"";
				throw new JSDuplicateResourceException("jsexception.resource.already.exists", new Object[] {quotedResource});
			}

			repo = createPersistentResource(persistentClass);

			RepoFolder parent = getFolder(resource.getParentFolder(), true);
			repo.setParent(parent);
		} else {
			repo = findByURI(persistentClass, resource.getURIString(), false);
			if (repo == null) {
				String quotedURI = "\"" + resource.getURIString() + "\""; 
				throw new JSException("jsexception.resource.does.not.exist", new Object[] {quotedURI});
			}
		}
		return repo;
	}

	public boolean resourceExists(ExecutionContext executionContext, String uri) {
		return resourceExists(uri);
	}

	public boolean resourceExists(ExecutionContext executionContext, String uri, Class resourceType) {
		return resourceExists(uri, resourceType);
	}

	public boolean resourceExists(ExecutionContext executionContext, FilterCriteria filterCriteria) {
		DetachedCriteria criteria = translateFilterToCriteria(filterCriteria);
		boolean exists;
		if (criteria == null) {
			exists = false;
		} else {
			criteria.setProjection(Projections.rowCount());
            criteria.getExecutableCriteria(getSession()).setCacheable(true);
			List countList = getHibernateTemplate().findByCriteria(criteria);
			int count = ((Integer) countList.get(0)).intValue();
			exists = count > 0;
		}
		return exists;
	}

	protected boolean resourceExists(String uri) {
		return resourceExists(uri, null);
	}
	
	protected boolean resourceExists(String uri, Class type) {
		int sep = uri.lastIndexOf(Folder.SEPARATOR);
		boolean exists = false;
		if (sep >= 0) {
			String name = uri.substring(sep + Folder.SEPARATOR_LENGTH);
			String folderName = uri.substring(0, sep);
            if("repo:".equals(folderName)){
                folderName += Folder.SEPARATOR;
            }
            RepoFolder folder = getFolder(folderName, false);
			if (folder != null) {
				exists = resourceExists(folder, name, type);
			}
		}
		return exists;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public ResourceLookup[] findResource(final ExecutionContext context, final FilterCriteria filterCriteria)
	{
		return (ResourceLookup[]) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResources(context, filterCriteria);
			}
		});		
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public ResourceLookup[] findResources(final ExecutionContext context, final FilterCriteria[] filterCriteria)
	{
		return (ResourceLookup[]) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadResources(context, filterCriteria);
			}
		});		
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List loadClientResources(FilterCriteria filterCriteria) {

		List repoResources = loadRepoResourceList(filterCriteria);
		
		List result = new ArrayList(repoResources.size());
		
		for (Iterator iter = repoResources.iterator(); iter.hasNext(); ) {
            RepoResourceBase repoResource = (RepoResourceBase) iter.next();
			result.add(repoResource.toClient(resourceFactory));
		}
		
		return result;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List loadResourcesList(FilterCriteria filterCriteria) {
		return loadResourcesList(null, filterCriteria);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List loadResourcesList(ExecutionContext context, FilterCriteria filterCriteria) {
		List repoResources = loadRepoResourceList(context, filterCriteria);		
		return toLookups(repoResources, false);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List loadResourcesList(ExecutionContext context, FilterCriteria[] filterCriteria) {
		List repoResources;
		if (filterCriteria.length == 1) {
			repoResources = loadRepoResourceList(context, filterCriteria[0]);
		} else {
			repoResources = new ArrayList();
			for (int i = 0; i < filterCriteria.length; i++) {
				FilterCriteria criteria = filterCriteria[i];
				List criteriaRes = loadRepoResourceList(context, criteria, false);
				if (criteriaRes != null) {
					repoResources.addAll(criteriaRes);
				}
			}
			
			sortRepoResourcesByURI(context, repoResources);
		}
		
		return toLookups(repoResources, false);
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Long> getResourcesIds(ExecutionContext context, String text, Class type, Class aClass,
                                                  List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters,
                                                  SearchSorter sorter, TransformerFactory transformerFactory,
                                                  int firstResult, int maxResult) {
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory is null.");
        }

        SearchCriteria criteria =
                getResourcesListCriteria(context, text, type, aClass, filters, typeSpecificFilters);

        ProjectionList distinctProperties = Projections.projectionList().
                add(Projections.id());
        criteria.addProjection(Projections.distinct(distinctProperties));

        if (sorter != null) {
            sorter.applyOrder(type.getName(), context, criteria);
        }

        List list  = getHibernateTemplate().findByCriteria(criteria, firstResult, maxResult);

        List<Long> ids = new ArrayList<Long>();
        ResultTransformer transformer = transformerFactory.createTransformer(filters, sorter);
        if (transformer != null) {
            ids.addAll(transformer.transformToIdList(list));
        } else {
            throw new IllegalArgumentException("Result transformer is null.");
        }

        return ids;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<ResourceLookup> getResourcesByIdList(List<Long> idList) {
        return getResourcesByIdList(idList, null);
    }

    protected List<ResourceLookup> getResourcesByIdList(List<Long> idList, SearchCriteriaFactory searchCriteriaFactory) {
        List resourceList = new ArrayList();

        if (!idList.isEmpty()) {
            DetachedCriteria criteria = searchCriteriaFactory != null ? searchCriteriaFactory.create(ExecutionContextImpl.getRuntimeExecutionContext(), null) : DetachedCriteria.forClass(RepoResource.class);
            criteria.add(Restrictions.in("id", idList));
            resourceList = getHibernateTemplate().findByCriteria(criteria);
        }

        // Reordering is required because the result (resource) list is not in the same order as id list.
        List orderedResourceList = orderByIdList(resourceList, idList);

        return toLookups(orderedResourceList, true);
    }

    private List orderByIdList(List<IdedRepoObject> resourceList, List<Long> idList) {
        List<IdedRepoObject> orderedList = new ArrayList<IdedRepoObject>();

        for (Long id : idList) {
            for (IdedRepoObject repoResource : resourceList) {
                if (repoResource.getId() == id) {
                    orderedList.add(repoResource);
                }
            }
        }

        return orderedList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Map<Class, Integer> loadResourcesMapCount(ExecutionContext context, String text, List<Class> resourceTypeList,
            List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters, SearchSorter sorter,
            TransformerFactory transformerFactory) {
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory is null.");
        }

        Map<Class, Integer> result = new HashMap<Class, Integer>();

        Map<Class, Class> typeToPersistentClassMap = getTypeToPersistentClassMap(resourceTypeList);

        for (Map.Entry<Class, Class> entry : typeToPersistentClassMap.entrySet()) {
            SearchCriteria criteria = getResourcesListCriteria(context, text, entry.getKey(), entry.getValue(), filters,
                    typeSpecificFilters);
            criteria.setProjection(Projections.countDistinct("id"));

//            if (sorter != null) {
//                sorter.applyOrder(entry.getKey().getName(), context, criteria);
//            }

            List resourceList = getHibernateTemplate().findByCriteria(criteria);

            ResultTransformer transformer = transformerFactory.createTransformer(filters, sorter);
            if (transformer != null) {
                result.put(entry.getKey(), transformer.transformToCount(resourceList));
            } else {
                throw new IllegalArgumentException("Result transformer is null.");
            }
        }

        return result;
    }

    private SearchCriteria getResourcesListCriteria(ExecutionContext context, String text, Class type, Class aClass,
        List<SearchFilter> filters, Map<String, SearchFilter> typeSpecificFilters) {
        SearchCriteria criteria = SearchCriteria.forClass(aClass);

        if (filters != null) {
            for (SearchFilter filter : filters) {
                filter.applyRestrictions(type.getName(), context, criteria);
            }
        }

        if (typeSpecificFilters != null && typeSpecificFilters.get(type.getName()) != null) {
            typeSpecificFilters.get(type.getName()).applyRestrictions(type.getName(), context, criteria);
        } else {
            String st = (text == null) ? "" : text;

            criteria.add(ResourceCriterionUtils.getTextCriterion(st));
        }

        String p = criteria.getAlias("parent", "p");
        criteria.add(Restrictions.eq(p + ".hidden", Boolean.FALSE));

        return criteria;
    }

    private Map<Class, Class> getTypeToPersistentClassMap(List<Class> resourceTypeList) {
        Map<Class, Class> typeToPersistentClassMap = new HashMap<Class, Class>();
        if (resourceTypeList == null) {
            typeToPersistentClassMap.put(null, RepoResource.class);
        } else {
            for (Class aClass : resourceTypeList) {
                Class persistentClass = getPersistentClassMappings().getImplementationClass(aClass);

                if (persistentClass == null) {
                    throw new JSResourceNotFoundException("jsexception.resource.of.type.not.found",
                            new Object[] {"", aClass.getName()});
                }

                typeToPersistentClassMap.put(aClass, persistentClass);
            }
        }

        return typeToPersistentClassMap;
    }

	protected List toLookups(List repoResources, boolean addIdAsAttr) {
		List result = new ArrayList(repoResources.size());
		
		for (Iterator iter = repoResources.iterator(); iter.hasNext(); ) {
            IdedRepoObject repoResource = (IdedRepoObject) iter.next();
            ResourceLookup resourceLookup = repoResource.toClientLookup();

            if (addIdAsAttr) {
                List attrList = resourceLookup.getAttributes();
                if (attrList == null) {
                    attrList = new ArrayList();
                    resourceLookup.setAttributes(attrList);
                }
                attrList.add(new IdAttribute(repoResource.getId()));
            }

			result.add(resourceLookup);
		}
		return result;
	}

	protected ResourceLookup[] loadResources(ExecutionContext context, FilterCriteria filterCriteria) {
		List repoResources = loadResourcesList(context, filterCriteria);
		
		ResourceLookup[] resourceLookups = new ResourceLookup[repoResources.size()];
		resourceLookups = (ResourceLookup[]) repoResources.toArray(resourceLookups);
		return resourceLookups;
	}

	protected ResourceLookup[] loadResources(ExecutionContext context, FilterCriteria[] filterCriteria) {
		List repoResources = loadResourcesList(context, filterCriteria);
		
		ResourceLookup[] resourceLookups = new ResourceLookup[repoResources.size()];
		resourceLookups = (ResourceLookup[]) repoResources.toArray(resourceLookups);
		return resourceLookups;
	}
	
	public List loadRepoResourceList(final FilterCriteria filterCriteria) {
		return loadRepoResourceList(null, filterCriteria);
	}
	
	protected List loadRepoResourceList(final ExecutionContext context, final FilterCriteria filterCriteria) {
		return loadRepoResourceList(context, filterCriteria, true);
	}

	public List loadRepoResourceList(final ExecutionContext context, final FilterCriteria filterCriteria,
			final boolean sort) {
		DetachedCriteria criteria = translateFilterToCriteria(filterCriteria);
		
		// If we don't have a mapping, ignore it
		if (criteria == null) {
			return new ArrayList();
		} else {
            criteria.getExecutableCriteria(getSession()).setCacheable(true);
        }
		
		List repoResources = getHibernateTemplate().findByCriteria(criteria);
		if (sort) {
			sortRepoResourcesByURI(context, repoResources);
		}
		
		return repoResources;
	}

	protected DetachedCriteria translateFilterToCriteria(FilterCriteria filterCriteria) {
		Class filterClass = filterCriteria == null ? null : filterCriteria.getFilterClass();
		Class persistentClass;
		if (filterClass == null) {
			persistentClass = RepoResource.class;
            //persistentClass = RepoOlapUnit.class;    
		} else {			
			persistentClass = getPersistentClassMappings().getImplementationClass(filterClass);
		}

		DetachedCriteria criteria;
		if (persistentClass == null) {			
			criteria = null;
		} else {
			criteria = DetachedCriteria.forClass(persistentClass);
			criteria.createAlias("parent", "parent");
			criteria.add(Restrictions.eq("parent.hidden", Boolean.FALSE));
			if (filterCriteria != null) {
				List filterElements = filterCriteria.getFilterElements();
				if (!filterElements.isEmpty()) {
					Conjunction conjunction = Restrictions.conjunction();
					HibernateFilter filter = new HibernateFilter(conjunction, this);
					for (Iterator it = filterElements.iterator(); it.hasNext();) {
						FilterElement filterElement = (FilterElement) it.next();
						filterElement.apply(filter);
					}
					criteria.add(conjunction);
				}
			}
		}
		
		return criteria;
	}
	
	protected void sortRepoResourcesByURI(final ExecutionContext context, List repoResources) {
		SortingUtils.sortRepoResourcesByURI(getCollator(context), repoResources);
	}

    protected void sortRepoFoldersByURI(final ExecutionContext context, List repoResources) {
        SortingUtils.sortFoldersByURI(getCollator(context), repoResources);
    }

	public Resource newResource(ExecutionContext context, Class _class) {
		return resourceFactory.newResource(context, _class);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public RepoResource findByURI(Class persistentClass, String uri, boolean required) {
		if (uri == null) {
			throw new JSException("jsexception.null.uri");
		}
		
		// Deal with URIs that come with "repo:" on the front
		
		final String repoURIPrefix = Resource.URI_PROTOCOL + ":";
		String workUri = uri.startsWith(repoURIPrefix) ? uri.substring(repoURIPrefix.length()) : uri;
		
		int sep = workUri.lastIndexOf(Folder.SEPARATOR);
		RepoResource res = null;
		if (sep >= 0) {
			String name = workUri.substring(sep + Folder.SEPARATOR_LENGTH);
			String folderName = workUri.substring(0, sep);
			log.debug("Looking for name: " + name + " in folder: " + folderName);
			RepoFolder folder = getFolder(folderName, false);
			if (folder != null) {
				res = findByName(persistentClass, folder, name, required);
			} else {
				log.debug("No folder: " + folderName);
			}
		}
		
		if (res == null) {
			if (log.isDebugEnabled()) {
				log.debug("Resource of type " + persistentClass.getName() 
						+ " not found at " + uri);
			}
			
			if (required) {
				String quotedURI = "\"" + uri + "\"";
				throw new JSResourceNotFoundException("jsexception.resource.of.type.not.found", 
						new Object[] {quotedURI, persistentClass});
			}
		}
		
		return res;
	}

	protected RepoResource findByName(Class persistentClass, RepoFolder folder, String name, boolean required) {
		DetachedCriteria criteria = resourceNameCriteria(persistentClass, folder, name);
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
		List resourceList = getHibernateTemplate().findByCriteria(criteria);
		
		RepoResource res;
		if (resourceList.isEmpty()) {
			if (required) {
				String uri = "\"" + folder.getURI() + Folder.SEPARATOR + name + "\"";
				throw new JSResourceNotFoundException("jsexception.resource.of.type.not.found", new Object[] {uri, persistentClass});
			}

			res = null;
		}
		else {
			res = (RepoResource) resourceList.get(0);
		}
		
		return res;
	}

	protected DetachedCriteria resourceNameCriteria(Class persistentClass,
			RepoFolder folder, String name) {
		DetachedCriteria criteria = DetachedCriteria.forClass(persistentClass);
		criteria.add(Restrictions.naturalId().set("name", name).set("parent", folder));
		return criteria;
	}

	protected boolean resourceExists(RepoFolder folder, String name, Class resourceType) {
		Class persistentClass = resourcePersistentClass(resourceType);
		DetachedCriteria criteria = resourceNameCriteria(persistentClass, folder, name);
		criteria.setProjection(Projections.rowCount());
        criteria.getExecutableCriteria(getSession()).setCacheable(true);
		List countList = getHibernateTemplate().findByCriteria(criteria);
		int count = ((Integer) countList.get(0)).intValue();		
		return count > 0;
	}
	
	protected long findResourceId(String uri, Class resourceType) {
    	String folderPath = RepositoryUtils.getParentPath(uri);
		if (folderPath == null) {
			throw new JSException("Invalid resource path " + uri);
		}
		
		RepoFolder folder = getFolder(folderPath, true);
		
		String resourceName = RepositoryUtils.getName(uri);
		Class persistentClass = resourcePersistentClass(resourceType);
		
		DetachedCriteria criteria = resourceNameCriteria(persistentClass, folder, resourceName);
		criteria.setProjection(Projections.id());
		List<Long> idList = getHibernateTemplate().findByCriteria(criteria);
		if (idList == null || idList.isEmpty()) {
			throw new JSException("Resource of type " + resourceType.getName() + " not found at " + uri);
		}
		
		if (idList.size() > 1) {
			// should not get here, but safer to check
			throw new RuntimeException("Found " + idList.size() 
					+ " IDs for resource of type " + resourceType.getName() + " at " + uri);
		}
		
		return idList.get(0);
	}
	
/*	
	protected boolean resourceDisplayNameExists(String parentUri, String displayName) {
		FilterCriteria criteria = FilterCriteria.createFilter();
		criteria.addFilterElement(FilterCriteria.createParentFolderFilter(parentUri));

		List resources = loadResourcesList(null, criteria); 
		for (int i=0; i<resources.size(); i++) {
			ResourceLookupImpl res = (ResourceLookupImpl)resources.get(i);
			if (displayName.equalsIgnoreCase(res.getLabel())) {
				return true;                   
			}
		}
		return false;
	}	
	
*/
	protected boolean folderExists(RepoFolder parent, String name) {
		String uri;
		if (parent.isRoot()) {
			uri = Folder.SEPARATOR + name;
		} else {
			uri = parent.getURI() + Folder.SEPARATOR + name;
		}
		return folderExists(uri);
	}

/*	
	protected boolean folderDisplayNameExists(String parentUri, String displayName) {
		List repoFolderList = getSubFolders(null, parentUri);
		for (int i=0; i<repoFolderList.size(); i++) {
			FolderImpl repoFolder = (FolderImpl)repoFolderList.get(i);
			if (displayName.equalsIgnoreCase(repoFolder.getLabel())) {
				return true;                   
			}
		}
		return false;
	}	
*/
	protected RepoResource createPersistentResource(Class persistentClass) {
		try {
			RepoResource repo = (RepoResource) persistentClass.newInstance();
			repo.setCreationDate(getOperationTimestamp());
			repo.setUpdateDate(getOperationTimestamp());
			return repo;
		} catch (InstantiationException e) {
			log.fatal("Error instantiating persistent resource", e);
			throw new JSExceptionWrapper(e);
		} catch (IllegalAccessException e) {
			log.fatal("Error instantiating persistent resource", e);
			throw new JSExceptionWrapper(e);
		}
	}

	protected RepoResource getRepositoryReference(RepoResource owner, Resource res) {
		Class persistentClass = getPersistentClassMappings().getImplementationClass(res.getClass());
		if (persistentClass == null) {
			String quotedClass = "\"" + res.getClass().getName() + "\"";
			throw new JSException("jsexception.no.persistent.class.mapped.to", new Object[] {quotedClass});
		}
		
		RepoResource repo = null;
		RepoFolder folder = owner.getChildrenFolder();
		if (res.isNew()) {
			//if a local resource with the same name already exists it will be orphaned and deleted
			boolean tempName = folder != null && !folder.isNew() && resourceExists(folder, res.getName(), null);
			repo = createPersistentResource(persistentClass);
			if (tempName) {
				tempNameResources().add(repo);
			}
		} else {
			if (folder != null && !folder.isNew()) {
				repo = findByName(persistentClass, folder, res.getName(), false);
			}
			if (repo == null) {
				String quotedResource = "\"" + res.getURIString() + "\"";
				throw new JSException("jsexception.resource.does.not.exist", new Object[] {quotedResource});
			}
		}

		return repo;
	}
	
	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver#getReference(com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource, com.jaspersoft.jasperserver.api.metadata.common.domain.Resource, java.lang.Class)
	 */
    @Transactional(propagation = Propagation.REQUIRED)
	public RepoResource getReference(RepoResource owner, ResourceReference resourceRef, Class persistentReferenceClass) {
		if (resourceRef == null) {
			return null;
		}
		RepoResource repoRes;
		if (resourceRef.isLocal()) {
			repoRes = getReference(owner, resourceRef.getLocalResource(), persistentReferenceClass);
		} else {
			repoRes = findByURI(persistentReferenceClass, resourceRef.getReferenceURI(), true);
		}
		return repoRes;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public RepoResource getReference(RepoResource owner, Resource resource, Class persistentReferenceClass) {
		return getReference(owner, resource, persistentReferenceClass, this);
	}
	
	protected RepoResource getReference(RepoResource owner, 
			Resource resource, Class persistentReferenceClass,
			ReferenceResolver referenceResolver) {
		if (resource == null) {
			return null;
		}
		
		RepoResource repoRes = getRepositoryReference(owner, resource);

		RepoFolder local = owner.getChildrenFolder();
		if (local == null) {
			if (log.isInfoEnabled()) {
				log.info("Creating children folder for " + owner.getResourceURI());
			}
			
			local = createNewRepoFolder();
			local.setName(getChildrenFolderName(owner.getName()));
			local.setLabel(owner.getLabel());
			local.setDescription(owner.getDescription());
			local.setParent(owner.getParent());
			local.setHidden(true);
			local.refreshURI(this);
			owner.setChildrenFolder(local);
		}

		owner.addNewChild(repoRes);
		repoRes.copyFromClient(resource, referenceResolver);
		
		if (tempNameResources().contains(repoRes)) {
			repoRes.setName(TEMP_NAME_PREFIX + repoRes.getName());
			RepoFolder childrenFolder = repoRes.getChildrenFolder();
			if (childrenFolder != null) {
				childrenFolder.setName(TEMP_NAME_PREFIX + childrenFolder.getName());
				refreshFolderPaths(childrenFolder);
			}
		}
		
		return repoRes;
	}

	protected void refreshFolderPaths(RepoFolder folder) {
		// refreshing recursively using a queue
		LinkedList<RepoFolder> folders = new LinkedList<RepoFolder>();
		folders.addLast(folder);
		
		while (!folders.isEmpty()) {
			RepoFolder aFolder = folders.removeFirst();
			aFolder.refreshURI(this);
			
			Set resources = aFolder.getChildren();
			if (resources != null && !resources.isEmpty()) {
				for (Iterator it = resources.iterator(); it.hasNext();) {
					RepoResource child = (RepoResource) it.next();
					RepoFolder grandChildrenFolder = child.getChildrenFolder();
					if (grandChildrenFolder != null) {
						folders.addLast(grandChildrenFolder);
					}
				}
			}
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List getAllFolders(final ExecutionContext context) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadAllSubfolders(context, null);
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public int getFoldersCount(final String parentURI) {
        return (Integer) executeCallback(new DaoCallback() {
            public Object execute() {
                DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
                criteria.add(Restrictions.eq("hidden", Boolean.FALSE));
                criteria.setProjection(Projections.rowCount());
                if (parentURI != null && !Folder.SEPARATOR.equals(parentURI)) {
                    criteria.add(Restrictions.or((parentURI.contains("%")) ? Restrictions.eq("URI", parentURI) :
                                    Restrictions.like("URI", parentURI), Restrictions.like("URI", parentURI + "/%")
                    ));
                }
                return new BasicTransformer().transformToCount(getHibernateTemplate().findByCriteria(criteria));
            }
        });
    }

    protected List loadAllSubfolders(ExecutionContext context, String parentURI) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RepoFolder.class);
		criteria.add(Restrictions.eq("hidden", Boolean.FALSE));
		
		if (parentURI != null && !Folder.SEPARATOR.equals(parentURI)) {
			criteria.add(Restrictions.or(
                (parentURI.indexOf("%") == -1) ?
                    Restrictions.eq("URI", parentURI) :
                    Restrictions.like("URI", parentURI),
				Restrictions.like("URI", parentURI + "/%")
			));
		}

        criteria.getExecutableCriteria(getSession()).setCacheable(true);
        List repoFolders = getHibernateTemplate().findByCriteria(criteria);
		List folders = new ArrayList(repoFolders.size());
		for (Iterator iter = repoFolders.iterator(); iter.hasNext();) {
			RepoFolder repoFolder = (RepoFolder) iter.next();
			Folder folder = repoFolder.toClient();
			folders.add(folder);
		}
		
		SortingUtils.sortFoldersByURI(getCollator(context), folders);
		
		return folders;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List getAllSubfolders(final ExecutionContext context, final String parentURI) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				return loadAllSubfolders(context, parentURI);
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List getSubFolders(final ExecutionContext context, final String folderURI) {
		return (List) executeCallback(new DaoCallback() {
			public Object execute() {
				final RepoFolder folder = getFolder(folderURI, false);
				List subfolders;
				if (folder == null || folder.getSubFolders() == null) {
					//return empty list for non-existing folder
					subfolders = new ArrayList();
				} else {
					List folders = getHibernateTemplate().executeFind(new HibernateCallback() {
						public Object doInHibernate(Session session) throws HibernateException, SQLException {
							return session.createFilter(folder.getSubFolders(), "where hidden = false").list();
						}
					});

					subfolders = new ArrayList(folders.size());
					for (Iterator it = folders.iterator(); it.hasNext();) {
						RepoFolder repoFolder = (RepoFolder) it.next();
						subfolders.add(repoFolder.toClient());
					}
					
					SortingUtils.sortFoldersByName(getCollator(context), subfolders);
				}
				return subfolders;
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public FileResourceData getResourceData(ExecutionContext context, final String uri) throws JSResourceNotFoundException {
		return (FileResourceData) executeCallback(new DaoCallback() {
			public Object execute() {
				RepoFileResource res = (RepoFileResource) findByURI(RepoFileResource.class, uri, true);
				while (res.isFileReference()) {
					res = res.getReference();
				}
				return res.copyData();
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public FileResourceData getContentResourceData(ExecutionContext context, final String uri) throws JSResourceNotFoundException {
		return (FileResourceData) executeCallback(new DaoCallback() {
			public Object execute() {
				ContentRepoFileResource res = (ContentRepoFileResource) findByURI(ContentRepoFileResource.class, uri, true);
				return res.copyData();
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public RepoResource getExternalReference(String uri, Class persistentReferenceClass) {
		return findByURI(persistentReferenceClass, uri, true);
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public RepoResource getPersistentReference(String uri, Class clientReferenceClass) {
		return findByURI(resourcePersistentClass(clientReferenceClass), uri, true);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteResourceNoFlush(ExecutionContext context, final String uri) {
        deleteResource(context, uri, false);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteResource(ExecutionContext context, final String uri) {
        deleteResource(context, uri, true);
	}

    public void deleteResource(ExecutionContext context, final String uri, boolean flush) {
        executeWriteCallback(new DaoCallback() {
            public Object execute() {
                deleteResource(uri);
                return null;
            }
        }, flush);
    }

    protected void deleteResource(String uri) {
		RepoResource repoResource = findByURI(RepoResource.class, uri, true);
        auditResourceActivity("deleteResource", uri, repoResource.getClientType().getName());
		getHibernateTemplate().delete(repoResource);
        for (Iterator it = getRepositoryListeners().iterator(); it.hasNext();) {
            RepositoryListener listener = (RepositoryListener) it.next();
            if (listener instanceof RepositoryEventListener) {
                ((RepositoryEventListener)listener).onResourceDelete(repoResource.getClientType(), uri);
            }
        }
        closeAuditEvent("deleteResource");
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void deleteFolder(ExecutionContext context, final String uri) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				deleteFolder(uri);
				return null;
			}
		});
	}

	protected void deleteFolder(String uri) {
        auditFolderActivity("deleteFolder", uri);
		RepoFolder folder = getFolder(uri, true);
		if (folder.isRoot()) {
			throw new JSException("jsexception.folder.delete.root");
		}
		getHibernateTemplate().delete(folder);
        closeAuditEvent("deleteFolder");
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void delete(ExecutionContext context, final String[] resourceURIs, final String[] folderURIs) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				if (resourceURIs != null && resourceURIs.length > 0) {
					for (int i = 0; i < resourceURIs.length; i++) {
						deleteResource(resourceURIs[i]);
					}
				}

				if (folderURIs != null && folderURIs.length > 0) {
					for (int i = 0; i < folderURIs.length; i++) {
						deleteFolder(folderURIs[i]);
					}
				}
				
				return null;
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public Folder getFolder(ExecutionContext context, final String uri) {

        return getFolderUnsecure(context, uri);
    }

	public boolean folderExists(ExecutionContext context, String uri)
	{
		return folderExists(uri); 
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public Folder getFolderUnsecure(ExecutionContext context, final String uri) {
		return (Folder) executeCallback(new DaoCallback() {
			public Object execute() {
				RepoFolder repoFolder = getFolder(uri, false);
				Folder folder = repoFolder == null ? null : repoFolder.toClient();
				return folder;
			}
		});
	}

	protected boolean folderExists(String uri) {
		return getFolder(uri, false) != null;
	}

	public String getChildrenFolderName(String resourceName)
	{
		return resourceName + CHILDREN_FOLDER_SUFFIX;
	}

	public CollatorFactory getCollatorFactory() {
		return collatorFactory;
	}

	public void setCollatorFactory(CollatorFactory collatorFactory) {
		this.collatorFactory = collatorFactory;
	}
	
	protected Collator getCollator(ExecutionContext context) {
		return getCollatorFactory().getCollator(context);
	}

	protected boolean pathExists(String uri) {
		return resourceExists(uri) || folderExists(uri);
	}
	
	public boolean repositoryPathExists(ExecutionContext context, String uri) {
		return pathExists(uri);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService#hideFolder(java.lang.String)
	 */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void hideFolder(String uri) {
		setFolderHiddenFlag(uri, true);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService#unhideFolder(java.lang.String)
	 */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void unhideFolder(String uri) {
		setFolderHiddenFlag(uri, false);
	}

	private void setFolderHiddenFlag(String uri, boolean hidden) {
		RepoFolder folder = getFolder(uri, false);
		if (folder != null && folder.isHidden() != hidden) {
			folder.setHidden(hidden);
            validateResourceUriLength(folder.getURI().length());
            getHibernateTemplate().saveOrUpdate(folder);
		}
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void moveFolder(ExecutionContext context, final String sourceURI,
			final String destinationFolderURI) {

		executeWriteCallback(new DaoCallback() {
			public Object execute() {
                auditFolderCopyAndMove("moveFolder", sourceURI, destinationFolderURI);
				RepoFolder source = getFolder(sourceURI, true);
				RepoFolder dest = getFolder(destinationFolderURI, true);
				
				if (isAncestorOrEqual(source.getResourceURI(), dest.getResourceURI())) {
					throw new JSException("jsexception.move.folder.source.uri.ancestor.destination", 
							new Object[]{sourceURI, destinationFolderURI});
				}
				if (source.getParent().getResourceURI().equals(dest.getResourceURI())) {
					throw new JSException("jsexception.move.folder.to.same.folder",
							new Object[]{sourceURI, destinationFolderURI});
				}
				if (nameExistsInFolder(dest, source.getName())) {
					throw new JSException("jsexception.move.folder.path.already.exists",
							new Object[]{sourceURI, destinationFolderURI});
				}
                validateResourceUriLength(source.getName().length() + destinationFolderURI.length());

				if (log.isDebugEnabled()) {
					log.debug("Moving folder " + source.getResourceURI() + " to " + dest.getResourceURI());
				}
				
				source.moveTo(dest, HibernateRepositoryServiceImpl.this);
                closeAuditEvent("moveFolder");
				return null;
			}
		});
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void moveResource(ExecutionContext context, final String sourceURI,
			final String destinationFolderURI) {
		executeWriteCallback(new DaoCallback() {
			public Object execute() {
				RepoResource source = findByURI(RepoResource.class, sourceURI, true);
				RepoFolder dest = getFolder(destinationFolderURI, true);

                auditResourceCopyAndMove("moveResource", sourceURI, destinationFolderURI, source.getClientType().getName());
				if (source.getParent().getResourceURI().equals(dest.getResourceURI())) {
					throw new JSException("jsexception.move.resource.to.same.folder",
							new Object[]{sourceURI, destinationFolderURI});
				}
				if (nameExistsInFolder(dest, source.getName())) {
					throw new JSException("jsexception.move.resource.path.already.exists",
							new Object[]{sourceURI, destinationFolderURI});
				}
    			if (log.isDebugEnabled()) {
					log.debug("Moving resource " + source.getResourceURI() + " to " + dest.getResourceURI());
				}
				
				source.moveTo(dest, HibernateRepositoryServiceImpl.this);
                closeAuditEvent("moveResource");
				return null;
			}
		});
	}
	
	protected boolean isAncestorOrEqual(String ancestorURI, String childURI) {
		return RepositoryUtils.isAncestorOrEqual(ancestorURI, childURI);
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void update(Object repoObject) {
        if (repoObject instanceof RepoResourceBase)
        {
            RepoResourceBase repoResource = (RepoResourceBase) repoObject;
            validateResourceUriLength(repoResource.getResourceURI().length());
        }
    	getHibernateTemplate().update(repoObject);
	}

	public List getRepositoryListeners() {
		return repositoryListeners;
	}

	public void setRepositoryListeners(List repositoryListeners) {
		this.repositoryListeners = repositoryListeners;
	}

	protected boolean nameExistsInFolder(RepoFolder folder, String name) {
		return resourceExists(folder, name, null)
				|| folderExists(folder, name);
	}

	public void folderMoved(RepoFolder folder, String oldBaseURI,
			String newBaseURI) {
		String newURI = folder.getResourceURI();
		String oldURI = getMovedOldURI(newURI, oldBaseURI, newBaseURI);
		
		if (log.isDebugEnabled()) {
			log.debug("Folder " + oldURI + " has been moved to " + newURI);
		}
		
		FolderMoveEvent folderMove = new FolderMoveEvent(oldBaseURI, newBaseURI, oldURI, newURI);
		for (Iterator it = getRepositoryListeners().iterator(); it.hasNext();) {
			RepositoryListener listener = (RepositoryListener) it.next();
			listener.folderMoved(folderMove);
		}
	}

	public void resourceMoved(RepoResource resource, String oldBaseURI,
			String newBaseURI) {
		String newURI = resource.getResourceURI();
		String oldURI = getMovedOldURI(newURI, oldBaseURI, newBaseURI);
		Class type = resource.getClientType();
		
		if (log.isDebugEnabled()) {
			log.debug("Resource " + oldURI + " has been moved to " + newURI);
		}
		
		ResourceMoveEvent resourceMove = new ResourceMoveEvent(type, oldBaseURI, newBaseURI, oldURI, newURI);
		for (Iterator it = getRepositoryListeners().iterator(); it.hasNext();) {
			RepositoryListener listener = (RepositoryListener) it.next();
			listener.resourceMoved(resourceMove);
		}
	}

	protected String getMovedOldURI(String newURI, String oldBaseURI,
			String newBaseURI) {
		if (!RepositoryUtils.isAncestorOrEqual(newBaseURI, newURI)) {
			throw new JSException("New folder URI " + newURI + " does not match new base URI " + newBaseURI);
		}
		
		String newPostBaseURI;
		if (newBaseURI.equals(Folder.SEPARATOR)) {
			newPostBaseURI = newURI;
		} else {
			newPostBaseURI = newURI.substring(newBaseURI.length());
		}
		
		String oldURI;
		if (oldBaseURI.equals(Folder.SEPARATOR)) {
			oldURI = newPostBaseURI;
		} else {
			oldURI = oldBaseURI + newPostBaseURI;
		}
		return oldURI;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.READ_COMMITTED)
	public Resource copyResource(ExecutionContext context,
			final String sourceURI, final String destinationURI) {
		initTempNameResources();
		try {
			Resource copy = (Resource) executeWriteCallback(new DaoCallback() {
				public Object execute() {
					RepoResource repoCopy = copyResource(sourceURI,
							destinationURI);
					return repoCopy.toClient(resourceFactory);
				}
			});
			return copy;
		} finally {
			resetTempNameResources();
		}
	}

	protected RepoResource copyResource(final String sourceURI,
			final String destinationURI) {
		RepoResource sourceRepoResource = findByURI(RepoResource.class, sourceURI, true);
        auditResourceCopyAndMove("copyResource", sourceURI, destinationURI, sourceRepoResource.getClientType().getName());        
		String copyURI = getCopyURI(destinationURI, true);
		RepoResource repoCopy = copyResource(sourceRepoResource, copyURI);
        closeAuditEvent("copyResource");
		return repoCopy;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.READ_COMMITTED)
	public void copyResources(ExecutionContext context,
			final String[] resources,
			final String destinationFolder) {
		initTempNameResources();
		try {
			executeWriteCallback(new DaoCallback() {
				public Object execute() {
					copyResources(resources, destinationFolder);
					return null;
				}
			});
		} finally {
			resetTempNameResources();
		}
	}

	protected void copyResources(String[] sourceURIs, String destinationFolder) {
		RepoResource[] sources = new RepoResource[sourceURIs.length];
		for (int i = 0; i < sourceURIs.length; i++) {
			sources[i] = findByURI(RepoResource.class, sourceURIs[i], true);
		}
		
		RepoFolder destination = getFolder(destinationFolder, true);
		
		MultiResourceCopier copier = new MultiResourceCopier(sources, destination);
		copier.copy();
	}

	protected String getCopyURI(String destinationURI, boolean resource) {
		String parentURI = RepositoryUtils.getParentPath(destinationURI);
		RepoFolder parent = getFolder(parentURI, true);
		lockReadFolder(parent);
		String destinationName = RepositoryUtils.getName(destinationURI);
		
		String copyURI = makeCopyURI(parent, destinationName, resource);
		
		if (log.isDebugEnabled()) {
			log.debug("Using " + copyURI + " for copy at " + destinationURI);
		}
		
		return copyURI;
	}

	protected String makeCopyURI(RepoFolder destination,
			String name, boolean resource) {
		String copyURI;
		if (nameExistsInFolder(destination, name)) {
			int maxNameLenght = RESOURCE_NAME_LENGHT;
			if (resource) {
				maxNameLenght -= CHILDREN_FOLDER_SUFFIX.length();
			}
			
			String baseName = name;
			String copyName;
			int counter = 1;
			do {
				copyName = baseName + COPY_GENERATED_NAME_SEPARATOR + counter;
				if (copyName.length() > maxNameLenght) {
					String truncatedBaseName = baseName.substring(0, 
							baseName.length() - copyName.length() + maxNameLenght);
					copyName = truncatedBaseName
							+ COPY_GENERATED_NAME_SEPARATOR + counter;
				}
				++counter;
			} while (nameExistsInFolder(destination, copyName));
			
			copyURI = RepositoryUtils.concatenatePath(destination.getURI(), 
					copyName);
		} else {
			copyURI = RepositoryUtils.concatenatePath(destination.getURI(), 
					name);
		}
		return copyURI;
	}
	
	protected RepoResource copyResource(RepoResource resource, String copyURI) {
		Resource copy = getClientClone(resource);
		copy.setURIString(copyURI);
		resourceCopied(resource.getResourceURI(), copy);
		
		RepoResource repoCopy = getRepoResource(copy);
		repoCopy.copyFromClient(copy, this);
        repoCopy.setUpdateDate(resource.getUpdateDate());
    	getHibernateTemplate().save(repoCopy);
		
		return repoCopy;
	}

	protected void resourceCopied(String sourceUri, Resource copy) {
		if (log.isDebugEnabled()) {
			log.debug("Resource " + copy.getURIString() + " copied from " + sourceUri);
		}
		
		ResourceCopiedEvent event = new ResourceCopiedEvent(sourceUri, copy);
		for (Iterator it = getRepositoryListeners().iterator(); it.hasNext();) {
			RepositoryListener listener = (RepositoryListener) it.next();
			listener.resourceCopied(event);
		}
	}

	protected Resource getClientClone(RepoResource resource) {
		Resource copy = (Resource) resource.toClient(resourceFactory, CLIENT_CLONE_OPTIONS);
		return copy;
	}

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.READ_COMMITTED)
	public Folder copyFolder(ExecutionContext context,
			final String sourceURI, final String destinationURI) {
		initTempNameResources();
        auditFolderCopyAndMove("copyFolder", sourceURI, destinationURI);
		try {
			Folder copy = (Folder) executeWriteCallback(new DaoCallback() {
				public Object execute() {
					RepoFolder folderCopy = copyFolder(sourceURI, destinationURI);
					return folderCopy.toClient();
				}
			});
            closeAuditEvent("copyFolder");
			return copy;
		} finally {
			resetTempNameResources();
		}
	}
	
	protected RepoFolder copyFolder(String sourceURI, String destinationURI) {
		RepoFolder repoSource = getFolder(sourceURI, true);
		
		String copyURI = getCopyURI(destinationURI, false);
		RecursiveCopier copier = new RecursiveCopier(repoSource, copyURI);
		copier.copy();
		return copier.getCopiedRootFolder();
	}

    /**
     * Returns maximum valid folder URI length
     *
     * The method is used for futher lazy initialization of resourceUriLength.
     * Loads sessionFactory bean from application context as LocalSessionFactoryBean class instance.
     * Bean is needed for getting hibernate Configuration object which stores configuration metadata.
     *
     * @return maximum valid folder URI length.
     *
     *
     * */
    private int getMaxUriLength() {
        if (resourceUriLength >=0){
            return resourceUriLength;
        }

        //If resourceUriLength is not initialized yet
        LocalSessionFactoryBean localSessionFactoryBean = (LocalSessionFactoryBean) applicationContext.getBean("&sessionFactory");

        String entityName = RepoFolder.class.getCanonicalName();
        PersistentClass persistentClass = localSessionFactoryBean.getConfiguration().getClassMapping(entityName);

        resourceUriLength = persistentClass.getTable().getColumn(new Column("URI")).getLength();
        return resourceUriLength;
    }
    
    private void validateResourceUriLength(int uriLength) {
        if (uriLength > getMaxUriLength()) {
            throw new JSException("jsexception.folder.too.long.uri", new Object[] {resourceUriLength});
        }
    }

	protected abstract class BaseResourceCopier implements ReferenceResolver {
		
		protected final LinkedHashMap copiedResources = new LinkedHashMap();
		private final Set copyingResourceStack = new HashSet();

		protected abstract boolean isCopiedResource(String uri);

		protected abstract RepoFolder getCopyParent(RepoResourceBase resource);
		
		protected abstract String getCopyURI(String uri);

		protected abstract RepoResource loadResourceToCopy(String uri,
				Class persistentClass);
		
		protected RepoResource copyResource(RepoResource resource) {
			String copyURI = getCopyURI(resource.getResourceURI());
			RepoResource resourceCopy = (RepoResource) copiedResources.get(copyURI);
			if (resourceCopy == null) {
				resourceCopy = copyResource(resource, copyURI);
			}
			return resourceCopy;
		}
		
		protected RepoResource copyResource(RepoResource resource, String copyURI) {
            auditResourceCopyAndMove("copyResource", resource.getResourceURI(), copyURI, resource.getClientType().getName());
			if (log.isDebugEnabled()) {
				log.debug("Creating copy of resource " + resource.getResourceURI() + " at " + copyURI);
			}
			
			if (!copyingResourceStack.add(copyURI)) {
				throw new JSException("jsexception.copy.resource.circular.reference", 
						new Object[]{resource.getResourceURI()});
			}
			try {
				Resource copy = getClientClone(resource);
				copy.setURIString(copyURI);
				resourceCopied(resource.getResourceURI(), copy);
				
				Class persistentClass = resourcePersistentClass(copy.getClass());
				RepoResource resourceCopy = createPersistentResource(persistentClass);
				RepoFolder copyParent = getCopyParent(resource);
				resourceCopy.setParent(copyParent);
				resourceCopy.copyFromClient(copy, this);
                resourceCopy.setUpdateDate(resource.getUpdateDate());
				
				copiedResources.put(copyURI, resourceCopy);

                closeAuditEvent("copyResource");
				return resourceCopy;
			} finally {
				copyingResourceStack.remove(copyURI);
			}
		}

		protected RepoResource getCopiedResourceReference(String uri, Class persistentClass) {
			String copyURI = getCopyURI(uri);
			RepoResource resourceCopy = (RepoResource) copiedResources.get(copyURI);
			if (resourceCopy == null) {
				RepoResource resource = loadResourceToCopy(uri, persistentClass);
				resourceCopy = copyResource(resource, copyURI);
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Reference to " + uri + " has been changed to " + copyURI);
			}
			
			return resourceCopy;
		}
		
		protected RepoResource getCopyReference(String uri, Class persistentReferenceClass) {
			RepoResource reference;
			if (isCopiedResource(uri)) {
				reference = getCopiedResourceReference(uri, persistentReferenceClass);
			} else {
				reference = findByURI(persistentReferenceClass, uri, true);
			}
			return reference;
		}
		
		public RepoResource getExternalReference(String uri, Class persistentReferenceClass) {
			return getCopyReference(uri, persistentReferenceClass);
		}

		public RepoResource getPersistentReference(String uri, Class clientReferenceClass) {
			return getCopyReference(uri, resourcePersistentClass(clientReferenceClass));
		}

		public RepoResource getReference(RepoResource owner,
				ResourceReference resourceReference, Class persistentReferenceClass) {
			if (resourceReference == null) {
				return null;
			}
			RepoResource repoRes;
			if (resourceReference.isLocal()) {
				repoRes = getReference(owner, resourceReference.getLocalResource(), persistentReferenceClass);
			} else {
				repoRes = getCopyReference(resourceReference.getReferenceURI(), persistentReferenceClass);
			}
			return repoRes;
		}

		public RepoResource getReference(RepoResource owner, 
				Resource resource, Class persistentReferenceClass) {
			return HibernateRepositoryServiceImpl.this.getReference(owner, 
					resource, persistentReferenceClass, this);
		}
	}
    
    protected class RecursiveCopier extends BaseResourceCopier {
		
		private final RepoFolder sourceRoot;
		private final String destinationRootURI;
		
		private final LinkedHashMap copiedFolders = new LinkedHashMap();
		
		public RecursiveCopier(RepoFolder sourceRoot, String destinationRootURI) {
			this.sourceRoot = sourceRoot;
			this.destinationRootURI = destinationRootURI;
		}

		public void copy() {
			copyFolderRecursively(sourceRoot);
			save();
		}
		
		protected void save() {
			HibernateTemplate template = getHibernateTemplate();
			
			for (Iterator it = copiedFolders.values().iterator(); it.hasNext();) {
				RepoFolder folder = (RepoFolder) it.next();
                validateResourceUriLength(folder.getResourceURI().length());
				template.save(folder);
			}
			
			for (Iterator it = copiedResources.values().iterator(); it.hasNext();) {
				RepoResource resource = (RepoResource) it.next();
				template.save(resource);
			}
		}

		protected void copyFolderRecursively(RepoFolder folder) {
			if (log.isDebugEnabled()) {
				log.debug("Recursively copying folder " + folder.getURI());
			}
			
			copyFolder(folder);

			getHibernateTemplate().refresh(folder);         // This ensures that folder object is synchronized
															// with database.  Synchronization populates in-memory
															// (persistence context) folder with the correct sub-folders

			Set subfolders = folder.getSubFolders();
			if (subfolders != null) {
				for (Iterator it = subfolders.iterator(); it.hasNext();) {
					RepoFolder subfolder = (RepoFolder) it.next();
					if (!subfolder.isHidden() && getSecurityChecker().isFolderReadable(subfolder.getURI())) {
						copyFolderRecursively(subfolder);
					}
				}
			}
			
			Set resources = folder.getChildren();
			if (resources != null) {
				for (Iterator it = resources.iterator(); it.hasNext();) {
					RepoResource resource = (RepoResource) it.next();
					if (getSecurityChecker().isResourceReadable(resource.getResourceURI())) {
						copyResource(resource);
					}
				}
			}
		}

		protected RepoFolder copyFolder(RepoFolder folder) {
			String copyURI = getCopyURI(folder.getURI());
			RepoFolder folderCopy = (RepoFolder) copiedFolders.get(copyURI);
			if (folderCopy == null) {
				if (log.isDebugEnabled()) {
					log.debug("Creating copy of folder " + folder.getURI() + " at " + copyURI);
				}
				
				Folder copy = folder.toClient();
				copy.setVersion(Resource.VERSION_NEW);
				copy.setURIString(copyURI);
				
				folderCopy = createNewRepoFolder();
				RepoFolder copyParent = getCopyParent(folder);
    			folderCopy.set(copy, copyParent, HibernateRepositoryServiceImpl.this);
                folderCopy.setUpdateDate(folder.getUpdateDate());

				copiedFolders.put(copyURI, folderCopy);
			}
			
			return folderCopy;
		}

		protected String getCopyURI(String uri) {
			String copyURI;
			if (sourceRoot.getURI().equals(uri)) {
				copyURI = destinationRootURI;
			} else if (sourceRoot.isRoot()) {
				copyURI = destinationRootURI + uri;
			} else {
				copyURI = destinationRootURI + uri.substring(sourceRoot.getURI().length());
			}
			return copyURI;
		}
		
		protected RepoFolder getCopyParent(RepoResourceBase resource) {
			String uri = resource.getResourceURI();
			RepoFolder parent;
			if (sourceRoot.getURI().equals(uri)) {
				String destinationParentURI = RepositoryUtils.getParentPath(destinationRootURI);
				parent = getFolder(destinationParentURI, true);
			} else {
				String copyParentURI = getCopyURI(resource.getParent().getURI());
				parent = (RepoFolder) copiedFolders.get(copyParentURI);
				if (parent == null) {
					parent = copyFolder(resource.getParent());
				}
			}
			return parent;
		}

		protected boolean isCopiedResource(String uri) {
			return RepositoryUtils.isAncestorOrEqual(sourceRoot.getURI(), uri);
		}

		protected RepoResource loadResourceToCopy(String uri,
				Class persistentClass) {
			if (!getSecurityChecker().isResourceReadable(uri)) {
				throw new JSException("jsexception.copy.resource.referenced.not.readable", 
						new Object[]{uri});
			}
			return findByURI(persistentClass, uri, true);
		}
		
		public RepoFolder getCopiedRootFolder() {
			return (RepoFolder) copiedFolders.get(destinationRootURI);
		}
	}
	
	protected class MultiResourceCopier extends BaseResourceCopier {

		private final Map sourcesMap;
		private final RepoFolder destination;
		private final Map copyURIs = new HashMap();

		public MultiResourceCopier(RepoResource[] sources, RepoFolder destination) {
			this.sourcesMap = new LinkedHashMap();
			for (int i = 0; i < sources.length; i++) {
				RepoResource source = sources[i];
				sourcesMap.put(source.getResourceURI(), source);
			}
			
			this.destination = destination;
		}

		public void copy() {
			copyResources();
			save();
		}
		
		protected void copyResources() {
			for (Iterator it = sourcesMap.values().iterator(); it.hasNext();) {
				RepoResource resource = (RepoResource) it.next();
				copyResource(resource);
			}
		}

		protected void save() {
			HibernateTemplate template = getHibernateTemplate();
			for (Iterator it = copiedResources.values().iterator(); it.hasNext();) {
				RepoResource resource = (RepoResource) it.next();
				template.save(resource);
			}
		}
		
		protected RepoFolder getCopyParent(RepoResourceBase resource) {
			return destination;
		}

		protected String getCopyURI(String uri) {
			String copyURI = (String) copyURIs.get(uri);
			if (copyURI == null) {
				String name = RepositoryUtils.getName(uri);
				copyURI = makeCopyURI(destination, name, true);
				
				if (log.isDebugEnabled()) {
					log.debug("Using " + copyURI + " for copy of " + name 
							+ " at " + destination.getURI());
				}
				
				copyURIs.put(uri, copyURI);
			}
			return copyURI;
		}

		protected boolean isCopiedResource(String uri) {
			return sourcesMap.containsKey(uri);
		}

		protected RepoResource loadResourceToCopy(String uri,
				Class persistentClass) {
			return (RepoResource) sourcesMap.get(uri);
		}
	}

	public void lockPath(RepoFolder folder) {
        validateResourceUriLength(folder.getURI().length());
		if (!isLockFoldersOnPathChange()) {
			return;
		}

		HibernateTemplate template = getHibernateTemplate();
		RepoFolder parent = folder.getParent();
		if (parent != null && !parent.isNew()) {
			lockReadFolder(parent);
		}
		
		if (!folder.isNew()) {
			//lock the folder to indicate that its path has changed
			template.lock(folder, LockMode.UPGRADE);
		}
	}

	protected void lockReadFolder(RepoFolder folder) {
		HibernateTemplate template = getHibernateTemplate();
		Session session = template.getSessionFactory().getCurrentSession();
		LockMode folderLockMode = session.getCurrentLockMode(folder);
		if (LockMode.READ.equals(folderLockMode)) {
			String currentURI = folder.getURI();
			
			//refresh and lock the parent
			template.refresh(folder, LockMode.UPGRADE);
			
			//check whether the parent URI has changed
			if (!currentURI.equals(folder.getURI())) {
				throw new JSException("jsexception.folder.moved",
						new Object[] {currentURI, folder.getURI()});
			}
		}
	}
	
	public boolean isLockFoldersOnPathChange() {
		return lockFoldersOnPathChange;
	}

	public void setLockFoldersOnPathChange(boolean lockFoldersOnPathChange) {
		this.lockFoldersOnPathChange = lockFoldersOnPathChange;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver#getPersistentObject(java.lang.Object)
	 */
    @Transactional(propagation = Propagation.REQUIRED)
	public Object getPersistentObject(Object clientObject) {
		if (clientObject instanceof Folder) {
			return getFolder(((Folder) clientObject).getURIString(), false);
		} else if (clientObject instanceof Resource) {
			return findByURI(RepoResource.class, ((Resource) clientObject).getURIString(), false);
		}
		return null;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceLookup> getResources(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory,
            List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory, int current,
            int max) {
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory is null.");
        }

        SearchCriteria criteria;
        List<ResourceLookup> result;
        if (queryModificationEvaluator.useFullResource(context)) {
            // we have to get ids only and make additional call, see:
            // https://community.jboss.org/wiki/HibernateFAQ-AdvancedProblems#Hibernate_does_not_return_distinct_results_for_a_query_with_outer_join_fetching_enabled_for_a_collection_even_if_I_use_the_distinct_keyword
            searchCriteriaFactory = searchCriteriaFactory.newFactory(Resource.class.getName());
            criteria = searchCriteriaFactory.create(context, filters);
            criteria.addProjection(Projections.distinct(Projections.id()));
            searchCriteriaFactory.applySorter(context, criteria, sorter);

            List list  = getHibernateTemplate().findByCriteria(criteria, current, max);

            List<Long> ids = new ArrayList<Long>();
            ResultTransformer transformer = transformerFactory.createTransformer(filters, sorter);
            if (transformer != null) {
                ids.addAll(transformer.transformToIdList(list));
            } else {
                throw new IllegalArgumentException("Result transformer is null.");
            }

            result = getResourcesByIdList(ids, searchCriteriaFactory);
        } else {
            // just get the resources since no outer joins supposed to be here
            criteria = searchCriteriaFactory.create(context, filters);
            searchCriteriaFactory.applySorter(context, criteria, sorter);

            List<IdedRepoObject> list  = getHibernateTemplate().findByCriteria(criteria, current, max);
            result = toLookups(list, true);
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public int getResourcesCount(ExecutionContext context, SearchCriteriaFactory searchCriteriaFactory,
            List<SearchFilter> filters, SearchSorter sorter, TransformerFactory transformerFactory) {
        if (transformerFactory == null) {
            throw new IllegalArgumentException("Transformer factory is null.");
        }

        SearchCriteria criteria;
        if (queryModificationEvaluator.useFullResource(context)) {
            criteria = searchCriteriaFactory.newFactory(Resource.class.getName()).create(context, filters);
            criteria.setProjection(Projections.countDistinct("id"));
        } else {
            criteria = searchCriteriaFactory.create(context, filters);
            criteria.setProjection(Projections.rowCount());
        }

        List resourceList = getHibernateTemplate().findByCriteria(criteria);

        ResultTransformer transformer = transformerFactory.createTransformer(filters, sorter);
        if (transformer != null) {
            return transformer.transformToCount(resourceList);
        } else {
            throw new IllegalArgumentException("Result transformer is null.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceLookup> getDependentResources(ExecutionContext context,
                                                      String uri, SearchCriteriaFactory searchCriteriaFactory,
                                                      int current, int max) {

        final Resource resource = getResource(context, uri);

        if(resource == null) {
            throw new JSException("jsexception.resource.does.not.exist");
        }

        if(resource instanceof ReportDataSource) {
            //ReportDataSource can have dependent ReportUnit items
            final ReportDataSource dataSource = (ReportDataSource) resource;
            RepoResource repoDS = findByURI(RepoResource.class, uri, true);
            Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
            //TODO GetDependentResourcesIds query can be updated to handle ReportUnit objects too
            org.hibernate.Query query = session.getNamedQuery("GetDependentResourcesIds");
            query.setEntity("dependency", repoDS).setFirstResult(current);
            if(max > 0) {
                query.setMaxResults(max);
            }
            List<Long> depRes = query.list();
            List<ResourceLookup> depResLookup = getResourcesByIdList(depRes);
            return depResLookup;
//TODO delete if code above proves to work fine
//            SearchFilter filter = new SearchFilter() {
//                public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
//                    DetachedCriteria dsCriteria = criteria.createCriteria("dataSource", "ds");
//                    dsCriteria.add(Restrictions.eq("ds.name", dataSource.getName()));
//
//                    DetachedCriteria dsParent = dsCriteria.createCriteria("parent", "dsParent");
//                    dsParent.add(Restrictions.eq("dsParent.URI", dataSource.getParentFolder()));
//
//                    // will use it later on for sorting
//                    DetachedCriteria folderCriteria = criteria.createCriteria("parent", "f");
//                }
//            };
//
//            SearchSorter sorter = new SearchSorter() {
//                public void applyOrder(String type, ExecutionContext context, SearchCriteria criteria) {
//                    criteria.addOrder(Order.asc("f.URI"));
//                    criteria.addOrder(Order.asc("name"));
//                    // by default we retrieving only id's and to be able sort  we had to add property to select clause
//                    criteria.addProjection(Projections.property("f.URI"));
//                    criteria.addProjection(Projections.property("name"));
//                }
//            };
//            SearchCriteriaFactory criteriaFactory = searchCriteriaFactory.newFactory(ReportUnit.class.getName());
//            List<ResourceLookup> resources = getResources(
//                    context,
//                    criteriaFactory,
//                    Arrays.asList(filter),
//                    sorter,
//                    new BasicTransformerFactory(),
//                    current, max);
//
//            return resources;

        //TODO: replace 'exist' with right solution, it's hardcoded fix for CE version
        } else if(resource instanceof ReportUnit && exist("com.jaspersoft.ji.adhoc.DashboardResource")) {
            //ReportUnit can have dependent DashboardResource items
            final RepoResource repoResource = getRepoResource(resource);
            SearchFilter filter = new SearchFilter() {
                public void applyRestrictions(String type, ExecutionContext context, SearchCriteria criteria) {
                    DetachedCriteria resourcesCriteria = criteria.createCriteria("resources", "res");
                    resourcesCriteria.add(Restrictions.eq("res.id", repoResource.getId()));
                }
            };

            SearchSorter sorter = new SearchSorter() {
                @Override
                protected void addOrder(String type, ExecutionContext context, SearchCriteria criteria) {
                    criteria.addOrder(Order.asc("name")).addOrder(Order.asc("id"));
                }

                @Override
                protected void addProjection(String type, ExecutionContext context, SearchCriteria criteria) { }
            };

            SearchCriteriaFactory criteriaFactory = searchCriteriaFactory.newFactory("com.jaspersoft.ji.adhoc.DashboardResource");
            List<ResourceLookup> resources = getResources(
                    context,
                    criteriaFactory,
                    Arrays.asList(filter),
                    sorter,
                    new BasicTransformerFactory(),
                    current, max);

            return resources;
        } else {
            return null; //unsupported resource
        }

    }
    /*
    @Transactional(propagation = Propagation.REQUIRED)
	public List<ResourceLookup> getDomainDependentResources(ExecutionContext context,
                                                      String uri, SearchCriteriaFactory searchCriteriaFactory,
                                                      int current, int max) {
        final Resource resource = getResource(context, uri);

        if(resource == null) {
            throw new JSException("jsexception.resource.does.not.exist");
        }

        if(resource instanceof ReportDataSource) {
            final ReportDataSource dataSource = (ReportDataSource) resource;
            RepoResource repoDS = findByURI(RepoResource.class, uri, true);
            Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
            org.hibernate.Query query = session.getNamedQuery("GetDomainDependentResourcesIds");
            query.setEntity("dependency", repoDS).setFirstResult(current);
            if(max > 0) {
                query.setMaxResults(max);
            }
            List<Long> depRes = query.list();
            List<ResourceLookup> depResLookup = getResourcesByIdList(depRes);
            return depResLookup;
        } else {
        	return null; //unsupported resource
        }
    }
*/

    //TODO: replace this with  right solution
    //temporary solution
    private Boolean exist(String className){
        Boolean result;
        try {
            Class.forName(className);
            result = true;
        } catch( ClassNotFoundException e ) {
            result = false;
        }
        return result;
    }

    // used to have REQUIRES_NEW but that resulted in a deadlock on DB when called from 
    // EngineServiceImpl.autoUpdateJRXMLResource()/HibernateRepositoryCache.saveData()
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	public void replaceFileResourceData(final String uri, DataContainer data) {
		final long resourceId = findResourceId(uri, FileResource.class);
		
		if (log.isDebugEnabled()) {
			log.debug("replacing file resource data at " + uri);
		}
		
		final byte[] dataBytes = data.getData();
		
		// we're using a batch update so that the version does not get incremented.
		getHibernateTemplate().execute(new HibernateCallback<Void>() {
			public Void doInHibernate(Session session) throws HibernateException, SQLException {
				int count = session.getNamedQuery("FileResourceReplaceDataById").
						setLong("id", resourceId).
						setBinary("data", dataBytes).
						executeUpdate();
				
				if (log.isDebugEnabled()) {
					log.debug("update " + count + " records for resource " + uri);
				}
				
				if (count == 0) {
					throw new JSException("Failed to update file resource data at " + uri
							+ " with id " + resourceId);
				}
				
				if (count > 1) {
					// should not get here, but protect against any accidents
					throw new RuntimeException("Replacing file resource data at " + uri 
							+ " updated " + count + " records");
				}
				
				return null;
			}
		});
	}
    
    @Override
    public List<RepoResource> findRepoResources(ExecutionContext context, FilterCriteria criteria) {
        DetachedCriteria detachedCriteria = translateFilterToCriteria(criteria);
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }

    @Override
    public List<RepoFolder> findRepoFolders(ExecutionContext context, FilterCriteria criteria) {
        // enforce Folder search
        criteria.setFilterClass(Folder.class);
        DetachedCriteria detachedCriteria = translateFilterToCriteria(criteria);
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAll(ExecutionContext context, List<RepoResource> resources, List<RepoFolder> folders) {
        // delete resource first
        if (resources != null && !resources.isEmpty()) {
            getHibernateTemplate().deleteAll(resources);
        }
        // delete folders then
        if (folders != null && !folders.isEmpty()) {
            getHibernateTemplate().deleteAll(folders);
        }
    }
    
    	
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveOrUpdateAll(ExecutionContext context, List<RepoResource> resources, List<RepoFolder> folders) {
        // save folders first
        if (folders != null && !folders.isEmpty()) {
            getHibernateTemplate().saveOrUpdateAll(folders);
        }
        // save resource then
        if (resources != null && !resources.isEmpty()) {
            getHibernateTemplate().saveOrUpdateAll(resources);
        }
    }

    public QueryModificationEvaluator getQueryModificationEvaluator() {
        return queryModificationEvaluator;
    }

    public void setQueryModificationEvaluator(QueryModificationEvaluator queryModificationEvaluator) {
        this.queryModificationEvaluator = queryModificationEvaluator;
    }

    /**
     * Transform  resource lookup path to relative URIs
     * @param internalPath
     * @return  external path
     */
    public String transformPathToExternal(String internalPath) {
        return internalPath;
    }

    public HibernateSaveUpdateDeleteListener getHibernateSaveUpdateDeleteListener() {
        return hibernateSaveUpdateDeleteListener;
    }

    public void setHibernateSaveUpdateDeleteListener(HibernateSaveUpdateDeleteListener hibernateSaveUpdateDeleteListener) {
        this.hibernateSaveUpdateDeleteListener = hibernateSaveUpdateDeleteListener;
    }
}
