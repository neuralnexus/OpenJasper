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
 * @author Sergey Prilukin
 * @version: $Id: AvailableItemsListWithTrueAllNew.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * AvailableItems list. Part of MultiSelect.
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        AvailableItemsListWithTrueAll = require("common/component/multiSelect/view/AvailableItemsListWithTrueAll"),
        AvailableItemsListNew = require("common/component/multiSelect/view/new/AvailableItemsListNew"),
        AvailableItemsList = require("common/component/multiSelect/view/AvailableItemsList"),
        listWithTrueAllSelectionTrait = require("common/component/list/view/listWithTrueAllSelectionTrait");

    var AvailableItemsListWithTrueAllNew = AvailableItemsListWithTrueAll.extend({
        _createListViewModel: AvailableItemsListNew.prototype._createListViewModel,

        _createListView: function(options) {
            var listView = AvailableItemsList.prototype._createListView.call(this, options);

            return _.extend(listView, listWithTrueAllSelectionTrait);
        },

        /* Event Handlers */

        selectionAdd:  AvailableItemsListNew.prototype.selectionAdd,

        selectionAddRange:  AvailableItemsListNew.prototype.selectionAddRange,

        selectionRemove:  AvailableItemsListNew.prototype.selectionRemove,

        selectionClear:  AvailableItemsListNew.prototype.selectionClear,

        /* Key handlers for KeyboardManager */

        /* Internal helper methods */

        setTrueAllForListView: function(all) {
            this.listView.setTrueAll(all);
        },

        changeFilter:  AvailableItemsListNew.prototype.changeFilter,

        processSelectionThroughApi:  AvailableItemsListNew.prototype.processSelectionThroughApi,


        convertSelectionForListViewModel:  AvailableItemsListNew.prototype.convertSelectionForListViewModel,

        /* Internal methods */
        //none

        /* API */

        getValue:  AvailableItemsListNew.prototype.getValue,

        setValue:  AvailableItemsListNew.prototype.setValue
    });

    return AvailableItemsListWithTrueAllNew;
});

