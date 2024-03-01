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

package com.jaspersoft.jasperserver.dto.resources;

import com.jaspersoft.jasperserver.dto.basetests.BaseDTOPresentableTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Andriy Tivodar <ativodar@tibco>
 */

class ClientBundleTest extends BaseDTOPresentableTest<ClientBundle> {

    private static final String TEST_LOCALE = "TEST_LOCALE";
    private static final String TEST_LOCALE_1 = "TEST_LOCALE_1";

    private static final ClientReferenceableFile TEST_FILE = new ClientReference().setUri("TEST_URI");
    private static final ClientReferenceableFile TEST_FILE_1 = new ClientReference().setUri("TEST_URI_1");

    @Override
    protected List<ClientBundle> prepareInstancesWithAlternativeParameters() {
        return Arrays.asList(
                createFullyConfiguredInstance().setLocale(TEST_LOCALE_1),
                createFullyConfiguredInstance().setFile(TEST_FILE_1),
                // fields with null values
                createFullyConfiguredInstance().setLocale(null),
                createFullyConfiguredInstance().setFile(null)
        );
    }

    @Override
    protected ClientBundle createFullyConfiguredInstance() {
        return createInstanceWithDefaultParameters()
                .setLocale(TEST_LOCALE)
                .setFile(TEST_FILE);
    }

    @Override
    protected ClientBundle createInstanceWithDefaultParameters() {
        return new ClientBundle();
    }

    @Override
    protected ClientBundle createInstanceFromOther(ClientBundle other) {
        return new ClientBundle(other);
    }

    @Test
    public void instanceCanBeCreatedFromLocaleAndFileParameters() {
        String locale = "locale";
        ClientReferenceableFile file = new ClientFile().setLabel("file");

        ClientBundle result = new ClientBundle(locale, file);

        assertEquals(locale, result.getLocale());
        assertEquals(file, result.getFile());
    }

    @Test
    public void instanceCanBeCreatedFromOtherWithClientFile() {
        ClientReferenceableFile file = new ClientFile().setLabel("file");
        fullyConfiguredTestInstance.setFile(file);

        ClientBundle result = new ClientBundle(fullyConfiguredTestInstance);

        assertEquals(file, result.getFile());
    }

}