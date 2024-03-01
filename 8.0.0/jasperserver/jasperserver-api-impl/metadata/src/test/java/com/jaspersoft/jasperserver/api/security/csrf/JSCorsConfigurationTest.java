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

package com.jaspersoft.jasperserver.api.security.csrf;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
//@PrepareForTest({
//        Pattern.class,
//        Matcher.class
//})
public class JSCorsConfigurationTest {


    @InjectMocks
    JSCorsConfiguration  config;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    DomainWhitelistProvider whitelistProvider;

    //Matcher matcher = PowerMockito.mock(Matcher.class);
    Pattern mockPattern;// = PowerMockito.mock(Pattern.class);
    private final String EXT_TEST_URL = "http://fiddle.jshell.net";
    private List<String> testWhitelistAttributes = new ArrayList<String>();


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockPattern = Pattern.compile("\\s*");
        Mockito.when(whitelistProvider.getWhitelistPattern()).thenReturn(mockPattern);
    }

    @Test
    public void checkOriginforNullRequestOrigin()   {

        String requestOrigin = null;
        assertEquals(config.checkOrigin(requestOrigin), null);

    }

    @Test
    public void checkOriginForEmptyWhiteList(){
        config.setAdditionalWhitelistAttributes(testWhitelistAttributes);
        assertEquals(config.checkOrigin(EXT_TEST_URL), null);

    }

    @Test
    public void checkOriginForALLWhiteListwithAllowCredentialsFalse(){
        testWhitelistAttributes.add("*");
        config.setAdditionalWhitelistAttributes(testWhitelistAttributes);
        assertEquals(config.checkOrigin(EXT_TEST_URL), "*");

    }

    @Test
    public void checkOriginForALLWhiteListwithAllowCredentialsSet(){
        testWhitelistAttributes.add("*");
        config.setAdditionalWhitelistAttributes(testWhitelistAttributes);
        config.setAllowCredentials(true);
        assertEquals(config.checkOrigin(EXT_TEST_URL), EXT_TEST_URL);

    }

    @Test
    public void checkOriginforValidRequestOrigin() throws Exception   {
        Pattern testPattern = mockPattern.compile(".*?");
        Mockito.when(whitelistProvider.getWhitelistPattern()).thenReturn(testPattern);
        assertEquals(config.checkOrigin(EXT_TEST_URL), EXT_TEST_URL);

    }


}
