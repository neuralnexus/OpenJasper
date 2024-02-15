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
package com.jaspersoft.jasperserver.dto.connection.metadata;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;
import com.jaspersoft.jasperserver.dto.resources.DataSourceTableDescriptor;

import java.util.List;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class PartialMetadataOptions implements DeepCloneable<PartialMetadataOptions> {
    private List<DataSourceTableDescriptor> includes;
    private List<DataSourceTableDescriptor> expands;

    private Boolean loadReferences;

    public PartialMetadataOptions(){}

    public PartialMetadataOptions(PartialMetadataOptions source){
        checkNotNull(source);

        includes = copyOf(source.getIncludes());
        expands = copyOf(source.getExpands());
    }


    public Boolean getLoadReferences() {
        return loadReferences;
    }

    public PartialMetadataOptions setLoadReferences(Boolean loadReferences) {
        this.loadReferences = loadReferences;
        return this;
    }


    public List<DataSourceTableDescriptor> getIncludes() {
        return includes;
    }

    public PartialMetadataOptions setIncludes(List<DataSourceTableDescriptor> includes) {
        this.includes = includes;
        return this;
    }

    public List<DataSourceTableDescriptor> getExpands() {
        return expands;
    }

    public PartialMetadataOptions setExpands(List<DataSourceTableDescriptor> expands) {
        this.expands = expands;
        return this;
    }

    @Override
    public PartialMetadataOptions deepClone() {
        return new PartialMetadataOptions(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PartialMetadataOptions that = (PartialMetadataOptions) o;

        if (includes != null ? !includes.equals(that.includes) : that.includes != null) return false;
        if (expands != null ? !expands.equals(that.expands) : that.expands != null) return false;
        return loadReferences != null ? loadReferences.equals(that.loadReferences) : that.loadReferences == null;
    }

    @Override
    public int hashCode() {
        int result = includes != null ? includes.hashCode() : 0;
        result = 31 * result + (expands != null ? expands.hashCode() : 0);
        result = 31 * result + (loadReferences != null ? loadReferences.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PartialMetadataOptions{" +
                "includes=" + includes +
                ", expands=" + expands +
                ", loadReferences=" + loadReferences +
                '}';
    }
}
