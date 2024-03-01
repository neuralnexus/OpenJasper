import { PaginatedValuesOptions } from '../../rest/types/InputControlsServiceType';

export default (
    options: {
        criteria?: string,
        offset: number,
        limit?: number,
        name: string,
        paginatedValuesOptions: PaginatedValuesOptions[] | undefined
    }
) => {
    const {
        paginatedValuesOptions,
        ...controlOptions
    } = options;

    if (paginatedValuesOptions && paginatedValuesOptions.length > 0) {
        return paginatedValuesOptions.map((option) => {
            if (option.name === controlOptions.name) {
                return { ...option, ...controlOptions };
            }

            return option;
        });
    }

    return [
        {
            ...controlOptions
        }
    ]
};
