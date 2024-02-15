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
 * @version: $Id: controls.components.tests.js 48468 2014-08-21 07:47:20Z yuriy.plakosh $
 */

define(["jquery", "controls.components", "text!templates/controls.htm", "jrs.configs"],
    function (jQuery, Controls, controlsText, jrsConfigs) {

    ControlsBase = {
        NULL_SUBSTITUTION_VALUE : "test_substitution_label",
        NULL_SUBSTITUTION_LABEL : "test_substitution_value",
        NOTHING_SUBSTITUTION_VALUE : "~NOTHING~"
    };

    JRS.i18n = {
        bundledCalendarFormat: "yy-mm-dd",
        bundledCalendarTimeFormat: "hh:mm"
    };

    describe("Control", function() {

        beforeEach(function(){
            setTemplates(controlsText);
        });

        describe("Boolean", function () {

            var boolControl;

            beforeEach(function () {

                boolControl = new Controls.Bool({
                    type:"bool",
                    uri:"/reports/coffeeBar",
                    id:"coffeeBar",
                    label:"Coffee Bar",
                    mandatory:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("Set", function () {
                boolControl.set({values:"true"});
                expect(boolControl.getElem().find(":checked")).toExist();

                boolControl.set({values:"false"});
                expect(boolControl.getElem().find(":checked")).not.toExist();
            });

            it("Get", function () {
                boolControl.set({values:"false"});
                expect(boolControl.get("selection")).toEqual("false");
                expect(boolControl.get("values")).toEqual("false");

            });
        });

        describe("Single Value Text", function () {

            var singleValue;

            beforeEach(function () {
                singleValue = new Controls.SingleValueText({
                    type:"singleValueText",
                    uri:"/reports/name",
                    id:"name",
                    label:"Name",
                    mandatory:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("Set", function () {
                singleValue.getElem();
                singleValue.set({values:"Steve"});
                expect(singleValue.getElem().find("input")).toHaveValue("Steve");
            });

            it("Get", function () {
                singleValue.set({values:"Steve"});
                expect(singleValue.get("selection")).toEqual("Steve");
                expect(singleValue.get("values")).toEqual("Steve");
            });

            it("Handle Substitutions", function(){
                singleValue.set({values:ControlsBase.NULL_SUBSTITUTION_VALUE});

                expect(singleValue.get("selection")).toEqual(ControlsBase.NULL_SUBSTITUTION_VALUE);
                expect(singleValue.get("values")).toEqual(ControlsBase.NULL_SUBSTITUTION_VALUE);

                expect(singleValue.getElem().find("input")).toHaveValue(ControlsBase.NULL_SUBSTITUTION_LABEL);
            });
        });

        describe("Single Value Number", function () {

            var singleValue;

            beforeEach(function () {
                singleValue = new Controls.SingleValueNumber({
                    type:"singleValueNumber",
                    uri:"/reports/order",
                    id:"order",
                    label:"Order",
                    mandatory:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("Set", function () {
                singleValue.getElem();
                singleValue.set({values:"1234"});
                expect(singleValue.getElem().find("input")).toHaveValue("1234");
            });

            it("Get", function () {
                singleValue.set({values:"1234"});
                expect(singleValue.get("selection")).toEqual("1234");
                expect(singleValue.get("values")).toEqual("1234");
            });

        });

        describe("Single Date", function () {

            var singleValueDate,
                originalDateFormat = jrsConfigs.localeSettings.dateFormat;

            beforeEach(function () {
                // change format to some unusual
                jrsConfigs.localeSettings.dateFormat = "dd.MM.yyyy";
                singleValueDate = new Controls.SingleValueDate({
                    type:"singleValueDate",
                    uri:"/reports/birthDate",
                    id:"birthDate",
                    label:"Birth Date",
                    mandatory:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function () {
                // restore original formats
                jrsConfigs.localeSettings.dateFormat = originalDateFormat;
                singleValueDate.getElem().find("input").datepicker("hide");
                singleValueDate.getElem().find("input").datepicker("destroy");
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("Set", function () {
                singleValueDate.set({values:"2014-07-09"});
                expect(singleValueDate.getElem().find("input")).toHaveValue("09.07.2014");
            });

            it("Get", function () {
                singleValueDate.set({values:"1996-01-01"});
                expect(singleValueDate.get("selection")).toEqual("1996-01-01");
                expect(singleValueDate.get("values")).toEqual("1996-01-01");
            });

            // TODO: restore after doing proper test isolation
            xit("Open calendar", function(){
                singleValueDate.getElem().next().simulate('click');
                expect(jQuery("#ui-datepicker-div-1-10-4")).toBeVisible();
            });

            it("Respond to calendar (day)", function(){
                var setStub = sinon.stub(singleValueDate, "set");

                jQuery("#jasmine-fixtures").append(singleValueDate.getElem());
                singleValueDate.getElem().find("input").datepicker("show");

                var div = jQuery("#ui-datepicker-div-1-10-4");
                var input = div.find(".ui-datepicker-week-end");
                input.simulate("click");

                expect(setStub).toHaveBeenCalled();
                singleValueDate.set.restore();
            });

            it("Respond to calendar (month&year, not changed)", function(){
                jQuery("#jasmine-fixtures").append(singleValueDate.getElem());
                singleValueDate.getElem().find("input").datepicker("show");

                var div = jQuery("#ui-datepicker-div-1-10-4");

                var input = div.find(".ui-datepicker-week-end");
                input.simulate("click");

                var setStub = sinon.stub(singleValueDate, "set");

                input = div.find(".ui-datepicker-year");
                input.trigger("change");

                expect(setStub).not.toHaveBeenCalled();

                singleValueDate.set.restore();
            });

            it("Value in date control should be converted to upper case and all spaces should be removed on change event", function(){
                jQuery("#jasmine-fixtures").append(singleValueDate.getElem());
                singleValueDate.getElem().find("input").datepicker("show");

                var input = singleValueDate.getElem().find("input");
                jQuery(input).val("day + 1");
                input.trigger("change");

                expect(jQuery(input)).toHaveValue("DAY+1");
            });

            it("workaround to remove the datepicker element from the page at the ends of its tests", function () {
                jQuery("#ui-datepicker-div-1-10-4").remove();
            });

        });

        describe("Single Datetime", function(){

            var singleValueDate,
                originalDateFormat = jrsConfigs.localeSettings.dateFormat,
                originalTimeFormat = jrsConfigs.localeSettings.timeFormat,
                originalTimestampSeparator = jrsConfigs.localeSettings.timestampSeparator;

            beforeEach(function(){
                // change format to some unusual
                jrsConfigs.localeSettings.dateFormat = "dd.MM.yyyy";
                jrsConfigs.localeSettings.timeFormat = "HH-mm-ss";
                jrsConfigs.localeSettings.timestampSeparator = "~";
                singleValueDate = new Controls.SingleValueDatetime({
                    type:"singleValueDate",
                    uri:"/reports/birthDate",
                    id:"birthDate",
                    label:"Birth Date",
                    mandatory:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // restore original formats
                jrsConfigs.localeSettings.dateFormat = originalDateFormat;
                jrsConfigs.localeSettings.timeFormat = originalTimeFormat;
                jrsConfigs.localeSettings.timestampSeparator = originalTimestampSeparator;
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });
            it("Set", function () {
                singleValueDate.set({values:"2014-07-09T11:01:55"});
                expect(singleValueDate.getElem().find("input")).toHaveValue("09.07.2014~11-01-55");
            });

            it("Get", function () {
                singleValueDate.set({values:"2014-07-09T11:01:55"});
                expect(singleValueDate.get("selection")).toEqual("2014-07-09T11:01:55");
                expect(singleValueDate.get("values")).toEqual("2014-07-09T11:01:55");
            });

            it("Value in dateTime control should be converted to upper case and all spaces should be removed on change event", function(){
                setTemplates(singleValueDate.getElem());

                var input = singleValueDate.getElem().find("input");
                jQuery(input).val("day + 1");
                input.trigger("change");

                expect(jQuery(input)).toHaveValue("DAY+1");
            });

        });

        describe("Single Time", function(){

            var singleValueTime,
                originalTimeFormat = jrsConfigs.localeSettings.timeFormat;

            beforeEach(function(){
                // change format to some unusual
                jrsConfigs.localeSettings.timeFormat = "HH-mm-ss";
                singleValueTime = new Controls.SingleValueTime({
                    type:"singleValueDatetime",
                    uri:"/reports/birthDate",
                    id:"birthDate",
                    label:"Birth Date",
                    mandatory:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // restore original formats
                jrsConfigs.localeSettings.timeFormat = originalTimeFormat;
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });
            it("Set", function () {
                singleValueTime.set({values:"11:01:55"});
                expect(singleValueTime.getElem().find("input")).toHaveValue("11-01-55");
            });

            it("Get", function () {
                singleValueTime.set({values:"11:01:55"});
                expect(singleValueTime.get("selection")).toEqual("11:01:55");
                expect(singleValueTime.get("values")).toEqual("11:01:55");
            });

        });

        describe("Single Select", function () {

            var singleSelect, controlData = [
                {value:"DF", label:"Mexico | CA"},
                {value:"Guerrero", label:"Mexico | Guerrero", selected:"true"},
                {value:"Jalisco", label:"Mexico | Jalisco"},
                {value:"Veracruz", label:"Mexico | Veracruz"}
            ];

            beforeEach(function () {
                singleSelect = new Controls.SingleSelect({
                    type:"singleSelect",
                    uri:"/reports/country",
                    id:"country",
                    label:"Country",
                    mandatory:"true",
                    readOnly:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
                jQuery("body").find(".sSelect-dropDown").remove();
                jQuery("body").find(".sListDropdown").remove();
            });

            it("Set", function () {
                singleSelect.set({values:controlData});

                var list = jQuery("body").find(".sSelect-dropDown");
                expect(list.find("li").length).toEqual(4);
                expect(list.find("li.selected").size()).toEqual(1);
                expect(list.find("li.selected").attr("data-index")).toEqual("1");
            });

            it("Get", function () {
                singleSelect.set({values:controlData});
                expect(singleSelect.get("selection")).toEqual("Guerrero");
                expect(singleSelect.get("values")).toEqual(controlData);
            });
        });

        describe("Multiple Select", function () {

            var multiSelect, controlData = [
                {value:"DF", label:"Mexico | CA"},
                {value:"Guerrero", label:"Mexico | Guerrero", selected:"true"},
                {value:"Jalisco", label:"Mexico | Jalisco"},
                {value:"Sinaloa", label:"Mexico | Sinaloa", selected:"true"},
                {value:"Veracruz", label:"Mexico | Veracruz"}
            ];

            var controlDataNone = [
                {value:"DF", label:"Mexico | CA"},
                {value:"Guerrero", label:"Mexico | Guerrero"},
                {value:"Jalisco", label:"Mexico | Jalisco"},
                {value:"Sinaloa", label:"Mexico | Sinaloa"},
                {value:"Veracruz", label:"Mexico | Veracruz"}
            ];

            beforeEach(function () {
                multiSelect = new Controls.MultiSelect({
                    type:"multiSelect",
                    uri:"/reports/state",
                    id:"state",
                    label:"State",
                    mandatory:"true",
                    readOnly:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
                jQuery("body").find(".mSelect-dropDown").remove();
            });

            it("Set", function () {
                multiSelect.set({values:controlData});

                var list = jQuery("body").find(".mSelect-dropDown");

                expect(list.find("li").length).toEqual(5);
                expect(list.find("li[data-index='0']")).not.toHaveClass("selected");
                expect(list.find("li[data-index='1']")).toHaveClass("selected");
                expect(list.find("li[data-index='2']")).not.toHaveClass("selected");
                expect(list.find("li[data-index='3']")).toHaveClass("selected");
                expect(list.find("li[data-index='4']")).not.toHaveClass("selected");
            });

            it("Get", function () {
                multiSelect.set({values:controlData});
                expect(multiSelect.get("values")).toEqual(controlData);
                expect(multiSelect.get("selection")).toEqual(["Guerrero", "Sinaloa"]);
            });

            it("Get none", function () {
                multiSelect.set({values:controlDataNone});
                expect(multiSelect.get("values")).toEqual(controlDataNone);
                expect(multiSelect.get("selection")).toEqual([ControlsBase.NOTHING_SUBSTITUTION_VALUE]);
            });

            it("can be readOnly", function () {
                expect(multiSelect.getElem().find("div[disabled]").size()).toEqual(1);
            });
        });

        describe("Single Select Radio", function () {

            var singleSelectRadio, controlData = [
                {value:"Canada", label:"Canada"},
                {value:"Mexico", label:"Mexico"},
                {value:"USA", label:"USA", selected:"true"}
            ];

            beforeEach(function () {
                singleSelectRadio = new Controls.SingleSelectRadio({
                    type:"singleSelectRadio",
                    uri:"/reports/countryRadio",
                    id:"countryRadio",
                    label:"Country",
                    mandatory:"true",
                    readOnly:"true",
                    validators:[],
                    visible: true
                });

            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("Set", function () {

                singleSelectRadio.set({values:controlData});

                expect(singleSelectRadio.getElem().find("input").length).toEqual(3);
                expect(singleSelectRadio.getElem().find("input[value=Canada]")).not.toBeChecked("checked");
                expect(singleSelectRadio.getElem().find("input[value=Mexico]")).not.toBeChecked("checked");
                expect(singleSelectRadio.getElem().find("input[value=USA]")).toBeChecked("checked");
            });

            it("Get", function () {
                singleSelectRadio.set({values:controlData});
                expect(singleSelectRadio.get('selection')).toEqual("USA");
                expect(singleSelectRadio.get('values')).toEqual(controlData);
            });

            it("can be readOnly", function () {
                singleSelectRadio.set({values:controlData});
                expect(singleSelectRadio.getElem().find("input[disabled]").length).toEqual(3);
            });
        });

        describe("Multiple Checkbox", function () {

            var multipleCheckbox, controlData = [
                {value:"DF", label:"Mexico | CA"},
                {value:"Guerrero", label:"Mexico | Guerrero", selected:"true"},
                {value:"Jalisco", label:"Mexico | Jalisco"},
                {value:"Sinaloa", label:"Mexico | Sinaloa", selected:"true"},
                {value:"Veracruz", label:"Mexico | Veracruz"}
            ];

            var controlDataNone = [
                {value:"DF", label:"Mexico | CA"},
                {value:"Guerrero", label:"Mexico | Guerrero"},
                {value:"Jalisco", label:"Mexico | Jalisco"},
                {value:"Sinaloa", label:"Mexico | Sinaloa"},
                {value:"Veracruz", label:"Mexico | Veracruz"}
            ];

            beforeEach(function () {
                multipleCheckbox = new Controls.MultiSelectCheckbox({
                    type:"multiSelectCheckbox",
                    uri:"/reports/stateCheckbox",
                    id:"stateCheckbox",
                    label:"State",
                    mandatory:"true",
                    readOnly:"true",
                    validators:[],
                    visible: true
                });
            });

            afterEach(function(){
                // remove all listeners which were set by Controller initializer
                jQuery(document).unbind();
            });

            it("Set", function () {
                multipleCheckbox.set({values:controlData});

                //Check view
                expect(multipleCheckbox.getElem().find("input").length).toEqual(5);
                expect(multipleCheckbox.getElem().find("input[value=DF]")).not.toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Guerrero]")).toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Jalisco]")).not.toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Sinaloa]")).toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Veracruz]")).not.toBeChecked("checked");
            });

            it("Get", function () {
                multipleCheckbox.set({values:controlData});
                expect(multipleCheckbox.get('selection')).toEqual(["Guerrero", "Sinaloa"]);
                expect(multipleCheckbox.get('values')).toEqual(controlData);
            });

            it("Get none", function () {
                multipleCheckbox.set({values:controlDataNone});
                expect(multipleCheckbox.get("values")).toEqual(controlDataNone);
                expect(multipleCheckbox.get("selection")).toEqual([ControlsBase.NOTHING_SUBSTITUTION_VALUE]);
            });

            it("can be readOnly", function () {
                multipleCheckbox.set({values:controlData});
                expect(multipleCheckbox.getElem().find("input[disabled]").length).toEqual(5);
            });
        });
    });
});
