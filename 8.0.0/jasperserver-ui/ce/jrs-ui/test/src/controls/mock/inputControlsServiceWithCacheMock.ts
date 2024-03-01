import $ from 'jquery';

import {
    MetadataResponse,
    PaginatedValuesResponse,
    SelectedOnlyResponse,
    SelectedValuesResponse, ValuesResponse
} from 'src/controls/rest/types/InputControlsServiceType';

import { getInputControlsServiceMock } from './inputContolsServiceMock';

export default (opts: {
    paginatedValuesResponse: PaginatedValuesResponse,
    selectedValuesResponse?: SelectedValuesResponse,
    metadataResponse?: MetadataResponse,
    valuesResponse?: ValuesResponse,
    selectedOnlyResponse?: SelectedOnlyResponse
    rejectPaginatedValuesResponseWith?: { errorCode: string }
}) => {
    const serviceMock = getInputControlsServiceMock(opts);

    return {
        ...serviceMock,
        getInputControlsPaginatedValues() {
            const dfd = $.Deferred();

            if (opts.rejectPaginatedValuesResponseWith) {
                return dfd.reject(opts.rejectPaginatedValuesResponseWith, 'error');
            }

            return dfd.resolve(opts.paginatedValuesResponse, 'success');
        }
    }
};
