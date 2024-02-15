package com.jaspersoft.jasperserver.war.cascade.utils;

import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlInformation;
import com.jaspersoft.jasperserver.api.engine.common.service.ReportInputControlValuesInformation;

/**
 * Dummy implementation of {@Link ReportInputControlInformation}
 * for tests only.
 */
public class ReportInputControlInformationMock implements ReportInputControlInformation {
    private String promptLabel;
    private String parameterName;
    private Class valueType;
    private Class nestedType;
    private Object defaultValue;
    private ReportInputControlValuesInformation valuesInformation;

    public String getPromptLabel() {
        return promptLabel;
    }

    public void setPromptLabel(String promptLabel) {
        this.promptLabel = promptLabel;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public Class getValueType() {
        return valueType;
    }

    public void setValueType(Class valueType) {
        this.valueType = valueType;
    }

    public Class getNestedType() {
        return nestedType;
    }

    public void setNestedType(Class nestedType) {
        this.nestedType = nestedType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ReportInputControlValuesInformation getReportInputControlValuesInformation() {
        return valuesInformation;
    }

    public void setReportInputControlValuesInformation(ReportInputControlValuesInformation valuesInformation) {
        this.valuesInformation = valuesInformation;
    }
}
