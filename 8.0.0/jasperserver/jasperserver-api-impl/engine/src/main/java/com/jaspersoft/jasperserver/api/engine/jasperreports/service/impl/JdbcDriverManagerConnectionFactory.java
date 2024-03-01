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

package com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl;

import com.amazonaws.auth.AWSSessionCredentials;
import com.jaspersoft.jasperserver.api.common.util.TibcoDriverManager;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsCredentialUtil;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.PooledObjectCache;
import java.sql.DriverManager;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;

public class JdbcDriverManagerConnectionFactory extends DriverManagerConnectionFactory {

    private static final Log log = LogFactory.getLog(JdbcDriverManagerConnectionFactory.class);

    private static final String AWS_CREDENTIALS_PROVIDER_CLASS_KEY = "AwsCredentialsProviderClass";
    private static final String AWS_CREDENTIALS_PROVIDER_CLASS_VALUE = "com.jaspersoft.jasperserver.api.engine.jasperreports.service.impl.AthenaCustomSessionCredentialsProvider";
    private static final String AWS_CREDENTIALS_PROVIDER_CLASS_ARGUMENTS = "AwsCredentialsProviderArguments";
    private static final String AWS_ARN = "arn";
    private static final String AWS_ACCESS_KEY = "AccessKey";
    private static final String AWS_SECRET_KEY = "SecretKey";

    public JdbcDriverManagerConnectionFactory(java.lang.String connectUri, java.lang.String uname, java.lang.String passwd) {
        super(connectUri, uname, passwd);
    }

    public Connection createConnection() throws java.sql.SQLException {
        TibcoDriverManager tibcoDriverManager = TibcoDriverManagerImpl.getInstance();
        Connection connection = null;
        if(_connectUri.contains(PooledObjectCache.JDBC_AWSATHENA)){
            String newConnectionUri = modifiedUrlForAthenaJDBCDriver(_connectUri);
            connection = DriverManager.getConnection(newConnectionUri);
        } else {
            connection = super.createConnection();
        }

        tibcoDriverManager.unlockConnection(connection);
        return connection;

    }

    private String modifiedUrlForAthenaJDBCDriver(String uri){
        String connectionUrl = uri;
        String connParams[] = connectionUrl.split(";");
        String roleARN = null, accessKey = null, secretKey = null;
        for (String param : connParams) {
            if (param.startsWith(AWS_ARN)) {
                connectionUrl = connectionUrl.replace(param + ";", "");
                roleARN = param.split("=").length > 1 ? param.split("=")[1] : "";
            }
            if (param.startsWith(AWS_ACCESS_KEY)) {
                connectionUrl = connectionUrl.replace(param + ";", "");
                accessKey = param.split("=")[1];
            }
            if (param.startsWith(AWS_SECRET_KEY)) {
                connectionUrl = connectionUrl.replace(param + ";", "");
                secretKey = param.split("=")[1];
            }
        }

        if (roleARN != null && !roleARN.isEmpty()) {
            AwsCredentialUtil awsCredentialUtil = new AwsCredentialUtil();
            AWSSessionCredentials awsCredentials = (AWSSessionCredentials) awsCredentialUtil
                .getAWSCredentials(accessKey, secretKey, roleARN);
            String providerArgs = null;
            if (awsCredentials != null) {
                providerArgs =
                    awsCredentials.getAWSAccessKeyId() + "," + awsCredentials.getAWSSecretKey() + ","
                        + awsCredentials.getSessionToken();
            }
            connectionUrl = connectionUrl + AWS_CREDENTIALS_PROVIDER_CLASS_KEY + "="
                + AWS_CREDENTIALS_PROVIDER_CLASS_VALUE + ";"
                + AWS_CREDENTIALS_PROVIDER_CLASS_ARGUMENTS + "=" + providerArgs + ";";
        } else {
            connectionUrl = connectionUrl + "User=" + accessKey + ";Password=" + secretKey + ";";
        }

        return connectionUrl;
    }

}
