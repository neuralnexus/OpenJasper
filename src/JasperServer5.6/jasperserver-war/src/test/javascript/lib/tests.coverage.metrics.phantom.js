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


/**
 * @author: dgorbenko
 * @version: $Id: tests.coverage.metrics.phantom.js 47331 2014-07-18 09:13:06Z kklein $
 */

var args = require('system').args;
var pathToJavaScriptSourceDir = args[1];
var pathToJSFilesToParse = args[2];
var pathToCoverageResultsDir = args[3];

console.log(" *** Counting global code coverage *** ");

var fs = require("fs");
var globalLinesOfCode = 0;
var amountOfFilesOnProject = 0;
var coverageResults = {};

// just a directory traversal function with callback for files
var processDir = function(dir, fileHandler) {
    var items = fs.list(dir);

    items.forEach(function(item){
        if (item == "." || item == "..") return;
        var fname = dir + "/" + item;

        if (!fs.isReadable(fname)) return;

        if (fs.isDirectory(fname)) {
            processDir(fname, fileHandler);
            return;
        }

        if (!fs.isFile(fname)) return;

        fileHandler(fname);
    });
};

// this function calculates total amount of lines of code on the project
var calculateGlobalLines = function() {
    processDir(pathToJSFilesToParse, function(fname) {

        amountOfFilesOnProject++;

        var content = fs.read(fname);
        // *.js
        content = content.replace(/(\/\*([\s\S]*?)\*\/)|(\/\/(.*)$)/gm, ''); // remove all comments
        content = content.replace(/[ _\t\r\|,\.\)\]\};\:\(\[\{]/gm, ''); // remove all useless characters
        var lines = content.split("\n");
        for (var i = 0, counter = 0; i < lines.length; i++) {
            if (lines[i] == "") continue;
            counter++;
        }
        globalLinesOfCode += counter;
    });
};


// this function fetches the coverage results
var calculateCoverage = function() {

    // check if there is a coverage report file
    var fname = pathToCoverageResultsDir + "/index.html";
    if (!fs.isReadable(fname) || !fs.isFile(fname)) {
        console.log("Failed to find the file 'index.html' containing coverage results");
        phantom.exit(1);
    }

    // create a Page
    var page = require('webpage').create();

    // load content into the Page with coverage results
    page.open(fname, function(status){

        if (status != "success") {
            console.log("Failed to load the file 'index.html' containing coverage results");
            phantom.exit(1);
        }

        // load jQuery inside the page to help us to search inside Page's DOM tree
        var jqueryFile = pathToJavaScriptSourceDir + "/lib/jquery/js/jquery-1.7.2.min.js";
        if (!fs.isReadable(jqueryFile) || !fs.isFile(jqueryFile)) {
            console.log("Failed to find the jquery source code file: " + jqueryFile);
            phantom.exit(1);
        }
        page.injectJs(jqueryFile);

        // now, run the effective code inside Page
        var coverage = page.evaluate(function() {
            var cells = jQuery("#summaryTotals").find("td");
            var statements = cells[1].innerHTML;
            var executed = cells[2].innerHTML;
            var filesCovered = jQuery("#summaryTable").find("tr").length;
            return statements + "|" + executed + "|" + filesCovered;
        }).split("|");

        coverageResults = {
            totalLines: coverage[0],
            covered: coverage[1],
            filesCovered: coverage[2]
        };

        // run the final function
        finalResults();
    });
};

// final function which displays gathered information
var finalResults = function() {
    console.log("** Results:");
    console.log("** Global lines of code: ", globalLinesOfCode);
    console.log("** Covered lines by tests: ", coverageResults.covered);
    console.log("** Total JavaScript files: ", amountOfFilesOnProject);
    console.log("** Covered files by tests: ", coverageResults.filesCovered);

    var globalFilesCoveredPercent = (coverageResults.filesCovered / amountOfFilesOnProject) * 100;
    globalFilesCoveredPercent = Math.round(globalFilesCoveredPercent * 100) / 100; // round it to two signs after period
    console.log("** Global files coverage percent: ", globalFilesCoveredPercent);

    var globalLinesCoveredPercent = (coverageResults.covered / globalLinesOfCode) * 100;
    globalLinesCoveredPercent = Math.round(globalLinesCoveredPercent * 100) / 100; // round it to two signs after period
    console.log("** Global lines coverage percent: ", globalLinesCoveredPercent);

    phantom.exit(0);
};

/* Global block avoider -- to be sure this script will not block the Ant's execution for more than X seconds */
executeTimeSeconds = 120;
setTimeout(function(){
    console.log(" !! " + executeTimeSeconds + " seconds have gone and the program is still working. Making emergency stop...");
    phantom.exit(1);
}, executeTimeSeconds * 1000);

calculateGlobalLines();
calculateCoverage();
