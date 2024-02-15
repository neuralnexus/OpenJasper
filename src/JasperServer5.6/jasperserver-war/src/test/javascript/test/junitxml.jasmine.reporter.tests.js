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
 * @version: $Id: junitxml.jasmine.reporter.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["junitxml.jasmine.reporter"],function (JUnitXmlReporter) {

    describe("Test JUnitXmlReporter inside Jasmin framework", function () {

        var xmlReport;

        beforeEach(function(){
            xmlReport = new jasmine.JUnitXmlReporter();
        });

        it("Jasmin has object JUnitXmlReporter", function (){
            expect(jasmine.JUnitXmlReporter).toBeDefined();
        });

        describe("Testing public JUnitXmlReporter methods", function () {

            var fakeSpec, fakeSuit;

            beforeEach(function(){
                fakeSpec = {suite : {id: "test"}};
                fakeSuit = {};
                xmlReport.reportRunnerStarting();
            });

            it("should define specDictionary property on runner start", function(){
                xmlReport.reportRunnerStarting();
                expect(xmlReport.suitesDictionary).toEqual(jasmine.any(Object));
            });

            it("should extend spec and suite object with 'startTime' on spec start", function(){
                xmlReport.reportSpecStarting(fakeSpec);
                expect(fakeSpec.startTime).toBeDefined();
                expect(fakeSpec.suite.startTime).toBeDefined();
            });

            it("should register spec in specDictionary on spec start", function(){
                xmlReport.reportSpecStarting(fakeSpec);
                expect(xmlReport.suitesDictionary["test"]).toEqual(fakeSpec.suite);
            });

            it("should extend spec object with 'endTime' on spec results", function(){
                xmlReport.reportSpecResults(fakeSpec);
                expect(fakeSpec.endTime).toBeDefined();
            });

            it("should extend suit object with 'endTime' on suit results", function(){
                xmlReport.reportSuiteResults(fakeSuit);
                expect(fakeSuit.endTime).toBeDefined();
            });
        });


        describe("Testing Serializer interfaces", function(){

            var xmlReport;

            beforeEach(function(){
                xmlReport = new jasmine.JUnitXmlReporter();
            });

            it("Jasmin has Serializer object in JUnitXmlReporter", function (){
                expect(xmlReport.serializer).toBeDefined();
            });

            it("Serializer has methods to process Suit and Spec", function (){
                expect(xmlReport.serializer.buildReportOnSuite).toBeDefined();
                expect(xmlReport.serializer.buildReportOnSpec).toBeDefined();
            });


            describe("Testing Reporter and Serializer output", function(){

                var xmlReport, clock, suite, spec, timeStamp;

                beforeEach(function(){
                    var ts = 1355741574321;
                    clock = sinon.useFakeTimers(ts);

                    xmlReport = new jasmine.JUnitXmlReporter();

                    timeStamp = xmlReport.serializer.ISODateString(clock.Date());

                    // --------------------------------
                    // prepare suite
                    suite = {
                        results: function(){ return {
                            totalCount: 1,
                            failedCount: 2
                        };},
                        description: "empty",
                        startTime: new Date(),
                        endTime: new Date(),
                        totalCount: 0,
                        failedCount: 0,
                        children_: []
                    };

                    // --------------------------------
                    // prepare spec
                    var env = {
                        nextSpecId: function(){return "env_spec_id";}
                    };


                    spec = new jasmine.Spec(env, suite, "some_description");
                    spec.results = function(){
                        return {
                            passed: function(){return true;}
                        }
                    };
                    spec.description = "AAA";
                    spec.suite = {
                        description: "test"
                    };
                    spec.startTime = new Date();
                    spec.endTime = new Date();


                    // add spec into suite children
                    suite.children_.push(spec);

                });

                afterEach(function(){
                    clock.restore();
                });


                it("has correct output from buildReportOnSpec method", function (){
                    var correctOutput = '<testcase classname="test" name="AAA" time="0"></testcase>';
                    var output = xmlReport.serializer.buildReportOnSpec(spec);
                    expect(output).toEqual(correctOutput);
                });

                it("has correct output from buildReportOnSpec method in case of failure", function (){
                    spec.results = function(){
                        return {
                            passed: function(){return false;},
                            getItems: function(){
                                return [
                                    {trace: {stack: "this is the trace format 1"}},
                                    {trace: "this is the trace format 2"}
                                ];
                            }
                        }
                    };

                    var correctOutput = '<testcase classname="test" name="AAA" time="0"><failure>this is the trace format 1</failure>\n' +
                        '<failure>this is the trace format 2</failure>\n' +
                        '</testcase>';
                    var output = xmlReport.serializer.buildReportOnSpec(spec);
                    expect(output).toEqual(correctOutput);
                });

                it("has correct output from buildReportOnSuite method", function (){
                     var correctOutput = '<testsuite name="empty" errors="0" failures="2" tests="1" time="0" timestamp="'+timeStamp+'"><testcase classname="test" name="AAA" time="0"></testcase></testsuite>';
                     var output = xmlReport.serializer.buildReportOnSuite(suite);
                     expect(output).toEqual(correctOutput);
                 });


                it("has correct output from buildReportOnSuite method with nested Suites", function (){

                    // build second suite

                    // --------------------------------
                    // prepare suite

                    var env = {nextSuiteId: function(){return 10;}};


                    var suite2 = new jasmine.Suite(env, "suite2_description", function(){}, suite);
                    suite2.results = function(){ return {
                            totalCount: 11,
                            failedCount: 22
                        };};
                    suite2.startTime = new Date();
                    suite2.endTime = new Date();
                    suite2.totalCount = 0;
                    suite2.failedCount = 0;
                    suite2.children_ = [];

                    suite.children_.push(suite2);

                    var correctOutput = '<testsuite name="empty" errors="0" failures="2" tests="1" time="0" timestamp="'+timeStamp+'"><testcase classname="test" name="AAA" time="0"></testcase><testsuite name="suite2_description" errors="0" failures="22" tests="11" time="0" timestamp="'+timeStamp+'"></testsuite></testsuite>';
                    var output = xmlReport.serializer.buildReportOnSuite(suite);
                    expect(output).toEqual(correctOutput);
                });

                it("should return valid report from the reportRunnerResults function", function(){

                    // create an array of Suites
                    xmlReport.suitesDictionary = {
                        1: suite,
                        2: suite
                    };

                    var correctOutput = '<?xml version=\'1.0\' encoding=\'UTF-8\' ?>\n' +
                        '<testsuite userAgent=\'' + (navigator ? navigator.userAgent : "") + '\'>\n' +
                    '<testsuite name="empty" errors="0" failures="2" tests="1" time="0" timestamp="'+timeStamp+'"><testcase classname="test" name="AAA" time="0"></testcase></testsuite>\n' +
                    '<testsuite name="empty" errors="0" failures="2" tests="1" time="0" timestamp="'+timeStamp+'"><testcase classname="test" name="AAA" time="0"></testcase></testsuite>\n' +
                    '</testsuite>';

                    xmlReport.reportRunnerResults();

                    expect(jasmine.__junit_xml_output__).toEqual(correctOutput);
                });
            });
        });
    });
});