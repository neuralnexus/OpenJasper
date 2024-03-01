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
import Controls from 'src/controls/controls.controller';
import sinon from 'sinon';
import dialogs from 'src/components/components.dialogs';

describe('Controller', function () {
    let controller,
        responseMockWithError,
        sandbox;

    beforeEach(function () {
        sandbox = sinon.createSandbox();

        responseMockWithError = {
            inputControlState: [
                {
                    id: 'test1',
                    error: 'Error'
                },
                {
                    id: 'test2'
                },
                {
                    id: 'test3'
                }
            ]
        };

        Controls.reportOptions = {
            set: sandbox.stub()
        };

        controller = new Controls.Controller({
            reportUri: 'testUri',
            viewModel: new Controls.ViewModel(),
            dataTransfer: new Controls.DataTransfer({dataConverter: new Controls.DataConverter()})
        });

        controller.viewModel.controls = {
            'test1': {
                id: 'test1',
                value: 1,
                fetch: sandbox.stub().returns(jQuery.Deferred().resolve()),
                set: sandbox.stub(),
                get: sandbox.stub(),
                reset: sandbox.stub(),
                updateSelectionOnOptionChange: sandbox.stub(),
                clearFilter: sandbox.stub()
            },
            'test2': {
                id: 'test2',
                value: 2,
                fetch: sandbox.stub().returns(jQuery.Deferred().resolve()),
                set: sandbox.stub(),
                get: sandbox.stub(),
                reset: sandbox.stub(),
                updateSelectionOnOptionChange: sandbox.stub(),
                clearFilter: sandbox.stub()
            },
            'test3': {
                id: 'test3',
                value: 3,
                fetch: sandbox.stub().returns(jQuery.Deferred().resolve()),
                set: sandbox.stub(),
                get: sandbox.stub(),
                selection: ['selection'],
                reset: sandbox.stub(),
                updateSelectionOnOptionChange: sandbox.stub(),
                clearFilter: sandbox.stub()
            },
            'test4': {
                id: 'test4',
                value: 4,
                fetch: sandbox.stub().returns(jQuery.Deferred().resolve()),
                set: sandbox.stub(),
                get: sandbox.stub().withArgs('error').returns('Error'),
                selection: ['selection'],
                reset: sandbox.stub(),
                updateSelectionOnOptionChange: sandbox.stub(),
                clearFilter: sandbox.stub()
            }
        };
    });

    afterEach(function () {
        Controls.reportOptions = undefined;

        sandbox.restore();
        // remove all listeners which were set by Controller initializer
        jQuery(document).off();
    });

    describe('Events', function () {

        describe('selection change event', () => {
            it('should not update input controls selection on control selection change if control is not in cascade', () => {
                sandbox.stub(Controls.Utils, 'showLoadingDialogOn');

                sandbox.stub(controller.inputControlsReportViewerService,
                    'fetchInputControlsValuesOnControlSelectionChange');

                sandbox.stub(controller.viewModel, 'set');

                jQuery(document).trigger(Controls.ViewModel.CHANGE_SELECTION, [
                    'test1',
                    ['value'],
                    false
                ]);

                expect(controller.inputControlsReportViewerService.fetchInputControlsValuesOnControlSelectionChange)
                    .not.toHaveBeenCalled();

                expect(Controls.Utils.showLoadingDialogOn).not.toHaveBeenCalled();
            });


            it('should show error popup on error', (done) => {
                sandbox.stub(Controls.Utils, 'showLoadingDialogOn');

                controller.viewModel.structure = 'structure';
                controller.viewModel.controlsOptions = {
                    initialSelectedValues: 'initialSelectedValues'
                };

                sandbox.stub(controller.viewModel, 'get').withArgs('selection').returns('selection');
                sandbox.stub(dialogs.errorPopup, 'show');

                sandbox.stub(controller.inputControlsReportViewerService,
                    'fetchInputControlsValuesOnControlSelectionChange').callsFake(() => {
                    return jQuery.Deferred().reject({
                        responseJSON: {
                            message: 'errorMessage'
                        }
                    });
                });

                jQuery(document).trigger(Controls.ViewModel.CHANGE_SELECTION, [
                    'test3',
                    ['value'],
                    true
                ]);

                const dfd = Controls.Utils.showLoadingDialogOn.args[0][0];

                dfd.catch(() => {
                    expect(controller.inputControlsReportViewerService.fetchInputControlsValuesOnControlSelectionChange)
                        .toHaveBeenCalledWith(
                            {
                                controlId: 'test3',
                                value: ['value'],
                                uri: 'testUri',
                                structure: 'structure',
                                selection: 'selection',
                                initialSelectedValues: 'initialSelectedValues'
                            }
                        );

                    expect(dialogs.errorPopup.show).toHaveBeenCalledWith('errorMessage', false, undefined);

                    expect(controller.viewModel.controls.test3.set).toHaveBeenCalledWith({
                        values: ['selection']
                    });

                    expect(Controls.Utils.showLoadingDialogOn).toHaveBeenCalled();

                    done();
                });
            });
        });

        it('should update input controls selection on control selection change if the control is in cascade', (done) => {
            sandbox.stub(Controls.Utils, 'showLoadingDialogOn');

            controller.viewModel.structure = 'structure';
            controller.viewModel.controlsOptions = {
                initialSelectedValues: 'initialSelectedValues'
            };

            sandbox.stub(controller.viewModel, 'get').withArgs('selection').returns('selection');

            const paginationOptions = [
                {
                    name: 'test1',
                },
                {
                    name: 'test2',
                },
                {
                    name: 'test3',
                }
            ];

            sandbox.stub(controller.inputControlsReportViewerService,
                'fetchInputControlsValuesOnControlSelectionChange').callsFake(() => {
                return jQuery.Deferred().resolve(
                    responseMockWithError,
                    {
                        test2: ['test2Selection'],
                        test3: ['test3Selection']
                    },
                    paginationOptions
                );
            });

            jQuery(document).trigger(Controls.ViewModel.CHANGE_SELECTION, [
                'test1',
                ['value'],
                true
            ]);

            const dfd = Controls.Utils.showLoadingDialogOn.args[0][0];

            dfd.then(() => {
                expect(controller.inputControlsReportViewerService.fetchInputControlsValuesOnControlSelectionChange)
                    .toHaveBeenCalledWith(
                        {
                            controlId: 'test1',
                            value: ['value'],
                            uri: 'testUri',
                            structure: 'structure',
                            selection: 'selection',
                            initialSelectedValues: 'initialSelectedValues'
                        }
                    );

                expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                    values: ['value']
                });
                expect(controller.viewModel.controls.test2.set).toHaveBeenCalledWith({
                    values: ['test2Selection']
                });
                expect(controller.viewModel.controls.test3.set).toHaveBeenCalledWith({
                    values: ['test3Selection']
                });

                expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                    error: 'Error'
                });

                expect(controller.viewModel.controls.test1.fetch).toHaveBeenCalledWith('testUri', paginationOptions);
                expect(controller.viewModel.controls.test2.fetch).toHaveBeenCalledWith('testUri', paginationOptions);
                expect(controller.viewModel.controls.test3.fetch).toHaveBeenCalledWith('testUri', paginationOptions);

                expect(Controls.Utils.showLoadingDialogOn).toHaveBeenCalled();

                done();
            });
        });


        describe('report option change event', () => {
            let selection,
                paginationOptions;

            beforeEach(() => {
                sandbox.stub(Controls.Utils, 'showLoadingDialogOn');

                controller.viewModel.structure = 'structure';

                selection = {
                    test1: [
                        {
                            value: 'test1Selection'
                        }
                    ],
                    test2: [
                        {
                            value: 'test2Selection'
                        }
                    ],
                    test3: [
                        {
                            value: 'test3Selection'
                        }
                    ],
                    test4: undefined
                };

                paginationOptions = {
                    test1: ['test1Options'],
                    test2: ['test2Options'],
                    test3: ['test3Options'],
                    test4: ['test4Options']
                };

                sandbox.stub(controller.inputControlsReportViewerService, 'fetchInitialInputControlsValuesByUri').callsFake(() => {
                    return jQuery.Deferred().resolve(
                        {
                            structure: 'structure',
                            selection,
                            paginationOptionsPerControl: paginationOptions,
                            paginatedValuesResponse: responseMockWithError
                        }
                    );
                });
            });

            it('should reset controls selection on option select', (done) => {
                jQuery(document).trigger('reportoptions:selection:changed', {
                    reportOption: {
                        uri: 'reportOptionUri'
                    },
                    selectedData: []
                });

                const dfd = Controls.Utils.showLoadingDialogOn.args[0][0];

                dfd.then(() => {
                    expect(controller.inputControlsReportViewerService.fetchInitialInputControlsValuesByUri)
                        .toHaveBeenCalledWith('reportOptionUri', 'structure');

                    expect(controller.viewModel.controls.test1.updateSelectionOnOptionChange).toHaveBeenCalled();
                    expect(controller.viewModel.controls.test2.updateSelectionOnOptionChange).toHaveBeenCalled();
                    expect(controller.viewModel.controls.test3.updateSelectionOnOptionChange).toHaveBeenCalled();
                    expect(controller.viewModel.controls.test4.updateSelectionOnOptionChange).toHaveBeenCalled();

                    expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                        values: ['test1Selection']
                    });
                    expect(controller.viewModel.controls.test2.set).toHaveBeenCalledWith({
                        values: ['test2Selection']
                    });
                    expect(controller.viewModel.controls.test3.set).toHaveBeenCalledWith({
                        values: ['test3Selection']
                    });
                    expect(controller.viewModel.controls.test4.set).toHaveBeenCalledWith({values:[]})

                    expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                        error: 'Error'
                    });

                    expect(controller.viewModel.controls.test1.fetch).toHaveBeenCalledWith('reportOptionUri', ['test1Options']);
                    expect(controller.viewModel.controls.test2.fetch).toHaveBeenCalledWith('reportOptionUri', ['test2Options']);

                    expect(controller.viewModel.controls.test3.fetch).toHaveBeenCalledWith('reportOptionUri', ['test3Options']);
                    expect(controller.viewModel.controls.test4.fetch).toHaveBeenCalledWith('reportOptionUri', ['test4Options']);

                    expect(Controls.Utils.showLoadingDialogOn).toHaveBeenCalled();

                    done();
                });
            });

            it('should reset controls selection to default option', (done) => {
                jQuery(document).trigger('reportoptions:selection:changed', {
                    reportOption: {},
                    selectedData: []
                });

                const dfd = Controls.Utils.showLoadingDialogOn.args[0][0];

                dfd.then(() => {
                    expect(controller.viewModel.controls.test1.fetch).toHaveBeenCalledWith('testUri', ['test1Options']);
                    expect(controller.viewModel.controls.test2.fetch).toHaveBeenCalledWith('testUri', ['test2Options']);
                    expect(controller.viewModel.controls.test3.fetch).toHaveBeenCalledWith('testUri', ['test3Options']);

                    done();
                });
            });

            it('should show error popup on error', (done) => {
                controller.inputControlsReportViewerService.fetchInitialInputControlsValuesByUri.restore();

                sandbox.stub(controller.inputControlsReportViewerService, 'fetchInitialInputControlsValuesByUri').callsFake(() => {
                    return jQuery.Deferred().reject({
                        responseJSON: {
                            message: 'errorMessage'
                        }
                    });
                });

                sandbox.stub(dialogs.errorPopup, 'show');

                jQuery(document).trigger('reportoptions:selection:changed', {
                    reportOption: {},
                    previousReportOption: 'previousReportOption',
                    selectedData: []
                });

                const dfd = Controls.Utils.showLoadingDialogOn.args[0][0];

                dfd.catch(() => {
                    expect(Controls.reportOptions.set).toHaveBeenCalledWith({
                        selection: 'previousReportOption'
                    }, true);
                    expect(dialogs.errorPopup.show).toHaveBeenCalledWith('errorMessage', false, undefined);

                    done();
                });
            });
        });
    });

    it('should get view model', function () {
        expect(controller.getViewModel()).toBeDefined();
    });

    it('should fetch and set input controls state', (done) => {
        sandbox.stub(controller.viewModel, 'set');

        sandbox.stub(controller.inputControlsReportViewerService, 'fetchInputControlsInitialState').callsFake(() => {
            return jQuery.Deferred().resolve(
                {
                    structure: 'structure',
                    selection: 'selection',
                    paginationOptionsPerControl: 'paginatedValuesOptions',
                    paginatedValuesResponse: responseMockWithError
                }
            );
        });

        const allRequestParams = {};

        controller.fetchAndSetInputControlsState(allRequestParams).then(() => {
            expect(controller.inputControlsReportViewerService.fetchInputControlsInitialState)
                .toHaveBeenCalledWith('testUri', allRequestParams);

            expect(controller.viewModel.set).toHaveBeenCalledWith({
                structure: 'structure',
                controlsOptions: {
                    dataUri: 'testUri',
                    inputControlsService: controller.inputControlsService,
                    initialSelectedValues: 'selection',
                    paginatedValuesOptions: 'paginatedValuesOptions',
                    paginatedValuesResponse: responseMockWithError
                }
            });

            expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                error: 'Error'
            });

            done();
        });
    });

    it('should fetch and set input controls state with no response', (done) => {
        sandbox.stub(controller.viewModel, 'set');

        sandbox.stub(controller.inputControlsReportViewerService, 'fetchInputControlsInitialState').callsFake(() => {
            return jQuery.Deferred().resolve();
        });

        controller.fetchAndSetInputControlsState().then(() => {

            expect(controller.viewModel.set).not.toHaveBeenCalledWith({
                structure: 'structure',
                controlsOptions: {
                    dataUri: 'testUri',
                    inputControlsService: controller.inputControlsService,
                    initialSelectedValues: 'selection',
                    paginatedValuesOptions: 'paginatedValuesOptions',
                    paginatedValuesResponse: responseMockWithError
                }
            });

            done();
        });
    });

    describe('Reset controls to selection', function () {
        const selection = {
            'test1': ['test1'],
            'test2': ['test2']
        };

        beforeEach(function () {
            controller.viewModel.structure = 'structure';

            sandbox.stub(controller.inputControlsReportViewerService, 'fetchInputControlsOptionsBySelectionAndUri').returns(
                jQuery.Deferred().resolve(
                    responseMockWithError,
                    [
                        {
                            name: 'test1',
                        },
                        {
                            name: 'test2',
                        },
                        {
                            name: 'test3',
                        }
                    ],
                    {
                        test1: ['test1Options'],
                        test2: ['test2Options']
                    }
                )
            );

            spyOn(controller.getViewModel(), 'get').and.returnValue(selection);
        });

        it('should reset controls to default selection', function (done) {
            controller.resetControlsToSelection().then(() => {
                expect(controller.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri)
                    .toHaveBeenCalledWith(selection, 'testUri', 'structure');

                expect(controller.viewModel.controls.test1.reset).toHaveBeenCalledWith({
                    values: ['test1']
                });

                expect(controller.viewModel.controls.test2.reset).toHaveBeenCalledWith({
                    values: ['test2']
                });

                expect(controller.viewModel.controls.test3.reset).toHaveBeenCalledWith({
                    error: null,
                    values: undefined,
                    selection: undefined
                });

                expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                    error: 'Error'
                });

                expect(controller.viewModel.controls.test1.fetch).toHaveBeenCalledWith('testUri', [
                    'test1Options'
                ]);
                expect(controller.viewModel.controls.test2.fetch).toHaveBeenCalledWith('testUri', [
                    'test2Options'
                ]);

                expect(controller.viewModel.controls.test3.fetch).toHaveBeenCalledWith('testUri', [
                    {
                        name: 'test1',
                    },
                    {
                        name: 'test2',
                    },
                    {
                        name: 'test3',
                    }
                ]);

                expect(controller.viewModel.controls.test1.clearFilter).toHaveBeenCalled();
                expect(controller.viewModel.controls.test2.clearFilter).toHaveBeenCalled();
                expect(controller.viewModel.controls.test3.clearFilter).toHaveBeenCalled();

                done();
            });
        });

        it('should reset controls to provided selection and uri', function (done) {
            const providedSelection = {
                'test1': ['test1Provided'],
                'test2': ['test2Provided']
            };

            controller.resetControlsToSelection(providedSelection).then(() => {
                expect(controller.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri)
                    .toHaveBeenCalledWith(providedSelection, 'testUri', 'structure');

                expect(controller.viewModel.controls.test1.reset).toHaveBeenCalledWith({
                    values: ['test1Provided']
                });
                expect(controller.viewModel.controls.test2.reset).toHaveBeenCalledWith({
                    values: ['test2Provided']
                });

                expect(controller.viewModel.controls.test1.fetch).toHaveBeenCalledWith('testUri', [
                    'test1Options'
                ]);
                expect(controller.viewModel.controls.test2.fetch).toHaveBeenCalledWith('testUri', [
                    'test2Options'
                ]);

                expect(controller.viewModel.controls.test3.fetch).toHaveBeenCalledWith('testUri', [
                    {
                        name: 'test1',
                    },
                    {
                        name: 'test2',
                    },
                    {
                        name: 'test3',
                    }
                ]);

                expect(controller.viewModel.controls.test1.clearFilter).toHaveBeenCalled();
                expect(controller.viewModel.controls.test2.clearFilter).toHaveBeenCalled();
                expect(controller.viewModel.controls.test3.clearFilter).toHaveBeenCalled();

                done();
            });
        });
    });

    describe('Validation', function () {
        let viewModel;

        beforeEach(function () {
            controller.viewModel.structure = 'structure';

            viewModel = controller.getViewModel();

            sandbox.stub(viewModel, 'get').withArgs('selection').returns('selection');
        });

        it('controls should be valid', (done) => {
            sandbox.stub(controller.inputControlsReportViewerService, 'fetchInputControlsOptionsBySelectionAndUri').returns(
                jQuery.Deferred().resolve(
                    {
                        inputControlState: [
                            {
                                id: 'test1',
                            },
                            {
                                id: 'test2'
                            },
                            {
                                id: 'test3'
                            }
                        ]
                    },
                    [
                        {
                            name: 'test1',
                        },
                        {
                            name: 'test2',
                        },
                        {
                            name: 'test3',
                        }
                    ],
                    {
                        test1: ['test1Options'],
                        test2: ['test2Options']
                    }
                )
            );

            controller.validate().then(() => {
                expect(controller.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri)
                    .toHaveBeenCalledWith(
                        'selection',
                        'testUri',
                        'structure'
                    );

                expect(controller.viewModel.controls.test1.set).not.toHaveBeenCalled();
                expect(controller.viewModel.controls.test2.set).not.toHaveBeenCalled();
                expect(controller.viewModel.controls.test3.set).not.toHaveBeenCalled();

                done();
            });
        });

        it('controls should be valid if previous state of controls have error', (done) => {
            sandbox.stub(controller.inputControlsReportViewerService, 'fetchInputControlsOptionsBySelectionAndUri').returns(
                jQuery.Deferred().resolve(
                    {
                        inputControlState: [
                            {
                                id: 'test4',
                            }
                        ]
                    },
                    [
                        {
                            name: 'test4',
                        },
                    ]
                )
            );

            controller.validate().then(() => {
                expect(controller.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri)
                    .toHaveBeenCalledWith(
                        'selection',
                        'testUri',
                        'structure'
                    );

                expect(controller.viewModel.controls.test4.set).toHaveBeenCalledWith({
                    error: null
                });

                done();
            });
        });

        it('controls should be invalid', (done) => {
            sandbox.stub(controller.inputControlsReportViewerService, 'fetchInputControlsOptionsBySelectionAndUri').returns(
                jQuery.Deferred().resolve(
                    responseMockWithError,
                    [
                        {
                            name: 'test1',
                        },
                        {
                            name: 'test2',
                        },
                        {
                            name: 'test3',
                        }
                    ],
                    {
                        test1: ['test1Options'],
                        test2: ['test2Options']
                    }
                )
            );

            controller.validate().then(() => {
                expect(controller.inputControlsReportViewerService.fetchInputControlsOptionsBySelectionAndUri)
                    .toHaveBeenCalledWith(
                        'selection',
                        'testUri',
                        'structure'
                    );

                expect(controller.viewModel.controls.test1.set).toHaveBeenCalledWith({
                    error: 'Error'
                });

                done();
            });
        });
    });
    it('should listen to custom code event to initialize control', () => {
        let eventSpy = jasmine.createSpy();
        document.addEventListener("controls:initialized",eventSpy);
        let customController = new Controls.Controller({
            viewModel: new Controls.ViewModel(),
        });
        expect(eventSpy).toHaveBeenCalledWith(jasmine.objectContaining({
            detail: customController.viewModel
        }));
        document.removeEventListener("controls:initialized",eventSpy);
    });
});