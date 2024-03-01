import {
    InputControlsServiceType,
    PaginatedControlOption,
    PaginatedValuesOptions
} from '../../rest/types/InputControlsServiceType';

export interface InputControlsDataProviderOptions {
    controlId: string,
    inputControlsService: InputControlsServiceType
}

export interface InputControlsDataProviderType {
    getData(uri: string, options: PaginatedValuesOptions[]): Promise<{
        data: PaginatedControlOption[],
        total: number
    }>
}
