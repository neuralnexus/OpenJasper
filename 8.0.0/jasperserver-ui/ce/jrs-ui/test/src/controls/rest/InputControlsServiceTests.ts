import $ from 'jquery';
import InputControlsService from 'src/controls/rest/InputControlsService';
import RequestOptions from 'src/controls/types/request';
import {
    InputControlsServiceType,
    MetadataResponse,
    PaginatedValuesResponse,
    SelectedValuesResponse,
    ValuesResponse
} from 'src/controls/rest/types/InputControlsServiceType';
import InputControlTypeEnum from '../../../../src/controls/enum/inputControlTypeEnum';

type RequestResponse = JQueryDeferred<SelectedValuesResponse>
    | JQueryDeferred<MetadataResponse>
    | JQueryDeferred<ValuesResponse>
    | JQueryDeferred<PaginatedValuesResponse>;

describe('InputControlsService Tests', () => {
    let inputControlsService: InputControlsServiceType;
    let request: (options: RequestOptions) => JQueryXHR;
    let requestOptions: RequestOptions;
    let dfd: RequestResponse;

    function getInputControlService(req: (options: RequestOptions) => JQueryXHR): InputControlsServiceType {
        inputControlsService = new InputControlsService({
            request: req
        });

        return inputControlsService;
    }

    it('should get input controls selected values', (done) => {
        request = (options) => {
            dfd = $.Deferred<SelectedValuesResponse>();

            requestOptions = options;

            return dfd.resolve({
                selectedValue: [
                    {
                        id: 'id',
                        options: [
                            {
                                label: 'value',
                                value: 'value'
                            }
                        ]
                    }
                ]
            }) as unknown as JQueryXHR;
        };

        inputControlsService = getInputControlService(request);

        inputControlsService.getInputControlsSelectedValues('/report/uri').then((resp) => {
            expect(resp).toEqual({
                selectedValue: [
                    {
                        id: 'id',
                        options: [
                            {
                                label: 'value',
                                value: 'value'
                            }
                        ]
                    }
                ]
            });
            expect(requestOptions).toEqual({
                type: 'GET',
                url: '/jasperserver-pro/rest_v2/reports/report/uri/inputControls/selectedValues',
                headers: {
                    Accept: 'application/json',
                },
            });

            done();
        });
    });

    it('should get input controls metadata', (done) => {
        const metadataResponse: MetadataResponse = {
            inputControl: [
                {
                    id: 'id',
                    description: 'desc',
                    type: InputControlTypeEnum.SINGLE_SELECT,
                    uri: 'uri',
                    label: 'label',
                    mandatory: true,
                    readOnly: true,
                    visible: true,
                    masterDependencies: ['1'],
                    slaveDependencies: ['1'],
                    validationRules: [
                        {
                            mandatoryValidationRule: {
                                errorMessage: 'error'
                            }
                        }
                    ]
                }
            ]
        };

        request = (options) => {
            dfd = $.Deferred<MetadataResponse>();

            requestOptions = options;

            return dfd.resolve(metadataResponse) as unknown as JQueryXHR;
        };

        inputControlsService = getInputControlService(request);

        inputControlsService.getInputControlsMetadata('/report/uri').then((resp) => {
            expect(resp).toEqual(metadataResponse);
            expect(requestOptions).toEqual({
                type: 'GET',
                url: '/jasperserver-pro/rest_v2/reports/report/uri/inputControls?exclude=state',
                headers: {
                    Accept: 'application/json',
                },
            });

            done();
        });
    });

    it('should get input controls values', (done) => {
        const valuesResponse: ValuesResponse = {
            inputControlState: [
                {
                    id: 'id',
                    uri: 'uri',
                    values: [
                        {
                            id: 'id',
                            value: ['value']
                        }
                    ]
                }
            ]
        };

        request = (options) => {
            dfd = $.Deferred<ValuesResponse>();

            requestOptions = options;

            return dfd.resolve(valuesResponse) as unknown as JQueryXHR;
        };

        inputControlsService = getInputControlService(request);

        inputControlsService.getInputControlsValues('/report/uri', ['1', '2']).then((resp) => {
            expect(resp).toEqual(valuesResponse);
            expect(requestOptions).toEqual({
                type: 'GET',
                url: '/jasperserver-pro/rest_v2/reports/report/uri/inputControls/1;2/values?freshData=false&includeTotalCount=true',
                headers: {
                    Accept: 'application/json',
                },
            });

            done();
        });
    });

    it('should get input controls paginated values', (done) => {
        const paginatedValuesResponse: PaginatedValuesResponse = {
            inputControlState: [
                {
                    id: 'id',
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
        };

        request = (options) => {
            dfd = $.Deferred<PaginatedValuesResponse>();

            requestOptions = options;

            return dfd.resolve(paginatedValuesResponse) as unknown as JQueryXHR;
        };

        inputControlsService = getInputControlService(request);

        const paginatedOptions = [
            {
                name: 'name1',
                offset: 0,
                limit: 100,
                criteria: 'none',
                select: 'selectedValues',
                value: ['value']
            },
            {
                name: 'name2',
                offset: 0,
                limit: 100,
                criteria: 'none',
                select: 'selectedValues',
                value: ['value']
            }
        ];

        inputControlsService.getInputControlsPaginatedValues('/report/uri', paginatedOptions).then((resp) => {
            expect(resp).toEqual(paginatedValuesResponse);
            expect(requestOptions).toEqual({
                type: 'POST',
                url: '/jasperserver-pro/rest_v2/reports/report/uri/inputControls/name1;name2/values/pagination?freshData=false&includeTotalCount=true',
                data: JSON.stringify({
                    reportParameter: paginatedOptions
                }),
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json'
                },
            });

            done();
        });
    });
});
