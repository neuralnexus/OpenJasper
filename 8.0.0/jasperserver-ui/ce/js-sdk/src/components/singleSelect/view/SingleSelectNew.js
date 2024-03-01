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
import listWithNavigationModelTrait from '../../scalableList/model/listWithNavigationModelTrait';
import SingleSelect from './SingleSelect';
import SingleSelectListModelNew from '../model/SingleSelectListModelNew';
function afterSelect(getSelectionIndex, selection) {
    if (selection && typeof selection === 'string') {
        if (!this.getActive()) {
            var index = getSelectionIndex(selection);
            if (typeof index !== 'undefined') {
                listWithNavigationModelTrait.setActive.call(this, {
                    index: index,
                    item: { value: selection },
                    silent: true
                });
            }
        }
    }
}
var SingleSelectNew = SingleSelect.extend({
    initialize: function (options) {
        _.bindAll(this, '_getSelectionIndex');
        this.formatValue = options.formatValue;
        SingleSelect.prototype.initialize.call(this, options);
    },
    _createListViewModel: function (options) {
        this.getData = options.getData;
        return options.model || _.extend(new SingleSelectListModelNew(options), { _afterSelect: _.partial(afterSelect, this._getSelectionIndex) });
    },
    _getSelectionIndex: function (selection) {
        var bufferStartIndex = this.listViewModel.get('bufferStartIndex');
        var items = this.listViewModel.get('items');
        for (var i = 0; i < items.length; i++) {
            if (selection === items[i].value) {
                return bufferStartIndex + i;
            }
        }
        return undefined;
    },
    _getGetData: function () {
        return this.getData;
    },
    _getActiveValueIndex: function (active) {
        return this._getSelectionIndex(active.value);
    },
    changeFilter: function () {
        var self = this;
        var criteria = this.model.get('criteria');
        this.listView.scrollTo(0);
        this._getGetData()({
            criteria: criteria,
            offset: 0,
            limit: 1
        }).done(function () {
            self.listView.fetch(function () {
                self.listView.resize();
                self.setValueToList();
            });
        });
    },
    getControlLabelByValue: function (value) {
        return value.label == null ? this.formatValue ? this.formatValue(value.value) : value.value : value.label;
    },
    setValue: function (value, options) {
        value = this.convertExternalValueToInternalFormat(value);
        if (options && options.silent) {
            this.silent = true;
        }
        this.model.set('value', value);
        this.setValueToList(options);
        if(this.silent) {
            delete this.silent;
        }
    },
    setValueToList: function (options) {
        var selection = this.model.get('value'), value = {};
        if (selection && selection.index) {
            value[selection.index] = selection.value;
        } else if (selection) {
            value = selection.value;
        }
        this.listView.setValue(value, options);
    }
});
export default SingleSelectNew;