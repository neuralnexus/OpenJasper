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

package com.jaspersoft.jasperserver.api.metadata.common.util;

import java.util.Iterator;
import java.util.Map;

import com.jaspersoft.jasperserver.api.common.properties.PropertyChangerAdapter;
import mondrian.olap.MondrianProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.metadata.common.service.RepositoryService;
import com.jaspersoft.jasperserver.api.common.properties.PropertyChanger;
import com.jaspersoft.jasperserver.api.logging.audit.context.AuditContext;
import com.jaspersoft.jasperserver.api.logging.audit.domain.AuditEvent;

/**
 * @author sbirney (sbirney@users.sourceforge.net)
 * @version $Id: EditMondrianPropertiesAction.java 8410 2007-05-29 23:34:07Z melih $
 */
public class MondrianPropertyChanger extends PropertyChangerAdapter {

    private static final Log log = LogFactory.getLog(MondrianPropertyChanger.class);

    public void setProperty(String key, String val) {
		log.debug("setting mondrian property: " + key + " - " + val);
		MondrianProperties.instance().setProperty((String)key, (String)val);
	}

	public String getProperty(String key) {
		return MondrianProperties.instance().getProperty(key);
	}

}