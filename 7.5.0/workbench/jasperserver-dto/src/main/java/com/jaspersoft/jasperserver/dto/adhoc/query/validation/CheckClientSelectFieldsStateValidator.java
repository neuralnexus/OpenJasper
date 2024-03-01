package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.common.ValidationErrorDescriptorBuilder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.QUERY_SELECT_ILLEGAL_FIELDS_STATE;

/**
 * <p></p>
 *
 * @author Yehor Bobyk
 * @version $Id$
 */

public class CheckClientSelectFieldsStateValidator implements ConstraintValidator<CheckClientSelect,
        ClientSelect>, ValidationErrorDescriptorBuilder {

    @Override
    public void initialize(CheckClientSelect constraintAnnotation) {
        //empty
    }

    @Override
    public boolean isValid(ClientSelect value, ConstraintValidatorContext context) {
        List<ClientQueryField> fields = Optional.ofNullable(value.getFields()).orElse(new ArrayList<>());
        List<ClientQueryField> distinctFields = Optional.ofNullable(value.getDistinctFields()).orElse(new ArrayList<>());

        return !(fields.size() > 0 && distinctFields.size() > 0);

    }

    @Override
    public ErrorDescriptor build(ConstraintViolation violation) {
        return QUERY_SELECT_ILLEGAL_FIELDS_STATE.createDescriptor();
    }
}
