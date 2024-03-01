/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

export const pageComponentsMapping = {
    'reportConfig': () => import('../model/ReportConfigModel'),
    'table': () => import('../model/TableComponentModel'),
    'column': () => import('../model/ColumnComponentModel'),
    'fusionChart': () => import('../model/FusionComponentModel'),
    'fusionMap': () => import('../model/FusionComponentModel'),
    'fusionWidget': () => import('../model/FusionComponentModel'),
    'googlemap': () => import('../model/GooglemapComponentModel'),
    'tibco-maps': () => import('../model/TibcomapComponentModel'),
    'crosstab': () => import('../model/CrosstabComponentModel'),
    'webfonts': () => import('../model/WebfontsComponentModel'),
    'hyperlinks': () => import('../model/HyperlinksComponentModel'),
    'bookmarks': () => import('../model/BookmarksComponentModel'),
    'reportparts': () => import('../model/ReportPartsComponentModel'),
    'CVComponent': () => import('../model/CustomComponentModel')
};