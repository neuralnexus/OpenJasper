import _ from 'underscore';
import RestParamsEnum from '../rest/enum/restParamsEnum';
import { PaginatedValuesOptions } from '../rest/types/InputControlsServiceType';
import InputControlTypeEnum from '../enum/inputControlTypeEnum';

const LIMIT = 100;

const omitLimit = (paginatedValuesOptions: PaginatedValuesOptions) => {
    return _.omit(paginatedValuesOptions, ['limit']);
};

type ControlSpecificOptionsFn = (paginatedValuesOptions: PaginatedValuesOptions) => PaginatedValuesOptions;
type ControlSpecificOptionsMap = {
    [key: string]: ControlSpecificOptionsFn | undefined
}

const controlSpecificOptionsMap: ControlSpecificOptionsMap = {
    [InputControlTypeEnum.SINGLE_SELECT_RADIO]: (paginatedValuesOptions: PaginatedValuesOptions) => {
        return omitLimit(paginatedValuesOptions);
    },
    [InputControlTypeEnum.MULTI_SELECT_CHECKBOX]: (paginatedValuesOptions: PaginatedValuesOptions) => {
        return omitLimit(paginatedValuesOptions);
    }
};

export default (
    id: string,
    type: string,
    options: {
        select?: RestParamsEnum.SELECTED_VALUES
        value?: string[]
    } = {}
) => {
    const result = {
        name: id,
        offset: 0,
        limit: LIMIT,
        ...options
    };

    const addControlSpecificOptions = controlSpecificOptionsMap[type];

    if (addControlSpecificOptions) {
        return addControlSpecificOptions(result);
    }

    return result;
};
