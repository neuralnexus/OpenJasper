/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.war.common;

import com.jaspersoft.jasperserver.api.engine.jasperreports.util.AwsEc2MetadataClient;
import com.jaspersoft.jasperserver.api.engine.jasperreports.util.InstanceProductTypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class HeartbeatAwsEc2ContributorTest {
    private static final String EC_2_INSTANCE_KEY = "ec2Instance";
    private static final String EC_2_INSTANCE_TYPE_KEY = "ec2InstanceType";

    private static final String AWS_PRODUCT_NAME_FOR_HEART_BEAT = "awsProductNameForHeartBeat";
    private static final String AWS_EC_2_INSTANCE_TYPE = "awsEc2InstanceType";


    private HeartbeatAwsEc2Contributor objectUnderTest = new HeartbeatAwsEc2Contributor();
    private AwsEc2MetadataClient awsEc2MetadataClient = mock(AwsEc2MetadataClient.class);
    private InstanceProductTypeResolver instanceProductTypeResolver = mock(InstanceProductTypeResolver.class);
    private HeartbeatCall heartbeatCall = mock(HeartbeatCall.class);

    @BeforeEach
    void setup() {
        objectUnderTest.setAwsEc2MetadataClient(awsEc2MetadataClient);
        objectUnderTest.setInstanceProductTypeResolver(instanceProductTypeResolver);

        doReturn(AWS_PRODUCT_NAME_FOR_HEART_BEAT).when(instanceProductTypeResolver).getAwsProductNameForHeartBeat();
        doReturn(AWS_EC_2_INSTANCE_TYPE).when(awsEc2MetadataClient).getEc2InstanceMetadataItem(AwsEc2MetadataClient.INSTANCE_TYPE_RESOURCE);
    }

    @Test
    void contributeToHttpCall_notEc2Instance_ec2InstanceParamIsContributed() {
        doReturn(false).when(awsEc2MetadataClient).isEc2Instance();

        objectUnderTest.contributeToHttpCall(heartbeatCall);

        verify(heartbeatCall).addParameter(EC_2_INSTANCE_KEY, AWS_PRODUCT_NAME_FOR_HEART_BEAT);
    }

    @Test
    void contributeToHttpCall_ec2Instance_ec2InstanceTypeAndEc2InstanceParamsAreContributed() {
        doReturn(true).when(awsEc2MetadataClient).isEc2Instance();

        objectUnderTest.contributeToHttpCall(heartbeatCall);

        verify(heartbeatCall).addParameter(EC_2_INSTANCE_KEY, AWS_PRODUCT_NAME_FOR_HEART_BEAT);
        verify(heartbeatCall).addParameter(EC_2_INSTANCE_TYPE_KEY, AWS_EC_2_INSTANCE_TYPE);
    }
}
