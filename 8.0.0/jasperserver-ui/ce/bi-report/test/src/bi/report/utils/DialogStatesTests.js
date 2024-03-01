/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import DialogStates from 'src/bi/report/utils/DialogStates';

describe('DialogStates tests', function() {

    it('getting some coverage and tests', function() {

        const dialogStates = new DialogStates();

        const moduleId = 'moduleId';

        const defaultState = {
            key: 'value'
        };

        let stateHolder = dialogStates.register(moduleId, defaultState);

        dialogStates.getState(moduleId);
        dialogStates.setState(moduleId, defaultState);

        expect(stateHolder.get().key).toEqual(defaultState.key);
        stateHolder.updateKey('key', 'value2');
        expect(stateHolder.get().key).toEqual('value2');
        stateHolder.set(stateHolder.get());
    })
});
