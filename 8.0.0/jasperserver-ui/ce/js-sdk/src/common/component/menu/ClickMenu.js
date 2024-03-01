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

import Menu from './Menu';
import ClickComponent from '../base/ClickComponent';
export default Menu.extend(ClickComponent.extend({
    constructor: function (options, attachTo, additionalSettings) {
        additionalSettings || (additionalSettings = {});

        const clickComponentAdditionalSettings = Object.assign({toggleMode: additionalSettings.menuToggleMode}, additionalSettings);

        ClickComponent.call(this, attachTo, additionalSettings.padding, clickComponentAdditionalSettings);
        try {
            Menu.call(this, options, additionalSettings);
        } catch (e) {
            ClickComponent.prototype.remove.apply(this, arguments);
            throw e;
        }
    },
    show: function () {
        ClickComponent.prototype.show.apply(this, arguments);
        return Menu.prototype.show.apply(this, arguments);
    },
    remove: function () {
        ClickComponent.prototype.remove.apply(this, arguments);
        Menu.prototype.remove.apply(this, arguments);
    }
}).prototype);