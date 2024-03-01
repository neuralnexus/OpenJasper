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
package com.jaspersoft.jasperserver.dto.connection.query;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TextFileCastConversionRule implements DeepCloneable<TextFileCastConversionRule> {
    private String column;
    private String type;

    public TextFileCastConversionRule(){
    }

    public TextFileCastConversionRule (TextFileCastConversionRule source){
        checkNotNull(source);

        this.column = source.getColumn();
        this.type = source.getType();
    }

    public String getColumn() {
        return column;
    }

    public TextFileCastConversionRule setColumn(String column) {
        this.column = column;
        return this;
    }

    public String getType() {
        return type;
    }

    public TextFileCastConversionRule setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFileCastConversionRule)) return false;

        TextFileCastConversionRule that = (TextFileCastConversionRule) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TextFileCastConversionRule{" +
                "column='" + column + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    /*
     * DeepCloneable
     */

    @Override
    public TextFileCastConversionRule deepClone() {
        return new TextFileCastConversionRule(this);
    }
}
