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

package com.jaspersoft.buildomatic.masterlist

import java.nio.file.Files
import java.nio.file.Path
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream

class Scanner {
    // use the map constructor to create
    // Path to start on
    def top
    // return true to include the file

    def scanDistrFolder = { false }

    def include = { true }
    // return true to scan entries on the ZIP file
    def recurse = { ext == "war" }
    // scan also entries types
    def entriesScanTypes = { ext == "war" || ext == "zip"}
    // return output for each file
    def output = { null }

    // make Path relative to top and change to "/" so they are the same as zip paths
    def parsePath(Path path) {
        parsePath(top.relativize(path).join("/"))
    }

    // parse a path into dir, filename, basename, and extension
    // e.g. for "aa/bb/cc.dd" return [dir: "aa/bb", name: "cc.dd", basename: "cc", ext: "dd"]
    def parsePath(String str) {
        def m = (str =~ "(([^/]+(/[^/]+)*)/)?(.*)")
        if (m) {
            def (i0, i1, dir, i2, name) = m[0]
            def nameparts = name.split("\\.")
            if (nameparts.size() > 1) {
                return [dir: dir, name: name, basename: nameparts[0..-2].join("."), ext: nameparts[-1]]
            } else {
                return [dir: dir, name: name, basename: name, ext: null]
            }
        }
        null
    }

    // wrap an InputStream and block calls to close()
    private class NoCloseInputStream extends FilterInputStream {
        NoCloseInputStream(is) { super(is) }
        void close() {}
    }

    // what it says...byte[] to hex string
    def hexArray = "0123456789ABCDEF".toCharArray();
    def bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    // take a ZipEntry and an InputStream for reading the contents of the entry
    // return a map with info extracted from the entry or its contents
    def computeSHA(is) {
        new DigestInputStream(is, MessageDigest.getInstance("SHA-1")).withCloseable { digger ->
            // Read the stream and do nothing with it
            def b = new byte[32768]
            while (digger.read(b) != -1) {}
            // Get the digest and finalize the computation
            final MessageDigest md = digger.getMessageDigest();
            return bytesToHex(md.digest())
        }
    }

    // with no args scan from top
    def scan(path = top) {
        if(Files.isDirectory(path)) {
            return scanDir(path)
        } else {
            def pathinfo = parsePath(path)
            if (pathinfo.with(recurse)) {
                return scanZipEntries(path)
            } else if (pathinfo.with(include)) {
                path.withInputStream { is ->
                    pathinfo["sha1"] = computeSHA(is)
                }
                return pathinfo
            }
            return null
        }
    }

    def scanDir(dir) {
        def entries = []
        dir.eachFileRecurse { path ->
            def relpath = dir.relativize(path).join("/")
            def pathinfo = parsePath(relpath)
            if (pathinfo.with(include)) {
                entries += scan(path)
            }
        }
        return entries
    }

    // iterate through a ZIP file using ZipInputStream, forcing it to nest by tricking it not to close top-level InputStream
    // return a list of maps of entry info
    def scanZipEntries(path) {
        def entries = []
        path.withInputStream { is ->
            def bis = new BufferedInputStream(is, 1048576)
            def zis = new ZipInputStream(bis)
            def ze
            while (ze = zis.getNextEntry()) {
                def entry = parsePath(ze.name)
                String filePath = "${entry.dir}/${entry.name}"
                if (filePath.with(scanDistrFolder)) {
                    if (entry.with(entriesScanTypes)) {
                        entries += traverseZIS(new NoCloseInputStream(zis), filePath)
                    } else if (entry.with(include)) {
                        if (!ze.directory) {
                            def ncis = new NoCloseInputStream(zis)
                            entry["sha1"] = computeSHA(ncis)
                            entry.putAll(parseBaseName(entry.basename))
                        }
                        entries += entry
                    }
                }
            }
        }

        return entries
    }

    // read nested ZIP (non-recursive)
    // return list of entry info
    def traverseZIS(is, String zipPath) {
        def entries = []
        def zis = new ZipInputStream(is)
        def ze
        while (ze = zis.getNextEntry()) {
            def entry = parsePath(ze.name)
            String filePath = "${zipPath}/${entry.dir}/${entry.name}"
            if (filePath.with(scanDistrFolder)) {
                if (entry.with(entriesScanTypes)) {
                    entries += traverseZIS(new NoCloseInputStream(zis), filePath)
                } else if (entry.with(include)) {
                    if (!ze.directory) {
                        def nzis = new NoCloseInputStream(zis)
                        entry["sha1"] = computeSHA(nzis)
                        entry.putAll(parseBaseName(entry.basename))
                    }
                    entries += entry
                }
            }
        }
        return entries
    }

    def parseBaseName(String basename) {
        def libNameParts = basename =~ /((?:[a-zA-Z](?:(?:-(?!\d))?(?:\.?\w))+)(?=-\d+))(?:-((?:\.?\d+)+(?:-?\.?\w+)*))|((?:-?[\w]+)+)/
        libNameParts = libNameParts[0]
        String libName = libNameParts[1]
        String libVersion = libNameParts[2]
        if (libVersion == null) {
            return [libName:libNameParts[0], libVersion: ""]
        } else {
            return [libName:libName, libVersion:libVersion]
        }
    }
    
    // call scan() then eval the closure on each pathinfo map
    // for some reason, Groovy doesn't like it when I call this from a script...oh well
    def collect(closure) {
    	scan().collect { it.with(closure) }
    }
}