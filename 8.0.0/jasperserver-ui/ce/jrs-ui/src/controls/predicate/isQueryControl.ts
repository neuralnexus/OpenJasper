import InputControlTypeEnum from '../enum/inputControlTypeEnum';

const queryControls = [
    InputControlTypeEnum.SINGLE_SELECT,
    InputControlTypeEnum.MULTI_SELECT,
    InputControlTypeEnum.SINGLE_SELECT_RADIO,
    InputControlTypeEnum.MULTI_SELECT_CHECKBOX
];

export default (control: {
    type: InputControlTypeEnum
}) => {
    return queryControls.indexOf(control.type) > -1;
};
