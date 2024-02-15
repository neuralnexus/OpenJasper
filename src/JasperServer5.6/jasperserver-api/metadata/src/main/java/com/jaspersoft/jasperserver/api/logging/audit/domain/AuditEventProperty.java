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
package com.jaspersoft.jasperserver.api.logging.audit.domain;

/**
 * @author Sergey Prilukin
 * @version $Id: AuditEventProperty.java 47331 2014-07-18 09:13:06Z kklein $
 */
public interface AuditEventProperty {

    public AuditEvent getAuditEvent();

    public void setAuditEvent(AuditEvent auditEvent);

    public String getPropertyType();

    public void setPropertyType(String propertyType);

    public String getValue();

    public void setValue(String value);

    public String getClobValue();

    public void setClobValue(String clobValue);    
}
