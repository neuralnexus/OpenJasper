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

package com.jaspersoft.jasperserver.remote.resources.converters;

import java.io.InputStream;
import java.util.Map;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id: ToServerConversionOptions.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ToServerConversionOptions {
    private boolean allowReferencesOnly, resetVersion;
    private String ownersUri;
    private Map<String, InputStream> attachments;

    public static ToServerConversionOptions getDefault(){
        return new ToServerConversionOptions();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToServerConversionOptions that = (ToServerConversionOptions) o;

        if (allowReferencesOnly != that.allowReferencesOnly) return false;
        if (resetVersion != that.resetVersion) return false;
        if (ownersUri != null ? !ownersUri.equals(that.ownersUri) : that.ownersUri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (allowReferencesOnly ? 1 : 0);
        result = 31 * result + (resetVersion ? 1 : 0);
        result = 31 * result + (ownersUri != null ? ownersUri.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToServerConversionOptions{" +
                "allowReferencesOnly=" + allowReferencesOnly +
                ", resetVersion=" + resetVersion +
                ", ownersUri='" + ownersUri + '\'' +
                '}';
    }
}
