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
 * @version: $Id: components.notificationviewtrait.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.NotificationViewTrait = (function ($, _, Backbone) {

    return Backbone.View.extend({

        initialize:function () {
            _.bindAll(this);

            if (this.collection){
                this.collection.each(this.addListeners);
            }
        },

        addListeners : function(model){
            model.on("error:server", this.handleServerError);
            model.on("notification:show", this.handleNotificationShow);
        },

        showErrorNotification: function(message, delay){
            this.showNotification(message, "error", delay);
        },

        showSuccessNotification: function(message, delay){
            this.showNotification(message,"success", delay);
        },

        showNotification: function(message, type, delay) {},

        hideNotification: function() {},

        handleServerError : function(error){
            this.showErrorNotification(error.message);
        },

        handleNotificationShow : function(data){
            this.showSuccessNotification(data.message);
        }

    })

})(
    jQuery,
    _,
    Backbone
);
