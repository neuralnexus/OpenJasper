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

import _ from 'underscore';
import orgModule from '../org/org.root.role';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import TenantsTreeView from '../tenantImportExport/view/TenantsTreeView';
import './mng.common.actions';
import '../org/org.role.mng.actions';

if (typeof orgModule.messages === 'undefined') {
    orgModule.messages = {};
}
if (typeof orgModule.Configuration === 'undefined') {
    orgModule.Configuration = {};
}
_.extend(window.localContext, jrsConfigs.roleManagement.localContext);
_.extend(orgModule.messages, jrsConfigs.roleManagement.orgModule.messages);
_.extend(orgModule.Configuration, jrsConfigs.roleManagement.orgModule.Configuration);
orgModule.roleManager.initialize({ TenantsTreeView: TenantsTreeView });
