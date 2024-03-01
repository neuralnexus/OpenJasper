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
package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.util.StaticExecutionContextProvider;
import com.jaspersoft.jasperserver.api.metadata.common.domain.DataType;
import com.jaspersoft.jasperserver.core.util.validators.ValidationUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.PatternSyntaxException;

/**
 * @author aztec
 * @version $Id$
 */
public class JasperServerUtil {

    public static boolean isDateType(byte type) {
        return type == DataType.TYPE_DATE || type == DataType.TYPE_DATE_TIME || type == DataType.TYPE_TIME;
    }

    /**
     * We should leave this helper method, because it is used in our third-party jpivot: {@link com.tonbeller.jpivot.table.RepoFolderList}
     */
    public static boolean regExValidateLabel(String inp) throws PatternSyntaxException {
        return ValidationUtil.regExValidateLabel(inp);
    }

    /**
     * We should leave this helper method, because it is used in our third-party jpivot: {@link com.tonbeller.jpivot.table.RepoFolderList}
     */
    public static ExecutionContext getExecutionContext(HttpServletRequest request) {
        return StaticExecutionContextProvider.getExecutionContext();
    }

}
