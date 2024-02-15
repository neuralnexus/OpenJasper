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

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: InvertibleValidationRule.java 47331 2014-07-18 09:13:06Z kklein $
 */
public abstract class InvertibleValidationRule<BuilderType extends InvertibleValidationRule<BuilderType>>
        extends ValidationRule<BuilderType> {
    private Boolean inverted = false;

    public InvertibleValidationRule(){}

    public InvertibleValidationRule(InvertibleValidationRule other) {
        super(other);
        this.inverted = other.isInverted();
    }

    public Boolean isInverted() {
        return inverted;
    }

    // definition of subclasses assures cast safety.
    @SuppressWarnings("unchecked")
    public BuilderType setInverted(Boolean inverted) {
        this.inverted = inverted;
        return (BuilderType) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InvertibleValidationRule)) return false;
        if (!super.equals(o)) return false;

        InvertibleValidationRule that = (InvertibleValidationRule) o;

        if (inverted != null ? !inverted.equals(that.inverted) : that.inverted != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (inverted != null ? inverted.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvertibleValidationRule{" +
                "inverted=" + inverted +
                "} " + super.toString();
    }
}
