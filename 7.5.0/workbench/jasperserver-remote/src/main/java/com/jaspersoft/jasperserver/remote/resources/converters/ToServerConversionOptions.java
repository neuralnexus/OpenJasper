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

package com.jaspersoft.jasperserver.remote.resources.converters;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ToServerConversionOptions {
    private static final Class<?>[] EMPTY_GROUPS = {};
    private boolean allowReferencesOnly, resetVersion, suppressValidation, skipRepoFieldsValidation;
    private String ownersUri;
    private Map<String, InputStream> attachments;
    private Map<String, String[]> additionalProperties;
    private Class<?>[] validationGroups;
    public static final String SKIP_DATA_BASE_METADATA_CHECK = "skipDataBaseMetadataCheck";

    public static ToServerConversionOptions getDefault() {
        return new ToServerConversionOptions();
    }

    public boolean isSkipRepoFieldsValidation() {
        return skipRepoFieldsValidation;
    }

    public ToServerConversionOptions setSkipRepoFieldsValidation(boolean skipRepoFieldsValidation) {
        this.skipRepoFieldsValidation = skipRepoFieldsValidation;
        return this;
    }

    public Map<String, InputStream> getAttachments() {
        return attachments;
    }

    public ToServerConversionOptions setAttachments(Map<String, InputStream> attachments) {
        this.attachments = attachments;
        return this;
    }

    public boolean isAllowReferencesOnly() {
        return allowReferencesOnly;
    }

    public ToServerConversionOptions setAllowReferencesOnly(boolean allowReferencesOnly) {
        this.allowReferencesOnly = allowReferencesOnly;
        return this;
    }

    public String getOwnersUri() {
        return ownersUri;
    }

    public ToServerConversionOptions setOwnersUri(String ownersUri) {
        this.ownersUri = ownersUri;
        return this;
    }

    public boolean isResetVersion() {
        return resetVersion;
    }

    public ToServerConversionOptions setResetVersion(boolean resetVersion) {
        this.resetVersion = resetVersion;
        return this;
    }

    public boolean isSuppressValidation() {
        return suppressValidation;
    }

    public ToServerConversionOptions setSuppressValidation(boolean suppressValidation) {
        this.suppressValidation = suppressValidation;
        return this;
    }

    public Map<String, String[]> getAdditionalProperties() {
        return additionalProperties;
    }

    public ToServerConversionOptions setAdditionalProperties(Map<String, String[]> additionalProperties) {
        this.additionalProperties = additionalProperties;
        return this;
    }

    public Class<?>[] getValidationGroups() {
        return validationGroups == null ? EMPTY_GROUPS : validationGroups;
    }

    public void setValidationGroups(Class<?>[] validationGroups) {
        this.validationGroups = validationGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToServerConversionOptions that = (ToServerConversionOptions) o;

        if (allowReferencesOnly != that.allowReferencesOnly) return false;
        if (resetVersion != that.resetVersion) return false;
        if (suppressValidation != that.suppressValidation) return false;
        if (skipRepoFieldsValidation != that.skipRepoFieldsValidation) return false;
        if (attachments != null ? !attachments.equals(that.attachments) : that.attachments != null) return false;
        if (ownersUri != null ? !ownersUri.equals(that.ownersUri) : that.ownersUri != null) return false;
        if (additionalProperties != null ? additionalProperties.equals(that.additionalProperties) : that.additionalProperties == null) return false;
        return Arrays.equals(validationGroups, that.validationGroups);
    }

    @Override
    public int hashCode() {
        int result = (allowReferencesOnly ? 1 : 0);
        result = 31 * result + (resetVersion ? 1 : 0);
        result = 31 * result + (suppressValidation ? 1 : 0);
        result = 31 * result + (skipRepoFieldsValidation ? 1 : 0);
        result = 31 * result + (ownersUri != null ? ownersUri.hashCode() : 0);
        result = 31 * result + (attachments != null ? attachments.hashCode() : 0);
        result = 31 * result + (additionalProperties != null ? additionalProperties.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(validationGroups);
        return result;
    }

    @Override
    public String toString() {
        return "ToServerConversionOptions{" +
                "allowReferencesOnly=" + allowReferencesOnly +
                ", resetVersion=" + resetVersion +
                ", suppressValidation=" + suppressValidation +
                ", skipRepoFieldsValidation=" + skipRepoFieldsValidation +
                ", ownersUri='" + ownersUri + '\'' +
                ", attachments=" + attachments +
                ", validationGroups=" + validationGroups +
                ", additionalProperties=" + additionalProperties +
                '}';
    }
}
