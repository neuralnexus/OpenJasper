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

import $ from 'jquery';
import AjaxUploader from 'src/common/transport/AjaxFormSubmitter';
/**
 * @version: $Id: components.ajaxuploader.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

/* jshint multistr: true */

describe("ajax uploader tests", function() {
    var form, uploader;

    beforeEach(function() {
        form = $('<form id="uploadTestTarget"></form>');
        uploader = new AjaxUploader(form);

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