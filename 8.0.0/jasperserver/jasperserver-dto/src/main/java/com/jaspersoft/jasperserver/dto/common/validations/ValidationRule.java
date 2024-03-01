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
package com.jaspersoft.jasperserver.dto.common.validations;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * Builder pattern requires to return concrete type of object by all it's setters.
 * Therefore generic parameter BuilderType is introduced.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public abstract class ValidationRule<BuilderType extends ValidationRule<BuilderType>> implements DeepCloneable<BuilderType> {
    private String errorMessage;

    public ValidationRule() {
    }

    public ValidationRule(ValidationRule other) {
        checkNotNull(other);

        this.errorMessage = other.getErrorMessage();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // definition of subclasses assures cast safety.
    @SuppressWarnings("unchecked")
    public BuilderType setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return (BuilderType) this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationRule)) return false;

        ValidationRule<?> that = (ValidationRule<?>) o;

        return errorMessage != null ? errorMessage.equals(that.errorMessage) : that.errorMessage == null;
    }

    @Override
    public int hashCode() {
        return errorMessage != null ? errorMessage.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ValidationRule{" +
                "errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
