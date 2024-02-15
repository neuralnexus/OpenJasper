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

package com.jaspersoft.jasperserver.api.metadata.common.service.impl.hibernate.util;

import org.apache.commons.collections.SetUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.LikeExpression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.engine.spi.TypedValue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.CharUtils.isAsciiPrintable;

/**
 * A criterion representing a "like" expression
 * that explicitly specifies escape character.
 *
 * @author askorodumov
 * @version $Id$
 */
public class LikeEscapeAwareExpression extends LikeExpression {

    private final String propertyName;
    private final Object value;

    public LikeEscapeAwareExpression(String propertyName, String value, MatchMode matchMode, Character escapeChar) {
        super(propertyName, value, matchMode, escapeChar, false);
        this.propertyName = propertyName;
        this.value = matchMode.toMatchString(value);

        checkEscapeChar(escapeChar);
    }

    public LikeEscapeAwareExpression(String propertyName, String value, Character escapeChar) {
        super(propertyName, value, escapeChar, false);
        this.propertyName = propertyName;
        this.value = value;

        checkEscapeChar(escapeChar);
    }

    @Override
    public TypedValue[] getTypedValues(
            Criteria criteria,
            CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] {
                criteriaQuery.getTypedValue( criteria, propertyName, value.toString() )
        };
    }

    static void checkEscapeChar(char escapeChar) {
        if (!isAsciiPrintable(escapeChar)) {
            throw new IllegalArgumentException("Illegal escape character");
        }
    }
}
