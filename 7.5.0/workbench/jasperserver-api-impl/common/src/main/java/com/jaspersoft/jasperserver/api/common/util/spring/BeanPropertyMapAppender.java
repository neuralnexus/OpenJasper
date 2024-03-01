/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.api.common.util.spring;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jaspersoft.jasperserver.api.JSException;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class BeanPropertyMapAppender extends AbstractBeanPropertyProcessor {
	
	private static final Log log = LogFactory.getLog(BeanPropertyMapAppender.class);

	private Map appended;

	@Override
	protected void handleFilterChainsToInsertValue(Object filterList, String patternUri) {
	}

	protected Object getProcessedPropertyValue(Object originalValue) {
		Map appendedValue;
		if (originalValue == null) {
			appendedValue = getAppended();
		} else {
			if (!(originalValue instanceof Map)) {
				throw new JSException("jsexception.property.not.a.map", new Object[] {getPropertyName(), getBeanName()});
			}
			appendedValue = (Map) originalValue;
			appendedValue.putAll(getAppended());
		}
		
		if (log.isInfoEnabled()) {
			log.info("Adding/overriding " + getAppended().size() + " entry(ies) to " + getBeanName() + "." + getPropertyName());
		}
		
		return appendedValue;
	}

	public Map getAppended() {
		return appended;
	}

	public void setAppended(Map appendedText) {
		this.appended = appendedText;
	}

}
