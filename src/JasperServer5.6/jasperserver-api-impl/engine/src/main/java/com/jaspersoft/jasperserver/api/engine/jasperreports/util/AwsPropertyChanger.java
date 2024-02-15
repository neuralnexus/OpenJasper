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
package com.jaspersoft.jasperserver.api.engine.jasperreports.util;

import com.jaspersoft.jasperserver.api.common.properties.PropertyChanger;
import com.jaspersoft.jasperserver.api.common.properties.PropertyChangerAdapter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 *
 * @author agodovanets@jaspersoft.com
 * @version $Id: AwsPropertyChanger.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class AwsPropertyChanger extends PropertyChangerAdapter {
    final public static String PROPERTY_PREFIX = "aws.";

    protected static final Log log = LogFactory.getLog(AwsPropertyChanger.class);

    private AwsProperties awsProperties;
    
    @Override
    public void setProperty(String key, String val) {
        assert (key.startsWith(PROPERTY_PREFIX));
        log.debug("setting AWS property: " + key + " - " + val);

        if (AwsProperties.DB_SECURITY_GROUP_CHANGES_ENABLED.equals(key)) {
            awsProperties.setSecurityGroupChangesEnabled(Boolean.valueOf(val));
        } else if (AwsProperties.DB_SECURITY_GROUP_NAME.equals(key)) {
            awsProperties.setSecurityGroupName(val);
        } else if (AwsProperties.DB_SECURITY_GROUP_DESCRIPTION.equals(key)) {
            awsProperties.setSecurityGroupDescription(val);
        } else if (AwsProperties.DB_SECURITY_GROUP_INGRESS_PUBLIC_IP.equals(key)) {
            awsProperties.setSecurityGroupIngressPublicIp(val);
        } else if (AwsProperties.DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS.equals(key)) {
            awsProperties.setSuppressEc2CredentialsWarnings(Boolean.valueOf(val));
        } else {
            throw new RuntimeException("Unknown AWS config property: "+key);
        }
    }

    @Override
    public String getProperty(String key) {
        assert (key.startsWith(PROPERTY_PREFIX));
        log.debug("getting AWS property: " + key);
        if (AwsProperties.DB_SECURITY_GROUP_CHANGES_ENABLED.equals(key)) {
            return Boolean.toString(awsProperties.isSecurityGroupChangesEnabled());
            //return Boolean.toString(awsDataSourceRecovery.isSecurityGroupChangesEnabled());
        } else if (AwsProperties.DB_SECURITY_GROUP_NAME.equals(key)) {
            return awsProperties.getSecurityGroupName();
            //return awsDataSourceRecovery.getSecurityGroupName();
        } else if (AwsProperties.DB_SECURITY_GROUP_DESCRIPTION.equals(key)) {
            return awsProperties.getSecurityGroupDescription();
            //return awsDataSourceRecovery.getSecurityGroupDescription();
        } else if (AwsProperties.DB_SECURITY_GROUP_INGRESS_PUBLIC_IP.equals(key)) {
            return awsProperties.getSecurityGroupIngressPublicIp();
            //return awsDataSourceRecovery.getSecurityGroupIngressPublicIp();
        } else if (AwsProperties.DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS.equals(key)) {
            return Boolean.toString(awsProperties.isSuppressEc2CredentialsWarnings());
            //return Boolean.toString(awsDataSourceRecovery.isSuppressEc2CredentialsWarnings());
        } else {
            throw new RuntimeException("Unknown AWS config property: "+key);
        }
    }

    public void setAwsProperties(AwsProperties awsProperties) {
        this.awsProperties = awsProperties;
    }

}
