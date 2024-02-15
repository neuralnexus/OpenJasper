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
package com.jaspersoft.jasperserver.dto.jdbcdrivers;

import com.jaspersoft.jasperserver.dto.resources.ClientProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: JdbcDriverInfo.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement
public class JdbcDriverInfo {
    private String name;
    private String label;
    private Boolean available;
    private String jdbcUrl;
    private Boolean isDefault;
    private String jdbcDriverClass;
    private List<ClientProperty> defaultValues;

    public JdbcDriverInfo(){
    }

    public JdbcDriverInfo(JdbcDriverInfo source){
        name = source.getName();
        label = source.getLabel();
        available = source.getAvailable();
        jdbcUrl = source.getJdbcUrl();
        isDefault = source.isDefault();
        jdbcDriverClass = source.getJdbcDriverClass();
        final List<ClientProperty> sourceDefaultValues = source.getDefaultValues();
        if(sourceDefaultValues != null){
            defaultValues = new ArrayList<ClientProperty>(sourceDefaultValues.size());
            for(ClientProperty property : sourceDefaultValues){
                defaultValues.add(new ClientProperty(property));
            }
        }
    }

    public String getName() {
        return name;
    }

    public JdbcDriverInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public JdbcDriverInfo setLabel(String label) {
        this.label = label;
        return this;
    }

    public Boolean getAvailable() {
        return available;
    }

    public JdbcDriverInfo setAvailable(Boolean available) {
        this.available = available;
        return this;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public JdbcDriverInfo setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        return this;
    }

    @XmlElement(name = "isDefault")
    public Boolean isDefault() {
        return isDefault;
    }

    public JdbcDriverInfo setDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }

    public String getJdbcDriverClass() {
        return jdbcDriverClass;
    }

    public JdbcDriverInfo setJdbcDriverClass(String jdbcDriverClass) {
        this.jdbcDriverClass = jdbcDriverClass;
        return this;
    }

    public List<ClientProperty> getDefaultValues() {
        return defaultValues;
    }

    public JdbcDriverInfo setDefaultValues(List<ClientProperty> defaultValues) {
        this.defaultValues = defaultValues;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JdbcDriverInfo info = (JdbcDriverInfo) o;

        if (available != null ? !available.equals(info.available) : info.available != null) return false;
        if (defaultValues != null ? !defaultValues.equals(info.defaultValues) : info.defaultValues != null)
            return false;
        if (isDefault != null ? !isDefault.equals(info.isDefault) : info.isDefault != null) return false;
        if (jdbcDriverClass != null ? !jdbcDriverClass.equals(info.jdbcDriverClass) : info.jdbcDriverClass != null)
            return false;
        if (jdbcUrl != null ? !jdbcUrl.equals(info.jdbcUrl) : info.jdbcUrl != null) return false;
        if (label != null ? !label.equals(info.label) : info.label != null) return false;
        if (name != null ? !name.equals(info.name) : info.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (available != null ? available.hashCode() : 0);
        result = 31 * result + (jdbcUrl != null ? jdbcUrl.hashCode() : 0);
        result = 31 * result + (isDefault != null ? isDefault.hashCode() : 0);
        result = 31 * result + (jdbcDriverClass != null ? jdbcDriverClass.hashCode() : 0);
        result = 31 * result + (defaultValues != null ? defaultValues.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "JdbcDriverInfo{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", available=" + available +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", isDefault=" + isDefault +
                ", jdbcDriverClass='" + jdbcDriverClass + '\'' +
                ", defaultValues=" + defaultValues +
                '}';
    }
}
