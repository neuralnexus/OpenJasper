package com.jaspersoft.jasperserver.war.cascade.utils;

import org.apache.commons.collections.map.LinkedMap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link LinkedMap}
 * which allows to convert Map<Object, List<String>> internally to Map<Object, String[]>.
 * This is useful when we instantiate LinkedMap through spring context
 */
public class OrderedMapMock extends LinkedMap {

    public OrderedMapMock(Map<Object, List<String>> map) {
        Map<Object, String[]> tempMap = new LinkedHashMap<Object, String[]>(map.size());
        for (Map.Entry<Object, List<String>> entry: map.entrySet()) {
            List<String> value = entry.getValue();
            tempMap.put(entry.getKey(), value != null ? value.toArray(new String[] {}) : null);
        }

        this.putAll(tempMap);
    }


}
