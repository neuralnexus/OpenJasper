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

import './commons.bare.main';
import heartbeat from '../components/components.heartbeat';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import aboutDialog from '../components/components.about';
import webHelp from '../components/components.webHelp';
import $ from 'jquery';

// Basic stdnav plugins from js-sdk
import stdnav from 'js-sdk/src/common/stdnav/stdnav';
import stdnavPluginAnchor from 'js-sdk/src/common/stdnav/plugins/stdnavPluginAnchor';
import stdnavPluginButton from 'js-sdk/src/common/stdnav/plugins/stdnavPluginButton';
import stdnavPluginGrid from 'js-sdk/src/common/stdnav/plugins/stdnavPluginGrid';
import stdnavPluginList from 'js-sdk/src/common/stdnav/plugins/stdnavPluginList';
import stdnavPluginTable from 'js-sdk/src/common/stdnav/plugins/stdnavPluginTable';
import stdnavPluginActionMenu from '../stdnav/plugins/stdnavPluginActionMenu';
import stdnavPluginDynamicList from '../stdnav/plugins/stdnavPluginDynamicList';
import stdnavPluginForms from '../stdnav/plugins/stdnavPluginForms';
import stdnavPluginToolbar from '../stdnav/plugins/stdnavPluginToolbar';
import stdnavPluginWorkflowCard from 'js-sdk/src/common/stdnav/plugins/stdnavPluginWorkflowCard';
import '../config/dateAndTimeSettings';

//Heartbeat
heartbeat.initialize(jrsConfigs.heartbeatInitOptions);
heartbeat.start();
jrsConfigs.initAdditionalUIComponents && aboutDialog.initialize();    //Web help
//Web help
var helpLink = $('#helpLink');
if (helpLink) {
    helpLink.on('click', function (e) {
        e.preventDefault();
        webHelp.displayWebHelp();
    });
}
if (jrsConfigs.enableAccessibility === 'true') {
    // Basic stdnav plugins from js-sdk
    stdnav.activate();
    stdnavPluginAnchor.activate(stdnav);
    stdnavPluginButton.activate(stdnav);
    stdnavPluginForms.activate(stdnav);
    stdnavPluginGrid.activate(stdnav);
    stdnavPluginList.activate(stdnav);
    stdnavPluginTable.activate(stdnav);    // JasperServer-specific stdnav plugins from jrs-ui
    // JasperServer-specific stdnav plugins from jrs-ui
    stdnavPluginActionMenu.activate(stdnav);
    stdnavPluginDynamicList.activate(stdnav);
    stdnavPluginToolbar.activate(stdnav);
    stdnavPluginWorkflowCard.activate(stdnav);
    stdnav.start();
}
