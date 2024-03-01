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

package com.jaspersoft.buildomatic.masterlist.util.build

class CheckMasterList  {

    String masterListPath
    String currentListPath

    def masterFileDescriptorMap = [:]
    def currentFileDescriptorMap = [:]

    def addedMap = [:]
    def removedMap = [:]
    def changedMap = [:]

    CheckMasterList(){

    }

    CheckMasterList(def masterListPath, def currentListPath) {
        this.masterListPath = masterListPath
        this.currentListPath = currentListPath
        init()
    }

    def init() {
        masterFileDescriptorMap = convertListPathToDescriptionMap(masterListPath)
        currentFileDescriptorMap = convertListPathToDescriptionMap(currentListPath)
    }

    def convertListPathToDescriptionMap(String listPath) {
        File currentListFile = new File(listPath)
        def fileDescriptionMap = convertFileToMap(currentListFile)
        fileDescriptionMap.sort()
        return fileDescriptionMap
    }

    def convertFileToMap(def listFile) {
        def resultMap = [:]
        def fileLines = listFile.readLines()
        fileLines.sort()
        fileLines.each { String line ->
            line = line.trim()
            if (!isEmptyLine(line)) {
                def splitLine = line.split(" ")
                if (splitLine.size() > 0) {
                    FileDescriptor lineFileDescriptor = new FileDescriptor(*splitLine)
                    if (resultMap.get(lineFileDescriptor.getLibName()) == null) {
                        resultMap.put(lineFileDescriptor.getLibName(), [])
                    }
                    def libNameVariety = resultMap.get(lineFileDescriptor.getLibName())
                    libNameVariety.add(lineFileDescriptor)
                    resultMap.put(lineFileDescriptor.getLibName(), libNameVariety)
                }
            }
        }
        return resultMap
    }

    def isEmptyLine(String line) {
        return line == "" || line == " "
    }

    def checkRemoved() {
        removedMap = [:]
        def removedLibNameList = getMasterKeySet() - getCurrentKeySet()
        removedLibNameList.each { libName ->
            removedMap.put(libName, masterFileDescriptorMap.get(libName))
        }
        getMasterKeySet().each { String libName ->
            if (!removedLibNameList.contains(libName)) {
                def removedList = getRemovedItemsList(libName)
                def addedList = getAddedItemsList(libName)

                def resultList = []
                if (removedList.size() > addedList.size()) {
                    resultList = getExcessItemsList(removedList, addedList)
                }
                if (resultList.size() > 0) {
                    removedMap.put(libName, resultList)
                }
            }
        }
        return removedMap
    }

    def checkAdded() {
        addedMap = [:]
        def addedLibNameList = getCurrentKeySet() - getMasterKeySet()
        addedLibNameList.each { libName ->
            addedMap.put(libName, currentFileDescriptorMap.get(libName))
        }
        getCurrentKeySet().each { String libName ->
            if (!addedLibNameList.contains(libName)) {
                def removedList = getRemovedItemsList(libName)
                def addedList = getAddedItemsList(libName)

                def resultList = []
                if (addedList.size() > removedList.size()) {
                    resultList = getExcessItemsList(addedList, removedList)
                }
                if (resultList.size() > 0) {
                    addedMap.put(libName, resultList)
                }
            }
        }

        return addedMap

    }

    def checkChanged() {
        changedMap = [:]
        getMasterKeySet().each { String libName ->
            def masterFileDescriptorList = masterFileDescriptorMap.get(libName)
            def currentFileDescriptorList = currentFileDescriptorMap.get(libName)

            if (masterFileDescriptorList != null && currentFileDescriptorList != null) {
                def removedList = getRemovedItemsList(libName)
                def addedList = getAddedItemsList(libName)

                def resultList = []
                if (addedList.size() < removedList.size()) {
                    addedList.eachWithIndex { FileDescriptor addedLibrary, index ->
                        FileDescriptor removedLibrary = removedList.get(index)
                        CheckResultObject checkResultObject = detectLibraryChange(addedLibrary, removedLibrary)
                        if (checkResultObject != null) {
                            resultList.add(checkResultObject)
                        }
                    }
                } else {
                    removedList.eachWithIndex { FileDescriptor removedLibrary, index ->
                        FileDescriptor addedLibrary = addedList.get(index)
                        CheckResultObject checkResultObject = detectLibraryChange(addedLibrary, removedLibrary)
                        if (checkResultObject != null) {
                            resultList.add(checkResultObject)
                        }
                    }
                }

                if (resultList.size() > 0) {
                    changedMap.put(libName, resultList)
                }
            }
        }
        return changedMap
    }

