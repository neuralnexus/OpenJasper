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

package com.jaspersoft.jasperserver.dto.adhoc.datasource;


import java.io.Serializable;

/**
 * Field that belongs to some dimension
 *
 * @author Andriy Godovanets
 */
public class ClientDataSourceLevel extends ClientDataSourceField implements Serializable {

    private String dimensionName;

    public ClientDataSourceLevel() {
    }

    public ClientDataSourceLevel(ClientDataSourceLevel other) {
        super(other);
        this.dimensionName = other.dimensionName;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public ClientDataSourceLevel setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
        return this;
    }

    public ClientDataSourceLevel setClientDataSourceField(ClientDataSourceField field) {
        setName(field.getName());
        setType(field.getType());
        setFormat(field.getFormat());

        setHierarchyName(field.getHierarchyName());

        setAggregateExpression(field.getAggregateExpression());
        setAggregateArg(field.getAggregateArg());
        setAggregateType(field.getAggregateType());
        setAggregateFunction(field.getAggregateFunction());
        setAggregateFirstLevelFunction(field.getAggregateFirstLevelFunction());
        return this;
    }

    @Override
    public ClientDataSourceLevel setAggregateArg(String aggregateArg) {
        return (ClientDataSourceLevel) super.setAggregateArg(aggregateArg);
    }

    @Override
    public ClientDataSourceLevel setAggregateExpression(String aggregateExpression) {
        return (ClientDataSourceLevel) super.setAggregateExpression(aggregateExpression);
    }

    @Override
    public ClientDataSourceLevel setAggregateFunction(String aggregateFunction) {
        return (ClientDataSourceLevel) super.setAggregateFunction(aggregateFunction);
    }

    public ClientDataSourceLevel setAggregateFirstLevelFunction(String aggregateFirstLevelFunction) {
        return (ClientDataSourceLevel) super.setAggregateFirstLevelFunction(aggregateFirstLevelFunction);
    }

    @Override
    public ClientDataSourceLevel setAggregateType(String aggregateType) {
        return (ClientDataSourceLevel) super.setAggregateType(aggregateType);
    }

    @Override
    public ClientDataSourceLevel setFormat(String format) {
        return (ClientDataSourceLevel) super.setFormat(format);
    }

    @Override
    public ClientDataSourceLevel setHierarchyName(String hierarchyName) {
        return (ClientDataSourceLevel) super.setHierarchyName(hierarchyName);
    }

    @Override
    public ClientDataSourceLevel setName(String name) {
        return (ClientDataSourceLevel) super.setName(name);
    }

    @Override
    public ClientDataSourceLevel setType(String type) {
        return (ClientDataSourceLevel) super.setType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientDataSourceLevel)) return false;
        if (!super.equals(o)) return false;

        ClientDataSourceLevel that = (ClientDataSourceLevel) o;

        return getDimensionName() != null ? getDimensionName().equals(that.getDimensionName()) : that.getDimensionName() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getDimensionName() != null ? getDimensionName().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientDataSourceLevel{");
        sb.append("dimensionName='").append(getDimensionName()).append('\'');
        sb.append(", name='").append(getName()).append('\'');
        sb.append(", hierarchyName='").append(getHierarchyName()).append('\'');
        sb.append(", type='").append(getType()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public ClientDataSourceLevel deepClone() {
        return new ClientDataSourceLevel(this);
    }
}
