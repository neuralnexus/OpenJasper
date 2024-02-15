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
package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.ImportRunMonitor;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Folder;
import com.jaspersoft.jasperserver.api.metadata.common.domain.InternalURI;
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryUnsecure;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.client.ObjectPermissionImpl;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.ObjectPermissionRecipientIdentity;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoObjectPermission;
import com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.acl.AclEntry;
import org.springframework.security.acl.AclProvider;
import org.springframework.security.acl.basic.AclObjectIdentity;
import org.springframework.security.acl.basic.BasicAclDao;
import org.springframework.security.acl.basic.BasicAclEntry;
import org.springframework.security.acl.basic.BasicAclEntryCache;
import org.springframework.security.acl.basic.EffectiveAclsResolver;
import org.springframework.security.acl.basic.GrantedAuthorityEffectiveAclsResolver;
import org.springframework.security.acl.basic.SimpleAclEntry;
import org.springframework.security.acl.basic.cache.NullAclEntryCache;
import org.springframework.security.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author swood
 *
 */
public class ObjectPermissionServiceImpl extends HibernateDaoImpl implements
				BasicAclDao, AclProvider, AclService, ObjectPermissionService, ObjectPermissionServiceInternal,
				ApplicationContextAware, PersistentObjectResolver, InitializingBean {

	//

	protected static final Log log = LogFactory.getLog(ObjectPermissionServiceImpl.class);
    public static final String RECIPIENT_USED_FOR_INHERITANCE_MARKER = "___INHERITANCE_MARKER_ONLY___";

    protected static final String RESOURCE_URI_PREFIX = Resource.URI_PROTOCOL + ":";
    protected static final int RESOURCE_URI_PREFIX_LENGTH = RESOURCE_URI_PREFIX.length();

    /**
     * Marker added to the cache to indicate an AclObjectIdentity has no
     * corresponding BasicAclEntry[]s
     */
    static String RECIPIENT_FOR_CACHE_EMPTY = "RESERVED_RECIPIENT_NOBODY";

    private HibernateRepositoryService repoService;
    private UserAuthorityService userService;

	private ResourceFactory objectFactory;
	private ResourceFactory persistentClassFactory;

    private BasicAclEntryCache basicAclEntryCache = new NullAclEntryCache();
    private EffectiveAclsResolver effectiveAclsResolver = new GrantedAuthorityEffectiveAclsResolver();

    private ApplicationContext appContext;
    private AuditContext auditContext;

	/**
	 * @return Returns the repoService.
	 */
	public HibernateRepositoryService getRepositoryService() {
		return repoService;
	}

	/**
	 * @param repoService The repoService to set.
	 */
	public void setRepositoryService(HibernateRepositoryService repoService) {
		this.repoService = repoService;
	}

	/**
	 * @return Returns the userService.
	 */
	public UserAuthorityService getUserAuthorityService() {
		return userService;
	}

	/**
	 * @param userService The userService to set.
	 */
	public void setUserAuthorityService(UserAuthorityService userService) {
		this.userService = userService;
	}

	public ResourceFactory getObjectMappingFactory() {
		return objectFactory;
	}

	public void setObjectMappingFactory(ResourceFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	public ResourceFactory getPersistentClassFactory() {
		return persistentClassFactory;
	}

	public void setPersistentClassFactory(ResourceFactory persistentClassFactory) {
		this.persistentClassFactory = persistentClassFactory;
	}

    public void setBasicAclEntryCache(BasicAclEntryCache basicAclEntryCache) {
        this.basicAclEntryCache = basicAclEntryCache;
    }

    public BasicAclEntryCache getBasicAclEntryCache() {
        return basicAclEntryCache;
    }

    /**
	 * @return Returns the effectiveAclsResolver.
	 */
	public EffectiveAclsResolver getEffectiveAclsResolver() {
		return effectiveAclsResolver;
	}

	/**
	 * @param effectiveAclsResolver The effectiveAclsResolver to set.
	 */
	public void setEffectiveAclsResolver(EffectiveAclsResolver effectiveAclsResolver) {
		this.effectiveAclsResolver = effectiveAclsResolver;
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		// Not needed really?
		appContext = arg0;
	}

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    /**
     * Returns the ACLs associated with the requested
     * <code>AclObjectIdentity</code>.
     *
     * <P>
     * The {@link BasicAclEntry}s returned by this method will have
     * <code>String</code>-based recipients. This will not be a problem if you
     * are using the <code>GrantedAuthorityEffectiveAclsResolver</code>, which
     * is the default configured against <code>BasicAclProvider</code>.
     * </p>
     *
     * @param aclObjectIdentity for which ACL information is required (cannot
     *        be <code>null</code>)
     *
     * @return the ACLs that apply (without any <code>null</code>s inside the
     *         array), or <code>null</code> if not found or if an incompatible
     *         <code>AclObjectIdentity</code> was requested
     */
    public BasicAclEntry[] getAcls(String uri) {

       BasicAclEntry[] entries = checkImport(uri);
       if (entries != null) return entries;

        Resource res = ((RepositoryUnsecure)getRepositoryService()).getResourceLookupUnsecure(null, uri);
	if (res == null) {
		res = ((RepositoryUnsecure)getRepositoryService()).getFolderUnsecure(null, uri);
	}
	if (res != null)
	{
		return getAcls((InternalURI) res);
	}

	return null;
    }
   /**
     * Returns the ACLs associated with the requested
     * <code>AclObjectIdentity</code>.
     *
     * <P>
     * The {@link BasicAclEntry}s returned by this method will have
     * <code>String</code>-based recipients. This will not be a problem if you
     * are using the <code>GrantedAuthorityEffectiveAclsResolver</code>, which
     * is the default configured against <code>BasicAclProvider</code>.
     * </p>
     *
     * @param aclObjectIdentity for which ACL information is required (cannot
     *        be <code>null</code>)
     *
     * @return the ACLs that apply (without any <code>null</code>s inside the
     *         array), or <code>null</code> if not found or if an incompatible
     *         <code>AclObjectIdentity</code> was requested
     */
    public BasicAclEntry[] getAcls(InternalURI targetURI) {


        //Object obj = org.springframework.security.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //String username = "unknow";
        //if (obj instanceof org.springframework.security.userdetails.UserDetails) {
        //  username = ((org.springframework.security.userdetails.UserDetails)obj).getUsername();
        //} else {
        //  username = obj.toString();
        //}
        //logger.error("Requesting access to: " + targetURI.getURI() + " | " + targetURI.getPath() + " User: " + username);

        if (targetURI == null) {
        	logger.error("getAcls(InternalURI targetURI): returning. null targetURI");
        	return null;
        }

        BasicAclEntry[] entries = checkImport(targetURI.getURI());
        if (entries != null) return entries;


        Map map = new HashMap();



	BasicAclEntry[] instanceAclEntries = null;

        // Look in cache by targetURI.getURI for BasicAclEntry[]
        if (targetURI instanceof Resource &&
            ((Resource)targetURI).isNew())
        {
          // Look if it is really is new resouce...
          Resource res = ((RepositoryUnsecure)getRepositoryService()).getResourceLookupUnsecure(null, "repo:" + targetURI.getPath());
          if (res == null) {
            res = ((RepositoryUnsecure)getRepositoryService()).getFolderUnsecure(null, "repo:" + targetURI.getPath());
          }

          if (res == null)
          {
            // Look directly to the parent...
                instanceAclEntries = lookup( getParentURI( "repo:" + targetURI.getPath()), null);
          }
        }
        else
        {
        	instanceAclEntries = lookup("repo:" + targetURI.getPath(), targetURI);
	    }


        // Exit if there is no ACL information or parent for this instance
        if (instanceAclEntries == null) {
            return null;
        }

        // Add the leaf objects to the Map, keyed on recipient
        for (int i = 0; i < instanceAclEntries.length; i++) {
            if (logger.isDebugEnabled()) {
                logger.debug("Explicit add: "
                        + instanceAclEntries[i].toString());
            }

            map.put(instanceAclEntries[i].getRecipient(), instanceAclEntries[i]);
        }

        String parent = getParentURI("repo:" + targetURI.getPath());

        while (parent != null) {
            BasicAclEntry[] parentAclEntries = lookup(parent, null);

            if (logger.isDebugEnabled()) {
                logger.debug("Parent lookup: " + parent);
            }

            // Exit loop if parent couldn't be found (unexpected condition)
            if (parentAclEntries == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Parent could not be found in ACL repository");
                }

                break;
            }

            // Now add each _NEW_ recipient to the list
            for (int i = 0; i < parentAclEntries.length; i++) {
                if (!map.containsKey(parentAclEntries[i].getRecipient())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Added parent to map: "
                                + parentAclEntries[i].toString()
                                + " for recipient: "
                                + parentAclEntries[i].getRecipient());
                    }

                    map.put(parentAclEntries[i].getRecipient(),
                            parentAclEntries[i]);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Did NOT add parent to map: "
                                + parentAclEntries[i].toString()
                                + " for recipient: "
                                + parentAclEntries[i].getRecipient());
                    }
                }
            }

            // Prepare for next iteration of while loop
            parent = getParentURI(parent);
        }

        Collection collection = map.values();

        return (BasicAclEntry[]) collection.toArray(new BasicAclEntry[]{});
    }

    public BasicAclEntry[] getAcls(AclObjectIdentity aclObjectIdentity) {
        if (aclObjectIdentity == null || !(aclObjectIdentity instanceof InternalURI)) {
        	logger.debug("getAcls(AclObjectIdentity aclObjectIdentity): returning. invalid object for getAcls: " + aclObjectIdentity);
        	return null;
        }

        InternalURI targetURI = (InternalURI) aclObjectIdentity;

		return getAcls(targetURI);
    }

    public BasicAclEntry[] getAcls(AclObjectIdentity aclObjectIdentity, Object recipient) {
        if (aclObjectIdentity == null || !(aclObjectIdentity instanceof InternalURI)) {
        	logger.debug("getAcls(AclObjectIdentity aclObjectIdentity, Object recipient): returning. invalid object for getAcls: " + aclObjectIdentity);
        	return null;
        }

        InternalURI targetURI = (InternalURI) aclObjectIdentity;
		return getAclsForRecipient(RESOURCE_URI_PREFIX + targetURI.getPath(), recipient);
    }

    private BasicAclEntry[] checkImport(String uri) {
        BasicAclEntry[] res = null;
        if (ImportRunMonitor.isImportRun()){
            //Add special permission for current user
            //to allow updating special resources like themes etc
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            for (GrantedAuthority authority : authentication.getAuthorities()){
                if (authority.getAuthority().equals(ObjectPermissionService.PRIVILEGED_OPERATION)){
                    ObjectPermission permission = new ObjectPermissionImpl();
                    permission.setPermissionMask(SimpleAclEntry.ADMINISTRATION);
                    permission.setPermissionRecipient(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                    res = new BasicAclEntry[]{createBasicAclEntry(uri, permission)};
                }
            }
        }
        return res;
    }

    private BasicAclEntry[] getAclsForRecipient(String targetURI, Object recipient) {
        Map map = new HashMap();

    	while (targetURI != null) {
    		BasicAclEntry[] entries = lookup(targetURI, null);
    		if (entries != null) {

    	        // Add the leaf objects to the Map, keyed on recipient
    	        for (int i = 0; i < entries.length; i++) {

    	        	// Include if we are not filtering by recipients or we have matched the given
    	        	// recipient, and we have not seen this recipient before

                    if ((recipient == null || recipient.equals(entries[i].getRecipient()))
                    		&& !map.containsKey(entries[i].getRecipient())) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Added: "
                                    + entries[i].toString());
                        }

                        map.put(entries[i].getRecipient(),
                        		entries[i]);
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Did NOT add: "
                                    + entries[i].toString());
                        }
                    }
    	        }
    		}
    		targetURI = getParentURI(targetURI);
    	}

        Collection collection = map.values();
    	return (BasicAclEntry[]) collection.toArray(new BasicAclEntry[]{});
    	//return new ArrayList(map.values());
    }

    /**
     * Create AclEntrys from targetURI and list of ObjectPermissions
     *
     *
     * @param targetURI
     * @param permissions
     * @return
     */
    private BasicAclEntry[] getAclsFromObjectPermissions(String targetURI, List permissions) {

        if (permissions == null || permissions.size() == 0) {
        	log.debug("No explicit permissions found");
            // return merely an inheritance marker (as we know about the object but it has no related ACLs)
            return new BasicAclEntry[] {createBasicAclEntry(targetURI, null)};
        } else {
        	if (log.isDebugEnabled()) {
        		log.debug("Found " + permissions.size() + " explicit permissions");
        	}
            // return the individual ACL instances
        	ObjectPermission[] aclHolders = (ObjectPermission[]) permissions.toArray(new ObjectPermission[] {});
            List toReturnAcls = new ArrayList(aclHolders.length);

            for (int i = 0; i < aclHolders.length; i++) {
            	if (log.isDebugEnabled()) {
            		log.debug(aclHolders[i]);
            	}
                toReturnAcls.add(createBasicAclEntry(targetURI, aclHolders[i]));
            }

            return (BasicAclEntry[]) toReturnAcls.toArray(new BasicAclEntry[] {});
        }
    }
    /**
     * Constructs an individual <code>BasicAclEntry</code> from the passed
     * <code>ObjectPermission</code> and <code>InternalURI</code>.
     *
     * <P>
     * Guarantees to never return <code>null</code> (exceptions are thrown in
     * the event of any issues).
     * </p>
     *
     * @param propertiesInformation mandatory information about which instance
     *        to create, the object identity, and the parent object identity
     *        (<code>null</code> or empty <code>String</code>s prohibited for
     *        <code>aclClass</code> and <code>aclObjectIdentity</code>
     * @param aclInformation optional information about the individual ACL
     *        record (if <code>null</code> only an "inheritence marker"
     *        instance is returned; if not <code>null</code>, it is prohibited
     *        to present <code>null</code> or an empty <code>String</code> for
     *        <code>recipient</code>)
     *
     * @return a fully populated instance suitable for use by external objects
     *
     * @throws IllegalArgumentException if the indicated ACL class could not be
     *         created
     */
    BasicAclEntry createBasicAclEntry(
    		String targetURI, ObjectPermission aclInformation) {
        BasicAclEntry entry;

        try {
            entry = (BasicAclEntry) SimpleAclEntry.class.newInstance();
        } catch (InstantiationException ie) {
            throw new IllegalArgumentException(ie.getMessage());
        } catch (IllegalAccessException iae) {
            throw new IllegalArgumentException(iae.getMessage());
        }

        entry.setAclObjectIdentity(new URIObjectIdentity(targetURI));
        entry.setAclObjectParentIdentity(new URIObjectIdentity(getParentURI(targetURI)));

        if (aclInformation == null) {
            // this is an inheritance marker instance only
            entry.setMask(0);
            entry.setRecipient(RECIPIENT_USED_FOR_INHERITANCE_MARKER);
        } else {
            // this is an individual ACL entry
            entry.setMask(aclInformation.getPermissionMask());
            entry.setRecipient(aclInformation.getPermissionRecipient());
        }

        return entry;
    }

    /**
     * Get the parent URI of the given URI, by slicing off the last part of the path
     *
     * @param currentURI
     * @return
     */
    private String getParentURI(String currentURI) {
    	if (currentURI == null || currentURI.trim().length() == 0) {
    		return null;
    	} else {

    		// Deal with URIs that come with "repo:" on the front

    		String workUri = currentURI.startsWith(RESOURCE_URI_PREFIX)
    							? currentURI.substring(RESOURCE_URI_PREFIX_LENGTH).trim()
    							: currentURI.trim();

    		/*
    		 * If we have a URI like "repo:/folder1/object", return "repo:/folder1".
    		 *
    		 * repo:/folder1 returns repo:/
    		 *
    		 * repo:/ returns null
    		 */
    		int lastSeparator = workUri.lastIndexOf(Folder.SEPARATOR);

    		// no separator
    		if (lastSeparator < 0) {
    			return null;
    		} else if (lastSeparator == 0) {
    			// if we are the root: no parent
    			if (workUri.length() == 1) {
    				return null;
    			} else {
    				return RESOURCE_URI_PREFIX + Folder.SEPARATOR;
    			}
    		} else {
    			return RESOURCE_URI_PREFIX + workUri.substring(0, lastSeparator);
    		}
    	}
    }

	/**
	 * Find ObjectPermissions for the object
	 *
	 *  (non-Javadoc)
	 * @see org.springframework.security.acl.AclManager#getAcls(java.lang.Object)
	 */
	public AclEntry[] getAcls(Object obj) {

		List resultList = new ArrayList();

		if (obj instanceof InternalURI) {
			return getAcls((InternalURI) obj);
		}
		else if (obj instanceof String) {
			String path = (String) obj;
			if (path.startsWith(RESOURCE_URI_PREFIX)) {
				path = path.substring(RESOURCE_URI_PREFIX_LENGTH);
			}
			return getAcls(new InternalURIDefinition(path));
		}

		return (BasicAclEntry[]) resultList.toArray(new BasicAclEntry[] {});
	}

	/**
	 * Find ObjectPermissions for the object and Authentication (user)
	 *
	 *  (non-Javadoc)
	 * @see org.springframework.security.acl.AclManager#getAcls(java.lang.Object, org.springframework.security.Authentication)
	 */
	public AclEntry[] getAcls(Object obj, Authentication auth) {

		if (auth == null || auth.getPrincipal() == null ||
		! (obj instanceof InternalURI || obj instanceof String)) {
			return new BasicAclEntry[] {};
		}
        AclEntry[] allAcls = (AclEntry[]) this.getAcls(obj);

        return this.effectiveAclsResolver.resolveEffectiveAcls(allAcls,
            auth);
	}

    private BasicAclEntry[] lookup(String targetURI, InternalURI targetObject) {
    	URIObjectIdentity objIdent = new URIObjectIdentity(targetURI);


        BasicAclEntry[] result = basicAclEntryCache.getEntriesFromCache(objIdent);

        if (result != null && result.length > 0) {
        	if (log.isDebugEnabled()) {
        		log.debug("Found " + targetURI + " in cache");
        	}
            if (result[0].getRecipient() == null || result[0].getRecipient().equals(RECIPIENT_FOR_CACHE_EMPTY)) {
                return null;
            } else {
                return result;
            }
        }

        if (log.isDebugEnabled()) {
    		log.debug("Did not find " + targetURI + " in cache");
    	}

        Resource res = null;
        if (targetObject instanceof Resource) {
        	res = (Resource) targetObject;

        	if (log.isDebugEnabled()) {
        		log.debug("Looking up permissions for resource object " + res.getURIString());
        	}
        } else {
        	res = ((RepositoryUnsecure)getRepositoryService()).getResourceLookupUnsecure(null, targetURI);
        	if (res == null) {
        		res = ((RepositoryUnsecure)getRepositoryService()).getFolderUnsecure(null, targetURI);
        		if (log.isDebugEnabled()) {
        			log.debug("Did not find " + targetURI + " as resource");
        			log.debug("Did " + ((res == null) ? "not" : "") + " find " + targetURI + " as folder");
        		}
        	} else {
        		if (log.isDebugEnabled()) {
        			log.debug("Found " + targetURI + " as resource");
        		}
        	}
        }

		if (res != null) {
			List permissions = getObjectPermissionsForObject(null, res);

			result = getAclsFromObjectPermissions(targetURI, permissions);
		} else {
			if (log.isDebugEnabled()) {
				log.debug("Resource not found for permissions lookup: " + targetURI +
						". Using parent permissions");
			}

			// is it a new resource? Return the parent permissions....
			return lookup( getParentURI(targetURI), null);
		}

        if (result == null) {

            if (log.isDebugEnabled()) {
        		log.debug("Default entries for " + targetURI + " cached");
        	}

            SimpleAclEntry[] emptyAclEntries = {new SimpleAclEntry(RECIPIENT_FOR_CACHE_EMPTY,
            		new URIObjectIdentity(targetURI), null, 0)};
            basicAclEntryCache.putEntriesInCache(emptyAclEntries);

            return null;
        }

        if (log.isDebugEnabled()) {
    		log.debug(result.length + " entries for " + targetURI + " cached");
    	}

        basicAclEntryCache.putEntriesInCache(result);

        return result;
    }


    public void deleteObjectPermissionForObject(ExecutionContext context, Object targetObject) {
		if (targetObject == null || !(targetObject instanceof InternalURI)) {
			return;
		}
		InternalURI res = (InternalURI) targetObject;
		deleteObjectPermissionForRepositoryPath(context, res.getPath());
    }


    public void deleteObjectPermissionForRepositoryPath(ExecutionContext context, String path) {
    	if (log.isDebugEnabled()) {
    		log.debug("Deleting object permissions for repository path " + path);
    	}

		List permissions = getRepoObjectPermissions(context, repositoryURI(path), null);
        deleteObjectPermissions(permissions, false);
    }

	public void deleteObjectPermissionsForRecipient(ExecutionContext context, Object recipient) {
    	if (log.isDebugEnabled()) {
    		log.debug("Deleting object permissions for recipient " + recipient);
    	}

		List permissions = getRepoObjectPermissions(context, null, recipient);
		deleteObjectPermissions(permissions);
	}

	public void deleteObjectPermissionsForRecipient(ExecutionContext context, ObjectPermissionRecipientIdentity recipientIdentity) {
    	if (log.isDebugEnabled()) {
    		log.debug("Deleting object permissions for recipient " + recipientIdentity);
    	}

		List permissions = getRepoObjectPermissions(recipientIdentity);
		deleteObjectPermissions(permissions);
	}

    protected void deleteObjectPermissions(List permissions) {
        deleteObjectPermissions(permissions, true);
    }
    protected void deleteObjectPermissions(List permissions, boolean checkAdministerAccess) {
        if (permissions != null && !permissions.isEmpty()) {
            for (Iterator it = permissions.iterator(); it.hasNext();) {
                RepoObjectPermission permission = (RepoObjectPermission) it.next();
                deleteObjectPermission(permission, checkAdministerAccess);
            }
        }
    }

	/**
	 * A key for the cache
	 *
	 * @author swood
	 *
	 */
    public static class URIObjectIdentity implements AclObjectIdentity {
    	String uri;
    	public URIObjectIdentity(String uri) {
    		String fullUri = uri;

    		// Catch things that do not have a protocol
    		if (uri != null && uri.startsWith(Folder.SEPARATOR)) {
    			fullUri = RESOURCE_URI_PREFIX + uri;
    		}
    		this.uri = fullUri;
    	}
    	public String getURI() {
    		return uri;
    	}

    	public String toString() {
    		return new ToStringBuilder(this)
    			.append("uri", getURI())
    			.toString();
    	}

        public boolean equals(Object other) {
            if ( !(other instanceof URIObjectIdentity) ) return false;
            URIObjectIdentity castOther = (URIObjectIdentity) other;
            return new EqualsBuilder()
                .append(this.getURI(), castOther.getURI())
                .isEquals();
        }

        public int hashCode() {
            return new HashCodeBuilder()
                .append(getURI())
                .toHashCode();
        }
    }

	/**
	 * We can authorize InternalURI
	 *
	 *  (non-Javadoc)
	 * @see org.springframework.security.acl.AclProvider#supports(java.lang.Object)
	 */
	public boolean supports(Object obj) {
		return obj instanceof InternalURI || obj instanceof String;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#newObjectPermission(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext)
	 */
	public ObjectPermission newObjectPermission(ExecutionContext context) {
		return (ObjectPermission) getObjectMappingFactory().newObject(ObjectPermission.class);
	}

    private void addParamsToSetPermissionAuditEvent(final ExecutionContext context, final ObjectPermission objPermission, final int previousPermissionValue) {
        String[] eventNames = new String[]{"createPermission", "updatePermission", "deletePermission"};
        auditContext.doInAuditContext(eventNames, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                Resource resource = repoService.getResource(context, objPermission.getURI());
                if (resource != null) {
                    auditContext.setResourceTypeToAuditEvent(resource.getResourceType(), auditEvent);
                } else {
                    auditContext.setResourceTypeToAuditEvent(Folder.class.getName(), auditEvent);
                }
                auditEvent.setResourceUri(objPermission.getURI());
                String recipientType = "UNKNOWN";
                String recipientName = null;
                String recipientTenantId = null;
                if (objPermission.getPermissionRecipient() instanceof User) {
                    recipientType = "USER";
                    recipientName = ((User) objPermission.getPermissionRecipient()).getUsername();
                    recipientTenantId = ((User) objPermission.getPermissionRecipient()).getTenantId();
                } else if (objPermission.getPermissionRecipient() instanceof Role) {
                    recipientType = "ROLE";
                    recipientName = ((Role) objPermission.getPermissionRecipient()).getRoleName();
                    recipientTenantId = ((Role) objPermission.getPermissionRecipient()).getTenantId();
                }
                auditContext.addPropertyToAuditEvent("recipientType", recipientType, auditEvent);
                auditContext.addPropertyToAuditEvent("recipientName", recipientName, auditEvent);
                auditContext.addPropertyToAuditEvent("recipientTenantId", recipientTenantId, auditEvent);
                if ("updatePermission".equals(auditEvent.getEventType())) {
                    auditContext.addPropertyToAuditEvent("previousPermissionValue", previousPermissionValue, auditEvent);
                }
                auditContext.addPropertyToAuditEvent("permissionValue", objPermission.getPermissionMask(), auditEvent);
            }
        });
    }

    /* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#putObjectPermission(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, com.jaspersoft.jasperserver.api.metadata.user.domain.ObjectPermission)
	 */
	public void putObjectPermission(ExecutionContext context, ObjectPermission objPermission) throws IllegalArgumentException{
        if(objPermission == null){
            throw new IllegalArgumentException("Permission can't be null");
        }
        // checking administrative access
        boolean privileged = (context != null && context.getAttributes() != null && context.getAttributes().contains(PRIVILEGED_OPERATION)) ? true : false;
        if (!privileged && !isObjectAdministrable(context, objPermission.getURI())) {
            throw new AccessDeniedException("Access is denied");
        }
        final Object permissionRecipient = objPermission.getPermissionRecipient();
        if(permissionRecipient == null){
            throw new IllegalArgumentException("Permission recipient can't be null");
        }
        if(permissionRecipient instanceof User){
            final User user = (User) permissionRecipient;
            final String username = user.getUsername();
            if(username == null || username.isEmpty()){
                throw new IllegalArgumentException("User name can't be null");
            }

            Set<String> tenantSet = new HashSet<String>(1);
            tenantSet.add(user.getTenantId());
            final List tenantUsers = userService.getTenantUsers(null, tenantSet, username);
            if(tenantUsers == null || tenantUsers.isEmpty()){
                throw new IllegalArgumentException("User '" + username + "' doesn't exists and can't be used as permissionRecipient for permission");
            }
        } else if(permissionRecipient instanceof Role){
            final Role role = (Role) permissionRecipient;
            final String roleName = role.getRoleName();
            if(roleName == null || roleName.isEmpty()){
                throw new IllegalArgumentException("Role name can't be null");
            }
            Set<String> tenantSet = new HashSet<String>();
            tenantSet.add(role.getTenantId());
            final List roles = userService.getTenantRoles(null, tenantSet, roleName);
            if(roles == null || roles.isEmpty()){
                throw new IllegalArgumentException("Role '" + roleName + "' doesn't exists and can't be used as permissionRecipient for permission");
            }
        } else {
            throw new IllegalArgumentException("Unknown type of permissionRecipient. Invalid permissionRecipient: " + permissionRecipient.toString());
        }
        // Given the object and the recipient, find the permission
		RepoObjectPermission existingPerm = getRepoObjectPermission(context, objPermission);
		if (existingPerm == null) {
			existingPerm = (RepoObjectPermission) getPersistentClassFactory().newObject(ObjectPermission.class);
		}

        addParamsToSetPermissionAuditEvent(context, objPermission, existingPerm.getPermissionMask());

		existingPerm.copyFromClient(objPermission, this);
		getHibernateTemplate().saveOrUpdate(existingPerm);

		clearAclEntriesCache(objPermission.getURI());
	}

	public void deleteObjectPermission(ExecutionContext context, ObjectPermission objPermission) {
		// Given the object and the recipient, find the permission
		RepoObjectPermission existingPerm = getRepoObjectPermission(context, objPermission);
		if (existingPerm == null) {
			throw new JSException("jsexception.no.such.object.permission");
		}
		// and delete
        addParamsToSetPermissionAuditEvent(context, objPermission, existingPerm.getPermissionMask());
		deleteObjectPermission(existingPerm);
	}

    protected void deleteObjectPermission(RepoObjectPermission permission) {
        deleteObjectPermission(permission, true);
    }
	protected void deleteObjectPermission(RepoObjectPermission permission, boolean checkAdministerAccess) {
        // checking administrable access
        if (checkAdministerAccess && !isObjectAdministrable(null, permission.getURI())) {
            throw new AccessDeniedException("Access is denied");
        }
		getHibernateTemplate().delete(permission);
		clearAclEntriesCache(permission.getURI());
	}

	public ObjectPermission getObjectPermission(ExecutionContext context, ObjectPermission objPermission) {
		// Given the object and the recipient, find the permission
		RepoObjectPermission existingPerm = getRepoObjectPermission(context, objPermission);
		if (existingPerm == null) {
			return null;
		} else {
			return (ObjectPermission) existingPerm.toClient(getObjectMappingFactory());
		}
	}
