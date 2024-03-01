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

import $ from 'jquery';
var $element1 = $('<li></li>');
var $element2 = $('<li></li>');
var $element3 = $('<li></li>');
var items = [
    {
        DEFAULT_ITEM_ID_PREFIX: 'item1',
        DEFAULT_SUB_LIST_ID_SUFFIX: 'SubList',
        DEFAULT_TEMPLATE_DOM_ID: 'dynamicListItemTemplate',
        _label: 'Sort By:',
        _respondOnItemEvents: false,
        _templateDomId: 'tabSet_control_horizontal_responsive:label',
        _value: {},
        first: true,
        last: false,
        isComposite: true,
        refreshStyle: function () {
        },
        setList: function (list) {
            this._list = list;
        },
        getList: function () {
            return this._list;
        },
        _getElement: function () {
            return $element1;
        },
        select: function () {
        },
        deselect: function () {
        },
        getFirstChild: function () {
            return items[1];
        },
        show: function () {
        },
        refresh: function () {
        },
        isRendered: function () {
        },
        index: function () {
            return 0;
        }
    },
    {
        DEFAULT_ITEM_ID_PREFIX: 'item2',
        DEFAULT_SUB_LIST_ID_SUFFIX: 'SubList',
        DEFAULT_TEMPLATE_DOM_ID: 'dynamicListItemTemplate',
        _label: 'Sort By:',
        _respondOnItemEvents: false,
        _templateDomId: 'tabSet_control_horizontal_responsive:label',
        _value: {},
        first: false,
        last: false,
        refreshStyle: function () {
        },
        setList: function (list) {
            this._list = list;
        },
        getList: function () {
            return this._list;
        },
        _getElement: function () {
            return $element2;
        },
        select: function () {
        },
        deselect: function () {
        },
        show: function () {
        },
        refresh: function () {
        },
        isRendered: function () {
        },
        index: function () {
            return 1;
        }
    },
    {
        DEFAULT_ITEM_ID_PREFIX: 'item3',
        DEFAULT_SUB_LIST_ID_SUFFIX: 'SubList',
        DEFAULT_TEMPLATE_DOM_ID: 'dynamicListItemTemplate',
        _label: 'Sort By:',
        _respondOnItemEvents: false,
        _templateDomId: 'tabSet_control_horizontal_responsive:label',
        _value: {},
        first: false,
        last: true,
        refreshStyle: function () {
        },
        setList: function (list) {
            this._list = list;
        },
        getList: function () {
            return this._list;
        },
        _getElement: function () {
            return $element3;
        },
        select: function () {
        },
        deselect: function () {
        },
        show: function () {
        },
        refresh: function () {
        },
        isRendered: function () {
        },
        index: function () {
            return 2;
        }
    }
];
export default items;