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
 * @version: $Id: SelectedItemsDataProvider.js 47805 2014-08-05 08:57:58Z sergey.prilukin $
 */

/**
 * DataProvider proxy for SelectedItemsList
 */

define(function (require) {
    'use strict';

    var _ = require("underscore"),
        SearcheableDataProvider = require("common/component/singleSelect/dataprovider/SearcheableDataProvider");

    var SelectedItemsDataProvider = function(options) {
        this.selectedData = options.selectedData || [];
        SearcheableDataProvider.call(this, options);
    };

    _.extend(SelectedItemsDataProvider.prototype, SearcheableDataProvider.prototype);

    _.extend(SelectedItemsDataProvider.prototype, {

        /* API */

        getData: function(options) {
            if (options && options.criteria) {
                delete options.criteria;
            }

            return SearcheableDataProvider.prototype.getData.call(this, options);
        },

        setSelectedData: function(selectedData) {
            this.dataCache = null;
            this.selectedData = selectedData;
        },

        getIndexMapping: function() {
            return SearcheableDataProvider.prototype.getIndexMapping.call(this);
        },

        getReverseIndexMapping: function() {
            return SearcheableDataProvider.prototype.getReverseIndexMapping.call(this);
        },

        /* Internal methods */

        _shouldFilter: function() {
            return true;
        },

        _filterValues: function(data) {
            var filteredData = [];

            var j = 0;
            for (var i = 0; i < data.length; i++) {
                var value = data[i];

                if (this.selectedData[i] !== undefined) {
                    filteredData[j] = value;
                    this.indexMapping[j] = i;
                    this.reverseIndexMapping[i] = j;
                    j += 1;
                }
            }

            return filteredData;
        }
    });

    return SelectedItemsDataProvider;
});
