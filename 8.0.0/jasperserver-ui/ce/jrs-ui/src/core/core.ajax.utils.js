/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved.
 * http://www.jaspersoft.com.
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import dialogs from "../components/components.dialogs";
import {JRS} from "../namespace/namespace";
import jQuery from 'jquery';

function showErrorPopup(errorMessage, options) {
    if ((errorMessage && errorMessage.indexOf('sessionAttributeMissingException')) > -1) {
        dialogs.clusterErrorPopup.show(errorMessage);
    } else {
        dialogs.errorPopup.show(errorMessage, false, options);
    }
}
function baseErrorHandler(ajaxAgent) {
    //Handling HTTP 500 - Internal server error
    if (ajaxAgent.status == 500) {
        showErrorPopup(ajaxAgent.responseText);
        return true;
    }
    var sessionTimeout = ajaxAgent.getResponseHeader('LoginRequested');
    if (sessionTimeout) {
        var newloc = '.';
        document.location = newloc;
        return true;
    }
    var isErrorPage = ajaxAgent.getResponseHeader('JasperServerError');
    if (isErrorPage) {
        var suppressError = ajaxAgent.getResponseHeader('SuppressError');
        if (!suppressError) {
            // For dashboard frame we should render error message as frame content.
            var dashboardViewFrame = jQuery('.dashboardViewFrame');
            if (dashboardViewFrame.length == 1) {
                jQuery(document.body).html(ajaxAgent.responseText);
                var iFrame = jQuery('#' + JRS.fid, window.parent.document);
                iFrame.removeClass('hidden').show();
            } else {
                // In other cases we render error message as popup.
                showErrorPopup(ajaxAgent.responseText);
            }
        }
        return true;
    }
    return false;
}

function errorHandler() {}
function showMessageDialog() {}

export {
    showErrorPopup,
    baseErrorHandler,
    errorHandler,
    showMessageDialog
}