/*
	private List getRepoObjectPermissions(ExecutionContext context, InternalURI targetURI, Object recipient) {
		return getRepoObjectPermissions(context, targetURI.getURI(), recipient);
	}
*/
	private List getRepoObjectPermissions(ExecutionContext context, final String uri, Object recipient) {
		// Given the object and the recipient, find the permission
		final IdedObject recipientObject = (IdedObject) getPersistentObject(recipient);

		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);
        final Class recipientObjectClassName = recipient == null ? null : getPersistentClassFactory().getImplementationClass(recipient.getClass());

		List objList = null;

		if (uri == null && recipientObject == null) {
			//throw new JSException("jsexception.no.uri.or.recipient.given");
			// this is to get all permissions
			final String queryString = "from " + objPermissionClassName;

			objList = getHibernateTemplate().executeFind(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	                Query query = session.createQuery(queryString);
	                return query.list();
	            }
	        });
		}

		if (uri != null && recipientObject != null) {
			// Select on both identity and recipient
			final String queryString = "from " + objPermissionClassName + " as objPermission " +
			"where objPermission.URI = ? and " +
			"      objPermission.permissionRecipient.id = ? and objPermission.permissionRecipient.class = ?";

			objList = getHibernateTemplate().executeFind(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	                Query query = session.createQuery(queryString);
	                query.setParameter(0, uri, Hibernate.STRING);
	                query.setParameter(1, new Long(recipientObject.getId()), Hibernate.LONG);
	                query.setParameter(2, recipientObjectClassName, Hibernate.CLASS);
	                return query.list();
	            }
	        });
		} else if (uri != null) {
			// Select on identity
			final String queryString = "from " + objPermissionClassName + " as objPermission " +
			"where objPermission.URI = ?";

			objList = getHibernateTemplate().executeFind(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException {
	                Query query = session.createQuery(queryString);
	                query.setParameter(0, uri, Hibernate.STRING);
	                return query.list();
	            }
	        });
		} else if (recipientObject != null) {
			// Select on recipient
			ObjectPermissionRecipientIdentity recipientIdentity = new ObjectPermissionRecipientIdentity(recipientObject);
			objList = getRepoObjectPermissions(recipientIdentity);

		}
		return objList;
	}

	protected List getRepoObjectPermissions(final ObjectPermissionRecipientIdentity recipientIdentity) {
		final String objPermissionClassName = getPersistentClassFactory().getImplementationClassName(ObjectPermission.class);

		final String queryString = "from " + objPermissionClassName + " as objPermission " +
			"where objPermission.permissionRecipient.id = ? and objPermission.permissionRecipient.class = ?";

		List objList = getHibernateTemplate().executeFind(new HibernateCallback() {
		    public Object doInHibernate(Session session) throws HibernateException {
		        Query query = session.createQuery(queryString);
		        query.setParameter(0, new Long(recipientIdentity.getId()), Hibernate.LONG);
		        query.setParameter(1, recipientIdentity.getRecipientClass(), Hibernate.CLASS);
		        return query.list();
		    }
		});
		return objList;
	}

	private List getRepoObjectPermissions(ExecutionContext context, ObjectPermission objPermission) {
		// Given the object and parent, find the objectIdentity
		return getRepoObjectPermissions(context, objPermission.getURI(), objPermission.getPermissionRecipient());
	}

	private RepoObjectPermission getRepoObjectPermission(ExecutionContext context, ObjectPermission objPermission) {
		List objList = getRepoObjectPermissions(context, objPermission);
		RepoObjectPermission objPerm = null;
		if (objList == null || objList.isEmpty()) {
			log.debug("ObjectPermission not found with object \"" +
					objPermission.getURI() + "\", recipient \"" + objPermission.getPermissionRecipient() + "\"");
		} else {
			if (log.isDebugEnabled()) {
				log.debug("ObjectPermission FOUND with object \"" +
						objPermission.getURI() + "\", recipient \"" + objPermission.getPermissionRecipient() + "\"");
				log.debug("Size: " + objList.size());
			}
			objPerm = (RepoObjectPermission) objList.get(0);
		}
		return objPerm;

	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#getObjectPermissionsForObject(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object)
	 */
	public List getObjectPermissionsForObject(ExecutionContext context, Object targetObject) {
		if (targetObject == null || !(targetObject instanceof InternalURI)) {
			return new ArrayList();
		}
		InternalURI res = (InternalURI) targetObject;
		List objList = getRepoObjectPermissions(context, "repo:" + res.getPath(), null);
		return makeObjectPermissionClientList("repo:" + res.getPath(), objList);
	}

    @Override
    public List<ObjectPermission> getEffectivePermissionsForObject(ExecutionContext context, Object targetObject) {
        List<ObjectPermission> result;
        BasicAclEntry[] acls = (BasicAclEntry[])getAcls(targetObject);
        if (acls != null) {
            result = new ArrayList<ObjectPermission>(acls.length);

            for (BasicAclEntry entry : acls) {
                if (!(entry.getRecipient() instanceof String)) {
                    ObjectPermission permission = new ObjectPermissionImpl();
                    permission.setPermissionRecipient(entry.getRecipient());
                    permission.setPermissionMask(entry.getMask());
                    permission.setURI(((ObjectPermissionServiceImpl.URIObjectIdentity) entry.getAclObjectIdentity()).getURI());
                    result.add(permission);
                }
            }
        } else {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /* (non-Javadoc)
      * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#getObjectPermissionsForRecipient(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object)
      */
	public List getObjectPermissionsForRecipient(ExecutionContext context, Object recipient) {
		List objList = getRepoObjectPermissions(context, null, recipient);
		return makeObjectPermissionClientList(null, objList);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#getObjectPermissionsForObjectAndRecipient(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object, java.lang.Object)
	 */
	public List getObjectPermissionsForObjectAndRecipient(ExecutionContext context, Object targetObject, Object recipient) {
		if (!(targetObject instanceof InternalURI)) {
			return new ArrayList();
		}
		InternalURI res = (InternalURI) targetObject;
		List objList = getRepoObjectPermissions(context, RESOURCE_URI_PREFIX + res.getPath(), recipient);
		return makeObjectPermissionClientList(RESOURCE_URI_PREFIX + res.getPath(), objList);
	}


    /* (non-Javadoc)
     * @see com.jaspersoft.jasperserver.api.metadata.user.service.ObjectPermissionService#isObjectAdministrable(com.jaspersoft.jasperserver.api.common.domain.ExecutionContext, java.lang.Object)
     */
	public boolean isObjectAdministrable(ExecutionContext context, Object targetObject) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((targetObject == null) || (auth == null)) {
            return true;
        }
        BasicAclEntry[] allAcls = (BasicAclEntry[]) effectiveAclsResolver.resolveEffectiveAcls(getAcls(targetObject), auth);
        if (allAcls != null) {
            for (int i = 0; i < allAcls.length; i++) {
                BasicAclEntry aclEntry = allAcls[i];
                if (aclEntry.getMask() == SimpleAclEntry.ADMINISTRATION) {
                    return true;
                }
            }
        }
        return false;
    }

	private List makeObjectPermissionClientList(String uri, List objList) {
		List resultList = new ArrayList(objList.size());
/*
		if ((objList == null || objList.isEmpty()) && uri != null && uri.length() > 0) {
			// Deal with default inheritance: return an object with a URI only
			RepoObjectPermission repoPerm = new RepoObjectPermission();
			repoPerm.setURI(uri);
			if (objList == null) {
				objList = new ArrayList();
			}
			objList.add(repoPerm);
		}
*/
		for (Iterator it = objList.iterator(); !objList.isEmpty() && it.hasNext();) {
			RepoObjectPermission repoPerm = (RepoObjectPermission) it.next();
			ObjectPermission clientPermission = (ObjectPermission) repoPerm.toClient(getObjectMappingFactory());
			resultList.add(clientPermission);
		}
		return resultList;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver#getPersistentObject(java.lang.Object)
	 */
	public Object getPersistentObject(Object clientObject) {
		// If already persisted, just return it
		if (clientObject == null) {
			return null;
		} else	if (clientObject instanceof IdedObject) {
			return clientObject;
		} else if (clientObject instanceof Role || clientObject instanceof User) {
			return ((PersistentObjectResolver) userService).getPersistentObject(clientObject);
		} else if (clientObject instanceof Resource) {
			// TODO Hack! Make it an interface!
			String uri = ((Resource) clientObject).getPath();
			return repoService.findByURI(RepoResource.class, uri, false);
		} else if (clientObject instanceof ObjectPermission) {
			return getRepoObjectPermission(null, (ObjectPermission) clientObject);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver#getExternalReference(java.lang.String, java.lang.Class)
	 */
	public RepoResource getExternalReference(String uri, Class persistentReferenceClass) {
		return ((ReferenceResolver) repoService).getExternalReference(uri, persistentReferenceClass);
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.ReferenceResolver#getReference(com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource, com.jaspersoft.jasperserver.api.metadata.common.domain.Resource, java.lang.Class)
	 */
	public RepoResource getReference(RepoResource owner, Resource resource, Class persistentReferenceClass) {
		return ((ReferenceResolver) repoService).getReference(owner, resource, persistentReferenceClass);
	}

	protected String repositoryURI(String repositoryPath) {
		return RESOURCE_URI_PREFIX + repositoryPath;
	}

	protected void clearAclEntriesCache(String uri) {
		URIObjectIdentity objId = new URIObjectIdentity(uri);
		if (log.isDebugEnabled()) {
			log.debug("Removing " + objId + " from permissions");
		}
		basicAclEntryCache.removeEntriesFromCache(objId);
	}

	public void updateObjectPermissionRepositoryPath(String oldPath, String newPath) {
		String oldURI = repositoryURI(oldPath);
		String newURI = repositoryURI(newPath);

		List permissions = getRepoObjectPermissions(null, oldURI, null);
		if (permissions != null && !permissions.isEmpty()) {
			for (Iterator it = permissions.iterator(); it.hasNext();) {
				RepoObjectPermission permission = (RepoObjectPermission) it.next();
				permission.setURI(newURI);
				getHibernateTemplate().update(permission);

				if (log.isDebugEnabled()) {
					log.debug("Updated URI of permission for " + oldURI + " and " + permission.getPermissionRecipient()
							+ " to " + newURI);
				}
			}
		}

		clearAclEntriesCache(oldURI);
	}

	public int getInheritedObjectPermissionMask(ExecutionContext context,
			Object targetObject, Object recipient) {
		if (!(targetObject instanceof InternalURI)) {
			if (log.isDebugEnabled()) {
				log.debug("Requested inherited permission non InternalURI object "
						+ targetObject);
			}

			return SimpleAclEntry.NOTHING;
		}

		InternalURI resource = (InternalURI) targetObject;
		int permissionMask = SimpleAclEntry.NOTHING;

		String folderURI = getParentURI(resource.getPath());
		while (folderURI != null) {
			List permissions = getRepoObjectPermissions(context,
					folderURI, recipient);
			if (permissions != null && !permissions.isEmpty()) {
				RepoObjectPermission permissionObject =
					(RepoObjectPermission) permissions.get(0);
				permissionMask = permissionObject.getPermissionMask();
				break;
			}

			folderURI = getParentURI(folderURI);
		}

		if (log.isDebugEnabled()) {
			if (folderURI == null) {
				log.debug("No inherited permission found for object "
						+ resource.getPath() + " and recipient " + recipient);
			} else {
				log.debug("Inherited permission " + permissionMask + " found at "
						+ folderURI + " for object " + resource.getPath()
						+ " and recipient " + recipient);
			}
		}

		return permissionMask;
	}

}
