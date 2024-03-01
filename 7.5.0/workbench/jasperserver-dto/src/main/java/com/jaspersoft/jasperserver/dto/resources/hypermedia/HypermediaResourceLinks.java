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

package com.jaspersoft.jasperserver.dto.resources.hypermedia;

import com.jaspersoft.jasperserver.dto.common.DeepCloneable;

import static com.jaspersoft.jasperserver.dto.utils.ValueObjectUtils.checkNotNull;

/**
 * Created by borys.kolesnykov on 9/23/2014.
 */
public class HypermediaResourceLinks implements DeepCloneable<HypermediaResourceLinks> {

    private String self;
    private String content;

    public HypermediaResourceLinks() {
    }

    public HypermediaResourceLinks(HypermediaResourceLinks other) {
        checkNotNull(other);

        this.self = other.getSelf();
        this.content = other.getContent();
    }

    @Override
    public HypermediaResourceLinks deepClone() {
        return new HypermediaResourceLinks(this);
    }

    public String getSelf() {
        return self;
    }

    public HypermediaResourceLinks setSelf(String self) {
        this.self = self;
        return this;
    }

    public String getContent() {
        return content;
    }

    public HypermediaResourceLinks setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HypermediaResourceLinks that = (HypermediaResourceLinks) o;

        if (self != null ? !self.equals(that.self) : that.self != null) return false;
        return content != null ? content.equals(that.content) : that.content == null;
    }

    @Override
    public int hashCode() {
        int result = self != null ? self.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HypermediaResourceLinks{" +
                "self='" + self + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
