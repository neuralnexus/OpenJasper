import getControlPaginationOptionsByControlIdAndType
    from 'src/controls/converter/getControlPaginationOptionsByControlIdAndType';

import InputControlTypeEnum from 'src/controls/enum/inputControlTypeEnum';
import RestParamsEnum from 'src/controls/rest/enum/restParamsEnum';

describe('getControlPaginationOptionsByControlIdAndType Tests', () => {

    it('should create paginatedValuesOptions with limit', () => {
        const result = getControlPaginationOptionsByControlIdAndType(
            'controlId',
            'controlType'
        );

        expect(result).toEqual({
            name: 'controlId',
            offset: 0,
            limit: 100
        });
    });

    it('should omit limit for single select radio control', () => {
        const result = getControlPaginationOptionsByControlIdAndType(
            'controlId',
            InputControlTypeEnum.SINGLE_SELECT_RADIO
        );

        expect(result).toEqual({
            name: 'controlId',
            offset: 0
        });
    });

    it('should omit limit for multi select checkbox control', () => {
        const result = getControlPaginationOptionsByControlIdAndType(
            'controlId',
            InputControlTypeEnum.MULTI_SELECT_CHECKBOX
        );

        expect(result).toEqual({
            name: 'controlId',
            offset: 0
        });
    });

    it('should add additional properties to result if they are specified', () => {
        const result = getControlPaginationOptionsByControlIdAndType(
            'controlId',
            'controlType',
            {
                select: RestParamsEnum.SELECTED_VALUES,
                value: ['values']
            }
        );

        expect(result).toEqual({
            name: 'controlId',
            offset: 0,
            limit: 100,
            select: RestParamsEnum.SELECTED_VALUES,
            value: ['values']
        });
    });
});
