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
 * @author: ztomchenco
 * @version: $Id: import.app.js 47331 2014-07-18 09:13:06Z kklein $
 */

JRS.Import.App = (function(importz, $,_, components) {

//module:
//
//summary:
//
    var ImportStateController = Backbone.View.extend(components.StateControllerTrait);

    return {

        initialize : function(args){

            _.bindAll(this);
            this.formModel = new importz.FormModel();
            var ui = this.ui = new components.Layout(_.extend({model: this.formModel}, args));
            $(document).ready(function(){
                ui.render(args);
            });

            var state = this.formModel.get("state");
            this.stateController = new ImportStateController({
                model: state,
                timeout: importz.configs.TIMEOUT,
                delay: importz.configs.DELAY
            });
        }

    }

})(
    JRS.Import,
    jQuery,
    _,
    jaspersoft.components
);
