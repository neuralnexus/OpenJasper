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
package com.jaspersoft.jasperserver.remote.executions.builders;

import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import com.jaspersoft.jasperserver.dto.resources.ClientProperty;
import com.jaspersoft.jasperserver.remote.exception.builders.DefaultMessageApplier;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Locale;

import static com.jaspersoft.jasperserver.remote.exception.builders.LocalizedErrorDescriptorBuilder.BUNDLE_PREFIX;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class DefaultMessageApplierTest {
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DefaultMessageApplier defaultMessageApplier;

    private String[] params = new String[]{"someParam"};
    private ClientProperty[] clientProperties = new ClientProperty[]{new ClientProperty("someParamKey", "someParam")};
    private String someErrorCode = "someErrorCode";
    private String bundleErrorCode = BUNDLE_PREFIX.concat(someErrorCode);
    private String defaultMessage = "Default message";
    private String someMessage1 = "Some message 1";
    private String someMessage2 = "Some message 2";
    private ErrorDescriptor descriptorWithPropsAndNullMsg = new ErrorDescriptor()
            .setErrorCode(someErrorCode).addProperties(clientProperties);
    private ErrorDescriptor descriptorWithParamsAndNullMsg = new ErrorDescriptor()
            .setErrorCode(someErrorCode).setParameters(params);
    private ErrorDescriptor descriptorWithPropsAndMsg = descriptorWithPropsAndNullMsg.deepClone().setMessage(defaultMessage);
    private ErrorDescriptor descriptorWithParamsAndMsg = descriptorWithParamsAndNullMsg.deepClone().setMessage(defaultMessage);

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void resetMocks(){
        reset(messageSource);
        when(messageSource.getMessage(eq(someErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenReturn(someMessage1);
        when(messageSource.getMessage(eq(bundleErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenReturn(someMessage2);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageIsNullAndPropertiesNotNullUseBundlePrefixIsFalse_success() {
        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithPropsAndNullMsg.deepClone());

        assertEquals(descriptorWithPropsAndNullMsg.deepClone().setMessage(someMessage1), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 1);
        verifyMessageCall(bundleErrorCode, 0);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageIsNullAndParamsNotNullUseBundlePrefixIsFalse_success() {
        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithParamsAndNullMsg.deepClone());

        assertEquals(descriptorWithParamsAndNullMsg.deepClone().setMessage(someMessage1), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 1);
        verifyMessageCall(bundleErrorCode, 0);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageIsNullAndPropertiesNotNullUseBundlePrefixIsTrue_success() {
        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithPropsAndNullMsg.deepClone(), true);

        assertEquals(descriptorWithPropsAndNullMsg.deepClone().setMessage(someMessage2), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 0);
        verifyMessageCall(bundleErrorCode, 1);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageIsNullAndParamsNotNullUseBundlePrefixIsTrue_success() {
        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithParamsAndNullMsg.deepClone(), true);

        assertEquals(descriptorWithParamsAndNullMsg.deepClone().setMessage(someMessage2), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 0);
        verifyMessageCall(bundleErrorCode, 1);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageNotNullAndPropertiesNotNullUseBundlePrefixIsFalse_success() {
        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithPropsAndMsg.deepClone());

        assertEquals(descriptorWithPropsAndNullMsg.deepClone().setMessage(defaultMessage), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 0);
        verifyMessageCall(bundleErrorCode, 0);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageNotNullAndParamsNotNullUseBundlePrefixIsTrue_success() {
        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithParamsAndMsg.deepClone(), true);

        assertEquals(descriptorWithParamsAndNullMsg.deepClone().setMessage(defaultMessage), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 0);
        verifyMessageCall(bundleErrorCode, 0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void applyDefaultMessageIfNotSet_messageIsNullAndMessageSourceThrowsException_success() {
        reset(messageSource);
        when(messageSource.getMessage(eq(someErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenThrow(NoSuchMessageException.class);
        when(messageSource.getMessage(eq(bundleErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenThrow(NoSuchMessageException.class);

        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithParamsAndNullMsg.deepClone(), true);

        assertEquals(descriptorWithParamsAndNullMsg.deepClone(), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 1);
        verifyMessageCall(bundleErrorCode, 1);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageIsNullAndAndMessageSourceReturnNulls_success() {
        reset(messageSource);
        when(messageSource.getMessage(eq(someErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenReturn(null);
        when(messageSource.getMessage(eq(bundleErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenReturn(null);

        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithParamsAndNullMsg.deepClone(), true);

        assertEquals(descriptorWithParamsAndNullMsg.deepClone(), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 1);
        verifyMessageCall(bundleErrorCode, 1);
    }

    @Test
    public void applyDefaultMessageIfNotSet_messageIsNullAndAndMessageSourceInputErrorCode_success() {
        reset(messageSource);
        when(messageSource.getMessage(eq(someErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenReturn(someErrorCode);
        when(messageSource.getMessage(eq(bundleErrorCode), eq(params), eq(Locale.ENGLISH))).
                thenReturn(bundleErrorCode);

        ErrorDescriptor updatedErrorDescriptor = defaultMessageApplier.applyDefaultMessageIfNotSet(descriptorWithParamsAndNullMsg.deepClone(), true);

        assertEquals(descriptorWithParamsAndNullMsg.deepClone(), updatedErrorDescriptor);
        verifyMessageCall(someErrorCode, 1);
        verifyMessageCall(bundleErrorCode, 1);
    }

    private void verifyMessageCall(String code, int times) {
        verify(messageSource, times(times)).getMessage(eq(code), eq(params), eq(Locale.ENGLISH));
    }

}

