import _ from 'underscore';
import RestParamsEnum from '../rest/enum/restParamsEnum';

import getControlPaginationOptionsByControlIdAndType from './getControlPaginationOptionsByControlIdAndType';
import InputControlTypeEnum from '../enum/inputControlTypeEnum';

interface Control {
    slaveDependencies?: string[],
    masterDependencies?: string[],
    type: InputControlTypeEnum,
    select?: RestParamsEnum.SELECTED_VALUES
    value?: string[]
}

interface Controls {
    [key: string]: Control
}

export default (
    options: {
        controlId: string,
        controls: Controls
    }
) => {
    const {
        controls
    } = options;

    return (
        Object.keys(controls).map((id) => {
            return {
                ...getControlPaginationOptionsByControlIdAndType(
                    id,
                    controls[id].type,
                    controls[id].value ? _.pick(controls[id], ['value']) : _.pick(controls[id], ['select'])
                )
            }
        })
    );
};
