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
package com.jaspersoft.jasperserver.api.metadata.common.domain.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * <p>Options set for to client conversion.</p>
 * Currently it includes expanded option only, but it can be extended later.
 * For instance it can specify what exactly fields are required and what fields should be excluded.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ToClientConversionOptions {
    /**
     * This property force all referenced resources of target resource to be expanded.
     */
    private boolean expanded;
    private List<String> includes;
    /**
     * The list of referenced resource types, that will be expanded in scope of target resource.
     */
    private Set<String> expandTypes;
    private String acceptMediaType;
    private Map<String, String[]> additionalProperties;
    /**
     * This property has similar behavior as {@link #expanded}. It is intended to control of referenced resources
     * expansion for target resource. But in contrast of {@link #expanded}, that expand all referenced resources,
     * it expand only local resources.
     */
    private boolean inMemoryResource;
    private TimeZone timeZone;
    /**
     * Technical debt... This property should be enabled just in the case when converted client object will be used JUST
     * internally, and nor exposed to the client. It was introduced to address issue
     * http://jira.jaspersoft.com/browse/JRS-15211 - SemanticLayerDataSourceValidator should do conversation of domain
     * datasource from server back to client with secured properties preserved in order to do metadata check by using
     * MetadataProducingDataSourceValidator, that uses ContextManager. But server object validation shouldn't have
     * dependency to client object.
     */
    private boolean allowSecureDataConversation;

    public boolean isEnableEncryption() {
        return enableEncryption;
    }

    public void setEnableEncryption(boolean enableEncryption) {
        this.enableEncryption = enableEncryption;
    }

    private boolean enableEncryption;

    public static ToClientConversionOptions getDefault(){
        return new ToClientConversionOptions();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public ToClientConversionOptions setExpanded(boolean expanded) {
        this.expanded = expanded;
        return this;
    }

    public boolean isInMemoryResource() {
        return inMemoryResource;
    }

    public ToClientConversionOptions setInMemoryResource(boolean inMemoryResource) {
        this.inMemoryResource = inMemoryResource;
        return this;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public ToClientConversionOptions setIncludes(List<String> includes) {
        this.includes = includes;
        return this;
    }

    public Set<String> getExpandTypes() {
        return expandTypes;
    }

    public ToClientConversionOptions setExpandTypes(Set<String> expandTypes) {
        this.expandTypes = expandTypes;
        return this;
    }

    /**
     * Checks whether <tt>this</tt> options has at least one expansion option enabled. It was introduced for performance
     * purposes.
     *
     * @param localResource true if resource is local.
     * @return <tt>true</tt> if <tt>this</tt> options has at least one expansion option enabled.
     */
    public boolean isExpansionEnabled(boolean localResource) {
        return isExpanded() || isExpandLocal(localResource) || isNotEmpty(getExpandTypes());
    }

    /**
     * Checks if referenced resource of main resource with target <tt>clientType</tt> will be expanded in scope of main
     * resource.
     *
     * @param clientType client type.
     * @param localResource <tt>true</tt> if resource is local.
     *
     * @return <tt>true</tt> if referenced resource of main resource should be expanded in scope of main resource.
     */
    public boolean isExpanded(String clientType, boolean localResource) {
        return isExpanded() || isExpandLocal(localResource) ||
                isNotEmpty(getExpandTypes()) && getExpandTypes().contains(clientType);
    }

    /**
     * Checks whether expansion will be occurred only by specific expand types. In this case expand all and expand local
     * resources should be disable.
     *
     * @param localResource true if resource is local.
     * @return <tt>true</tt> if expansion will be occurred by specific client types.
     */
    public boolean isExpansionByType(boolean localResource) {
        return !(isExpanded() || isExpandLocal(localResource)) && isNotEmpty(getExpandTypes());
    }

    private boolean isExpandLocal(boolean localResource) {
        return isInMemoryResource() && localResource;
    }

    public String getAcceptMediaType() {
        return acceptMediaType;
    }

    public ToClientConversionOptions setAcceptMediaType(String acceptMediaType) {
        this.acceptMediaType = acceptMediaType;
        return this;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public ToClientConversionOptions setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }

    public Map<String, String[]> getAdditionalProperties() {
        return additionalProperties;
    }

    public ToClientConversionOptions setAdditionalProperties(Map<String, String[]> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    public boolean isAllowSecureDataConversation() {
        return allowSecureDataConversation;
    }

    public ToClientConversionOptions setAllowSecureDataConversation(boolean allowSecureDataConversation) {
        this.allowSecureDataConversation = allowSecureDataConversation;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToClientConversionOptions that = (ToClientConversionOptions) o;

        if (expanded != that.expanded) return false;
        if (inMemoryResource != that.inMemoryResource) return false;
        if (allowSecureDataConversation != that.allowSecureDataConversation) return false;
        if (includes != null ? !includes.equals(that.includes) : that.includes != null) return false;
        if (expandTypes != null ? !expandTypes.equals(that.expandTypes) : that.expandTypes != null) return false;
        if (acceptMediaType != null ? !acceptMediaType.equals(that.acceptMediaType) : that.acceptMediaType != null)
            return false;
        if (additionalProperties != null ? !additionalProperties.equals(that.additionalProperties) : that.additionalProperties != null)
            return false;
        return timeZone != null ? timeZone.equals(that.timeZone) : that.timeZone == null;
    }

    @Override
    public int hashCode() {
        int result = (expanded ? 1 : 0);
        result = 31 * result + (includes != null ? includes.hashCode() : 0);
        result = 31 * result + (expandTypes != null ? expandTypes.hashCode() : 0);
        result = 31 * result + (acceptMediaType != null ? acceptMediaType.hashCode() : 0);
        result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
        result = 31 * result + (inMemoryResource ? 1 : 0);
        result = 31 * result + (timeZone != null ? timeZone.hashCode() : 0);
        result = 31 * result + (allowSecureDataConversation ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToClientConversionOptions{" +
                "expanded=" + expanded +
                ", includes=" + includes +
                ", expandTypes=" + expandTypes +
                ", acceptMediaType='" + acceptMediaType + '\'' +
                ", additionalProperties=" + additionalProperties +
                ", inMemoryResource=" + inMemoryResource +
                ", timeZone=" + timeZone +
                ", allowSecureDataConversation=" + allowSecureDataConversation +
                '}';
    }
}
