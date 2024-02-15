package com.jaspersoft.jasperserver.core.util;

import com.jaspersoft.jasperserver.core.util.validators.InputValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author  schubar
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:context.xml"})
public class CustomPatternEmailValidatorTest {

    @javax.annotation.Resource(name="customEmailValidator")
    private InputValidator<String> validator;

    @Test
    public void shouldValidateSimpleEmail() {
        assertTrue(validator.isValid("niceandsimple@example.com"));
    }

    @Test
    public void shouldValidateCommonEmail() {
        assertTrue(validator.isValid("very.common@example.com"));
    }
    @Test
    public void shouldValidateLongEmail() {
        assertTrue(validator.isValid("a.little.lengthy.but.fine@dept.example.com"));
    }

    @Test
    public void shouldInvalidateEmailWithPlus() {
        assertFalse(validator.isValid("disposable.style.email.with+symbol@example.com"));
    }

    @Test
    public void shouldInvalidateSimpleEmailWithApostrophe() {
        assertFalse(validator.isValid("nice'and'simple@example.com"));
    }
}
