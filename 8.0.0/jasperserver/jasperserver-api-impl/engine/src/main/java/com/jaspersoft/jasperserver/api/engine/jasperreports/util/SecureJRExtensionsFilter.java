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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import net.sf.jasperreports.engine.fonts.SimpleFontExtensionsRegistryFactory;

import java.util.Properties;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class SecureJRExtensionsFilter {

	private static final SecureJRExtensionsFilter INSTANCE = new SecureJRExtensionsFilter();
	
	public static SecureJRExtensionsFilter instance() {
		return INSTANCE;
	}
	
	public SecureJRExtensionsFilter() {
	}

	public byte[] filterExtensionProperties(InputStream propertiesStream) throws IOException {
		Properties properties = new Properties();
		properties.load(propertiesStream);
		
		for (Iterator<Entry<Object, Object>> it = properties.entrySet().iterator(); it.hasNext();) {
			Entry<Object, Object> entry = it.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			//only keeping font extension properties
			if (!(SimpleFontExtensionsRegistryFactory.class.getName().equals(value)
					|| name.startsWith(SimpleFontExtensionsRegistryFactory.SIMPLE_FONT_FAMILIES_PROPERTY_PREFIX)))
			{
				it.remove();
			}
		}
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		properties.store(output, null);
		return output.toByteArray();
	}

}
