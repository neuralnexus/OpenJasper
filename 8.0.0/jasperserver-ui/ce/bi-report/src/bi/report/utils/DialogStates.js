/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

class StateHolder {
    constructor (state) {
        this.state = state;
    }

    get() {
        return this.state
    }

    set(state) {
        this.state = state;
    }

    updateKey(key, value) {
        this.state[key] = value;
    }
}

class DialogStates {
    constructor () {
        this.states = {};
    }

    register(id, initialState) {
        if (!this.states[id]) {
            this.states[id] = new StateHolder(initialState);
        }

        return this.getState(id);
    }

    getState(id) {
        return this.states[id] || null;
    }

    setState(id, state) {
        this.states[id].set(state);
    }
}

export default DialogStates;
