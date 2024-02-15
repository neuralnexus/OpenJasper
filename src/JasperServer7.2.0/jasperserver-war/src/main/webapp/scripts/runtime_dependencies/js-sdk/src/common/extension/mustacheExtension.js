/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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


/**
 * @author: dlitvak
 */

define(['mustache.original', 'jrs.configs'], function (Mustache, jrsConfigs) {
    "use strict";

    var jsNonceTag = RegExp(/(<js-templateNonce>\s*<\/js-templateNonce>)|(<js-templateNonce\s*\/>)/gi);
    var htmlNonce = "<!-- " + jrsConfigs.xssNonce + " (xss nonce htm) -->";

    var originalTo_Html = Mustache.to_html;

    /*
    Substitute <js-templateNonce></js-templateNonce> with the current session nonce.
     */
    Mustache.to_html = function(template, view, partials, send_fun) {
        var html = originalTo_Html.call(this, template, view, partials, send_fun);

        return html ? html.replace(jsNonceTag, htmlNonce) : html;
    };

    return Mustache;
});
