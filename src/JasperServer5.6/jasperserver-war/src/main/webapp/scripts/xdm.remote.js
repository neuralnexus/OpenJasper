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
 * @version: $Id: xdm.remote.js 47331 2014-07-18 09:13:06Z kklein $
 */

define(function(require) {
    var $ = require("jquery"),
        xdm = require("xdm");

    var remote = new xdm.Rpc(
        {
            acl: ['*']
        },
        {
            local: {
                // define the exposed method
                request: function (config, success, error) {

                    $.ajax(config).then(function(data, status, jqXhr) {
                        // TODO: this is hack to fix xdm issue with transmitting more than one callback params. I'm packing all params to single one
                        jqXhr.responseHeadersString = jqXhr.getAllResponseHeaders();
                        success({
                            data: data,
                            status: status,
                            xhr: jqXhr
                        });
                    }, error);
                }
            }
        });

    return remote;
});