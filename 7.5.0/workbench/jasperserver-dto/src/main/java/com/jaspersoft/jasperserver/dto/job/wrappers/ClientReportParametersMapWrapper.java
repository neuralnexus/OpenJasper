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

package com.jaspersoft.jasperserver.dto.job.wrappers;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.hashCodeOfMapWithArraysAsValues;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.isMapsWithArraysAsValuesEquals;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
@XmlRootElement(name = "parameters")
public class ClientReportParametersMapWrapper implements DeepCloneable<ClientReportParametersMapWrapper> {
    private HashMap<String, String[]> parameterValues;

    public ClientReportParametersMapWrapper() {
    }

    public ClientReportParametersMapWrapper(HashMap<String, String[]> parameterValues) {
        if (parameterValues == null) {
            return;
        }
        this.parameterValues = new LinkedHashMap<String, String[]>();
        for (Map.Entry<String, String[]> entry : parameterValues.entrySet()) {
            this.parameterValues.put(entry.getKey(), entry.getValue());
        }
    }

    public ClientReportParametersMapWrapper(ClientReportParametersMapWrapper other) {
        checkNotNull(other);

        parameterValues = copyOf(other.getParameterValues());
    }

    public HashMap<String, String[]> getParameterValues() {
        return parameterValues;
    }

    public ClientReportParametersMapWrapper setParameterValues(HashMap<String, String[]> parameterValues) {
        this.parameterValues = parameterValues;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientReportParametersMapWrapper that = (ClientReportParametersMapWrapper) o;

        if (!isMapsWithArraysAsValuesEquals(parameterValues, that.parameterValues))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCodeOfMapWithArraysAsValues(parameterValues);
    }

    @Override
    public String toString() {
        return "ClientReportParametersMapWrapper{" +
                "parameterValues=" + parameterValues +
                '}';
    }

    @Override
    public ClientReportParametersMapWrapper deepClone() {
        return new ClientReportParametersMapWrapper(this);
    }
}
