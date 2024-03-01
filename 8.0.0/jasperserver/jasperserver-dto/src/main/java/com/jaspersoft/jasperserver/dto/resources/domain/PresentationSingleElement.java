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
package com.jaspersoft.jasperserver.dto.resources.domain;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class PresentationSingleElement extends PresentationElement<PresentationSingleElement> {
    private String maskId;
    private String mask;
    private String aggregation;
    private Kind kind;
    private String type;
    private String hierarchicalName;
    public PresentationSingleElement(){}

    public PresentationSingleElement(PresentationSingleElement source){
        super(source);
        maskId = source.getMaskId();
        mask = source.getMask();
        aggregation = source.getAggregation();
        kind = source.getKind();
        type = source.getType();
        hierarchicalName = source.getHierarchicalName();
    }

    @Override
    public PresentationSingleElement deepClone() {
        return new PresentationSingleElement(this);
    }

    public String getHierarchicalName() {
        return hierarchicalName;
    }

    public PresentationSingleElement setHierarchicalName(String hierarchicalName) {
        this.hierarchicalName = hierarchicalName;
        return this;
    }

    public String getResourcePath() {
        return super.getResourcePath();
    }

    public String getMaskId() {
        return maskId;
    }

    public PresentationSingleElement setMaskId(String maskId) {
        this.maskId = maskId;
        return this;
    }

    public String getMask() {
        return mask;
    }

    public PresentationSingleElement setMask(String mask) {
        this.mask = mask;
        return this;
    }

    public String getAggregation() {
        return aggregation;
    }

    public PresentationSingleElement setAggregation(String aggregation) {
        this.aggregation = aggregation;
        return this;
    }

    public Kind getKind() {
        return kind;
    }

    public PresentationSingleElement setKind(Kind kind) {
        this.kind = kind;
        return this;
    }

    public String getType() {
        return type;
    }

    public PresentationSingleElement setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PresentationSingleElement that = (PresentationSingleElement) o;

        if (aggregation != null ? !aggregation.equals(that.aggregation) : that.aggregation != null) return false;
        if (hierarchicalName != null ? !hierarchicalName.equals(that.hierarchicalName) : that.hierarchicalName != null)
            return false;
        if (kind != that.kind) return false;
        if (mask != null ? !mask.equals(that.mask) : that.mask != null) return false;
        if (maskId != null ? !maskId.equals(that.maskId) : that.maskId != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (maskId != null ? maskId.hashCode() : 0);
        result = 31 * result + (mask != null ? mask.hashCode() : 0);
        result = 31 * result + (aggregation != null ? aggregation.hashCode() : 0);
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (hierarchicalName != null ? hierarchicalName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PresentationSingleElement{" +
                "maskId='" + maskId + '\'' +
                ", mask='" + mask + '\'' +
                ", aggregation='" + aggregation + '\'' +
                ", kind=" + kind +
                ", type='" + type + '\'' +
                ", hierarchicalName='" + hierarchicalName + '\'' +
                "} " + super.toString();
    }

    public enum Kind {
        level, measure;
    }
}
