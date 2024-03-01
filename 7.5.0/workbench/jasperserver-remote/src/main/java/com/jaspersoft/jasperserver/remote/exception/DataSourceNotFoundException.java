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

package com.jaspersoft.jasperserver.remote.exception;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceable;

/**
 * <p>
 * <p>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class DataSourceNotFoundException extends ErrorDescriptorException {
    private static final String ERROR_CODE  = "domain.data.source.not.found";

    public DataSourceNotFoundException(ClientReferenceable datasource){
        ErrorDescriptor errorDescriptor = this.getErrorDescriptor();
        errorDescriptor.setErrorCode(ERROR_CODE)
                .setMessage("Datasource not found.")
                .addProperties(new ClientProperty(new ClientProperty("attribute", "dataSource")));
        if (datasource instanceof ClientReference) {
            errorDescriptor.addProperties(new ClientProperty("referenceUri", datasource.getUri()));
        } else {
            errorDescriptor.addProperties(new ClientProperty("datasource", datasource.toString()));
        }
    }
}
