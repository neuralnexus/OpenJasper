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

package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsEc2MetadataClient;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.InstanceProductTypeResolver;
import org.apache.commons.httpclient.methods.PostMethod;


/**
 *
 * @author vsabadosh
 */
public class HeartbeatAwsEc2Contributor implements HeartbeatContributor {
    private AwsEc2MetadataClient awsEc2MetadataClient;
    private InstanceProductTypeResolver instanceProductTypeResolver;

    @Override
    public void contributeToHttpCall(PostMethod post) {
        String awsEc2InstanceType;
        if (awsEc2MetadataClient.isEc2Instance()) {
            awsEc2InstanceType = awsEc2MetadataClient.getEc2InstanceMetadataItem(AwsEc2MetadataClient.INSTANCE_TYPE_RESOURCE);
            post.addParameter("ec2InstanceType", awsEc2InstanceType);
        }
        post.addParameter("ec2Instance", instanceProductTypeResolver.getAwsProductNameForHeartBeat());
    }
    


    public void setAwsEc2MetadataClient(AwsEc2MetadataClient awsEc2MetadataClient) {
        this.awsEc2MetadataClient = awsEc2MetadataClient;
    }

    public void setInstanceProductTypeResolver(InstanceProductTypeResolver instanceProductTypeResolver) {
        this.instanceProductTypeResolver = instanceProductTypeResolver;
    }
}
