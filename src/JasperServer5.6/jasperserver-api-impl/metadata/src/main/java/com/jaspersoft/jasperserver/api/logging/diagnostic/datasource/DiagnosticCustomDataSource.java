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
package com.jaspersoft.jasperserver.api.logging.diagnostic.datasource;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author vsabadosh
 * @version $Id: DiagnosticCustomDataSource.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class DiagnosticCustomDataSource implements JRDataSource {

    /**
     *
     */
    private List<List<Object>> data;


    private int index = -1;

    /**
     *
     */
    public DiagnosticCustomDataSource(List<List<Object>> data)
    {
        this.data = data;
    }

    /**
     *
     */
    public boolean next() throws JRException
    {
        index++;
        return (index < data.size());
    }

    /**
     *
     */
    public Object getFieldValue(JRField field) throws JRException
    {
        Object value = null;

        String fieldName = field.getName();

        if ("Section".equals(fieldName))
        {
            value = data.get(index).get(0);
        }
        else if ("Attribute".equals(fieldName))
        {
            value = data.get(index).get(1);
        }
        else if ("Value".equals(fieldName))
        {
            Object attributeValue = data.get(index).get(2);
            if (attributeValue instanceof Map) {
                value =  transformToList((Map<String, Object>)attributeValue);
            } else if (attributeValue instanceof List) {
                List<String> list = createOverrideList();
                list.addAll((List<String>)attributeValue);
                value = list;
            } else {
                List<String> list = createOverrideList();
                if (attributeValue != null) {
                    list.add(attributeValue.toString());
                    value = list;
                } else {
                    list.add("");
                    value = list;
                }
            }
        }
        else if ("Description".equals(fieldName)) {
            value = data.get(index).get(3);
        }
        return value;
    }

    List<String> transformToList(Map<String, Object> map) {
        List<String> list = createOverrideList();
        if (map !=null && map.keySet().size() > 0) {
            for (String key : map.keySet()) {
                if (map.get(key) != null) {
                    list.add(key + " = "+ map.get(key).toString());
                } else {
                    list.add(key + " = "+ "");
                }
            }
        }
        return list;
    }
    
    private List<String> createOverrideList() {
        return new ArrayList<String>() {
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                if (this.size() == 1) {
                    sb.append(this.get(0));
                    return sb.toString();
                } else {
                    Iterator<String> iterator = this.iterator();
                    while (iterator.hasNext()) {
                        String element = iterator.next();
                        sb.append(StringEscapeUtils.escapeJava(element));
                        if (iterator.hasNext()) {
                            sb.append("\n");
                        }
                    }
                }
                return sb.toString();
            }
        };
    }
    
}
