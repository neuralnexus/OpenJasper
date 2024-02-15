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
 * @version: $Id: RequestBiComponentErrorTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BiComponentError = require("bi/error/BiComponentError"),
        json3 = require("json3"),
        errorCodes = require("bi/error/enum/biComponentErrorCodes"),
        messages = require("bi/error/enum/biComponentErrorMessages"),
        RequestBiComponentError = require("bi/error/RequestBiComponentError");

    describe("RequestBiComponentError tests", function() {
        it("should BiComponentError instance", function(){
            expect(typeof RequestBiComponentError).toBe("function");
            expect(RequestBiComponentError.prototype instanceof BiComponentError).toBeTruthy();
        });

        it("should accept 'xmlHttpRequest' as argument in constructor and call base constructor", function(){
            var xhr = {},
                constructorSpy = sinon.spy(BiComponentError.prototype, "constructor"),
                error = new RequestBiComponentError(xhr);

            expect(error.xmlHttpRequest).toBe(xhr);
            expect(constructorSpy).toHaveBeenCalledWith(
                errorCodes.UNEXPECTED_ERROR,
                messages[errorCodes.UNEXPECTED_ERROR]);

            expect(error.errorCode).toBe(errorCodes.UNEXPECTED_ERROR);
            expect(error.message).toBe(messages[errorCodes.UNEXPECTED_ERROR]);

            constructorSpy.restore();
        });

        it("should determine 401 status code as 'authentication.error'", function() {
            var xhr = { status: 401 },
                constructorSpy = sinon.spy(BiComponentError.prototype, "constructor"),
                error = new RequestBiComponentError(xhr);

            expect(error.xmlHttpRequest).toBe(xhr);
            expect(constructorSpy).toHaveBeenCalledWith(errorCodes.AUTHENTICATION_ERROR, messages[errorCodes.AUTHENTICATION_ERROR]);

            expect(error.errorCode).toBe(errorCodes.AUTHENTICATION_ERROR);
            expect(error.message).toBe(messages[errorCodes.AUTHENTICATION_ERROR]);

            constructorSpy.restore();
        });
        it("should get error code and message from xmlHttpRequest", function() {
            var xhr = {
                    status: 404,
                    responseText: json3.stringify({
                        errorCode: "test.error.code",
                        message: "test error",
                        parameters: ["test parameter"]
                    })
                },
                constructorSpy = sinon.spy(BiComponentError.prototype, "constructor"),
                error = new RequestBiComponentError(xhr);

            expect(error.xmlHttpRequest).toBe(xhr);
            expect(constructorSpy).toHaveBeenCalledWith("test.error.code", "test error");
            expect(error.errorCode).toBe("test.error.code");
            expect(error.parameters).toEqual(["test parameter"]);
            expect(error.message).toBe("test error");

            constructorSpy.restore();
        });
    });
});
