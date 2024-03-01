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

package com.jaspersoft.jasperserver.api.common.error.handling;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.dto.common.ErrorDescriptor;
import net.sf.jasperreports.engine.JRException;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;

/**
 * Unit tests for {@link SecureExceptionHandlerImpl} class.
 *
 * @author dlitvak
 * @version $Id$
 */
@RunWith(MockitoJUnitRunner.class)
public class SecureExceptionHandlerImplTest{
    @Mock
    private Logger loggerMock;

    @Mock
    private MessageSource messageSourceMock;

    @Mock
    private ExceptionOutputManager outputManagerMock;

    private static final String SERVER_ERROR_MESSAGE = "RevenueDetailReport.pdf " +
            "は、リポジトリには保存されませんでした。エラーにより処理が中断されました。";
    private SecureExceptionHandlerImpl exceptionHandler;

    @Before
    public void before() throws Exception {
        exceptionHandler = new SecureExceptionHandlerImpl();
        exceptionHandler.setExceptionOutputManager(outputManagerMock);
        exceptionHandler.setMessageSource(messageSourceMock);
    }

    @Test
    public void shouldReturnProperErrorDescriptorForJasperadminUserWhenErrorOccurred() {

        // given
        final Exception exception =
                new JSException("jsexception.report.resource.already.exists.no.overwrite", new JRException("1"));

        doReturn(true).when(outputManagerMock).isExceptionMessageAllowed();
        doReturn(true).when(outputManagerMock).isStackTraceAllowed();
        doReturn(false).when(outputManagerMock).isUIDOutputOn();

        // when
        ErrorDescriptor descriptor = exceptionHandler.handleException(exception,
                new ErrorDescriptor().setMessage(SERVER_ERROR_MESSAGE), Locale.JAPAN);

        // then
        assertEquals(descriptor.getMessage(), SERVER_ERROR_MESSAGE);
        String[] params = descriptor.getParameters();
        assertSame(params.length, 1);
    }

    @Test
    public void shouldReturnProperErrorDescriptorForJoeuserWhenErrorOccurred() {
        // given
        final Exception exception =
                new JSException("jsexception.report.resource.already.exists.no.overwrite", new JRException("1"));

        doReturn(true).when(outputManagerMock).isExceptionMessageAllowed();
        doReturn(false).when(outputManagerMock).isStackTraceAllowed();
        doReturn(true).when(outputManagerMock).isUIDOutputOn();

        doReturn("Error UID").when(messageSourceMock).getMessage(
                ArgumentMatchers.eq("error.uid.message"),
                ArgumentMatchers.nullable(Object[].class),
                ArgumentMatchers.nullable(Locale.class)
        );

        // when
        ErrorDescriptor retrievedDescriptor = exceptionHandler.handleException(exception,
                new ErrorDescriptor().setMessage(SERVER_ERROR_MESSAGE), Locale.JAPAN);

        // then
        assertNotNull(retrievedDescriptor.getErrorUid());
        assertEquals(retrievedDescriptor.getMessage(), String.format(
                "%s (Error UID: %s)", SERVER_ERROR_MESSAGE, retrievedDescriptor.getErrorUid()
        ));
    }

    @After
    public void after() {
        Mockito.reset(loggerMock, messageSourceMock, outputManagerMock);
    }
}
