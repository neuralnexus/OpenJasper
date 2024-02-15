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

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.Diagnostic;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * AWS properties
 *
 * @author vsabadosh
 */
public class AwsProperties implements Diagnostic, InitializingBean {

    private Boolean securityGroupChangesEnabled;
    private String securityGroupName;
    private String securityGroupDescription;
    private String securityGroupIngressPublicIp;
    private Boolean suppressEc2CredentialsWarnings;
    
    public static final String DB_SECURITY_GROUP_CHANGES_ENABLED = "aws.db.security.group.changes.enabled";
    public static final String DB_SECURITY_GROUP_NAME = "aws.db.security.group.name";
    public static final String DB_SECURITY_GROUP_DESCRIPTION = "aws.db.security.group.description";
    public static final String DB_SECURITY_GROUP_INGRESS_PUBLIC_IP = "aws.db.security.group.ingressPublicIp";
    public static final String DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS = "aws.db.security.group.suppressEc2CredentialsWarnings";

    private MessageSource messageSource;
    private AwsEc2MetadataClient awsEc2MetadataClient;

    public Boolean isSecurityGroupChangesEnabled() {
        return securityGroupChangesEnabled;
    }

    public void setSecurityGroupChangesEnabled(Boolean securityGroupChangesEnabled) {
        this.securityGroupChangesEnabled = securityGroupChangesEnabled;
    }

    public String getSecurityGroupName() {
        return securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getSecurityGroupDescription() {
        return securityGroupDescription;
    }

    public void setSecurityGroupDescription(String securityGroupDescription) {
        this.securityGroupDescription = securityGroupDescription;
    }

    public String getSecurityGroupIngressPublicIp() {
        return securityGroupIngressPublicIp;
    }

    public void setSecurityGroupIngressPublicIp(String securityGroupIngressPublicIp) {
        this.securityGroupIngressPublicIp = securityGroupIngressPublicIp;
    }

    public Boolean isSuppressEc2CredentialsWarnings() {
        return suppressEc2CredentialsWarnings;
    }

    public void setSuppressEc2CredentialsWarnings(Boolean suppressEc2CredentialsWarnings) {
        this.suppressEc2CredentialsWarnings = suppressEc2CredentialsWarnings;
    }

    public void setAwsEc2MetadataClient(AwsEc2MetadataClient awsEc2MetadataClient) {
        this.awsEc2MetadataClient = awsEc2MetadataClient;
    }

    @Override
    public Map<DiagnosticAttribute, DiagnosticCallback> getDiagnosticData() {
        return new DiagnosticAttributeBuilder()
            .addDiagnosticAttribute(DiagnosticAttributeBuilder.AWS_SETTINGS, new DiagnosticCallback<Map<String, Object>>() {
                public Map<String, Object> getDiagnosticAttributeValue() {
                    Map<String, Object> awsSettings = new HashMap<String, Object>();
                    awsSettings.put(DB_SECURITY_GROUP_CHANGES_ENABLED,
                            generateValueWithDescription(securityGroupChangesEnabled, DB_SECURITY_GROUP_CHANGES_ENABLED));
                    awsSettings.put(DB_SECURITY_GROUP_NAME,
                            generateValueWithDescription(securityGroupName, DB_SECURITY_GROUP_NAME));
                    awsSettings.put(DB_SECURITY_GROUP_DESCRIPTION,
                            generateValueWithDescription(securityGroupDescription, DB_SECURITY_GROUP_DESCRIPTION));
                    awsSettings.put(DB_SECURITY_GROUP_INGRESS_PUBLIC_IP,
                            generateValueWithDescription(securityGroupIngressPublicIp, DB_SECURITY_GROUP_INGRESS_PUBLIC_IP));
                    awsSettings.put(DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS,
                            generateValueWithDescription(suppressEc2CredentialsWarnings, DB_SECURITY_GROUP_CHANGES_SUPPRESS_EC2_CREDENTIALS_WARNINGS));

                    return awsSettings;
                }
            }).build();
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String generateValueWithDescription(Object awsValue, String messagePath) {
        Locale locale = LocaleContextHolder.getLocale();
        String description = messageSource.getMessage(messagePath, null, "", locale);
        if (description == null || description.isEmpty()) {
            description = "";
        } else {
            description = " (" + description + ")";
        }
        return awsValue + description;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String instanceId = awsEc2MetadataClient.getEc2InstanceMetadataItem(AwsEc2MetadataClient.INSTANCE_ID_RESOURCE);
        if (isNotEmpty(instanceId)) {
            this.securityGroupName = getSecurityGroupName() + "_" + instanceId;
        } else {
            this.securityGroupName = getSecurityGroupName();
        }
    }
}
