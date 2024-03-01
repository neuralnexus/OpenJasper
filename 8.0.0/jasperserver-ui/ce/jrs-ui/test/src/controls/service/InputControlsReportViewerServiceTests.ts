import $ from 'jquery';
import sinon, { SinonSandbox } from 'sinon';

import InputControlsReportViewerService from 'src/controls/service/InputControlsReportViewerService';
import {
    MetadataResponse,
    PaginatedValuesResponse,
    SelectedValuesResponse, ValuesResponse, SelectedOnlyResponse
} from 'src/controls/rest/types/InputControlsServiceType';
import getInputControlsServiceMock from '../mock/inputControlsServiceWithCacheMock';
import {
    selectedOnlyResponseSelectionChanges, selectedOnlyResponseSelectionChangesMock1,
    selectedOnlyResponseSelectionChangesMock2, selectedOnlyResponseSelectionChangesMock2ForTextInput
} from './mock/selectedOnlyValuesResponseMock'

import { inputControlsMetadataMock, inputControlsMetadataMock2 } from './mock/inputControlsMetadataMock';
import { inputControlsPaginatedValuesResponseMock, inputControlsPaginatedValuesResponseMockForText } from './mock/inputControlsPaginatedValuesResponseMock';
import inputControlsSelectedValuesMock from './mock/inputControlsSelectedValuesMock';
import inputControlsStructureMock from './mock/inputControlsStructureMock';

import initialStateSelectionMock from './mock/fetchInputControlsInitialState/initialStateSelectionMock';
import { initialStatePaginationOptionsPerControl, initialStatePaginationOptionsPerControlWithSelection }
    from './mock/fetchInputControlsInitialState/initialStatePaginationOptionsPerControl';
import initialStatePaginationOptionsPerControlPassingParams from './mock/fetchInputControlsInitialState/initialStatePaginationOptionsPerControlPassingParams'

import { initialStateInitialPaginationValuesOptionsMock, initialStateInitialPaginationValuesOptionsWithPreSelectionMock }
    from './mock/fetchInputControlsInitialState/initialStateInitialPaginationValuesOptionsMock';
import paginationOptionsBySelectionMock
    from './mock/fetchInputControlsOptionsBySelectionAndUri/paginationOptionsBySelectionMock';
import paginationOptionsPerControlBySelectionMock
    from './mock/fetchInputControlsOptionsBySelectionAndUri/paginationOptionsPerControlBySelectionMock';
import paginationOptionsOnControlSelectionChangeMock
    from './mock/fetchInputControlsValuesOnControlSelectionChange/paginationOptionsOnControlSelectionChangeMock';
import paginationOptionsOnControlSelectionChangeWithInitSelectionMock
    from './mock/fetchInputControlsValuesOnControlSelectionChange/paginationOptionsOnControlSelectionChangeWithInitSelectionMock';

