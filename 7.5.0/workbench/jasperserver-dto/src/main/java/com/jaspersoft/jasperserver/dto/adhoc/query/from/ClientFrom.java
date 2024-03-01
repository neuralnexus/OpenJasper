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
package com.jaspersoft.jasperserver.dto.adhoc.query.from;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * @author Andriy Godovanets
 * @version $Id: ClientFrom.java 54107 2015-09-28 22:16:32Z schubar $
 */
public class ClientFrom implements DeepCloneable<ClientFrom> {
    private String dataSource;
    private String olapCube;

    public ClientFrom() {
    }

    public ClientFrom(ClientFrom source) {
        checkNotNull(source);

        dataSource = source.getDataSource();
        olapCube = source.getOlapCube();
    }

    public ClientFrom(String dataSource) {
        this.dataSource = dataSource;
    }

    public ClientFrom(String dataSource, String olapCube) {
        this.dataSource = dataSource;
        this.olapCube = olapCube;
    }

    @Override
    public ClientFrom deepClone() {
        return new ClientFrom(this);
    }

    public String getDataSource() {
        return dataSource;
    }

    public ClientFrom setDataSource(String dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public String getOlapCube() {
        return olapCube;
    }

    public ClientFrom setOlapCube(String olapCube) {
        this.olapCube = olapCube;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientFrom from = (ClientFrom) o;

        if (getDataSource() != null ? !getDataSource().equals(from.getDataSource()) : from.getDataSource() != null)
            return false;
        return getOlapCube() != null ? getOlapCube().equals(from.getOlapCube()) : from.getOlapCube() == null;

    }

    @Override
    public int hashCode() {
        int result = getDataSource() != null ? getDataSource().hashCode() : 0;
        result = 31 * result + (getOlapCube() != null ? getOlapCube().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClientFrom{" +
                "dataSource='" + dataSource + '\'' +
                ", olapCube='" + olapCube + '\'' +
                '}';
    }
}
