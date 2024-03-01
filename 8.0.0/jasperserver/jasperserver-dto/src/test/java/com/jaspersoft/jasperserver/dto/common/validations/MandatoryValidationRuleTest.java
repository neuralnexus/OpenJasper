/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
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

package com.jaspersoft.jasperserver.dto.common.validations;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class MandatoryValidationRuleTest extends BaseDTOPresentableTest<MandatoryValidationRule> {

    @Override
    protected List<MandatoryValidationRule> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setErrorMessage("error2"),
                // with null values
                createFullyConfiguredInstance().setErrorMessage(null)
        );
    }

    @Override
    protected MandatoryValidationRule createFullyConfiguredInstance() {
        MandatoryValidationRule mandatoryValidationRule = new MandatoryValidationRule();
        mandatoryValidationRule.setErrorMessage("error");
        return mandatoryValidationRule;
    }

    @Override
    protected MandatoryValidationRule createInstanceWithDefaultParameters() {
        return new MandatoryValidationRule();
    }

    @Override
    protected MandatoryValidationRule createInstanceFromOther(MandatoryValidationRule other) {
        return new MandatoryValidationRule(other);
    }
}
