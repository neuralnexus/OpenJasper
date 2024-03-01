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
package com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.impl.datasource;

import java.io.Serializable;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class RepoCustomDataSourceProperty implements Serializable {
    private String name;
    private String value;
    private RepoCustomDataSource dataSource;
    
    public RepoCustomDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(RepoCustomDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public static final RepoCustomDataSourceProperty newProperty(RepoCustomDataSource source){
    	RepoCustomDataSourceProperty result = new RepoCustomDataSourceProperty();
    	result.setDataSource(source);
    	return  result;
    }
    
    public String getName() {
        return name;
    }

    public RepoCustomDataSourceProperty setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public RepoCustomDataSourceProperty setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RepoCustomDataSourceProperty other = (RepoCustomDataSourceProperty) obj;
		if (dataSource == null) {
			if (other.dataSource != null)
				return false;
		} else if (!dataSource.equals(other.dataSource))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

    @Override
    public String toString() {
        return "RepoCustomDataSourceProperty{" +
                "id=" + dataSource +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
