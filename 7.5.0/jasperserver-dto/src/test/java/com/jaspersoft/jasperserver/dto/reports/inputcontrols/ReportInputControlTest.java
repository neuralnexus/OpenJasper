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
package com.jaspersoft.jasperserver.dto.reports.inputcontrols;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import com.jaspersoft.jasperserver.dto.common.validations.DateTimeFormatValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.MandatoryValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.RangeValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.RegexpValidationRule;
import com.jaspersoft.jasperserver.dto.common.validations.ValidationRule;
import com.jaspersoft.jasperserver.dto.resources.ClientDataType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author yaroslav.kovalchyk
 * @version $Id: ReportInputControlTest.java 58644 2015-10-16 12:19:26Z ykovalch $
 */
public class ReportInputControlTest extends BaseDTOPresentableTest<ReportInputControl> {

    private static final String TEST_ID = "TEST_ID";
    private static final String TEST_ID_1 = "TEST_ID_1";

    private static final String TEST_DESCRIPTION = "TEST_DESCRIPTION";
    private static final String TEST_DESCRIPTION_1 = "TEST_DESCRIPTION_1";

    private static final String TEST_TYPE = "TEST_TYPE";
    private static final String TEST_TYPE_1 = "TEST_TYPE_1";

    private static final String TEST_URI = "TEST_URI";
    private static final String TEST_URI_1 = "TEST_URI_1";

    private static final String TEST_LABEL = "TEST_LABEL";
    private static final String TEST_LABEL_1 = "TEST_LABEL_1";

    private static final Boolean TEST_MANDATORY = true;
    private static final Boolean TEST_MANDATORY_1 = false;

    private static final Boolean TEST_READ_ONLY = true;
    private static final Boolean TEST_READ_ONLY_1 = false;

    private static final Boolean TEST_VISIBLE = true;
    private static final Boolean TEST_VISIBLE_1 = false;

    private static final List<ValidationRule> TEST_VALIDATION_RULES = Arrays.asList(
            (ValidationRule)new DateTimeFormatValidationRule().setErrorMessage("TEST_ERROR_MESSAGE").setErrorMessage("TEST_ERROR_MESSAGE"),
            (ValidationRule)new MandatoryValidationRule().setErrorMessage("TEST_ERROR_MESSAGE"),
            (ValidationRule)new RangeValidationRule().setErrorMessage("TEST_ERROR_MESSAGE"),
            (ValidationRule)new RegexpValidationRule().setErrorMessage("TEST_ERROR_MESSAGE")
    );
    private static final List<ValidationRule> TEST_VALIDATION_RULES_1 = Arrays.asList(
            (ValidationRule)new DateTimeFormatValidationRule().setErrorMessage("TEST_ERROR_MESSAGE").setErrorMessage("TEST_ERROR_MESSAGE_1"),
            (ValidationRule)new MandatoryValidationRule().setErrorMessage("TEST_ERROR_MESSAGE_1"),
            (ValidationRule)new RangeValidationRule().setErrorMessage("TEST_ERROR_MESSAGE_1"),
            (ValidationRule)new RegexpValidationRule().setErrorMessage("TEST_ERROR_MESSAGE_1")
    );
    private static final List<ValidationRule> TEST_VALIDATION_RULES_EMPTY = new ArrayList<ValidationRule>();

    private static final InputControlState TEST_STATE = new InputControlState().setId("TEST_ID");
    private static final InputControlState TEST_STATE_1 = new InputControlState().setId("TEST_ID_1");

    private static final ClientDataType TEST_DATA_TYPE = new ClientDataType().setPattern("TEST_PATTERN");
    private static final ClientDataType TEST_DATA_TYPE_1 = new ClientDataType().setPattern("TEST_PATTERN_1");

    private static final List<String> TEST_MASTER_DEPENDENCIES = Collections.singletonList("TEST_MASTER_DEPENDENCY");
    private static final List<String> TEST_MASTER_DEPENDENCIES_1 = Collections.singletonList("TEST_MASTER_DEPENDENCY_1");
    private static final List<String> TEST_MASTER_DEPENDENCIES_EMPTY = new ArrayList<String>();

