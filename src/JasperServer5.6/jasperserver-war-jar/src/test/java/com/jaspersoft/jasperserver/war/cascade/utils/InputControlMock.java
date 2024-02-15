package com.jaspersoft.jasperserver.war.cascade.utils;

import com.jaspersoft.jasperserver.api.metadata.common.domain.client.InputControlImpl;

import java.util.List;

/**
 * @author Sergey Prilukin
 * @version $Id$
 */
public class InputControlMock extends InputControlImpl {

    public void setQueryVisibleColumnsList(List<String> columns) {
        getQueryVisibleColumnsAsList().clear();
        for (String column: columns) {
            addQueryVisibleColumn(column);
        }
    }
}
