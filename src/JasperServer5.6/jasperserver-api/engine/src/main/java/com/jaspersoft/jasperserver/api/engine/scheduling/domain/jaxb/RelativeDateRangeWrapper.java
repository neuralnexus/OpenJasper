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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import net.sf.jasperreports.types.date.RelativeDateRange;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: RelativeDateRangeWrapper.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement(name = "relativeDateRange")
public class RelativeDateRangeWrapper {
    private RelativeDateRange relativeDateRange;
    public RelativeDateRangeWrapper(){
    }

    public RelativeDateRangeWrapper(RelativeDateRange range){
        relativeDateRange = range;
    }
    @XmlElement
    public String getExpression(){
        return relativeDateRange != null ? relativeDateRange.getExpression() : null;
    }

    public void setExpression(String expression){
        relativeDateRange = new RelativeDateRange(expression);
    }

    @XmlTransient
    public RelativeDateRange getRelativeDateRange(){
        return relativeDateRange;
    }
}
