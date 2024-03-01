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

/* global $ */

// This extension is used as a dependency by files which are not AMD modules.
// They use $ from the global scope, before body of this amd module will be executed.
// Legacy code expects $ from the global scope to be Prototype.js not jQuery.
// So we have to call noConflict outside define callback.
if (typeof $ !== "undefined" && typeof $.noConflict === "function") {
    $.noConflict();
}

define(function(require) {

    var $ = require('jquery'),
        jqueryExtension = require('runtime_dependencies/js-sdk/src/common/extension/jqueryExtension');

    // do not extend during build
    if (typeof $ !== 'string') {
        jqueryExtension.extend($);

        $.noConflict();
    }

    return $;
});