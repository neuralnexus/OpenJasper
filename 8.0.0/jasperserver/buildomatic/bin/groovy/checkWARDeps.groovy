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

import com.jaspersoft.buildomatic.masterlist.exception.ComparisonMismatchException
import com.jaspersoft.buildomatic.masterlist.util.build.CheckMasterList
import com.jaspersoft.buildomatic.masterlist.util.build.CheckResultObject

import java.nio.file.Files
import java.nio.file.Paths

def version = args[0]
def includeDistrPaths = args[1]
def depsFolder = args[2]
def resultPath = args[3]
def scanFoldersList = []
if (includeDistrPaths != "") {
	def scanFoldersPath = Paths.get(includeDistrPaths)
	if (!Files.isReadable(scanFoldersPath)) {
		fail("couldn't read exclusion filter file ${scanFoldersPath}")
	}
	scanFoldersList = scanFoldersPath.toFile() as List<String>
} else {
	scanFoldersList.add(version == "ce" ? "jasperserver.war" : "jasperserver-pro.war")
}

def libDepsResultList = []

scanFoldersList.each { String scanFolder ->

	String outputFolderName = scanFolder.endsWith(".war") ? "war" : scanFolder

	String masterFileName = "${depsFolder}/master-${outputFolderName}-deps-${version}.txt"
	String currentFileName = "${resultPath}/current-${outputFolderName}-deps-${version}.txt"

	def filePaths = [Paths.get(masterFileName), Paths.get(currentFileName)]
	filePaths.each { if (!Files.exists(it)) ant.fail("$it doesn't exist") }

	CheckMasterList checkMasterList = new CheckMasterList(masterFileName, currentFileName)

	def removedLibraryMap = checkMasterList.checkRemoved()
	def addedLibraryMap = checkMasterList.checkAdded()
	def changedLibraryMap = checkMasterList.checkChanged()

	if (addedLibraryMap.size() != 0 || removedLibraryMap.size() != 0 || changedLibraryMap.size() != 0) {
		ant.echo "The list of third-party JARs found in the WAR file:"
		ant.echo "  $currentFileName"
		ant.echo "is different from the master list:"
		ant.echo "  $masterFileName"
		ant.echo "If you didn't intend to change the dependencies, try to fix whatever caused this change and run the build again."
		ant.echo "If you did change the dependencies,"
		ant.echo "copy $currentFileName"
		ant.echo "to $masterFileName,"
		ant.echo "rebuild, and provide information on the dependency update in your commit message, or add a comment on the merge request"

		def outputPath = Paths.get("${resultPath}/${outputFolderName}-libs-result-${version}.txt")
		Files.createDirectories(outputPath.parent)

		def resultWriterClosure = { w, map ->
			map.each { libName, list ->
				list.each { lib ->
					w.writeLine("\t${lib.getLibName()} ${lib.getLibVersion()} ${lib.getLibSHA()}")
				}
			}
		}

		outputPath.withWriter { w ->
			w.writeLine("The following ${checkMasterList.getAddedLibrariesCount()} library(-ies) have been added:")
			resultWriterClosure(w, addedLibraryMap)

			w.writeLine("\nThe following ${checkMasterList.getRemovedLibrariesCount()} library(-ies) have been removed:")
			resultWriterClosure(w, removedLibraryMap)

			w.writeLine("\nThe following ${checkMasterList.getChangedLibrariesCount()} library(-ies) have been changed:")
			changedLibraryMap.each { name, list ->
				list.each { lib ->
					if (lib.getChangedType() == CheckResultObject.ChangedType.version) {
						w.writeLine("\t${lib.getLibName()}: version has been changed from [${lib.getLibVersion()}] to [${lib.getChangedLibVersion()}] SHA: ${lib.getChangedLibSHA()}")
					} else {
						w.writeLine("\t${lib.getLibName()}: SHA has been changed from [${lib.getLibSHA()}] to [${lib.getChangedLibSHA()}] version: ${lib.getLibVersion()}")
					}
				}
			}
		}
		libDepsResultList.add(outputPath)
	}
}

libDepsResultList.each { resultFilePath ->
	throw new ComparisonMismatchException(new File(String.valueOf(resultFilePath)).text.toString())
}