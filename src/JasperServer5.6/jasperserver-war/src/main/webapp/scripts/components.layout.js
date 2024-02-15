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
 * @version: $Id: components.layout.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.Layout = (function($,_, Backbone, components) {


    return Backbone.View.extend({

        initialize:function (args) {
            _.bindAll(this);
            this.createUIComponents(args);
        },

        render:function (options) {
            this.notificationView.render();

            if (options&&options.container) {
                $(options.container).find(".body").prepend(this.notificationView.el);
                this.defaultRender(this.formView, {container:$(options.container)});
            }else{
                this.dialogRender(this.formView, this.dialog);
                this.dialog.$el.find(".body").prepend(this.notificationView.el);
            }
            return this;
        },

        createUIComponents:function (options) {
            if (options) {
                if (options.type && options.namespace[options.type]) {
                    this.formView = new options.namespace[options.type]({model:this.model});
                }
                if (!options.container) {
                    this.dialog = new components.Dialog({modal:options.modal, templateId : "exportsDialogTemplate"});
                }
            }

            if (this.model){
                this.stateView = new components.StateView({model: this.model.get("state")});
                var collectionToListen = new Backbone.Collection([this.model, this.model.get("state")]);
                this.notificationView = new components.SystemNotificationView({collection:collectionToListen});
            }
        },

        defaultRender:function (view, options) {
            if (options && options.container) {
                view.render(options);
            }
        },

        dialogRender:function (view, dialog) {
            if (dialog && view) {
                dialog.render($("body"));
                view.render({container:dialog.$el});
                //TODO: fix it;workaround for popup dialog
                $("#exportDialog #cancelExportButton").removeClass("hidden");
            }
        },

        showDialog:function () {
            if (this.dialog) {
                this.formView.prepareToShow && this.formView.prepareToShow();
                this.notificationView.hideNotification();
                this.dialog.show();
            }
        }


    });

})(
    jQuery,
    _,
    Backbone,
    jaspersoft.components
);
