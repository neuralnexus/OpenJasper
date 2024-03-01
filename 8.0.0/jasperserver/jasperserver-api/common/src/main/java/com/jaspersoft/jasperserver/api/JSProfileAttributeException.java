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

package com.jaspersoft.jasperserver.api;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;

/**
 * <p></p>
 *                                                             
 * @author Vlad Zavadskii
 * @version $Id$
 */
public class JSProfileAttributeException extends JSException implements ErrorDescriptorHolder<JSProfileAttributeException> {
    private ErrorDescriptor errorDescriptor;

    //We keep localizedMessage, because it is used for legacy popup error dialogs.
    // In ErrorDescriptor we use default message in English locale
    public JSProfileAttributeException(String localizedMessage, ErrorDescriptor errorDescriptor) {
        super(localizedMessage);

        this.errorDescriptor = errorDescriptor;
    }

    public JSProfileAttributeException(ErrorDescriptor errorDescriptor) {
        super(errorDescriptor.getMessage() != null ? errorDescriptor.getMessage() :
                "Profile attribute substitutions error: " + errorDescriptor.toString());

        this.errorDescriptor = errorDescriptor;
    }

    @Override
    public ErrorDescriptor getErrorDescriptor() {
        return errorDescriptor;
    }

    @Override
    public JSProfileAttributeException setErrorDescriptor(ErrorDescriptor errorDescriptor) {
        this.errorDescriptor = errorDescriptor;
        return this;
    }

}
