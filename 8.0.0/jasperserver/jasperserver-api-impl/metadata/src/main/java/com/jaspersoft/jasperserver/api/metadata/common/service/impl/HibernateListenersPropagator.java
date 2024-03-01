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
package com.jaspersoft.jasperserver.api.metadata.common.service.impl;

import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionFactoryImpl;

/**
 * @author o.gavavka
 *         11.08.2016.
 */
public class HibernateListenersPropagator {

    private SessionFactory sessionFactory;

    private MergeEventListener mergeEventListener=null;
    private DeleteEventListener deleteEventListener=null;
    private SaveOrUpdateEventListener saveOrUpdateEventListener;
    private PostUpdateEventListener postUpdateEventListener;
    private PostDeleteEventListener postDeleteEventListener;
    private PersistEventListener persistEventListener;

    public HibernateListenersPropagator() {
    }

    public void propagate() {
        EventListenerRegistry registry = ((SessionFactoryImpl)sessionFactory).getServiceRegistry().getService(EventListenerRegistry.class);
        if (mergeEventListener!=null)
            registry.getEventListenerGroup(EventType.MERGE).prependListener(mergeEventListener);
        if (deleteEventListener!=null)
            registry.getEventListenerGroup(EventType.DELETE).prependListener(deleteEventListener);


        if (postUpdateEventListener!=null)
            registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(postUpdateEventListener);
        if (postDeleteEventListener!=null)
            registry.getEventListenerGroup(EventType.POST_DELETE).appendListener(postDeleteEventListener);

    }


    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setMergeEventListener(MergeEventListener mergeEventListener) {
        this.mergeEventListener = mergeEventListener;
    }

    public void setDeleteEventListener(DeleteEventListener deleteEventListener) {
        this.deleteEventListener = deleteEventListener;
    }

    public void setSaveOrUpdateEventListener(SaveOrUpdateEventListener saveOrUpdateEventListener) {
        this.saveOrUpdateEventListener = saveOrUpdateEventListener;
    }

    public void setPostUpdateEventListener(PostUpdateEventListener postUpdateEventListener) {
        this.postUpdateEventListener = postUpdateEventListener;
    }

    public void setPostDeleteEventListener(PostDeleteEventListener postDeleteEventListener) {
        this.postDeleteEventListener = postDeleteEventListener;
    }

    public PersistEventListener getPersistEventListener() {
        return persistEventListener;
    }

    public void setPersistEventListener(PersistEventListener persistEventListener) {
        this.persistEventListener = persistEventListener;
    }

}
