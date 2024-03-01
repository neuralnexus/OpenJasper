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
import $ from 'jquery';
import _ from 'underscore';
import configs from 'js-sdk/src/jrs.configs';
import requestRegistry from 'js-sdk/src/common/transport/requestRegistry';
import requestSettingsDefault from '../config/requestSettings';
import logger from 'js-sdk/src/common/logging/logger';

var log = logger.register("request");

export const request = function () {
    const requestSettings = requestSettingsDefault();

    // workaround for optimizer which usually runs in Node env without document defined
    if (typeof document === "undefined" || window === "undefined") {
        return {};
    }


    function getOriginFromUrl(url) {
        var a = document.createElement('a');
        a.href = url;

        return a.origin || (a.protocol + "//" + a.host);
    }

    // configure CORS
    var currentUri = window.location.href;
    var origin = getOriginFromUrl(window.location.href);


    // TODO: observableRequest functionality is temporary here. This should be revised after Amber release.

    var $document = $(document);
    function isSameOrigin(serverOrigin) {
        return origin === serverOrigin;
    }

    function isSameDomainWithDifferentPath(serverOrigin) {
        return currentUri.search(serverOrigin + configs.urlContext) < 0;
    }


    function triggerEvent() {
        $document.trigger.apply($document, arguments);
    }

    function checkSessionExpiration(xhr) {
        //TODO: custom error handlers should be used instead
        if (xhr.status == 401 || xhr.getResponseHeader("LoginRequested")) {
            //trigger custom event to allow do something before page reloading
            $(window).trigger("sessionExpired");
            log.warn("Session timed-out. Redirecting to login page.");
            window.location.reload();
        }
    }

    _.partial(triggerEvent, "request:before").apply(null, arguments);
    var mergedOptions = _.extend({}, requestSettings, arguments[0]);
    var serverOrigin = getOriginFromUrl(mergedOptions.url);
    if (requestSettings.headers && arguments[0].headers) {
        mergedOptions.headers = _.extend({}, requestSettings.headers, arguments[0].headers);
    }
    if (!isSameOrigin(serverOrigin)) {
        mergedOptions.xhrFields = {withCredentials: true};
        mergedOptions.crossDomain = true;
        mergedOptions.headers["X-Remote-Domain"] = origin;
    } else if (isSameDomainWithDifferentPath(serverOrigin)) {
        mergedOptions.headers["X-Remote-Domain"] = origin + "/" + currentUri.split("/")[3];
    }


    return $.ajax(mergedOptions)
        .fail(isSameOrigin(serverOrigin) && checkSessionExpiration)
        .fail(function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.getResponseHeader("adhocException")) {
                log.error(jqXHR.getResponseHeader("adhocException"));
            } else if (jqXHR.status == 500 || (jqXHR.getResponseHeader("JasperServerError") && !jqXHR.getResponseHeader("SuppressError"))) {
                log.error(jqXHR.responseText);
            }
        })
        .fail(_.partial(triggerEvent, "request:failure"))
        .done(_.partial(triggerEvent, "request:success"))
        .done(arguments[1]);
}

export default () => {
    // set request to registry so now everyone who uses js-sdk/src/common/transport/request will use
    // this request function from the registry
    requestRegistry.request = request;
};