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
package com.jaspersoft.jasperserver.api.metadata.olap.util;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.olap4j.driver.xmla.cache.XmlaOlap4jInvalidStateException;
import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache;

/**
 * this class overwrites XmlaOlap4jNamedMemoryCache to fix bug 29966
 * by enforcing UTF-8 characterset for cached responses
 * Created by IntelliJ IDEA. User: ichan Date: 11/27/12 Time: 2:47 PM
 */
public class JasperServerXMLACache extends XmlaOlap4jNamedMemoryCache {

	protected static final Log log = LogFactory.getLog(JasperServerXMLACache.class);

    private static String USER = "USER";

    public JasperServerXMLACache() {
		super();
	}

    @Override
    public byte[] get(String id, URL url, byte[] request)
            throws RuntimeException {
        log.debug("Intercepted XMLA cache get\nid: "+id+"\nurl: "+url+"\nrequest: " + request);
        try {
            byte[] response = super.get(id, url, request);
            if (response != null) {
                return new String(response, "UTF-8").getBytes("UTF-8");
            } else {
                return null;
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Failed to convert XMLA cached response to UTF-8", ex);
        }  catch (XmlaOlap4jInvalidStateException ex) {
            throw new RuntimeException("Xmla olap4j invalid state exception", ex);
        }
    }

    @Override
    public String setParameters(
            Map<String, String> config,
            Map<String, String> props) {
        if (props.containsKey(XmlaOlap4jNamedMemoryCache.Property.NAME.name()) && config.containsKey(USER)) {
            String refId = props.get(XmlaOlap4jNamedMemoryCache.Property.NAME.name()) + "_" + config.get(USER);
            //Put cache info per authorized user.
            props.put(XmlaOlap4jNamedMemoryCache.Property.NAME.name(), refId);
        }
        return super.setParameters(config, props);
    }

}
