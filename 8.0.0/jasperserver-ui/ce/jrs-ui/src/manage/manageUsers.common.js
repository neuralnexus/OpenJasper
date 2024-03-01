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

import 'xregexp';
import 'backbone';
import _ from 'underscore';
import orgModule from '../org/org.root.user';
import jrsConfigs from 'js-sdk/src/jrs.configs';
import i18n from '../i18n/AttributesBundle.properties';
import i18n2 from 'js-sdk/src/i18n/CommonBundle.properties';
import attributesTypesEnum from '../attributes/enum/attributesTypesEnum';
import ConfirmationDialog from 'js-sdk/src/common/component/dialog/ConfirmationDialog';
import attributesViewOptionsFactory from '../attributes/factory/attributesViewOptionsFactory';
import scrollEventTrait from '../attributes/trait/attributesViewScrollEventTrait';
import AttributesViewFacade from '../attributes/AttributesViewFacade';
import TenantsTreeView from '../tenantImportExport/view/TenantsTreeView';
import '../manage/mng.common.actions';
import '../org/org.user.mng.components';
import 'js-sdk/src/common/util/encrypter';
import '../util/utils.common';

if (typeof orgModule.messages === 'undefined') {
    orgModule.messages = {};
}
if (typeof orgModule.Configuration === 'undefined') {
    orgModule.Configuration = {};
}
_.extend(window.localContext, jrsConfigs.userManagement.localContext);
_.extend(orgModule.messages, jrsConfigs.userManagement.orgModule.messages);
_.extend(orgModule.Configuration, jrsConfigs.userManagement.orgModule.Configuration);
orgModule.userManager.initialize({
    _: _,
    i18n: i18n,
    i18n2: i18n2,
    attributesViewOptionsFactory: attributesViewOptionsFactory,
    AttributesViewFacade: AttributesViewFacade,
    scrollEventTrait: scrollEventTrait,
    attributesTypesEnum: attributesTypesEnum,
    ConfirmationDialog: ConfirmationDialog,
    TenantsTreeView: TenantsTreeView
});
