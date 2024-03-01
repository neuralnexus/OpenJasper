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

import resourcesTreeGetDataUriFnUtil from 'src/bi/repository/util/resourcesTreeGetDataUriFnUtil';

describe("resourcesTreeGetDataUriFnUtil Tests.", function() {

    it("should return uri with all params possible", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            recursive: true,
            type: ["type"],
            containerType: "containerType",
            exclude: "/exclude",
            forceTotalCount: true,
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=true&type=type&containerType=containerType&excludeFolder=/exclude&offset=0&limit=100&forceTotalCount=true&forceFullPage=true");
    });

    it("should return uri with default recursive param value", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            type: ["type"],
            containerType: "containerType",
            exclude: "/exclude",
            forceTotalCount: true,
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=false&type=type&containerType=containerType&excludeFolder=/exclude&offset=0&limit=100&forceTotalCount=true&forceFullPage=true");
    });

    it("should return uri without type param", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            recursive: true,
            containerType: "containerType",
            exclude: "/exclude",
            forceTotalCount: true,
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=true&containerType=containerType&excludeFolder=/exclude&offset=0&limit=100&forceTotalCount=true&forceFullPage=true");
    });

    it("should return uri without containerType param", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            recursive: true,
            type: ["type"],
            exclude: "/exclude",
            forceTotalCount: true,
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=true&type=type&excludeFolder=/exclude&offset=0&limit=100&forceTotalCount=true&forceFullPage=true");
    });

    it("should return uri without exclude param", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            recursive: true,
            type: ["type"],
            containerType: "containerType",
            forceTotalCount: true,
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=true&type=type&containerType=containerType&offset=0&limit=100&forceTotalCount=true&forceFullPage=true");
    });

    it("should return uri without forceTotalCount param", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            recursive: true,
            type: ["type"],
            containerType: "containerType",
            exclude: "/exclude",
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=true&type=type&containerType=containerType&excludeFolder=/exclude&offset=0&limit=100&forceFullPage=true");
    });

    it("should return uri without forceFullPage param", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            contextPath: "contextPath",
            recursive: true,
            type: ["type"],
            containerType: "containerType",
            exclude: "/exclude",
            forceTotalCount: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?folderUri=%2Fpath%2F%40to&recursive=true&type=type&containerType=containerType&excludeFolder=/exclude&offset=0&limit=100&forceTotalCount=true");
    });

    it("should return uri without folderUri param", function() {
        var uriFn = resourcesTreeGetDataUriFnUtil({
            getFolderUri: function(id) {
                return id === "/path/@to" ? "" : id;
            },
            contextPath: "contextPath",
            recursive: true,
            type: ["type"],
            containerType: "containerType",
            exclude: "/exclude",
            forceTotalCount: true,
            forceFullPage: true
        });

        expect(uriFn({
            offset: 0,
            limit: 100,
            id: "/path/@to"
        })).toEqual("contextPath/rest_v2/api/resources?&recursive=true&type=type&containerType=containerType&excludeFolder=/exclude&offset=0&limit=100&forceTotalCount=true&forceFullPage=true");
    });
});
