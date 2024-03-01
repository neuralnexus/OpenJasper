import isControlInCascade from 'src/controls/predicate/isControlInCascade';

describe('isControlInCascade Tests', () => {

    it('should return true if control is in cascade', () => {
        const result = isControlInCascade({
            slaveDependencies: ['controlId']
        });

        expect(result).toEqual(true);
    });
});
