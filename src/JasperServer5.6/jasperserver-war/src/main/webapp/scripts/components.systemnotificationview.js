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
 * @version: $Id: components.systemnotificationview.js 47331 2014-07-18 09:13:06Z kklein $
 */

jaspersoft.components.SystemNotificationView = (function (components, dialogs) {

    return components.NotificationViewTrait.extend({

        //setup big delay to simulate infinity mode for dialog.systemConfirm
        PSEUDO_INFINITY_DELAY : 9999999999,

        showNotification: function(message) {
            //just delegate to existed one
            dialogs.systemConfirm.show(message, this.PSEUDO_INFINITY_DELAY);
        }

    })

})(
    jaspersoft.components,
    dialogs
);
