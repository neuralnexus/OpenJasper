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

/*global spyOn*/

import jQuery from 'jquery';
import Controls from 'src/controls/controls.basecontrol';
import sinon from 'sinon';

describe('BaseControl', function () {
    let sandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });

    afterEach(() => {
        sandbox.restore();
    });

    describe('creation', function () {
        beforeEach(function () {
            this.baseRender = Controls.BaseControl.prototype.baseRender;
            this.bindEvents = Controls.BaseControl.prototype.bindCustomEventListeners;
            this.init = Controls.BaseControl.prototype.initialize;
        });
        afterEach(function () {
            Controls.BaseControl.prototype.baseRender = this.baseRender;
            Controls.BaseControl.prototype.bindCustomEventListeners = this.bindEvents;
            Controls.BaseControl.prototype.initialize = this.init;
        });
        it('can initialize', function () {
            spyOn(Controls, 'BaseControl');
            new Controls.BaseControl({test: 'test'});
            expect(Controls.BaseControl).toHaveBeenCalledWith({test: 'test'});
        });
        it('should invoke base rendering and bind custom events', function () {
            var baseRenderSpy = jasmine.createSpy('baseRender');
            var bindCustomEventsSpy = jasmine.createSpy('bindCustomEventListeners');
            Controls.BaseControl.prototype.baseRender = baseRenderSpy;
            Controls.BaseControl.prototype.bindCustomEventListeners = bindCustomEventsSpy;
            var args = {
                test: 'test',
                visible: true
            };
            new Controls.BaseControl(args);
            expect(baseRenderSpy).toHaveBeenCalledWith(args);
            expect(bindCustomEventsSpy).toHaveBeenCalled();
        });
        it('should not invoke binding custom events on invisible control', function () {
            var bindCustomEventsSpy = jasmine.createSpy('bindCustomEventListeners');
            Controls.BaseControl.prototype.bindCustomEventListeners = bindCustomEventsSpy;
            var args = {
                test: 'test',
                visible: false
            };
            new Controls.BaseControl(args);
            expect(bindCustomEventsSpy).not.toHaveBeenCalled();
        });
        it('has base render', function () {
            Controls.BaseControl.prototype.initialize = function () {
            };
            spyOn(Controls.TemplateEngine, 'createTemplate').and.callFake(function () {
                return function () {
                    return '<div id=\'test\'>aaa</div>';
                };
            });
            var baseControl = new Controls.BaseControl();
            spyOn(baseControl, 'setElem');
            baseControl.baseRender({type: 'baseType'});
            expect(Controls.TemplateEngine.createTemplate).toHaveBeenCalledWith('baseType');
            expect(baseControl.setElem.calls.mostRecent().args[0][0].outerHTML.toLowerCase().strip().replace('"test"', 'test')).toEqual('<div id=test>aaa</div>');
        });
    });
    describe('useful functions', function () {
        var baseControl, testValues;
        beforeEach(function () {
            baseControl = new Controls.BaseControl({
                type: 'baseType',
                visible: true
            });
            testValues = [
                {
                    value: 1,
                    label: 1,
                    selected: true
                },
                {
                    value: 2,
                    label: 2
                },
                {
                    value: 3,
                    label: 3,
                    selected: true
                }
            ];
        });
        afterEach(function () {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
        });
        it('can set or get elem property', function () {
            baseControl.setElem('test');
            expect(baseControl.getElem()).toEqual('test');
        });
        it('can fire change control event', function () {
            var spyListener = jasmine.createSpy('spyListener');
            Controls.listen({'changed:control': spyListener});
            baseControl.fireControlSelectionChangeEvent('selection');
            expect(spyListener).toHaveBeenCalled();
            expect(spyListener.calls.mostRecent().args[1]).toEqual({
                control: baseControl,
                selection: 'selection'
            });
        });
        it('can be disabled, enabled', function () {
            jasmine.getFixtures().set('<div id=\'test1\'><input value=\'test\'/><select></select></div>');
            baseControl.elem = jQuery('#test1');
            baseControl.disable();
            expect(jQuery('#test1 input')).toBeDisabled();
            expect(jQuery('#test1 select')).toBeDisabled();
            baseControl.enable();
            expect(jQuery('#test1 input')).not.toBeDisabled();
            expect(jQuery('#test1 select')).not.toBeDisabled();
        });
        it('can update warning message', function () {
            var message = 'bad news';
            jasmine.getFixtures().set('<div id=\'test1\'><span class=\'warning\'></span></div>');
            baseControl.elem = jQuery('#test1');
            baseControl.error = message;
            baseControl.updateWarningMessage();
            expect(baseControl.elem.find('.warning')).toHaveText(message);
            baseControl.error = null;
            baseControl.updateWarningMessage();
            expect(baseControl.elem.find('.warning')).toHaveText('');
        });
        it('can get template section', function () {
            sinon.stub(Controls.TemplateEngine, 'createTemplateSection').callsFake(function (section) {
                return 'test';
            });
            var resultSection = baseControl.getTemplateSection('data');
            expect(Controls.TemplateEngine.createTemplateSection).toHaveBeenCalledWith('data', 'baseType');
            expect(resultSection).toEqual('test');
            resultSection = baseControl.getTemplateSection('data');
            expect(Controls.TemplateEngine.createTemplateSection).toHaveBeenCalledOnce();
            expect(resultSection).toEqual('test');
            Controls.TemplateEngine.createTemplateSection.restore();
        });
        it('can return \'selection\' or any other property', function () {
            baseControl.selection = [
                1,
                3
            ];
            expect(baseControl.get('selection')).toEqual([
                1,
                3
            ]);
            baseControl.value = 1;
            expect(baseControl.get('value')).toEqual(1);
            baseControl.test = 'palundra!';
            expect(baseControl.get('test')).toEqual('palundra!');
        });
        it('check validness', function () {
            expect(baseControl.isValid()).toBeTruthy();
            baseControl.error = 'palundra!';
            expect(baseControl.isValid()).toBeFalsy();
        });

        it('can reset control when values, selection and errors are undefined', function () {
            sandbox.stub(baseControl, 'setValue');

            baseControl.reset({
                error: null,
                values: undefined,
                selection: undefined
            });

            expect(baseControl.setValue.callCount).toEqual(1);
        });

        it('can reset control when values are defined', function () {
            sandbox.stub(baseControl, 'setValue');

            baseControl.reset({
                values: ['value']
            });

            expect(baseControl.setValue.callCount).toEqual(1);
        });

        it('can reset control when it is not visible', function () {
            sandbox.stub(baseControl, 'setValue');
            sandbox.stub(baseControl, 'isVisible').returns(false);

            baseControl.reset({
                values: ['value']
            });

            expect(baseControl.setValue.callCount).toEqual(0);
        });

        it('can find different attributes in values property', function () {
            baseControl.values = testValues;
            expect(baseControl.find({label: 3})).toEqual({
                value: 3,
                label: 3,
                selected: true
            });    //check for null safety
            //check for null safety
            baseControl.find();
            baseControl.find(null);
        });
        it('can set values', function () {
            spyOn(baseControl, 'setValue');
            baseControl.set({
                values: [
                    {value: 1},
                    {value: 2},
                    {value: 3}
                ]
            });
            expect(baseControl.setValue).toHaveBeenCalled();
        });
        it('should not call update on set if control is invisible', function () {
            baseControl.visible = false;
            spyOn(baseControl, 'setValue');
            baseControl.set({
                values: [
                    {value: 1},
                    {value: 2},
                    {value: 3}
                ]
            });
            expect(baseControl.setValue).not.toHaveBeenCalled();
        });
        it('can update selection while updating values', function () {
            baseControl.set({
                values: [
                    1,
                    2,
                    3
                ]
            });

            expect(baseControl.selection).toEqual(1);

            baseControl.selection = [];

            let values = [
                1,
                2,
                3
            ];

            baseControl.set({
                values
            });

            expect(baseControl.selection).toEqual(values);

            baseControl.selection = undefined;

            baseControl.set({
                values: [
                    1,
                    2,
                    3
                ]
            });

            expect(baseControl.selection).toEqual(1);
        });
        describe('setting selection ', function () {
            beforeEach(function () {
                spyOn(baseControl, 'fireControlSelectionChangeEvent');
            });
            it('can set selection silent or fire event', function () {
                baseControl.set({selection: 'blabla'});
                expect(baseControl.fireControlSelectionChangeEvent).toHaveBeenCalled();
                baseControl.fireControlSelectionChangeEvent.calls.reset();
                baseControl.set({selection: 'albalb'}, true);
                expect(baseControl.fireControlSelectionChangeEvent).not.toHaveBeenCalled();
            });
            it('can update single value ', function () {
                baseControl.values = 'test';
                baseControl.set({selection: 'blabla'});
                expect(baseControl.selection).toEqual('blabla');
                expect(baseControl.values).toEqual('blabla');
            });
            it('can update multi values with single selection ', function () {
                baseControl.values = [
                    1,
                    2,
                    3
                ];
                baseControl.set({selection: 'blabla'});
                expect(baseControl.selection).toEqual('blabla');
                expect(baseControl.values).toEqual([
                    1,
                    2,
                    3
                ]);
            });
            it('can update multi values with multi selection ', function () {
                baseControl.values = [
                    2,
                    3,
                    4,
                    5,
                    6
                ];
                baseControl.set({
                    selection: [
                        1,
                        2,
                        3
                    ]
                });
                expect(baseControl.selection).toEqual([
                    1,
                    2,
                    3
                ]);
                expect(baseControl.values).toEqual([
                    2,
                    3,
                    4,
                    5,
                    6
                ]);
            });
        });
        it('can be disabled or enabled', function () {
            spyOn(baseControl, 'disable');
            spyOn(baseControl, 'enable');
            baseControl.set({disabled: true});
            expect(baseControl.disable).toHaveBeenCalled();
            expect(baseControl.enable).not.toHaveBeenCalled();
            baseControl.disable.calls.reset();
            baseControl.enable.calls.reset();
            baseControl.set({disabled: false});
            expect(baseControl.enable).toHaveBeenCalled();
            expect(baseControl.disable).not.toHaveBeenCalled();
            baseControl.disable.calls.reset();
            baseControl.enable.calls.reset();
            baseControl.readOnly = true;
            baseControl.set({disabled: true});
            expect(baseControl.disable).not.toHaveBeenCalled();
            expect(baseControl.enable).not.toHaveBeenCalled();
            baseControl.disable.calls.reset();
            baseControl.enable.calls.reset();
            baseControl.set({disabled: false});
            expect(baseControl.enable).not.toHaveBeenCalled();
            expect(baseControl.disable).not.toHaveBeenCalled();
        });
        it('can update error message', function () {
            spyOn(baseControl, 'updateWarningMessage');
            baseControl.set({error: 'palundra!'});
            expect(baseControl.updateWarningMessage).toHaveBeenCalled();
        });
        it('can update error message and update selection for mandatory controls', function () {
            baseControl.selection = undefined;
            baseControl.mandatory = true;
            spyOn(baseControl, 'updateWarningMessage');
            baseControl.set({error: 'Mandatory fields'});
            expect(baseControl.updateWarningMessage).toHaveBeenCalled();
            expect(baseControl.selection).toEqual([])
        });
    });
    describe(' merge values with selection', function () {
        var values, selection;
        beforeEach(function () {
            values = [
                {
                    value: 1,
                    selected: true
                },
                {value: 2},
                {value: 3}
            ];
            selection = [
                2,
                3
            ];
        });
        it('merges array with array', function () {
            expect(Controls.BaseControl.merge(values, selection)).toEqual([
                {value: 1},
                {
                    value: 2,
                    selected: true
                },
                {
                    value: 3,
                    selected: true
                }
            ]);
        });
        it('merges array with single value', function () {
            selection = 3;
            expect(Controls.BaseControl.merge(values, selection)).toEqual([
                {value: 1},
                {value: 2},
                {
                    value: 3,
                    selected: true
                }
            ]);
        });
        it('handle undefined', function () {
            expect(Controls.BaseControl.merge(values, null)).toEqual(values);
            expect(Controls.BaseControl.merge(values, undefined)).toEqual(values);
            expect(Controls.BaseControl.merge(null, selection)).toEqual(selection);
            expect(Controls.BaseControl.merge(undefined, selection)).toEqual(selection);
        });
    });
});