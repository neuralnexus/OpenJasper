/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

export const jiveComponentViewMapping = {
    'fusionMap': () => import('./FusionComponentView'),
    'fusionChart': () => import('./FusionComponentView'),
    'fusionWidget': () => import('./FusionComponentView'),
    'googlemap': () => import('./GooglemapComponentView'),
    'tibco-maps': () => import('./TibcomapComponentView'),
    'CVComponent': () => import('./CustomJiveComponentView'),
    'table': () => import('./TableJiveComponentView'),
    'crosstab': () => import('./CrosstabJiveComponentView')
};