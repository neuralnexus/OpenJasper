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
package com.jaspersoft.jasperserver.remote.services;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id: ExportExecutionOptions.java 26599 2012-12-10 13:04:23Z ykovalchyk $
 */
@XmlRootElement(name = "export")
public class ExportExecutionOptions {

    public static final String PARAM_NAME_PAGES = "pages";
    public static final String PARAM_NAME_ATTACHMENTS_PREFIX = "attachmentsPrefix";

    private String outputFormat;
    private String attachmentsPrefix;
    private ReportOutputPages pages;
    private String baseUrl;
    private boolean allowInlineScripts = true;

    public boolean isAllowInlineScripts() {
        return allowInlineScripts;
    }

    public ExportExecutionOptions setAllowInlineScripts(boolean allowInlineScripts) {
        this.allowInlineScripts = allowInlineScripts;
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public ExportExecutionOptions setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ExportExecutionOptions setPages(ReportOutputPages pages) {
        this.pages = pages;
        return this;
    }

    public String getAttachmentsPrefix() {
        return attachmentsPrefix;
    }

    public ExportExecutionOptions setAttachmentsPrefix(String attachmentsPrefix) {
        this.attachmentsPrefix = attachmentsPrefix;
        return this;
    }

    @XmlJavaTypeAdapter(ReportOutputPagesToStringXmlAdapter.class)
    public ReportOutputPages getPages(){
        return pages;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public ExportExecutionOptions setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExportExecutionOptions that = (ExportExecutionOptions) o;

        if (allowInlineScripts != that.allowInlineScripts) return false;
        if (attachmentsPrefix != null ? !attachmentsPrefix.equals(that.attachmentsPrefix) : that.attachmentsPrefix != null)
            return false;
        if (baseUrl != null ? !baseUrl.equals(that.baseUrl) : that.baseUrl != null) return false;
        if (outputFormat != null ? !outputFormat.equals(that.outputFormat) : that.outputFormat != null) return false;
        if (pages != null ? !pages.equals(that.pages) : that.pages != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = outputFormat != null ? outputFormat.hashCode() : 0;
        result = 31 * result + (attachmentsPrefix != null ? attachmentsPrefix.hashCode() : 0);
        result = 31 * result + (pages != null ? pages.hashCode() : 0);
        result = 31 * result + (baseUrl != null ? baseUrl.hashCode() : 0);
        result = 31 * result + (allowInlineScripts ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExportExecutionOptions{" +
                "outputFormat='" + outputFormat + '\'' +
                ", attachmentsPrefix='" + attachmentsPrefix + '\'' +
                ", pages=" + pages +
                ", baseUrl='" + baseUrl + '\'' +
                ", allowInlineScripts=" + allowInlineScripts +
                '}';
    }
}
