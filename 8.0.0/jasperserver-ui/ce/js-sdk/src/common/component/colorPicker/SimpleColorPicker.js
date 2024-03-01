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

import Backbone from 'backbone';
import _ from 'underscore';
import $ from 'jquery';
import i18n from '../../../i18n/CommonBundle.properties';
import colors from './enum/colors';
import template from './template/simpleColorPickerTemplate.htm';

export default Backbone.View.extend({
    events: { 'click .color': '_selectColor' },
    constructor: function (options) {
        options || (options = {});
        this.label = options && options.label;
        this.showTransparentInput = options && options.showTransparentInput;
        this.showNoneInput = options && options.showNoneInput;
        Backbone.View.apply(this, arguments);
    },
    el: function () {
        return _.template(template)({
            colors: colors,
            i18n: i18n,
            label: this.label,
            showTransparentInput: this.showTransparentInput,
            showNoneInput: this.showNoneInput
        });
    },
    highlightColor: function (color) {
        var colorBox, index;
        if (color === null) {
            colorBox = this.$el.find('.color.none');
        } else if (color === 'rgba(0, 0, 0, 0)' || color === 'transparent') {
            colorBox = this.$el.find('.color.transparent');
        } else {
            index = _.indexOf(colors, color);
            index >= 0 && (colorBox = this.$el.find('div[data-index=\'' + index + '\']'));
        }
        this.$el.find('.color.transparent.selected, .color.none.selected, .colorWrapper.selected').removeClass('selected');
        colorBox && colorBox.addClass('selected');
    },
    _selectColor: function (event) {
        var colorEl = $(event.target), color;
        if (colorEl.is('.none')) {
            color = null;
        } else {
            color = colorEl.css('background-color');
        }
        this.highlightColor(color);
        this.trigger('color:selected', color);
    },
    show: function () {
        this.$el.show();
    },
    hide: function () {
        this.$el.hide();
    }
});