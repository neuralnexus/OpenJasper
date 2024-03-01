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
import i18n from '../../i18n/CommonBundle.properties';
import i18n2 from '../../i18n/EditSettingsBundle.properties';
import logging from '../../administer/administer.logging';
import Administer from '../../administer/administer.base';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import buttonsTrait from '../../serverSettingsCommon/view/traits/buttonsTrait';
import ResetSettingsCollectionView from './view/ResetSettingsCollectionView';
import ResetSettingsItemView from './view/ResetSettingsItemView';
import ResetSettingsEmptyView from './view/ResetSettingsEmptyView';
import ResetSettingsCollection from './collection/ResetSettingsCollection';
import ResetSettingsModel from './model/ResetSettingsModel';
import tooltipTemplate from './templates/tooltipTemplate.htm';

var ResetSettingsCollectionViewExtended = ResetSettingsCollectionView.extend(buttonsTrait);

Administer.urlContext = jrsConfigs.urlContext;
logging.initialize();
var collection = new ResetSettingsCollection([], {model: ResetSettingsModel}),
    resetSettingsView = new ResetSettingsCollectionViewExtended({
        el: $('.resetSettings'),
        tooltip: {
            template: tooltipTemplate,
            i18n: i18n2
        },
        collection: collection,
        childViewContainer: '.tbody',
        childView: ResetSettingsItemView,
        emptyView: ResetSettingsEmptyView,
        buttons: [
            {
                label: i18n['button.save'],
                action: 'save',
                primary: true
            },
            {
                label: i18n['button.cancel'],
                action: 'cancel',
                primary: false
            }
        ],
        buttonsContainer: '.buttonsContainer'
    });
resetSettingsView.fetchData().done(resetSettingsView.render);
