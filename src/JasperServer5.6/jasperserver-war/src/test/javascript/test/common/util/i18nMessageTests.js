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
 * @version: $Id: i18nMessageTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var i18nMessage = require("common/util/i18nMessage");

    describe("i18nMessage", function() {
        it("should have 'extend' static method", function() {
            expect(typeof i18nMessage.extend).toBe("function");
        });

        it("should have 'bundle' property as empty object", function() {
            expect(i18nMessage.prototype.bundle).toEqual({});
        });

        it("should accept 'code' as first argument and store other arguments in 'args' array", function() {
            var msg = new i18nMessage("message.code", "arg1", 2);

            expect(msg.code).toBe("message.code");
            expect(_.isArray(msg.args)).toBeTruthy();
            expect(msg.args.length).toBe(2);
            expect(msg.args[0]).toBe("arg1");
            expect(msg.args[1]).toBe(2);
        });

        it("should have overridden 'toString' method", function() {
            expect(typeof i18nMessage.prototype.toString).toBe("function");
        });

        it("should return code itself when code was not found in bundle when calling 'toString' method", function() {
            var msg = new i18nMessage("message.code", "arg1", 2);

            expect(msg.toString()).toBe("message.code");
        });

        it("should return message from bundle found by code when calling 'toString' method", function() {
            var MessageWithBundle = i18nMessage.extend({ bundle: { "message.code": "Test message is here" }}),
                msg = new MessageWithBundle("message.code", "arg1", 2);

            expect(msg.toString()).toBe("Test message is here");
        });

        it("should replace placeholders in message from bundle found by code when calling 'toString' method", function() {
            var MessageWithBundle = i18nMessage.extend({ bundle: { "message.code": "Test message {0} is here with {1}" }}),
                msg = new MessageWithBundle("message.code", "arg1", 2);

            expect(msg.toString()).toBe("Test message arg1 is here with 2");
        });
    });
});