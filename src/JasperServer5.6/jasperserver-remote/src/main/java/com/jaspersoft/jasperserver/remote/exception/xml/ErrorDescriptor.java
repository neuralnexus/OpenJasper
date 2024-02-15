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
package com.jaspersoft.jasperserver.remote.exception.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: ErrorDescriptor.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement
public class ErrorDescriptor {
    public static final String ERROR_CODE_UNEXPECTED_ERROR = "unexpected.error";
    private String message;
    private String errorCode;
    private String[] parameters;

    public ErrorDescriptor() {
    }

    public ErrorDescriptor(Throwable cause) {
        this.errorCode = ERROR_CODE_UNEXPECTED_ERROR;
        final String causeMessage = cause.getMessage();
        this.message = causeMessage != null && !causeMessage.isEmpty() ? causeMessage : "Unexpected error";
        final StringWriter trace = new StringWriter();
        final PrintWriter traceWriter = new PrintWriter(trace);
        cause.printStackTrace(traceWriter);
        this.parameters = new String[]{trace.toString()};
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @XmlElementWrapper(name = "parameters")
    @XmlElement(name = "parameter")
    public String[] getParameters() {
        return parameters;
    }

    public void setParameters(String... parameters) {
        this.parameters = parameters;
    }

    public void setParameters(Object... args) {
        if (args != null && args.length > 0) {
            List<String> values = new LinkedList<String>();
            for (Object arg : args) {
                if (arg != null) {
                    values.add(arg.toString());
                }
            }
            if (!values.isEmpty()) {
                parameters = values.toArray(new String[values.size()]);
            }
        }
    }

    @Override
    public String toString() {
        return "ErrorDescriptor{" +
                "message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }

    public static class Builder {
        private ErrorDescriptor errorDescriptor = new ErrorDescriptor();

        public Builder setMessage(String message) {
            this.errorDescriptor.setMessage(message);
            return this;
        }

        public Builder setErrorCode(String errorCode) {
            this.errorDescriptor.setErrorCode(errorCode);
            return this;
        }

        public Builder setParameters(String... parameters) {
            this.errorDescriptor.setParameters(parameters);
            return this;
        }

        public Builder setParameters(Object... args) {
            this.errorDescriptor.setParameters(args);
            return this;
        }

        public ErrorDescriptor getErrorDescriptor() {
            return errorDescriptor;
        }
    }
}
