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

package com.jaspersoft.jasperserver.war.themes;

import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.HibernateSaveUpdateDeleteListener;
import com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.persistent.RepoResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.event.DeleteEvent;
import org.hibernate.event.SaveOrUpdateEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * This class listens delete and saveOrUpdate hibernate events.
 * It detects updates in application themes and flushes the theme cache.
 *
 * @author asokolnikov
 */
public class ThemeHibernateListener implements HibernateSaveUpdateDeleteListener,
        BeanFactoryAware {

    private static Log log = LogFactory.getLog(ThemeHibernateListener.class);

    protected BeanFactory beanFactory;
    protected String themeCacheBeanName;
    protected ThemeCache themeCache;

    protected Log getLog() {
        return log;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public ThemeCache getThemeCache() {
        if (themeCache == null) {
            themeCache = (ThemeCache) beanFactory.getBean(themeCacheBeanName);
        }
        return themeCache;
    }

    protected void preProcessDelete(RepoResource res) {
    }

    protected void preProcessUpdate(RepoResource res) {
    }

    @Override
    public void beforeSave(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void afterSave(Object entity, HibernateTemplate hibernateTemplate) {
        afterSaveOrUpdate(entity, hibernateTemplate);
    }

    @Override
    public void beforeUpdate(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void afterUpdate(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void beforeSaveOrUpdate(Object entity, HibernateTemplate hibernateTemplate) {
    }

    @Override
    public void afterSaveOrUpdate(Object entity, HibernateTemplate hibernateTemplate) {
        if (entity instanceof RepoResource) {
            RepoResource res = (RepoResource) entity;
            String uri = res.getResourceURI();
            if (getThemeCache().isThemeResource(uri)) {
                getLog().trace("onSaveOrUpdate: Resource :" + uri);
                preProcessUpdate(res);
                getThemeCache().onThemeResourceChanged(uri);
            }
        }
    }

    @Override
    public void beforeDelete(Object entity, HibernateTemplate hibernateTemplate) {
        if (entity instanceof RepoResource) {
            RepoResource res = (RepoResource) entity;
            String uri = res.getResourceURI();
            if (getThemeCache().isThemeResource(uri)) {
                getLog().trace("onDelete: Resource :" + uri);
                preProcessDelete(res);
                getThemeCache().onThemeResourceChanged(uri);
            }
        }
    }

    @Override
    public void afterDelete(Object entity, HibernateTemplate hibernateTemplate) {
    }

    public String getThemeCacheBeanName() {
        return themeCacheBeanName;
    }

    public void setThemeCacheBeanName(String themeCacheBeanName) {
        this.themeCacheBeanName = themeCacheBeanName;
    }
}
