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

package com.jaspersoft.jasperserver.jaxrs.resources;

import org.junit.Test;
import org.junit.Assert;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;

public class RepositoryJaxrsServiceTest {

    @InjectMocks
    private RepositoryJaxrsService repositoryJaxrsService = new RepositoryJaxrsService();

    @BeforeClass
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void decodeHeaderContentLocation_UTF8() throws Exception {
        String sourceUri = "/organizations/organization_1/%E3%83%86%E3%82%B9%E3%83%88%E3%83%95%E3%82%A9%E3%83%AB%E3%83%80";

        String val = repositoryJaxrsService.decodeHeaderContentLocation(sourceUri);
        String decodedUri = "/organizations/organization_1/テストフォルダ";
        Assert.assertEquals(decodedUri, val);
    }

}
