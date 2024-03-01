package com.jaspersoft.jasperserver.dto.adhoc.query.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import java.lang.annotation.*;

import static com.jaspersoft.jasperserver.dto.executions.QueryExecutionsErrorCode.Codes.QUERY_SELECT_ILLEGAL_FIELDS_STATE;

/**
 * <p></p>
 *
 * @author Yehor Bobyk
 * @version $Id$
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = {CheckClientSelectFieldsStateValidator.class})
@ReportAsSingleViolation
public @interface CheckClientSelect {

    String message() default QUERY_SELECT_ILLEGAL_FIELDS_STATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
