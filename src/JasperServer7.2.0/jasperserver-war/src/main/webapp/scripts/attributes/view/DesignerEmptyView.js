/*
 * Copyright (C) 2005 - 2019 TIBCO Software Inc. All rights reserved.
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

/**
 * @author: Olesya Bobruyko
 * @version: $Id$
 */

define(function(require) {
    var _ = require("underscore"),
        Marionette = require("backbone.marionette"),
        attributesTypesEnum = require("attributes/enum/attributesTypesEnum"),
        rowTemplatesFactory = require("attributes/factory/rowTemplatesFactory");


    var EmptyView = Marionette.ItemView.extend({
        className: "table-row",

        templateHelpers: function() {
            return {
                type: this.type,
                types: attributesTypesEnum
            };
        },

        initialize: function(options) {
            options = options || {};

            this.type = options.type;
            this.template = _.template(rowTemplatesFactory({empty: true}));
        }
    });

    return EmptyView;
});