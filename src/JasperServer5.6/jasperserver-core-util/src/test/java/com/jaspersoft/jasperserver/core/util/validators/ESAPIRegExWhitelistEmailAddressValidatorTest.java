package com.jaspersoft.jasperserver.core.util;

import com.jaspersoft.jasperserver.core.util.validators.InputValidator;
import org.junit.Ignore;
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
public class ESAPIRegExWhitelistEmailAddressValidatorTest {

    @javax.annotation.Resource(name="ESAPIRegExWhitelistPatternEmailAddressesValidator")
    private InputValidator<String> validator;

    @Test
    public void shouldValidateSimpleEmail() {
        assertTrue(validator.isValid("niceandsimple@example.com"));
    }

    @Test
    public void shouldValidateSimpleEmailWithApostrophe() {
        assertTrue(validator.isValid("nice'and'simple@example.com"));
    }

    @Test
    public void shouldValidateCommonEmail() {
        assertTrue(validator.isValid("very.common@example.com"));
    }
    @Test
    public void shouldValidateLongEmail() {
        assertTrue(validator.isValid("a.little.lengthy.but.fine@dept.example.com"));
    }

    /**
     * This email is valid by our ESAPI validation rules (RegExp Whitelist)
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldValidateEmailWithPlus() {
        assertTrue(validator.isValid("disposable.style.email.with+symbol@example.com"));
    }

    /**
     * This email is valid by  RFC spec, but email validator will not pass it
     * @throws Exception
     */
    @Test
    @Ignore
    public void shouldValidateIP6Email() {
        assertTrue(validator.isValid("user@[IPv6:2001:db8:1ff::a0b:dbd0]"));
    }

    /**
     * This email is valid by our ESAPI validation rules (RegExp Whitelist)
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldValidateEmailWithSpace() {
        assertTrue(validator.isValid("\"much.more unusual\"@example.com"));
    }

    /**
     * This email is valid by our ESAPI validation rules (RegExp Whitelist)
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldValidateEmailWithSymbols() {
        assertTrue(validator.isValid("!#$%&'*+-/=?^_`{}|~@example.org"));
    }
    @Test
    public void shouldValidateLocalDomainEmail() {
        assertTrue(validator.isValid("admin@mailserver1"));
    }

    /**
     * This email is valid by our ESAPI validation rules (RegExp Whitelist)
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldValidateCorrectMail() {
        assertTrue(validator.isValid("\"()<>[]:,;@\\\\\\\"!#$%&'*+-/=?^_`{}| ~.a\"@example.org"));
    }

    /**
     * This email is valid by our ESAPI validation rules (RegExp Whitelist)
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldValidateVeryLongEmail() {
        assertTrue(validator.isValid("\"very.(),:;<>[]\\\".VERY.\\\"very@\\\\ \\\"very\\\".unusual\"@strange.example.com"));
    }
    @Test
    public void shouldValidateTopLevelDomainEmail() {
        assertTrue(validator.isValid("postbox@com"));
    }
    @Test
    public void shouldInvalidateEmailWithoutAt() {
        assertFalse(validator.isValid("Abc.example.com"));
    }
    @Test
    public void shouldInvalidateEmailWithToManyAt() {
        assertFalse(validator.isValid("A@b@c@example.com"));
    }
    /**
     *
     * This email is invalid by RFC spec, but email validator will not pass it
     * @throws Exception
     */
    @Ignore
    @Test
    public void shouldInvalidateEmail1() {
        assertFalse(validator.isValid("just\"not\"right@example.com"));
    }
    @Test
    public void shouldInvalidateEmail2() {
        assertFalse(validator.isValid("this is\"not\\allowed@example.com"));
    }
    @Test
    public void shouldInvalidateEmail3() {
        assertFalse(validator.isValid("this\\ still\\\"not\\\\allowed@example.com"));
    }
    @Test
    public void shouldInvalidateEmail4() {
        assertFalse(validator.isValid("a\"b(c)d,e:f;g<h>i[j\\k]l@example.com"));
    }

    @Test
    public void shouldInvalidateEmail5() {
        assertFalse(validator.isValid("random"));
    }


}
