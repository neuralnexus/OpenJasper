import inputControlsToViewModelConverter from 'src/controls/converter/inputControlsToViewModelConverter';
import InputControlTypeEnum from 'src/controls/enum/inputControlTypeEnum';

describe('inputControlsToViewModelConverter Tests', () => {

    it('should convert metadata to view model', () => {
        const result = inputControlsToViewModelConverter.metadataToViewModelConverter({
            inputControl: [
                {
                    uri: 'repo:/uri',
                    masterDependencies: [],
                    slaveDependencies: [],
                    id: 'id',
                    type: InputControlTypeEnum.BOOL
                }
            ]
        });

        expect(result).toEqual([
            {
                uri: '/uri',
                masterDependencies: [],
                slaveDependencies: [],
                id: 'id',
                type: InputControlTypeEnum.BOOL
            }
        ]);
    });
});
