import {
    InputControlsServiceType, PaginatedControl, PaginatedControlOption,
    PaginatedValuesOptions
} from '../rest/types/InputControlsServiceType';
import {
    InputControlsDataProviderType as InputControlsDataProviderInterface,
    InputControlsDataProviderOptions
} from './types/InputControlsDataProviderType';

export default class InputControlsDataProvider implements InputControlsDataProviderInterface {
    private readonly inputControlsService: InputControlsServiceType;

    private readonly controlId: string;

    // @ts-ignore: unused property TODO: why we need it? it's really never used
    private additionalParams: {select?: string, value?: string[]};

    constructor(options: InputControlsDataProviderOptions) {
        this.controlId = options.controlId;
        this.inputControlsService = options.inputControlsService;
        this.additionalParams = {};
    }

    getData(uri: string, serviceOptions: PaginatedValuesOptions[]) {
        const result = this.inputControlsService
            .getInputControlsPaginatedValues(uri, serviceOptions).then((response) => {
                const state: PaginatedControl[] = response && response.inputControlState ? response.inputControlState : [];
                const controlData = state.find((control) => {
                    return control.id === this.controlId;
                }) || {
                    id: '',
                    uri: '',
                    options: [],
                    totalCount: '0'
                };

                const data = controlData.options && controlData.options.map((option) => {
                    return {
                        ...option
                    };
                });

                return {
                    data,
                    total: parseInt(controlData.totalCount as string, 10)
                }
            });

        return (result as unknown) as Promise<{
            data: PaginatedControlOption[],
            total: number
        }>
    }
};
