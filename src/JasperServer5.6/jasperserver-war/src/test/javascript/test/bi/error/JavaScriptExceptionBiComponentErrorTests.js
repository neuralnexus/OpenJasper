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
 * @version: $Id: JavaScriptExceptionBiComponentErrorTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var BiComponentError = require("bi/error/BiComponentError"),
        errorCodes = require("bi/error/enum/biComponentErrorCodes"),
        messages = require("bi/error/enum/biComponentErrorMessages"),
        JavaScriptExceptionBiComponentError = require("bi/error/JavaScriptExceptionBiComponentError");

    describe("JavaScriptExceptionBiComponentError tests", function() {
        it("should BiComponentError instance", function(){
            expect(typeof JavaScriptExceptionBiComponentError).toBe("function");
            expect(JavaScriptExceptionBiComponentError.prototype instanceof BiComponentError).toBeTruthy();
        });

        it("should accept 'exception' as argument in constructor and call base constructor", function(){
            var ex = new Error("test exception"),
                constructorSpy = sinon.spy(BiComponentError.prototype, "constructor"),
                error = new JavaScriptExceptionBiComponentError(ex);

            expect(error.exception).toBe(ex);
            expect(constructorSpy).toHaveBeenCalledWith(
                errorCodes.UNEXPECTED_ERROR,
                messages[errorCodes.UNEXPECTED_ERROR] + " : " + ex.toString());

            expect(error.errorCode).toBe(errorCodes.UNEXPECTED_ERROR);
            expect(error.message).toBe(messages[errorCodes.UNEXPECTED_ERROR] + " : " + ex.toString());

            constructorSpy.restore();
        });
    });
});
