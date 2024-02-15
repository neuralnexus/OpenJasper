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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.jaspersoft.jasperserver.remote.connection;

import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.dto.connection.BeanConnection;
import com.jaspersoft.jasperserver.remote.exception.IllegalParameterValueException;
import com.jaspersoft.jasperserver.remote.exception.MandatoryParameterNotFoundException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: BeanConnectionStrategy.java 47331 2014-07-18 09:13:06Z kklein $
 */
@Service
public class BeanConnectionStrategy implements ConnectionManagementStrategy<BeanConnection> {
    @Autowired
    private ApplicationContext ctx;

    @Override
    public BeanConnection createConnection(BeanConnection connectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        // functionality of this method is copied from com.jaspersoft.jasperserver.war.action.DataSourceAction.testBeanDataSource()
        final Object bean;
        try {
            bean = ctx.getBean(connectionDescription.getBeanName());
        } catch (NoSuchBeanDefinitionException e) {
            throw new ConnectionFailedException(connectionDescription.getBeanName(), "beanName", null, e);
        }
        if (bean == null) {
            throw new ConnectionFailedException(connectionDescription.getBeanName(), "beanName", null, null);
        } else {
            final String beanMethod = connectionDescription.getBeanMethod();
            if (beanMethod == null) {
                // The bean had better be a ReportDataSourceService
                if (!(bean instanceof ReportDataSourceService)) {
                    throw new MandatoryParameterNotFoundException("beanMethod");
                }
            } else {
                // The method on this bean returns a ReportDataSourceService
                Method serviceMethod;
                try {
                    serviceMethod = bean.getClass().getMethod(beanMethod, null);
                    Object obj = serviceMethod.invoke(bean, null);
                    if (obj == null) {
                        throw new ConnectionFailedException(connectionDescription);
                    }
                } catch (NoSuchMethodException e) {
                    throw new ConnectionFailedException(connectionDescription.getBeanMethod(), "beanMethod",
                            "No such method: " + connectionDescription.getBeanMethod(), e);
                } catch (SecurityException e) {
                    throw new ConnectionFailedException(connectionDescription.getBeanMethod(), "beanMethod", null, e);
                } catch (Exception e) {
                    throw new ConnectionFailedException(connectionDescription, e);
                }
            }
        }
        return connectionDescription;
    }

    @Override
    public void deleteConnection(BeanConnection connectionDescription, Map<String, Object> data) {
    }

    @Override
    public BeanConnection modifyConnection(BeanConnection newConnectionDescription, BeanConnection oldConnectionDescription, Map<String, Object> data) throws IllegalParameterValueException {
        // here is nothing to update, just check if it can be connected.
        return createConnection(newConnectionDescription, data);
    }

    @Override
    public BeanConnection secureGetConnection(BeanConnection connectionDescription, Map<String, Object> data) {
        // no hidden attributes
        return connectionDescription;
    }
}