    private static final List<String> TEST_SLAVE_DEPENDENCIES = Collections.singletonList("TEST_SLAVE_DEPENDENCY");
    private static final List<String> TEST_SLAVE_DEPENDENCIES_1 = Collections.singletonList("TEST_SLAVE_DEPENDENCY_1");
    private static final List<String> TEST_SLAVE_DEPENDENCIES_EMPTY = new ArrayList<String>();

    @Test
    public void setDataType_resetGenericResourceFields(){
        final ClientDataType dataType = new ClientDataType();
        //set specific fields
        dataType.setMaxLength(100).setMaxValue("100").setMinValue("10").setPattern("testPattern").setStrictMax(true)
                .setStrictMin(true).setType(ClientDataType.TypeOfDataType.number);
        // clone data type to verify result
        final ClientDataType dataTypeClone = new ClientDataType(dataType);
        // set generic fields
        dataType.setUri("testUri").setVersion(100)
                .setCreationDate("creationDate").setUpdateDate("updateDate").setPermissionMask(32)
                .setDescription("description").setLabel("label");
        final ClientDataType result = new ReportInputControl().setDataType(dataType).getDataType();
        // verify that generic fields are reset
        assertTrue(dataTypeClone.equals(result));
    }

    /*
     * BaseDTOPresentableTests
     */

    @Override
    protected List<ReportInputControl> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setId(TEST_ID_1),
                createFullyConfiguredInstance().setDescription(TEST_DESCRIPTION_1),
                createFullyConfiguredInstance().setType(TEST_TYPE_1),
                createFullyConfiguredInstance().setUri(TEST_URI_1),
                createFullyConfiguredInstance().setLabel(TEST_LABEL_1),
                createFullyConfiguredInstance().setMandatory(TEST_MANDATORY_1),
                createFullyConfiguredInstance().setReadOnly(TEST_READ_ONLY_1),
                createFullyConfiguredInstance().setVisible(TEST_VISIBLE_1),
                createFullyConfiguredInstance().setMasterDependencies(TEST_MASTER_DEPENDENCIES_1),
                createFullyConfiguredInstance().setMasterDependencies(TEST_MASTER_DEPENDENCIES_EMPTY),
                createFullyConfiguredInstance().setSlaveDependencies(TEST_SLAVE_DEPENDENCIES_1),
                createFullyConfiguredInstance().setSlaveDependencies(TEST_SLAVE_DEPENDENCIES_EMPTY),
                createFullyConfiguredInstance().setValidationRules(TEST_VALIDATION_RULES_1),
                createFullyConfiguredInstance().setValidationRules(TEST_VALIDATION_RULES_EMPTY),
                createFullyConfiguredInstance().setState(TEST_STATE_1),
                createFullyConfiguredInstance().setDataType(TEST_DATA_TYPE_1),
                // null values
                createFullyConfiguredInstance().setId(null),
                createFullyConfiguredInstance().setDescription(null),
                createFullyConfiguredInstance().setType(null),
                createFullyConfiguredInstance().setUri(null),
                createFullyConfiguredInstance().setLabel(null),
                createFullyConfiguredInstance().setMandatory(null),
                createFullyConfiguredInstance().setReadOnly(null),
                createFullyConfiguredInstance().setVisible(null),
                createFullyConfiguredInstance().setMasterDependencies(null),
                createFullyConfiguredInstance().setSlaveDependencies(null),
                createFullyConfiguredInstance().setValidationRules(null),
                createFullyConfiguredInstance().setState(null),
                createFullyConfiguredInstance().setDataType(null)
        );
    }

    @Override
    protected ReportInputControl createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setId(TEST_ID)
                .setDescription(TEST_DESCRIPTION)
                .setType(TEST_TYPE)
                .setUri(TEST_URI)
                .setLabel(TEST_LABEL)
                .setMandatory(TEST_MANDATORY)
                .setReadOnly(TEST_READ_ONLY)
                .setVisible(TEST_VISIBLE)
                .setMasterDependencies(TEST_MASTER_DEPENDENCIES)
                .setSlaveDependencies(TEST_SLAVE_DEPENDENCIES)
                .setValidationRules(TEST_VALIDATION_RULES)
                .setState(TEST_STATE)
                .setDataType(TEST_DATA_TYPE);
    }

    @Override
    protected ReportInputControl createInstanceWithDefaultParameters() {
        return new ReportInputControl();
    }

    @Override
    protected ReportInputControl createInstanceFromOther(ReportInputControl other) {
        return new ReportInputControl(other);
    }
}
