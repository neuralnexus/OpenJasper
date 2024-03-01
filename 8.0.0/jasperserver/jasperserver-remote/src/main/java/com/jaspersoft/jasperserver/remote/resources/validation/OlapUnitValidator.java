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

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.ErrorDescriptorException;
import com.jaspersoft.jasperserver.api.common.domain.ExecutionContext;
import com.jaspersoft.jasperserver.api.common.domain.ValidationDetail;
import com.jaspersoft.jasperserver.api.common.domain.ValidationResult;
import com.jaspersoft.jasperserver.api.metadata.olap.domain.OlapUnit;
import com.jaspersoft.jasperserver.api.metadata.olap.service.OlapConnectionService;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.jaspersoft.jasperserver.remote.resources.validation.ValidationHelper.empty;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
@Component
public class OlapUnitValidator extends GenericResourceValidator<OlapUnit> {
    @Resource
    private OlapConnectionService olapConnectionService;

    @Override
    protected void internalValidate(ExecutionContext ctx, OlapUnit resource, List<Exception> errors, Map<String, String[]> additionalParameters) {
        if (empty(resource.getMdxQuery())){
            errors.add(new MandatoryParameterNotFoundException("mdxQuery"));
        }

        if (empty(resource.getOlapClientConnection()) || empty(resource.getOlapClientConnection().getTargetURI())){
            errors.add(new MandatoryParameterNotFoundException("olapConnection"));
        } else {
            ValidationResult result = olapConnectionService.validate(null, resource);
            if (ValidationResult.STATE_ERROR.equals(result.getValidationState())){
                for (ValidationDetail detail : (List<ValidationDetail>)result.getResults()){
                    errors.add(new ErrorDescriptorException(
                            new ErrorDescriptor().setErrorCode(IllegalParameterValueException.ERROR_CODE)
                            .setMessage(detail.getMessage())
                            .setParameters(
                                    detail.getMessage(),
                                    (detail.getId() != null? detail.getId().toString() : detail.getSource()),
                                    detail.getResult())
                    ));
                }
            }
        }
    }
}
