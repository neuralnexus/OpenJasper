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
import sinon from 'sinon';
import DateAndTimePicker from 'src/components/dateAndTime/DateAndTimePicker';
import Backbone from 'backbone';
import $ from 'jquery';

describe("DataAndTimePicker", function () {

    var sandbox, dataTimePicker;

    beforeEach(function () {
        sandbox = sinon.createSandbox();
    });

    afterEach(function () {
        sandbox.restore();
    });

    describe("Helper Methods", function () {
        var input, inst, body = $("body"), container;

        beforeEach(function () {
            input = document.createElement("input");
            input.className = ("#date-inline");
            $(input).css("width", "150");
            inst = {dpDiv: $("<div class='temp'></div>")};
            body.append(input)
        });

        afterEach(function () {
            $(input).remove();
            inst = {};
        });

        it("Should have static methods", function () {
            expect(typeof DateAndTimePicker.Helpers.fixPopupPositionAndStyling).toBe("function");
        });

        it("Should changed css", function () {
            expect(inst.dpDiv.css("marginLeft")).toBe("");

            DateAndTimePicker.Helpers.movePickerRelativelyToTriggerIcon(input, inst);

            expect(parseInt(inst.dpDiv.css("marginLeft").replace("px", ""))).toBeGreaterThan(150);

        });

        it("Should add popup wrapper if container in body", function () {
            body.append(inst.dpDiv);

            expect(body.children(".temp").length).toBe(1);

            DateAndTimePicker.Helpers.stylePopupContainer(input, inst);

            expect(body.children(".temp").length).toBe(0);
            expect(body.children(".jr-jDatepickerPopupContainer").children(".temp").length).toBe(1);

            $(".jr-jDatepickerPopupContainer").remove();
        });

        it("Should add container without wrapper if container not in body", function () {
            container = document.createElement("div");
            container.className = ("container");

            $(container).append(inst.dpDiv);
            body.append(container);

            expect(body.children(".temp").length).toBe(0);
            expect(body.children(".container").children(".temp").length).toBe(1);

            DateAndTimePicker.Helpers.stylePopupContainer(input, inst);

            expect(body.children(".temp").length).toBe(0);
            expect(body.children(".container").children(".temp").length).toBe(1);

            $(container).remove();
        });

        it("Should assign beforeShow method to provided object", function () {
            var originalBeforeShow = function () {
            };
            var options = {el: "#date-inline", beforeShow: originalBeforeShow};

            var picker = new DateAndTimePicker(options);
            var stub = sandbox.stub(options, "beforeShow");

            options.beforeShow.apply(picker, [1]);

            expect(stub).toHaveBeenCalledWith(1);

            expect(picker.pickerOptions.beforeShow).not.toEqual(originalBeforeShow);

            picker.remove();
        });

        it("Should assign afterInject method to provided object", function () {
            var originalAfterInject = function () {
            };
            var options = {el: "#date-inline", afterInject: originalAfterInject};

            var picker = new DateAndTimePicker(options);
            var stub = sandbox.stub(options, "afterInject");

            options.afterInject.apply(picker, [1]);

            expect(stub).toHaveBeenCalledWith(1);

            expect(picker.pickerOptions.afterInject).not.toEqual(originalAfterInject);

            picker.remove();
        });


        describe("Discover Picker Type", function () {

            var discoverPickerType = DateAndTimePicker.Helpers.discoverPickerType;

            it("should return 'datetimepicker' by default", function () {
                expect(discoverPickerType({})).toEqual("datetimepicker");
            });

            it("should return 'datetimepicker' if both 'dateFormat' and 'timeFormat' were passed", function () {
                expect(discoverPickerType({
                    dateFormat: "mm/dd/yy",
                    timeFormat: "HH:mm"
                })).toEqual("datetimepicker");
            });

            it("should return 'datepicker' if only'dateFormat' was passed", function () {
                expect(discoverPickerType({
                    dateFormat: "mm/dd/yy"
                })).toEqual("datepicker");
            });

            it("should return 'datepicker' if only'dateFormat' was passed", function () {
                expect(discoverPickerType({
                    timeFormat: "HH:mm"
                })).toEqual("timepicker");
            });

        });

    });

    describe("Component", function () {

        beforeEach(function () {
            dataTimePicker = new DateAndTimePicker({
                el: "#date-input-1"
            });
        });

        afterEach(function () {
            dataTimePicker && dataTimePicker.remove();
        });

        it("should be Backbone.View instance", function () {
            expect(typeof DateAndTimePicker).toBe("function");
            expect(DateAndTimePicker.prototype instanceof Backbone.View).toBeTruthy();
        });

        it("should have public API", function () {
            expect(dataTimePicker.show).toBeDefined();
            expect(dataTimePicker.hide).toBeDefined();
        });

        it("should called 'show' method", function () {
            var stub = sandbox.stub(dataTimePicker, "_callPickerAction");

            dataTimePicker.show();
            expect(stub).toHaveBeenCalledWith("show");
        });

        it("should called 'hide' method", function () {
            var stub = sandbox.stub(dataTimePicker, "_callPickerAction");

            dataTimePicker.hide();
            expect(stub).toHaveBeenCalledWith("hide");
        });

        it("'remove' method should destroy datepicker", function () {
            var stub = sandbox.stub(dataTimePicker, "_callPickerAction");

            dataTimePicker.remove();
            expect(stub).toHaveBeenCalledWith("destroy");
            dataTimePicker = null;
        });

        it("'remove' method should call Backbone.View remove", function () {
            var dataTimePicker = new DateAndTimePicker({});

            var stub = sandbox.stub(Backbone.View.prototype, "remove");

            dataTimePicker.remove();
            expect(stub).toHaveBeenCalled();
            dataTimePicker = null;
        });

        it("DateAndTimePicker should have initialize method", function () {
            expect(DateAndTimePicker.prototype.initialize).toBeDefined();
            expect(typeof DateAndTimePicker.prototype.initialize).toEqual("function");
        });

    });

    describe("Logging", function () {
        var log;

        beforeEach(function () {
            log = {
                debug: sandbox.stub()
            };

            dataTimePicker = new DateAndTimePicker({
                el: $("<input type='text'/>")[0],
                dateFormat: "mm/dd/yy",
                log: log
            });
        });

        afterEach(function () {
            dataTimePicker && dataTimePicker.remove();
        });

        it("should log debug message because can't parse provided time as a string", function () {
            dataTimePicker.setDate("11/30/2005");

            expect(log.debug).not.toHaveBeenCalled();
        });

        it("should log debug message because can't parse provided time as a string", function () {
            dataTimePicker.setDate("Nov 30, 2005, 19:19:03");

            expect(log.debug).toHaveBeenCalled();
        });


    });

    describe("jQuery Delegations", function () {

        it("should call jquery `datetimepicker` by default", function () {
            var pickerSpy = sandbox.spy($.fn, "datetimepicker");
            var picker = new DateAndTimePicker({
                el: $("<input type='text'/>")[0]
            });

            expect(pickerSpy).toHaveBeenCalled();

            picker.remove();
        });

        it("should call jquery `timepicker` if only 'timeFormat` specified'", function () {
            var pickerSpy = sandbox.spy($.fn, "timepicker");
            var picker = new DateAndTimePicker({
                el: $("<input type='text'/>")[0],
                timeFormat: "HH:mm"
            });

            expect(pickerSpy).toHaveBeenCalled();

            picker.remove();
        });

        it("should call jquery `datepicker` if only 'dateFormat` specified'", function () {
            var pickerSpy = sandbox.spy($.fn, "datepicker");
            var picker = new DateAndTimePicker({
                el: $("<input type='text'/>")[0],
                dateFormat: "yy/mm/dd"
            });

            expect(pickerSpy).toHaveBeenCalled();

            picker.remove();
        });

        it("should call jquery `datetimepicker` if both  'dateFormat` and `timeFormat` specified'", function () {
            var pickerSpy = sandbox.spy($.fn, "datetimepicker");
            var picker = new DateAndTimePicker({
                el: $("<input type='text'/>")[0],
                dateFormat: "yy/mm/dd",
                timeFormat: "HH:mm"
            });

            expect(pickerSpy).toHaveBeenCalled();

            picker.remove();
        });

    });
});