/*
 * Copyright © 2005 - 2018 TIBCO Software Inc.
 * http://www.jaspersoft.com.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.core.util.validators;

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
