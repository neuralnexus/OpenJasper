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
package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jaspersoft.jasperserver.api.JSException;
import com.jaspersoft.jasperserver.api.JSExceptionWrapper;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.BeanReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;

/**
 * @author swood
 *
 */
public class BeanReportDataSourceServiceFactory implements ReportDataSourceServiceFactory, ApplicationContextAware {

	ApplicationContext ctx;
	
	/**
	 * 
	 */
	public BeanReportDataSourceServiceFactory() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		ctx = arg0;
	}

	/* (non-Javadoc)
	 * @see com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory#createService(com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource)
	 */
	public ReportDataSourceService createService(ReportDataSource reportDataSource) {
		if (!(reportDataSource instanceof BeanReportDataSource)) {
			throw new JSException("jsexception.invalid.bean.datasource", new Object[] {reportDataSource.getClass()});
		}
		BeanReportDataSource beanDataSource = (BeanReportDataSource) reportDataSource;
	
		Object bean = ctx.getBean(beanDataSource.getBeanName());
		
		if (bean == null) {
			throw new JSException("jsexception.bean.no.name", new Object[] {beanDataSource.getBeanName()});
		}
		
		if (beanDataSource.getBeanMethod() == null) {
			// The bean had better be a ReportDataSourceService
			if (!(bean instanceof ReportDataSourceService)) {
				throw new JSException("jsexception.bean.not.a.ReportDataSourceService", new Object[] {beanDataSource.getBeanName()});
			} else {
				return (ReportDataSourceService) bean;
			}
		} else {
			// The method on this bean returns a ReportDataSourceService
			Method serviceMethod;
			try {
				serviceMethod = bean.getClass().getMethod(beanDataSource.getBeanMethod(), null);
				return (ReportDataSourceService) serviceMethod.invoke(bean, null);
			} catch (SecurityException e) {
				throw new JSExceptionWrapper(e);
			} catch (NoSuchMethodException e) {
				throw new JSException("jsexception.bean.has.no.method", new Object[] {beanDataSource.getBeanName(), beanDataSource.getBeanMethod()});
			} catch (IllegalArgumentException e) {
				throw new JSExceptionWrapper(e);
			} catch (IllegalAccessException e) {
				throw new JSExceptionWrapper(e);
			} catch (InvocationTargetException e) {
				throw new JSExceptionWrapper(e);
			}
		}
	}

}
