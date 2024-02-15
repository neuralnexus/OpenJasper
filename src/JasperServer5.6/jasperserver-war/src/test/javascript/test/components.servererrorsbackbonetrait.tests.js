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
 * @version: $Id: components.servererrorsbackbonetrait.tests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(["underscore",
        "components.servererrorsbackbonetrait",
        "json3"],
    function (_, ServerBackboneTrait, JSON) {

    var mocks = {

        serializableError : {
            message: "export.session.expired",
            errorCode: "error.unexpected"
        },

        badRequestError : {
            message: "export.session.expired",
            errorCode: "unserializable.error"
        }
    };

    describe("ServerErrorsBackboneTrait", function () {

        var serverErrorsTrait;

        beforeEach(function(){
            serverErrorsTrait = _.clone(ServerBackboneTrait);
        });

        it("can parse server error's responces", function(){

            var result = serverErrorsTrait.parseServerError({
                statusText:"Bad Request",
                responseText: JSON.stringify(mocks.serializableError),
                status: 400
            });

            expect(result).toEqual(mocks.serializableError);

            result = serverErrorsTrait.parseServerError({
                statusText: "Status Text",
                responseText: "<html><body>non json content</body></html>",
                status: 400
            });

            expect(result).toEqual({
                message: "Status Text",
                errorCode: "unserializable.error"
            });
        });

        it("delegate server error", function(){

            var error = {},
                triggerSpy = sinon.spy();

            serverErrorsTrait.trigger = triggerSpy;
            sinon.stub(serverErrorsTrait, "parseServerError").returns(error);
            serverErrorsTrait.defaultErrorDelegator({}, {});
            expect(triggerSpy).toHaveBeenCalledWith("error:server",error);
            serverErrorsTrait.parseServerError.restore();
        });

        it("serialize error with status text message if no such status in mapping", function(){

            var xhrMock = {status: 400, statusText: "Http status message"};
            expect(serverErrorsTrait.mapUnserializableErrors(xhrMock)).toEqual({
                    message:"Http status message",
                    errorCode:"unserializable.error"
            })
        });

        it("serialize error with status text message from statuses mapping", function(){
            serverErrorsTrait.statuses[403] = "Test message";
            var xhrMock = {status: 403, statusText: "Http status message"};
            expect(serverErrorsTrait.mapUnserializableErrors(xhrMock)).toEqual({
                message:"Test message",
                errorCode:"unserializable.error"
            })
        });
    });
});