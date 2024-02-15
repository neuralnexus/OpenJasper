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
 * @version: $Id: InputControlStateModelTests.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function (require) {
    "use strict";

    var Backbone = require("backbone"),
        BaseModel = require("common/model/BaseModel"),
        InputControlStateModel = require("inputControl/model/InputControlStateModel"),
        InputControlOptionCollection = require("inputControl/collection/InputControlOptionCollection");

    describe("InputControlStateModel tests", function() {
        it("should be Backbone.Model instance", function(){
            expect(typeof InputControlStateModel).toBe("function");
            expect(InputControlStateModel.prototype instanceof Backbone.Model).toBeTruthy();
        });

        it("should be BaseModel instance", function(){
            expect(InputControlStateModel.prototype instanceof BaseModel).toBeTruthy();
        });

        it("should have default attributes", function() {
            expect(InputControlStateModel.prototype.defaults).toBeDefined();

            expect(InputControlStateModel.prototype.defaults).toEqual({
                id: undefined,
                value: undefined,
                options: undefined,
                uri: undefined,
                error: undefined
            });
        });

        it("should init empty 'options' collection", function() {
            var model = new InputControlStateModel();

            expect(model.options).toBeDefined();
            expect(model.options instanceof InputControlOptionCollection).toBeTruthy();
            expect(model.options.length).toBe(0);
        });

        it("should init 'options' collection from 'options' attribute", function() {
            var model = new InputControlStateModel({
                options: [
                    {
                        selected: true,
                        label: "test",
                        value: "test"
                    }
                ]
            });

            expect(model.options).toBeDefined();
            expect(model.options instanceof InputControlOptionCollection).toBeTruthy();
            expect(model.options.length).toBe(1);
            expect(model.options.at(0).attributes).toEqual({
                selected: true,
                label: "test",
                value: "test"
            });
        });

        it("should reset 'options' collection when 'options' attribute changes", function() {
            var model = new InputControlStateModel(),
                resetSpy = sinon.spy(model.options, "reset");

            model.set("options", [
                {
                    selected: true,
                    label: "test",
                    value: "test"
                }
            ]);

            expect(resetSpy).toHaveBeenCalledWith(model.get("options"));

            resetSpy.restore();
        });
    });
});
