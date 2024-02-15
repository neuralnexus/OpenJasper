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
 * @version: $Id: components.ajaxuploader.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["jquery", "components.ajaxuploader", "text!templates/components.htm"],
    function($, AjaxUploader, componentsText) {
    describe("ajax uploader tests", function() {
        var form, uploader;

        beforeEach(function() {
            setTemplates('<form id="uploadTestTarget"></form>', componentsText);
            form = $("#uploadTestTarget");
            uploader = new AjaxUploader(form, function(){});

        });

        describe("dom", function() {
            it("should add iframe to the form", function() {
                expect(form.find("iframe").length).toEqual(1);
                expect(uploader.iframe).toBeDefined();
            });

            it("should set target to iframe", function() {
                expect(uploader.name).toBeDefined();
                expect(form.attr("target")).toEqual(uploader.name);
                expect(form.find("iframe").attr("name")).toEqual(uploader.name);
            });

        });

        describe("timout functionality", function() {

            var clock, clearIntervalSpy, setIntervalSpy;

            beforeEach(function(){
                clock = sinon.useFakeTimers();
                uploader = new AjaxUploader(form, function(){}, 10000);
                clearIntervalSpy = sinon.spy(window, "clearInterval");
                setIntervalSpy = sinon.spy(window, "setInterval");
            });

            afterEach(function(){
                clock.restore();
                clearIntervalSpy.restore();
                setIntervalSpy.restore();

            });

            it("should save timeout value on init", function(){
                expect(uploader.timeout).toEqual(10000);
            });

            it("should start and clear time interval", function(){
                var spyCallback = sinon.spy();
                uploader.startTimeoutLookup(spyCallback);
                expect(setIntervalSpy).toHaveBeenCalled();
                clock.tick(uploader.timeout);
                expect(clearIntervalSpy).toHaveBeenCalled();
            });

            it("should invoke callback on timeout ", function() {
                var spyCallback = sinon.spy();
                uploader.startTimeoutLookup(spyCallback);
                clock.tick(1000);
                expect(spyCallback).not.toHaveBeenCalled();
                clock.tick(uploader.timeout);
                expect(spyCallback).toHaveBeenCalledWith({errorCode: "error.timeout"});
            });

            it("should invoke callback on timeout ", function() {
                var spyCallback = sinon.spy();
                uploader.startTimeoutLookup(spyCallback);
                clock.tick(1000);
                expect(uploader.isTimeout()).toBeFalsy();
                clock.tick(uploader.timeout);
                expect(uploader.isTimeout()).toBeTruthy();
            });
        });

        describe("document translation", function(){

            it("should parse document", function(){
                var doc = $.parseXML("<state><phase>phase</phase><message>message</message></state>");
                var expected = {
                    phase:"phase",
                    message: "message"
                };

                var result = uploader.parceXmlDocToObject(doc);

                expect(result).toBeDefined();
                expect(result.phase).toEqual(expected.phase);
                expect(result.message).toEqual(expected.message);
            });

            it("should parse complex document", function(){
                var doc = $.parseXML('<?xml version="1.0" encoding="UTF-8"?>   \
                 <errorDescriptor>                                          \
                    <errorCode>resource.not.found</errorCode>                  \
                    <message>Resource kk not found.</message>                  \
                    <parameters>                                                   \
                      <parameter>kk</parameter>                                  \
                    </parameters>                                                  \
                </errorDescriptor>                                             \
                ');
                var expected = {
                    errorCode: "resource.not.found",
                    message: "Resource kk not found.",
                    parameters: {
                        parameter: "kk"
                    }
                };

                var result = uploader.parceXmlDocToObject(doc);

                expect(result).toBeDefined();
                expect(result.errorCode).toEqual(expected.errorCode);
                expect(result.message).toEqual(expected.message);
                expect(result.parameters.parameter).toEqual(expected.parameters.parameter);
            });

            it("should parse complex with arrays document", function(){
                var doc = $.parseXML('<?xml version="1.0" encoding="UTF-8"?>   \
                 <errorDescriptor>                                          \
                    <errorCode>resource.not.found</errorCode>                  \
                    <message>Resource kk not found.</message>                  \
                    <parameters>                                                   \
                      <parameter>kk</parameter>                                  \
                      <parameter>kkaa</parameter>                                  \
                    </parameters>                                                  \
                </errorDescriptor>                                             \
                ');
                var expected = {
                    errorCode:"resource.not.found",
                    message: "Resource kk not found.",
                    parameters: ["kk","kkaa"]

                };

                var result = uploader.parceXmlDocToObject(doc);

                expect(result).toBeDefined();
                expect(result.errorCode).toEqual(expected.errorCode);
                expect(result.message).toEqual(expected.message);
                expect(result.parameters.length).toEqual(expected.parameters.length);
                expect(result.parameters[0].parameter).toEqual(expected.parameters[0].parameter);
                expect(result.parameters[1].parameter).toEqual(expected.parameters[1].parameter);
            });

            it("should respond on HTML document", function(){
                var doc = document;
                var expected = {
                    errorCode:"unexpected.error"
                };

                var result = uploader.parceXmlDocToObject(doc);

                expect(result).toBeDefined();
                expect(result.errorCode).toEqual(expected.errorCode);
            });
        });

    });

});
