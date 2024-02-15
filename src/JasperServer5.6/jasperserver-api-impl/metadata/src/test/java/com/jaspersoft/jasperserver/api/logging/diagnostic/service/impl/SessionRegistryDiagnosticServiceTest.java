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

package com.jaspersoft.jasperserver.api.logging.diagnostic.service.impl;

import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttribute;
import com.jaspersoft.jasperserver.api.logging.diagnostic.domain.DiagnosticAttributeImpl;
import com.jaspersoft.jasperserver.api.logging.diagnostic.helper.DiagnosticAttributeBuilder;
import com.jaspersoft.jasperserver.api.logging.diagnostic.service.DiagnosticCallback;
import org.junit.Before;
import org.springframework.security.concurrent.SessionInformation;
import org.springframework.security.concurrent.SessionRegistry;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import org.junit.Test;
import org.unitils.inject.annotation.InjectInto;
import org.unitils.inject.annotation.TestedObject;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;


/**
 * Tests for {@link SessionRegistryDiagnosticService}
 *
 * @author vsabadosh
 */
public class SessionRegistryDiagnosticServiceTest extends UnitilsJUnit4 {

    @TestedObject
    private SessionRegistryDiagnosticService sessionRegistryDiagnosticService;

    @InjectInto(property = "sessionRegistry")
    private Mock<SessionRegistry> sessionRegistryMock;

    private String user1 = "User1";
    private String user2 = "User2";
    private String user3 = "User3";

    @Before
    public void setUp() {
        Object[] principals = new Object[3];
        principals[0] = user1;
        principals[1] = user2;
        principals[2] = user3;
        
        sessionRegistryMock.returns(principals).getAllPrincipals();

        SessionInformation[] sessionInfosForUser1 = new SessionInformation[3];
        sessionInfosForUser1[0] = new SessionInformation(user1, "sessionId1", new Date());
        sessionInfosForUser1[1] = new SessionInformation(user1, "sessionId2", new Date());
        sessionInfosForUser1[2] = new SessionInformation(user1, "sessionId3", new Date());

        SessionInformation[] sessionInfosForUser2 = new SessionInformation[2];
        sessionInfosForUser2[0] = new SessionInformation(user2, "sessionId4", new Date());
        sessionInfosForUser2[1] = new SessionInformation(user2, "sessionId5", new Date());

        SessionInformation[] sessionInfosForUser3 = new SessionInformation[1];
        sessionInfosForUser3[0] = new SessionInformation(user3, "sessionId6", new Date());

        sessionRegistryMock.returns(sessionInfosForUser1).getAllSessions(user1, false);
        sessionRegistryMock.returns(sessionInfosForUser2).getAllSessions(user2, false);
        sessionRegistryMock.returns(sessionInfosForUser3).getAllSessions(user3, false);
    }

    @Test
    public void getDiagnosticDataTest() {
        Map<DiagnosticAttribute, DiagnosticCallback> resultDiagnosticData = sessionRegistryDiagnosticService.getDiagnosticData();

        //Test total size of diagnostic attributes collected from SessionRegistryDiagnosticService
        assertEquals(2, resultDiagnosticData.size());

        //Test actual values of diagnostic attributes
        assertEquals(3, resultDiagnosticData.get(new DiagnosticAttributeImpl(
                DiagnosticAttributeBuilder.TOTAL_LOGGED_IN_USERS, null, null)).getDiagnosticAttributeValue());
        Map<String, Integer> sessionsCountByUserMap = (Map<String, Integer>)resultDiagnosticData.
                get(new DiagnosticAttributeImpl(DiagnosticAttributeBuilder.TOTAL_SESSIONS_BY_USER, null, null)).getDiagnosticAttributeValue();
        assertEquals(3, sessionsCountByUserMap.size());

        int sessionsForUser1 = sessionsCountByUserMap.get(user1);
        assertEquals(3, sessionsForUser1);

        int sessionsForUser2 = sessionsCountByUserMap.get(user2);
        assertEquals(2, sessionsForUser2);

        int sessionsForUser3 = sessionsCountByUserMap.get(user3);
        assertEquals(1, sessionsForUser3);
    }


}