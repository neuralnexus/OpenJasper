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
 * @version: $Id: InputControlModelTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        BaseModel = require("common/model/BaseModel"),
        InputControlModel = require("inputControl/model/InputControlModel"),
        InputControlStateModel = require("inputControl/model/InputControlStateModel");

    var stateAttrs = {
        id: "My_Input_Control",
        value: "My_Input_Control",
        options: [],
        uri: "/test/My_Input_Control",
        error: undefined
    };

    describe("InputControlModel tests", function() {
        it("should be Backbone.Model instance", function(){
            expect(typeof InputControlModel).toBe("function");
            expect(InputControlModel.prototype instanceof Backbone.Model).toBeTruthy();
        });

        it("should be BaseModel instance", function(){
            expect(InputControlModel.prototype instanceof BaseModel).toBeTruthy();
        });

        it("should have default attributes", function() {
            expect(InputControlModel.prototype.defaults).toBeDefined();

            expect(InputControlModel.prototype.defaults).toEqual({
                id: undefined,
                label: undefined,
                mandatory: false,
                readOnly: false,
                type: undefined,
                uri: undefined,
                visible: false,
                masterDependencies: undefined,
                slaveDependencies: undefined,
                validationRules: undefined,
                state: undefined
            });
        });

        it("should init empty 'state' model", function() {
            var model = new InputControlModel();

            expect(model.state).toBeDefined();
            expect(model.state instanceof InputControlStateModel).toBeTruthy();
            expect(model.state.attributes).toEqual({});
        });

        it("should init 'state' model from 'state' attribute", function() {
            var model = new InputControlModel({
                state: stateAttrs
            });

            expect(model.state).toBeDefined();
            expect(model.state instanceof InputControlStateModel).toBeTruthy();
            expect(model.state.attributes).toEqual(stateAttrs);
        });

        it("should update 'state' model when 'state' attribute changes", function() {
            var model = new InputControlModel(),
                setSpy = sinon.spy(model.state, "set");

            model.set("state", stateAttrs);

            expect(setSpy).toHaveBeenCalledWith(model.get("state"));

            setSpy.restore();
        });
    });
});
