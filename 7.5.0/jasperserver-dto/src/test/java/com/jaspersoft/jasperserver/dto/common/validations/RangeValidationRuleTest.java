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

package com.jaspersoft.jasperserver.dto.common.validations;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

public class RangeValidationRuleTest extends BaseDTOPresentableTest<RangeValidationRule> {

    @Override
    protected List<RangeValidationRule> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setMaxValue(BigDecimal.valueOf(21.3)),
                createFullyConfiguredInstance().setMinValue(BigDecimal.valueOf(11.3)),
                createFullyConfiguredInstance().setIncludeMaxValue(false),
                createFullyConfiguredInstance().setIncludeMinValue(false),
                createFullyConfiguredInstance().setInverted(false),
                createFullyConfiguredInstance().setErrorMessage("error2"),
                // with null values
                createFullyConfiguredInstance().setMaxValue(null),
                createFullyConfiguredInstance().setMinValue(null),
                createFullyConfiguredInstance().setIncludeMaxValue(null),
                createFullyConfiguredInstance().setIncludeMinValue(null),
                createFullyConfiguredInstance().setInverted(null),
                createFullyConfiguredInstance().setErrorMessage(null)
        );
    }

    @Override
    protected RangeValidationRule createFullyConfiguredInstance() {
        RangeValidationRule rangeValidationRule = new RangeValidationRule();
        rangeValidationRule.setMaxValue(BigDecimal.valueOf(22.2));
        rangeValidationRule.setMinValue(BigDecimal.valueOf(12.2));
        rangeValidationRule.setIncludeMaxValue(true);
        rangeValidationRule.setIncludeMinValue(true);
        rangeValidationRule.setInverted(true);
        rangeValidationRule.setErrorMessage("error");
        return rangeValidationRule;
    }

    @Override
    protected RangeValidationRule createInstanceWithDefaultParameters() {
        return new RangeValidationRule();
    }

    @Override
    protected RangeValidationRule createInstanceFromOther(RangeValidationRule other) {
        return new RangeValidationRule(other);
    }
}
