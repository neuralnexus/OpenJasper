/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

import Backbone from 'backbone';
export default Backbone.Model.extend({
    defaults: function () {
        return {
            counter: 0,
            states: [],
            position: -1,
            canUndo: false,
            canRedo: false
        };
    },
    initialize: function () {
        this.on('change:position', function () {
            this.set({
                'canUndo': this.hasPrevious(),
                'canRedo': this.hasNext()
            });
        }, this);
    },
    newState: function () {
        if (this.get('position') + 2 < this.get('states').length) {
            this.get('states').splice(this.get('position') + 2, this.get('states').length - this.get('position') - 2);
        }
        this.set('counter', this.get('counter') + 1);
        this.get('states')[this.get('position') + 1] = this.get('counter');
        this.set('position', this.get('position') + 1);
    },
    previousState: function () {
        if (this.get('position') > 0) {
            this.set('position', this.get('position') - 1);
        }
    },
    firstState: function () {
        this.set('position', 0);
    },
    nextState: function () {
        if (this.get('position') + 1 < this.get('states').length) {
            this.set('position', this.get('position') + 1);
        }
    },
    hasPrevious: function () {
        return this.get('position') > 0;
    },
    hasNext: function () {
        return this.get('position') + 1 < this.get('states').length;
    },
    currentState: function () {
        return this.get('states')[this.get('position')];
    },
    reset: function() {
        this.clear({ silent: true });
        this.set(this.defaults());
    }
});