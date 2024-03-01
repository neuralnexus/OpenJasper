import $ from 'jquery';
import {
    MetadataResponse,
    PaginatedValuesResponse,
    SelectedValuesResponse,
    ValuesResponse,
    SelectedOnlyResponse
} from 'src/controls/rest/types/InputControlsServiceType';
import InputControlTypeEnum from '../../../../src/controls/enum/inputControlTypeEnum';

const defaultInputControlsSelectedValuesResponse = {
    selectedValue: [
        {
            id: 'id',
            options: [
                {
                    label: 'value',
                    value: 'value'
                }
            ],
        }
    ]
};

const defaultInputControlsMetadataResponse: MetadataResponse = {
    inputControl: [
        {
            id: 'id',
            description: 'description',
            type: InputControlTypeEnum.SINGLE_SELECT,
            uri: 'uri',
            label: 'label',
            mandatory: true,
            readOnly: true,
            visible: true,
            masterDependencies: ['1'],
            slaveDependencies: ['2'],
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

const defaultInputControlValuesResponse: ValuesResponse = {
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

const getInputControlsServiceMock = (opts: {
    paginatedValuesResponse?: PaginatedValuesResponse,
    selectedValuesResponse?: SelectedValuesResponse,
    metadataResponse?: MetadataResponse,
    valuesResponse?: ValuesResponse,
    selectedOnlyResponse?: SelectedOnlyResponse,
    rejectPaginatedValuesResponseWith?: { errorCode: string }
} = {}) => {
    return {
        getInputControlsSelectedValues() {
            const dfd = $.Deferred();

            return dfd.resolve(opts.selectedValuesResponse
                || defaultInputControlsSelectedValuesResponse, 'success') as unknown as JQueryXHR;
        },
        getInputControlsMetadata() {
            const dfd = $.Deferred();

            return dfd.resolve(opts.metadataResponse
                || defaultInputControlsMetadataResponse, 'success') as unknown as JQueryXHR;
        },
        getInputControlsValues() {
            const dfd = $.Deferred();

            return dfd.resolve(opts.valuesResponse
                || defaultInputControlValuesResponse, 'success') as unknown as JQueryXHR;
        },
        getInputControlsOnlySelectedValue() {
            const dfd = $.Deferred();

            return dfd.resolve(opts.selectedOnlyResponse
                || defaultInputControlValuesResponse, 'success') as unknown as JQueryXHR;
        },
        getInputControlsPaginatedValues(reportUri: string, options: {
            name: string,
            offset: number,
            limit: number,
            criteria: string,
            select?: string,
            value?: string[]
        }[]) {
            const dfd = $.Deferred();

            if (opts.rejectPaginatedValuesResponseWith) {
                return dfd.reject(opts.rejectPaginatedValuesResponseWith, 'error') as unknown as JQueryXHR;
            }

            if (opts.paginatedValuesResponse) {
                return dfd.resolve(opts.paginatedValuesResponse, 'success') as unknown as JQueryXHR;
            }

            return dfd.resolve({
                inputControlState: options.map((option) => {
                    return {
                        id: option.name,
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
                })
            }, 'success') as unknown as JQueryXHR;
        },
    };
};

export {
    getInputControlsServiceMock, defaultInputControlsSelectedValuesResponse,
    defaultInputControlsMetadataResponse, defaultInputControlValuesResponse
};
