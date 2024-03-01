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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ErrorsAdapter extends AbstractErrors {
    private final List<Exception> errors;
    private final String objectName;

    public ErrorsAdapter(List<Exception> errors, String objectName) {
        this.errors = errors;
        this.objectName = objectName;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
        errors.add(new ErrorDescriptorException(new ErrorDescriptor()
                .setErrorCode(errorCode)
                .setMessage(defaultMessage)
                .setParameters(toStrings(errorArgs))
        ));
    }

    @Override
    public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {
        reject(errorCode, errorArgs, defaultMessage);
    }

    private String[] toStrings(Object[] objects){
        if (objects == null) return null;
        final String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++) {
            strings[i] = objects[i] != null ? objects[i].toString() : null;
        }
        return strings;
    }

    @Override
    public void addAllErrors(Errors errors) {
        throw new NotImplementedException();
    }

    @Override
    public List<ObjectError> getGlobalErrors() {
        throw new NotImplementedException();
    }

    @Override
    public List<FieldError> getFieldErrors() {
        throw new NotImplementedException();
    }

    @Override
    public Object getFieldValue(String field) {
        throw new NotImplementedException();
    }
}