    def detectLibraryChange(FileDescriptor addedLibrary, FileDescriptor removedLibrary) {
        CheckResultObject checkResultObject = null
        if (isVersionChanged(addedLibrary, removedLibrary)) {
            checkResultObject = getVersionChangedLibraryObject(removedLibrary, addedLibrary)
        } else if (isSHAChanged(addedLibrary, removedLibrary)) {
            checkResultObject = getSHAChangedLibraryObject(removedLibrary, addedLibrary)
        }

        return checkResultObject
    }

    def isVersionChanged(FileDescriptor originalLib, FileDescriptor currentLib) {
        return !originalLib.getLibVersion().equals(currentLib.getLibVersion())
    }

    def isSHAChanged(FileDescriptor originalLib, FileDescriptor currentLib) {
        return !originalLib.getLibSHA().equals(currentLib.getLibSHA())
    }


    def getVersionChangedLibraryObject(FileDescriptor originalLibrary, FileDescriptor changedLibrary) {
        CheckResultObject checkResultObject = new CheckResultObject(originalLibrary)
        checkResultObject.setChangedType(CheckResultObject.ChangedType.version)
        checkResultObject.setChangedLibVersion(changedLibrary.getLibVersion())
        checkResultObject.setChangedLibSHA(changedLibrary.getLibSHA())

        return checkResultObject
    }

    def getSHAChangedLibraryObject(FileDescriptor originalLibrary, FileDescriptor changedLibrary) {
        CheckResultObject checkResultObject = new CheckResultObject(originalLibrary)
        checkResultObject.setChangedType(CheckResultObject.ChangedType.SHA)
        checkResultObject.setChangedLibSHA(changedLibrary.getLibSHA())

        return checkResultObject
    }

    def getExcessItemsList(def list1, def list2) {
        def resultList = []
        list1.eachWithIndex{ fileDescriptor, index ->
            if (index >= list2.size()) {
                resultList.add(new CheckResultObject(fileDescriptor))
            }
        }
        return resultList
    }

    def getRemovedItemsList(String libName) {
        def masterLibNameList = masterFileDescriptorMap.get(libName)
        def currentLibNameList = currentFileDescriptorMap.get(libName)
        return getRemovedItemsList(masterLibNameList, currentLibNameList)
    }

    def getAddedItemsList(String libName) {
        def masterLibNameList = masterFileDescriptorMap.get(libName)
        def currentLibNameList = currentFileDescriptorMap.get(libName)
        return getAddedItemsList(masterLibNameList, currentLibNameList)
    }

    def getRemovedItemsList(def list1, def list2) {
        return list1 - list2
    }

    def getAddedItemsList(def list1, def list2) {
        return list2 - list1
    }

    def getMasterKeySet() {
        return masterFileDescriptorMap.keySet()
    }

    def getCurrentKeySet() {
        return currentFileDescriptorMap.keySet()
    }

    def getAddedLibrariesCount() {
        return countSizeClosure(0, addedMap)
    }

    def getRemovedLibrariesCount() {
        return countSizeClosure(0, removedMap)
    }

    def getChangedLibrariesCount() {
        return countSizeClosure(0, changedMap)
    }

    def countSizeClosure = { total, m ->
        return total + m.inject(0) { subTotal, k, v ->
            subTotal + v.size()
        }
    }
}
