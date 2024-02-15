package com.jaspersoft.jasperserver.war.cascade.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JSONQueryExecutorKeyGenerator implements QueryExecutorTestCaseKeyGenerator {
    public static final String NO_REPLACEMENT = "\"";

    private ObjectMapper jsonMapper = new ObjectMapper();
    private String replacementForDoubleQuotes = NO_REPLACEMENT;
    private Boolean quoteFieldNames = true;

    public void setReplacementForDoubleQuotes(String replacementForDoubleQuotes) {
        this.replacementForDoubleQuotes = replacementForDoubleQuotes;
    }

    public void setQuoteFieldNames(Boolean quoteFieldNames) {
        this.quoteFieldNames = quoteFieldNames;
    }

    @Override
    public String generateKey(String name, Map<String, Object> paramValues) throws Exception {
        jsonMapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, quoteFieldNames);

        Map<String, Object> sortedParamValues = new LinkedHashMap<String, Object>(paramValues.size());
        List<String> paramNamesList = new ArrayList<String>(paramValues.keySet());
        Collections.sort(paramNamesList);

        for (String paramName: paramNamesList) {
            sortedParamValues.put(paramName, paramValues.get(paramName));
        }

        String json = jsonMapper.writeValueAsString(sortedParamValues);
        if (!NO_REPLACEMENT.equals(replacementForDoubleQuotes)) {
            json = json.replaceAll("\"", replacementForDoubleQuotes);
        }

        return json;
    }
}
