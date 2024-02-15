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
 * @version: $Id: console.jasmine.reporter.js 47331 2014-07-18 09:13:06Z kklein $
 */

/**
 Jasmine Reporter that outputs test results to the browser console.
 Useful for running in a headless environment such as PhantomJs, ZombieJs etc.

 Usage:
 // From your html file that loads jasmine:
 jasmine.getEnv().addReporter(new jasmine.ConsoleReporter());
 jasmine.getEnv().execute();
 */

(function (jasmine, console) {
    if (!jasmine) {
        throw "jasmine library isn't loaded!";
    }

    var ConsoleReporter = function () {
        this.statuses = {
            stopped:"stopped",
            running:"running",
            fail:"fail",
            success:"success"
        };
        if (!console || !console.log) {
            throw "console isn't present!";
        }
        this.setStatus(this.statuses.stopped)
    };

    ConsoleReporter.prototype.reportRunnerStarting = function (runner) {
        exportStatus = this.statuses.running;
        this.start_time = (new Date()).getTime();
        this.executed_specs = 0;
        this.passed_specs = 0;
        this.suitesDictionary = {}
    };

    ConsoleReporter.prototype.setStatus = function (status) {
        this.status = status;
        //export status to common namespace
        jasmine.__console_reporter_status__ = status;
    };

    ConsoleReporter.prototype.reportRunnerResults = function (runner) {
        var failed = this.executed_specs - this.passed_specs;
        var spec_str = this.executed_specs + (this.executed_specs === 1 ? " spec, " : " specs, ");
        var fail_str = failed + (failed === 1 ? " failure in " : " failures in ");
        var color = (failed > 0) ? "red" : "green";
        var dur = (new Date()).getTime() - this.start_time;

        this.log("");
        this.log("Finished");
        this.log("-----------------");
        this.log(spec_str + fail_str + (dur / 1000) + "s.", color);

        this.setStatus((failed > 0) ? this.statuses.fail : this.statuses.success);

        delete this.suitesDictionary;

        /* Print something that signals that testing is over so that headless browsers
         like PhantomJs know when to terminate. */
        this.log("");
        this.log("ConsoleReporter finished");
    };


    ConsoleReporter.prototype.reportSpecStarting = function (spec) {
        this.executed_specs++;
        //TODO: check parentSuite
        if (!spec.suite.parentSuite && !this.suitesDictionary[spec.suite.id]) {
            this.log("");
            this.log(spec.suite.description);
            this.suitesDictionary[spec.suite.id] = spec.suite
        }
        this.log("\t" + spec.suite.description + ' : ' + spec.description + ' ... ');
    };

    ConsoleReporter.prototype.reportSpecResults = function (spec) {
        if (spec.results().passed()) {
            this.passed_specs++;
            return;
        }

        var resultText = spec.suite.description + " : " + spec.description;
        this.log(resultText, "red");
        this.log("===========================FAILED!============================");
        var items = spec.results().getItems();
        for (var i = 0; i < items.length; i++) {
            var trace = items[i].trace.stack || items[i].trace;
            this.log(trace, "red");
        }
    };

    ConsoleReporter.prototype.reportSuiteResults = function (suite) {
        if (!suite.parentSuite) {
            return;
        }
        var results = suite.results();
        var failed = results.totalCount - results.passedCount;
        var color = (failed > 0) ? "red" : "green";
        this.log("\t" + suite.description + ": " + results.passedCount + " of " + results.totalCount + " passed.", color);
    };

    ConsoleReporter.prototype.log = function (str, color) {
        var console = jasmine.getGlobal().console;

        if (console && console.log) {
            console.log(str);
        }
    };

    jasmine.ConsoleReporter = ConsoleReporter;

})(jasmine, console);

