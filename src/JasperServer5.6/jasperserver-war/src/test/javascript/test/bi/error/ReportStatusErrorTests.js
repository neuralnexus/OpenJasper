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
 * @author: Kostiantyn Tsaregradskyi
 * @version: $Id: ReportStatusErrorTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BiComponentError = require("bi/error/BiComponentError"),
        errorCodes = require("bi/error/enum/biComponentErrorCodes"),
        messages = require("bi/error/enum/biComponentErrorMessages"),
        ReportStatusError = require("bi/error/ReportStatusError");

    describe("ReportStatusError tests", function() {
        it("should ReportStatusError instance", function(){
            expect(typeof ReportStatusError).toBe("function");
            expect(ReportStatusError.prototype instanceof BiComponentError).toBeTruthy();
        });

        it("should object as argument in constructor, determine type of error and call base constructor", function(){
            var constructorSpy = sinon.spy(BiComponentError.prototype, "constructor"),
                error = new ReportStatusError({ status: "failed", source: "execution"});

            expect(error.status).toEqual("failed");
            expect(error.source).toEqual("execution");
            expect(constructorSpy).toHaveBeenCalledWith(
                errorCodes.REPORT_EXECUTION_FAILED,
                messages[errorCodes.REPORT_EXECUTION_FAILED]);

            expect(error.errorCode).toBe(errorCodes.REPORT_EXECUTION_FAILED);
            expect(error.message).toBe(messages[errorCodes.REPORT_EXECUTION_FAILED]);

            constructorSpy.reset();

            error = new ReportStatusError({ status: "failed", source: "export", format: "html" });

            expect(error.status).toEqual("failed");
            expect(error.source).toEqual("export");
            expect(error.format).toEqual("html");
            expect(constructorSpy).toHaveBeenCalledWith(
                errorCodes.REPORT_EXPORT_FAILED,
                messages[errorCodes.REPORT_EXPORT_FAILED] + " : " + "format - 'html'");

            expect(error.errorCode).toBe(errorCodes.REPORT_EXPORT_FAILED);
            expect(error.message).toBe(messages[errorCodes.REPORT_EXPORT_FAILED] + " : " + "format - 'html'");


            constructorSpy.reset();

            error = new ReportStatusError(
                {
                    status: "failed",
                    source: "export",
                    format: "html",
                    errorDescriptor:{errorCode:"test.error.code", message: "test error message", parameters: ["test parameter"]}
                });

            expect(error.status).toEqual("failed");
            expect(error.source).toEqual("export");
            expect(error.format).toEqual("html");
            expect(constructorSpy).toHaveBeenCalledWith("test.error.code", "test error message", ["test parameter"]);

            expect(error.errorCode).toBe("test.error.code");
            expect(error.message).toBe("test error message");
            expect(error.parameters).toEqual(["test parameter"]);


            constructorSpy.restore();
        });
    });
});
