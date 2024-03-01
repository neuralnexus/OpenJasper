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
package com.jaspersoft.jasperserver.dto.adhoc.query.el;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientNumber;
import org.junit.Test;

import java.math.BigDecimal;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class ClientNumberTest {
    @Test
    public void equals_differentNumberTypeButTheSameNumericValue_true(){
        assertTrue(new ClientNumber(1).equals(new ClientNumber(new BigDecimal(1))));
        assertTrue(new ClientNumber(new Short((short) 10)).equals(new ClientNumber(new BigDecimal(10))));
        assertTrue(new ClientNumber(new Double(5.5)).equals(new ClientNumber(new BigDecimal(5.5))));
        assertTrue(new ClientNumber(new Float(111.5)).equals(new ClientNumber(new BigDecimal(111.5))));
    }
    
    @Test
    public void equals_withValueNull(){
        assertTrue(new ClientNumber().equals(new ClientNumber()));
        assertFalse(new ClientNumber(1).equals(new ClientNumber()));
        assertFalse(new ClientNumber().equals(new ClientNumber(1)));
    }
}
