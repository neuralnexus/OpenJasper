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


/**
 * @author: inesterenko
 * @version: $Id: components.stateview.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.StateView = (function($,_, Backbone, utils, State) {


    return Backbone.View.extend({

        initialize: function(){
            _.bindAll(this);
            if(this.model){
                this.model.on("change:phase", this.handleStateChanges);
                this.model.on("error:server", this.handleError);
            }
        },

        handleStateChanges: function (model, phase) {
            if (phase === State.INPROGRESS) {
              this.handleInprogressPhase(model)
            }else if (phase === State.READY) {
              this.dialogDfd.resolve();
            }else if (phase === State.FAILED){
              this.dialogDfd.resolve();
            }
        },

        handleInprogressPhase: function(model){
          if (model.previous("phase") === State.NOT_STARTED){
               this.dialogDfd = this.createDeferredDialog();
          }
        },

        createDeferredDialog: function(){
            var dfd = new $.Deferred();
            utils.showLoadingDialogOn(dfd, 500, true);
            return dfd;
        },

        handleError: function(){
            if (this.dialogDfd){
                this.dialogDfd.resolve();
            }
        }

    });

})(
    jQuery,
    _,
    Backbone,
    jaspersoft.components.utils,
    jaspersoft.components.State
);
