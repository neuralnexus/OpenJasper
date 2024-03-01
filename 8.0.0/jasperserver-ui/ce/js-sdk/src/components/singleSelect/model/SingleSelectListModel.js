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

import _ from 'underscore';
import ListWithSelectionModel from '../../scalableList/model/ListWithSelectionModel';
import listWithNavigationModelTrait from '../../scalableList/model/listWithNavigationModelTrait';
var SingleSelectListModel = ListWithSelectionModel.extend(listWithNavigationModelTrait);
SingleSelectListModel = SingleSelectListModel.extend({
    setActive: function (options) {
        listWithNavigationModelTrait.setActive.call(this, _.extend(options, { silent: true }));
        var selection = {};
        if (typeof options.index === 'number') {
            selection[options.index] = options.item.value;
        }
        this.select(selection);
    },
    _convertInitialArrayToSelection: function (data, pendingSelection, options) {
        for (var i = 0; i < data.length; i++) {
            if (pendingSelection[data[i].value]) {
                this._addToSelection(data[i].value, i);
                if (!this.getActive()) {
                    listWithNavigationModelTrait.setActive.call(this, {
                        index: i,
                        item: data[i],
                        silent: true
                    });
                }
            }
        }
        this._triggerSelectionChange(options);
    },
    _convertSelectionObject: function (selection) {
        for (var index in selection) {
            if (selection.hasOwnProperty(index) && selection[index] !== undefined) {
                var value = selection[index];
                return {
                    index: parseInt(index, 10),
                    value: value
                };
            }
        }
    },
    _select: ListWithSelectionModel.prototype.select,
    select: function (selection, options) {
        var convertedSelection;
        var shouldFetchLabel = false;
        if (!selection || typeof selection === 'string' || _.isArray(selection)) {
            listWithNavigationModelTrait.setActive.call(this, { silent: true });
        } else {
            convertedSelection = this._convertSelectionObject(selection);
            if (!convertedSelection) {
                listWithNavigationModelTrait.setActive.call(this, { silent: true });
            } else {
                var active = this.getActive();
                if (!active || convertedSelection.value !== active.value || convertedSelection.index !== active.index) {
                    shouldFetchLabel = true;
                }
            }
        }
        if (shouldFetchLabel) {
            this.activate(convertedSelection.index);
        } else {
            this._select(selection, options);
        }
        return this;
    }
});
export default SingleSelectListModel;