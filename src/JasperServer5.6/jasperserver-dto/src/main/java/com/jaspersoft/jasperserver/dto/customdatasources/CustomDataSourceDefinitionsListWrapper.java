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

package com.jaspersoft.jasperserver.dto.customdatasources;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: CustomDataSourceDefinitionsListWrapper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "definitions")
public class CustomDataSourceDefinitionsListWrapper {
    private List<String> definitions;

    public CustomDataSourceDefinitionsListWrapper(){}

    public CustomDataSourceDefinitionsListWrapper(CustomDataSourceDefinitionsListWrapper source){
        this.definitions = new ArrayList<String>(source.getDefinitions());
    }

    public CustomDataSourceDefinitionsListWrapper(List<String> definitions){
        this.definitions = definitions;
    }

    @XmlElement(name = "definition")
    public List<String> getDefinitions() {
        return definitions;
    }

    public CustomDataSourceDefinitionsListWrapper setDefinitions(List<String> definitions) {
        this.definitions = definitions;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomDataSourceDefinitionsListWrapper that = (CustomDataSourceDefinitionsListWrapper) o;

        if (definitions != null ? !definitions.equals(that.definitions) : that.definitions != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return definitions != null ? definitions.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CustomDataSourceDefinitionsListWrapper{" +
                "definitions=" + definitions +
                "} " + super.toString();
    }
}
