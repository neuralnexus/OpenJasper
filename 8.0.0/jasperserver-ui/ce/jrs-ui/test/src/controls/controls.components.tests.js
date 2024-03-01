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

import jQuery from 'jquery';
import Controls from 'src/controls/controls.components';
import controlsText from 'src/controls/test/template/controls.htm';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import {ControlsBase} from 'src/controls/controls.base';
import setTemplates from 'js-sdk/test/tools/setTemplates';
import {JRS} from 'src/namespace/namespace';
import sinon from 'sinon';
import RestParamsEnum from 'src/controls/rest/enum/restParamsEnum';

import { rewire$showErrorPopup, restore } from 'src/core/core.ajax.utils';

var OriginalControlsBase = ControlsBase;
JRS.i18n = {
    bundledCalendarFormat: 'yy-mm-dd',
    bundledCalendarTimeFormat: 'hh:mm'
};
describe("Control", function() {
    let sandbox;

    beforeEach(function() {
        sandbox = sinon.createSandbox();

        ControlsBase.NULL_SUBSTITUTION_VALUE = "test_substitution_label";
        ControlsBase.NULL_SUBSTITUTION_LABEL = "test_substitution_value";
        ControlsBase.NOTHING_SUBSTITUTION_VALUE = "~NOTHING~";

        setTemplates(controlsText);
    });

    afterEach(function() {
        ControlsBase.NULL_SUBSTITUTION_VALUE = OriginalControlsBase.NULL_SUBSTITUTION_VALUE;
        ControlsBase.NULL_SUBSTITUTION_LABEL = OriginalControlsBase.NULL_SUBSTITUTION_LABEL;
        ControlsBase.NOTHING_SUBSTITUTION_VALUE = OriginalControlsBase.NOTHING_SUBSTITUTION_VALUE;

        sandbox.restore();
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
                visible: true,
                readOnly: false
            });
        });

        afterEach(function(){
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
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
                visible: true,
                readOnly: false
            });
        });

        afterEach(function(){
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
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
                visible: true,
                readOnly: false
            });
        });

        afterEach(function(){
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
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
            jrsConfigs.localeSettings.dateFormat = "dd.mm.yy";
            singleValueDate = new Controls.SingleValueDate({
                type:"singleValueDate",
                uri:"/reports/birthDate",
                id:"birthDate",
                label:"Birth Date",
                mandatory:"true",
                validators:[],
                visible: true,
                readOnly: false
            });
        });

        afterEach(function () {
            // restore original formats
            jrsConfigs.localeSettings.dateFormat = originalDateFormat;
            singleValueDate.getElem().find("input").datepicker("hide");
            singleValueDate.getElem().find("input").datepicker("destroy");
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
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
        // eslint-disable-next-line no-undef
        xit("Open calendar", function(){
            singleValueDate.getElem().next().simulate('click');
            expect(jQuery(".jr-jDatepickerPopupContainer")).toBeVisible();
        });

        it("Respond to calendar (day)", function(){
            var setStub = sinon.stub(singleValueDate, "set");

            jQuery("#jasmine-fixtures").append(singleValueDate.getElem());
            singleValueDate.getElem().find("input").datepicker("show");

            var div = jQuery(".jr-jDatepickerPopupContainer");
            var input = div.find(".ui-datepicker-week-end");
            input.simulate("click");

            expect(setStub).toHaveBeenCalled();
            singleValueDate.set.restore();
        });

        it("Respond to calendar (month&year, not changed)", function(){
            jQuery("#jasmine-fixtures").append(singleValueDate.getElem());
            singleValueDate.getElem().find("input").datepicker("show");

            var div = jQuery(".jr-jDatepickerPopupContainer");

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
            jQuery(".jr-jDatepickerPopupContainer").remove();
        });

    });

    describe("Single Datetime", function(){

        var singleValueDate,
            originalDateFormat = jrsConfigs.localeSettings.dateFormat,
            originalTimeFormat = jrsConfigs.localeSettings.timeFormat,
            originalTimestampSeparator = jrsConfigs.localeSettings.timestampSeparator;

        beforeEach(function(){
            // change format to some unusual
            jrsConfigs.localeSettings.dateFormat = "dd.mm.yy";
            jrsConfigs.localeSettings.timeFormat = "HH-mm-ss";
            jrsConfigs.localeSettings.timestampSeparator = "~";
            singleValueDate = new Controls.SingleValueDatetime({
                type:"singleValueDate",
                uri:"/reports/birthDate",
                id:"birthDate",
                label:"Birth Date",
                mandatory:"true",
                validators:[],
                visible: true,
                readOnly: false
            });
        });

        afterEach(function(){
            // restore original formats
            jrsConfigs.localeSettings.dateFormat = originalDateFormat;
            jrsConfigs.localeSettings.timeFormat = originalTimeFormat;
            jrsConfigs.localeSettings.timestampSeparator = originalTimestampSeparator;
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
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

        // TODO : Modifie to test case according to new chnages
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
                visible: true,
                readOnly: false
            });
        });

        afterEach(function(){
            // restore original formats
            jrsConfigs.localeSettings.timeFormat = originalTimeFormat;
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
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
                type: "singleSelect",
                uri: "/reports/country",
                id: "country",
                label: "Country",
                mandatory: "true",
                readOnly: "true",
                validators: [],
                visible: true
            }, {
                dataUri: '/uri',
                initialSelectedValues: [
                    {value:"Guerrero", label:"Mexico | Guerrero"}
                ],
                inputControlsService: sandbox.stub(),
                paginatedValuesOptions: [
                    {
                        name: 'country'
                    }
                ],
                initialSelectedValuesToLabelMap: { Guerrero: "Mexico | Guerrero" }
            });

            sandbox.stub(singleSelect.inputControlsDataProviderWithDataLabelHash, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: controlData,
                    total: controlData.length
                })
            );
        });

        afterEach(function() {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
            jQuery("body").find(".jr-mSingleselect-dropdown").remove();
        });

        it("Set", function (done) {
            singleSelect.render();

            singleSelect.fetch('/report/uri', [
                {
                    name: 'country'
                }
            ]).then(() => {
                singleSelect.set({
                    values: ['Guerrero']
                });

                expect(singleSelect.inputControlsDataProviderWithDataLabelHash.getData).toHaveBeenCalledWith(
                    '/report/uri',
                    [
                        {
                            name: 'country',
                            offset: 0,
                            limit: 100
                        }
                    ]
                );

                const list = jQuery("body").find(".jr-mSingleselect-dropdown");

                expect(list.find("li").length).toEqual(4);
                expect(list.find("li.jr-isSelected").length).toEqual(1);
                expect(list.find("li.jr-isSelected").attr("data-index")).toEqual("1");

                done();
            });
        });

        it("Get", function (done) {
            singleSelect.render();

            singleSelect.fetch('/uri', [
                {
                    name: 'country'
                }
            ]).then(() => {
                singleSelect.set({
                    values: ['Guerrero']
                });

                expect(singleSelect.get("selection")).toEqual("Guerrero");
                expect(singleSelect.get("values")).toEqual(['Guerrero']);

                done();
            });
        });

        it('updateSelectionOnOptionChange', function () {
            var newSelection = [{ 'label': 'Guerrero', 'value': 'Guerrero' }];
            singleSelect.updateSelectionOnOptionChange(newSelection);
            expect(singleSelect.initialSelectedValuesToLabelMap).toEqual({ Guerrero: 'Guerrero' })
        });

        it('should show error popup on single select component fetch', (done) => {
            singleSelect.inputControlsDataProviderWithDataLabelHash.getData.restore();

            sandbox.stub(singleSelect.inputControlsDataProviderWithDataLabelHash, 'getData').returns(
                jQuery.Deferred().reject({
                    responseJSON: {
                        message: 'Error message'
                    }
                })
            );

            const showErrorPopupStub = sandbox.stub();

            rewire$showErrorPopup(showErrorPopupStub);

            singleSelect.render();

            singleSelect.fetch('/uri', [
                {
                    name: 'country'
                }
            ]).catch(() => {
                expect(showErrorPopupStub).toHaveBeenCalledWith('Error message');
                restore();
                done();
            });
        });

        it('should show error popup once if errors occur in single select component after the fetch', (done) => {
            singleSelect.inputControlsDataProviderWithDataLabelHash.getData.restore();

            sandbox.stub(singleSelect.inputControlsDataProviderWithDataLabelHash, 'getData').returns(
                jQuery.Deferred().reject({
                    responseJSON: {
                        message: 'Error message'
                    }
                })
            );

            const showErrorPopupStub = sandbox.stub();

            rewire$showErrorPopup(showErrorPopupStub);

            singleSelect.render();

            singleSelect.fetch('/uri', [
                {
                    name: 'country'
                }
            ]).catch(() => {
                singleSelect.singleSelect.trigger('listRenderError', {
                    responseJSON: {
                        message: 'Another error message'
                    }
                });

                expect(showErrorPopupStub).toHaveBeenCalledWith('Error message');
                expect(showErrorPopupStub).toHaveBeenCalledWith('Another error message');
                expect(showErrorPopupStub.callCount).toEqual(2);

                restore();
                done();
            });
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

        const stubMultiSelectDataProvider = (multiSelect, data) => {
            sandbox.stub(multiSelect.inputControlsDataProviderWithDataLabelHash, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: data,
                    total: data.length
                })
            );
        };

        const initialSelectedValues = [
            {value:"Guerrero", label:"Mexico | Guerrero"},
            {value:"Sinaloa", label:"Mexico | Sinaloa"}
        ];

        const initialSelectedValuesToLabelMap={
            Guerrero: "Mexico | Guerrero",
            Sinaloa:"Mexico | Sinaloa"
        }

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
            }, {
                dataUri: '/uri',
                initialSelectedValues,
                paginatedValuesOptions: [],
                inputControlsService: {
                    getInputControlsPaginatedValues: sandbox.stub().returns(
                        jQuery.Deferred().resolve({
                            data: [],
                            total: 0
                        })
                    )
                },
                initialSelectedValuesToLabelMap
            });
        });

        afterEach(function() {
            // remove all listeners which were set by Controller initializer
            multiSelect && multiSelect.multiSelect.remove();
            jQuery(document).off();
        });

        it("Set", function (done) {
            stubMultiSelectDataProvider(multiSelect, controlData);
            multiSelect.render();

            multiSelect.fetch('/uri', [
                {
                    name: 'state'
                }
            ]).then(() => {
                multiSelect.set({ values: ['Guerrero', 'Sinaloa'] });

                var list = multiSelect.multiSelect.$el.find(".jr-mScalablelist").first();

                expect(multiSelect.inputControlsDataProviderWithDataLabelHash.getData)
                    .toHaveBeenCalledWith('/uri', [
                        {
                            name: 'state',
                            offset: 0,
                            limit: 100
                        }
                    ]);

                expect(list.find("li.jr-mSelectlist-item").length).toEqual(5);
                expect(list.find("li.jr-mSelectlist-item[data-index='0']")).not.toHaveClass("jr-isSelected");
                expect(list.find("li.jr-mSelectlist-item[data-index='1']")).toHaveClass("jr-isSelected");
                expect(list.find("li.jr-mSelectlist-item[data-index='2']")).not.toHaveClass("jr-isSelected");
                expect(list.find("li.jr-mSelectlist-item[data-index='3']")).toHaveClass("jr-isSelected");
                expect(list.find("li.jr-mSelectlist-item[data-index='4']")).not.toHaveClass("jr-isSelected");

                done();
            });
        });

        it("Get", function (done) {
            stubMultiSelectDataProvider(multiSelect, controlData);
            multiSelect.render();

            multiSelect.fetch([]).then(() => {
                multiSelect.set({ values: ['Guerrero', 'Sinaloa'] });

                expect(multiSelect.get("values")).toEqual(['Guerrero', 'Sinaloa']);
                expect(multiSelect.get("selection")).toEqual(["Guerrero", "Sinaloa"]);

                done();
            });
        });

        it("Get none", function (done) {
            stubMultiSelectDataProvider(multiSelect, controlData);
            multiSelect.render();

            multiSelect.fetch([]).then(() => {
                multiSelect.set({ values: [] });
                expect(multiSelect.get("values")).toEqual([]);
                expect(multiSelect.get("selection")).toEqual([]);

                done();
            });
        });

        it('setValue when nothing selected', function(){
            const setValueStub = sandbox.stub(multiSelect.multiSelect, 'setValue');
            const resizeStub = sandbox.stub(multiSelect,'_resize')
            const selectedValues = [RestParamsEnum.NOTHING_SUBSTITUTION_VALUE]
            multiSelect.setValue(selectedValues);
            expect(setValueStub).toHaveBeenCalledWith([]);
            expect(resizeStub).toHaveBeenCalled();
        })

        it('setValue when value selected', function(){
            const setValueStub = sandbox.stub(multiSelect.multiSelect, 'setValue');
            const resizeStub = sandbox.stub(multiSelect,'_resize')
            const selectedValues = ['USA']
            multiSelect.setValue(selectedValues);
            expect(setValueStub).toHaveBeenCalledWith(['USA']);
            expect(resizeStub).toHaveBeenCalled();
        })

        it("can be readOnly", function () {
            stubMultiSelectDataProvider(multiSelect, controlData);
            multiSelect.render();

            expect(multiSelect.getElem().find("div[disabled]").length).toEqual(1);
        });

        it('updateSelectionOnOptionChange',function(){
            var passingSelection = [
                {value:"DF", label:"Mexico | CA"},
                {value:"Guerrero", label:"Mexico | Guerrero"},
                {value:"Jalisco", label:"Mexico | Jalisco"},
                {value:"Sinaloa", label:"Mexico | Sinaloa"},
                {value:"Veracruz", label:"Mexico | Veracruz"},
                {value: '', label: ''}
            ];
            multiSelect.updateSelectionOnOptionChange(passingSelection);
            var resultSelection =
            {
                DF: "Mexico | CA",
                Guerrero: "Mexico | Guerrero",
                Jalisco: 'Mexico | Jalisco',
                Sinaloa: 'Mexico | Sinaloa',
                Veracruz: 'Mexico | Veracruz',
                '': ''

            }
            expect(multiSelect.initialSelectedValuesToLabelMap).toEqual(resultSelection)
        });

        it('clearFilter',function(){
            var ClearFilterStub = sandbox.stub(multiSelect.multiSelect, 'clearFilter');
            multiSelect.clearFilter();
            expect(ClearFilterStub).toHaveBeenCalled();
        });

        it('should show error popup on multi select component fetch', (done) => {
            sandbox.stub(multiSelect.inputControlsDataProviderWithDataLabelHash, 'getData').returns(
                jQuery.Deferred().reject({
                    responseJSON: {
                        message: 'Error message'
                    }
                })
            );

            const showErrorPopupStub = sandbox.stub();

            rewire$showErrorPopup(showErrorPopupStub);

            multiSelect.render();

            multiSelect.fetch('/uri', [
                {
                    name: 'country'
                }
            ]).catch(() => {
                expect(showErrorPopupStub).toHaveBeenCalledWith('Error message');
                restore();
                done();
            });
        });

        it('should show error popup once if errors occur in multi select component after the fetch', (done) => {
            sandbox.stub(multiSelect.inputControlsDataProviderWithDataLabelHash, 'getData').returns(
                jQuery.Deferred().reject({
                    responseJSON: {
                        message: 'Error message'
                    }
                })
            );

            const showErrorPopupStub = sandbox.stub();

            rewire$showErrorPopup(showErrorPopupStub);

            multiSelect.render();

            multiSelect.fetch('/uri', [
                {
                    name: 'country'
                }
            ]).catch(() => {
                multiSelect.multiSelect.trigger('listRenderError', {
                    responseJSON: {
                        message: 'Another error message'
                    }
                });

                expect(showErrorPopupStub).toHaveBeenCalledWith('Error message');
                expect(showErrorPopupStub).toHaveBeenCalledWith('Another error message');
                expect(showErrorPopupStub.callCount).toEqual(2);

                restore();
                done();
            });
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
            }, {
                dataUri: '/uri',
                inputControlsService: sandbox.stub(),
                paginatedValuesOptions: [
                    {
                        name: 'countryRadio'
                    }
                ]
            });
        });

        afterEach(function() {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
        });

        it('should render component with readOnly values', (done) => {
            sandbox.stub(singleSelectRadio.inputControlsDataProvider, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: controlData
                })
            );

            singleSelectRadio.render().then(() => {
                singleSelectRadio.set({
                    values: 'USA'
                });

                expect(singleSelectRadio.inputControlsDataProvider.getData).toHaveBeenCalledWith(
                    '/uri',
                    [
                        {
                            name: 'countryRadio',
                            offset: 0
                        }
                    ]
                );

                expect(singleSelectRadio.getElem().find("input").length).toEqual(3);
                expect(singleSelectRadio.getElem().find("input[value=Canada]")).not.toBeChecked("checked");
                expect(singleSelectRadio.getElem().find("input[value=Mexico]")).not.toBeChecked("checked");
                expect(singleSelectRadio.getElem().find("input[value=USA]")).toBeChecked("checked");

                expect(singleSelectRadio.get('selection')).toEqual('USA');
                expect(singleSelectRadio.get('values')).toEqual('USA');

                expect(singleSelectRadio.getElem().find("input[disabled]").length).toEqual(3);

                done();
            });
        });

        it('should fetch and render component with selection', (done) => {
            sandbox.stub(singleSelectRadio.inputControlsDataProvider, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: controlData
                })
            );

            singleSelectRadio.render().then(() => {
                singleSelectRadio.fetch('/report/uri', [
                    {
                        name: 'countryRadio'
                    }
                ]).then(() => {
                    expect(singleSelectRadio.inputControlsDataProvider.getData).toHaveBeenCalledWith(
                        '/report/uri',
                        [
                            {
                                name: 'countryRadio',
                                offset: 0
                            }
                        ]
                    );

                    expect(singleSelectRadio.getElem().find("input").length).toEqual(3);
                    expect(singleSelectRadio.getElem().find("input[value=Canada]")).not.toBeChecked("checked");
                    expect(singleSelectRadio.getElem().find("input[value=Mexico]")).not.toBeChecked("checked");
                    expect(singleSelectRadio.getElem().find("input[value=USA]")).toBeChecked("checked");

                    done();
                });
            });
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
            }, {
                dataUri: '/uri',
                inputControlsService: sandbox.stub(),
                paginatedValuesOptions: [
                    {
                        name: 'stateCheckbox'
                    }
                ]
            });
        });

        afterEach(function() {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
        });

        it('should render component with readOnly values', (done) => {
            sandbox.stub(multipleCheckbox.inputControlsDataProvider, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: controlData
                })
            );

            multipleCheckbox.render().then(() => {
                multipleCheckbox.set({
                    values: ["Guerrero", "Sinaloa"]
                });

                expect(multipleCheckbox.inputControlsDataProvider.getData).toHaveBeenCalledWith(
                    '/uri',
                    [
                        {
                            name: 'stateCheckbox',
                            offset: 0
                        }
                    ]
                );

                //Check view
                expect(multipleCheckbox.getElem().find("input").length).toEqual(5);
                expect(multipleCheckbox.getElem().find("input[value=DF]")).not.toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Guerrero]")).toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Jalisco]")).not.toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Sinaloa]")).toBeChecked("checked");
                expect(multipleCheckbox.getElem().find("input[value=Veracruz]")).not.toBeChecked("checked");

                expect(multipleCheckbox.get('selection')).toEqual(["Guerrero", "Sinaloa"]);
                expect(multipleCheckbox.get('values')).toEqual(["Guerrero", "Sinaloa"]);

                expect(multipleCheckbox.getElem().find("input[disabled]").length).toEqual(5);

                done();
            });
        });

        it('should render component with readOnly values', (done) => {
            sandbox.stub(multipleCheckbox.inputControlsDataProvider, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: controlData
                })
            );

            multipleCheckbox.render().then(() => {
                multipleCheckbox.fetch('/report/uri', [
                    {
                        name: 'stateCheckbox'
                    }
                ]).then(() => {
                    expect(multipleCheckbox.inputControlsDataProvider.getData).toHaveBeenCalledWith(
                        '/report/uri',
                        [
                            {
                                name: 'stateCheckbox',
                                offset: 0
                            }
                        ]
                    );

                    //Check view
                    expect(multipleCheckbox.getElem().find("input").length).toEqual(5);
                    expect(multipleCheckbox.getElem().find("input[value=DF]")).not.toBeChecked("checked");
                    expect(multipleCheckbox.getElem().find("input[value=Guerrero]")).toBeChecked("checked");
                    expect(multipleCheckbox.getElem().find("input[value=Jalisco]")).not.toBeChecked("checked");
                    expect(multipleCheckbox.getElem().find("input[value=Sinaloa]")).toBeChecked("checked");
                    expect(multipleCheckbox.getElem().find("input[value=Veracruz]")).not.toBeChecked("checked");

                    done();
                });
            });
        });

        it("should render component with none values", function (done) {
            sandbox.stub(multipleCheckbox.inputControlsDataProvider, 'getData').returns(
                jQuery.Deferred().resolve({
                    data: controlDataNone
                })
            );

            multipleCheckbox.render().then(() => {
                multipleCheckbox.set({
                    values: []
                });

                expect(multipleCheckbox.get("values")).toEqual([]);
                expect(multipleCheckbox.get("selection")).toEqual([]);

                done();
            });
        });
    });
});