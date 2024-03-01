/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

function createKey(dataType, operator) {
    return `net.sf.jasperreports.components.sort.FilterType${dataType}OperatorsEnum.${operator}`;
}

export function getFilterOperatorKey(dataType, key) {
    let i18nKey;

    switch(dataType) {
    case "numeric":
        i18nKey = createKey("Numeric", key);
        break;
    case "date":
    case "time":
        i18nKey = createKey("Date", key);
        break;
    case "text":
        i18nKey = createKey("Text", key);
        break;
    case "boolean":
        i18nKey = createKey("Boolean", key);
        break;
    default:
        i18nKey = key;
    }

    return i18nKey;
}