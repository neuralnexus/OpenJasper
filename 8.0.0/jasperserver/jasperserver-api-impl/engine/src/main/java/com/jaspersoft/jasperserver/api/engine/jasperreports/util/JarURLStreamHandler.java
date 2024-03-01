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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceIdentityMap;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
public class JarURLStreamHandler extends URLStreamHandler {
	
	private static final String PROTOCOL = "jsjar";
	private static final String URL_PREFIX = PROTOCOL + ":";
	private static final int URL_PREFIX_LENGHT = URL_PREFIX.length();
	private static final String SEPARATOR = "!";
	private static final int SEPARATOR_LENGHT = SEPARATOR.length();
	
	private final ReferenceIdentityMap urlStreams;

	protected void parseURL(URL u, String spec, int start, int limit) {
		String quotedSpec = "\"" + spec + "\"";
		if (!spec.startsWith(URL_PREFIX)) {
			throw new JSException("jsexception.jar.malformed.url", new Object[] {quotedSpec});
		}
		int sep = spec.indexOf(SEPARATOR);
		if (sep < 0) {
			throw new JSException("jsexception.jar.malformed.url", new Object[] {quotedSpec});
		}
		String jar = spec.substring(URL_PREFIX_LENGHT, sep);
		String res = spec.substring(sep + SEPARATOR_LENGHT, spec.length());
		setURL(u, PROTOCOL, null, -1, null, null, jar, null, res);
	}

	public JarURLStreamHandler() {
		urlStreams = new ReferenceIdentityMap(AbstractReferenceMap.WEAK, AbstractReferenceMap.HARD);
	}

	protected URLConnection openConnection(URL u) throws IOException {
		JarFileEntry entry = getURLEntry(u);
		if (entry == null) {
			throw new JSException("jsexception.jar.entry.not.found", new Object[] {u});
		}
		return new JarConnection(u, entry);
	}

	public URL createURL(JarFileEntry jarEntry) {
		try {
			String spec = URL_PREFIX + jarEntry.getJarFile().getName()
					+ SEPARATOR + jarEntry.getEntry().getName();
			URL url = new URL(null, spec, this);
			putURL(url, jarEntry);
			return url;
		} catch (MalformedURLException e) {
			throw new JSExceptionWrapper(e);
		}
	}
	
	public synchronized void putURL(URL u, JarFileEntry jarEntry) {
		urlStreams.put(u, jarEntry);
	}

	protected synchronized JarFileEntry getURLEntry(URL u) {
		return (JarFileEntry) urlStreams.get(u);
	}

	protected boolean equals(URL u1, URL u2) {
		if (u1 == u2) {
			return true;
		}
		
		JarFileEntry entry1 = getURLEntry(u1);
		JarFileEntry entry2 = getURLEntry(u2);
		return entry1 != null && entry2 != null && entry1.equals(entry2);
	}

	protected int hashCode(URL u) {
		JarFileEntry entry = getURLEntry(u);
		return entry == null ? System.identityHashCode(u) : entry.hashCode();
	}

	protected String toExternalForm(URL u) {
		return URL_PREFIX + u.getPath() + SEPARATOR + u.getRef();
	}
}
