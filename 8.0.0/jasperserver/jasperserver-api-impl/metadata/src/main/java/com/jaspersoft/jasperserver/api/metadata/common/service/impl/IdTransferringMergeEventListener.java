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

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.spi.MergeEvent;
import org.hibernate.persister.entity.EntityPersister;

import java.io.Serializable;
import java.util.Map;

/**
 * @author oleg
 *         11.08.2016.
 *source fro org.springframework.orm.hibernate3.support.IdTransferringMergeEventListener
 */
public class IdTransferringMergeEventListener extends DefaultMergeEventListener {
    @Override
    protected void entityIsTransient(MergeEvent event, Map copyCache) {
        super.entityIsTransient(event, copyCache);
        SessionImplementor session = event.getSession();
        EntityPersister persister = session.getEntityPersister(event.getEntityName(), event.getEntity());
        // Extract id from merged copy (which is currently registered with Session).
        Serializable id = persister.getIdentifier(event.getResult(), session);
        // Set id on original object (which remains detached).
        persister.setIdentifier(event.getOriginal(), id, session);
    }

}