describe('InputControlsReportViewerService Tests', () => {
    let sandbox: SinonSandbox;

    const createInputControlsReportViewerService = (opts: {
        paginatedValuesResponse: PaginatedValuesResponse,
        selectedValuesResponse?: SelectedValuesResponse,
        metadataResponse?: MetadataResponse,
        valuesResponse?: ValuesResponse,
        selectedOnlyResponse?: SelectedOnlyResponse,
        rejectPaginatedValuesResponseWith?: { errorCode: string }
    }) => {
        const inputControlsService = {
            ...getInputControlsServiceMock(opts),
            setCacheValueForControlPaginatedValues() { },
            clearCache() { }
        };

        const inputControlsReportViewerService = new InputControlsReportViewerService({
            inputControlsService
        });

        return {
            inputControlsService,
            inputControlsReportViewerService
        }
    };

    beforeEach(() => {
        sandbox = sinon.createSandbox();
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should fetchInputControlsInitialState', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock
        });

        const getSelectedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');
        const getPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');
        const allRequestParameters: any = {}

        inputControlsReportViewerService
            .fetchInputControlsInitialState('/report/uri', allRequestParameters)
            .then((options) => {
                const {
                    structure,
                    selection,
                    paginationOptionsPerControl,
                    paginatedValuesResponse
                } = options;

                expect(structure).toEqual(inputControlsStructureMock);
                expect(selection).toEqual(initialStateSelectionMock);
                expect(paginationOptionsPerControl).toEqual(initialStatePaginationOptionsPerControl);
                expect(paginatedValuesResponse).toEqual(inputControlsPaginatedValuesResponseMock);

                expect(getPaginatedValuesSpy).toHaveBeenCalledWith('/report/uri',
                    initialStateInitialPaginationValuesOptionsMock);
                expect(getSelectedValuesSpy).toHaveBeenCalledWith('/report/uri');

                expect(setCacheValueForControlPaginatedValuesSpy.callCount).toEqual(4);

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[1],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[2],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[3],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[4],
                    inputControlsPaginatedValuesResponseMock
                );

                done();
            });
    });

    it('should fetchInputControlsInitialState with undefined options', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock
        });

        const resoleObj: any = $.Deferred().resolve();
        const metaStub = sandbox.stub(inputControlsService, 'getInputControlsMetadata');
        metaStub.returns(resoleObj);

        const getSelectedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');
        const getPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const allRequestParameters: any = {};
        inputControlsReportViewerService
            .fetchInputControlsInitialState('/report/uri', allRequestParameters)
            .then(() => {

                expect(getPaginatedValuesSpy).not.toHaveBeenCalledWith('/report/uri',
                    initialStateInitialPaginationValuesOptionsMock);

                expect(getSelectedValuesSpy).not.toHaveBeenCalledWith('/report/uri');

                done();
            });
    });

    it('should fetchInitialInputControlsValuesByUri', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock
        });

        const getSelectedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');
        const getPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');

        inputControlsReportViewerService
            .fetchInitialInputControlsValuesByUri('/report/uri', inputControlsStructureMock, {})
            .then((options) => {
                const {
                    structure,
                    selection,
                    paginationOptionsPerControl,
                    paginatedValuesResponse
                } = options;

                expect(structure).toEqual(inputControlsStructureMock);
                expect(selection).toEqual(initialStateSelectionMock);
                expect(paginationOptionsPerControl).toEqual(initialStatePaginationOptionsPerControl);
                expect(paginatedValuesResponse).toEqual(inputControlsPaginatedValuesResponseMock);

                expect(getPaginatedValuesSpy).toHaveBeenCalledWith('/report/uri',
                    initialStateInitialPaginationValuesOptionsMock);
                expect(getSelectedValuesSpy).toHaveBeenCalledWith('/report/uri');

                expect(setCacheValueForControlPaginatedValuesSpy.callCount).toEqual(4);

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[1],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[2],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[3],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControl[4],
                    inputControlsPaginatedValuesResponseMock
                );

                done();
            });
    });

    it('should fetchInputControlsOptionsBySelectionAndUri', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock
        });

        const clearCacheSpy = sandbox.spy(inputControlsService, 'clearCache');
        const getInputControlsPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService,
            'setCacheValueForControlPaginatedValues');

        inputControlsReportViewerService
            .fetchInputControlsOptionsBySelectionAndUri(
                {
                    1: ['value1'],
                    2: ['value2'],
                    3: ['value3'],
                    4: ['value4'],
                    5: ['value5']
                },
                '/report/uri',
                inputControlsStructureMock
            ).then((response, paginationOptions, paginationOptionsPerControl) => {
                expect(clearCacheSpy).toHaveBeenCalled();
                expect(getInputControlsPaginatedValuesSpy)
                    .toHaveBeenCalledWith('/report/uri', paginationOptionsBySelectionMock);

                expect(setCacheValueForControlPaginatedValuesSpy.callCount).toEqual(4);
                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    paginationOptionsPerControlBySelectionMock[1],
                    inputControlsPaginatedValuesResponseMock
                );
                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    paginationOptionsPerControlBySelectionMock[2],
                    inputControlsPaginatedValuesResponseMock
                );
                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    paginationOptionsPerControlBySelectionMock[3],
                    inputControlsPaginatedValuesResponseMock
                );
                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    paginationOptionsPerControlBySelectionMock[4],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(response).toEqual(inputControlsPaginatedValuesResponseMock);
                expect(paginationOptions).toEqual(paginationOptionsBySelectionMock);
                expect(paginationOptionsPerControl).toEqual(paginationOptionsPerControlBySelectionMock);

                done();
            });
    });

    it('should fetchInputControlsOptionsBySelectionAndUri for nothing substitution', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock
        });

        const clearCacheSpy = sandbox.spy(inputControlsService, 'clearCache');
        inputControlsReportViewerService
            .fetchInputControlsOptionsBySelectionAndUri(
                {
                    1: []
                },
                '/report/uri',
                inputControlsStructureMock
            ).then((response, paginationOptions) => {
                expect(clearCacheSpy).toHaveBeenCalled();
                expect(paginationOptions).toEqual([{
                    name: '1', offset: 0, limit: 100, value: ['~NOTHING~']
                }]);

                done();
            });
    });

    it('should fetchInputControlsValuesOnControlSelectionChange', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedOnlyResponse: selectedOnlyResponseSelectionChanges
        });

        const clearCacheSpy = sandbox.spy(inputControlsService, 'clearCache');
        const getInputControlsPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');
        const getSelectedOnlyValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsOnlySelectedValue');

        inputControlsReportViewerService.fetchInputControlsValuesOnControlSelectionChange({
            controlId: '2',
            value: ['value2'],
            uri: '/report/uri',
            structure: inputControlsStructureMock,
            selection: {
                1: ['value1'],
                2: ['value2'],
                3: ['value3'],
                4: ['value4'],
                5: ['value5']
            },
            initialSelectedValues: {
                1: [{ value: 'value' }],
                2: [{ value: 'value' }],
                3: [{ value: 'value' }],
                4: [{ value: 'value' }],
                5: [{ value: 'value' }]
            }
        }).then((response, selectionPerControl, paginationOptions) => {
            expect(clearCacheSpy).toHaveBeenCalled();
            expect(getInputControlsPaginatedValuesSpy).toHaveBeenCalledWith(
                '/report/uri',
                paginationOptionsOnControlSelectionChangeMock
            );
            expect(getSelectedOnlyValuesSpy).toHaveBeenCalledWith('/report/uri',
                {
                    1: ['value1'],
                    2: ['value2'],
                    3: ['value3'],
                    4: ['value4'],
                    5: ['value5']
                });

            expect(response).toEqual(inputControlsPaginatedValuesResponseMock);
            expect(paginationOptions).toEqual(paginationOptionsOnControlSelectionChangeMock);
            expect(selectionPerControl).toEqual({
                1: ['1'], 2: ['2'], 3: ['3'], 4: ['4'], 5: ['5']
            });
            expect(setCacheValueForControlPaginatedValuesSpy).not.toHaveBeenCalled();
            done();
        });
    });

    it('should fetchInputControlsValuesOnControlSelectionChange with initial selection included', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock,
            selectedOnlyResponse: selectedOnlyResponseSelectionChangesMock1
        });

        const clearCacheSpy = sandbox.spy(inputControlsService, 'clearCache');
        const getInputControlsPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');
        const getSelectedOnlyValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsOnlySelectedValue');

        inputControlsReportViewerService.fetchInputControlsValuesOnControlSelectionChange({
            controlId: '2',
            value: ['value2', 'value'],
            uri: '/report/uri',
            structure: inputControlsStructureMock,
            selection: {
                1: ['value1'],
                2: ['value2', 'value'],
                3: ['value3'],
                4: ['value4'],
                5: ['value5']
            },
            initialSelectedValues: {
                1: [{ value: 'value' }],
                2: [{ value: 'value' }],
                3: [{ value: 'value' }],
                4: [{ value: 'value' }],
                5: [{ value: 'value' }]
            }
        }).then((response, selectionPerControl, paginationOptions) => {
            expect(clearCacheSpy).toHaveBeenCalled();
            paginationOptionsOnControlSelectionChangeWithInitSelectionMock[2] = {
                name: '3', offset: 0, limit: 100, value: ['value3']
            }
            expect(getInputControlsPaginatedValuesSpy).toHaveBeenCalledWith(
                '/report/uri',
                paginationOptionsOnControlSelectionChangeWithInitSelectionMock
            );

            expect(getSelectedOnlyValuesSpy).toHaveBeenCalledWith('/report/uri',
                {
                    1: ['value1'],
                    2: ['value2', 'value'],
                    3: ['value3'],
                    4: ['value4'],
                    5: ['value5']
                });

            expect(response).toEqual(inputControlsPaginatedValuesResponseMock);
            expect(paginationOptions).toEqual(paginationOptionsOnControlSelectionChangeWithInitSelectionMock);
            expect(selectionPerControl).toEqual({
                1: ['value1'], 2: ['value2', 'value'], 3: ['value3'], 4: ['value4'], 5: ['value5']
            });

            expect(setCacheValueForControlPaginatedValuesSpy).not.toHaveBeenCalled();

            done();
        });
    });

    it('should return selection as per passing params to fetchInputControlsInitialState', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock,
            selectedOnlyResponse: selectedOnlyResponseSelectionChangesMock2
        });

        const getSelectedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');
        const allRequestParameters: any = { 1: ['1.1'] }
        const getSelectedOnlyValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsOnlySelectedValue');

        inputControlsReportViewerService
            .fetchInputControlsInitialState('/report/uri', allRequestParameters)
            .then((options) => {
                const {
                    structure,
                    selection,
                    paginationOptionsPerControl,
                    paginatedValuesResponse
                } = options;

                expect(structure).toEqual(inputControlsStructureMock);
                initialStateSelectionMock[1] = [{ value: '1.1', label: '1.1' }]
                expect(selection).toEqual(initialStateSelectionMock);
                expect(paginationOptionsPerControl).toEqual(initialStatePaginationOptionsPerControlPassingParams);
                expect(paginatedValuesResponse).toEqual(inputControlsPaginatedValuesResponseMock);
                expect(getSelectedValuesSpy).toHaveBeenCalledWith('/report/uri');
                expect(getSelectedOnlyValuesSpy).toHaveBeenCalledWith('/report/uri', {
                    1: ['1.1'], 2: ['2'], 3: ['3'], 4: ['4'], 5: ['5']
                });

                expect(setCacheValueForControlPaginatedValuesSpy.callCount).toEqual(4);

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlPassingParams[1],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlPassingParams[2],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlPassingParams[3],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlPassingParams[4],
                    inputControlsPaginatedValuesResponseMock
                );

                done();
            });
    });

    // Test case when initial selection from other resources like URL param or schedular

    it('should fetchInitialInputControlsValuesByUri passing initial selection', (done) => {
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMock,
            selectedValuesResponse: inputControlsSelectedValuesMock,
            selectedOnlyResponse: selectedOnlyResponseSelectionChangesMock2
        });

        const getSelectedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');
        const getPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const getSelectedOnlyValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsOnlySelectedValue');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');
        const mockSelection = {
            1: ['1.1']
        }

        inputControlsReportViewerService
            .fetchInitialInputControlsValuesByUri('/report/uri', inputControlsStructureMock, mockSelection)
            .then((options) => {
                const {
                    structure,
                    selection,
                    paginationOptionsPerControl,
                    paginatedValuesResponse
                } = options;

                expect(structure).toEqual(inputControlsStructureMock);

                initialStateSelectionMock[1] = [{ value: '1.1', label: '1.1' }]
                expect(selection).toEqual(initialStateSelectionMock);
                expect(paginationOptionsPerControl).toEqual(initialStatePaginationOptionsPerControlWithSelection);
                expect(paginatedValuesResponse).toEqual(inputControlsPaginatedValuesResponseMock);

                expect(getPaginatedValuesSpy).toHaveBeenCalledWith('/report/uri',
                    initialStateInitialPaginationValuesOptionsWithPreSelectionMock);
                expect(getSelectedOnlyValuesSpy).toHaveBeenCalledWith('/report/uri', {
                    1: ['1.1'], 2: ['2'], 3: ['3'], 4: ['4'], 5: ['5']
                });
                expect(getSelectedValuesSpy).toHaveBeenCalledWith('/report/uri');

                expect(setCacheValueForControlPaginatedValuesSpy.callCount).toEqual(4);

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[1],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[2],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[3],
                    inputControlsPaginatedValuesResponseMock
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[4],
                    inputControlsPaginatedValuesResponseMock
                );

                done();
            });
    });
    // Test case when initial selection from other resources like URL param or schedular text only

    it('should fetchInitialInputControlsValuesByUri passing initial selection for text selection', (done) => {
        const selectedValuesMocks = {
            selectedValue: [
                {
                    id: '1',
                    options: [
                        {
                            label: '1',
                            value: '1'
                        }
                    ]
                }
            ]
        }
        const {
            inputControlsService,
            inputControlsReportViewerService,
        } = createInputControlsReportViewerService({
            metadataResponse: inputControlsMetadataMock2,
            paginatedValuesResponse: inputControlsPaginatedValuesResponseMockForText,
            selectedValuesResponse: selectedValuesMocks,
            selectedOnlyResponse: selectedOnlyResponseSelectionChangesMock2ForTextInput
        });

        const getSelectedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');
        const getPaginatedValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');
        const getSelectedOnlyValuesSpy = sandbox.spy(inputControlsService, 'getInputControlsOnlySelectedValue');
        const setCacheValueForControlPaginatedValuesSpy = sandbox.spy(inputControlsService, 'setCacheValueForControlPaginatedValues');
        const mockSelection = {
            1: ['1.1']
        }

        inputControlsReportViewerService
            .fetchInitialInputControlsValuesByUri('/report/uri', inputControlsStructureMock, mockSelection)
            .then((options) => {
                const {
                    structure,
                    selection,
                    paginationOptionsPerControl,
                    paginatedValuesResponse
                } = options;

                expect(structure).toEqual(inputControlsStructureMock);

                initialStateSelectionMock[1] = [{ value: '1.1', label: '1.1' }]
                expect(selection).toEqual({ 1: '1.1' });
                expect(paginationOptionsPerControl).toEqual(initialStatePaginationOptionsPerControlWithSelection);
                expect(paginatedValuesResponse).toEqual(inputControlsPaginatedValuesResponseMockForText);

                expect(getPaginatedValuesSpy).toHaveBeenCalledWith('/report/uri',
                    initialStateInitialPaginationValuesOptionsWithPreSelectionMock);
                expect(getSelectedOnlyValuesSpy).toHaveBeenCalledWith('/report/uri', {
                    1: ['1.1']
                });
                expect(getSelectedValuesSpy).toHaveBeenCalledWith('/report/uri');

                expect(setCacheValueForControlPaginatedValuesSpy.callCount).toEqual(4);

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[1],
                    inputControlsPaginatedValuesResponseMockForText
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[2],
                    inputControlsPaginatedValuesResponseMockForText
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[3],
                    inputControlsPaginatedValuesResponseMockForText
                );

                expect(setCacheValueForControlPaginatedValuesSpy).toHaveBeenCalledWith(
                    '/report/uri',
                    initialStatePaginationOptionsPerControlWithSelection[4],
                    inputControlsPaginatedValuesResponseMockForText
                );

                done();
            });
    });

});
