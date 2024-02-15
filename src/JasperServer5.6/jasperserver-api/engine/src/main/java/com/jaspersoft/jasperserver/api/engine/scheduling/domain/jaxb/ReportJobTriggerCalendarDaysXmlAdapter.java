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
package com.jaspersoft.jasperserver.api.engine.scheduling.domain.jaxb;

import com.jaspersoft.jasperserver.api.common.domain.jaxb.AbstractEnumXmlAdapter;
import com.jaspersoft.jasperserver.api.common.domain.jaxb.NamedPropertyHolder;
import com.jaspersoft.jasperserver.api.engine.scheduling.domain.ReportJobCalendarTrigger;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id: ReportJobTriggerCalendarDaysXmlAdapter.java 47331 2014-07-18 09:13:06Z kklein $
 */
public class ReportJobTriggerCalendarDaysXmlAdapter extends AbstractEnumXmlAdapter<Byte> {
    @Override
    protected NamedPropertyHolder<Byte>[] getEnumConstantsArray() {
        return DayTypes.values();
    }

    public enum DayTypes implements NamedPropertyHolder<Byte> {
        ALL(ReportJobCalendarTrigger.DAYS_TYPE_ALL),
        WEEK(ReportJobCalendarTrigger.DAYS_TYPE_WEEK),
        MONTH(ReportJobCalendarTrigger.DAYS_TYPE_MONTH);

        private final Byte byteValue;

        private DayTypes(Byte byteValue) {
            this.byteValue = byteValue;
        }
         public Byte getProperty(){
             return this.byteValue;
         }
    }




}
