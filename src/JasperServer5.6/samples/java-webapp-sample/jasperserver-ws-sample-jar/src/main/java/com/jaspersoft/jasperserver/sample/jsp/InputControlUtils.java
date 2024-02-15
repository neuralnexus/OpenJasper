package com.jaspersoft.jasperserver.sample.jsp;

import com.jaspersoft.jasperserver.api.metadata.xml.domain.impl.ResourceDescriptor;

/**
 * Input controls utils.
 *
 * @author Yuriy Plakosh
 */
public class InputControlUtils {
    private InputControlUtils() {
    }

    public static boolean isMultiSelect(ResourceDescriptor rd) {
        checkInputControl(rd);

        return rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES ||
                rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_LIST_OF_VALUES_CHECKBOX ||
                rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY ||
                rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_SELECT_QUERY_CHECKBOX ||
                rd.getControlType() == ResourceDescriptor.IC_TYPE_MULTI_VALUE;
    }

    private static void checkInputControl(ResourceDescriptor rd) {
        if (!ResourceDescriptor.TYPE_INPUT_CONTROL.equals(rd.getWsType())) {
            throw new IllegalArgumentException("Not input control");
        }
    }
}
