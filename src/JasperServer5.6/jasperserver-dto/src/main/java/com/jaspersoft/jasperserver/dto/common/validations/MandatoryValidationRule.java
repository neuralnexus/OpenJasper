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
package com.jaspersoft.jasperserver.dto.common.validations;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: MandatoryValidationRule.java 47331 2014-07-18 09:13:06Z kklein $
 */
@XmlRootElement
public class MandatoryValidationRule extends ValidationRule<MandatoryValidationRule>{
    public static final String ERROR_KEY = "fillParameters.error.mandatoryField";

    public MandatoryValidationRule(){
    }

    public MandatoryValidationRule(MandatoryValidationRule other) {
        super(other);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getClass().getName().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MandatoryValidationRule)) return false;
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "MandatoryValidationRule{} " + super.toString();
    }
}
