import InputControlTypeEnum from 'src/controls/enum/inputControlTypeEnum';

export default [
    {
        description: '1',
        label: '1',
        mandatory: false,
        readOnly: false,
        visible: true,
        validationRules: [
            {
                mandatoryValidationRule: {
                    errorMessage: ''
                }
            }
        ],
        id: '1',
        type: InputControlTypeEnum.MULTI_SELECT,
        slaveDependencies: [
            '2',
            '3'
        ],
        uri: '/uri/1'
    },
    {
        description: '2',
        label: '2',
        mandatory: false,
        readOnly: false,
        visible: true,
        validationRules: [
            {
                mandatoryValidationRule: {
                    errorMessage: ''
                }
            }
        ],
        id: '2',
        type: InputControlTypeEnum.MULTI_SELECT,
        slaveDependencies: [
            '3'
        ],
        masterDependencies: [
            '1'
        ],
        uri: '/uri/2'
    },
    {
        description: '3',
        label: '3',
        mandatory: false,
        readOnly: false,
        visible: true,
        validationRules: [
            {
                mandatoryValidationRule: {
                    errorMessage: ''
                }
            }
        ],
        id: '3',
        type: InputControlTypeEnum.SINGLE_SELECT,
        masterDependencies: [
            '1',
            '2'
        ],
        uri: '/uri/3'
    },
    {
        description: '4',
        label: '4',
        mandatory: false,
        readOnly: false,
        visible: true,
        validationRules: [
            {
                mandatoryValidationRule: {
                    errorMessage: ''
                }
            }
        ],
        id: '4',
        type: InputControlTypeEnum.MULTI_SELECT_CHECKBOX,
        uri: '/uri/4'
    },
    {
        description: '5',
        label: '5',
        mandatory: false,
        readOnly: false,
        visible: true,
        validationRules: [
            {
                mandatoryValidationRule: {
                    errorMessage: ''
                }
            }
        ],
        id: '5',
        type: InputControlTypeEnum.BOOL,
        uri: '/uri/5'
    }
];
