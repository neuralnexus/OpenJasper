import { InputControlsDataProviderType } from './InputControlsDataProviderType';

export interface InputControlsDataProviderWithDataLabelHashType extends InputControlsDataProviderType {
    getLabelByValue(value: string): string
}
