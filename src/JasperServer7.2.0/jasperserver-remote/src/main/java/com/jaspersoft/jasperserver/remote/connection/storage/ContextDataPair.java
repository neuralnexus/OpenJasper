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
package com.jaspersoft.jasperserver.remote.connection.storage;

import java.util.Map;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id$
 */
public class ContextDataPair {
    private final Object context;
    private final Map<String, Object> data;
    private Class externalContextClass;

    public ContextDataPair(Object context, Map<String, Object> data) {
        this.context = context;
        this.data = data;
    }

    public Object getContext() {
        return context;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Class getExternalContextClass() {
        return externalContextClass;
    }

    public ContextDataPair setExternalContextClass(Class externalContextClass) {
        this.externalContextClass = externalContextClass;
        return this;
    }
}
