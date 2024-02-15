package com.jaspersoft.jasperserver.export;

import com.jaspersoft.jasperserver.api.logging.access.domain.AccessEvent;
import com.jaspersoft.jasperserver.api.logging.access.service.impl.AccessService;
import com.jaspersoft.jasperserver.api.logging.context.LoggableEvent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Zakhar.Tomchenco
 * Date: 7/25/12
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AccessServiceMock implements AccessService {

    public void purgeAccessEvents() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<AccessEvent> getAllEvents(int firstResult, int maxResults) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getAccessEventsCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveEvent(LoggableEvent loggableEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveEvents(List<LoggableEvent> loggableEvents) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
