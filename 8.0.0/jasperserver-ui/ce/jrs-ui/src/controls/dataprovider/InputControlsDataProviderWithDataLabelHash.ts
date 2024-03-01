import { InputControlsDataProviderType } from './types/InputControlsDataProviderType';
import { InputControlsDataProviderWithDataLabelHashType } from './types/InputControlsDataProviderWithDataLabelHashType';
import { PaginatedValuesOptions } from '../rest/types/InputControlsServiceType';

interface InputControlsDataProviderWithDataLabelHashOptions {
    inputControlsDataProvider: InputControlsDataProviderType
}

export default class InputControlsDataProviderWithDataLabelHash implements InputControlsDataProviderWithDataLabelHashType {
    private readonly inputControlsDataProvider: InputControlsDataProviderType;

    private readonly dataLabelHash: {[key: string]: string};

    constructor(options: InputControlsDataProviderWithDataLabelHashOptions) {
        this.dataLabelHash = {};
        this.inputControlsDataProvider = options.inputControlsDataProvider;
    }

    getData(uri: string, options: PaginatedValuesOptions[]) {
        return this.inputControlsDataProvider.getData(uri, options).then((response) => {
            const {
                data,
                total
            } = response;

            data.forEach((option) => {
                this.dataLabelHash[option.value] = option.label;
            });

            return {
                data,
                total
            }
        });
    }

    getLabelByValue(value: string) {
        return this.dataLabelHash[value];
    }
};
