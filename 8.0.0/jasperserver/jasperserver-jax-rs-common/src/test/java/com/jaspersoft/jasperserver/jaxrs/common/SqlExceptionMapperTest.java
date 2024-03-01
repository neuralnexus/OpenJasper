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
package com.jaspersoft.jasperserver.jaxrs.common;

import com.jaspersoft.jasperserver.remote.exception.SqlErrorDescriptorBuilder;
import org.testng.annotations.Test;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * <p></p>
 *
 * @author mchan
 * @version $Id$
 */
public class SqlExceptionMapperTest {
    @Test
    public void toResponse(){
        final SqlErrorDescriptorBuilder sqlErrorDescriptorBuilder = new SqlErrorDescriptorBuilder();
        final Response result = new SqlExceptionMapper()
                .toResponse(new SQLException("Test"), sqlErrorDescriptorBuilder);
        assertNotNull(result);
        assertEquals(result.getStatus(), 400);
    }
}
