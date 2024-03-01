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

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class TimeStringTest extends BaseDTOTest<TimeString> {

    @Override
    protected List<TimeString> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setTime("time2"),
                // with null values
                createFullyConfiguredInstance().setTime(null)
        );
    }

    @Override
    protected TimeString createFullyConfiguredInstance() {
        TimeString timeString = new TimeString();
        timeString.setTime("time");
        return timeString;
    }

    @Override
    protected TimeString createInstanceWithDefaultParameters() {
        return new TimeString();
    }

    @Override
    protected TimeString createInstanceFromOther(TimeString other) {
        return new TimeString(other);
    }

    @Test
    public void createdWithParamsTimeStringHaveSameTime() {
        TimeString timeString1 = createFullyConfiguredInstance();
        TimeString timeString2 = new TimeString(timeString1.getTime());

        assertSame(timeString1.getTime(), timeString2.getTime());
    }
}
