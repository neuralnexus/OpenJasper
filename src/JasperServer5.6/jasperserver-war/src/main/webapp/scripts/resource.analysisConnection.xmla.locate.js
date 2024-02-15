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
 * @version: $Id: resource.analysisConnection.xmla.locate.js 47331 2014-07-18 09:13:06Z kklein $
 */

var resourceOLAPLocate = {
    FRAME_ID : "frame",
    TYPE_ID : "type",
    messages:[],

    initialize: function() {
        var connectionTypeElement = $(this.TYPE_ID);
        resourceLocator.initialize({
            resourceInput : 'resourceUri',
            browseButton : 'browser_button',
            treeId : 'OLAPTreeRepoLocation',
            providerId : 'XMLATreeDataProvider',
            dialogTitle : resourceOLAPLocate.messages["resource.AnalysisConnectionXmlaLocate.Title"],
            selectLeavesOnly: true
        });

        connectionTypeElement.observe('change', function() {
            $("chooseType").click();
        });
    }
};

if (typeof require === "undefined") {
    document.observe('dom:loaded', function() {
        resourceOLAPLocate.initialize();
    });
}
