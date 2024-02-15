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
 * @author: inesterenko
 * @version: $Id: tests.jscover.phantom.js 47331 2014-07-18 09:13:06Z kklein $
 */

var fs = require("fs");

function PhantomCoverageRunner(page, exitFunc) {

    this.getStatus = function () {
        return page.evaluate(function () {
            return __code_coverage_reports__
        });
    };

    this.terminate = function () {
        var status = this.getStatus();
        if (status){
           exitFunc(0);
        }else{
           exitFunc(1);
        }
    };

}

function  CodeCoverageReportsWriter(page, folderPath){


    this.getContent = function () {
        return page.evaluate(function () {
            return __code_coverage_reports__
        });
    };

    this.save = function(){
        if (folderPath){
            var reports = this.getContent();
            console.log("Content: "+ reports);
            function saveReports (children){
                if (children){
                      for(var i= 0; i < children.length; i++){
                          var report = children[i];
                          if (report.name && report.markup){
                              fs.write(folderPath + report.name, report.markup ? report.markup : "can't get report's content", 'w');
                          }
                          saveReports(report.children);
                      }
                }
            }
            saveReports(reports);
            console.log("Save tests reports to " + folderPath +  "index.html");
        }
    }

}

//Script Begin
if (phantom.args.length < 2) {
    console.log("Need a url for document to open and url for document with tests");
    phantom.exit(1);
}

var docToOpenUrl = phantom.args[0];
var docWithTestsUrl = "file:///" + fs.absolute(phantom.args[1]);


console.log("Document to open: "+ docToOpenUrl);
console.log("Document with tests: "+ docWithTestsUrl);


var page = require('webpage').create();
// set a special variable to indicate the environment
page.addCookie({
    'name': 'phantomJS',
    'value': 'true'
});


var runner = new PhantomCoverageRunner(page, phantom.exit);
var codeCoverageReportsWriter = new CodeCoverageReportsWriter(page, phantom.args[2]);
//
////Don't supress console output
page.onConsoleMessage = function (msg) {
    console.log(msg);
    //We cannot use a callback function for this (because page.evaluate is sandboxed),
    // so we have to *observe* the website.
    if (msg == "Coverage report is ready") {
        codeCoverageReportsWriter.save();
        phantom.exit(0);
    }
};

var isPageCalled = false;
page.open(docToOpenUrl, function (status) {
    if (status != "success") {
        console.log("tests.jscover.phantom.js: can't load the address: " + docToOpenUrl);
        phantom.exit(1);
    }else{
        if(!isPageCalled){
            //fix side effect from iFrame on the page, it's triggers multiple page onload events
            isPageCalled  = true;
            console.log("Open page" + docToOpenUrl);
            //Inject script which cut off coverage report markup
            page.injectJs("lib/jcover.summary.report.js");
            //Start injected script
            page.evaluate(function(url){
                console.log("Start to generate code coverage report");
                window.generateCoverageReport(url);
            }, docWithTestsUrl);
        }
    }
    //Now we wait until onConsoleMessage reads the termination signal from the log.
});

/* Global block avoider -- to be sure this script will not block the Ant's execution for more than X seconds */
executeTimeSeconds = 120;
setTimeout(function(){
    console.log(" !! " + executeTimeSeconds + " seconds have gone and the program is still working. Doing emergency stop...");
    phantom.exit(1);
}, executeTimeSeconds * 1000);


