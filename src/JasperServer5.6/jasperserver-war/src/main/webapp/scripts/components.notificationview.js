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
 * @version: $Id: components.notificationview.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.NotificationView = (function (Export, $, _, Backbone, TemplateEngine, components) {

    var constants = {
        DEFAULT_TEMPLATE_ID : "componentsNotificationTemplate"
    };

    return components.NotificationViewTrait.extend({

        initialize:function () {
            _.bindAll(this);

            //super
            components.NotificationViewTrait.prototype.initialize.call(this, arguments);

            var templateFunction = TemplateEngine.createTemplate(components.NotificationView.DEFAULT_TEMPLATE_ID);
            if (!templateFunction){
                throw Error("Not found template by id '{0}'".replace("{0}", components.NotificationView.DEFAULT_TEMPLATE_ID));
            }
            this.notificationTemplate = templateFunction;
        },

        render:function () {
            var notificationHtml = this.notificationTemplate();
            this.$el = $(notificationHtml);
            this.el = this.$el[0];
            return this;
        },

        showNotification: function(message, type, delay) {
            this.hideNotification();
            this.hideTimer && clearTimeout(this.hideTimer);
            this.$el.addClass(type ? type : "success").find(".message").text(message);
            if (delay){
                this.hideTimer = setTimeout(this.hideNotification, delay);
            }
        },

        hideNotification: function() {
            this.$el.removeClass("success").removeClass("error")
        }

    }, constants)

})(
    JRS.Export,
    jQuery,
    _,
    Backbone,
    jaspersoft.components.templateEngine,
    jaspersoft.components
);
