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

import './commons.minimal.main';
import '../namespace/namespace';
import '../core/core.accessibility';
import '../core/core.events.bis';
import '../core/core.key.events';
import stdnav from 'js-sdk/src/common/stdnav/stdnav';
import actionModel from '../actionModel/actionModel.modelGenerator';
import primaryNavigation from '../actionModel/actionModel.primaryNavigation';
import globalSearch from '../repository/repository.search.globalSearchBoxInit';
import layoutModule from '../core/core.layout';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import $ from 'jquery';
import stdnavPluginActionMenu from '../stdnav/plugins/stdnavPluginActionMenu';
import stdnavPluginDynamicList from '../stdnav/plugins/stdnavPluginDynamicList';
import stdnavPluginToolbar from '../stdnav/plugins/stdnavPluginToolbar';

// add information about locale into body's class
$('body').addClass('locale-' + jrsConfigs.userLocale);
layoutModule.initialize();
primaryNavigation.initializeNavigation();    //navigation setup
//navigation setup
actionModel.initializeOneTimeMenuHandlers();    //menu setup
// JRS-specific stdnav plugins from jrs-ui
//menu setup
// JRS-specific stdnav plugins from jrs-ui
stdnavPluginActionMenu.activate(stdnav);
stdnavPluginDynamicList.activate(stdnav);
stdnavPluginToolbar.activate(stdnav);
jrsConfigs.initAdditionalUIComponents && globalSearch.initialize();    //isNotNullORUndefined(window.accessibilityModule) && accessibilityModule.initialize();
//trigger protorype's dom onload manualy
//isNotNullORUndefined(window.accessibilityModule) && accessibilityModule.initialize();
//trigger protorype's dom onload manualy
document.fire('dom:loaded');
