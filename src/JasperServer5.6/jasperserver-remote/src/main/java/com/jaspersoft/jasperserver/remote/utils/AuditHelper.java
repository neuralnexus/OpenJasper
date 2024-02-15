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
package com.jaspersoft.jasperserver.remote.utils;

import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * @author gtoffoli
 * @version $Id: AuditHelper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Component
public class AuditHelper {
    @Resource(name = "concreteAuditContext")
    private AuditContext auditContext;

    public void setAuditContext(AuditContext auditContext) {
        this.auditContext = auditContext;
    }

    public void createAuditEvent(final String auditEventType) {
        auditContext.doInAuditContext(
                new AuditContext.AuditContextCallback() {
                    public void execute() {
                        auditContext.createAuditEvent(auditEventType);
                    }
                });
    }

    public void closeAuditEvent(String jobType) {
        auditContext.doInAuditContext(jobType, new AuditContext.AuditContextCallbackWithEvent() {
            public void execute(AuditEvent auditEvent) {
                auditContext.closeAuditEvent(auditEvent);
            }
        });
    }

    public void addPropertyToAuditEvent(final String auditEventType, final String propertyName, final Object property) {
        auditContext.doInAuditContext(auditEventType,
                new AuditContext.AuditContextCallbackWithEvent() {
                    public void execute(AuditEvent auditEvent) {
                        auditContext.addPropertyToAuditEvent(propertyName, property, auditEvent);
                    }
                });
    }

    public void addExceptionToAllAuditEvents(final Throwable exception) {
        auditContext.doInAuditContext(
                new String[]{"saveResource", "updateResource", "deleteResource", "copyResource", "moveResource",
                        "createFolder", "updateFolder", "deleteFolder", "copyFolder", "moveFolder",
                        "runReport"},
                new AuditContext.AuditContextCallbackWithEvent() {
                    public void execute(AuditEvent auditEvent) {
                        auditContext.addPropertyToAuditEvent("exception", exception, auditEvent);
                    }
                });
    }



    public void createAuditEvent(String operation, String wsType, boolean isNew) {
        if ("copy".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("copyFolder");
            } else {
                createAuditEvent("copyResource");
            }
        } else if ("move".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("moveFolder");
            } else {
                createAuditEvent("moveResource");
            }
        } else if ("delete".equals(operation)) {
            if ("folder".equals(wsType)) {
                createAuditEvent("deleteFolder");
            } else {
                createAuditEvent("deleteResource");
            }
        } else if ("put".equals(operation)) {
            if ("folder".equals(wsType)) {
                if (isNew) {
                    createAuditEvent("createFolder");
                } else {
                    createAuditEvent("updateFolder");
                }
            } else {
                if (isNew) {
                    createAuditEvent("saveResource");
                } else {
                    createAuditEvent("updateResource");
                }
            }
        } else if ("get".equals(operation)) {
            if (!"folder".equals(wsType)) {
                createAuditEvent("accessResource");
            }
        }
    }

}
