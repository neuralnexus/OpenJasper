/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.jaspersoft.jasperserver.remote.resources.validation;

import com.jaspersoft.jasperserver.api.JSValidationException;
import com.jaspersoft.jasperserver.api.metadata.common.domain.ListOfValues;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesImpl;
import com.jaspersoft.jasperserver.api.metadata.common.domain.client.ListOfValuesItemImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * <p></p>
 *
 * @author Zakhar.Tomchenco
 * @version $Id$
 */
public class ListOfValuesValidatorTest {
    final ListOfValuesValidator validator = new ListOfValuesValidator();
    ListOfValues lov;

    @BeforeMethod
    public void setUp(){
        lov = new ListOfValuesImpl();
        lov.setLabel("aa");
    }

    @Test
    public void testValidate() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("test");
        item.setValue("lala");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_nullItemLabel() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setValue("lala");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_nullItemValue() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("lala");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyItemLabel() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("");
        item.setValue("lala");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_emptyItemValue() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("lala");
        item.setValue("");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_spasesItemValue() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("lala");
        item.setValue("      ");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_spasesItemLabel() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("     ");
        item.setValue("a");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_ItemLabelTooLong() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setLabel("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        item.setValue("a");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_ItemValueTooLong() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setValue("123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
        item.setLabel("a");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_ItemLabelUnsupportedSymbols() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setValue("1234567890");
        item.setLabel("<>");
        lov.addValue(item);

        validator.validate(lov);
    }

    @Test(expectedExceptions = {JSValidationException.class})
    public void testValidate_ItemValueUnsupportedSymbols() throws Exception {
        ListOfValuesItemImpl item = new ListOfValuesItemImpl();
        item.setValue("<>");
        item.setLabel("a");
        lov.addValue(item);

        validator.validate(lov);
    }
}
