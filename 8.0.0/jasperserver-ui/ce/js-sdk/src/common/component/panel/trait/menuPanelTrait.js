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
import $ from 'jquery';
import abstractPanelTrait from './abstractPanelTrait';
import ClickMenu from '../../menu/ClickMenu';
import groupMenuTrait from '../../menu/groupMenuTrait';
import menuPanelMarkup from '../template/menuPanelTemplate.htm';
var GroupMenu = ClickMenu.extend(groupMenuTrait);
export default _.extend({}, abstractPanelTrait, {
    onConstructor: function (options) {
        options || (options = {});
        this.menuOptions = options.menuOptions;
        this.menuOptionSelectable = options.menuOptionSelectable;
        this.menuPadding = options.menuPadding;
        this.menuToggleMode = options.menuToggleMode;
    },
    afterSetElement: function () {
        this.$menuEl = $(menuPanelMarkup);
        this.$el.find('.title').after(this.$menuEl);
        this.filterMenu = new GroupMenu(this.menuOptions, this.$menuEl, {
            toggle: this.menuOptionSelectable,
            toggleClass: 'active',
            padding: this.menuPadding,
            menuToggleMode: this.menuToggleMode
        });
        this.listenTo(this.filterMenu, 'all', function (name, view, model) {
            if (name.indexOf(this.filterMenu.contextName) >= 0) {
                this.filterMenu.hide();
                this.trigger(name, view, model);
            }
        }, this);
    },
    onRemove: function () {
        this.filterMenu.remove();
    }
});