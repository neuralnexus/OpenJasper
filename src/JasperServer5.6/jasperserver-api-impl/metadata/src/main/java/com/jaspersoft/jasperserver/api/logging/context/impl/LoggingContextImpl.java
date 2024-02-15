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
package com.jaspersoft.jasperserver.api.logging.context.impl;

import com.jaspersoft.jasperserver.api.logging.context.LoggingContext;
import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class LoggingContextImpl implements LoggingContext {

    private List<LoggableEvent> events = new ArrayList<LoggableEvent>();
    private Map<Class<? extends LoggableEvent>, Boolean> enabledLoggingTypesMap;

    public void setEnabledLoggingTypesMap(Map<Class<? extends LoggableEvent>, Boolean> enabledLoggingTypesMap) {
        this.enabledLoggingTypesMap = enabledLoggingTypesMap;
    }

    private boolean isLogEnabled(LoggableEvent loggableEvent) {
        boolean isEnabled = false;

        for (Class<? extends LoggableEvent> clazz: enabledLoggingTypesMap.keySet()) {
            if (clazz.isAssignableFrom(loggableEvent.getClass())) {
                isEnabled = true;
                break;
            }
        }

        return isEnabled;
    }

    public void logEvent(LoggableEvent loggableEvent) {
        if (loggableEvent != null && isLogEnabled(loggableEvent)) {
            events.add(loggableEvent);
        }
    }

    public List<LoggableEvent> getAllEvents() {
        return Collections.unmodifiableList(events);
    }

    public void removeEvents(List<LoggableEvent> events) {
        for (LoggableEvent event: events) {
            this.events.remove(event);
        }
    }

    public void clearAllEvents() {
        events.clear();
    }
}
