import $ from 'jquery';
import _ from 'underscore';

import {
    InputControlsServiceType as InputControlsServiceInterface,
    PaginatedValuesOptions, PaginatedValuesResponse, SelectedOnlyOptions
} from './types/InputControlsServiceType';

interface InputControlsServiceOptions {
    inputControlsService: InputControlsServiceInterface
}

const getCacheKey = (reportUri: string, options: PaginatedValuesOptions[]) => {
    const sortedOptions = _.sortBy(options, 'name');

    return sortedOptions.reduce((memo, option) => {
        const {
            name,
            offset,
            limit
        } = option;

        const params = [name, offset, limit]
            .concat(option.criteria || [])
            .concat(option.value || [])
            .concat(option.select || []);

        return `${memo} ${JSON.stringify(params)}`;
    }, `${reportUri}`);
};

export interface InputControlsServiceWithCacheInterface
    extends Pick<InputControlsServiceInterface, 'getInputControlsSelectedValues' | 'getInputControlsMetadata' | 'getInputControlsValues'> {
    getInputControlsPaginatedValues(reportUri: string, options: PaginatedValuesOptions[]): JQueryDeferred<PaginatedValuesResponse>,
    setCacheValueForControlPaginatedValues(
        reportUri: string,
        options: PaginatedValuesOptions[],
        cacheValue: PaginatedValuesResponse
    ): void,
    getInputControlsOnlySelectedValue(reportUri: string, options: SelectedOnlyOptions): JQueryXHR,
    clearCache(): void
}

export default class InputControlsServiceWithCache implements InputControlsServiceWithCacheInterface {
    private readonly inputControlsService: InputControlsServiceInterface;

    private cache: { [key: string]: PaginatedValuesResponse };

    constructor(options: InputControlsServiceOptions) {
        this.cache = {};
        this.inputControlsService = options.inputControlsService;
    }

    getInputControlsSelectedValues(reportUri: string) {
        return this.inputControlsService.getInputControlsSelectedValues(reportUri);
    }

    getInputControlsMetadata(reportUri: string) {
        return this.inputControlsService.getInputControlsMetadata(reportUri);
    }

    getInputControlsValues(reportUri: string, inputControlIds: string[]) {
        return this.inputControlsService.getInputControlsValues(reportUri, inputControlIds);
    }

    getInputControlsOnlySelectedValue(reportUri: string, options: SelectedOnlyOptions) {
        return this.inputControlsService.getInputControlsOnlySelectedValue(reportUri, options)
    }

    getInputControlsPaginatedValues(reportUri: string, options: PaginatedValuesOptions[]) {
        const cacheKey = getCacheKey(reportUri, options);
        const cacheValue = this.cache[cacheKey];

        const dfd = $.Deferred();

        if (cacheValue) {
            dfd.resolve(cacheValue);
        } else {
            this.inputControlsService.getInputControlsPaginatedValues(
                reportUri,
                options
            ).then((...args) => {
                const [
                    response
                ] = args;

                this.cache[cacheKey] = response;

                dfd.resolve(response);
            }, (...args) => {
                dfd.reject(...args);
            });
        }

        return dfd;
    }

    setCacheValueForControlPaginatedValues(
        reportUri: string,
        options: PaginatedValuesOptions[],
        cacheValue: PaginatedValuesResponse
    ) {
        const cacheKey = getCacheKey(reportUri, options);

        this.cache[cacheKey] = cacheValue;
    }

    clearCache() {
        this.cache = {};
    }
}
