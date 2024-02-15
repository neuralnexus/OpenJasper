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
import com.jaspersoft.jasperserver.api.metadata.common.domain.Resource;
import com.jaspersoft.jasperserver.api.metadata.common.domain.impl.IdedObject;
import com.jaspersoft.jasperserver.api.metadata.common.service.ResourceFactory;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.HibernateDaoImpl;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateRepositoryService;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.PersistentObjectResolver;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import com.jaspersoft.jasperserver.api.metadata.user.domain.ProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.domain.Role;
import com.jaspersoft.jasperserver.api.metadata.user.domain.User;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.client.MetadataUserDetails;
import com.jaspersoft.jasperserver.api.metadata.user.domain.impl.hibernate.RepoProfileAttribute;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributeService;
import com.jaspersoft.jasperserver.api.metadata.user.service.UserAuthorityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Manages attributes for principals - Users, Roles.
 *
 * @author sbirney
 */
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class ProfileAttributeServiceImpl extends HibernateDaoImpl
        implements ProfileAttributeService, PersistentObjectResolver {

    protected static final Log log = LogFactory.getLog(ProfileAttributeServiceImpl.class);

    private HibernateRepositoryService repoService;
    private UserAuthorityService userService;

    private ResourceFactory objectFactory;
    private ResourceFactory persistentClassFactory;

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

    @Transactional(propagation = Propagation.REQUIRED)
    public ProfileAttribute newProfileAttribute(ExecutionContext context) {
        return (ProfileAttribute) getObjectMappingFactory().newObject(ProfileAttribute.class);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void putProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
        RepoProfileAttribute existingAttr = getRepoProfileAttribute(attr);
        if (existingAttr == null) {
            existingAttr = (RepoProfileAttribute) getPersistentClassFactory().newObject(ProfileAttribute.class);
        }
        existingAttr.copyFromClient(attr, this);
        getHibernateTemplate().saveOrUpdate(existingAttr);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
        RepoProfileAttribute existingAttr = getRepoProfileAttribute(attr);
        getHibernateTemplate().delete(existingAttr);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttributes(Collection<ProfileAttribute> attributesToDelete) {
		Map<String, ProfileAttribute> paMapToDel = new HashMap<String, ProfileAttribute>(attributesToDelete.size());
		for (ProfileAttribute pa : attributesToDelete)
			paMapToDel.put(pa.getAttrName(), pa);
		deleteProfileAttributes(paMapToDel);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void deleteProfileAttributes(Map<String, ProfileAttribute> attributeMapToDelete) {
		List<RepoProfileAttribute> paListToDelete = new ArrayList<RepoProfileAttribute>();
		List<RepoProfileAttribute> paList = getRepoProfileAttributes(getCurrentUserDetails());
		for (RepoProfileAttribute pa : paList) {
			if (attributeMapToDelete.containsKey(pa.getAttrName()))
		       paListToDelete.add(pa);
		}
        getHibernateTemplate().deleteAll(paListToDelete);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ProfileAttribute getProfileAttribute(ExecutionContext context, ProfileAttribute attr) {
        // Given the object and the recipient, find the permission
        RepoProfileAttribute existingPerm = getRepoProfileAttribute(attr);
        if (existingPerm == null) {
            return null;
        } else {
            return (ProfileAttribute) existingPerm.toClient(getObjectMappingFactory());
        }
    }

    private RepoProfileAttribute getRepoProfileAttribute(ProfileAttribute attr) {
        // Given the principal find the attributes
        final IdedObject principalObject =
                (IdedObject) getPersistentObject(attr.getPrincipal());

        final String attrClassName =
                getPersistentClassFactory().getImplementationClassName(ProfileAttribute.class);

        if (principalObject == null) {
            throw new JSException("jsexception.no.principal");
        }

        final String queryString =
                "from " + attrClassName + " as profileAttr " +
                        "where profileAttr.principal.id = ? " +
                        "and profileAttr.principal.class = ? " +
                        "and profileAttr.attrName = ?";

        final String attrName = attr.getAttrName();

        List objList = getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query query = session.createQuery(queryString);
                query.setParameter(0, principalObject.getId(), Hibernate.LONG);
                query.setParameter(1, getObjectClass(principalObject.getClass()), Hibernate.CLASS);
                query.setParameter(2, attrName, Hibernate.STRING);
                return query.list();
            }
        });

        if (objList.size() == 0) {
            return null;
        }

        return (RepoProfileAttribute) objList.get(0);
    }

    private Class<?> getObjectClass(Class<?> classObj) {
        if (classObj.toString().indexOf("_$$_") > 0) return classObj.getSuperclass();
        return classObj;
    }

    private List getRepoProfileAttributes(Object principal) {
        // Given the principal find the attributes
        final IdedObject principalObject = (IdedObject) getPersistentObject(principal);

        final String attrClassName = getPersistentClassFactory().getImplementationClassName(ProfileAttribute.class);

        if (principalObject == null) {
            throw new JSException("jsexception.no.principal");
        }

        final String queryString =
                "from " + attrClassName + " as profileAttr " +
                        "where profileAttr.principal.id = ? " +
                        "and profileAttr.principal.class = ? " +
                        "order by profileAttr.id asc";

        List objList = getHibernateTemplate().executeFind(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException {
                Query query = session.createQuery(queryString);
                query.setParameter(0, principalObject.getId(), Hibernate.LONG);
                query.setParameter(1, getObjectClass(principalObject.getClass()), Hibernate.CLASS);
                return query.list();
            }
        });

        return objList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List getProfileAttributesForPrincipal(ExecutionContext context, Object principal) {
        List objList = getRepoProfileAttributes(principal);
        return makeProfileAttributeClientList(objList);
    }

    public List getProfileAttributesForPrincipal(ExecutionContext context) {
		User user = getCurrentUserDetails();
       	return getProfileAttributesForPrincipal(context, user);
    }

    public List getProfileAttributesForPrincipal() {
       	return getProfileAttributesForPrincipal(null);
    }

    private List makeProfileAttributeClientList(List objList) {
        List resultList = new ArrayList(objList.size());

        for (Iterator it = objList.iterator(); !objList.isEmpty() && it.hasNext(); ) {
            RepoProfileAttribute repoPerm = (RepoProfileAttribute) it.next();
            ProfileAttribute clientPermission =
                    (ProfileAttribute) repoPerm.toClient(getObjectMappingFactory());
            resultList.add(clientPermission);
        }
        return resultList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Object getPersistentObject(Object clientObject) {
        // If already persisted, just return it
        if (clientObject == null) {
            return null;
        } else if (clientObject instanceof IdedObject) {
            return clientObject;
        } else if (clientObject instanceof Role || clientObject instanceof User) {
            return ((PersistentObjectResolver) userService).getPersistentObject(clientObject);
        } else if (clientObject instanceof Resource) {
            // TODO Hack! Make it an interface!
            String uri = ((Resource) clientObject).getPath();
            return repoService.findByURI(RepoResource.class, uri, false);
        } else if (clientObject instanceof ProfileAttribute) {
            return getRepoProfileAttribute((ProfileAttribute) clientObject);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String getCurrentUserPreferenceValue(String attrName) {
        String attrValue = null;
        User user = getCurrentUserDetails();
        ProfileAttribute attr = newProfileAttribute(null);
        attr.setPrincipal(user);
        attr.setAttrName(attrName);
        ProfileAttribute savedAttr = getProfileAttribute(null, attr);
        if (savedAttr != null) {
            attrValue = savedAttr.getAttrValue();
        }
        return attrValue;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void setCurrentUserPreferenceValue(String attrName, String attrValue) {
        User user = getCurrentUserDetails();
        ProfileAttribute attr = newProfileAttribute(null);
        attr.setPrincipal(user);
        attr.setAttrName(attrName);
        ProfileAttribute savedAttr = getProfileAttribute(null, attr);
        if (savedAttr == null) {
            savedAttr = attr;
        }
        if (!equalsWithNull(savedAttr.getAttrValue(), attrValue)) {
            savedAttr.setAttrValue(attrValue);
            putProfileAttribute(null, savedAttr);
        }
    }

    public static boolean equalsWithNull(Object o1, Object o2) {
        return (o1 == null && o2 == null) || (o1 != null && o1.equals(o2));
    }

    public MetadataUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return (MetadataUserDetails) auth.getPrincipal();
        }
        return null;
    }
}
