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

package com.jaspersoft.jasperserver.api.metadata.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Introduced to keep track if external resource is resolvable. Resource is resolved if it's present in metadata
 * (in some cases we mark sources as resolved without check because we cant perform it or don't care).
 *
 * Needed because information about actual source of some object is usually stored as a string or as part of some other complex string (e.g. expression)
 * we may not be able to identify source details without some external class or complex logic. At the same time this operation may be expensive.
 *
 * Use this object to remember  availability on the external resource.
 *
 * {@link SourceDescriptor} is identified by composite id + type. But can be identified by id only in case of partial resolution.
 *
 * @author schubar
 *
 */
public class SourceDescriptor implements Serializable {

    /**
     Five-option field resolution
     If resource is present in metadata with same id and type it will be resolved as {@link Resolution#PATH_ID_AND_TYPE}.
     If resource is present in metadata with same id only it will be resolved as {@link Resolution#PATH_ID}.
     If resource is present in metadata in other set and with same type it will be resolved as {@link Resolution#ITEM_ID_AND_TYPE}.
     If resource is present in metadata in other set it will be resolved as {@link Resolution#ITEM_ID}.
     If resource is not present in metadata it will be resolved as {@link Resolution#NONE}.
     */
    private enum Resolution {
        PATH_ID_AND_TYPE,
        PATH_ID,
        ITEM_ID_AND_TYPE,
        ITEM_ID,
        NONE
    }

    public enum Kind {
        DIMENSION,
        MEASURE,
        ANY // use any we don't need to differentiate
        ;

        public static Kind valueOfOrAny(final String v) {
            try {
                return valueOf(v);
            } catch (IllegalArgumentException e) {
                return ANY;
            }
        }
    }

    private String id;
    private String type;
    private Kind kind = Kind.ANY;
    private Resolution resolution = Resolution.NONE;

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Kind getKind() {
        return kind;
    }

    @JsonIgnore
    public boolean isPathIdResolved() {
        return resolution == Resolution.PATH_ID;
    }

    @JsonIgnore
    public boolean isItemIdAndTypeResolved() {
        return resolution == Resolution.ITEM_ID_AND_TYPE;
    }

    @JsonIgnore
    public boolean isItemIdResolved() {
        return resolution == Resolution.ITEM_ID;
    }

    public boolean isResolved() {
        return resolution == Resolution.PATH_ID_AND_TYPE;
    }

    private SourceDescriptor(String id, String type, Kind kind) {
        checkNotNull(id);

        this.id = id;
        this.type = type;
        this.kind = kind;
    }

    private SourceDescriptor(String id, String type, boolean resolved) {
        this(id, type, Kind.ANY);
        this.resolution = resolved ? Resolution.PATH_ID_AND_TYPE : Resolution.NONE;
    }

    private SourceDescriptor(String id, String type, Kind kind, boolean resolved) {
        this(id, type, kind);
        this.resolution = resolved ? Resolution.PATH_ID_AND_TYPE : Resolution.NONE;
    }


    private SourceDescriptor(SourceDescriptor sourceDescriptor) {
        this(sourceDescriptor.getId(), sourceDescriptor.getType(), sourceDescriptor.getKind(), sourceDescriptor.isResolved());
    }

    public static SourceDescriptor of(String id, String type) {
        return new SourceDescriptor(id, type, Kind.ANY);
    }

    public static SourceDescriptor of(String id, String type, Kind kind) {
        return new SourceDescriptor(id, type, kind);
    }

    public static SourceDescriptor of(String id) {
        return new SourceDescriptor(id, null, Kind.ANY);
    }

    public static SourceDescriptor ofUnresolvable(String id) {
        return new SourceDescriptor(id, null, Kind.ANY, false);
    }

    public static SourceDescriptor ofUnresolvable(String id, String type, Kind kind) {
        return new SourceDescriptor(id, type, kind,false);
    }

    public static SourceDescriptor of(SourceDescriptor originalSourceDescriptor) {
        return new SourceDescriptor(originalSourceDescriptor);
    }

    public static SourceDescriptor ofResolved(String id) {
        return new SourceDescriptor(id, null, Kind.ANY, true);
    }

    public static SourceDescriptor ofResolved(String id, String type) {
        return ofResolved(id, type, Kind.ANY);
    }

    public static SourceDescriptor ofResolved(String id, String type, Kind kind) {
        return new SourceDescriptor(id, type, kind,true);
    }

    public static SourceDescriptor ofResolved(SourceDescriptor originalSourceDescriptor) {
        return of(originalSourceDescriptor).resolved();
    }

    public SourceDescriptor resolvedPathId() {
        this.resolution = Resolution.PATH_ID;
        return this;
    }

    public SourceDescriptor resolvedItemIdAndType() {
        this.resolution = Resolution.ITEM_ID_AND_TYPE;
        return this;
    }

    public SourceDescriptor resolvedItemId() {
        this.resolution = Resolution.ITEM_ID;
        return this;
    }

    public SourceDescriptor resolved() {
        this.resolution = Resolution.PATH_ID_AND_TYPE;
        return this;
    }

    public SourceDescriptor unresolved() {
        this.resolution = Resolution.NONE;
        return this;
    }

    public SourceDescriptor resolveAs(final String type, final Kind kind) {
        this.resolution = Resolution.PATH_ID_AND_TYPE;
        this.type = type;
        this.kind = kind;
        return this;
    }

    public SourceDescriptor classify(final String type, final Kind kind) {
        this.type = type;
        this.kind = kind;
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SourceDescriptor that = (SourceDescriptor) o;
        return Objects.equal(id, that.id) && Objects.equal(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, type);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("type", type)
            .add("kind", kind)
            .add("resolution", resolution)
            .toString();
    }
}
