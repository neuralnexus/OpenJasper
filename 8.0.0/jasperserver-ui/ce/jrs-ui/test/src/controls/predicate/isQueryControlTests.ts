import isQueryControl from 'src/controls/predicate/isQueryControl';
import InputControlTypeEnum from 'src/controls/enum/inputControlTypeEnum';

describe('isQueryControl Tests', () => {

    it('should return true if control is a query control', () => {
        let result = isQueryControl({
            type: InputControlTypeEnum.SINGLE_SELECT
        });

        expect(result).toEqual(true);

        result = isQueryControl({
            type: InputControlTypeEnum.MULTI_SELECT
        });

        expect(result).toEqual(true);

        result = isQueryControl({
            type: InputControlTypeEnum.SINGLE_SELECT_RADIO
        });

        expect(result).toEqual(true);

        result = isQueryControl({
            type: InputControlTypeEnum.MULTI_SELECT_CHECKBOX
        });

        expect(result).toEqual(true);
    });

    it('should return false if control is not a query control', () => {
        const result = isQueryControl({
            type: InputControlTypeEnum.BOOL
        });

        expect(result).toEqual(false);
    });
});
