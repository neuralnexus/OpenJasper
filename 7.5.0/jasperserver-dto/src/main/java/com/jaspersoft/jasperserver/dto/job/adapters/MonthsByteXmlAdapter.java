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
package com.jaspersoft.jasperserver.dto.job.adapters;

import com.jaspersoft.jasperserver.dto.job.wrappers.ClientMonthsSortedSetWrapper;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */
public class MonthsByteXmlAdapter extends XmlAdapter<ClientMonthsSortedSetWrapper, SortedSet<Byte>>{
    @Override
    public SortedSet<Byte> unmarshal(ClientMonthsSortedSetWrapper v) throws Exception {
        SortedSet<Byte> result = null;
        if(v != null && v.getMongths() != null && !v.getMongths().isEmpty()){
            result = new TreeSet<Byte>();
            for(String value : v.getMongths())
                result.add(Byte.valueOf(value));
        }
        return result;
    }

    @Override
    public ClientMonthsSortedSetWrapper marshal(SortedSet<Byte> v) throws Exception {
        ClientMonthsSortedSetWrapper result = null;
        if(v != null && !v.isEmpty()){
            SortedSet<String> strings = new TreeSet<String>();
            for(Byte value : v)
                strings.add(value.toString());
            result = new ClientMonthsSortedSetWrapper(strings);
        }
        return result;
    }
}
