/*
 * Copyright (C) 2005 - 2020 TIBCO Software Inc. All rights reserved. Confidentiality & Proprietary.
 * Licensed pursuant to commercial TIBCO End User License Agreement.
 */

/**
 * @author Taras Bidyuk
 */

import _ from 'underscore';
import i18n from '../../../../i18n/jasperreports_messages.properties';
import jiveTypes from '../enum/jiveTypes';
import sortOrder from '../enum/sortOrder';
import jiveActions from '../enum/jiveActions';
import HeaderToolbarView from '../view/overlay/HeaderToolbarView';

var typeToToolbarOptionsMap = {};
var noop = function () {};

function generateOptions(type, options) {
    typeToToolbarOptionsMap[jiveTypes.CROSSTAB] = {
        buttons: [
            {
                icon: 'sortAscIcon',
                title: i18n['net.sf.jasperreports.components.headertoolbar.label.sortasc'],
                message: jiveActions.SORT,
                order: sortOrder.ASC,
                action: 'select'
            },
            {
                icon: 'sortDescIcon',
                title: i18n['net.sf.jasperreports.components.headertoolbar.label.sortdesc'],
                message: jiveActions.SORT,
                order: sortOrder.DESC,
                action: 'select'
            }
        ]
    };
    typeToToolbarOptionsMap[jiveTypes.TABLE] = {
        buttons: [
            {
                title: i18n['net.sf.jasperreports.components.headertoolbar.condition.format'],
                icon: 'formatIcon',
                hoverMenuOptions: [
                    {
                        message: jiveActions.FORMAT,
                        label: i18n['net.sf.jasperreports.components.headertoolbar.label.formatting'],
                        action: 'select'
                    },
                    {
                        message: jiveActions.HIDE_COLUMN,
                        label: i18n["net.sf.jasperreports.components.headertoolbar.label.hidecolumn"],
                        test: options.hideColumnOptionTestFn || noop,
                        action: "select"
                    },
                    {
                        message: jiveActions.SHOW_COLUMN,
                        label: i18n["net.sf.jasperreports.components.headertoolbar.label.showcolumns"],
                        test: options.showColumnsOptionTestFn || noop,
                        children: options.children || []
                    }
                ]
            },
            {
                title: i18n['net.sf.jasperreports.components.headertoolbar.label.columnfilters'],
                icon: 'filterIcon',
                message: jiveActions.FILTER,
                action: 'filter'
            },
            {
                title: i18n['net.sf.jasperreports.components.headertoolbar.label.sortasc'],
                icon: 'sortAscIcon',
                message: jiveActions.SORT,
                order: 'Asc',
                action: 'sortAsc'
            },
            {
                title: i18n['net.sf.jasperreports.components.headertoolbar.label.sortdesc'],
                icon: 'sortDescIcon',
                message: jiveActions.SORT,
                order: 'Desc',
                action: 'sortDesc'
            }
        ]
    };
    return typeToToolbarOptionsMap[type];
}

export default function (type, options) {
    options = _.extend({}, generateOptions(type, options), options);
    return new HeaderToolbarView(options);
}