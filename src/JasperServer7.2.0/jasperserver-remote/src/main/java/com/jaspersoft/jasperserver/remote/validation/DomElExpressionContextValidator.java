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
package com.jaspersoft.jasperserver.remote.validation;

import com.jaspersoft.jasperserver.dto.domain.DomElExpressionContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p></p>
 *
 * @author tiefimen
 * @version $Id$$
 */
@Component
public class DomElExpressionContextValidator implements ClientValidator<DomElExpressionContext> {

    @Resource
    ConsistentExpressionRepresentationsValidator representationsValidator;

    @Override
    public List<Exception> validate(DomElExpressionContext value) {
        return representationsValidator.validate(value.getExpression());
    }

}
