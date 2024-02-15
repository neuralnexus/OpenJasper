/*
 * Copyright (c) 2008-2013 Pivotal Labs. This software is licensed under the MIT License.
 */

(function () {

    if (!jasmine) {
        throw new Exception("jasmine library does not exist in global namespace!");
    }

    //store tests result in hidden property
    jasmine.__junit_xml_output__ = "";


    /*
     SuiteJunitSerializer is our custom XML reporter, it takes structures (suite and specs)
     created by Jasmine and creates XML report for our internal purposes.
     If you need to include other reported, please, see into JUnitXmlReporter function: you'll
     find there declaration of used serializer.
     */
    var SuiteJunitSerializer = function() {

    };

    SuiteJunitSerializer.prototype = {

        TEST_SUITE_TEMPLATE: '<testsuite name="{name}" errors="{errs}" failures="{fails}" tests="{count}" time="{time}" timestamp="{timestamp}">{childs}</testsuite>',
        TEST_CASE_TEMPLATE: '<testcase classname="{classname}" name="{name}" time="{time}">{body}</testcase>',

        buildReportOnSuite: function (suite) {
            var results = suite.results();

            var resultOnChilds = "";
            for (var i = 0, child; i < suite.children_.length; i++) {
                child = suite.children_[i];
                if (child instanceof jasmine.Spec) {
                    resultOnChilds += this.buildReportOnSpec(child);
                }
                else if (child instanceof jasmine.Suite) {
                    resultOnChilds += this.buildReportOnSuite(child);
                }
            }

            // in some cases (as instance in case of using xit() functions
            // some variables can be undefined
            // it happens because Jasmine does not register these functions at all.
            if (typeof suite.startTime === "undefined") {
                suite.startTime = new Date();
                suite.endTime = new Date();
            }

            var xml = this.TEST_SUITE_TEMPLATE
                .replace("{name}", this.encodeXML(suite.description))
                .replace("{errs}", 0)
                .replace("{fails}", results.failedCount)
                .replace("{count}", results.totalCount)
                .replace("{time}", this.elapsed(suite.startTime, suite.endTime))
                .replace("{timestamp}", this.ISODateString(suite.startTime))
                .replace("{childs}", resultOnChilds);

            return xml;
        },

        buildReportOnSpec: function (spec) {

            var specBody = "";
            var results = spec.results();
            if (!results.passed()) {
                var items = results.getItems();
                for (var i = 0; i < items.length; i++) {
                    var trace = items[i].trace.stack || items[i].trace;
                    specBody += "<failure>" + this.encodeXML(trace) + "</failure>\n";
                }
            }

            var output = this.TEST_CASE_TEMPLATE
                .replace("{classname}", this.encodeXML(spec.suite.description))
                .replace("{name}", this.encodeXML(spec.description))
                .replace("{time}", this.elapsed(spec.startTime, spec.endTime))
                .replace("{body}", specBody);

            return output;
        },

        encodeXML: function(str) {
            if (typeof str !== "string") return "";

            return str
                .replace(/&/g, '&amp;')
                .replace(/</g, '&lt;')
                .replace(/>/g, '&gt;')
                .replace(/"/g, '&quot;');
        },

        elapsed: function(startTime, endTime) {
            return (endTime - startTime) / 1000;
        },

        ISODateString: function(d) {
            function pad(n) {
                return n < 10 ? '0' + n : n;
            }

            return d.getFullYear() + '-'
                + pad(d.getMonth() + 1) + '-'
                + pad(d.getDate()) + 'T'
                + pad(d.getHours()) + ':'
                + pad(d.getMinutes()) + ':'
                + pad(d.getSeconds());
        }
    };


    /**
     * Generates JUnit XML for the given spec run.
     * Allows the test results to be used in java based CI
     * systems like CruiseControl and Hudson.
     */

    var JUnitXmlReporter = function () {
        this.serializer = new SuiteJunitSerializer();
    };

    //Add static fields
    JUnitXmlReporter.SuiteJunitSerializer = SuiteJunitSerializer;

    JUnitXmlReporter.prototype = {

        reportRunnerResults: function (runner) {

            var output = "<?xml version='1.0' encoding='UTF-8' ?>\n<testsuite userAgent='" + (navigator ? navigator.userAgent : "") + "'>\n";
            for (var id in this.suitesDictionary) {
                if (!this.suitesDictionary.hasOwnProperty(id)) continue;
                output += this.serializer.buildReportOnSuite(this.suitesDictionary[id]) + "\n";
            }
            output += "</testsuite>";
            jasmine.__junit_xml_output__ = output;
        },

        reportRunnerStarting: function () {
            this.suitesDictionary = {};
        },

        reportSpecResults: function (spec) {
            spec.endTime = new Date();
        },

        reportSuiteResults: function(suite){
            suite.endTime = new Date();
        },

        reportSpecStarting: function (spec) {
            spec.startTime = new Date();
            if (!spec.suite.startTime) {
                spec.suite.startTime = new Date();
            }
            if (!this.suitesDictionary[spec.suite.id]) {
                this.suitesDictionary[spec.suite.id] = spec.suite;
            }
        }

    };

    // export public
    jasmine.JUnitXmlReporter = JUnitXmlReporter;
})();