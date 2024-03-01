import $ from 'jquery';
import sinon, { SinonSandbox } from 'sinon';
import InputControlsServiceWithCache from 'src/controls/rest/InputControlsServiceWithCache';
import {
    InputControlsServiceType
} from 'src/controls/rest/types/InputControlsServiceType';

import {
    getInputControlsServiceMock, defaultInputControlsSelectedValuesResponse,
    defaultInputControlsMetadataResponse, defaultInputControlValuesResponse
} from '../mock/inputContolsServiceMock';

describe('InputControlsServiceWithCache Tests', () => {
    let inputControlsServiceWithCache: InputControlsServiceWithCache;
    let inputControlsService: InputControlsServiceType;
    let sandbox: SinonSandbox;

    beforeEach(() => {
        sandbox = sinon.createSandbox();

        inputControlsService = getInputControlsServiceMock();

        inputControlsServiceWithCache = new InputControlsServiceWithCache({
            inputControlsService
        });
    });

    afterEach(() => {
        sandbox.restore();
    });

    it('should call getInputControlsSelectedValues method without caching', (done) => {
        const spy = sandbox.spy(inputControlsService, 'getInputControlsSelectedValues');

        $.when(
            inputControlsServiceWithCache.getInputControlsSelectedValues('/report/uri'),
            inputControlsServiceWithCache.getInputControlsSelectedValues('/report/uri')
        ).done((resp1, resp2) => {
            expect(resp1[0]).toEqual(defaultInputControlsSelectedValuesResponse);
            expect(resp2[0]).toEqual(defaultInputControlsSelectedValuesResponse);

            expect(spy).toHaveBeenCalledWith('/report/uri');
            expect(spy.callCount).toEqual(2);

            done();
        });
    });

    it('should call getInputControlsMetadata method without caching', (done) => {
        const spy = sandbox.spy(inputControlsService, 'getInputControlsMetadata');

        $.when(
            inputControlsServiceWithCache.getInputControlsMetadata('/report/uri'),
            inputControlsServiceWithCache.getInputControlsMetadata('/report/uri')
        ).then((resp1, resp2) => {
            expect(resp1[0]).toEqual(defaultInputControlsMetadataResponse);
            expect(resp2[0]).toEqual(defaultInputControlsMetadataResponse);

            expect(spy).toHaveBeenCalledWith('/report/uri');
            expect(spy.callCount).toEqual(2);

            done();
        });
    });

    it('should call getInputControlsValues method without caching', (done) => {
        const spy = sandbox.spy(inputControlsService, 'getInputControlsValues');

        $.when(
            inputControlsServiceWithCache.getInputControlsValues('/report/uri', ['1']),
            inputControlsServiceWithCache.getInputControlsValues('/report/uri', ['1'])
        ).then((resp1, resp2) => {
            expect(resp1[0]).toEqual(defaultInputControlValuesResponse);
            expect(resp2[0]).toEqual(defaultInputControlValuesResponse);

            expect(spy).toHaveBeenCalledWith('/report/uri', ['1']);
            expect(spy.callCount).toEqual(2);

            done();
        });
    });

    it('should call getInputControlsPaginatedValues method and cache the result', (done) => {
        const spy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');

        const options = [
            {
                name: '1',
                offset: 0,
                limit: 100,
                criteria: 'a'
            },
            {
                name: '2',
                offset: 0,
                limit: 100,
                criteria: 'a'
            }
        ];

        inputControlsServiceWithCache.getInputControlsPaginatedValues('/report/uri', options).then(() => {
            inputControlsServiceWithCache.getInputControlsPaginatedValues('/report/uri', options).then((resp) => {
                expect(resp).toEqual({
                    inputControlState: [
                        {
                            id: '1',
                            uri: 'uri',
                            totalCount: '100',
                            options: [
                                {
                                    selected: true,
                                    value: 'value',
                                    label: 'label'
                                }
                            ]
                        },
                        {
                            id: '2',
                            uri: 'uri',
                            totalCount: '100',
                            options: [
                                {
                                    selected: true,
                                    value: 'value',
                                    label: 'label'
                                }
                            ]
                        }
                    ]
                });

                expect(spy).toHaveBeenCalledWith('/report/uri', options);
                expect(spy.callCount).toEqual(1);

                done();
            });
        });
    });

    it('should call getInputControlsPaginatedValues method and return and error', (done) => {
        inputControlsService = getInputControlsServiceMock({
            rejectPaginatedValuesResponseWith: {
                errorCode: 'error.code'
            }
        });

        inputControlsServiceWithCache = new InputControlsServiceWithCache({
            inputControlsService
        });

        const options = [
            {
                name: '1',
                offset: 0,
                limit: 100,
                criteria: 'a'
            },
            {
                name: '2',
                offset: 0,
                limit: 100,
                criteria: 'a'
            }
        ];

        inputControlsServiceWithCache.getInputControlsPaginatedValues('/report/uri', options).then(() => {},
            (error) => {
                expect(error).toEqual({ errorCode: 'error.code' });
                done();
            });
    });

    it('should call getInputControlsPaginatedValues method after cache was cleared', (done) => {
        const spy = sandbox.spy(inputControlsService, 'getInputControlsPaginatedValues');

        const options = [
            {
                name: '1',
                offset: 0,
                limit: 100,
                criteria: 'a'
            },
            {
                name: '2',
                offset: 0,
                limit: 100,
                criteria: 'a'
            }
        ];

        inputControlsServiceWithCache.getInputControlsPaginatedValues('/report/uri', options).then(() => {
            inputControlsServiceWithCache.clearCache();

            inputControlsServiceWithCache.getInputControlsPaginatedValues('/report/uri', options).then((resp) => {
                expect(resp).toEqual({
                    inputControlState: [
                        {
                            id: '1',
                            uri: 'uri',
                            totalCount: '100',
                            options: [
                                {
                                    selected: true,
                                    value: 'value',
                                    label: 'label'
                                }
                            ]
                        },
                        {
                            id: '2',
                            uri: 'uri',
                            totalCount: '100',
                            options: [
                                {
                                    selected: true,
                                    value: 'value',
                                    label: 'label'
                                }
                            ]
                        }
                    ]
                });

                expect(spy).toHaveBeenCalledWith('/report/uri', options);
                expect(spy.callCount).toEqual(2);

                done();
            });
        });
    });
});
