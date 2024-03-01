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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class StandardHeartbeatCallTest {
    private static final String NAME = "name";
    private static final String VALUE = "value";

    private static final List<NameValuePair> NAME_VALUE_PAIR_LIST = new ArrayList<NameValuePair>() {{
        add(new BasicNameValuePair(NAME, VALUE));
    }};

    @Test
    void getAndSet_instanceWithDefaultValues() {
        StandardHeartbeatCall instance = new StandardHeartbeatCall();
        assertTrue(instance.getParameters().isEmpty());
    }

    @Test
    void getAndSet_fullyConfiguredInstance() {
        StandardHeartbeatCall instance = new StandardHeartbeatCall();
        instance.addParameter(NAME, VALUE);

        assertEquals(NAME_VALUE_PAIR_LIST, instance.getParameters());
    }

}
