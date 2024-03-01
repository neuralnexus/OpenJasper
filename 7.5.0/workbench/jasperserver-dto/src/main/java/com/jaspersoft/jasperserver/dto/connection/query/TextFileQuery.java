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
import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.copyOf;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TextFileQuery implements DeepCloneable<TextFileQuery> {
    private Integer offset;
    private Integer limit;
    private TextFileSelect select;
    private TextFileConvert convert;

    public TextFileQuery(){}

    public TextFileQuery(TextFileQuery source){
        checkNotNull(source);

        offset = source.getOffset();
        limit = source.getLimit();
        select = copyOf(source.getSelect());
        convert = copyOf(source.getConvert());
    }

    public TextFileConvert getConvert() {
        return convert;
    }

    public TextFileQuery setConvert(TextFileConvert convert) {
        this.convert = convert;
        return this;
    }

    public TextFileSelect getSelect() {
        return select;
    }

    public TextFileQuery setSelect(TextFileSelect select) {
        this.select = select;
        return this;
    }

    public Integer getOffset() {
        return offset;
    }

    public TextFileQuery setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    public Integer getLimit() {
        return limit;
    }

    public TextFileQuery setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextFileQuery)) return false;

        TextFileQuery that = (TextFileQuery) o;

        if (convert != null ? !convert.equals(that.convert) : that.convert != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        if (offset != null ? !offset.equals(that.offset) : that.offset != null) return false;
        if (select != null ? !select.equals(that.select) : that.select != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = offset != null ? offset.hashCode() : 0;
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (select != null ? select.hashCode() : 0);
        result = 31 * result + (convert != null ? convert.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TextFileQuery{" +
                "offset=" + offset +
                ", limit=" + limit +
                ", select=" + select +
                ", convert=" + convert +
                '}';
    }

    /*
     * DeepCloneable
     */

    @Override
    public TextFileQuery deepClone() {
        return new TextFileQuery(this);
    }
}
