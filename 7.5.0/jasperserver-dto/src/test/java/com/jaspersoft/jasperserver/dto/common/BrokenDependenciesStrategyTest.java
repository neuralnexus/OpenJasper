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

package com.jaspersoft.jasperserver.dto.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class BrokenDependenciesStrategyTest {

    @Test
    public void BrokenDependenciesStrategyParseStringReturnsSkipStrategy() {
        BrokenDependenciesStrategy brokenDependenciesStrategy = BrokenDependenciesStrategy.parseString("skip");

        assertEquals(BrokenDependenciesStrategy.SKIP, brokenDependenciesStrategy);
    }

    @Test
    public void BrokenDependenciesStrategyParseStringIsCaseInsensitive() {
        BrokenDependenciesStrategy brokenDependenciesStrategy = BrokenDependenciesStrategy.parseString("iNcLuDe");

        assertEquals(BrokenDependenciesStrategy.INCLUDE, brokenDependenciesStrategy);
    }

    @Test
    public void BrokenDependenciesStrategyParseStringReturnsFailStrategyByDefault() {
        BrokenDependenciesStrategy brokenDependenciesStrategy = BrokenDependenciesStrategy.parseString("JUST WRONG STRING");

        assertEquals(BrokenDependenciesStrategy.FAIL, brokenDependenciesStrategy);
    }

    @Test
    public void IncludeBrokenDependenciesStrategyReturnIncludeLabel() {
        BrokenDependenciesStrategy brokenDependenciesStrategy = BrokenDependenciesStrategy.INCLUDE;

        assertEquals("include", brokenDependenciesStrategy.getLabel());
    }
}
