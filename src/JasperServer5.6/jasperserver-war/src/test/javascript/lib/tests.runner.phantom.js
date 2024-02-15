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
 * @version: $Id: tests.runner.phantom.js 47331 2014-07-18 09:13:06Z kklein $
 */

function PhantomJasmineRunner(page, exitFunc) {

    this.getStatus = function () {
        return page.evaluate(function () {
            return jasmine.__console_reporter_status__
        });
    };

    this.terminate = function () {
        switch (this.getStatus()) {
            case "success":
                exitFunc(0);
                break;
            case "fail":
                exitFunc(1);
                break;
            default:
                exitFunc(2);
        }
    };

}

function JUnitXmlReportWriter(page, filePath) {
    var fs = require("fs");

    this.getContent = function () {
        return page.evaluate(function () {
            return jasmine.__junit_xml_output__;
        });
    };

    this.save = function () {
        if (filePath){
            var content = this.getContent();
            if (!content) {
                console.log("Can't get a JUnitXml content from the page");
            } else {
                console.log("Save tests reports to " + filePath);
                fs.write(filePath, content, 'w');
            }
        }
    }
}

//Script Begin
if (phantom.args.length == 0) {
    console.log("Need a url as the argument");
    phantom.exit(1);
}

var page = require('webpage').create();
var runner = new PhantomJasmineRunner(page, phantom.exit);
var junitReportWriter = new JUnitXmlReportWriter(page, phantom.args[2]);

//Don't suppress console output
page.onConsoleMessage = function (msg) {
    console.log(msg);
    //Terminate when the reporter signals that testing is over.
    //We cannot use a callback function for this (because page.evaluate is sandboxed),
    // so we have to *observe* the website.
    if (msg == "ConsoleReporter finished") {
        junitReportWriter.save();
        runner.terminate();
    }
};
page.onError = function(msg, trace) {
    var msgStack = ['ERROR: ' + msg];
    if (trace) {
        msgStack.push('TRACE:');
        trace.forEach(function(t) {
            msgStack.push(' -> ' + t.file + ': ' + t.line + (t.function ? ' (in function "' + t.function + '")' : ''));
        });
    }
    console.error(msgStack.join('\n'));
};
page.onAlert = function(msg) {
    console.log('ALERT: ' + msg);
};


var address = phantom.args[0];

// set a special variable to indicate the environment
page.addCookie({
    'name': 'phantomJS',
    'value': 'true'
});
page.addCookie({
    'name': 'edition',
    'value': phantom.args[1] || "none"
});

page.open(address, function (status) {
    if (status != "success") {
        var error_str = "tests.runner.phantom.js: can't load the address: '" + address + "', the status is: " + status;
        console.log(error_str);
        phantom.exit(1);
    } else {
        page.evaluate(function(testPath) {
            window.__test__ = testPath;
        }, phantom.args[3]);
    }
    //Now we wait until onConsoleMessage reads the termination signal from the log.
});

/* Global block avoider -- to be sure this script will not block the Ant's execution for more than X seconds */
executeTimeSeconds = 120;
setTimeout(function(){
    console.log(" !! " + executeTimeSeconds + " seconds have gone and the program is still working. Doing emergency stop...");
    phantom.exit(1);
}, executeTimeSeconds * 1000);
