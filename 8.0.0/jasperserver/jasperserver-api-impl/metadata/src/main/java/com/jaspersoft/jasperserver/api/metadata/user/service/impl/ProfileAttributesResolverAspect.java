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

package com.jaspersoft.jasperserver.api.metadata.user.service.impl;

import com.jaspersoft.jasperserver.api.JSShowOnlyErrorMessage;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.AwsReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.ReportDataSource;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.domain.client.AwsReportDataSourceImpl;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceService;
import com.jaspersoft.jasperserver.api.metadata.jasperreports.service.ReportDataSourceServiceFactory;
import com.jaspersoft.jasperserver.api.metadata.user.service.ProfileAttributesResolver;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * Interceptor over {@link ReportDataSourceServiceFactory} interface for
 * resolving profile attributes in parametrized data source
 *
 * @author Volodya Sabadosh
 * @version $Id$
 */
public class ProfileAttributesResolverAspect {
    private ProfileAttributesResolver profileAttributesResolver;

    public void setProfileAttributesResolver(ProfileAttributesResolver profileAttributesResolver) {
        this.profileAttributesResolver = profileAttributesResolver;
    }

    public ReportDataSourceService resolveDataSourceAttributes(ProceedingJoinPoint call) throws Throwable {
        // clone args
        Object[] args = new Object[call.getArgs().length];
        System.arraycopy(call.getArgs(), 0, args, 0, call.getArgs().length);
        ReportDataSource reportDataSource = (ReportDataSource) call.getArgs()[0];
        ReportDataSource mergedReportDataSource = profileAttributesResolver.mergeResource(reportDataSource);
        // replace first element with resolved data source
        args[0]=mergedReportDataSource;
        // proceed with the call.
        return (ReportDataSourceService) call.proceed(args);
    }

    public Object resolveAwsCredentialAttributes(ProceedingJoinPoint call) throws Throwable {
        AwsReportDataSource awsReportDataSource = new AwsReportDataSourceImpl();
        awsReportDataSource.setURIString("");
        awsReportDataSource.setAWSAccessKey((String) call.getArgs()[0]);
        awsReportDataSource.setAWSSecretKey((String) call.getArgs()[1]);
        awsReportDataSource.setRoleARN((String) call.getArgs()[2]);

        try {
            AwsReportDataSource resolvedAwsDataSource = profileAttributesResolver.mergeResource(awsReportDataSource);
            Object[] resolvedArguments = new String[]{
                    resolvedAwsDataSource.getAWSAccessKey(),
                    resolvedAwsDataSource.getAWSSecretKey(),
                    resolvedAwsDataSource.getRoleARN()
            };
            return call.proceed(resolvedArguments);
        } catch (Exception ex) {
            if(ex.getClass().isAssignableFrom(JSShowOnlyErrorMessage.class)){
                throw ex; // rethrow show only error message exception as it is
            } else {
                throw new JSShowOnlyErrorMessage(ex.getMessage());
            }
        }
    }
}
