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

package com.jaspersoft.jasperserver.api.security;

import org.springframework.beans.propertyeditors.PropertiesEditor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import java.beans.PropertyEditorSupport;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id$
 */
/*
* this class is used for transforming flow security definitions from property(name=value) to FlowDefinitionSource structure
* found only 1 usage for com.jaspersoft.jasperserver.api.security.FlowRoleAccessVoter */

// TODO: ogavavka SpringUpgrade 22.08.2016 - Maybe some refactoring needed to remove this class
public class FlowDefinitionSourceEditor extends PropertyEditorSupport {

    public void setAsText(String s) throws IllegalArgumentException {
        FlowDefinitionSource source = new FlowDefinitionSource();

        if (s != null && s.length() > 0) {
            PropertiesEditor propertiesEditor = new PropertiesEditor();
            propertiesEditor.setAsText(s);
            Properties props = (Properties) propertiesEditor.getValue();


            for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Entry) it.next();
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                Collection<ConfigAttribute> attr = SecurityConfig.createList(value.split(","));
                source.addFlow(name, attr);
            }
        }

        setValue(source);
    }

}
