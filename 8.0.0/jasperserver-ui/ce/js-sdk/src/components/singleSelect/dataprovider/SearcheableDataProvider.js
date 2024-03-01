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
import _ from 'underscore';
var SearcheableDataProvider = function (options) {
    _.bindAll(this, 'getData', '_filterValues', '_shouldFilter');
    this.indexMapping = {};
    this.internalGetData = options.getData;
};
_.extend(SearcheableDataProvider.prototype, {
    getData: function (options) {
        var deferred = new $.Deferred();
        if (options && typeof options.criteria !== 'undefined') {
            this.dataCache = null;
            this.criteria = options.criteria;
            deferred.resolve();
            return deferred.promise();
        }
        let that = this;

        if (that._shouldFilter()) {
            if (this.dataCache) {
                this._resolvePromise(options, deferred);
            } else {
                this.internalGetData().done(function (values) {
                    that.indexMapping = {};
                    that.reverseIndexMapping = {};
                    that.dataCache = that._filterValues(values.data);
                    that._resolvePromise(options, deferred);
                }).fail(deferred.reject);
            }
            return deferred.promise();
        } else {
            this.indexMapping = null;
            this.reverseIndexMapping = null;
            this.dataCache = null;
            return this.internalGetData(options);
        }
    },
    getIndexMapping: function () {
        return this.indexMapping;
    },
    getReverseIndexMapping: function () {
        return this.reverseIndexMapping;
    },
    _resolvePromise: function (options, deferred) {
        var first = options && options.offset ? options.offset : 0;
        var last = options && options.limit ? first + options.limit : this.dataCache.length;
        var dataSegment = this._getDataSegment(this.dataCache, first, last);
        deferred.resolve({
            data: dataSegment,
            total: this.dataCache.length
        });
    },
    _shouldFilter: function () {
        return this.criteria;
    },
    _filterValues: function (data) {
        var filteredData = [];
        var j = 0;
        for (var i = 0; i < data.length; i++) {
            var value = data[i];
            var searchIn = value.label === undefined ? value.value : value.label;
            if (searchIn.toLowerCase().indexOf(this.criteria.toLowerCase()) > -1) {
                filteredData[j] = value;
                this.indexMapping[j] = i;
                this.reverseIndexMapping[i] = j;
                j += 1;
            }
        }
        return filteredData;
    },
    _getDataSegment: function (values, first, last) {
        last = Math.min(last, values.length);
        var result = [];
        for (var i = first; i < last; i++) {
            result.push({
                label: values[i].label,
                value: values[i].value
            });
        }
        return result;
    }
});
export default SearcheableDataProvider;