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

package com.jaspersoft.jasperserver.remote.helpers;

import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class PermissionsRecipientIdentityResolver {
    @Resource(name = "protocolMapping")
    protected Map<String, Class<?>> map;

    public Class<?> getClassForProtocol(String protocol) throws IllegalParameterValueException{
        return map.get(protocol);
    }

    public String getProtocolForClass(Class<?> clazz) {
        for (String key : map.keySet()){
            if (map.get(key).equals(clazz)){
                return key;
            }
        }
        return null;
    }

    public PermissionsRecipientIdentity toIdentity(String uri) throws IllegalParameterValueException {
        String[] protocolLevel = uri.split(":/");
        if (protocolLevel.length != 2) {
            throw new IllegalParameterValueException("recipientUri", uri);
        }

        Class<?> clazz = map.get(protocolLevel[0]);
        if (clazz == null){
            throw new IllegalParameterValueException("type", protocolLevel[0]);
        }

        return new PermissionsRecipientIdentity(clazz, protocolLevel[1]);
    }
}
