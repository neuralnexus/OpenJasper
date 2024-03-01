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

package TestNG;

import org.testng.Assert;
import org.testng.annotations.Test;

/*
 * TestNG throws java.io.FileNotFoundException: target/surefire-reports/testng.css when compiling with JAVA 11
 * with no testNG test cases found in the folder
 *
 * We add this dummy TESTNG case to avoid running into this FileNotFoundException from TestNG
 */
public class DummyTest {

    @Test()
    public void testDummy() {
        Assert.assertTrue(new Boolean("true"));
    }

}
