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

package com.jaspersoft.jasperserver.war.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 * @version $Id$
 */
class UserLocaleTest {
    private static final String DESCRIPTION = "description";
    private static final String CODE = "code";

    @Test
    void getAndSet_instanceWithDefaultValues() {
        final UserLocale instance = new UserLocale(null, null);

        assertAll("an instance with default values",
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getCode());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertNull(instance.getDescription());
                    }
                });
    }

    @Test
    void getAndSet_fullyConfiguredInstance() {
        final UserLocale instance = new UserLocale(null, null);
        instance.setCode(CODE);
        instance.setDescription(DESCRIPTION);

        assertAll("a fully configured instance",
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(CODE, instance.getCode());
                    }
                },
                new Executable() {
                    @Override
                    public void execute() {
                        assertEquals(DESCRIPTION, instance.getDescription());
                    }
                });
    }
}
