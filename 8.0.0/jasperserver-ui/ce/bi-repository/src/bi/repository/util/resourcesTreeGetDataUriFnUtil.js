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

import _ from 'underscore';

export default function(options) {

    return function(params) {
        var uri = "";

        uri += options.contextPath + "/rest_v2/api/resources?";

        if (options.getFolderUri) {
            var folderUri = options.getFolderUri(params.id);

            if (folderUri) {
                uri += "folderUri=" + encodeURIComponent(folderUri);
            }
        } else {
            uri += "folderUri=" + encodeURIComponent(params.id);
        }

        if (options.recursive) {
            uri += "&recursive=true";
        } else {
            uri += "&recursive=false";
        }

        if (options.type) {
            uri += options.type.reduce(function(memo, type) {
                memo += "&type=" + type;

                return memo;
            }, "");
        }

        if (options.containerType) {
            uri += "&containerType=" + options.containerType;
        }

        if (options.exclude) {
            uri += "&excludeFolder=" + options.exclude;
        }

        uri += "&offset={{= offset }}&limit={{= limit }}";

        if (options.forceTotalCount) {
            uri += "&forceTotalCount=true";
        }

        if (options.forceFullPage) {
            uri += "&forceFullPage=true";
        }

        return _.template(uri, params);
    }
}
