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
import i18n from '../../i18n/AttributesBundle.properties';
import attributesTypesEnum from '../../attributes/enum/attributesTypesEnum';
import BaseTable from 'js-sdk/src/common/component/baseTable/BaseTable';
import tableTemplatesFactory from '../../attributes/factory/tableTemplatesFactory';

var AttributesViewer = BaseTable.extend({
    className: 'attributesViewer',
    templateHelpers: function () {
        return {
            i18n: i18n,
            type: this.type,
            types: attributesTypesEnum
        };
    },
    initialize: function (options) {
        options = options || {};
        this.type = options.type;
        this.childViewOptions = _.extend({}, options.childViewOptions, {type: this.type});
        this.$container = $(options.$container);
        BaseTable.prototype.initialize.apply(this, arguments);
        this.tooltip && this._initTooltipEvents();
    },
    render: function () {
        BaseTable.prototype.render.apply(this, arguments);
        this.$container.append(this.$el);
        return this;
    },
    setContext: function (value, refreshAttributes) {
        return this.collection.setContext(value, refreshAttributes);
    },
    show: function () {
        this.$el.show();
    },
    hide: function () {
        this.$el.hide();
    },
    getTemplate: function () {
        var template = tableTemplatesFactory({
            readOnly: true,
            empty: !this.collection.models.length
        });
        return _.template(template);
    },
    _initTooltipEvents: function () {
        this.listenTo(this, 'childview:mouseover', this._onChildViewMouseOver);
        this.listenTo(this, 'childview:mouseout', this._onChildViewMouseOut);
    },
    _onChildViewMouseOver: function (view, model, e) {
        var $parentTableColumn = $(e.target).closest('.table-column');
        if ($parentTableColumn.hasClass('name')) {
            model = _.pick(model.toJSON(), [
                'name',
                'description'
            ]);
        }
        if ($parentTableColumn.hasClass('value')) {
            model = _.pick(model.toJSON(), 'value');
        }
        this.tooltip.show(model);
    },
    _onChildViewMouseOut: function (view, model, e) {
        this.tooltip.hide();
    }
});
export default AttributesViewer;