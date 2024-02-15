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

package com.jaspersoft.jasperserver.sample;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;

import net.sf.jasperreports.engine.JRPrintHyperlink;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameter;
import net.sf.jasperreports.engine.JRPrintHyperlinkParameters;
import net.sf.jasperreports.engine.export.JRHyperlinkProducer;

/**
 * @author lucian
 */
public class ReportExecutionHyperlinkProducer implements JRHyperlinkProducer {

    public String getHyperlink(JRPrintHyperlink hyperlink) {
        StringBuffer sb = new StringBuffer();
        sb.append("executeReport.jsp?");

        JRPrintHyperlinkParameters parameters = hyperlink.getHyperlinkParameters();
        if (parameters != null) {
            for (Iterator it = parameters.getParameters().iterator(); it.hasNext();) {
                JRPrintHyperlinkParameter parameter = (JRPrintHyperlinkParameter) it.next();
                if (parameter.getName().equals("_report")) {
                    appendParameter(sb, "uri", (String) parameter.getValue());
                } else if (parameter.getName().equals("_output")) {
                    appendParameter(sb, "format", (String) parameter.getValue());
                } else {
                    appendParameter(sb, parameter);
                }
            }
        }

        return sb.toString();
    }

    protected void appendParameter(StringBuffer sb, JRPrintHyperlinkParameter parameter) {
        String name = parameter.getName();
        String valueClassName = parameter.getValueClass();
        Class valueClass = loadClass(valueClassName);
        Object value = parameter.getValue();
        if (valueClass.equals(String.class)) {
            appendParameter(sb, name, (String) value);
        } else if (valueClass.equals(Boolean.class)) {
            if (value != null && ((Boolean) value).booleanValue()) {
                appendParameter(sb, name, "true");
            }
        } else if (Number.class.isAssignableFrom(valueClass)) {
            if (value != null) {
                appendParameter(sb, name, value.toString());
            }
        } else if (Date.class.isAssignableFrom(valueClass)) {
            if (value != null) {
                appendParameter(sb, name, Long.toString(((Date) value).getTime()));
            }
        }
    }

    protected Class loadClass(String valueClassName) {
        try {
            return Class.forName(valueClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void appendParameter(StringBuffer sb, String name, String value) {
        if (!sb.substring(sb.length() - 1, sb.length()).equals("?")) {
            sb.append('&');
        }
        sb.append(encode(name));

        if (value != null) {
            sb.append('=');
            sb.append(encode(value));
        }
    }

    protected String encode(String text) {
        try {
            return URLEncoder.encode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
