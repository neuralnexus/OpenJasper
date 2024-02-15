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

package com.jaspersoft.jasperserver.export.modules.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.exolab.castor.types.AnyNode;
import org.exolab.castor.types.DateTime;

import com.jaspersoft.jasperserver.api.JSExceptionWrapper;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 * @version $Id: ReportParameterValueBean.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportParameterValueBean {

	private Object name;
	private Object[] values;

	public ReportParameterValueBean() {
	}
	
	public ReportParameterValueBean(String name, Object[] values) {
		this.name = name;
		this.values = values;
	}
	
	public Object getName() {
		return name;
	}
	
	public void setName(Object name) {
		this.name = name;
	}
	
	public Object[] getValues() {
        return values;
	}
	
	public void setValues(Object[] values) {
		//workaround for http://jira.codehaus.org/browse/CASTOR-1887
        //and JRS Bugzilla bug 30523
		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {
				Object value = values[i];
				if (value instanceof AnyNode) {
					AnyNode node = (AnyNode) value;
					String type = getNodeAttributeValue(node, "type");
					if ("date".equals(type)) {
						String strValue = node.getStringValue();
						if (strValue != null) {
							try {
								DateTime dateTime = new DateTime(strValue);
								values[i] = dateTime.toDate();
							} catch (ParseException e) {
								throw new JSExceptionWrapper(e);
							}
						}
                    } else if ("sql-timestamp".equals(type)) {
                        String strValue = node.getStringValue();
                        if (strValue != null) {
                            try {
                                strValue = strValue.concat("00");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                Date date = simpleDateFormat.parse(strValue);
                                long milliseconds = date.getTime();
                                java.sql.Timestamp timestamp = new java.sql.Timestamp(milliseconds);
                                values[i] = timestamp;
                            } catch (ParseException e) {
                                throw new JSExceptionWrapper(e);
                            }
                        }
					}
				}
			}
		}

		this.values = values;
	}
	
	private static String getNodeAttributeValue(AnyNode node, String attributeName) {
		String val = null;
		for (AnyNode attr = node.getFirstAttribute();
			attr != null;
			attr = attr.getNextSibling()) {
			if (attributeName.equals(attr.getLocalName())) {
				val = attr.getStringValue();
				break;
			}
		}
		return val;
	}
}
