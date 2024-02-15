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
package com.jaspersoft.jasperserver.remote.services.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.TimeZone;

/**
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class TimeZoneXmlAdapter extends XmlAdapter<String, TimeZone>{
    @Override
    public TimeZone unmarshal(String v) throws Exception {
        return TimeZone.getTimeZone(v);
    }

    @Override
    public String marshal(TimeZone v) throws Exception {
        return v.getID();
    }
}
