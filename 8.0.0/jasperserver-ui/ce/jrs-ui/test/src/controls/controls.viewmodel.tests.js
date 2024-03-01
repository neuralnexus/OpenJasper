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

/*global spyOn, spyOnEvent*/

import jQuery from 'jquery';
import _ from 'underscore';
import Controls from 'src/controls/controls.viewmodel';
import sinon from 'sinon';
import RestParamsEnum from 'src/controls/rest/enum/restParamsEnum';

describe('ViewModel', function () {
    let sandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });

    afterEach(function () {
        sandbox.restore();
        // remove all listeners which were set by Controller initializer
        jQuery(document).off();
    });
    it('use default container if it\'s not in an arguments', function () {
        var viewModel = new Controls.ViewModel();
        expect(viewModel.containerSelector).toEqual(Controls.ViewModel.DEFAULT_CONTAINER);
    });
    it('use container selector from  arguments', function () {
        var viewModel = new Controls.ViewModel({containerSelector: 'blabla'});
        expect(viewModel.containerSelector).toEqual('blabla');
    });
    it('put themselves to \'Controls\' object on creation of new instance', function () {
        var viewModel = new Controls.ViewModel();
        expect(Controls.getViewModel()).toEqual(viewModel);
    });
    it('listen for \'changed:control\' after object creation', function () {
        var spy = sinon.spy(Controls.ViewModel.prototype, 'onControlChange');
        var viewModel = new Controls.ViewModel();
        jQuery(document).trigger(Controls.CHANGE_CONTROL, {
            control: {},
            selection: 'selection'
        });
        expect(spy).toHaveBeenCalled();
        expect(spy.lastCall.args[1]).toEqual({
            control: {},
            selection: 'selection'
        });
        spy.restore();
    });
    describe(' behaves as Model', function () {
        var model;
        beforeEach(function () {
            model = new Controls.ViewModel();
            Controls['TestControlType_1'] = Controls.BaseControl;
            Controls['TestControlType_2'] = Controls.BaseControl;
            Controls['TestControlType_3'] = Controls.BaseControl;
        });
        afterEach(function () {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
        });
        it('can create control', function () {
            spyOn(Controls, 'TestControlType_1').and.callFake(function () {
                this.markedControls = true;
            });

            var jsonStructure = {
                type: 'testControlType_1',
                id: 'testId'
            };

            model.createControl(jsonStructure, []);

            expect(Controls.TestControlType_1).toHaveBeenCalledWith(jsonStructure, []);
            expect(model.getControls()['testId'].markedControls).toBeTruthy();
        });
        it('can remove control', function () {
            model.controls = {
                test1: '1',
                test2: '2'
            };
            model.removeControl('test2');
            expect(model.getControls()).toEqual({test1: '1'});
        });
        it('can instantiate', function () {
            sinon.stub(model, 'createControl');

            const control1 = {
                id: 'test1',
                type: 'testControlType_1'
            };

            const selection1 = ['selection1'];

            const control2 = {
                id: 'test2',
                type: 'testControlType_2'
            };

            const selection2 = ['selection2'];

            const control3 = {
                id: 'test3',
                type: 'testControlType_3'
            };

            model.instantiate([
                control1,
                control2,
                control3
            ], {
                dataUri: 'dataUri',
                inputControlsService: 'inputControlsService',
                initialSelectedValues: {
                    test1: selection1,
                    test2: selection2
                },
                paginatedValuesOptions: {
                    test1: 'paginatedOptions1',
                    test2: 'paginatedOptions2'
                }
            });

            expect(model.createControl.callCount).toEqual(3);

            expect(model.createControl).toHaveBeenCalledWith(control1, {
                dataUri: 'dataUri',
                inputControlsService: 'inputControlsService',
                initialSelectedValues: selection1,
                paginatedValuesOptions: 'paginatedOptions1'
            });
            expect(model.createControl).toHaveBeenCalledWith(control2, {
                dataUri: 'dataUri',
                inputControlsService: 'inputControlsService',
                initialSelectedValues: selection2,
                paginatedValuesOptions: 'paginatedOptions2'
            });
            expect(model.createControl).toHaveBeenCalledWith(control3, {
                dataUri: 'dataUri',
                inputControlsService: 'inputControlsService',
                initialSelectedValues: [],
                paginatedValuesOptions: []
            });
        });
        it('can get all controls', function () {
            model.controls = 'test';
            expect(model.getControls()).toEqual('test');
        });
        it('can get \'selection\' from all controls except controls with undefined values', function () {
            var testControl1 = {
                id: 'test1',
                get: jasmine.createSpy('get').and.callFake(function () {
                    return '1';
                })
            };
            var testControl2 = {
                id: 'test2',
                get: jasmine.createSpy('get').and.callFake(function () {
                    return [
                        '1',
                        '2',
                        '3'
                    ];
                })
            };
            var testControl3 = {
                id: 'test3',
                get: jasmine.createSpy('get').and.callFake(function () {
                    return undefined;
                })
            };
            var testControl4 = {
                id: 'test4',
                get: jasmine.createSpy('get').and.callFake(function () {
                    return [];
                })
            };
            var testControl5 = {
                id: 'test5',
                get: jasmine.createSpy('get').and.callFake(function () {
                    return [];
                }),
                mandatory: true
            }

            model.controls = {
                'test1': testControl1,
                'test2': testControl2,
                'test3': testControl3,
                'test4': testControl4,
                'test5': testControl5

            };
            expect(model.get('selection')).toEqual({
                'test1': ['1'],
                'test2': [
                    '1',
                    '2',
                    '3'
                ],
                'test4':[RestParamsEnum.NOTHING_SUBSTITUTION_VALUE],
                'test5':[]
            });
            expect(testControl1.get).toHaveBeenCalledWith('selection');
            expect(testControl2.get).toHaveBeenCalledWith('selection');
            expect(testControl3.get).toHaveBeenCalledWith('selection');
            expect(testControl4.get).toHaveBeenCalledWith('selection');
            expect(testControl5.get).toHaveBeenCalledWith('selection');
        });
        it('can get any attribute thought \'get\'', function () {
            model.blabla = 'test';
            expect(model.get('blabla')).toEqual('test');
        });
        it('can find control by any attribute', function () {
            var testControl1 = {
                id: 'test1',
                blabal: '2'
            };
            var testControl2 = {
                id: 'test2',
                blabal: '22'
            };
            model.controls = {
                'test1': testControl1,
                'test2': testControl2
            };
            sinon.spy(model, 'getControls');
            expect(model.findControl({id: 'test1'})).toEqual(testControl1);
            expect(model.getControls).toHaveBeenCalled();
            expect(model.findControl({blabal: '2'})).toEqual(testControl1);
            expect(model.findControl({id: 'test2'})).toEqual(testControl2);
            expect(model.findControl({blabal: '22'})).toEqual(testControl2);
            model.getControls.restore();
        });
        it('can \'pluck\' any property from all controls', function () {
            spyOn(model, 'getControls');
            spyOn(_, 'pluck').and.callThrough();
            model.pluck('blabla');
            expect(model.getControls).toHaveBeenCalled();
            expect(_.pluck.calls.mostRecent().args[1]).toEqual('blabla');
        });
        it('can set \'structure\' and trigger instantiation', function () {
            sinon.stub(model, 'instantiate');
            sinon.stub(model, 'update');
            sinon.stub(model, 'draw');

            var testStructure = {
                control1: 'test1',
                control2: 'test2'
            };

            model.set({
                structure: testStructure,
                controlsOptions: {
                    initialSelectedValues: ['initialSelectedValues']
                }
            });

            expect(model.instantiate).toHaveBeenCalledWith(testStructure, {
                initialSelectedValues: ['initialSelectedValues']
            });
            expect(model.draw).toHaveBeenCalledWith(testStructure);
            expect(model.update).toHaveBeenCalledWith(['initialSelectedValues']);
        });
        it('can set initialSelectedValues and trigger update', function () {
            sinon.stub(model, 'update');
            spyOnEvent(jQuery(document), Controls.ViewModel.CHANGE_VALUES);

            model.set({
                controlsOptions: {
                    initialSelectedValues: ['initialSelectedValues']
                }
            });

            expect(model.update).toHaveBeenCalledWith(['initialSelectedValues']);
            expect(Controls.ViewModel.CHANGE_VALUES).toHaveBeenTriggeredOn(jQuery(document));
        });
        it('can set any attribute', function () {
            var testAttribute = {
                blabla: {
                    a: '1',
                    b: '2'
                }
            };
            model.set(testAttribute);
            expect(model.blabla).toEqual({
                a: '1',
                b: '2'
            });
        });
        describe('reorders structure', function () {
            it('length: 1, order ->', function () {
                model.structure = ['a'];
                var reordered = ['a'];
                model.reorderControl(0, 1);
                expect(model.structure).toEqual(reordered);
            });
            it('length: 2, order ->', function () {
                model.structure = [
                    'a',
                    'b'
                ];
                var reordered = [
                    'b',
                    'a'
                ];
                model.reorderControl(0, 1);
                expect(model.structure).toEqual(reordered);
            });
            it('length: 2, order <-', function () {
                model.structure = [
                    'a',
                    'b'
                ];
                var reordered = [
                    'b',
                    'a'
                ];
                model.reorderControl(1, 0);
                expect(model.structure).toEqual(reordered);
            });
            it('length: 3, order <-, neighbours', function () {
                model.structure = [
                    'a',
                    'b',
                    'c'
                ];
                var reordered = [
                    'b',
                    'a',
                    'c'
                ];
                model.reorderControl(1, 0);
                expect(model.structure).toEqual(reordered);
            });
            it('length: 3, order ->, neighbours', function () {
                model.structure = [
                    'a',
                    'b',
                    'c'
                ];
                var reordered = [
                    'b',
                    'a',
                    'c'
                ];
                model.reorderControl(0, 1);
                expect(model.structure).toEqual(reordered);
            });
            it('length: 3, order <-, not neighbours', function () {
                model.structure = [
                    'a',
                    'b',
                    'c'
                ];
                var reordered = [
                    'c',
                    'a',
                    'b'
                ];
                model.reorderControl(2, 0);
                expect(model.structure).toEqual(reordered);
            });
            it('length: 3, order ->, not neighbours', function () {
                model.structure = [
                    'a',
                    'b',
                    'c'
                ];
                var reordered = [
                    'b',
                    'c',
                    'a'
                ];
                model.reorderControl(0, 2);
                expect(model.structure).toEqual(reordered);
            });
        });
    });
    describe(' behaves as View', function () {
        var view, testControl1, testControl2, testControl3, setSpy, state;
        beforeEach(function () {
            jasmine.getFixtures().set('<div id=\'inputControlsContainer\'></div>');
            view = new Controls.ViewModel();
            setSpy = jasmine.createSpy('set');
            testControl1 = {
                id: 'test1',
                visible: true,
                getElem: jasmine.createSpy('getElem').and.callFake(function () {
                    return 'elem1';
                }),
                set: setSpy,
                isValid: function () {
                    return true;
                },
                render: sinon.stub()
            };
            testControl2 = {
                id: 'test2',
                visible: true,
                getElem: jasmine.createSpy('getElem').and.callFake(function () {
                    return 'elem2';
                }),
                set: setSpy,
                isValid: function () {
                    return false;
                },
                render: sinon.stub()
            };
            testControl3 = {
                id: 'test3',
                getElem: jasmine.createSpy('getElem'),
                set: setSpy,
                isValid: function () {
                    return true;
                },
                render: sinon.stub()
            };
            view.controls = {
                'test1': testControl1,
                'test2': testControl2,
                'test3': testControl3
            };
            state = {
                'test1': {values: '1'},
                'test2': {
                    values: [
                        '1',
                        '2',
                        '3'
                    ],
                    error: 'palundra!!!'
                },
                'test3': {values: '3'}
            };
        });
        afterEach(function () {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
        });
        it('can get container', function () {
            expect(view.getContainer()).toHaveHtml(jQuery(Controls.ViewModel.DEFAULT_CONTAINER).html());
        });
        it('can remove all validation messages', function () {
            view.removeValidationMessages();
            expect(setSpy.calls.count()).toEqual(3);
            var expectedArgs = {error: null};
            expect(setSpy.calls.argsFor(0)[0]).toEqual(expectedArgs);
            expect(setSpy.calls.argsFor(1)[0]).toEqual(expectedArgs);
            expect(setSpy.calls.argsFor(2)[0]).toEqual(expectedArgs);
        });
        it('can put controls in specific container', function () {
            var container = view.getContainer();

            spyOn(container, 'empty').and.callThrough();
            spyOn(container, 'append');

            sinon.spy(view, 'getContainer');

            view.draw([
                testControl1,
                testControl2,
                testControl3
            ]);

            expect(testControl1.render).toHaveBeenCalled();
            expect(testControl2.render).toHaveBeenCalled();
            expect(testControl3.render).not.toHaveBeenCalled();

            expect(view.getContainer).toHaveBeenCalled();
            expect(container.empty).toHaveBeenCalled();
            expect(container.append.calls.argsFor(0)[0]).toEqual('elem1');
            expect(container.append.calls.argsFor(1)[0]).toEqual('elem2');

            view.getContainer.restore();
        });

        it('can update state of all controls', function () {
            sinon.stub(view, 'removeValidationMessages');

            view.update({
                test1: [
                    {
                        value: 'value1'
                    }
                ],
                test2: [
                    {
                        value: 'value2'
                    }
                ],
                test3: [
                    {
                        value: 'value3'
                    }
                ]
            });

            expect(setSpy.calls.argsFor(0)[0]).toEqual({
                'values': ['value1'],
                'error': null
            });

            expect(setSpy.calls.argsFor(1)[0]).toEqual({
                'values': ['value2'],
                'error': null
            });

            expect(setSpy.calls.argsFor(2)[0]).toEqual({
                'values': ['value3'],
                'error': null
            });
        });

        it('can update state of all controls', function () {
            sinon.stub(view, 'removeValidationMessages');

            view.update({
                test1: 'value1'
            });

            expect(setSpy.calls.argsFor(0)[0]).toEqual({
                'values': 'value1',
                'error': null
            });
        });

        it('can check for validness', function () {
            expect(view.areAllControlsValid()).toBeFalsy();
        });
        it('can set container', function () {
            jasmine.getFixtures().set('<div id=\'otherContainer\'>test</div>');
            view.setContainer('#otherContainer');
            expect(view.getContainer()).toHaveId('otherContainer');
        });
        it('can reload container', function () {
            view.container = null;
            view.reloadContainer();
            expect(view.getContainer()).toHaveId('inputControlsContainer');
        });
        it('can disable/enable all controls', function () {
            view.disable();
            var expectedArg = {disabled: true};
            expect(setSpy.calls.argsFor(0)[0]).toEqual(expectedArg);
            expect(setSpy.calls.argsFor(1)[0]).toEqual(expectedArg);
            expect(setSpy.calls.argsFor(2)[0]).toEqual(expectedArg);
            setSpy.calls.reset();
            view.enable();
            expectedArg.disabled = false;
            expect(setSpy.calls.argsFor(0)[0]).toEqual(expectedArg);
            expect(setSpy.calls.argsFor(1)[0]).toEqual(expectedArg);
            expect(setSpy.calls.argsFor(2)[0]).toEqual(expectedArg);
        });
    });
    describe('On control selection change', function () {
        let view;

        beforeEach(() => {
            view = new Controls.ViewModel();
        });

        afterEach(function () {
            // remove all listeners which were set by Controller initializer
            jQuery(document).off();
        });

        it('should trigger selection change event with correct parameters', function () {
            Controls.listen({
                'viewmodel:selection:changed': function (event, controlId, selection, controlInCascade) {
                    expect(controlId).toEqual('controlId');
                    expect(selection).toEqual(['selection']);

                    expect(controlInCascade).toBeFalsy();
                }
            });

            view.onControlChange({}, {
                control: {
                    id: 'controlId',
                    masterDependencies: ['1']
                },
                selection: ['selection']
            });

            Controls.ignore('viewmodel:selection:changed');
        });

        it('should trigger values change event when input control is not in cascade', function () {
            view.state = 'state';

            Controls.listen({
                'viewmodel:values:changed': function (event, state) {
                    expect(state).toEqual('state');
                }
            });

            view.onControlChange({}, {
                control: {
                    id: 'controlId'
                },
                selection: ['selection']
            });

            Controls.ignore('viewmodel:values:changed');
        });
    });

    describe('Detect changes in selection', function () {
        var isSelectionChanged;
        beforeEach(function () {
            isSelectionChanged = Controls.ViewModel.isSelectionChanged;
        });
        it('compare to selections', function () {
            var previous = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            var next = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            expect(isSelectionChanged(previous, next)).toBeFalsy();
            previous = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            next = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    4
                ],
                test3: ['c']
            };
            expect(isSelectionChanged(previous, next)).toBeTruthy();
            previous = {
                test1: ['a'],
                test2: [
                    1,
                    5,
                    3
                ],
                test3: ['c']
            };
            next = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            expect(isSelectionChanged(previous, next)).toBeTruthy();
            previous = {
                test1: ['a'],
                test2: [1],
                test3: ['c']
            };
            next = {
                test1: [
                    'a',
                    'b',
                    'c'
                ],
                test2: [
                    1,
                    5,
                    3
                ],
                test3: ['c']
            };
            expect(isSelectionChanged(previous, next)).toBeTruthy();
            previous = {
                test1: [
                    'a',
                    'b',
                    'c'
                ],
                test2: [
                    1,
                    5,
                    3
                ],
                test3: ['c']
            };
            next = {
                test1: ['a'],
                test2: [1],
                test3: ['c']
            };
            expect(isSelectionChanged(previous, next)).toBeTruthy();
        });
        it('compare with empty', function () {
            var previous = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            var next = {};
            expect(isSelectionChanged(previous, next)).toBeTruthy();
            next = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            previous = {};
            expect(isSelectionChanged(previous, next)).toBeTruthy();
            next = {};
            previous = {};
            expect(isSelectionChanged(previous, next)).toBeFalsy();
        });
        it('compare with null/undefined', function () {
            var previous = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            var next;
            expect(isSelectionChanged(previous, next)).toBeTruthy();
            next = {
                test1: ['a'],
                test2: [
                    1,
                    2,
                    3
                ],
                test3: ['c']
            };
            previous = undefined;
            expect(isSelectionChanged(previous, next)).toBeTruthy();
        });
    });
});