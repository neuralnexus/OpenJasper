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

package com.jaspersoft.buildomatic.task

import com.jaspersoft.buildomatic.masterlist.util.build.CheckMasterList


class CheckMasterListTest extends GroovyTestCase {

    private static final String DEPENDENCY_TEST_PATH1 = "com/jaspersoft/build/dependencyChecker/list1.txt"
    private static final String DEPENDENCY_TEST_PATH2 = "com/jaspersoft/build/dependencyChecker/list2.txt"

    private static String getTestFileAbsolutePath(String filePath) {
        ClassLoader classLoader = new CheckMasterList().getClass().getClassLoader()
        File file = new File(classLoader.getResource(filePath).getFile())
        return file.getAbsolutePath()
    }

    void testCheckAddedLibraries() {
        CheckMasterList checkMasterList = new CheckMasterList(getTestFileAbsolutePath(DEPENDENCY_TEST_PATH1),
                getTestFileAbsolutePath(DEPENDENCY_TEST_PATH2))
        def list = checkMasterList.checkAdded()
        assertEquals 1, list.size()
        assertEquals"library-1.4", list.get(0)
    }

    void testCheckRemovedLibraries() {
        CheckMasterList checkMasterList = new CheckMasterList(getTestFileAbsolutePath(DEPENDENCY_TEST_PATH1),
                getTestFileAbsolutePath(DEPENDENCY_TEST_PATH2))
        def list = checkMasterList.checkRemoved()
        assertEquals 1, list.size()
        assertEquals"library-1.2", list.get(0)

    }

    void testCheckChangedLibraries() {
        CheckMasterList checkMasterList = new CheckMasterList(getTestFileAbsolutePath(DEPENDENCY_TEST_PATH1),
                getTestFileAbsolutePath(DEPENDENCY_TEST_PATH2))
        def list = checkMasterList.checkChanged()
        assertEquals 1, list.size()
        assertEquals"library-1.3", list.get(0)

    }
}
