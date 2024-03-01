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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import java.util.Set;

/**
 * Helper to convert bytes to strings and back.
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class OutputFormatConversionHelper {
    public static Set<Byte> toBytes(Set<String> strings) throws Exception {
        return toBytes(strings, null);
    }

    public static Set<Byte> toBytes(Set<String> strings, String exportType) throws Exception {
        ReportJobOutputFormatsWrapper formatsWrapper = new ReportJobOutputFormatsWrapper();
        formatsWrapper.setFormats(strings);
        formatsWrapper.setExportType(exportType);
        return new OutputFormatXmlAdapter().unmarshal(formatsWrapper);
    }

    public static Set<String> toStrings(Set<Byte> bytes) throws Exception {
        return toOutputFormats(bytes).getFormats();
    }

    public static ReportJobOutputFormatsWrapper toOutputFormats(Set<Byte> bytes) throws Exception {
        ReportJobOutputFormatsWrapper wrapper = new OutputFormatXmlAdapter().marshal(bytes);
		return wrapper;
    }
}
