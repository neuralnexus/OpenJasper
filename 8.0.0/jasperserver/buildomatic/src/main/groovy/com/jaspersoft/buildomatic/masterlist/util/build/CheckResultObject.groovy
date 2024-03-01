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

package com.jaspersoft.buildomatic.masterlist.util.build

class CheckResultObject {

    enum ChangedType {
        version("Version"), SHA("SHA")

        String type

        ChangedType(String type) {
            this.type = type
        }

        String getType() {
            return type
        }
    }

    String libName
    String libVersion
    String changedLibVersion
    String libSHA
    String changedLibSHA
    ChangedType changedType

    CheckResultObject() {

    }

    CheckResultObject(String libName, String libVersion, String libSHA) {
        this.libName = libName
        this.libVersion = libVersion
        this.libSHA = libSHA
    }

    CheckResultObject(FileDescriptor fileDescriptor) {
        this.libName = fileDescriptor.getLibName()
        this.libVersion = fileDescriptor.getLibVersion()
        this.libSHA = fileDescriptor.getLibSHA()
    }

    def getLibName() {
        return libName
    }

    def getLibVersion() {
        return libVersion
    }

    def getLibSHA() {
        return libSHA
    }

    def setChangedLibVersion(String changedLibVersion) {
        this.changedLibVersion = changedLibVersion
    }

    def getChangedLibVersion() {
        return changedLibVersion
    }

    def setChangedLibSHA(String changedLibSHA) {
        this.changedLibSHA = changedLibSHA
    }

    def getChangedLibSHA() {
        return changedLibSHA
    }

    def setChangedType(ChangedType type) {
        this.changedType = type
    }

    ChangedType getChangedType() {
        return changedType
    }

}
