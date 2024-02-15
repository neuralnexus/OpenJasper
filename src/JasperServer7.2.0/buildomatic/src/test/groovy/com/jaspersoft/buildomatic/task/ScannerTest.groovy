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

package com.jaspersoft.buildomatic.task

import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.nio.file.Paths
import com.jaspersoft.buildomatic.masterlist.Scanner

class ScannerTest extends GroovyTestCase {

    private static final String DEPENDENCY_TEST_PATH = "com/jaspersoft/build/dependencyChecker/libraries/"
    private static final String DEPENDENCY_TEST_PATH1 = "com/jaspersoft/build/dependencyChecker/package.zip"
    private static PathMatcher rootPathMatcher = FileSystems.getDefault().getPathMatcher("glob:package**")

    private static String getTestFileAbsolutePath(String filePath) {
        ClassLoader classLoader = new Scanner().getClass().getClassLoader()
        File file = new File(classLoader.getResource(filePath).getFile())
        return file.getAbsolutePath()
    }

    void testParsePathWithExtension() {
        Scanner scanner = new Scanner()
        String parsedPath = scanner.parsePath("aa/bb/cc.dd")
        assertEquals "{dir=aa/bb, name=cc.dd, basename=cc, ext=dd}", parsedPath
    }

    void testParsePathWithoutExtension() {
        Scanner scanner = new Scanner()
        String parsedPath = scanner.parsePath("aa/bb/cc")
        assertEquals "{dir=aa/bb, name=cc, basename=cc, ext=null}", parsedPath
    }

    void testScanFolderForLibraries() {
        String path = getTestFileAbsolutePath(DEPENDENCY_TEST_PATH)
        Scanner scanner = new Scanner (
            top: Paths.get(path),
            recurse: {false},
                scanDistrFolder: { value ->
                    rootPathMatcher.matches(Paths.get(value))},
            include: {ext == "jar"}
        )
        def libraries = scanner.scan()
        assertEquals 4, libraries.size()
    }

    void testScanCompressedFileForLibraries() {
        String path = getTestFileAbsolutePath(DEPENDENCY_TEST_PATH1)
        Scanner scanner = new Scanner (
                top: Paths.get(path),
                recurse: {true},
                scanDistrFolder: { value ->
                    rootPathMatcher.matches(Paths.get(value))},
                include: {ext == "jar"}
        )
        def libraries = scanner.scan()
        assertEquals 6, libraries.size()
    }

    void testScanLibrariesInsideSpecialFolder() {
        String path = getTestFileAbsolutePath(DEPENDENCY_TEST_PATH1)
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*/Folder1**")
        Scanner scanner = new Scanner (
                top: Paths.get(path),
                recurse: {true},
                scanDistrFolder: { value ->
                    matcher.matches(Paths.get(value))},
                include: {ext == "jar"}
        )
        def libraries = scanner.scan()
        assertEquals 2, libraries.size()
    }

    void testFindLibVersion() {
        Scanner scanner = new Scanner()

        def testedLibFullNameList = ["Saxon-HE-9.6.0-4", "lib.rary.3.3-4.3.2.Final", "libraryWithoutVersion", "library-1.3.0-0",
                                     "lib_1.2_rary-3-version"]
        def expectedLibNameList = ["Saxon-HE", "lib.rary.3.3", "libraryWithoutVersion", "library", "lib_1.2_rary"]
        def expectedLibVersionList = ["9.6.0-4", "4.3.2.Final", "", "1.3.0-0", "3-version"]

        testedLibFullNameList.eachWithIndex { libFullName, i ->
            def result = scanner.parseBaseName(libFullName)
            assertEquals expectedLibNameList.get(i), result.libName
            assertEquals expectedLibVersionList.get(i), result.libVersion
        }
    }
}
