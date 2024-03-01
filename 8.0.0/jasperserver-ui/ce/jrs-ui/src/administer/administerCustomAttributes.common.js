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

import $ from 'jquery';
import logging from '../administer/administer.logging';
import Administer from '../administer/administer.base';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import attributesDesignerFactory from '../attributes/factory/attributesDesignerFactory';
import attributesViewOptionsFactory from '../attributes/factory/attributesViewOptionsFactory';
import scrollEventTrait from '../attributes/trait/attributesViewScrollEventTrait';
import attributesTypesEnum from '../attributes/enum/attributesTypesEnum';

Administer.urlContext = jrsConfigs.urlContext;
logging.initialize();
var attributesView = attributesDesignerFactory(attributesTypesEnum.SERVER, attributesViewOptionsFactory({
    type: attributesTypesEnum.SERVER,
    el: $('.attributes')
}));
attributesView.setContext().done(attributesView.render).then(function () {
    scrollEventTrait.initScrollEvent(attributesView);
});
