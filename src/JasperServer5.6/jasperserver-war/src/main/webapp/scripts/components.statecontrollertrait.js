/*
 * Copyright (C) 2005 - 2014 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased  a commercial license agreement from Jaspersoft,
 * the following license terms  apply:
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License  as
 * published by the Free Software Foundation, either version 3 of  the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero  General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public  License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */


/*
 * @author inesterenko
 * @version: $Id: components.statecontrollertrait.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.StateControllerTrait = (function ($, _, Backbone, i18n, State) {


    return {

        initialize:function (args) {
            _.bindAll(this);
            if (this.model) {
                this.model.on("change:phase", this.handleStateChanges);
                this.model.on("change:id", this.startObserver);
            }
            if (args) {
                this.timeout = args.timeout;
                this.delay = args.delay;
            }
        },

        handleStateChanges:function (model, phase) {
            if (phase === State.INPROGRESS) {
                this.handleInprogressPhase(model)
            } else if (phase === State.READY) {
                this.handleReadyPhase(model);
            } else if (phase === State.FAILED) {
                this.handleFailedPhase(model);
            }
        },

        startObserver:function (model) {
            var phase = model.get("phase");
            if (!this.intervalId && (phase == State.INPROGRESS || phase == State.NOT_STARTED)) {
                this.intervalId = this.observePhase(model, this.timeout);
            }
        },

        handleInprogressPhase:function () {
        },

        handleReadyPhase:function (model) {
            this.reset();
        },

        handleFailedPhase:function (model) {
        },

        handleServerError:function (model) {
            model.defaultErrorDelegator.apply(model, arguments);
            this.reset();
        },

        observePhase:function (state, timeout) {

            var startTime = (new Date()).getTime(),
                self = this,
                intervalId;

            function isTimeout() {
                var currentTime = (new Date()).getTime();
                return timeout <= (currentTime - startTime);
            }

            intervalId = setInterval(_.bind(function () {
                var phase = state.get("phase"),
                    rollback = function (errorMessage) {
                        clearInterval(intervalId);
                        delete self.intervalId;
                        if (errorMessage) {
                            state.set({
                                phase:State.FAILED,
                                message:i18n[errorMessage]
                            });
                        }
                    };
                if (phase === State.INPROGRESS) {
                    if (!isTimeout()) {
                        try {
                            state.fetch({error:this.handleServerError});
                        } catch (Error) {
                            rollback("error.invalid.request")
                        }
                    } else {
                        rollback("error.timeout")
                    }
                } else {
                    rollback();
                }
            }, this), this.delay);

            return intervalId;
        },

        reset:function () {
            if (this.model) {
                this.model.reset();
            }
            if (this.intervalId) {
                clearInterval(this.intervalId);
                delete this.intervalId;
            }
        }

    }
})(
    jQuery,
    _,
    Backbone,
    jaspersoft.i18n,
    jaspersoft.components.State
);
