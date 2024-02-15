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
 * @author yaroslav.kovalchyk
 * @version $Id: settingsPluginMock.js 48605 2014-08-27 22:39:46Z ogavavka $
 */

define(function(require) {
    "use strict";
    var settings = {
        dataSourcePatterns: {
            "dbHost": "^[\\p{L}\\p{M}\\p{N}_.,\\-]+$",
            "dbPort": "^[\\p{L}\\p{M}\\p{N}_.,\\-]+$",
            "dbName": "^[\\p{L}\\p{M}\\p{N}\\s_.,\\-}{]+$",
            "sName": "^[\\p{L}\\p{M}\\p{N}\\s_.,\\-}{]+$",
            "driverType": "^[1|2|3|4]{1}$",
            "schemaName": "^[\\p{L}\\p{M}\\p{N}\\s_.,\\-}{]+$",
            "informixServerName": "^[\\p{L}\\p{M}\\p{N}\\s_.,\\-}{]+$",
            "dynamicUrlPartPattern": "([\\p{L}\\p{M}\\p{N}\\s\\_.,\\-}{]+)"
        },
        awsSettings: {
            "isEc2Instance":false,
            "awsRegions":[
                "us-east-1.amazonaws.com",
                "us-west-2.amazonaws.com",
                "us-west-1.amazonaws.com",
                "eu-west-1.amazonaws.com",
                "ap-southeast-1.amazonaws.com",
                "ap-southeast-2.amazonaws.com",
                "ap-northeast-1.amazonaws.com",
                "sa-east-1.amazonaws.com"
            ],
            "suppressEc2CredentialsWarnings":false,
            "productTypeIsEc2":false,
            "productTypeIsJrsAmi":false,
            "productTypeIsMpAmi":false

    },
        userTimeZones: [
            {"code":"Europe/Helsinki","description":"Eastern European Time"},
            {"code":"America/Los_Angeles","description":"Pacific Standard Time"},
            {"code":"America/Denver","description":"Mountain Standard Time"},
            {"code":"America/Chicago","description":"Central Standard Time"},
            {"code":"America/New_York","description":"Eastern Standard Time"},
            {"code":"Europe/London","description":"Greenwich Mean Time"},
            {"code":"Europe/Berlin","description":"Central European Time"},
            {"code":"Europe/Bucharest","description":"Eastern European Time"}
        ]
    };
    return {
        load: function(name, req, onLoad, config) {
            onLoad(settings[name]);
        }
    };
});