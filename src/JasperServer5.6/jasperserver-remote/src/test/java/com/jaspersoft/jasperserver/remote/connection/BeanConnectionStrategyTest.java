/*
* Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
* http://www.jaspersoft.com.
*
* Unless you have purchased  a commercial license agreement from Jaspersoft,
* the following license terms  apply:
*
* This program is free software: you can redistribute it and/or  modify
* it under the terms of the GNU Affero General Public License  as
* published by the Free Software Foundation, either version 3 of  the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero  General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public  License
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.dto.connection.BeanConnection;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertSame;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: BeanConnectionStrategyTest.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class BeanConnectionStrategyTest {
    @InjectMocks
    private final BeanConnectionStrategy strategy = new BeanConnectionStrategy();
    @Mock
    private ApplicationContext ctx;

    @BeforeClass
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @BeforeMethod
    public void resetMocks(){
        reset(ctx);

    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_wrongBeanName_null(){
        strategy.createConnection(new BeanConnection().setBeanName("test"), null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_wrongBeanName_NoSuchBeanDefinitionException(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenThrow(NoSuchBeanDefinitionException.class);
        strategy.createConnection(new BeanConnection().setBeanName(beanName), null);
    }

    @Test
    public void createConnection_noBeanMethod_instanceofReportDataSourceService_success(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(mock(ReportDataSourceService.class));
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName);
        final BeanConnection connection = strategy.createConnection(connectionDescription, null);
        assertSame(connection, connectionDescription);
    }

    @Test(expectedExceptions = MandatoryParameterNotFoundException.class)
    public void createConnection_noBeanMethod_notInstanceofReportDataSourceService_exception(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(new Object());
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName);
        strategy.createConnection(connectionDescription, null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_wrongBeanMethod_exception(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(new Object());
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName).setBeanMethod("notExistentMethodName");
        strategy.createConnection(connectionDescription, null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_privateMethod_exception(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(new Object() {
            private Object testMethod() {
                return new Object();
            }
        });
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName).setBeanMethod("testMethod");
        strategy.createConnection(connectionDescription, null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_methodReturnsNull_exception(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(new Object(){
            public Object testMethod(){
                return null;
            }
        });
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName).setBeanMethod("testMethod");
        strategy.createConnection(connectionDescription, null);
    }

    @Test(expectedExceptions = ConnectionFailedException.class)
    public void createConnection_methodThrowsException(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(new Object(){
            public Object testMethod(){
                throw new RuntimeException();
            }
        });
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName).setBeanMethod("testMethod");
        strategy.createConnection(connectionDescription, null);
    }

    @Test
    public void createConnection_methodReturnsObject_success(){
        final String beanName = "test";
        when(ctx.getBean(beanName)).thenReturn(new Object(){
            public Object testMethod(){
                return new Object();
            }
        });
        final BeanConnection connectionDescription = new BeanConnection().setBeanName(beanName).setBeanMethod("testMethod");
        final BeanConnection connection = strategy.createConnection(connectionDescription, null);
        assertSame(connection, connectionDescription);
    }


}
