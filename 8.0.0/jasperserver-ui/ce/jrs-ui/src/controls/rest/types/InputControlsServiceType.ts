import InputControlTypeEnum from '../../enum/inputControlTypeEnum';

export type SelectedValuesResponse = {
    selectedValue: {
        id: string,
        options: {
            label: string,
            value: string
        }[],
        value?: string
    }[]
};

export interface MetadataResponse {
    inputControl: {
        id: string,
        description: string,
        type: InputControlTypeEnum,
        uri: string,
        label: string,
        mandatory: boolean,
        readOnly: boolean,
        visible: boolean,
        masterDependencies?: string[],
        slaveDependencies?: string[],
        validationRules: {
            mandatoryValidationRule: {
                errorMessage: string
            }
        }[]
    }[]
}

export interface ValuesResponse {
    inputControlState: {
        id: string,
        uri: string,
        values: {
            id: string,
            value: string[]
        }[]
    }[]
}

export interface PaginatedControlOption {
    selected: boolean,
    value: string,
    label: string
}

export interface PaginatedControl {
    id: string,
    uri: string,
    totalCount: string,
    value?: string,
    options?: PaginatedControlOption[]
}

export interface PaginatedValuesResponse {
    inputControlState: PaginatedControl[]
}

export interface PaginatedValuesOptions {
    name: string,
    offset: number,
    limit?: number,
    criteria?: string,
    select?: string,
    value?: string[]
}

export interface SelectedOnlyOptions { [key: string]: string[] }

export type SelectedOnlyResponse = {
    inputControlState: {
        id: string,
        options?: {
            label: string,
            value: string
        }[],
        value?: string,
        uri?: string
    }[]
};

export interface InputControlsServiceType {
    getInputControlsSelectedValues(reportUri: string): JQueryXHR,
    getInputControlsMetadata(reportUri: string): JQueryXHR,
    getInputControlsValues(reportUri: string,
        inputControlIds: string[]): JQueryXHR,
    getInputControlsPaginatedValues(reportUri: string, options: PaginatedValuesOptions[]): JQueryXHR
    getInputControlsOnlySelectedValue(reportUri: string, options: SelectedOnlyOptions): JQueryXHR
}
