package com.jaspersoft.jasperserver.war.cascade.utils;

import java.util.Map;

public interface QueryExecutorTestCaseKeyGenerator {
    public String generateKey(String name, Map<String, Object> paramValues) throws Exception;
}
