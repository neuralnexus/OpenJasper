// @ts-ignore
import jrsConfigs from 'js-sdk/src/jrs.configs';
// @ts-ignore
import request from 'js-sdk/src/common/transport/request';
import RequestOptions from '../types/request';
import {
    InputControlsServiceType as InputControlsServiceInterface,
    PaginatedValuesOptions, SelectedOnlyOptions
} from './types/InputControlsServiceType';

interface InputControlsServiceOptions {
    request(options: RequestOptions): JQueryXHR
}

export default class InputControlsService implements InputControlsServiceInterface {
    private readonly request: (options: RequestOptions) => JQueryXHR;

    constructor(options: InputControlsServiceOptions) {
        this.request = options.request || request;
    }

    getInputControlsSelectedValues(reportUri: string) {
        return this.request({
            type: 'GET',
            url: `${jrsConfigs.urlContext}/rest_v2/reports${reportUri}/inputControls/selectedValues`,
            headers: {
                Accept: 'application/json',
            },
        });
    }

    getInputControlsMetadata(reportUri: string) {
        return this.request({
            type: 'GET',
            url: `${jrsConfigs.urlContext}/rest_v2/reports${reportUri}/inputControls?exclude=state`,
            headers: {
                Accept: 'application/json',
            },
        });
    }

    getInputControlsValues(reportUri: string, inputControlIds: string[]) {

        return this.request({
            type: 'GET',
            url: `${jrsConfigs.urlContext}/rest_v2/reports${reportUri}/inputControls/${inputControlIds.join(';')}/values?freshData=false&includeTotalCount=true`,
            headers: {
                Accept: 'application/json',
            },
        });
    }

    getInputControlsOnlySelectedValue(reportUri: string, options: SelectedOnlyOptions) {
        const controlIds = Object.keys(options).map((ip) => ip).join(';');
        return this.request({
            type: 'POST',
            url: `${jrsConfigs.urlContext}/rest_v2/reports${reportUri}/inputControls/${controlIds}/values?freshData=false&selectedOnly=true`,
            data: JSON.stringify(options),
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json'
            },
        });
    }

    getInputControlsPaginatedValues(reportUri: string, options: PaginatedValuesOptions[]) {
        const controlIds = options.map((c) => c.name).join(';');

        return this.request({
            type: 'POST',
            url: `${jrsConfigs.urlContext}/rest_v2/reports${reportUri}/inputControls/${controlIds}/values/pagination?freshData=false&includeTotalCount=true`,
            data: JSON.stringify({
                reportParameter: options
            }),
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json'
            },
        });
    }
}
