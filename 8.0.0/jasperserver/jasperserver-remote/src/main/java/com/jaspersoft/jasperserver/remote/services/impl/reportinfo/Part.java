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
package com.jaspersoft.jasperserver.remote.services.impl.reportinfo;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Narcis Marcu (nmarcu@tibco.com)
 */
public class Part {
    private Integer idx;
    private String name;

    public Part(Integer _idx, String _name) {
        idx = _idx;
        name = _name;
    }

    @XmlElement
    public Integer getIdx() {
        return idx;
    }

    @XmlElement
    public String getName() {
        return name;
    }
}
